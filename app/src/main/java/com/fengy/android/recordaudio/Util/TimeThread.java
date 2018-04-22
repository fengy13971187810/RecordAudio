package com.fengy.android.recordaudio.Util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by admin on 2018/4/22.
 */

public class TimeThread {
    private TimeThread.TimeListener mListener;
    private Object timeLock = new Object();
    private boolean isRunning = true;
    private boolean isWork;
    private int count;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String time = formatter.format(Integer.valueOf(TimeThread.this.count * 1000 - TimeZone.getDefault().getRawOffset()));
            if(TimeThread.this.mListener != null) {
                TimeThread.this.mListener.OnTime(time);
            }

        }
    };
    private Thread workThread = new Thread(new Runnable() {
        public void run() {
            while(TimeThread.this.isRunning) {
                if(TimeThread.this.isWork) {
                    TimeThread.this.count++;
                    TimeThread.this.mHandler.sendEmptyMessage(0);
                } else {
                    synchronized(TimeThread.this.timeLock) {
                        try {
                            TimeThread.this.timeLock.wait();
                        } catch (InterruptedException var5) {
                            var5.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }
            }

        }
    });

    public TimeThread(TimeThread.TimeListener listener) {
        this.mListener = listener;
    }

    public void startTime() {
        if(!this.workThread.isAlive()) {
            this.workThread.start();
        }

        this.isWork = true;
        Object var1 = this.timeLock;
        synchronized(this.timeLock) {
            this.timeLock.notify();
        }
    }

    public void pauseTime() {
        this.isWork = false;
    }

    public void stopTime() {
        this.isWork = false;
        this.count = 0;
    }

    public void releaseThread() {
        this.isRunning = false;
        this.workThread.interrupt();
        this.workThread = null;
    }

    public interface TimeListener {
        void OnTime(String var1);
    }
}