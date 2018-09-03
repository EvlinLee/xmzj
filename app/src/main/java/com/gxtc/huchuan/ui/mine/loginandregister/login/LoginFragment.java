package com.gxtc.huchuan.ui.mine.loginandregister.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePwsActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.RegexUtils;
import com.gxtc.huchuan.widget.CusWrapHeightViewPager;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static android.content.ContentValues.TAG;

/**
 * Created by ALing on 2017/2/17.
 */

@SuppressLint("ValidFragment")
public class LoginFragment extends BaseTitleFragment implements LoginContract.View {

    @BindView(R.id.et_count)
    TextInputEditText mEtCount;
    @BindView(R.id.et_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    @BindView(R.id.tv_forget_psw)
    TextView mTvForgetPsw;

    private CusWrapHeightViewPager vp;

    private LoginContract.Presenter mPresenter;


    @SuppressLint("ValidFragment")
    public LoginFragment(CusWrapHeightViewPager vp) {
        this.vp = vp;
    }

    public LoginFragment() {

    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //vp.setObjectForPosition(view, 0);       //存放你的view和它对应的position
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        new LoginPrenster(this);
        mEtCount.setFilters(new InputFilter[]{filter});
        mEtPassword.setFilters(new InputFilter[]{filter});
        mEtCount.addTextChangedListener(new MobileTextWatcher(this));
        mEtPassword.addTextChangedListener(new PasswordTextWatcher(this));
    }

    @OnClick({R.id.btn_login, R.id.tv_forget_psw})
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_forget_psw:
                GotoUtil.goToActivity(getActivity(), ChangePwsActivity.class);
                break;
        }
    }

    private void login() {
        if (TextUtils.isEmpty(mEtCount.getText().toString().trim())) {
            ToastUtil.showShort(getActivity(), getString(R.string.count_canot_empty));
        } else if (mEtCount.getText().toString().trim().length() > 11) {  //现在不只是手机号能登陆，新媒号也能登录，所以不能只判断是否是手机号,当前只能通过账号长度判断，新媒号当前最长不超过11
            ToastUtil.showShort(getActivity(), getString(R.string.incorrect_account_format));
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString())) {
            ToastUtil.showShort(getActivity(), getString(R.string.pwd_canot_empty));
        } else if (mEtPassword.getText().length() < 6) {
            ToastUtil.showShort(getActivity(), getString(R.string.toast_password_length));
        } else {
            mPresenter.getLogin(mEtCount.getText().toString(), mEtPassword.getText().toString());
        }
    }

    @Override
    public void showLogin(User bean) {
        UserManager.getInstance().saveUser(bean);

        //极光推送
        String alias = UserManager.getInstance().getUserCode();
        JPushUtil.getInstance().setJPushAlias(getActivity(), alias);


        //登录成功 返回上一页面
        EventBusUtil.post(new EventLoginBean(EventLoginBean.LOGIN));
        Intent intent = new Intent();
        LoginFragment.this.getActivity().setResult(Constant.ResponseCode.LOGINRESPONSE_CODE, intent);
        getActivity().finish();     //登录成功 返回上一页面

    }

    @Override
    public void thirdLoginResult(User bean) {

    }

    @Override
    public void thirdLoginBindPhone() {

    }

    @Override
    public void bindPhoneResult() {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getActivity(), info);
        Log.e(TAG, "showError: " + info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getLogin(mEtCount.getText().toString(),
                        mEtPassword.getText().toString());
            }
        });
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    /**
     * 手机号输入监听
     */
    static class MobileTextWatcher implements TextWatcher {
        private LoginFragment mLoginFragment;
        private WeakReference<LoginFragment> mWeakReference;

        public MobileTextWatcher(LoginFragment loginFragment) {
            mWeakReference = new WeakReference<>(loginFragment);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
           /* if (mEtCount.getText().toString().length() == 11) {
                mEtPassword.setEnabled(true);
                mEtPassword.setFocusable(true);
                mEtPassword.setFocusableInTouchMode(true);
                mEtPassword.requestFocus();

            } else {
                mEtPassword.setEnabled(false);

            }*/
        }
    }

    /**
     * 密码输入监听
     */
    static class PasswordTextWatcher implements TextWatcher {
        private CharSequence temp;
        private LoginFragment mLoginFragment;
        private WeakReference<LoginFragment> mWeakReference;

        public PasswordTextWatcher(LoginFragment loginFragment) {
            mWeakReference = new WeakReference<>(loginFragment);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mLoginFragment = mWeakReference.get();
            if (mWeakReference == null) return;
            if ((temp.length() >= 6 && temp.length() < 16) || temp.length() == 16) {
                mLoginFragment.mBtnLogin.setEnabled(true);
                if (temp.length() == 16) try {
                    WindowUtil.closeInputMethod(mLoginFragment.getActivity());
                } catch (Exception e) {
                    return;
                }
            } else mLoginFragment.mBtnLogin.setEnabled(false);

        }
    }

    //禁止输入空格和换行
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
}
