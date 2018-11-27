package com.zfbu.zfcore.UI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zfbu.zfcore.Config.Config;
import com.zfbu.zfcore.ProData.OrderDataVar;
import com.zfbu.zfcore.ProData.OrderGroupVar;
import com.zfbu.zfcore.ProData.OrderGroupView;
import com.zfbu.zfcore.R;
import com.zfbu.zfcore.UI.OrderAdapter.OrderItemAdapter;
import com.zfbu.zfcore.UI.OrderBoxListView.interfaces.OnDragStateChangeListener;
import com.zfbu.zfcore.UI.OrderBoxListView.widget.InboxBackgroundScrollView;
import com.zfbu.zfcore.UI.OrderBoxListView.widget.InboxLayoutBase;
import com.zfbu.zfcore.UI.OrderBoxListView.widget.InboxLayoutListView;
import com.zfbu.zfcore.Util.HBSQL.DBManager;
import com.zfbu.zfcore.Util.ZFLog;
import com.zfbu.zfcore.lemonDialog.lemonbubble.LemonBubble;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLayoutStyle;
import com.zfbu.zfcore.lemonDialog.lemonbubble.enums.LemonBubbleLocationStyle;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHello;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloAction;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloInfo;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloView;
import com.zfbu.zfcore.lemonDialog.lemonhello.adapter.LemonHelloEventDelegateAdapter;
import com.zfbu.zfcore.lemonDialog.lemonhello.interfaces.LemonHelloActionDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderListActivity extends Activity implements View.OnClickListener {
    public InboxLayoutListView inboxLayoutListView;
    public HBHandler hbHandler = new HBHandler();  //线程
    DBManager dbManager;
    List<OrderGroupVar> groupArray;
    List<List<OrderDataVar>> childArray;
    OrderItemAdapter orderAdspter;
    InboxBackgroundScrollView inboxBackgroundScrollView;
    TextView order_tvTitle;//顶部标题
    ImageView order_loLeft;
    int groupId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderlist);

        dbManager = DBManager.getInstance(this); //数据库
        order_tvTitle = findViewById(R.id.order_tvTitle);
        order_loLeft = findViewById(R.id.order_loLeft);
        //子列表的滚动框架
        inboxBackgroundScrollView = findViewById(R.id.scroll);
        inboxLayoutListView = findViewById(R.id.inboxlayout_list); //子列表
        inboxLayoutListView.setBackgroundScrollView(inboxBackgroundScrollView);//绑定scrollview
        inboxLayoutListView.setCloseDistance(50);
        inboxLayoutListView.setOnDragStateChangeListener(new OnDragStateChangeListener() {
            @Override
            public void dragStateChange(InboxLayoutBase.DragState state) {
                switch (state) {
                    case CANCLOSE:
                        Message zuuu_msg = new Message();
                        zuuu_msg.what = 1;
                        zuuu_msg.obj = "返回订单列表";
                        hbHandler.sendMessage(zuuu_msg);
                        break;
                    case CANNOTCLOSE:
                        if (Config.order_page_list_hasopen) { //如果已经打开
                            zuuu_msg = new Message();
                            zuuu_msg.what = 1;
                            zuuu_msg.obj = groupArray.get(groupId).getTime();
                            hbHandler.sendMessage(zuuu_msg);
                        } else {
                            zuuu_msg = new Message();
                            zuuu_msg.what = 1;
                            zuuu_msg.obj = "订单列表";
                            hbHandler.sendMessage(zuuu_msg);
                        }
                        break;
                }
            }
        });

        LemonBubble.getRoundProgressBubbleInfo()
                .setLocationStyle(LemonBubbleLocationStyle.BOTTOM)
                .setLayoutStyle(LemonBubbleLayoutStyle.ICON_LEFT_TITLE_RIGHT)
                .setBubbleSize(200, 50)
                .setProportionOfDeviation(0.1f)
                .setTitle("正在请求服务器...")
                .show(this);

        groupArray = new ArrayList<>();//用户组列表 初始化
        childArray = new ArrayList<>();//每组用户列表
        List<Map<String, Object>> tempSqlValue;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        for (int i = 0; i < 7; i++) //noinspection MagicConstant
        {
            if (i != 0) {
                c.add(Calendar.DATE, -1);
            }
            Date d = c.getTime();
            String day = format.format(d);

            int weekId = c.get(Calendar.DAY_OF_WEEK) - 1;
            weekId = weekId < 0 ? 0 : weekId;
            //ZFLog.i(day + "   " + weekId);

            tempSqlValue = dbManager.query_select("SELECT *  FROM `hb_order_itme` WHERE `username` = ? AND `timestr` = ?"
                    , new String[]{Config.userName, day});//0为通用
            if (tempSqlValue != null) {

                OrderGroupVar tempOrderGroupVar = new OrderGroupVar(weekId, day,
                        Objects.requireNonNull(tempSqlValue.get(0).get("order_money")).toString(),
                        Objects.requireNonNull(tempSqlValue.get(0).get("order_number")).toString(),
                        Integer.valueOf(Objects.requireNonNull(tempSqlValue.get(0).get("sendState")).toString()));
                setGroundAdapter(this, tempOrderGroupVar, groupArray.size());
                groupArray.add(tempOrderGroupVar);


                //riqi daima 优化
                tempSqlValue = dbManager.query_select("SELECT *  FROM `hb_order` WHERE `username` = ? AND `timestr` = ?"
                        , new String[]{Config.userName, day});//0为通用
                if (tempSqlValue != null) {
                    //这里模拟获取数据
                    childArray.add(new ArrayList<OrderDataVar>());
                    for (int ii = 0; ii < tempSqlValue.size(); ii++) {
                        childArray.get(childArray.size() - 1).add(new OrderDataVar(Objects.requireNonNull(tempSqlValue.get(ii).get("appid")).toString(),
                                Objects.requireNonNull(tempSqlValue.get(ii).get("paytime")).toString(),
                                Objects.requireNonNull(tempSqlValue.get(ii).get("actual")).toString(),
                                Objects.requireNonNull(tempSqlValue.get(ii).get("sendway")).toString(),
                                Integer.valueOf(Objects.requireNonNull(tempSqlValue.get(ii).get("sendState")).toString()),
                                (tempSqlValue.get(ii).get("paynote") == null ? "" : Objects.requireNonNull(tempSqlValue.get(ii).get("paynote")).toString()),
                                (tempSqlValue.get(ii).get("orderid") == null ? "" : Objects.requireNonNull(tempSqlValue.get(ii).get("orderid")).toString())));
                    }
                }
            }
            //测试
           /* OrderGroupVar tempOrderGroupVar = new OrderGroupVar(weekId, day, String.valueOf(100 + i), String.valueOf(1000 + i), 1);
            setGroundAdapter(this, tempOrderGroupVar, i);
            groupArray.add(tempOrderGroupVar);
            childArray.add(new ArrayList<OrderDataVar>());
            for (int ii = 0; ii < i; ii++) {
                childArray.get(i).add(new OrderDataVar("33", "1540393643123", "3.2", "启动失败", 2));
            }*/
        }
        LemonBubble.forceHide(); //停止提示框显示
        if (groupArray.size() == 0) {
            LemonHello.getErrorHello("暂无数据", String.valueOf("当前暂无七天内订单数据"))
                    .addAction(new LemonHelloAction("关闭", new LemonHelloActionDelegate() {
                        @Override
                        public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                            helloView.hide();
                            OrderListActivity.this.finish();
                        }
                    }))
                    .setEventDelegate(new LemonHelloEventDelegateAdapter() {
                        @Override
                        public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {
                            super.onMaskTouch(helloView, helloInfo);
                            helloView.hide();
                            OrderListActivity.this.finish();
                        }
                    })
                    .show(this);
        }

        //子选项的选中
        inboxLayoutListView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("zuuuuuuuuuu", "点击了订单选项: " + groupId + "    " + position);

            }
        });

        order_loLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.order_page_list_hasopen) {
                    inboxLayoutListView.finishWithAnim(); //打开子目录
                } else {
                    OrderListActivity.this.finish();
                }
            }
        });

    }

    void setItemAdapter(int openGroupId) {
        orderAdspter = new OrderItemAdapter(OrderListActivity.this, childArray.get(openGroupId));
        inboxLayoutListView.setAdapter(orderAdspter);
    }

    @Override
    public void onClick(View v) {
        ZFLog.i("点击了按钮:" + v.getId());
        groupId = v.getId();

        ContentValues tempValues = new ContentValues();
        tempValues.put("sendState", "3");//图标隐藏
        String tempIfStr = "username=? AND timestr=?";
        String[] tempBingArgs = new String[]{Config.userName, groupArray.get(groupId).getTime()};
        dbManager.update("hb_order_itme", tempValues, tempIfStr, tempBingArgs); //更新数据

        setItemAdapter(groupId); //设置界面
        Config.order_page_list_hasopen = true; //被打开

        //设置标题
        Message zuuu_msg = new Message();
        zuuu_msg.what = 1;
        zuuu_msg.obj = groupArray.get(groupId).getTime();
        hbHandler.sendMessage(zuuu_msg);

        inboxLayoutListView.openWithAnim(v); //打开子目录
    }

    void setGroundAdapter(Context context, OrderGroupVar tempVar, int btnId) {
        LinearLayout input_linearLayout = findViewById(R.id.input_linearLayout);
        View show_linearlayout = LayoutInflater.from(context).inflate(R.layout.order_item_group, input_linearLayout, false);
        OrderGroupView tempOrderGroupView = new OrderGroupView();
        tempOrderGroupView.week_time = show_linearlayout.findViewById(R.id.week_time);
        tempOrderGroupView.week_time.setText(tempVar.getTime()); //日期时间
        tempOrderGroupView.week_money = show_linearlayout.findViewById(R.id.week_money);
        tempOrderGroupView.week_money.setText(tempVar.getMoney()); //钱
        tempOrderGroupView.week_orderNum = show_linearlayout.findViewById(R.id.week_orderNum);
        tempOrderGroupView.week_orderNum.setText(tempVar.getOrderNum()); //抓单次数
        tempOrderGroupView.week_nun_img = show_linearlayout.findViewById(R.id.week_img);
        tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_1);
        switch (tempVar.getWeek()) {
            case 0:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_0);
                break;
            case 1:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_1);
                break;
            case 2:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_2);
                break;
            case 3:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_3);
                break;
            case 4:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_4);
                break;
            case 5:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_5);
                break;
            case 6:
                tempOrderGroupView.week_nun_img.setImageResource(R.drawable.order_group_week_6);
                break;
        }
        tempOrderGroupView.week_img_state = show_linearlayout.findViewById(R.id.week_state);
        if (tempVar.getState() == 1) { //有错误
            tempOrderGroupView.week_img_state.setImageResource(R.drawable.order_group_subway_error_flag);
        } else if (tempVar.getState() == 2) { //有新增
            tempOrderGroupView.week_img_state.setImageResource(R.drawable.order_groupsubway_new_flag);
        } else {//隐藏
            tempOrderGroupView.week_img_state.setVisibility(View.GONE);
        }
        show_linearlayout.setId(btnId);
        show_linearlayout.setOnClickListener(this); //设置按钮监听
        input_linearLayout.addView(show_linearlayout);
    }

    @SuppressLint("HandlerLeak")
    private class HBHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: //显示程序顶部的标题
                    order_tvTitle.setText((CharSequence) msg.obj);
                    break;
            }
        }
    }
}
