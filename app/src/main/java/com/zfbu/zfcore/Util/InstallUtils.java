package com.zfbu.zfcore.Util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 下载更新APK的工具
 */
public class InstallUtils {

    //任务定时器
    private Timer mTimer;
    //定时任务
    private TimerTask mTask;
    //文件总大小
    private int fileLength = 1;
    //下载的文件大小
    private int fileCurrentLength;

    private Context context;
    private String httpUrl;
    private String savePath;
    private String saveName;
    private DownloadCallBack downloadCallBack;
    private static File saveFile;

    private boolean isComplete = false;


    public interface DownloadCallBack {
        void onStart();

        void onComplete(String path);

        void onLoading(long total, long current);

        void onFail(Exception e);
    }

    public InstallUtils(Context context, String httpUrl, String saveName, String savePath, DownloadCallBack downloadCallBack) {
        this.context = context;
        this.httpUrl = httpUrl;
        this.saveName = saveName;
        this.downloadCallBack = downloadCallBack;
        this.savePath = savePath;
    }


    public void downloadAPK() {
        if (TextUtils.isEmpty(httpUrl)) {
            return;
        }
        saveFile = new File(savePath);
        if (!saveFile.exists()) {
            boolean isMK = saveFile.mkdirs();
            if (!isMK) {
                //创建失败
                return;
            }
        }

        saveFile = new File(savePath + File.separator + saveName);

        if (downloadCallBack != null) {
            //下载开始
            downloadCallBack.onStart();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(httpUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10 * 1000);
                    connection.setReadTimeout(10 * 1000);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    outputStream = new FileOutputStream(saveFile);
                    fileLength = connection.getContentLength();

                    //计时器
                    initTimer();

                    byte[] buffer = new byte[1024];
                    int current = 0;
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                        current += len;
                        if (fileLength > 0) {
                            fileCurrentLength = current;
                        }
                    }
                    isComplete = true;
                    //下载完成
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadCallBack != null) {
                                downloadCallBack.onComplete(saveFile.getPath());
                                downloadCallBack = null;
                            }
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadCallBack != null) {
                                downloadCallBack.onFail(e);
                                downloadCallBack = null;
                            }
                        }
                    });
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                        if (outputStream != null)
                            outputStream.close();
                        if (connection != null)
                            connection.disconnect();
                    } catch (IOException e) {
                    }
                    //销毁Timer
                    destroyTimer();
                }
            }
        }).start();

    }

    private void initTimer() {
        mTimer = new Timer();
        mTask = new TimerTask() {//在run方法中执行定时的任务
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadCallBack != null) {
                            if (!isComplete) {
                                downloadCallBack.onLoading(fileLength, fileCurrentLength);
                            }
                        }
                    }
                });
            }
        };
        //任务定时器一定要启动
        mTimer.schedule(mTask, 0, 200);
    }


    private void destroyTimer() {
        if (mTimer != null && mTask != null) {
            mTask.cancel();
            mTimer.cancel();
            mTask = null;
            mTimer = null;
        }
    }


}
