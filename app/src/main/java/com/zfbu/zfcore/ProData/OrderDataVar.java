package com.zfbu.zfcore.ProData;


public class OrderDataVar {
    private String money;//支付金额
    private String payuser;//付款人
    private String info; //备注
    private String time;//时间 字符串
    private String gmtCreate;//订单时间戳
    private String orderId; //订单号
    private String appid;//如题
    private int state;//状态 提交    1:提交成功 status:1   2.提交成功 status:0  3.提交失败
    private String sendway;//状态提示文本


    public void setMoney(String money) {
        this.money = money;
    }

    public void setPayuser(String payuser) {
        this.payuser = payuser;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setState(int state) {
        this.state = state;
    }

    public OrderDataVar() {

    }

    public OrderDataVar(String appid, String gmtCreate, String money, String sendway, int state,String info,String orderId) {
        this.appid = appid;
        this.gmtCreate = gmtCreate;
        this.money = money;
        this.sendway = sendway;
        this.state = state;
        this.info = info;
        this.orderId = orderId;
    }

    public String getMoney() {
        return money;
    }

    public String getPayuser() {
        return payuser;
    }

    public String getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public String getAppid() {
        return appid;
    }

    public String getInfo() {
        return info;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getSendway() {
        return sendway;
    }

    public void setSendway(String sendway) {
        this.sendway = sendway;
    }
}
