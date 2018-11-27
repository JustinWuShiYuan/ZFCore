package com.zfbu.zfcore.UI.OrderAdapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zfbu.zfcore.ProData.OrderDataVar;
import com.zfbu.zfcore.ProData.OrderDataView;
import com.zfbu.zfcore.R;
import com.zfbu.zfcore.Util.Core;

import java.util.List;

public class OrderItemAdapter extends BaseAdapter {

    private List<OrderDataVar> data;
    private LayoutInflater layoutInflater;


    public OrderItemAdapter(Context context, List<OrderDataVar> data) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDataView temp0rderDataView = null;
        if (convertView == null) {
            temp0rderDataView = new OrderDataView();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.order_item_child, null);
            temp0rderDataView.tv_name = (TextView) convertView.findViewById(R.id.tv_title);//钱
            temp0rderDataView.tv_appid = (TextView) convertView.findViewById(R.id.tv_appid);//appid
            temp0rderDataView.tv_sig = (TextView) convertView.findViewById(R.id.tv_sig);//提示
            /*temp0rderDataView.tv_sig = (TextView) convertView.findViewById(R.id.tv_sig);
            temp0rderDataView.tv_sig.setVisibility(View.GONE);//暂时隐藏*/
            temp0rderDataView.tv_state = (ImageView) convertView.findViewById(R.id.tv_state);//状态
            temp0rderDataView.tv_time = (TextView) convertView.findViewById(R.id.tv_time);//时间
            convertView.setTag(temp0rderDataView);
        } else {
            temp0rderDataView = (OrderDataView) convertView.getTag();
        }
        OrderDataVar tempOrderDataVar = this.data.get(position); //临时数据

        temp0rderDataView.tv_name.setText(tempOrderDataVar.getMoney());//钱
        temp0rderDataView.tv_appid.setText(tempOrderDataVar.getInfo());//appid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        temp0rderDataView.tv_sig.setText("订单号:" + tempOrderDataVar.getOrderId());//提示 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (tempOrderDataVar.getState() == 1) {//1:提交成功 status:1
            temp0rderDataView.tv_state.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.order_list_success));
        } else if (tempOrderDataVar.getState() == 2) {//2.提交成功 status:0
            temp0rderDataView.tv_state.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.order_list_wait));
        } else {//3.提交失败
            temp0rderDataView.tv_state.setImageDrawable(convertView.getResources().getDrawable(R.mipmap.order_list_error));
        }
        temp0rderDataView.tv_time.setText(Core.HH_MM_SS(Long.parseLong(tempOrderDataVar.getGmtCreate())));
        return convertView;
    }


}