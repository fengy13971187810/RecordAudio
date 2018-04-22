package com.fengy.android.recordaudio.view;

import android.animation.TimeAnimator;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fengy.android.recordaudio.R;
import com.fengy.android.recordaudio.Util.AudioContent;
import com.fengy.android.recordaudio.Util.AudioManager;
import com.fengy.android.recordaudio.Util.BufferChunk;
import com.fengy.android.recordaudio.Util.INotify;
import com.fengy.android.recordaudio.Util.TimeThread;
import com.fengy.android.recordaudio.encord.Encoder;
import com.fengy.android.recordaudio.record.Recorder;
import java.io.File;

/**
 * Created by admin on 2018/4/22.
 */

public class MainActivity extends BaseActivity implements INotify, TimeThread.TimeListener, View.OnClickListener {
    Button start;
    Button stop;
    TextView time;
    private AudioManager audioTranAPI;
    public static final String path = Environment.getExternalStorageDirectory().getPath() + "/record";
    private static final String TAG = "record";
    private TimeThread thread;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_audio);
        this.initView();
        this.init();
    }

    private void initView() {
        this.start = (Button)this.findViewById(R.id.start);
        this.stop = (Button)this.findViewById(R.id.stop);
        this.time = (TextView)this.findViewById(R.id.time);
        this.start.setOnClickListener(this);
        this.stop.setOnClickListener(this);
    }

    private void init() {
        this.audioTranAPI = new AudioManager(this);
        this.thread = new TimeThread(this);
    }

    public boolean createAudio() {
        try {
            File file = new File(path);
            if(!file.exists()) {
                file.mkdirs();
            }

            File mfile = new File(path + File.separator + System.currentTimeMillis() + ".aac");
            mfile.createNewFile();
            if(this.audioTranAPI != null) {
                this.audioTranAPI.createAudioFile(mfile.getPath());
            }
            return true;
        } catch (Exception var3) {
            var3.printStackTrace();
            Log.i("test","文件创建失败");
            return false;
        }
    }

    public void onClick(View view) {
       switch (view.getId()){
           case R.id.start:
               if(start.getText().equals("开 始")){
                   if(audioTranAPI.getmState().equals(AudioContent.IDLE_RECORDER)||audioTranAPI.getmState().equals(AudioContent.STOP_RECORDER)){
                       if(createAudio()){
                           audioTranAPI.StartAudioTran();
                           startTime();
                           start.setText("暂 停");

                       }
                   }else if(audioTranAPI.getmState().equals(AudioContent.PAUSE_RECORDER)){
                           audioTranAPI.StartAudioTran();
                           startTime();
                           start.setText("暂 停");
                   }
               }else{
                   audioTranAPI.StopAudioTran();
                   pauseTime();
                   start.setText("开 始");
               }
               break;

           case R.id.stop:
               if(audioTranAPI.getmState()!=AudioContent.IDLE_RECORDER&&audioTranAPI.getmState()!=AudioContent.STOP_RECORDER){
                   audioTranAPI.stopRecordFile();
                   stopTime();
                   time.setText("00:00:00");
               }
               break;
       }
    }

    public void onDataComeing(Object context, byte[] data, int size) {
        if(this.audioTranAPI != null) {
            if(Recorder.class.isInstance(context)) {
                if(this.audioTranAPI.getmDataPusher() != null) {
                    BufferChunk bufferChunk = new BufferChunk(data, size, System.nanoTime());
                    this.audioTranAPI.getmDataPusher().push(bufferChunk);
                }
            } else if(Encoder.class.isInstance(context)) {
                this.audioTranAPI.saveInAACfile(data, size);
            }

        }
    }

    public void onStateChanged(Object o, int i) {
    }

    public void startTime() {
        if(this.thread != null) {
            this.thread.startTime();
        }

    }

    public void pauseTime() {
        if(this.thread != null) {
            this.thread.pauseTime();
        }

    }

    public void stopTime() {
        if(this.thread != null) {
            this.thread.stopTime();
        }

    }

    public void OnTime(String t) {
        this.time.setText(t);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.thread = null;
        if(this.audioTranAPI != null) {
            this.audioTranAPI.realse();
        }

    }
}

