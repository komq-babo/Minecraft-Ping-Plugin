package io.github.koba.ping

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

var ping = false
var pingspeed = 0.0

class PingPlugin : JavaPlugin() {

    companion object {
        lateinit var instance : PingPlugin
    }

    init {
        instance = this
    }

    override fun onEnable() {
        saveConfig()
        val cfile = File(dataFolder, "config.yml")
        if (cfile.length() == 0L) {
            config.options().copyDefaults(true)
            saveConfig()
        }

        pingspeed = config.getDouble("ping")

        server.scheduler.runTaskTimer(this, PingTask(), 0L, 1L)

        setupCommands()
    }

    private fun setupCommands() = kommand {
        KommandPing.register(this@PingPlugin, this)
    }
}

class PingTask : Runnable {
    override fun run() {
        if (ping) {
            try {
                Thread.sleep(pingspeed.toLong())
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }
}
