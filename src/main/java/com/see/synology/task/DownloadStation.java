package com.see.synology.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.see.synology.command.SynologyConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DownloadStation {
    private final String APP_NAME = "DownloadStation";
    private Auth auth;
    private Map<String, Map<String, Object>> apiList;
    private SynologyConfig serverConfig;
    private boolean initFlag = false;

    public boolean init() throws Exception {
        if (serverConfig == null) {
            throw new Exception("未初始化群晖配置");
        }
        auth = new Auth(serverConfig);
        if (!auth.login()) {
            throw new Exception("登录失败");
        }
        getApiList();
        initFlag = true;
        return true;
    }

    public void getApiList() {
        apiList = new HashMap<>();
        String url = StrUtil.format("{}query.cgi?api=SYNO.API.Info", serverConfig.getBaseUrl());
        Map<String, Object> params = new HashMap<>();
        params.put("version", '1');
        params.put("method", "query");
        params.put("query", "all");
        String paramStr = HttpUtil.toParams(params);
        String fullUrl = url + "&" + paramStr;
        String body = HttpUtil.get(fullUrl);
        JSON json = JSONUtil.parse(body);
        Map<String, Map<String, Object>> data = json.getByPath("data", Map.class);
        data.forEach((K, V)->{
            if (K.toLowerCase(Locale.ROOT).contains(APP_NAME.toLowerCase(Locale.ROOT))) {
                apiList.put(K, V);
            }
        });
    }

    public boolean createTask(DownloadTask task) {
        String apiName = "SYNO.DownloadStation.Task";
        Map<String, Object> apiInfo = apiList.get(apiName);
        String apiPath = (String) apiInfo.get("path");
        String url = StrUtil.format("{}{}?api={}", serverConfig.getBaseUrl(), apiPath , apiName);
        Map<String, Object> params = new HashMap<>();
        params.put("version", apiInfo.get("maxVersion"));
        params.put("method", "create");
        params.put("uri", task.getUrl());
        String path = task.getDownloadPath();
        if (StrUtil.isBlank(path)) {
            path = serverConfig.getDownloadPath();
        }
        if (StrUtil.isNotBlank(path)) {
            params.put("destination", path);
        }
        params.put("_sid", auth.getSid());
        String paramStr = HttpUtil.toParams(params);
        String fullUrl = url + "&" + paramStr;
        String body = HttpUtil.get(fullUrl);
        JSON json = JSONUtil.parse(body);
        return json.getByPath("success", boolean.class);
    }

    public SynologyConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(SynologyConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public boolean isInitFlag() {
        return initFlag;
    }
}
