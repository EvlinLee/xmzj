package com.gxtc.huchuan.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.gxtc.commlibrary.utils.LogUtil;

/**
 * Created by sjr on 2017/3/29.
 * 带进度条加载的webview
 */

public class ProgressWebView extends WebView {

    private ProgressBar progressbar;
    private Handler handler;

    @SuppressLint("SetJavaScriptEnabled")
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        setColors(progressbar,
                0xffFFFFFF,   //bgColor blue
                0xff2b8cff//progressColor red
        );
        addView(progressbar);

        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        getSettings().setDomStorageEnabled(true);              //开启 DOM storage API 功能
        getSettings().setDatabaseEnabled(true);                //开启 database storage API 功能
        getSettings().setAppCacheEnabled(true);                //开启 Application Caches 功能
        getSettings().setBlockNetworkImage(true);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            LogUtil.i("newProgress   : " + newProgress);
            if (newProgress == 100) {
                progressbar.setProgress(100);
                handler.postDelayed(runnable, 500); //0.5秒后隐藏进度条
            } else if (progressbar.getVisibility() == GONE) {
                progressbar.setVisibility(VISIBLE);
            }
            //设置初始进度10，显得效果真一点
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            progressbar.setProgress(newProgress );
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setColors(ProgressBar progressBar, int backgroundColor, int progressColor) {
        //Background
        ClipDrawable bgClipDrawable = new ClipDrawable(new ColorDrawable(backgroundColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        bgClipDrawable.setLevel(10000);
        //Progress
        ClipDrawable progressClip = new ClipDrawable(new ColorDrawable(progressColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgClipDrawable, progressClip/*second*/, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);

        progressBar.setProgressDrawable(progressLayerDrawable);
    }

    /**
     * 刷新界面（此处为加载完成后进度消失）
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressbar.setVisibility(GONE);
        }
    };
    public void clearAllAction(){
        handler.removeCallbacks(runnable);//避免内存泄漏
        setWebChromeClient(null);
        handler = null;
    }
}
