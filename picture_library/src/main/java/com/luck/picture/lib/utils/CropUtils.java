package com.luck.picture.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/27.
 */

public class CropUtils {

    public static UCrop.Options getDefualtOptions(Context context){
        Resources resources = context.getResources();
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(Color.parseColor("#393a3e"));
        options.setStatusBarColor(Color.parseColor("#393a3e"));
        options.setCropFrameColor(Color.parseColor("#2b8cff"));
        options.setActiveWidgetColor(Color.parseColor("#2b8cff"));
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.SCALE);
        return options;
    }

}
