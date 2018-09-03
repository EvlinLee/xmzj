package com.gxtc.huchuan.bean;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by zzg on 2017/10/28.
 */

public class SentImageBean {
    public Bitmap bitmap;
    public Intent intent;
    public int   isShare;//0 否，1是

    public int getIsShare() {
        return isShare;
    }

    public void setIsShare(int isShare) {
        this.isShare = isShare;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
