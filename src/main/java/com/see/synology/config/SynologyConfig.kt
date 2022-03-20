package com.see.synology.command

import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("mirai-plugin-synology"){
    val synologyConfig: SynologyConfig by value()
}

@Serializable
class SynologyConfig {
    var server: String? = null
    var port: Int? = null
    var userName: String? = null
    var password: String? = null
    var downloadPath: String? = null
    var version: Int? = null
    
    fun getBaseUrl():String {
        return "${server}:${port}/webapi/"
    }

    fun checkConfig(): Boolean {
        return StrUtil.isNotBlank(server) && ObjectUtil.isNotNull(port) && StrUtil.isNotBlank(userName) &&
                StrUtil.isNotBlank(password) && ObjectUtil.isNotNull(version)
    }
}