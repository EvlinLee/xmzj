package com.gxtc.huchuan.ui.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.shield.ShieldListActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 隐私设置
 * Created by zzg on 2017/7/3.
 */
//privacy
public class PrivacyActivity extends BaseTitleActivity {

    @BindView(R.id.tv_blacklist)                  TextView     tvBlackList;
    @BindView(R.id.tv_not_allow_ses_dynamic)      TextView     tvNotAllowSeeDynimic;
    @BindView(R.id.tv_not_see_somone_dynamic)     TextView     tvNotseeSommonDynimic;
    @BindView(R.id.switch_messsge_status)         SwitchCompat switchMessgeStatus;
    @BindView(R.id.switch_messsge_comment_status) SwitchCompat switchMessgeCommendStatus;


    Subscription sub;
    boolean isFirst = true;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showTitle(getString(R.string.title_privacy_setting));
        getUserInfo(UserManager.getInstance().getToken());
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //仅好友发信息
        switchMessgeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat mCheckBox = (SwitchCompat) v;
                if (mCheckBox.isChecked()) {
                    setFriend("0", "1");
                } else {
                    setFriend("0", "0");
                }
            }
        });
        //仅好友评论
        switchMessgeCommendStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat mCheckBox = (SwitchCompat) v;
                if (mCheckBox.isChecked()) {
                    setFriend("1", "1");
                } else {
                    setFriend("1", "0");
                }
            }
        });

    }

    private void getUserInfo(String token) {
        mCompositeSubscription.add(
                MineApi.getInstance().getUserInfo(token).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<User>>(new ApiCallBack<User>() {
                            @Override
                            public void onSuccess(User data) {
                                if (data != null) {
                                    //仅好友发信息
                                    if (data.getFriendChat().equals("1")) {
                                        switchMessgeStatus.setChecked(true);
                                    } else {
                                        switchMessgeStatus.setChecked(false);
                                    }
                                    //仅好友评论
                                    if (data.getFriendComment().equals("1")) {
                                        switchMessgeCommendStatus.setChecked(true);
                                    } else {
                                        switchMessgeCommendStatus.setChecked(false);
                                    }
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(PrivacyActivity.this,
                                        errorCode, message);
                            }
                        })));
    }

    private void setFriend(String type, String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", type);           //0、仅好友可聊天；1、仅好友可评论
        map.put("value", value);         //0、全部可操作；1、仅好友可操作
        sub = MineApi.getInstance().setFriend(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {}

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(PrivacyActivity.this, errorCode,
                                message);
                    }
                }));

    }

    @OnClick({R.id.tv_blacklist,
            R.id.tv_not_allow_ses_dynamic,
            R.id.tv_not_see_somone_dynamic,
            R.id.tv_not_see_circle_dynamic,
            R.id.tv_not_see_article})
    public void onClick(View view) {
        switch (view.getId()) {
            //黑名单
            case R.id.tv_blacklist:
                ShieldListActivity.gotoActivity(this, "2", "黑名单");
                break;

            //不给谁看动态
            case R.id.tv_not_allow_ses_dynamic:
                ShieldListActivity.gotoActivity(this, "1", "不给他(她)看动态");
                break;

            //不看谁的动态
            case R.id.tv_not_see_somone_dynamic:
                ShieldListActivity.gotoActivity(this, "0", "不看他(她)的动态");
                break;

            //不看他的文章
            case R.id.tv_not_see_article:
                ShieldListActivity.gotoActivity(this, "3", "不看他(她)的文章");
                break;

            //屏蔽圈子的动态
            case R.id.tv_not_see_circle_dynamic:
                ShieldListActivity.gotoActivity(this, "4", "屏蔽圈子的动态");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
