package com.gxtc.huchuan.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.*;

/**
 * Created by Steven on 17/3/10.
 * 带旋转动画的按钮
 */
public class RotateImageButton extends ImageView {

    private static final int DURATION = 6 * 1000;

    private Animation animation;

    private boolean isRotate = false;
    private ObjectAnimator mAnimator;

    public RotateImageButton(Context context) {
        this(context, null);
    }

    public RotateImageButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimator();
    }

    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        mAnimator.setDuration(DURATION);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    public boolean isRotate() {
        return isRotate;
    }

    public void setRotate(boolean rotate) {
        if (rotate) {
            startRotate();
        } else {
            stopRotate();
        }
        isRotate = rotate;
    }

    //开始旋转
    private void startRotate() {
        if (mAnimator != null && !isRotate) {
            mAnimator.start();
        }
    }

    //停止旋转
    private void stopRotate() {
        if (mAnimator != null && isRotate) {
            mAnimator.cancel();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        animation = new RotateAnimation(0, 360, w / 2, h / 2);
//        animation.setDuration(DURATION);
//        animation.setRepeatMode(Animation.INFINITE);
//        animation.setRepeatCount(Animation.INFINITE);
//        animation.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }
}
