package com.gxtc.huchuan.im.manager;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.view.View;

import java.io.File;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by Gubr on 2017/2/23.
 * <p>
 * <p>
 * 录音管理
 */

public class AudioRecordManager {
    private MediaRecorder mMediaRecorder;
    private Conversation.ConversationType mConversationType;
    private String mTargetId;
    private Uri mAudioPath;
    private long smStartRecTime;
    private int mDuration;

    public static AudioRecordManager getInstance() {
        return SingletonHolder.sInstance;
    }


    static class SingletonHolder {
        static AudioRecordManager sInstance = new AudioRecordManager();
    }


    public void startRecord(Conversation.ConversationType conversationType, String targetId) {
        mConversationType = conversationType;
        mTargetId = targetId;
    }

    public void startRec(Context context) {
        try {
            mMediaRecorder = new MediaRecorder();
            Resources resources = context.getResources();
            mMediaRecorder.setAudioSamplingRate(8000);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.setAudioChannels(1);
            mAudioPath = Uri.fromFile(new File(context.getCacheDir(), System.currentTimeMillis() + "temp.voice"));
            mMediaRecorder.setOutputFile(mAudioPath.getPath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            smStartRecTime = SystemClock.elapsedRealtime();
        } catch (Exception e) {
            e.printStackTrace();
            stopRec();
        }
    }


    public void deleteAudioFile() {
        if (mAudioPath != null) {
            File file = new File(mAudioPath.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void sendAudioFile() {
        if (mAudioPath != null) {
            File file = new File(mAudioPath.getPath());
            if (!file.exists() || file.length() == 0L) {
                return;
            }
        }
    }


    public void stopRec() {
        try {
            if (mMediaRecorder != null) {
                mDuration = (int) (SystemClock.elapsedRealtime() / 1000 - smStartRecTime / 1000);

                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release(){
        stopRec();
    }


    public Uri getAudioPath() {
        return mAudioPath;
    }

    public int getDuration() {
        return mDuration;
    }


}
