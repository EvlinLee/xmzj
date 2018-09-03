package com.gxtc.huchuan.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Gubr on 2017/2/13.
 */

public class AudioPlayService extends Service {

    private MediaPlayer mMediaPlayer;


    public class MyBinder extends Binder {
        public AudioPlayService getService() {
            return AudioPlayService.this;
        }
    }


//    public void startPlay(Context context, Uri audioUri,IAudio)

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }


}
