package com.gxtc.huchuan.ui.mine.loginandregister;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LoginRegisterAdapter;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.login.LoginContract;
import com.gxtc.huchuan.ui.mine.loginandregister.login.LoginFragment;
import com.gxtc.huchuan.ui.mine.loginandregister.login.LoginPrenster;
import com.gxtc.huchuan.ui.mine.loginandregister.register.RegisteFragment;
import com.gxtc.huchuan.ui.mine.loginandregister.register.RegisterContract;
import com.gxtc.huchuan.widget.CusWrapHeightViewPager;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.eventbus.EventBus;

import static com.umeng.socialize.bean.SHARE_MEDIA.QQ;

/**
 * 登录注册
 */
public class LoginAndRegisteActivity extends BaseTitleActivity implements LoginContract.View {

    private static final String TAG = LoginAndRegisteActivity.class.getSimpleName();

    //@BindView(R.id.vp_login_tab)   CusWrapHeightViewPager mVpLoginTab;
    @BindView(R.id.vp_login_tab)   ViewPager              mVpLoginTab;
    @BindView(R.id.iv_qq)          ImageView              mIvQq;
    @BindView(R.id.iv_webchat)     ImageView              mIvWebchat;
    @BindView(R.id.iv_weibo)       ImageView              mIvWeibo;
    @BindView(R.id.tabLayout_main) TabLayout              mTabLayoutMain;

    private LoginRegisterAdapter       adapter;
    private List<Fragment>             fragments;
    private LoginContract.Presenter    mPresenter;
    private RegisterContract.Presenter mRegistePresenter;

