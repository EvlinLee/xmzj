package com.gxtc.huchuan.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.LiveActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity;
import com.gxtc.huchuan.utils.ADFilterTool;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;

import butterknife.BindView;

/**
 * Created by 宋家任 on 2017/4/9.
 * 暂时是通用的webview，文章详情除外
 */

public class CommonWebViewActivity extends BaseTitleActivity {

    @BindView(R.id.webView)           WebView      webView;
    @BindView(R.id.ll_webview_bottom) LinearLayout llWebviewBottom;

    String mUrl;
    private String mWebTitle;

    private boolean isShare;

    public static void startActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, CommonWebViewActivity.class);
        intent.putExtra("web_url", url);
        intent.putExtra("web_title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_news_web_view);
        initData();
        initWebView();
    }


    @Override
    public void initData() {
        llWebviewBottom.setVisibility(View.GONE);

        final Intent intent = getIntent();
        mUrl = intent.getStringExtra("web_url");
        if(mUrl.contains("?")){
            mUrl = mUrl + "&from=android" + "&viewUserCode=" + UserManager.getInstance().getUserCode();
        }else{
            mUrl = mUrl + "?&from=android" + "&viewUserCode=" + UserManager.getInstance().getUserCode();
        }
        mWebTitle = intent.getStringExtra("web_title");
        if (!"".equals(mWebTitle))
            getBaseHeadView().showTitle(mWebTitle);

        if (isShare) {
            //分享
        }
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                    return;
                }
                CommonWebViewActivity.this.finish();
            }
        });

    }

    private void initWebView() {
        webView.loadUrl(mUrl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        webView.getSettings().setDomStorageEnabled(true);// 开启 DOM storage API 功能
        webView.getSettings().setDatabaseEnabled(true);//开启 database storage API 功能
        webView.getSettings().setAppCacheEnabled(true);//开启 Application Caches 功能
        webView.getSettings().setBlockNetworkImage(true);
        webView.addJavascriptInterface(this, "xmzj");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getBaseHeadView().showTitle(view.getTitle());
                webView.getSettings().setBlockNetworkImage(false);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == WebViewClient.ERROR_CONNECT) {
                    showNetError();
                }
            }

            /**
             * 广告拦截
             * @param view
             * @param url
             * @return
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                url = url.toLowerCase();
                if (!url.contains(mUrl)) {
                    if (!ADFilterTool.hasAd(CommonWebViewActivity.this, url)) {
                        return super.shouldInterceptRequest(view, url);
                    } else {
                        return new WebResourceResponse(null, null, null);
                    }
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * 初始网络错误，点击重新加载
     */
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //防止内存泄露
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeJavascriptInterface("xmzj");
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }



    /**
     * type，0：圈子，1：课程，2：系列课，3：交易  4: 群聊邀请  5: 文章  6: 商品  7: 交易详情  8: 个人主页
     */
    @JavascriptInterface
    public void onJumpNative(String type, String id){
        switch (type){
           case "0":
               if(!TextUtils.isEmpty(id)){
                    CircleMainActivity.startActivity(this, StringUtil.toInt(id));
               }
               break;

            case "1":
                if(TextUtils.isEmpty(id)){
                    GotoUtil.goToActivity(this, LiveActivity.class);
                }else{
                    LiveIntroActivity.startActivity(this, id);
                }
                break;

            case "2":
                if(!TextUtils.isEmpty(id)){
                    SeriesActivity.startActivity(this, id);
                }
                break;

            case "5":
                if(!TextUtils.isEmpty(id)){
                    CircleShareHandler handler = new CircleShareHandler(this);
                    handler.getNewsData(id);
                }
                break;

            case "6":
                if(!TextUtils.isEmpty(id)){
                    MallDetailedActivity.startActivity(this, id);
                }
                break;

            case "7":
                if(!TextUtils.isEmpty(id)){
                    GoodsDetailedActivity.startActivity(this, id);
                }
                break;

            case "8":
                if(!TextUtils.isEmpty(id)){
                    PersonalHomePageActivity.startActivity(this, id);
                }
                break;
        }
    }

}
