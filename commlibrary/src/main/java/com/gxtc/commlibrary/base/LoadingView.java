package com.gxtc.commlibrary.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gxtc.commlibrary.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LoadingView {

    private ImageView img;
    private FrameLayout baseLoadingArea;

    private static AnimationDrawable anim ;

    public LoadingView(View view) {
        baseLoadingArea = (FrameLayout) view.findViewById(R.id.base_loading_area);
        img = (ImageView) view.findViewById(R.id.img_waiting);
        //anim = (AnimationDrawable) img.getDrawable();
    }

    /**
     * 显示加载背景
     */
    public void showLoading() {
        showLoading(false);
    }

    private boolean isLoading = false;
    private Subscription loadSub;

    /**
     * 显示半透明加载背景
     * @param translucenceBg
     */
    public void showLoading(final boolean translucenceBg){
        anim = createAnim();

        if(!isLoading && anim != null){
            img.setBackgroundDrawable(anim);
            isLoading = true;
            loadSub =
                Observable.timer(400, TimeUnit.MILLISECONDS)
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new Action1<Long>() {
                              @Override
                              public void call(Long aLong) {
                                  if(isLoading && baseLoadingArea != null && anim != null && !anim.isRunning()){
                                      baseLoadingArea.setVisibility(View.VISIBLE);
                                      if(translucenceBg){
                                          baseLoadingArea.setBackgroundColor(Color.parseColor("#66000000"));
                                      }
                                      anim.start();
                                  }
                              }
                          });
        }
    }

    public void hideLoading() {
        baseLoadingArea.setVisibility(View.GONE);
        if(anim != null){
            anim.stop();
            anim = null;
        }
        if(loadSub != null)
            loadSub.unsubscribe();
        loadSub = null;

        isLoading = false;
    }


    public void show(boolean isLoading){
        if(isLoading){
            showLoading();
        }else{
            hideLoading();
        }
    }



    private AnimationDrawable createAnim(){
        Context context = img.getContext();
        Resources resources = context.getResources();
        if(resources != null){
            //创建帧动画
            AnimationDrawable animationDrawable = new AnimationDrawable();
            //添加帧
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_0), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_1), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_2), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_3), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_4), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_5), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_6), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_7), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_8), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_9), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_10), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_11), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_12), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_13), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_14), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_15), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_16), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_17), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_18), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_19), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_20), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_21), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_22), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_23), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_24), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_25), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_26), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_27), 40);
            animationDrawable.addFrame(resources.getDrawable(R.drawable.load_animation_28), 40);

            //设置动画是否只播放一次， 默认是false
            animationDrawable.setOneShot(false);

            return animationDrawable;
        }
        return null;
    }

}
