package com.fengy.android.recordaudio.Util;

import com.fengy.android.recordaudio.encord.Encoder;
import com.fengy.android.recordaudio.record.Recorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 2018/4/22.
 */

public class AudioManager implements INotify {
    private Recorder mRecorder = null;
    private Encoder mEncoder = null;
    private DataPusher mDataPusher = null;
    private int mSourceID = 1;
    private int mRecordState = 0;
    private INotify mNotifyer = null;
    public FileOutputStream mfileWitter;
    private String mState = "idle";

    public String getmState() {
        return this.mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public AudioManager(INotify notifyer) {
        this.mRecorder = new Recorder(this.mSourceID, this);
        this.mNotifyer = notifyer;
        this.mEncoder = new Encoder(this);
        this.mEncoder.startEnCoder();
        this.mDataPusher = new DataPusher(this.mEncoder);
    }

    public void createAudioFile(String fileName) {
        try {
            this.mfileWitter = new FileOutputStream(fileName);
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

    }

    public void StartAudioTran() {
        this.setmState("start_recorder");
        if(this.mRecorder != null) {
            this.mRecorder.startRecord();
        } else {
            this.mRecorder = new Recorder(this.mSourceID, this);
            this.mRecorder.startRecord();
        }

    }

    public void StopAudioTran() {
        this.setmState(this.mState = "pause_recorder");
        if(this.mRecorder != null) {
            this.mRecorder.stopRecord();
        }

    }

    public void onDataComeing(Object context, byte[] data, int size) {
        if(Recorder.class.isInstance(context)) {
            if(this.mDataPusher != null) {
                BufferChunk bufferChunk = new BufferChunk(data, size, System.nanoTime());
                this.mDataPusher.push(bufferChunk);
            }
        } else if(Encoder.class.isInstance(context)) {
            this.mNotifyer.onDataComeing(context, data, size);
        }

    }

    public void onStateChanged(Object context, int state) {
        if(Recorder.class.isInstance(context)) {
            this.mRecordState = state;
        } else {
            this.mNotifyer.onStateChanged(context, state);
        }

    }

    public DataPusher getmDataPusher() {
        return this.mDataPusher;
    }

    public void saveInAACfile(byte[] packet, int packetLen) {
        byte[] data = new byte[packetLen];
        System.arraycopy(packet, 0, data, 0, packetLen);

        try {
            if(this.mfileWitter != null) {
                this.mfileWitter.write(data);
                this.mfileWitter.flush();
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public boolean stopRecordFile() {
        this.setmState("stop_recorder");
        this.mRecorder.stopRecord();
        this.mDataPusher.clear();
        if(this.mfileWitter != null) {
            try {
                this.mfileWitter.close();
                return true;
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        return false;
    }

    public void realse() {
        if(this.mRecorder != null) {
            this.mRecorder.release();
            this.mRecorder = null;
        }

        if(this.mDataPusher != null) {
            this.mDataPusher.clear();
            this.mDataPusher = null;
        }

        if(this.mEncoder != null) {
            this.mEncoder.realseEncoder();
            this.mEncoder = null;
        }

    }
}
