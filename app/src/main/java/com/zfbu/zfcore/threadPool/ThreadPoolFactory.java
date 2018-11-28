package com.zfbu.zfcore.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {
    private static ExecutorService executorService = null;


    private static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(5, 30,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static ExecutorService getExecutorService() {
        if(null ==executorService ){
            executorService = newCachedThreadPool();
        }
        return executorService;
    }
}
