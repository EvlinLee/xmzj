package com.gxtc.huchuan.im.manager;

import android.net.Uri;
import android.support.annotation.DimenRes;

/**
 * Created by Gubr on 2017/2/13.
 */

public interface IAudioPlayListener {


    /**
     * 准备好 开始播放
     * @param uri  播放的uri
     */
    @Deprecated
    void onStart(Uri uri,int duration);

    /**
     * 播放中停止播放
     * @param uri
     */
    @Deprecated
    void onStop(Uri uri);

    /**播放完成
     * @param uri
     */
    @Deprecated
    void onComplete(Uri uri);


    /**
     * 播放事件
     * @param voiceTimeBean
     */
    void PlayEvent(VoiceTimeBean voiceTimeBean);

    /**
     * 播放进度
     * @param uri
     * @param currentPosition
     */
    @Deprecated
    void onProgress(Uri uri, int currentPosition, int duration);
}
