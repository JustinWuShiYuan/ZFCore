package com.zfbu.zfcore.lemonDialog.lemonhello.adapter;


import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloAction;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloInfo;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloView;
import com.zfbu.zfcore.lemonDialog.lemonhello.interfaces.LemonHelloEventDelegate;

/**
 * LemonHello 事件代理适配器
 * Created by LiuRi on 2017/1/11.
 */

public abstract class LemonHelloEventDelegateAdapter implements LemonHelloEventDelegate {

    @Override
    public void onActionDispatch(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {

    }

    @Override
    public void onMaskTouch(LemonHelloView helloView, LemonHelloInfo helloInfo) {

    }
    
}
