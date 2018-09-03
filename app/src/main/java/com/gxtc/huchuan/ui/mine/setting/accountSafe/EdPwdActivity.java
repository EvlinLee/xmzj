package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;

import butterknife.BindView;

/**
 * 修改密码
 * Created by zzg on 2017/7/3.
 */
//privacy
public class EdPwdActivity extends BaseTitleActivity {

    @BindView(R.id.input_phone_id)
    TextView tvPhone;
    @BindView(R.id.et_val_pwd)
    EditText edPwd;
    @BindView(R.id.et_confirm_pwd)
    EditText edConfirmPwd;

    private ChangePswContract.Source mData;

    private String password;
    private String confirmPwd;
    private String oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ed_pwd_layout);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("修改密码");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        tvPhone.setText(UserManager.getInstance().getPhone());
    }

    @Override
    public void initData() {
        oldPassword = getIntent().getStringExtra("password");
        mData = new ChangePwsRepository();
    }

    private void changePassword() {
        password = edPwd.getText().toString();
        confirmPwd = edConfirmPwd.getText().toString();

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showShort(this, "请输入密码");
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showShort(this, "请输入6-16位密码");
            return;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtil.showShort(this, "请再次输入密码");
            return;
        }

        if (!password.trim().equals(confirmPwd.trim())) {
            ToastUtil.showShort(this, "两次密码不一致");
            edConfirmPwd.setText("");
            return;
        }

        if (password.equals(oldPassword)){
            ToastUtil.showShort(this, "新密码与旧密码重复");
            return;
        }

        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mData.changePassword(token, password.trim(), confirmPwd.trim(), new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(EdPwdActivity.this, "修改密码成功");
                finish();
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(EdPwdActivity.this, message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
    }
}
