package com.gxtc.huchuan.ui.mine.loginandregister.changepsw;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.CountDownTimerUtils;
import com.gxtc.huchuan.utils.RegexUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ALing on 2017/2/14.
 * 修改密码
 */
public class ChangePwsActivity extends BaseTitleActivity implements ChangePswContract.View, View.OnClickListener {

    @BindView(R.id.et_phone)
    TextInputEditText mEtPhone;
    @BindView(R.id.iv_show_psw)
    ImageButton mIvShowPsw;
    @BindView(R.id.et_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.rl_psw)
    RelativeLayout mRlPsw;
    @BindView(R.id.et_yzm)
    TextInputEditText mEtYzm;
    @BindView(R.id.btn_send_yzm)
    Button mBtnSendYzm;
    @BindView(R.id.rl_yzm)
    RelativeLayout mRlYzm;
    @BindView(R.id.btn_findback)
    Button mBtnFindback;

    private boolean canRead = true;
    private ChangePswContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pws);
        initListener();
        initData();
    }

    @Override
    public void initListener() {
        super.initListener();
        getBaseHeadView().showTitle(getString(R.string.title_forget_psw));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.iv_show_psw, R.id.btn_send_yzm, R.id.btn_findback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_show_psw:
                canReadPsw();
                break;
            case R.id.btn_send_yzm:
                if (ClickUtil.isFastClick()) return;
                if (TextUtils.isEmpty(mEtPhone.getText().toString())) {
                    ToastUtil.showShort(this, getString(R.string.count_canot_empty));
                    return;
                }
                if (RegexUtils.isMobileExact(mEtPhone.getText().toString())) {
                    mPresenter.getValidationCode(mEtPhone.getText().toString(), "1");
                } else {
                    ToastUtil.showShort(this, getString(R.string.incorrect_phone_format));
                }
                break;
            case R.id.btn_findback:
                findBackPsw();
                break;
        }
    }

    private void findBackPsw() {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtPhone.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.count_canot_empty));
            return;
        }
        if (!RegexUtils.isMobileExact(mEtPhone.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.incorrect_phone_format));
            return;
        }
        if (mEtPassword.getText().toString().length() < 6 || mEtPassword.getText().toString().length() > 16) {
            ToastUtil.showShort(this, getString(R.string.label_input_psw));
            return;
        }
        if (TextUtils.isEmpty(mEtPassword.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.pwd_canot_empty));
            return;
        }
        if (TextUtils.isEmpty(mEtYzm.getText().toString())) {
            ToastUtil.showShort(this, getString(R.string.yzm_canot_empty));
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", mEtPhone.getText().toString());
        map.put("password", mEtPassword.getText().toString());
        map.put("checkCode", mEtYzm.getText().toString());
        mPresenter.changePws(map);
    }

    @Override
    public void initData() {
        super.initData();
        new ChangePswPrenster(this);
        mEtPhone.addTextChangedListener(new MobileTextWatcher());
        mEtPassword.addTextChangedListener(new PasswordTextWatcher());
        mEtYzm.addTextChangedListener(new YzmTextWatcher());
    }

    private void canReadPsw() {
        if (canRead) {
            canRead = false;
            mIvShowPsw.setImageResource(R.drawable.login_xianshi);
            mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else{
            canRead = true;
            mIvShowPsw.setImageResource(R.drawable.login_yincang);
            mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void changePwsResult(Object object) {
        ToastUtil.showShort(this, getString(R.string.modify_success));
        this.finish();
    }

    @Override
    public void showValidationCode(Object object) {
        ToastUtil.showShort(this, getString(R.string.verification_code_send));
    }

    private CountDownTimerUtils countDownTimerUtils;

    @Override
    public void showCountdown() {
        countDownTimerUtils = new CountDownTimerUtils(mBtnSendYzm, 60000, 1000, this);
        countDownTimerUtils.start();
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (countDownTimerUtils != null) countDownTimerUtils.cancel();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
        mBtnSendYzm.setOnClickListener(this);
    }

    @Override
    public void showNetError() {

    }

    @Override
    public void setPresenter(ChangePswContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 手机号输入监听
     */
    class MobileTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEtPhone.getText().toString().length() == 11) {
                mEtPassword.setEnabled(true);
                mEtPassword.setFocusable(true);
                mEtPassword.setFocusableInTouchMode(true);
                mEtPassword.requestFocus();
            }
//            else {
//                mEtPassword.setEnabled(false);
//                mEtPassword.setFocusable(false);
//            }
        }
    }

    /**
     * 密码输入监听
     */
    class PasswordTextWatcher implements TextWatcher {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if ((temp.length() >= 6 && temp.length() < 16) || temp.length() == 16) {
                mBtnSendYzm.setEnabled(true);
                mEtYzm.setEnabled(true);
                mEtYzm.setFocusable(true);

                if (temp.length() == 16) try {
                    mEtYzm.setFocusableInTouchMode(true);
                    mEtYzm.requestFocus();
                    WindowUtil.closeInputMethod(ChangePwsActivity.this);
                } catch (Exception e) {
                    return;
                }
            }
//            else {
//                mBtnSendYzm.setEnabled(false);
//                mEtYzm.setEnabled(false);
//            }

        }
    }

    /**
     * 验证码输入监听
     */
    class YzmTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEtYzm.getText().toString().length() == 6) mBtnFindback.setEnabled(true);
            try {
                if (mEtYzm.getText().toString().length() == 6)
                    WindowUtil.closeInputMethod(ChangePwsActivity.this);
            } catch (Exception e) {
                return;
            }
        }
    }
}