    private LoginFragment           loginFragment;
    private RegisteFragment         registeFragment;
    private ProgressDialog          mDialog;
    private HashMap<String, String> dataMap;
    private Map<String, String>     authMap;
    private String                  thirdType;           //第三方登录类型
    private String                  uniqueKey;           //第三方登录唯一标识码
    public AuthImp authListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        EventBusUtil.register(this);
    }

    @Override
    public void initData() {
        super.initData();

        new LoginPrenster(this);
        mTabLayoutMain.setupWithViewPager(mVpLoginTab);
        mDialog = new ProgressDialog(this);
        authListener = new AuthImp(this);
        initViewPager();

    }

    private void initViewPager() {
        String[] arrTabTitles = getResources().getStringArray(R.array.title_login_register);
        fragments = new ArrayList<>();
        loginFragment = new LoginFragment();
        registeFragment = new RegisteFragment();
        fragments.add(loginFragment);
        fragments.add(registeFragment);
        mVpLoginTab.setAdapter(new LoginRegisterAdapter(getSupportFragmentManager(), fragments, arrTabTitles));

        //Viewpager自适应子布局的高度
        mVpLoginTab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*if (position == 0) {
                    mVpLoginTab.resetHeight(position);
                } else if (position == 1) {
                    mVpLoginTab.resetHeight(position);
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.iv_qq, R.id.iv_webchat, R.id.iv_weibo})
    public void onClick(View v) {
        WeakReference<Activity> weakRefrente = new WeakReference<Activity>(this);
        if(weakRefrente.get() == null) return;
        Activity activity = weakRefrente.get();
        UMShareAPI mShareAPI = UMShareAPI.get(activity);
        switch (v.getId()) {
            case R.id.iv_qq:
                if (mShareAPI.isInstall(activity, QQ)) {
                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDialog.show();
                    mDialog.setMessage("正在努力授权登录，请等待..");
                } else {
                    ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), getString(R.string.label_not_install_QQ));
                    return;
                }
                mShareAPI.getPlatformInfo(activity, QQ, authListener);
                break;
            case R.id.iv_webchat:
                if (mShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDialog.show();
                    mDialog.setMessage("正在努力授权登录，请等待..");
                } else {
                    ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), getString(R.string.label_not_install_WeiXin));
                    return;
                }
                mShareAPI.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, authListener);

                break;
            case R.id.iv_weibo:
                if (mShareAPI.isInstall(activity, SHARE_MEDIA.SINA)) {
                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDialog.show();
                    mDialog.setMessage("正在努力授权登录，请等待..");
                } else {
                    ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), getString(R.string.label_not_install_Sina));
                    return;
                }
                mShareAPI.getPlatformInfo(activity, SHARE_MEDIA.SINA, authListener);
                break;

        }

    }

    /**
     * 登录监听
     * 内部类容易内存泄漏，采用静态内部类和弱引用避免内存泄漏
     */
    private static class AuthImp implements UMAuthListener{

        WeakReference<LoginAndRegisteActivity> weakReference;
        LoginAndRegisteActivity activity;

        public AuthImp(LoginAndRegisteActivity mLoginAndRegisteActivity) {
            this.weakReference = new WeakReference<>(mLoginAndRegisteActivity);
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {}

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if(weakReference.get() == null) return;
                activity = weakReference.get();
            if(activity.mDialog.isShowing()){
                activity.mDialog.cancel();
            }
            activity.authMap = map;
            Log.e("data", map.toString());
            switch (share_media) {
                case QQ:
                    activity.thirdType = "1";
                    String uid = map.get("uid");
                    activity.uniqueKey = uid;
                    activity.dataMap = new HashMap();
                    activity.dataMap.put("thirdType", "1");
                    activity.dataMap.put("uniqueKey", map.get("uid"));
                    activity.dataMap.put("nickname", map.get("screen_name"));
                    activity.dataMap.put("headPic", map.get("profile_image_url"));
                    activity.dataMap.put("sex", "男".equals(map.get("gender")) ? "1":"2");//1：男； 2：女
                    activity.thirdLogin(SHARE_MEDIA.QQ, activity.dataMap);
                    break;
                case WEIXIN:
                    activity.thirdType = "2";
                    String unionid = map.get("unionid");
                    activity.uniqueKey = unionid;
                    activity.dataMap = new HashMap();
                    activity.dataMap.put("thirdType", "2");
                    activity.dataMap.put("uniqueKey", map.get("unionid"));
                    activity.dataMap.put("nickname", map.get("screen_name"));
                    activity.dataMap.put("headPic", map.get("profile_image_url"));
                    activity.dataMap.put("sex", "男".equals(map.get("gender")) ? "1":"2");//1：男； 2：女
                    activity.thirdLogin(SHARE_MEDIA.WEIXIN, activity.dataMap);
                    break;
                case SINA:
                    activity.thirdType = "3";
                    String sinaUid = map.get("uid");
                    activity.uniqueKey = sinaUid;
                    activity.dataMap = new HashMap();
                    activity.dataMap.put("thirdType", "3");
                    activity.dataMap.put("uniqueKey", map.get("uid"));
                    activity.dataMap.put("nickname", map.get("screen_name"));
                    activity.dataMap.put("headPic", map.get("profile_image_url"));
                    activity.dataMap.put("sex", "男".equals(map.get("gender")) ? "1":"2");//1：男； 2：女
                    activity.thirdLogin(SHARE_MEDIA.SINA, activity.dataMap);
                    break;
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            if(weakReference.get() == null) return;
            activity = weakReference.get();
            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), "授权错误");
            if(activity.mDialog.isShowing()){
                activity.mDialog.cancel();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            if(weakReference.get() == null) return;
            activity = weakReference.get();
            ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), "授权取消");
            if(activity.mDialog.isShowing()){
                activity.mDialog.cancel();
            }
        }
    }

    private void thirdLogin(SHARE_MEDIA share_media, HashMap<String, String> map) {
        switch (share_media) {
            case QQ:
                mPresenter.getThirdLogin(map);
                break;
            case WEIXIN:
                mPresenter.getThirdLogin(map);
                break;
            case SINA:
                mPresenter.getThirdLogin(map);
                break;
        }
    }

    @Subscribe
    public void onEvent(EventLoadBean bean){
        getBaseLoadingView().show(bean.isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.requestCode.QQLOGIN:
                //注册成功
                /*Intent intent = new Intent();
                setResult(9999, intent);
                finish();*/
                break;
            case Constant.requestCode.WEIXINLOGIN:

                break;
            case Constant.requestCode.SINALOGIN:

                break;
        }
    }

    @Override
    public void showLogin(User bean) {}

    @Override
    public void thirdLoginResult(User bean) {
        UserManager.getInstance().saveUser(bean);
        EventBusUtil.post(new EventLoginBean(EventLoginBean.THIRDLOGIN));
        Intent intent = new Intent();
        this.setResult(Constant.ResponseCode.LOGINRESPONSE_CODE, intent);
        //注册成功，返回上一个页面
        this.finish();
    }

    //绑定手机
    @Override
    public void thirdLoginBindPhone() {
        if("2".equals(thirdType)) //仅微信登录才提示
        ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), "授权成功，为了你的账号安全，请绑定手机号");
        //绑定手机  跳到注册绑定手机页面
        mVpLoginTab.setCurrentItem(2);
        Button btnRegiste = (Button) findViewById(R.id.btn_register);
        btnRegiste.setText(getString(R.string.label_bind));
        EventBusUtil.post(new EventLoginBean(EventLoginBean.THIRDLOGIN, thirdType, uniqueKey,dataMap));

    }

    //第三方注册结果
    @Override
    public void bindPhoneResult() {}

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {}

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(MyApplication.getInstance().getBaseContext(), info);
    }

    @Override
    public void showNetError() {}

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
