package com.see.synology.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.see.synology.Plugin;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class TaskListener extends SimpleListenerHost {
    private static boolean batchTaskFlag = false;
    private static Long user = null;
    //private final List<String> URL_PREFIX = Arrays.asList("http://", "https://", "magnet:?xt=urn:btih", "ed2k://")

    @EventHandler
    public ListeningStatus onMessage(MessageEvent event) {
        if (batchTaskFlag && event.getSender().getId() == user) {
            String[] lines = event.getMessage().contentToString().split("\n");
            for (String line : lines) {
                DownloadTask task = new DownloadTask(line, null);
                Plugin.INSTANCE.getDownloadStation().createTask(task);
            }
            event.getSender().sendMessage("已批量创建任务");
            batchTaskFlag = false;
            user = null;
        }
        return ListeningStatus.LISTENING;
    }

    public static void waitBatchTask(Long user) {
        batchTaskFlag = true;
        TaskListener.user = user;
    }
}
