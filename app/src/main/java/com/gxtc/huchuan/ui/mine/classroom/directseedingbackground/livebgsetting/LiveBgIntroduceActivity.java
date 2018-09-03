package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.event.EventLiveManager;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Describe: 课堂管理 > 课堂简介
 * Created by ALing on 2017/3/23 .
 */

public class LiveBgIntroduceActivity extends BaseTitleActivity implements View.OnClickListener,LiveBgSettingContract.View{
    @BindView(R.id.et_live_intro)
    EditText mEtLiveIntro;

    private LiveBgSettingContract.Presenter mPresenter;
    private HashMap<String, String> map;
    private String token;
    private String chatRoomId;
    private String mIntroduce;      //上个页面传过来的简介

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_bg_introduce);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_live_bg_intro));
    }

    @Override
    public void initData() {
        super.initData();
        new LiveBgSettingPrensenter(this);
        mIntroduce = getIntent().getStringExtra("introduce");
        if (mIntroduce != null){
            mEtLiveIntro.setText(mIntroduce);
            mEtLiveIntro.setSelection(mIntroduce.length());
        }
        token = UserManager.getInstance().getToken();
        chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;
            //保存
            case R.id.headRightButton:
                save();
                break;
        }
    }

    private void save() {
        if (TextUtils.isEmpty(mEtLiveIntro.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.toast_empty_series_intro));
            return;
        } else {
            map = new HashMap<>();
            map.put("token", token);
            map.put("id", chatRoomId);
            map.put("introduce", mEtLiveIntro.getText().toString());
            mPresenter.saveChatRoomSetting(map);

        }
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showLiveManageData(LiveRoomBean bean) {
    }

    @Override
    public void showPicList(List<BgPicBean> picData) {}

    @Override
    public void showCompressSuccess(File file) {}

    @Override
    public void showCompressFailure() {}

    @Override
    public void showUploadingSuccess(String url) {}

    @Override
    public void showChatRoomSetting(LiveBgSettingBean bean) {
        ToastUtil.showShort(this,getString(R.string.modify_success));
        String introduce = bean.getIntroduce();
        Intent intent    = new Intent();
        intent.putExtra("introduce",introduce);
        setResult(RESULT_OK,intent);
//        EventBusUtil.post(new EventLiveManager(introduce));
        finish();
    }

    @Override
    public void showLoadMore(List<BgPicBean> datas) {

    }

    @Override
    public void showNoMore() {

    }

    @Override
    public void showManagerList(LiveManagerBean bean) {

    }

    @Override
    public void showMoreManagerList(LiveManagerBean bean) {
    }

    @Override
    public void setPresenter(LiveBgSettingContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {
        mPresenter.getLiveManageData(map);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getLiveManageData(map);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
