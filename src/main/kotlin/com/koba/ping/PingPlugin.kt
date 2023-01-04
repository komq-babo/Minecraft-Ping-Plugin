package com.koba.ping

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

private var ping = false
private var pingspeed = 0.0

class PingPlugin: JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        saveConfig()
        val cfile = File(dataFolder, "config.yml")
        if (cfile.length() == 0L) {
            config.options().copyDefaults(true)
            saveConfig()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label.equals("ping", true)) {
            ping = if (ping) {
                Bukkit.broadcastMessage("§e독일핑이 비활성화 되었습니다.")
                false
            } else {
                Bukkit.broadcastMessage("§e독일핑이 활성화 되었습니다.")
                server.scheduler.runTaskTimer(this, PingTask(), 0L , 1L)
                pingspeed = config.getInt("ping").toDouble()
                true
            }
        }
        if (label.equals("setping", true)) {
            if (args.isNotEmpty()) {
                if (args[0].matches("[+-]?\\d*(\\.\\d+)?".toRegex())) {
                    pingspeed = java.lang.Double.valueOf(args[0])
                    config["ping"] = pingspeed
                    saveConfig()
                    Bukkit.broadcastMessage(String.format("§a지연 시간을 " + pingspeed + "ms로 설정했습니다."))
                } else {
                    sender.sendMessage("§e/setping <ping>")
                }
            } else {
                sender.sendMessage("§e/setping <ping>")
            }
            return false
        }
        return false
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


