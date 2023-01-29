package io.github.koba.ping

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

var ping = false
var pingspeed = 0.0

class PingPlugin: JavaPlugin() {

    override fun onEnable() {

        saveConfig()
        val cfile = File(dataFolder, "config.yml")
        if (cfile.length() == 0L) {
            config.options().copyDefaults(true)
            saveConfig()
        }

        pingspeed = config.getDouble("ping")

        server.scheduler.runTaskTimer(this, PingTask(), 0L, 1L)

        kommand {
            register("ping") {

                then("toggle") {
                    requires { isOp }

                    executes {
                        ping = if (ping) {
                            Bukkit.broadcastMessage("§e독일핑이 비활성화 되었습니다.")
                            false
                        } else {
                            Bukkit.broadcastMessage("§e독일핑이 활성화 되었습니다.")
                            true
                        }
                    }
                }

                then("set") {
                    requires { isOp }

                    then("pingdouble" to double()) {
                        executes {
                            val pingdouble: Double by it

                            if (pingdouble < 0) {
                                sender.sendMessage("§c0보다 작은 수는 입력할 수 없습니다.")
                            } else {

                                pingspeed = pingdouble
                                config["ping"] = pingspeed
                                saveConfig()
                                Bukkit.broadcastMessage(String.format("§a지연 시간을 " + pingspeed + "ms로 설정했습니다."))
                            }
                        }
                    }
                }

                then("check") {
                    executes {
                        pingspeed = config.getDouble("ping")

                        sender.sendMessage("§l----------독일핑----------")

                        if (ping) sender.sendMessage("§l독일핑 : §a켜짐")
                        if (!ping) sender.sendMessage("§l독일핑 : §c꺼짐")

                        if (pingspeed < 200) sender.sendMessage("§l지연시간 : §a$pingspeed§ams")
                        if (pingspeed >= 200 && pingspeed < 300) sender.sendMessage("§l지연시간 : §e$pingspeed§ems")
                        if (pingspeed >= 300) sender.sendMessage("§l지연시간 : §c$pingspeed§cms")
                    }
                }
            }
        }
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