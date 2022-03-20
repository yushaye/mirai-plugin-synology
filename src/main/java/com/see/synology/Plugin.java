package com.see.synology;

import com.see.synology.command.PluginConfig;
import com.see.synology.command.SynologyCommand;
import com.see.synology.task.DownloadStation;
import com.see.synology.task.TaskListener;
import net.mamoe.mirai.console.command.CommandExecuteResult;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;

public final class Plugin extends JavaPlugin {
    public static final Plugin INSTANCE = new Plugin();

    public DownloadStation getDownloadStation() {
        return downloadStation;
    }

    public final DownloadStation downloadStation;

    private Plugin() {
        super(new JvmPluginDescriptionBuilder("com.see.synology.plugin", "1.0-SNAPSHOT")
                .name("mirai-plugin-synology")
                .info("推送下载任务到群晖")
                .author("seeye")
                .build());
        downloadStation = new DownloadStation();
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        // 加载配置
        reloadPluginConfig(PluginConfig.INSTANCE);
        CommandManager.INSTANCE.registerCommand(SynologyCommand.ServerConfigCommand.INSTANCE, false);
        CommandManager.INSTANCE.registerCommand(SynologyCommand.DownloadStationCommand.INSTANCE, false);
        // 注册消息监听，批量任务通过消息创建
        GlobalEventChannel.INSTANCE.registerListenerHost(new TaskListener());
        // 消息命令
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class, messageEvent -> {
            CommandSender sender = CommandSender.from(messageEvent);
            CommandExecuteResult result = CommandManager.INSTANCE.executeCommand(sender, messageEvent.getMessage(),true);
        });
        // 尝试初始化DS
        if (PluginConfig.INSTANCE.getSynologyConfig().checkConfig()) {
            downloadStation.setServerConfig(PluginConfig.INSTANCE.getSynologyConfig());
            try {
                downloadStation.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}