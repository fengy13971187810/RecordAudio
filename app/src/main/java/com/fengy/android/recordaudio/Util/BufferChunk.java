package com.fengy.android.recordaudio.Util;

/**
 * Created by admin on 2018/4/22.
 */

public class BufferChunk {
    private byte[] buffer;
    private long mPresentationTimeUs;

    public BufferChunk(byte[] data, int size, long presentationTimeUs) {
        this.buffer = new byte[size];
        System.arraycopy(data, 0, this.buffer, 0, size);
        this.mPresentationTimeUs = presentationTimeUs;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public long getmPresentationTimeUs() {
        return this.mPresentationTimeUs;
    }
}
