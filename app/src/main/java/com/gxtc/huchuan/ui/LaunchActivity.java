package com.gxtc.huchuan.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.commlibrary.widget.MyRadioGroup;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.WelcomeAdapter;
import com.gxtc.huchuan.bean.ListNewsHeadBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.utils.AdLancherClickUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * 启动界面
 * @author 伍玉南
 */
public class LaunchActivity extends AppCompatActivity implements MyRadioGroup.OnPageChangeListener {

    public final static String FIRST_OPEN = "first_open";
    private  int WAIT_TIME = 1000;

    @BindView(R.id.viewpager_welcome)  ViewPager             viewPager;
    @BindView(R.id.btn_welcome_access) TextView              button;
    @BindView(R.id.layout_welcome)     RelativeLayout        layoutWelcome;
    @BindView(R.id.img_launch)         ImageView             imgLaunch;
    @BindView(R.id.img_ad)             ImageView             imgAd;
    @BindView(R.id.layout_ad)          View                  mLinearLayout;
    @BindView(R.id.radioGroup)         MyRadioGroup          radioGroup;
    @BindView(R.id.btn_skip)           BootstrapButton       btnSkip;

    private List<View>    views;
    private List<Integer> imgIds;            //欢迎页的imgId

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        /**
         * 用户在使用app时用户点击Home键切出应用到桌面，再从桌面点击应用程序图标试图切回刚刚打开的界面时，
         * 应用会重新启动，而从后台菜单选项中点击进入不会重新打开，经过查找也试过很多种方法，
         * 设置activity的launchMode等都不能解决此问题。其实原因很简单，利用程序安装器打开程序，
         * 启动的Intent是没有带Category，而我们自己打开程序是带了Category,所以只需要在配置Intent.ACTION_MAIN
         * 的Activity判断下有无Category。然后放到通知栏的时候要更具有无Category 来生成启动的Intent。
         * http://blog.csdn.net/u014172743/article/details/50719188
         */
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        boolean firstLaunch = SpUtil.getBoolean(getApplicationContext(), FIRST_OPEN, true);
        //判断是否是第一次启动APP
        if (firstLaunch) {
            SpUtil.putBoolean(getApplicationContext(), FIRST_OPEN, false);
            showWelcome();
        } else {
            if(ACache.get(MyApplication.getInstance()).getAsObject("app_lanch_ad") == null){
                showImg(null);
            }else {
                ListNewsHeadBean bean = (ListNewsHeadBean) ACache.get(MyApplication.getInstance()).getAsObject("app_lanch_ad");
                showImg(bean);
            }
        }
        getAd();            //每次来到启动页都直接去拿广告并存储
        initListener();
    }

    private void getAd() {
        Subscription sub =
                AllApi.getInstance()
                      .getNewsAds("03")
                      .flatMap(new Func1<ApiResponseBean<List<NewsAdsBean>>, Observable<ListNewsHeadBean>>() {
                            @Override
                            public Observable<ListNewsHeadBean> call(ApiResponseBean<List<NewsAdsBean>> listApiResponseBean) {
                                    final ListNewsHeadBean bean = new ListNewsHeadBean();
                                    bean.setAdvertise(listApiResponseBean.getResult());
                                    return Observable.just(bean);
                            }
                        })
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ListNewsHeadBean>(
                        new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                ListNewsHeadBean bean = (ListNewsHeadBean) data;
                                if(ACache.get(MyApplication.getInstance()).getAsObject("app_lanch_ad") != null){
                                    ACache.get(MyApplication.getInstance()).remove("app_lanch_ad");
                                }
                                ACache.get(MyApplication.getInstance()).put("app_lanch_ad", bean);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(MyApplication.getInstance(),message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    public void initListener() {
        radioGroup.setOnPageChangeListener(this);
    }


    @OnClick({R.id.btn_welcome_access, R.id.btn_skip})
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        switch (view.getId()) {
            case R.id.btn_welcome_access:
                startActivity(intent);
                finish();
                break;
            case R.id.btn_skip:
                startActivity(intent);
                RxTaskHelper.getInstance().cancelTask(this);
                finish();
                break;
        }
    }


    /**
     * 显示欢迎页面
     */
    private void showWelcome() {
        btnSkip.setVisibility(View.GONE);
        views = new ArrayList<View>();
        imgIds = new ArrayList<Integer>();
        imgIds.add(R.drawable.lead_in_pages_1);
        imgIds.add(R.drawable.lead_in_pages_2);
        imgIds.add(R.drawable.lead_in_pages_3);
        imgIds.add(R.drawable.lead_in_pages_4);

       Subscription sub = Observable.from(imgIds).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                ImageView img = new ImageView(MyApplication.getInstance());
                img.setScaleType(ScaleType.FIT_XY);
                views.add(img);
            }
        });
        RxTaskHelper.getInstance().addTask(this,sub);
        viewPager.setAdapter(new WelcomeAdapter(this, views, imgIds));
        radioGroup.setCount(imgIds.size(), viewPager);
    }


    /**
     * 显示启动页面
     */
    private void showImg(final ListNewsHeadBean mbean) {
        mLinearLayout.setVisibility(View.VISIBLE);
        if(mbean != null && mbean.getAdvertise() != null && mbean.getAdvertise().size() > 0 ){
            WAIT_TIME = 3000;
            final NewsAdsBean bean = mbean.getAdvertise().get(0);
            imgAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdLancherClickUtil.performClick(LaunchActivity.this,bean);
                    finish();
                }
            });
            Glide.with(MyApplication.getInstance())
                 .load(R.drawable.boot_page)
                 .apply(new RequestOptions().placeholder(R.drawable.boot_page))
                 .into(imgLaunch);                                          //启动页过大不要直接设置在imageview上，用Glide加载出来避免内存溢出
            Glide.with(MyApplication.getInstance())
                 .load(bean.getCover())
                 .apply(new RequestOptions().dontAnimate())
                 .transition(withCrossFade(ImageHelper.TRANSTION_TIME))
                 .into(new DrawableImageViewTarget(imgAd){
                     @Override
                     public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                         super.onResourceReady(resource, transition);
                         jumpToMainActivity();
                     }
                 });

        }else {
           WAIT_TIME = 1000;
           Glide.with(MyApplication.getInstance()).load(R.drawable.boot_page).apply(new RequestOptions().placeholder(R.drawable.boot_page)).into(imgLaunch);//启动页过大不要直接设置在imageview上，用Glide加载出来避免内存溢出
           jumpToMainActivity();
        }
    }

    private void jumpToMainActivity(){
        Subscription sub =
            Observable.timer(WAIT_TIME, TimeUnit.MILLISECONDS)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Action1<Long>() {
                          @Override
                          public void call(Long aLong) {
                              Intent intent = new Intent(MyApplication.getInstance(), MainActivity.class);
                              startActivity(intent);
                              finish();
                          }
                      });
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void onPageSelected(int arg0) {
        if (arg0 == views.size() - 1) {
            button.setVisibility(View.VISIBLE);
        } else {
            if (button.getVisibility() == View.INVISIBLE) return;
            button.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {}

    @Override
    public void onPageScrollStateChanged(int arg0) {}

    @Override
    protected void onDestroy() {
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }
}
