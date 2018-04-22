package com.fengy.android.recordaudio.Util;

import android.annotation.SuppressLint;

/**
 * Created by admin on 2018/4/22.
 */

public class Format {
    public static final int KEY_SAMPLE_RATE = 44100;
    public static final int KEY_CHANNEL_COUNT = 16;
    public static final int KEY_AUDIO_FORMAT = 2;
    public static final String MIME_TYPE = "audio/mp4a-latm";
    public static final int KEY_BIT_RATE = 65536;
    @SuppressLint({"InlinedApi"})
    public static final int KEY_AAC_PROFILE = 2;

    public Format() {
    }
}
