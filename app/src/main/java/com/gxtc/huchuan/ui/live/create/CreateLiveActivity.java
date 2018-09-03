package com.gxtc.huchuan.ui.live.create;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.CreateLiveBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.circle.dynamic.SyncIssueInCircleActivity;
import com.gxtc.huchuan.ui.live.apply.ApplyLecturerActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.DirectSeedingBackgroundActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.MultiRadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Gubr on 2017/3/3.
 * 创建课堂
 */

public class CreateLiveActivity extends BaseTitleActivity implements View.OnClickListener,
        MultiRadioGroup.OnCheckedChangeListener {


    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @BindView(R.id.et_liveroom_name) EditText        mEtLiveroomName;
    @BindView(R.id.tv_type_selector) TextView        mTvTypeSelector;
    @BindView(R.id.et_intro)         EditText        mEtIntro;
    @BindView(R.id.radioGroup)       MultiRadioGroup mRadioGroup;


    private String[] mTitles;

    private int chatTypeId = -1;

    private int property = -1;


    PopPosition mPopPosition;
    private List<LiveHeadTitleBean> mData;
    private List<Integer> mGroupIds = new ArrayList<>();//同步的圈子id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_authentication);
    }


    @Override
    public void initView() {
        getBaseHeadView().showTitle("创建课堂");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.live_navigation_submit, this);
    }

    @Override
    public void initListener() {
        mTvTypeSelector.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        getTypeData();
        if ("0".equals(UserManager.getInstance().getIsAnchor())) {
            showDialog();
        } else if ("2".equals(UserManager.getInstance().getIsAnchor())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setMessage("您现在还不是讲师，您提交的申请在审核中！");
            dialog.setCancelable(false);
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreateLiveActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }

    }

    private AlertDialog mDialog;

    private void showDialog() {
        mDialog = DialogUtil.createDialog(this, "温馨提示", "您还不是主播不能创建课堂，请申请讲师！", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        CreateLiveActivity.this.finish();
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        GotoUtil.goToActivity(CreateLiveActivity.this, ApplyLecturerActivity.class);
                        mDialog.dismiss();
                    }

                });
        mDialog.show();

    }

    private void getTypeData() {
        mCompositeSubscription.add(
                LiveApi.getInstance().getChatType().subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<List<LiveHeadTitleBean>>>(
                                new ApiCallBack<List<LiveHeadTitleBean>>() {


                                    @Override
                                    public void onSuccess(List<LiveHeadTitleBean> data) {
                                        if (data != null) {
                                            mData = data;
                                            mTitles = new String[data.size()];
                                            for (int i = 0; i < data.size(); i++) {
                                                mTitles[i] = data.get(i).getTypeName();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {

                                    }
                                })));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.tv_type_selector:
                showPositionPop();
                break;

            case R.id.HeadRightImageButton:
                submit();
                break;
        }

    }

    private void showSysDialog(final CircleFileBean bean) {
        DialogUtil.showSysDialog(this, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_issue_tongbu:
                        Intent intent = new Intent(CreateLiveActivity.this, SyncIssueInCircleActivity.class);
                        intent.putExtra("default", "-1");
                        intent.putExtra("isClass", true);
                        CreateLiveActivity.this.startActivityForResult(intent, 666);
                        break;

                    case R.id.tv_cancel:
                        break;

                    case R.id.tv_sure:
                        SysGroup(bean);
                        break;
                }

            }
        });
    }

    //同步文章到圈子 同步类型1、文章；2、课堂；3、文件
    private void SysGroup(CircleFileBean bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if (mGroupIds != null && mGroupIds.size() > 0) {
            String groupids = mGroupIds.toString().substring(1, mGroupIds.toString().length() - 1);
            map.put("groupIds", groupids);
        }
        map.put("linkId", bean.getId() + "");
        map.put("type", "3");

        Subscription sub =
            MineApi.getInstance()
                   .appendToGroup(map)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(getBaseLoadingView() == null)    return;
                            ToastUtil.showShort(CreateLiveActivity.this, "同步成功");
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if(getBaseLoadingView() == null)    return;
                            ToastUtil.showShort(CreateLiveActivity.this, message);
                        }
                    }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void submit() {
        String liveRoomName = mEtLiveroomName.getText().toString();
        String token;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        } else {
            // 这里到时候  改成 跳到 登录界面
            ToastUtil.showShort(this, "请先登录");
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        if (TextUtils.isEmpty(liveRoomName)) {
            ToastUtil.showShort(this, "请输入课堂名字");
            return;
        }
        if (chatTypeId < 0) {
            ToastUtil.showShort(this, "请选择类型");
            return;
        }
        if (property < 0) {
            ToastUtil.showShort(this, "请选择认证类型");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("chatTypeId", String.valueOf(chatTypeId));
        map.put("property", String.valueOf(property));
        map.put("roomname", liveRoomName);
        map.put("introduce", mEtIntro.getText().toString());


        Log.d("CreateLiveActivity", map.toString());
        mCompositeSubscription.add(
                LiveApi.getInstance().saveChatRoom(map).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<CreateLiveBean>>(
                                new ApiCallBack<CreateLiveBean>() {
                                    @Override
                                    public void onSuccess(CreateLiveBean data) {
                                        if(getBaseLoadingView() == null)    return;

                                        //这里到时候应该是跳到开好的课堂。
                                        EventBusUtil.post(new EventEditInfoBean(
                                                EventEditInfoBean.CREATELIVE));
                                        GotoUtil.goToActivity(CreateLiveActivity.this,
                                                DirectSeedingBackgroundActivity.class);
                                        User user = UserManager.getInstance().getUser();
                                        user.setChatRoomId(data.getId());
                                        UserManager.getInstance().updataUser(user);

                                        finish();
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        LoginErrorCodeUtil.showHaveTokenError(CreateLiveActivity.this, errorCode, message);
                                    }
                                })));


    }


    /**
     * 选择课堂类型
     */
    private void showPositionPop() {
        if (mPopPosition == null) {
            if (mTitles == null) {
                ToastUtil.showShort(this, "数据加载出错 请稍后再试");
                getTypeData();
                return;
            }
            mPopPosition = new PopPosition(this, R.layout.pop_ts_position);

            mPopPosition.setData(mTitles);
            mPopPosition.setTitle("选择分类");
            mPopPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    mTvTypeSelector.setText(mTitles[newVal]);
                    chatTypeId = newVal + 1;
                }
            });
        }
        mPopPosition.showPopOnRootView(this);
    }


    @Override
    protected void onDestroy() {
        if (mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }


    @Override
    public void onCheckedChanged(MultiRadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_lecturer://讲师
                property = 0;
                break;
            case R.id.rb_institution://机构
                property = 1;
                break;
            case R.id.rb_audience://听众
                property = 2;
                break;
        }
    }
}
