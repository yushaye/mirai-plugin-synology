package com.see.synology.task;


public class DownloadTask {
    private String url;
    // TODO
    private String downloadPath;

    public DownloadTask(String url, String downloadPath) {
        this.url = url;
        this.downloadPath = downloadPath;
    }

    public String getUrl() {
        return url;
    }


    public String getDownloadPath() {
        return downloadPath;
    }
}
