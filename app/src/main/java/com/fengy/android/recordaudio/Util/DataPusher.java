package com.fengy.android.recordaudio.Util;

import android.util.Log;

import com.fengy.android.recordaudio.encord.Encoder;

/**
 * Created by admin on 2018/4/22.
 */

public class DataPusher implements ITaskContext {
    private static final String TAG = "DataPusher";
    private Task mPushTask = new Task(this);
    private ProsumerQueue mQueue = new ProsumerQueue();
    private Encoder mEncoder = null;
    private boolean isPush = true;

    public DataPusher(Encoder encoder) {
        if(encoder != null) {
            this.mEncoder = encoder;
            this.mPushTask.restart();
        }

    }

    public void push(BufferChunk data) {
        this.isPush = true;
        this.mQueue.enqueue(data);
    }

    public void clear() {
        this.isPush = false;
        this.mQueue.clear();
    }

    public boolean doTask() {
        Log.d("DataPusher", "doTask DataPusher！！！！！！！！！！！！！！！！！！！！！");
        BufferChunk buffChunk = this.mQueue.dequeue();
        if(this.isPush) {
            this.mEncoder.pushEncodeData(buffChunk.getBuffer(), buffChunk.getmPresentationTimeUs());
            buffChunk = null;
        }

        return true;
    }

    public void onStateChanged(int nState) {
    }
}