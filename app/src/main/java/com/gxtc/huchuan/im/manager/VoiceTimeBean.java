package com.gxtc.huchuan.im.manager;

import android.net.Uri;

import io.rong.message.VoiceMessage;

/**
 * Created by Gubr on 2017/2/27.
 * 通过这个 来接收播放进度
 */

public class VoiceTimeBean {
    private Uri          mUri;
    private int          currentposition;
    private int          duration;
    private boolean      isPlayed;//是否播放结果
    private boolean      isPlaying;
    private VoiceMessage mVoiceMessage;
    private String       targetId;
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public int getCurrentposition() {
        return currentposition;
    }

    public void setCurrentposition(int currentposition) {
        this.currentposition = currentposition;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public VoiceMessage getVoiceMessage() {
        return mVoiceMessage;
    }

    public void setVoiceMessage(VoiceMessage voiceMessage) {
        mVoiceMessage = voiceMessage;
    }




    public VoiceTimeBean(VoiceMessage voiceMessage) {
        mVoiceMessage = voiceMessage;
        currentposition = 0;
        duration = voiceMessage.getDuration();
        mUri = voiceMessage.getUri();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoiceTimeBean that = (VoiceTimeBean) o;

        return mUri != null ? mUri.equals(that.mUri) : that.mUri == null;

    }

    @Override
    public int hashCode() {
        return mUri != null ? mUri.hashCode() : 0;
    }
}
