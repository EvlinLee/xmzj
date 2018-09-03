package com.gxtc.huchuan.ui;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gxtc.commlibrary.base.BaseActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LightStatusBarUtils;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.TabBean;
import com.gxtc.huchuan.bean.UpdataBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventGuideBean;
import com.gxtc.huchuan.bean.event.EventJPushMessgeBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventMainClickBean;
import com.gxtc.huchuan.bean.event.EventMenuBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.bean.event.EventUnReadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.UpdataDialog;
import com.gxtc.huchuan.helper.GuideHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.im.manager.MessageManager;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.pop.PopMain;
import com.gxtc.huchuan.receiver.BootCompletedReceiver;
import com.gxtc.huchuan.receiver.JobSchedulerManager;
import com.gxtc.huchuan.ui.circle.home.MainCircleHomeFragment;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;
import com.gxtc.huchuan.ui.deal.leftMenu.WxLoginActivity;
import com.gxtc.huchuan.ui.live.LiveChanelFragment;
import com.gxtc.huchuan.ui.mall.NewMallFragment;
import com.gxtc.huchuan.ui.message.MessageFragment;
import com.gxtc.huchuan.ui.mine.MineFragmentNew;
import com.gxtc.huchuan.ui.mine.account.UsableAccountActivity;
import com.gxtc.huchuan.ui.news.NewsFragment;
import com.gxtc.huchuan.ui.news.ShareMakeMoneyActivity;
import com.gxtc.huchuan.ui.news.VideoNewsActivity;
import com.gxtc.huchuan.ui.resource.ResourceFragment;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.MobileInfoUtils;
import com.gxtc.huchuan.utils.PermissionUtils;
import com.gxtc.huchuan.utils.StatisticsHandler;
import com.gxtc.huchuan.utils.SystemTools;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import io.rong.eventbus.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;
import static com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity.REQUEST_CODE_AVATAR;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_DEAL;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_DISTRIBUTE;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_HOT_CLASS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_MAll;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_NEW_VIDEO;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_PACKGE;

/**
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, MainContract.View {

    private final int CIRCLE_INDEX = 2;         //圈子的位置总是改变，用一个统一的变量吧

    @BindView(R.id.fl_fragment)
    FrameLayout flFragment;
    @BindView(R.id.common_tab_layout)
    CommonTabLayout commonTabLayout;
    @BindView(R.id.drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.left_layout)
    LinearLayout leftLayout;
    @BindView(R.id.btn_login_wx)
    View btnLoginWx;


    //@BindView(R.id.btn_main)          RotateImageButton btnMain;


    //private DealTabFragment dFragment;        //暂时屏蔽；流量交易
    private NewsFragment       nFragment;
//    private MainNewsFragment nFragment;
    private MineFragmentNew mFragment;
    private MainCircleHomeFragment circleHomeFragment;    //圈子首页第三版
    private MessageFragment msgFragment;
    private ResourceFragment rFragment;
    private boolean firstLaunch;

    public static final String STATISTICS_STATUS = "StatisticsHandler";
    public static final String STATISTICS = "1";
    public static final String STATISTICS_EMTPT = "";
    /**
     * @see PopMain 中的fragment
     */
    private LiveChanelFragment liveFragment;
    private DealFragment dealFragment;
    private NewMallFragment mNewMallFragment;

    private long exitTime = 0;
    private int index = 0;

    private String[] mTitles = {"首页", "消息", "圈子", "资源", "我的"};
    private int[] mIconNormalIds = {R.drawable.tabbar_news,
            R.drawable.tabbar_live_liaotian,
            R.drawable.tabbar_quanzi,
            R.drawable.tabbar_discover,
            R.drawable.tabbar_wode};
    private int[] mIconPressIds = {R.drawable.tabbar_news_selected,
            R.drawable.tabbar_live_liaotian_selected,
            R.drawable.tabbar_quanzi_selected,
            R.drawable.tabbar_discover_selected,
            R.drawable.tabbar_wode_selected};

    private ArrayList<CustomTabEntity> tabEntitys;

    private PopMain popMain;

    private MainContract.Presenter mPresenter;

    @Override
    public boolean isLightStatusBar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LightStatusBarUtils.setLightStatusBar(this, isLightStatusBar());

        firstLaunch = SpUtil.getBoolean(MainActivity.this, NewsFragment.FIRST_GUIDE, true);

        nFragment = new NewsFragment();
