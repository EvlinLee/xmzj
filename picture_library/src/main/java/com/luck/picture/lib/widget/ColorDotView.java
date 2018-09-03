package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.luck.picture.lib.R;


/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/23.
 * 用在图片编辑画笔颜色选择那里
 */

public class ColorDotView extends View{

    private int width = 0;
    private int height = 0;
    private int radius = 0;
    private int maxRadius = 0;      //选中状态的半径

    private int color = 0;

    private Rect mRect;
    private Paint mPaint;

    public ColorDotView(Context context) {
        this(context, null);
    }

    public ColorDotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Drawable d = getBackground();
        if(d instanceof ColorDrawable){
            color = ((ColorDrawable) d).getColor();
        }else{
            color = context.getResources().getColor(R.color.color_4d);
        }
        setBackgroundColor(0);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(isSelected()){
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawCircle(width / 2, width / 2, maxRadius, mPaint);
            mPaint.setColor(color);
            canvas.drawCircle(width / 2, width / 2, maxRadius - 5, mPaint);
        }else{
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawCircle(width / 2, width / 2, radius, mPaint);
            mPaint.setColor(color);
            canvas.drawCircle(width / 2, width / 2, radius - 5, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        maxRadius = Math.min(width, height) / 2;
        radius = maxRadius - 5;

        if(mRect == null){
            mRect = new Rect(0, 0, radius, radius);
        }
    }

    public int getColor() {
        return color;
    }
}
