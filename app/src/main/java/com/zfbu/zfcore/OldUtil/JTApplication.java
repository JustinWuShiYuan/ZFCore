package com.zfbu.zfcore.OldUtil;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.zfbu.zfcore.threadPool.ThreadPoolFactory;


//import com.xdandroid.hellodaemon.DaemonEnv;

public class JTApplication extends Application {
    private static JTApplication application;

    public static JTApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
//        DaemonEnv.initialize(this, LiveService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
//        LiveService.sShouldStopService = false;
//        DaemonEnv.startServiceMayBind(LiveService.class);
        ThreadPoolFactory.getExecutorService();//初始化线程池

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                ActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }
}
