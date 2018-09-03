package com.gxtc.huchuan.ui.mine.setting;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.KotlinGlideCacheUtil;
import com.gxtc.commlibrary.utils.LocaUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.UpdataBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventUnReadBean;
import com.gxtc.huchuan.bean.event.EventUpdataBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.UpdataDialog;
import com.gxtc.huchuan.helper.BuglyHelper;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MallApi;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.message.MessageFragment;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.setting.accountSafe.AccountSafeActivity;
import com.gxtc.huchuan.ui.mine.setting.dustbin.DustbinListActivity;
import com.gxtc.huchuan.ui.mine.shield.ShieldListActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.GoToActivityIfLoginUtil;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.PermissionUtils;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.SystemTools;
import com.gxtc.huchuan.utils.UMShareUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 设置
 * Created by ALing on 2017/3/6.
 */
public class SettingActivity extends BaseTitleActivity {

    public static boolean sound = true;

    public static String SP_SOUND() {
        return "sound" + UserManager.getInstance().getUserCode();
    }

    //    @BindView(R.id.tv_aboout_us)
//    TextView tvAbooutUs;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.tv_kefu)
    TextView tvKeFu;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.tv_shield)
    TextView tvShield;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_dustbin)
    TextView tvDustbin;
    @BindView(R.id.tv_ys)
    TextView tvYs;
    @BindView(R.id.clear_cache)
    RelativeLayout clearCache;
    @BindView(R.id.clear_conversation_record)
    TextView clearConversationRecord;
    @BindView(R.id.tv_cache_size)
    TextView tvCacheSize;
    @BindView(R.id.switch_sound)
    SwitchCompat switchSound;
    @BindView(R.id.dragView_setting)
    View messgaeSettingRedDot;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        super.initView();
        if (!UserManager.getInstance().isLogin()) {
            tvExit.setText("立即登录");
        } else {
            tvExit.setText("退出");
        }

        String version = SystemTools.getAppVersionName(this);
        tvVersion.setText("v" + version);

        sound = SpUtil.getBoolean(this, SP_SOUND(), true);
        switchSound.setChecked(sound);

        getCacheSize();

    }


    /**
     * 计算缓存大小
     * glide 的图片缓存
     * xmzj的本地文件缓存
     */
    private void getCacheSize() {
        Subscription sub =

                Observable.create(new Observable.OnSubscribe<Long>() {
                    @Override
                    public void call(Subscriber<? super Long> subscriber) {
                        long glideCache = LocaUtil.getInstance(MyApplication.getInstance().getApplicationContext()).getCacheSize();

                        long customCache = LocaUtil.getDirSize(FileStorage.getAppPath());
                        subscriber.onNext(glideCache + customCache);
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long size) {
                                if (0 == size) {
                                    tvCacheSize.setText("");
                                } else {
                                    tvCacheSize.setText(LocaUtil.formatFileSize(size));
                                }
                            }
                        });

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showTitle(getString(R.string.title_personal_setting));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sound = isChecked;
                SpUtil.putBoolean(getApplicationContext(), SP_SOUND(), sound);
                JPushUtil.setSoundAndVibrate(SettingActivity.this, sound, !sound);
            }
        });
    }

    @OnClick({R.id.tv_exit, R.id.tv_feedback, R.id.tv_kefu, R.id.tv_share, R.id.tv_shield, R.id.btn_check_version, R.id.tv_dustbin, R.id.tv_ys,
            R.id.clear_cache, R.id.clear_conversation_record, R.id.tv_account_safe, R.id.tv_messge_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_feedback:
                GotoUtil.goToActivity(this, FeedBackActivity.class);
                break;

            //分享应用
            case R.id.tv_share:
                UMShareUtils mUMShareUtils = new UMShareUtils(this);
                mUMShareUtils.shareDefault(R.mipmap.person_icon_head_share, "新媒之家",
                        "一个汇聚百万新媒体大咖的信息交流，资源交换平台",
                        Constant.Url.SHARE_MARKET_URL);
                mUMShareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                    @Override
                    public void onItemClick(int flag) {
                        switch (flag) {
                            case 0:
                                ClipboardManager cmb = (ClipboardManager) SettingActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText( Constant.Url.SHARE_MARKET_URL);
                                ToastUtil.showShort(SettingActivity.this, "已复制");
                                break;
                        }
                    }
                });
                break;

            case R.id.tv_kefu:
                if (UserManager.getInstance().isLogin())
                    getKefu();
                else
                    GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
                break;

            case R.id.tv_exit:
                if (UserManager.getInstance().isLogin()) {
                    mAlertDialog = DialogUtil.showInputDialog(this, false, null, "确定退出",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogOut();
                                }
                            });
                } else {
                    GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
                }
                break;

            //屏蔽列表
            case R.id.tv_shield:
                GoToActivityIfLoginUtil.goToActivity(this, ShieldListActivity.class);
                break;

            //账号与安全
            case R.id.tv_account_safe:
                goToActivity(AccountSafeActivity.class);
                break;

            //隐私设置
            case R.id.tv_ys:
                goToActivity(PrivacyActivity.class);
                break;

            //检查版本更新
            case R.id.btn_check_version:
                initPermission();
                break;

            case R.id.tv_messge_set:
                goToActivity(MessageSettingActivity.class);
                break;
            case R.id.tv_dustbin:
                goToActivity(DustbinListActivity.class);
                break;
            case R.id.clear_cache:
                mAlertDialog = DialogUtil.showDeportDialog(this, false, null, "确定要清除缓存",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getId() == R.id.tv_dialog_confirm) {
                                    clearCache();
                                }
                                mAlertDialog.dismiss();
                            }
                        });
                break;
            //清楚聊天记录
            case R.id.clear_conversation_record:
                if (UserManager.getInstance().isLogin()) {
                    mAlertDialog = DialogUtil.showDeportDialog(this, false, null, "确定要清除聊天记录",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        clearConversationCache();
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, Constant.requestCode.NEWS_AUTHOR);
                }

                break;

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
                                    PermissionUtils.checkSettingAlertPermission(SettingActivity.this, PermissionUtils.PERMISSION_SETTING_REQ_CODE);


                                }
                                mAlertDialog.dismiss();
                            }
                        });
            }
        } else {
            checkUpdata();
        }
    }

    private void checkUpdata() {

        getBaseLoadingView().showLoading();
        messgaeSettingRedDot.setVisibility(View.INVISIBLE);

        final int code = SystemTools.getAppVersionCode(MyApplication.getInstance());
        Subscription sub = AllApi.getInstance()
                .getAppVersion(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<UpdataBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (getBaseLoadingView() != null && data != null && data instanceof UpdataBean) {
                            getBaseLoadingView().hideLoading();
                            UpdataBean bean = (UpdataBean) data;
                            if (code < bean.getVersionCode()) {
                                showUpdataDialog(bean);
                            } else {
                                ToastUtil.showShort(SettingActivity.this, "当前已是最新版本");
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getBaseLoadingView() != null) {
                            getBaseLoadingView().hideLoading();
                            ToastUtil.showShort(SettingActivity.this, message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private UpdataDialog mUpdataDialog;

    private void showUpdataDialog(UpdataBean bean) {
        mUpdataDialog = new UpdataDialog();
        mUpdataDialog.setUpdataInfo(bean);
        mUpdataDialog.show(getSupportFragmentManager(), UpdataDialog.class.getSimpleName());
    }


    private void getKefu() {
        //0：全局客服1：商城客服 2：交易客服 3：app客服  rand  0：列表 1：随机
        Subscription sub = MallApi.getInstance().getIMServiceList(MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_APP(), MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_RAND())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new ApiObserver<ApiResponseBean<ArrayList<CoustomMerBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data == null) return;
                        ArrayList<CoustomMerBean> datas = (ArrayList<CoustomMerBean>) data;
                        if (datas.size() > 0)
                            RongIM.getInstance().startPrivateChat(SettingActivity.this, datas.get(0).getUserCode(), datas.get(0).getName());
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if ("110".equals(errorCode)) {
                            ToastUtil.showShort(MyApplication.getInstance(), "当前客服不在");
                        } else if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(errorCode)) {
                            mAlertDialog = DialogUtil.showDeportDialog(SettingActivity.this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        ReLoginUtil.ReloginTodo(SettingActivity.this);
                                    }
                                    mAlertDialog.dismiss();
                                }
                            });
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, toClass);
        } else {
            GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                    Constant.requestCode.NEWS_AUTHOR);
        }
    }

    //清除缓存
    private void clearCache() {
        Observable.create(new Observable.OnSubscribe<Context>() {
            @Override
            public void call(Subscriber<? super Context> subscriber) {
                subscriber.onNext(MyApplication.getInstance().getApplicationContext());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<Context>() {
                    @Override
                    public void call(Context context) {
                        FileUtil.deleteDir(FileStorage.getAppPath());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Context>() {

                    @Override
                    public void onCompleted() {
                        tvCacheSize.setText("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(),
                                e.getMessage());
                    }

                    @Override
                    public void onNext(Context mContext) {
                        KotlinGlideCacheUtil.Companion.getInstance().clearImageAllCache(mContext);
                        LocaUtil.getInstance(mContext).clearAppCache();
                    }
                });
    }

    //清楚聊天记录
    private void clearConversationCache() {
        List<Conversation> conversations = RongIM.getInstance().getRongIMClient().getConversationList();
        if (conversations == null || conversations.size() == 0) return;

        Observable.from(conversations).flatMap(new Func1<Conversation, Observable<Conversation>>() {
            @Override
            public Observable<Conversation> call(Conversation conversations) {
                return Observable.just(conversations);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new Observer<Conversation>() {
                    @Override
                    public void onCompleted() {
                        ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), "清除成功");
                        EventBusUtil.post(new EventUnReadBean());
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), "清除失败");
                    }

                    @Override
                    public void onNext(final Conversation conversation) {
                        RongIM.getInstance().getRongIMClient().clearMessages(
                                conversation.getConversationType(), conversation.getTargetId(),
                                new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        //清除未读信息
                                        RongIM.getInstance().clearMessagesUnreadStatus(
                                                conversation.getConversationType(),
                                                conversation.getTargetId());
                                        //清楚会话列表
                                        RongIM.getInstance().getRongIMClient().removeConversation(
                                                conversation.getConversationType(),
                                                conversation.getTargetId());
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        ToastUtil.showShort(
                                                MyApplication.getInstance().getApplicationContext(),
                                                "清除失败");
                                    }
                                });
                    }
                });
    }


    private void LogOut() {
        tvExit.setText("立即登录");
        mAlertDialog.dismiss();

        SpUtil.remove(this, MessageFragment.KEY_NUM());
        UserManager.getInstance().deleteUser();
        EventBusUtil.post(new EventLoginBean(EventLoginBean.EXIT));
        RongIM.getInstance().logout();
        //退出登录收不到推送
        JPushUtil.getInstance().closeJPush(SettingActivity.this);
        GotoUtil.goToActivity(SettingActivity.this, LoginAndRegisteActivity.class);
        finish();
    }

    private void gotoWebView(String url, String title) {
        Intent intent = new Intent(this, CommonWebViewActivity.class);
        intent.putExtra("web_url", url);
        intent.putExtra("web_title", title);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || bean.status == EventLoginBean.THIRDLOGIN) {
            tvExit.setText("退出");
            finish();

        } else if (bean.status == EventLoginBean.EXIT || bean.status == EventLoginBean.TOKEN_OVERDUCE) {      //退出登录
            tvExit.setText("立即登录");
        }
    }

    //版本更新
    @Subscribe(sticky = true)
    public void onEvent(EventUpdataBean bean) {
        messgaeSettingRedDot.setVisibility(View.VISIBLE);
        EventBus.getDefault().removeStickyEvent(bean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置悬浮窗权限回调
        if (requestCode == PermissionUtils.PERMISSION_SETTING_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    SpUtil.putBoolean(this, "Overlays", true);
                    ToastUtil.showShort(MyApplication.getInstance(), "设置成功");
                    checkUpdata();
                } else {
                    ToastUtil.showShort(MyApplication.getInstance(), "设置失败");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
    }

}
