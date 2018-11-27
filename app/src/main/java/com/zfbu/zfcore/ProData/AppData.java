package com.zfbu.zfcore.ProData;

public class AppData {
    private String appId;
    private String appName;
    private String appAliId;

    public AppData(String appId, String appName, String appAliId) {
        this.appId = appId;
        this.appName = appName;
        this.appAliId = appAliId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppAliId() {
        return appAliId;
    }

    public void setAppAliId(String appAliId) {
        this.appAliId = appAliId;
    }
}
