package com.zfbu.zfcore.ProData;

public class QrData {
    private String qrId;
    private String appId;
    private String money;

    public QrData(String qrId, String appId, String money) {
        this.qrId = qrId;
        this.appId=appId;
        this.money=money;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
