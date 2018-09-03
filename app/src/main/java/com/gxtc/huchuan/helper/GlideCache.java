package com.gxtc.huchuan.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.gxtc.commlibrary.utils.FileStorage;

import java.io.File;

/**
 * Created by Gubr on 2017/4/19.
 */

public class GlideCache implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        File imgCacheFile = FileStorage.getImgCacheFile();
        String downloadDirectoryPath = imgCacheFile + "/GlideCache";
        int cacheSize = 100 * 1000 * 100;
        builder.setDiskCache(new DiskLruCacheFactory(downloadDirectoryPath, cacheSize));
    }


    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {

    }
}
