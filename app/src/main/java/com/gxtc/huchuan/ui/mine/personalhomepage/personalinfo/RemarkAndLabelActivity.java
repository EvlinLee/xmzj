package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.bean.event.EventRemarkPersonalInfo;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;

import java.util.HashMap;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Describe:
 * Created by ALing on 2017/5/31.
 */

public class RemarkAndLabelActivity extends BaseTitleActivity implements View.OnClickListener,
        PersonalInfoContract.View {

    @BindView(R.id.et_edit_remark) EditText                       mEtEditRemark;
    @BindView(R.id.et_more_remark) EditText                       mEtMoreRemark;

    private PersonalInfoContract.Presenter mPresenter;
    private HashMap<String,String> map;
    private String userCode;

    private PersonInfoBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark_and_label);
        AndroidBug5497Workaround.assistActivity(this);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_remark_info));
        getBaseHeadView().showHeadRightButton(getString(R.string.label_complete),this);
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
        userCode = getIntent().getStringExtra("userCode");
        new PersonalInfoPresenter(this);
        getInfo();
    }

    private void getInfo() {
        map = new HashMap<>();
        //获取个人详细信息
        map.put("token", UserManager.getInstance().getToken());
        map.put("userCode",userCode);
        mPresenter.getUserInformation(map);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            //完成
            case R.id.headRightButton:
                editFinish();
                break;

        }
    }

    private void editFinish() {
        map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("userCode",userCode);

        if (!(TextUtils.isEmpty(mEtEditRemark.getText().toString().trim()))){
            if(mEtEditRemark.getText().toString().length() > 50){
                ToastUtil.showShort(this,"用户备注名不能超过50个字符");
                return;
            }
            map.put("remarkName",mEtEditRemark.getText().toString());
        }
        if (!(TextUtils.isEmpty(mEtMoreRemark.getText().toString()))){
            map.put("remarkDesc",mEtMoreRemark.getText().toString());
        }

        mPresenter.saveLinkRemark(map);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showUserInformation(PersonInfoBean bean) {
        this.bean = bean;
        mEtEditRemark.setText(bean.getRemarkName());
        mEtEditRemark.setSelection(bean.getRemarkName().length());

        mEtMoreRemark.setText(bean.getRemarkDesc());
        mEtMoreRemark.setSelection(bean.getRemarkDesc().length());
    }

    @Override
    public void showsaveLinkRemark(Object o) {
        EventBusUtil.post(new EventRemarkPersonalInfo());
        String reName = mEtEditRemark.getText().toString();
        String img = bean.getHeadPic();
        UserInfo userInfo = new UserInfo(userCode,reName, Uri.parse(img));
        RongIM.getInstance().refreshUserInfoCache(userInfo);
        this.finish();
    }

    @Override
    public void showFollowSuccess() {

    }

    @Override
    public void showApplySuccess() {

    }


    @Override
    public void setPresenter(PersonalInfoContract.Presenter presenter) {
        mPresenter = presenter;
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
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    public static void startActivity(Context context, String userCode) {
        Intent intent = new Intent(context, RemarkAndLabelActivity.class);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }
}
