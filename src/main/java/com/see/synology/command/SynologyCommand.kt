package com.see.synology.command

import com.see.synology.Plugin
import com.see.synology.task.DownloadTask
import com.see.synology.task.TaskListener
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

class SynologyCommand {
    object ServerConfigCommand : CompositeCommand(Plugin.INSTANCE, "syno", description = "配置群晖") {
        @SubCommand
        suspend fun CommandSender.server(server: String) {
            if (server.startsWith("http://") || server.startsWith("https://")) {
                PluginConfig.synologyConfig.server = server
                sendMessage("成功")
            }
        }

        @SubCommand
        suspend fun CommandSender.port(port: Int) {
            PluginConfig.synologyConfig.port = port
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.account(account: String) {
            PluginConfig.synologyConfig.userName = account
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.password(password: String) {
            PluginConfig.synologyConfig.password = password
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.path(path: String) {
            PluginConfig.synologyConfig.downloadPath = path
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.version(version: Int) {
            PluginConfig.synologyConfig.version = version
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.clear() {
            PluginConfig.synologyConfig.server = null
            PluginConfig.synologyConfig.port = null
            PluginConfig.synologyConfig.userName = null
            PluginConfig.synologyConfig.password = null
            PluginConfig.synologyConfig.downloadPath = null
            PluginConfig.synologyConfig.version = null
            sendMessage("成功")
        }

        @SubCommand
        suspend fun CommandSender.verify() {
            if (PluginConfig.synologyConfig.checkConfig() ) {
                Plugin.INSTANCE.getDownloadStation().setServerConfig(PluginConfig.synologyConfig)
                if (Plugin.INSTANCE.getDownloadStation().init()) {
                    sendMessage("验证成功，群晖配置可用")
                    return
                }
            }
            sendMessage("验证失败，请检查群晖配置")
        }
    }


    // TODO
    object DownloadStationCommand : CompositeCommand(Plugin.INSTANCE, "task", description = "添加下载") {
        @SubCommand
        suspend fun CommandSender.add(url:String) {
            if (!Plugin.INSTANCE.getDownloadStation().isInitFlag()) {
                sendMessage("DS未初始化，尝试初始化")
                if (Plugin.INSTANCE.getDownloadStation().init()) {
                    sendMessage("DS初始化成功")
                } else {
                    sendMessage("DS初始化失败，请检查群晖配置")
                    return
                }
            }
            val downloadTask = DownloadTask(url, null)
            if (Plugin.INSTANCE.getDownloadStation().createTask(downloadTask)) {
                sendMessage("创建下载任务成功")
            } else {
                sendMessage("创建下载任务失败")
            }

        }

        @SubCommand
        suspend fun CommandSender.addBatch() {
            if (!Plugin.INSTANCE.getDownloadStation().isInitFlag()) {
                sendMessage("DS未初始化，尝试初始化")
                if (Plugin.INSTANCE.getDownloadStation().init()) {
                    sendMessage("DS初始化成功")
                } else {
                    sendMessage("DS初始化失败，请检查群晖配置")
                    return
                }
            }
            TaskListener.waitBatchTask(this.user!!.id)
            sendMessage("请在下一条消息发送要添加的任务链接")
        }
    }
}