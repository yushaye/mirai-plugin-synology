package com.see.synology.task;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.see.synology.command.SynologyConfig;

import java.util.HashMap;
import java.util.Map;

public class Auth {
    private SynologyConfig serverConfig;
    private String sid;

    public String getSid() {
        return sid;
    }

    public Auth(SynologyConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public boolean login() {
        String url = serverConfig.getBaseUrl() + "auth.cgi?api=SYNO.API.Auth";
        Map<String, Object> params = new HashMap<>();
        params.put("version", serverConfig.getVersion());
        params.put("method", "login");
        params.put("account", serverConfig.getUserName());
        params.put("passwd", serverConfig.getPassword());
        params.put("session", "DownloadStation");
        params.put("format", "cookie");
        String paramStr = HttpUtil.toParams(params);
        String fullUrl = url + "&" + paramStr;
        String body = HttpUtil.get(fullUrl);
        JSON json = JSONUtil.parse(body);
        Map<String, Object> data = json.getByPath("data", Map.class);
        if (json.getByPath("success", boolean.class)) {
            this.sid = (String) data.get("sid");
            return true;
        }
        return false;
    }
}
