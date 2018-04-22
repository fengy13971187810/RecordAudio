package com.fengy.android.recordaudio.record;

import android.media.AudioRecord;
import android.util.Log;

import com.fengy.android.recordaudio.Util.Format;
import com.fengy.android.recordaudio.Util.INotify;
import com.fengy.android.recordaudio.Util.ITaskContext;
import com.fengy.android.recordaudio.Util.Task;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by admin on 2018/4/22.
 */

public class Recorder implements ITaskContext {
    private static final String TAG = "Recorder";
    private Task mRecordTask;
    private AudioRecord recorder;
    private int bufferSize;
    private byte[] buffer;
    private ArrayList<INotify> lisenters = new ArrayList();

    public Recorder(int source, INotify notifyer) {
        Log.d("Recorder", "Recorder！！！！！！！！！！！！！！！！！！！！！");
        this.bufferSize = AudioRecord.getMinBufferSize(Format.KEY_SAMPLE_RATE, 16, 2);
        this.buffer = new byte[this.bufferSize];
        this.mRecordTask = new Task(this);
        this.resetSource(source);
        this.lisenters.add(notifyer);
    }

    public void startRecord() {
        Log.d("Recorder", "startRecord！！！！！！！！！！！！！！！！！！！！！");
        if(this.recorder != null && this.recorder.getState() != 0) {
            this.recorder.startRecording();
            this.mRecordTask.restart();
        }

    }

    public void stopRecord() {
        Log.d("Recorder", "resetSource！！！！！！！！！！！！！！！！！！！！！");
        if(this.recorder != null && this.recorder.getState() != 0) {
            this.mRecordTask.stop();
            this.recorder.stop();
        } else {
            Log.e("Recorder", "recorder != null && recorder.getState() != AudioRecord.STATE_UNINITIALIZED");
        }

    }

    public void release() {
        if(this.recorder != null) {
            this.recorder.release();
            this.recorder = null;
        }

    }

    public boolean isRecording() {
        return this.mRecordTask.isRuning();
    }

    public void resetSource(int source) {
        Log.d("Recorder", "resetSource！！！！！！！！！！！！！！！！！！！！！");
        this.mRecordTask.stop();

        try {
            this.recorder = new AudioRecord(source, Format.KEY_SAMPLE_RATE, 16, 2, this.bufferSize * 2);
        } catch (IllegalArgumentException var3) {
            Log.d("Recorder", var3.toString());
            this.recorder = null;
        }

    }

    public boolean doTask() {
        Log.d("Recorder", "Recorder doTask！！！！！！！！！！！！！！！！！！！！！---Tag 1");
        if(this.recorder != null) {
            int bufferReadResult = this.recorder.read(this.buffer, 0, this.bufferSize / 2);
            Log.d("Recorder", "Recorder doTask！！！！！！！！！！！！！！！！！！！！！");
            if(bufferReadResult == -3) {
                return false;
            } else {
                if(bufferReadResult == -2) {
                    Log.d("Recorder", "bufferReadResult== AudioRecord.ERROR_BAD_VALUE");
                } else {
                    this.onDataComeing(this.buffer, bufferReadResult);
                }

                return true;
            }
        } else {
            Log.d("Recorder", "Record false！！！！！！！！！！！！！！！！！！！！！");
            return false;
        }
    }

    public void onStateChanged(int nState) {
        Iterator var3 = this.lisenters.iterator();

        while(var3.hasNext()) {
            INotify it = (INotify)var3.next();
            if(it != null) {
                it.onStateChanged(this, nState);
            }
        }

    }

    private void onDataComeing(byte[] data, int size) {
        Iterator var4 = this.lisenters.iterator();

        while(var4.hasNext()) {
            INotify it = (INotify)var4.next();
            if(it != null) {
                it.onDataComeing(this, data, size);
            }
        }

    }
}