//        nFragment = new MainNewsFragment();
        mFragment = new MineFragmentNew();
        circleHomeFragment = new MainCircleHomeFragment();
        msgFragment = new MessageFragment();
        rFragment = new ResourceFragment();

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        EventBusUtil.register(this);
        //OnepxReceiver.register1pxReceiver(this);     //保证应用锁屏之后的存活率
        connectJiGuang();

        if (savedInstanceState != null) {
            resumeData(savedInstanceState);
        } else {
            commonTabLayout.setCurrentTab(0);
            switchFragment(nFragment, NewsFragment.class.getSimpleName(), R.id.fl_fragment);
        }
    }


    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        mPresenter = new MainPresenter(this);

        tabEntitys = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            tabEntitys.add(new TabBean(mTitles[i], mIconPressIds[i], mIconNormalIds[i]));
        }
        commonTabLayout.setTabData(tabEntitys);
        /*popMain = new PopMain(this, R.layout.pop_main);
        popMain.setTransparentBg();*/

        /*if(!SpUtil.getBoolean(this, BootCompletedReceiver.KEY_BOOT, false)){
            requestAutoStartPermissions();
        }*/

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            JobSchedulerManager.INSTANCE.startJobScheduler(this);
        }

        //圈子动态未读
        getDynamicUnread();

        checkUpdata();
        initPermission();
    }


    private int lastPosition = 0;

    @Override
    public void initListener() {
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                index = position;
                if (firstLaunch) {
                    commonTabLayout.setCurrentTab(0);
                    firstLaunch = SpUtil.getBoolean(MainActivity.this, NewsFragment.FIRST_GUIDE, true);
                } else {
                    clickTab(position);
                }
            }

            @Override
            public void onTabReselect(int position) {
                lastPosition = position;
                if (ClickUtil.isFastClick() && lastPosition == index) {
                    //双击定位到未读消息那一行
                    if (position == 1) {
                        EventBus.getDefault().post(new EventClickBean("location", null));
                        return;
                    }
                    EventBusUtil.post(new EventScorllTopBean(position));
                }
            }
        });
    }


    @OnClick({R.id.btn_login_wx/*,R.id.btn_main*/})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //中间的tab
            /*case R.id.btn_main:
                popMain.setBlurView(getWindow().getDecorView());
                popMain.showPopOnRootView(this);
                break;*/

            //添加微信公众号
            case R.id.btn_login_wx:
                GotoUtil.goToActivity(this, WxLoginActivity.class);
                break;
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSwipeBackEnable(false);      //首页activity 不需要滑动返回
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AVATAR) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }

        //圈子动态发送成功
        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            circleHomeFragment.onActivityResult(requestCode, resultCode, data);
        }

        //圈子动态转发
        if (requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK) {
            circleHomeFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 101) {
            rFragment.onActivityResult(requestCode, resultCode, data);
        }

        //分享视频
        if (requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK) {
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
        //设置悬浮窗权限回调
        if (requestCode == PermissionUtils.PERMISSION_SETTING_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    SpUtil.putBoolean(this, "Overlays", true);
                    ToastUtil.showShort(MyApplication.getInstance(), "设置成功");
                } else {
                    ToastUtil.showShort(MyApplication.getInstance(), "设置失败");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (JZVideoPlayer.backPress()) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void showUnreadMsg(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 0) {
                    commonTabLayout.hideMsg(1);
                } else {
                    commonTabLayout.showMsg(1, count);
                    commonTabLayout.setMsgMargin(1, -10, 5);
                }
            }
        });
    }


    private AlertDialog mAlertDialog;

    @Override
    public void showUpdata(final UpdataBean updataInfo) {
        if (!firstLaunch) {
            String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                    new BaseActivity.PermissionsResultListener() {
                        @Override
                        public void onPermissionGranted() {
                            UpdataDialog mUpdataDialog = new UpdataDialog();
                            mUpdataDialog.setUpdataInfo(updataInfo);
                            mUpdataDialog.show(getSupportFragmentManager(), UpdataDialog.class.getSimpleName());
                            mUpdataDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_cancel && updataInfo.isForce() == 1) {
                                        finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied() {
                            mAlertDialog = DialogUtil.showDeportDialog(MainActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.tv_dialog_confirm) {
                                                JumpPermissionManagement.GoToSetting(MainActivity.this);
                                            }
                                            mAlertDialog.dismiss();
                                        }
                                    });
                        }
                    });
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", index);
    }

    /**
     * 以前的popmain菜单页面已经弃用  暂时不要这段代码
     */
    @Subscribe
    public void onEvent(EventMainClickBean bean) {
        Map<String, String> map = new HashMap<>();

        //第一页的tab
        if (bean.outPosition == 0) {
            switch (bean.insidePosition) {
                //课程
                case 0:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_HOT_CLASS, true);
                    if (liveFragment == null) {
                        liveFragment = (LiveChanelFragment) getFragment(LiveChanelFragment.class);
                    }
                    switchFragment(liveFragment, LiveChanelFragment.class.getSimpleName(), R.id.fl_fragment);
                    break;

                //商城
                case 1:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_MAll, true);
                    if (mNewMallFragment == null) {
                        mNewMallFragment = (NewMallFragment) getFragment(NewMallFragment.class);
                        Bundle bundle = new Bundle();
                        mNewMallFragment.setArguments(bundle);
                    }
                    switchFragment(mNewMallFragment, NewMallFragment.class.getSimpleName(), R.id.fl_fragment);
                    break;

                //交易
                case 2:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_DEAL, true);
                    if (dealFragment == null) {
                        dealFragment = (DealFragment) getFragment(DealFragment.class);
                        Bundle bundle = new Bundle();
                        dealFragment.setArguments(bundle);
                    }
                    switchFragment(dealFragment, DealFragment.class.getSimpleName(), R.id.fl_fragment);
                    break;

                //视频
                case 3:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_NEW_VIDEO, true);
                    GotoUtil.goToActivity(this, VideoNewsActivity.class);
                    break;

                //分销列表
                case 4:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_DISTRIBUTE, true);
                    GotoUtil.goToActivity(this, ShareMakeMoneyActivity.class);
                    break;

                //我的钱包
                case 5:
                    StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_PACKGE, true);
                    GotoUtil.goToActivity(this, UsableAccountActivity.class);
                    break;
            }
        }

        /*Observable.timer(500, TimeUnit.MILLISECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<Long>() {
                      @Override
                      public void call(Long aLong) {
                          popMain.closePop();
                      }
                  });*/
    }

    //刷新未读消息数量  刷新地方：会话界面退出、删除会话记录、清除聊天记录
    @Subscribe
    public void onEvent(EventUnReadBean bean) {
        mPresenter.getConversationList();
    }

    //刷新头像 用户名
    @Subscribe
    public void onEvent(EventLoginBean bean) {
        //退出登录
        if (bean.status == EventLoginBean.EXIT || bean.status == EventLoginBean.TOKEN_OVERDUCE) {
            commonTabLayout.hideMsg(1);
            commonTabLayout.hideMsg(CIRCLE_INDEX);
        }

        //登录
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.THIRDLOGIN) {
            mPresenter.connectRongIm();
            getDynamicUnread();
        }
    }

    @Subscribe
    public void onEventMenu(EventMenuBean bean) {
        if (drawerLayout.isDrawerOpen(leftLayout)) {
            drawerLayout.closeDrawer(leftLayout);
        } else {
            drawerLayout.openDrawer(leftLayout);
        }
    }

    @Subscribe
    public void onEvent(EventGuideBean bean) {
        if (bean.index == 3) {
            ViewGroup layout = (ViewGroup) commonTabLayout.getChildAt(0);
            new GuideHelper(this).show(GuideHelper.TYPE_THREE, layout.getChildAt(1),
                    layout.getChildAt(2), layout.getChildAt(3));
        }
    }


    @Subscribe(sticky = true)
    public void onEvent(EventJPushMessgeBean bean) {
        if (UserManager.getInstance().isLogin()) {
            if (!TextUtils.isEmpty(bean.unReadNum) && !bean.unReadNum.equals("0")) {
                commonTabLayout.showMsg(CIRCLE_INDEX, Integer.parseInt(bean.unReadNum));
                commonTabLayout.setMsgMargin(CIRCLE_INDEX, -10, 5);
            } else {
                commonTabLayout.hideMsg(CIRCLE_INDEX);
            }
        } else {
            commonTabLayout.hideMsg(CIRCLE_INDEX);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        StatisticsHandler.Companion.getInstant().destroy();
        //OnepxReceiver.unregister1pxReceiver(this);
        mPresenter.destroy();
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (liveFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(liveFragment).commitAllowingStateLoss();
                liveFragment = null;
            }

            if (dealFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(dealFragment).commitAllowingStateLoss();
                dealFragment = null;
            }

            if (mNewMallFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(mNewMallFragment).commitAllowingStateLoss();
                mNewMallFragment = null;
            }

            MessageManager.getInstance().release();
        }
    }

    //恢复保存的数据
    private void resumeData(Bundle savedInstanceState) {
        LogUtil.i("resumeData  ===========");
        index = savedInstanceState.getInt("index");
        commonTabLayout.setCurrentTab(index);
    }

    //连接极光
    private void connectJiGuang() {
        String userCode = UserManager.getInstance().getUserCode();
        LogUtil.i("userCode   : " + userCode);
        if (!TextUtils.isEmpty(userCode)) {
            //极光推送
            JPushUtil.getInstance().setJPushAlias(this, userCode);
        }
    }

    //检查是否有悬浮窗权限  参考这篇文章https://blog.csdn.net/cankingapp/article/details/49686853  在onActivityResult方法里回调结果
    private void initPermission() {
            boolean overlays = SpUtil.getBoolean(this, "Overlays", false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !overlays) {
                boolean isAlert = Settings.canDrawOverlays(this);
                if (!isAlert) {
                    //没有悬浮窗权限,跳转申请
                    mAlertDialog = DialogUtil.showDeportDialog(this, false, null, "应用需要开启悬浮窗权限,是否开启?",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        //有些机型找不到目标界面，这里做一下容错处理
                                        try{
                                            PermissionUtils.checkSettingAlertPermission(MainActivity.this, PermissionUtils.PERMISSION_SETTING_REQ_CODE);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                }
            }

    }


    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            //ToastUtil.showShort(getApplicationContext(), "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            /*RongIM.getInstance().disconnect();
            finish();*/
            SystemTools.goHome(this);
        }
    }


    private void clickTab(int position) {
        switch (position) {
            //资讯
            case 0:
                switchFragment(nFragment, NewsFragment.class.getSimpleName(), R.id.fl_fragment);
                break;

            //消息
            case 1:
                switchFragment(msgFragment, MessageFragment.class.getSimpleName(), R.id.fl_fragment);
                break;

            //圈子
            case CIRCLE_INDEX:
                switchFragment(circleHomeFragment, MainCircleHomeFragment.class.getSimpleName(), R.id.fl_fragment);
                break;

            //资源
            case 3:
                switchFragment(rFragment, ResourceFragment.class.getSimpleName(), R.id.fl_fragment);
                break;

            //我的
            case 4:
                switchFragment(mFragment, MineFragmentNew.class.getSimpleName(), R.id.fl_fragment);
                Log.i("token", "token:" + UserManager.getInstance().getToken());
                break;
        }
    }


    private void checkUpdata() {
        mPresenter.checkUpdata();
    }


    private void getDynamicUnread() {
        Observable.just(UserManager.getInstance().getUserCode())
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, EventJPushMessgeBean>() {
                    @Override
                    public EventJPushMessgeBean call(String s) {
                        return (EventJPushMessgeBean) ACache.get(MainActivity.this).getAsObject(s + EventJPushMessgeBean.class.getSimpleName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<EventJPushMessgeBean>() {
                    @Override
                    public void call(EventJPushMessgeBean bean) {
                        if (bean != null && !TextUtils.isEmpty(bean.unReadNum) && !bean.unReadNum.equals("0")) {
                            commonTabLayout.showMsg(CIRCLE_INDEX, Integer.parseInt(bean.unReadNum));
                            commonTabLayout.setMsgMargin(CIRCLE_INDEX, -10, 5);
                        } else {
                            commonTabLayout.hideMsg(CIRCLE_INDEX);
                        }
                    }
                });
    }


    private BaseTitleFragment getFragment(Class<? extends BaseTitleFragment> clazz) {
        try {
            return clazz.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private AlertDialog autoDialog;

    //弹窗获取开机自启动权限
    private void requestAutoStartPermissions() {
        autoDialog = DialogUtil.showInputDialog(this, false, "", "应用需要获取自启动权限是否现在去设置？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileInfoUtils.jumpStartInterface(MainActivity.this);
                SpUtil.putBoolean(MainActivity.this, BootCompletedReceiver.KEY_BOOT, true);
                autoDialog.dismiss();
            }
        });
    }
}
