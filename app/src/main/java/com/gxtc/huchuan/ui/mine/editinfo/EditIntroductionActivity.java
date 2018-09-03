package com.gxtc.huchuan.ui.mine.editinfo;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.bean.event.EventIntroBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by ALing on 2017/2/24.
 * 简介编辑
 */
public class EditIntroductionActivity extends BaseTitleActivity implements View.OnClickListener,EditInfoContract.View {

    @BindView(R.id.et_introduction)         TextInputEditText    mEtIntroduction;
    @BindView(R.id.iv_botton_hint)          TextView             mIvBottonHint;

    private HashMap<String, String> mDataMap;
    private String mEditIntro;

    private EditInfoContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_introduction);
        AndroidBug5497Workaround.assistActivity(this);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_edit_introduction));
        mEtIntroduction.setFocusable(true);
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), this);
    }

    @Override
    public void initData() {
        super.initData();
        new EditInfoPrenster(this);
        mEditIntro = getIntent().getStringExtra("editIntro");
        if (mEditIntro != null){
            mEtIntroduction.setText(mEditIntro);
            mEtIntroduction.setSelection(mEtIntroduction.getText().length());
        }
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
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtIntroduction.getText().toString().trim())){
            ToastUtil.showShort(this,getString(R.string.toast_intro_cannot_empty));
            return;
        }else {
            mDataMap = new HashMap<>();
            mDataMap.put("token", UserManager.getInstance().getToken());
            mDataMap.put("introduction",mEtIntroduction.getText().toString());
            mPresenter.getEditInfo(mDataMap);
        }

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void getUserSuccess(User object) {

    }

    @Override
    public void EditInfoSuccess(Object object) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        User user = UserManager.getInstance().getUser();
        user.setIntroduction(mEtIntroduction.getText().toString());
        UserManager.getInstance().updataUser(user);
        EventBusUtil.post(new EventEditInfoBean(EventEditInfoBean.INTRO));
        EventBusUtil.post(new EventIntroBean(mEtIntroduction.getText().toString()));
        finish();
    }

    @Override
    public void showUploadResult(User bean) {}

    @Override
    public void compression(String path) {}

    @Override
    public void setPresenter(EditInfoContract.Presenter presenter) {
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

    }

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }
    /*@Override
    public void initData() {
        super.initData();
        mTvTitle.setText(getString(R.string.title_edit_introduction));
    }

    @onClick({R.id.tv_cancel,R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:

                break;

        }
    }*/

}
