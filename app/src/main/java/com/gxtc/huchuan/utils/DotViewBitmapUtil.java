package com.gxtc.huchuan.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;

import io.rong.imageloader.core.display.RoundedBitmapDisplayer.RoundedDrawable;

/**
 * Created by sjr on 2017/3/3.
 * 显示未读信息小红点
 */

public class DotViewBitmapUtil {
    //背景半径
    private static final int BACKGROUND_RADIUS = 12;


    public static Bitmap getDotNumViewBitmap(Context context, Bitmap icon, String num) {
        //获取屏幕大小
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float baseDensity = 2.3f;
        float factor = dm.density / baseDensity;

        //初始化画布
        int iconSize = (int) context.getResources().
                getDimension(R.dimen.px110dp);
        Bitmap numIcon = Bitmap.createBitmap(iconSize, iconSize,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(numIcon);

        //背景Paint
        Paint iconPaint = new Paint();
        //抗锯齿
        iconPaint.setAntiAlias(true);

        //对图片进行剪裁，设置为null就会显示整个图片
        Rect src = new Rect(0, 0, icon.getWidth()  , icon.getHeight());
        //图片在Canvas画布中显示的区域，大于src则把src的裁截区放大，小于src则把src的裁截区缩小
        Rect dst = new Rect(0, 0, iconSize, iconSize);
        canvas.drawBitmap(icon, src, dst, iconPaint);
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }
        if (Integer.valueOf(num) > 99) {
            num = "99+";
        }

        //文字Paint
        Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numPaint.setColor(Color.WHITE);
        numPaint.setTextSize(60f * factor);

        numPaint.setTypeface(Typeface.DEFAULT);
        int textWidth = (int) numPaint.measureText(num, 0, num.length());
        int backgroundHeight = (int) (2 * 32 * factor);
        int backgroundWidth = textWidth > backgroundHeight ? (int) (textWidth + 20 * factor)
                : backgroundHeight;

        //保存状态
        canvas.save();
        //画背景
        ShapeDrawable drawable = getBackground(context);
        drawable.setIntrinsicHeight(backgroundHeight );
        drawable.setIntrinsicWidth(backgroundWidth);
        drawable.setBounds(0, 0, backgroundWidth, backgroundHeight);
        canvas.translate(iconSize - backgroundWidth  , 0);
        drawable.draw(canvas);

        //重置状态
        canvas.restore();

        canvas.drawText(num, (float) (iconSize - (backgroundWidth + textWidth) / 2),
                52 * factor, numPaint);
        return numIcon;
    }

    /**
     * 获取圆形背景
     *
     * @param context
     * @return
     */
    private static ShapeDrawable getBackground(Context context) {
        int radius = WindowUtil.dip2px(context, BACKGROUND_RADIUS);
        float[] other = new float[]{radius, radius, radius, radius,
                radius, radius, radius, radius};

        RoundRectShape rrs = new RoundRectShape(other, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rrs);
        drawable.getPaint().setColor(Color.RED);
        return drawable;
    }
}
