package com.gxtc.huchuan.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.gxtc.huchuan.customemoji.utils.LogUtils;
import com.gxtc.huchuan.receiver.JobSchedulerManager;

/**
 * zzg
 */
public class OnepxActivity extends AppCompatActivity{

    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(br, new IntentFilter("finish activity"));

        JobSchedulerManager.INSTANCE.startJobScheduler(this);
        checkScreenOn("onCreate");
    }

    @Override
    protected void onDestroy() {
        LogUtils.i("===onDestroy===");
        try {
            unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
            LogUtils.e("receiver is not resisted: " + e);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn("onResume");
    }

    private void checkScreenOn(String methodName) {
        LogUtils.d("from call method: " + methodName);
        PowerManager pm = (PowerManager) OnepxActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        LogUtils.i("isScreenOn: " + isScreenOn);
        if (isScreenOn) {
            finish();
        }
    }

}