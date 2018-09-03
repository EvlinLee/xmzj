package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.im.redPacket.PayPwdSettingActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.GoToActivityIfLoginUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 账号与安全
 * Created by zzg on 2017/7/3.
 */
//privacy
public class AccountSafeActivity extends BaseTitleActivity {

    @BindView(R.id.rl_account_id)
    RelativeLayout rlAccountId;
    @BindView(R.id.account_id)
    TextView tvAccountId;
    @BindView(R.id.rl_bind_phone)
    RelativeLayout rlBindPhone;
    @BindView(R.id.phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.rl_cind_wei_xin)
    RelativeLayout rlWeiXin;
    @BindView(R.id.weixin_account)
    TextView tvWeiXin;
    @BindView(R.id.rl_edit_pwd)
    RelativeLayout rlEditPwd;
    @BindView(R.id.tv_edit_pwd)
    TextView tvEditPwd;
    @BindView(R.id.ed_pay_pwd)
    TextView tvEditPayPwd;

    private EditText editPassword;

    private ChangePswContract.Source mData;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safe);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_account_safe));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvAccountId.setText(UserManager.getInstance().getUserCode());
        tvPhoneNumber.setText(UserManager.getInstance().getPhone());
        //是否有支付密码。       0：否；1：有
        if (UserManager.getInstance().getUser().getHasPayPwd() == 0) {
            tvEditPayPwd.setText("设置支付密码");
        } else if (UserManager.getInstance().getUser().getHasPayPwd() == 1) {
            tvEditPayPwd.setText("修改支付密码");
        }
    }

    @Override
    public void initData() {
        mData = new ChangePwsRepository();
    }

    @OnClick({R.id.rl_bind_phone, R.id.rl_cind_wei_xin, R.id.rl_edit_pwd, R.id.ed_pay_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            //绑定手机
            case R.id.rl_bind_phone:
                //需改变
                GoToActivityIfLoginUtil.goToActivity(this, BindPhoneActivity.class);
                break;

            //绑定微信
            case R.id.rl_cind_wei_xin:
                GotoUtil.goToActivity(this, BindWXActivity.class);
                break;
//            GotoUtil.goToActivity(this,  PayPwdSettingActivity.class);

            //修改登录密码
            case R.id.rl_edit_pwd:
                View dialogView = View.inflate(this, R.layout.dialog_ed_pwd_layout, null);
                editPassword = (EditText) dialogView.findViewById(R.id.edit_password);
                mDialog = DialogUtil.showEditPwdDialog(AccountSafeActivity.this, dialogView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyPassword();
                    }
                });
                break;
            //修改（设置）支付密码
            case R.id.ed_pay_pwd:
                switch (UserManager.getInstance().getUser().getHasPayPwd()) {
                    case 0://设置支付密码
                        GotoUtil.goToActivity(this, PayPwdSettingActivity.class);
                        break;
                    case 1://修改支付密码
                        GotoUtil.goToActivity(this, EditPayPwdActivity.class);
                        break;
                }
                break;
        }
    }

    private void verifyPassword() {
        final String password = editPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShort(this, "请输入密码");
            return;
        }

        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mData.verifyPassword(token, password.trim(), new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                Intent intent = new Intent(AccountSafeActivity.this, EdPwdActivity.class);
                intent.putExtra("password", password);
                startActivity(intent);
                mDialog.dismiss();
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(AccountSafeActivity.this, message);
                editPassword.setText("");
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
    }
}
