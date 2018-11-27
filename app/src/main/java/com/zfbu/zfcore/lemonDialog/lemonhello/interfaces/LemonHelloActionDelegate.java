package com.zfbu.zfcore.lemonDialog.lemonhello.interfaces;


import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloAction;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloInfo;
import com.zfbu.zfcore.lemonDialog.lemonhello.LemonHelloView;

/**
 * LemonHello - 事件回调代理
 * Created by LiuRi on 2017/1/11.
 */

public interface LemonHelloActionDelegate {

    void onClick(
            LemonHelloView helloView,
            LemonHelloInfo helloInfo,
            LemonHelloAction helloAction
    );

}
