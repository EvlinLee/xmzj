package com.imnjh.imagepicker.util;

import android.content.Intent;

import java.util.HashMap;

/**
 * Created by Gubr on 2017/5/22.
 */

public class PhotoManager {
   public Intent mIntent;
    public static PhotoManager getInstance() {
        return SingletonHolder.sInstance;
    }


    static class SingletonHolder {
        static PhotoManager sInstance = new PhotoManager();
    }
}
