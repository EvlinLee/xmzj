package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.GoToActivityIfLoginUtil;
import com.gxtc.huchuan.utils.RegexUtils;

import butterknife.BindView;

/**
 * 隐私设置
 * Created by zzg on 2017/7/3.
 */
public class BindPhoneActivity extends BaseTitleActivity {

    @BindView(R.id.item)         LinearLayout item;
    @BindView(R.id.et_phone_num) EditText     edPhoneNumber;
    @BindView(R.id.tv_phone)     TextView     tvPhone;

    private EditText editCode;
    private TextView tvDialogPhone;

    private String                   phone;
    private ChangePswContract.Source findData;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("绑定手机");
    }

    @Override
    public void initData() {
        findData = new ChangePwsRepository();
        String phone = "当前手机号：" + UserManager.getInstance().getPhone();
        tvPhone.setText(phone);
    }

    @Override
    public void initListener() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton("下一步", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtil.closeInputMethod(BindPhoneActivity.this);
                getCaptchaCode();
            }
        });
    }

    //获取验证码
    private void getCaptchaCode() {
        phone = edPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort(this, "请输入手机号");
            return;
        }
        if (!RegexUtils.isMobileSimple(phone)) {
            ToastUtil.showShort(this, "请输入正确的手机号");
            return;
        }

        getBaseLoadingView().showLoading(true);
        findData.getValidationCode(new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                View dialogView = View.inflate(BindPhoneActivity.this, R.layout.dialog_bind_phone_layout, null);
                tvDialogPhone = (TextView) dialogView.findViewById(R.id.tv_phone);
                editCode = (EditText) dialogView.findViewById(R.id.edit_code);
                tvDialogPhone.setText(phone);
                mDialog = DialogUtil.showBindPhoneDialog(BindPhoneActivity.this, dialogView,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //验证验证码
                                verifyCode();
                            }
                        });
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(BindPhoneActivity.this, message);
            }
        }, phone.trim(), "3");
    }

    //验证验证码
    private void verifyCode() {
        String code = editCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showShort(this, "请输入验证码");
            return;
        }

        getBaseLoadingView().showLoading(true);
        findData.verifyCode(phone, code, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                GoToActivityIfLoginUtil.goToActivity(BindPhoneActivity.this, ValitificationCodeActivity.class);
                mDialog.dismiss();
                finish();
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                editCode.setText("");
                ToastUtil.showShort(BindPhoneActivity.this, message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        findData.destroy();
    }
}
