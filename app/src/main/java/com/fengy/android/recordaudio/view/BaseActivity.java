package com.fengy.android.recordaudio.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by admin on 2018/4/22.
 */

public class BaseActivity extends AppCompatActivity {
   private static final int REQUEST_CODE = 200;
   private  String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(this,
                permissions,
                REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            for (int grantResult : grantResults) {
                if(grantResult!= PackageManager.PERMISSION_GRANTED){
                    Log.i("test","权限未通过!");
                    return;
                }
            }
            Log.i("test","权限通过!");
        }
    }
}
