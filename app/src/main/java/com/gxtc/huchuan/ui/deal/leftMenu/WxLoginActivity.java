package com.gxtc.huchuan.ui.deal.leftMenu;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.WxResponse;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录微信公众平台界面
 */
public class WxLoginActivity extends BaseTitleActivity implements View.OnClickListener, WxLoginContract.View {

    @BindView(R.id.btn_login)                   Button                          btn;
    @BindView(R.id.btn_ver)                     TextView                        btnVer;
    @BindView(R.id.img_ver)                     ImageView                       imgVer;
    @BindView(R.id.rl_ver)                      RelativeLayout                  layoutVer;
    @BindView(R.id.edit_username)               TextInputEditText               editName;
    @BindView(R.id.edit_password)               TextInputEditText               editPass;
    @BindView(R.id.edit_ver)                    TextInputEditText               editVer;

    private String verUrl = "https://mp.weixin.qq.com/cgi-bin/verifycode?username=%s&r=%s";     //验证码地址

    private WxLoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_login);

    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().getParentView().setBackgroundColor(getResources().getColor(R.color.common_background));
        getBaseHeadView().hideHeadLine();
        AndroidBug5497Workaround.assistActivity(this);
        layoutVer.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_login,R.id.btn_ver})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.btn_login:
                goLogin();
                break;

            //换一张验证码
            case R.id.btn_ver:
                showVerifcode(editName.getText().toString().trim());
                break;
        }
    }

    @Override
    public void initData() {
        new WxLoginPresenter(this);
    }

    private void goLogin() {
        String userName = editName.getText().toString().trim();
        String password = editPass.getText().toString().trim();
        String ver = editVer.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showShort(this, getString(R.string.login_emp_accout));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShort(this, getString(R.string.login_emp_password));
            return;
        }

        if(layoutVer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(ver)){
            ToastUtil.showShort(this, getString(R.string.label_input_yzm));
            return;
        }

        mPresenter.login(userName, password,ver);

    }

    @Override
    public void loginSuccess(WxResponse response) {
        ToastUtil.showShort(this, response.getResp().getErr_msg());
    }

    @Override
    public void loginFailed(String msg) {
        ToastUtil.showShort(this, msg);
    }

    //显示验证码
    @Override
    public void showVerifcode(String userName) {
        layoutVer.setVisibility(View.VISIBLE);

        long r = System.currentTimeMillis();
        String url = String.format(Locale.CHINA,verUrl,userName,r+"");
        ImageHelper.loadImage(this,imgVer,url);
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
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
    public void setPresenter(WxLoginContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
