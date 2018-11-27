package com.zfbu.zfcore.ProData;


public class OrderGroupVar {
    private int week; //星期
    private String time;//时间
    private String money;//金额
    private String orderNum;//订单数
    private int state;//是否有状态图标

    public OrderGroupVar(int week, String time, String money, String orderNum, int state) {
        this.week = week;
        this.time = time;
        this.money = money;
        this.orderNum = orderNum;
        this.state = state;
    }

    public int getWeek() {
        return week;
    }

    public String getTime() {
        return time;
    }

    public String getMoney() {
        return money;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public int getState() {
        return state;
    }

}
