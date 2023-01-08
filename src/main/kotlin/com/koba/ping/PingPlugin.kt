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

            if (args.isNotEmpty()) {
                    if (args[0] == "toggle") {
                        if (sender.hasPermission("ping.command")) {
                            ping = if (ping) {
                                Bukkit.broadcastMessage("§e독일핑이 비활성화 되었습니다.")
                                false
                            } else {
                                Bukkit.broadcastMessage("§e독일핑이 활성화 되었습니다.")
                                server.scheduler.runTaskTimer(this, PingTask(), 0L, 1L)
                                pingspeed = config.getInt("ping").toDouble()
                                true
                            }
                        } else {
                            sender.sendMessage("§c명령어를 쓸 수 있는 권한이 없습니다.")
                        }
                    }

                    if (args[0] == "set") {
                        if (sender.hasPermission("ping.command")) {
                            if (args.size == 2) {
                                if (args[1].matches("[+-]?\\d*(\\.\\d+)?".toRegex())) {
                                    if (args[1].toInt() < 0) {
                                        sender.sendMessage("§c0보다 작은 수는 입력할 수 없습니다.")
                                        return false
                                    }
                                    pingspeed = java.lang.Double.valueOf(args[1])
                                    config["ping"] = pingspeed
                                    saveConfig()
                                    Bukkit.broadcastMessage(String.format("§a지연 시간을 " + pingspeed + "ms로 설정했습니다."))
                                } else {
                                    sender.sendMessage("/ping toggle")
                                    sender.sendMessage("/ping set [Int]")
                                    sender.sendMessage("/ping check")
                                }
                            } else {
                                sender.sendMessage("/ping toggle")
                                sender.sendMessage("/ping set [Int]")
                                sender.sendMessage("/ping check")
                            }

                        } else {
                            sender.sendMessage("§c명령어를 쓸 수 있는 권한이 없습니다.")
                        }
                    }

                if (args[0] == "check") {
                    pingspeed = config.getInt("ping").toDouble()
                    sender.sendMessage("§l----------독일핑----------")
                    if (ping) sender.sendMessage("§l독일핑 : §a켜짐")
                    if (!ping) sender.sendMessage("§l독일핑 : §c꺼짐")
                    if (pingspeed.toInt() <= 100) sender.sendMessage("§l지연시간 : §a$pingspeed§ams")
                    if (pingspeed.toInt() == 200) sender.sendMessage("§l지연시간 : §e$pingspeed§ems")
                    if (pingspeed.toInt() >= 300) sender.sendMessage("§l지연시간 : §c$pingspeed§cms")
                }
                return false

                sender.sendMessage("/ping toggle")
                sender.sendMessage("/ping set [Int]")
                sender.sendMessage("/ping check")
            }

            sender.sendMessage("/ping toggle")
            sender.sendMessage("/ping set [Int]")
            sender.sendMessage("/ping check")

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
