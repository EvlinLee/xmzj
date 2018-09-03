package com.gxtc.huchuan.ui.mine.circle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleEditActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sjr on 2017/5/19.
 * 圈子设置
 * 圈子收费标准新开一个页面
 */

public class CircleSettingActivity extends BaseTitleActivity {

    @BindView(R.id.cb_circle_setting_chat)
    CheckBox cbCircleSettingChat;
    @BindView(R.id.cb_circle_setting_open)
    CheckBox cbCircleSettingOpen;
    @BindView(R.id.ll_circle_change)
    LinearLayout llCircleChange;
    @BindView(R.id.layout_circle_setting_isAudit)
    LinearLayout auditLayout;

    CircleBean bean;

    Subscription subChat;
    Subscription subOpen;
    Subscription subAudit;
    Subscription subReal;

    @BindView(R.id.ll_circle_msg)
    LinearLayout llCircleMsg;
    @BindView(R.id.cb_circle_setting_isAudit)
    CheckBox cbIsAudit;

    @BindView(R.id.cb_circle_setting_meaberaudit)
    CheckBox cbIsmeaberaudit;

    @BindView(R.id.cb_circle_setting_user_jion)
    CheckBox cbIsUserCanJion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_setting);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("circle_data", bean);
                setResult(678, intent);
                CircleSettingActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_circle_setting));
    }

    @Override
    public void initData() {
        bean = (CircleBean) getIntent().getSerializableExtra("circle_data");

        //0允许1不允许私聊
        if (0 == bean.getPrivateChat()) {
            cbCircleSettingChat.setChecked(true);
        } else if (1 == bean.getPrivateChat()) {
            cbCircleSettingChat.setChecked(false);
        }

        //1设置停止招募  0正常
        if (0 == bean.getCanJoin()) {
            cbIsUserCanJion.setChecked(false);
        } else if (1 == bean.getCanJoin()) {
            cbIsUserCanJion.setChecked(true);
        }

        cbCircleSettingChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chat("0");
                } else {
                    chat("1");
                }
            }
        });

        switch (bean.getOnlyReal()){
            case "0":
                cbIsmeaberaudit.setChecked(false);
                break;
            case "1":
                cbIsmeaberaudit.setChecked(true);
                break;
        }

        cbIsUserCanJion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0、正常；1、不可以加入
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    setCbIsUserCanJion("1");
                } else {
                    setCbIsUserCanJion("0");
                }
            }
        });
        cbIsmeaberaudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0、不需要；1、需要
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    setOnlyReal("1");
                } else {
                    setOnlyReal("0");
                }
            }
        });
        //0开放1不开放
        if (0 == bean.getDispark()) {
            cbCircleSettingOpen.setChecked(true);
        } else if (1 == bean.getDispark()) {
            cbCircleSettingOpen.setChecked(false);
        }
        cbCircleSettingOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    open("0");
                } else {
                    open("1");
                }
            }
        });
        //0 普通成员  1 管理员  2 圈主
        if (1 == bean.getMemberType() || 2 == bean.getMemberType()) {
            llCircleChange.setVisibility(View.VISIBLE);
        } else llCircleChange.setVisibility(View.GONE);

        if (0 == bean.getIsFee()) {//免费才显示
            auditLayout.setVisibility(View.VISIBLE);
            //加入是否需要审核
            if ("0".equals(bean.getIsAudit())) {//0、不需要
                cbIsAudit.setChecked(false);
            } else if ("1".equals(bean.getIsAudit())) {
                cbIsAudit.setChecked(true);
            }
            cbIsAudit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isAudit(1);
                    } else {
                        isAudit(0);
                    }
                }
            });
        }else {
            auditLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.ll_circle_change, R.id.ll_circle_msg,R.id.layout_circle_setting_isAudit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_circle_change:
                Intent intent = new Intent(this, MembershipApproachActivity.class);
                intent.putExtra("circle_id", bean.getId());
                intent.putExtra("isFree", bean.getIsFee());
                intent.putExtra("fee", bean.getFee());
                intent.putExtra("pent", bean.getPent());
                startActivity(intent);
                break;
            case R.id.ll_circle_msg:
                GotoUtil.goToActivity(this, CircleEditActivity.class, 0, bean);
                break;
            case R.id.layout_circle_setting_isAudit:
                GotoUtil.goToActivity(this, CircleAuditActivity.class, 100, bean);
                break;
        }
    }


    /**
     * 是否可以私聊
     *
     * @param type
     */
    private void chat(final String type) {
        if (UserManager.getInstance().isLogin()) {
            subChat = CircleApi.getInstance().privateChat(UserManager.getInstance().getToken(), bean.getId(), type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if ("1".equals(type)) {
                                bean.setPrivateChat(1);
                            } else if ("0".equals(type)) {
                                bean.setPrivateChat(0);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(MyApplication.getInstance(), message);
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    //0、不需要；1、需要
    private void setOnlyReal(final String onlyReal) {
        if (UserManager.getInstance().isLogin()) {
            subReal = CircleApi.getInstance().setOnlyReal(UserManager.getInstance().getToken(), bean.getId(), onlyReal)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            switch (onlyReal) {
                                case "0":
                                    cbIsmeaberaudit.setChecked(false);
                                    bean.setOnlyReal("0");
                                    break;
                                case "1":
                                    cbIsmeaberaudit.setChecked(true);
                                    bean.setOnlyReal("1");
                                    break;
                            }

                        }

                        @Override
                        public void onError(String errorCode, String message) {

                            ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(),
                                    message);
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    private void setCbIsUserCanJion(final String clickType) {
        if (UserManager.getInstance().isLogin()) {
            subReal = CircleApi.getInstance().setCbIsUserCanJion(UserManager.getInstance().getToken(), bean.getId(), "4",clickType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            switch (clickType) {
                                case "0":
                                    cbIsUserCanJion.setChecked(false);
                                    bean.setCanJoin(0);
                                    break;
                                case "1":
                                    cbIsUserCanJion.setChecked(true);
                                    bean.setCanJoin(1);
                                    break;
                            }

                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(),
                                    message);
                        }
                    }));
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    /**
     * 是否开放成员
     *
     * @param type
     */
    private void open(final String type) {
        if (UserManager.getInstance().isLogin()) {
            subOpen = CircleApi.getInstance().dispark(UserManager.getInstance().getToken(), bean.getId(), type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if ("1".equals(type)) {

                                bean.setDispark(1);

                            } else if ("0".equals(type)) {

                                bean.setDispark(0);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(),
                                    message);
                        }
                    }));
        } else {
            Intent intent = new Intent(this, CircleManagerActivity.class);
            intent.putExtra("circleId", bean.getId());
            intent.putExtra("isMy", bean.getIsMy());
            startActivity(intent);
        }
    }

    /**
     * 加入是否需要审核
     *
     * @param type 0、不审核；1、审核
     */
    private void isAudit(final int type) {
        if (UserManager.getInstance().isLogin()) {
            subAudit = CircleApi.getInstance().isAuditMember(UserManager.getInstance().getToken(), bean.getId(), type,"")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if (1 == type) {
                                bean.setIsAudit("1");
                            } else if (0 == type) {
                                bean.setIsAudit("0");
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(CircleSettingActivity.this,
                                    message);
                        }
                    }));
        } else {
            Intent intent = new Intent(this, CircleManagerActivity.class);
            intent.putExtra("circleId", bean.getId());
            intent.putExtra("isMy", bean.getIsMy());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && RESULT_OK == resultCode){
            String isAudit  = data.getStringExtra("isAudit");
            bean.setIsAudit(isAudit);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subChat != null && !subChat.isUnsubscribed()) {
            subChat.unsubscribe();
        }
        if (subOpen != null && !subOpen.isUnsubscribed()) {
            subOpen.unsubscribe();
        }

        if (subAudit != null && !subAudit.isUnsubscribed()) {
            subAudit.unsubscribe();
        }
        if (subReal != null && !subReal.isUnsubscribed()) {
            subReal.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("circle_data", bean);
        setResult(678, intent);
        this.finish();
    }


}
