package com.fengy.android.recordaudio.encord;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.fengy.android.recordaudio.Util.Format;
import com.fengy.android.recordaudio.Util.INotify;
import com.fengy.android.recordaudio.Util.ITaskContext;
import com.fengy.android.recordaudio.Util.Task;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by admin on 2018/4/22.
 */

public class Encoder implements ITaskContext {
    private static final String TAG = "Encoder";
    private INotify mNotifyer = null;
    private byte[] mFrameByte = null;
    private MediaCodec mEncoder = null;
    private Task mEncodeTask = null;
    private boolean isRunning = true;

    @SuppressLint({"WrongConstant"})
    public Encoder(INotify notifyer) {
        Log.d("Encoder", "Encoder(INotify notifyer)！！！！！！！！！！！！！！！！！！！！！");
        this.mNotifyer = notifyer;

        try {
            this.mEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        MediaFormat format = new MediaFormat();
        format.setString("mime", "audio/mp4a-latm");
        format.setInteger("bitrate", 65536);
        format.setInteger("channel-count", 1);
        format.setInteger("sample-rate", Format.KEY_SAMPLE_RATE);
        format.setInteger("aac-profile", 2);
        this.mEncoder.configure(format, (Surface)null, (MediaCrypto)null, 1);
        int maxFrameSize = AudioRecord.getMinBufferSize(Format.KEY_SAMPLE_RATE, 16, 2);
        this.mFrameByte = new byte[maxFrameSize];
        this.mEncodeTask = new Task(this);
    }

    public void startEnCoder() {
        Log.d("Encoder", "startEnCoder！！！！！！！！！！！！！！！！！！！！！");
        this.mEncoder.start();
        this.mEncodeTask.restart();
    }

    public void stopEnCoder() {
        Log.d("Encoder", "stopEnCoder！！！！！！！！！！！！！！！！！！！！！");
        this.mEncodeTask.stop();
        this.mEncoder.stop();
    }

    public void realseEncoder() {
        this.stopEnCoder();
        this.mEncoder.release();
        this.mEncoder = null;
    }

    public boolean doTask() {
        try {
            Log.d("Encoder", "doTask！！！！！！！！！！！！！！！！！！！！！---Start");
            MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = this.mEncoder.dequeueOutputBuffer(mBufferInfo, -1L);
            Log.d("Encoder", "Encoder doTask！！！！！！！！！！！！！！！！！！！！！");
            if((mBufferInfo.flags & 2) != 0) {
                mBufferInfo.size = 0;
            }

            if(outputBufferIndex >= 0) {
                if(mBufferInfo.size != 0) {
                    ByteBuffer[] outputBuffer = this.mEncoder.getOutputBuffers();
                    int length = mBufferInfo.size + 7;
                    if(this.mFrameByte == null || this.mFrameByte.length < length) {
                        this.mFrameByte = new byte[length];
                    }

                    outputBuffer[outputBufferIndex].get(this.mFrameByte, 7, mBufferInfo.size);
                    this.addADTStoPacket(this.mFrameByte, length);
                    this.mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                    this.mNotifyer.onDataComeing(this, this.mFrameByte, length);
                } else {
                    Log.d("Encoder", "mBufferInfo size" + mBufferInfo.size);
                }
            } else {
                Log.d("Encoder", "outputBufferIndex <0！！！！！！！！！！！！！！！！！！！！！");
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return true;
    }

    public void onStateChanged(int nState) {
        this.mNotifyer.onStateChanged(this, nState);
    }

    public void pushEncodeData(byte[] data, long presentationTimeUs) {
        try {
            Log.e("Encoder", "pushEncodeData---------------------Start");
            int inputBufferIndex = this.mEncoder.dequeueInputBuffer(-1L);
            if(inputBufferIndex >= 0) {
                ByteBuffer[] inputBuffer = this.mEncoder.getInputBuffers();
                inputBuffer[inputBufferIndex].clear();
                inputBuffer[inputBufferIndex].put(data);
                inputBuffer[inputBufferIndex].limit(data.length);
                this.mEncoder.queueInputBuffer(inputBufferIndex, 0, data.length, presentationTimeUs, 0);
            } else {
                Log.e("Encoder", "inputBufferIndex <= 0");
            }
        } catch (Exception var6) {
            ;
        }

    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;
        int freqIdx = 4;
        int chanCfg = 2;
        packet[0] = -1;
        packet[1] = -7;
        packet[2] = (byte)((profile - 1 << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte)(((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte)((packetLen & 2047) >> 3);
        packet[5] = (byte)(((packetLen & 7) << 5) + 31);
        packet[6] = -4;
    }
}
