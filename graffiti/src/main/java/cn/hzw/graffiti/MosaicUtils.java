package cn.hzw.graffiti;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/22.
 * 生成马赛克底图的工具
 */
public class MosaicUtils {

    /**
     * 马赛克效果
     *
     * @param bitmap 原图
     * @return 马赛克图片
     */
    public static Bitmap getMosaic(Bitmap bitmap) {
        if(bitmap == null)  return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = 40;       //模糊半径


        Bitmap mosaicBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(mosaicBitmap);

        int horCount = (int) Math.ceil(width / (float) radius);
        int verCount = (int) Math.ceil(height / (float) radius);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int horIndex = 0; horIndex < horCount; ++horIndex) {
            for (int verIndex = 0; verIndex < verCount; ++verIndex) {
                int l = radius * horIndex;
                int t = radius * verIndex;
                int r = l + radius;
                if (r > width) {
                    r = width;
                }
                int b = t + radius;
                if (b > height) {
                    b = height;
                }
                int  color = bitmap.getPixel(l, t);
                Rect rect  = new Rect(l, t, r, b);
                paint.setColor(color);
                canvas.drawRect(rect, paint);
            }
        }
        canvas.save();

        return mosaicBitmap;
    }

}
