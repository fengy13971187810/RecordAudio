package com.fengy.android.recordaudio.Util;

/**
 * Created by admin on 2018/4/22.
 */

public class Task implements Runnable {
    private Thread mTaskContext = null;
    private boolean isRuning = false;
    private Object SyncMuter = null;
    private ITaskContext mTask = null;

    public Task(ITaskContext task) {
        this.mTask = task;
        this.mTaskContext = new Thread(this);
        this.SyncMuter = new Object();
    }

    public void restart() {
        if(this.mTaskContext != null) {
            Object var1 = this.SyncMuter;
            Object var2 = this.SyncMuter;
            synchronized(this.SyncMuter) {
                this.SyncMuter.notify();
            }

            if(!this.mTaskContext.isAlive()) {
                this.mTaskContext.start();
            }
        } else {
            this.mTaskContext = new Thread(this);
            if(!this.mTaskContext.isAlive()) {
                this.mTaskContext.start();
            }
        }

        this.isRuning = true;
        this.mTask.onStateChanged(this.isRuning?1:0);
    }

    public void stop() {
        if(this.isRuning) {
            this.isRuning = false;
            if(this.mTaskContext.isInterrupted()) {
                this.mTaskContext.stop();
                this.mTaskContext = null;
            } else {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

    public boolean isRuning() {
        return this.isRuning;
    }

    public void run() {
        while(true) {
            if(!this.isRuning) {
                try {
                    this.mTask.onStateChanged(this.isRuning?1:0);
                    Object var1 = this.SyncMuter;
                    Object var2 = this.SyncMuter;
                    synchronized(this.SyncMuter) {
                        this.SyncMuter.wait();
                    }

                    this.mTask.onStateChanged(this.isRuning?1:0);
                } catch (InterruptedException var5) {
                    this.isRuning = false;
                }
            } else if(!this.mTask.doTask()) {
                this.isRuning = false;
            }
        }
    }
}
