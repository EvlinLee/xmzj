package com.gxtc.huchuan.ui.mine.loginandregister.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.CountDownTimerUtils;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.RegexUtils;
import com.gxtc.huchuan.utils.SystemTools;
import com.gxtc.huchuan.widget.CusWrapHeightViewPager;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by ALing on 2017/2/17.
 */

@SuppressLint("ValidFragment")
public class RegisteFragment extends BaseTitleFragment implements RegisterContract.View, View.OnClickListener {

    @BindView(R.id.et_register_phone)
    TextInputEditText mEtRegisterPhone;
    @BindView(R.id.et_register_yzm)
    TextInputEditText mEtRegisterYzm;
    @BindView(R.id.btn_send_yzm)
    Button mBtnSendYzm;
    @BindView(R.id.iv_show_pws)
    ImageButton mIvShowPws;
    @BindView(R.id.et_register_password)
    TextInputEditText mEtRegisterPassword;
    @BindView(R.id.btn_register)
    Button mBtnRegister;
    @BindView(R.id.cb_register_read)
    TextView mCbRegisterRead;
    @BindView(R.id.tv_register_xy)
    TextView mTvRegisterXy;

    private CusWrapHeightViewPager vp;
    private RegisterContract.Presenter mPresenter;
    private boolean canRead = true;
    private HashMap<String, String> map;
    private int status;
    private String thirdType;       //三方登录类型 1:qq       2.winxin        3.sina
    private String uniqueKey;           //第三方登录唯一标识码
    private AlertDialog mAlertDialog;
    public Map<String, String> dataMap;
    public User localUserbean;
    public YzmTextWatcher yzmTextWatcher;
    public MobileTextWatcher mobileTextWatcher;
    public PasswordTextWatcher passwordTextWatcher;

    public RegisteFragment() {
    }

    @SuppressLint("ValidFragment")
    public RegisteFragment(CusWrapHeightViewPager vp) {
        this.vp = vp;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_registe, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        EventBusUtil.register(this);
        new RegisterPrenster(this);
        yzmTextWatcher = new YzmTextWatcher(this);
        mobileTextWatcher = new MobileTextWatcher(this);
        passwordTextWatcher = new PasswordTextWatcher(this);
        mEtRegisterPhone.setFilters(new InputFilter[]{filter});
        mEtRegisterYzm.setFilters(new InputFilter[]{filter});
        mEtRegisterPassword.setFilters(new InputFilter[]{filter});
        mEtRegisterPhone.addTextChangedListener(mobileTextWatcher);
        mEtRegisterYzm.addTextChangedListener(yzmTextWatcher);
        mEtRegisterPassword.addTextChangedListener(passwordTextWatcher);
    }

    @OnClick({R.id.btn_send_yzm,
            R.id.btn_register,
            R.id.iv_show_pws,
            R.id.tv_register_xy})
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;
        switch (view.getId()) {
            case R.id.btn_send_yzm:
                mBtnSendYzm.setOnClickListener(null);
                //三方绑定发送验证码
                if (status == EventLoginBean.THIRDLOGIN) {
                    if (RegexUtils.isMobileSimple(mEtRegisterPhone.getText().toString())) {
                        mPresenter.getValidationCode(mEtRegisterPhone.getText().toString(), "3");
                    } else {
                        ToastUtil.showShort(getActivity(), getString(R.string.incorrect_phone_format));
                    }

                    //注册发送验证码
                } else {
                    if (RegexUtils.isMobileSimple(mEtRegisterPhone.getText().toString())) {
                        mPresenter.getValidationCode(mEtRegisterPhone.getText().toString(), "0");
                    } else {
                        ToastUtil.showShort(getActivity(), getString(R.string.incorrect_phone_format));
                    }
                }

                break;

            case R.id.btn_register:
                WindowUtil.closeInputMethod(getActivity());
                if (TextUtils.isEmpty(mEtRegisterPhone.getText().toString())) {
                    ToastUtil.showShort(getActivity(), getString(R.string.count_canot_empty));
                    return;
                }
                if (!RegexUtils.isMobileSimple(mEtRegisterPhone.getText().toString())) {
                    ToastUtil.showShort(getActivity(), getString(R.string.incorrect_phone_format));
                    return;
                }
                if (TextUtils.isEmpty(mEtRegisterYzm.getText().toString())) {
                    ToastUtil.showShort(getActivity(),
                            getString(R.string.yzm_canot_empty));
                    return;
                }
                if (TextUtils.isEmpty(mEtRegisterPassword.getText().toString())) {
                    ToastUtil.showShort(getActivity(), getString(R.string.pwd_canot_empty));
                    return;
                }
                if (status == EventLoginBean.THIRDLOGIN) {
                    //第三方注册
                    map = new HashMap<>();
                    map.put("msourceKey", SystemTools.getUniquePsuedoID());  //唯一标识
                    map.put("msourceType", "1");                             //标识安卓
                    map.put("phone", mEtRegisterPhone.getText().toString());
                    map.put("checkCode", mEtRegisterYzm.getText().toString());
                    map.put("password", mEtRegisterPassword.getText().toString());
                    map.put("type", "1");
                    map.put("thirdType", thirdType);
                    map.put("uniqueKey", uniqueKey);
                    mPresenter.getRegister(map);

                } else {
                    //手机号注册
                    map = new HashMap<>();
                    map.put("msourceKey", SystemTools.getUniquePsuedoID());  //唯一标识
                    map.put("msourceType", "1");                             //标识安卓
                    map.put("phone", mEtRegisterPhone.getText().toString());
                    map.put("checkCode", mEtRegisterYzm.getText().toString());
                    map.put("password", mEtRegisterPassword.getText().toString());
                    mPresenter.getRegister(map);
                }
                break;

            case R.id.iv_show_pws:
                canReadPsd();
                break;

            case R.id.tv_register_xy:
                CommonWebViewActivity.startActivity(getContext(), Constant.Url.PROTOCOL_REGISTER, "用户注册协议");
                break;
        }
    }

    private void canReadPsd() {
        if (canRead) {
            mIvShowPws.setImageResource(R.drawable.login_xianshi);
            mEtRegisterPassword.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance());
            canRead = false;
        } else {
            mIvShowPws.setImageResource(R.drawable.login_yincang);
            mEtRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            canRead = true;
        }
    }

    @Override
    public void showRegisterSuccess(User bean) {
        if (status == EventLoginBean.THIRDLOGIN) {
            //第三方注册成功在调修改用户信息的接口把第三方传回的用户昵称，头像和性别保存到我们的服务器
            Map<String, String> map = new HashMap<>();
            switch (thirdType) {
                //qq
                case "1":
                    if (!TextUtils.isEmpty(dataMap.get("nickname"))) {
                        map.put("name", dataMap.get("nickname"));
                        bean.setName(dataMap.get("nickname"));
                    }
                    if (!TextUtils.isEmpty(dataMap.get("headPic"))) {
                        map.put("headPic", dataMap.get("headPic"));
                        bean.setHeadPic(dataMap.get("headPic"));
                    }
                    if (!TextUtils.isEmpty(dataMap.get("sex"))) {
                        map.put("sex", dataMap.get("sex"));
                        bean.setSex(dataMap.get("sex"));
                    }
                    break;
                //wechat
                case "2":
                    if (!TextUtils.isEmpty(dataMap.get("nickname"))) {
                        map.put("name", dataMap.get("nickname"));
                        bean.setName(dataMap.get("nickname"));
                    }
                    if (!TextUtils.isEmpty(dataMap.get("headPic"))) {
                        map.put("headPic", dataMap.get("headPic"));
                        bean.setHeadPic(dataMap.get("headPic"));
                    }
                    if (!TextUtils.isEmpty(dataMap.get("sex"))) {
                        map.put("sex", dataMap.get("sex"));
                        bean.setSex(dataMap.get("sex"));
                    }
                    break;
            }
            this.localUserbean = bean;
            map.put("token", bean.getToken());
            mPresenter.getEditInfo(map);
        } else {
            ToastUtil.showShort(getActivity(), getString(R.string.register_success));
            //极光推送
            String alias = UserManager.getInstance().getPhone();
            JPushUtil.getInstance().setJPushAlias(getActivity(), alias);

            UserManager.getInstance().saveUser(bean);
            EventBusUtil.post(new EventLoginBean(EventLoginBean.REGISTE));
            Intent intent = new Intent();
            RegisteFragment.this.getActivity().setResult(Constant.ResponseCode.LOGINRESPONSE_CODE, intent);
            mAlertDialog = DialogUtil.showNoteDialog(getActivity(), true, "温馨提示", "为保证平台的用户体验，不要在本平台任何地方乱打小广告。" +
                    "乱发违法广告，留微信号，电话号码，二维码引流和发布什么免费带人做项目，收徒弟广告的，一律封号处理。正常的交易和对接信息可以发布", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                    //注册成功，返回上一个页面
                    getActivity().finish();
                    //跳转到信息设置页面
                    GotoUtil.goToActivity(getActivity(), EditInfoActivity.class);
                }
            });

        }
    }

    @Override
    public void showValidationCode(Object object) {
        ToastUtil.showShort(getActivity(), getString(R.string.verification_code_send));
    }

    private CountDownTimerUtils mCountDownTimerUtils;

    //倒计时
    @Override
    public void showCountdown() {
        mCountDownTimerUtils = new CountDownTimerUtils(mBtnSendYzm, 60000, 1000, this);
        mCountDownTimerUtils.start();
    }

    @Override
    public void EditInfoSuccess(Object object) {
        String alias = UserManager.getInstance().getPhone();
        JPushUtil.getInstance().setJPushAlias(getActivity(), alias);

        //绑定手机号成功。进入主页面
        UserManager.getInstance().saveUser(localUserbean);
        EventBusUtil.post(new EventLoginBean(EventLoginBean.THIRDLOGIN));
        Intent intent = new Intent();
        RegisteFragment.this.getActivity().setResult(Constant.ResponseCode.LOGINRESPONSE_CODE, intent);
        mAlertDialog = DialogUtil.showNoteDialog(getActivity(), true, "温馨提示", "为保证平台的用户体验，不要在本平台任何地方乱打小广告。" +
                "乱发违法广告，留微信号，电话号码，二维码引流和发布什么免费带人做项目，收徒弟广告的，一律封号处理。正常的交易和对接信息可以发布", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                //注册成功，返回上一个页面
                getActivity().finish();
                //跳转到信息设置页面
                GotoUtil.goToActivity(getActivity(), EditInfoActivity.class);
            }
        });

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
        mBtnSendYzm.setOnClickListener(this);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(getActivity(), getString(R.string.empty_net_error));
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (mEtRegisterPhone != null) {
            mEtRegisterPhone.removeTextChangedListener(mobileTextWatcher);
        }
        if (mEtRegisterYzm != null) {
            mEtRegisterYzm.removeTextChangedListener(yzmTextWatcher);
        }
        if (mEtRegisterPassword != null) {
            mEtRegisterPassword.removeTextChangedListener(passwordTextWatcher);
        }
        EventBusUtil.unregister(this);
        if (mCountDownTimerUtils != null) mCountDownTimerUtils.cancel();
    }

    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.THIRDLOGIN) {
            thirdType = bean.getThirdType();
            uniqueKey = bean.getUniqueKey();
            status = bean.status;
            dataMap = bean.datas;
            Log.i(TAG, "onEvent: " + status + "," + thirdType + "," + uniqueKey);
            mBtnRegister.setText(getString(R.string.label_bind));
        }
    }

    /**
     * 手机号输入监听
     */
    static class MobileTextWatcher implements TextWatcher {
        RegisteFragment mRegisteFragment;
        WeakReference<RegisteFragment> mWeakReference;

        public MobileTextWatcher(RegisteFragment registeFragment) {
            mWeakReference = new WeakReference<>(registeFragment);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisteFragment = mWeakReference.get();
            if (mRegisteFragment == null) return;
            if (mRegisteFragment.mEtRegisterPhone.getText().toString().length() == 11) {
                mRegisteFragment.mBtnSendYzm.setEnabled(true);
                mRegisteFragment.mEtRegisterYzm.setEnabled(true);
                mRegisteFragment.mEtRegisterYzm.setFocusable(true);
                mRegisteFragment.mEtRegisterYzm.setFocusableInTouchMode(true);
                mRegisteFragment.mEtRegisterYzm.requestFocus();
                try {
                    WindowUtil.closeInputMethod(mRegisteFragment.getActivity());
                } catch (Exception e) {
                    return;
                }

            } else {
                mRegisteFragment.mBtnSendYzm.setEnabled(false);
                mRegisteFragment.mEtRegisterYzm.setEnabled(false);
            }
        }
    }

    /**
     * 验证码输入监听
     */
    static class YzmTextWatcher implements TextWatcher {
        RegisteFragment mRegisteFragment;
        WeakReference<RegisteFragment> mWeakReference;

        public YzmTextWatcher(RegisteFragment registeFragment) {
            mWeakReference = new WeakReference<>(registeFragment);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mRegisteFragment = mWeakReference.get();
            if (mRegisteFragment == null) return;
            if (mRegisteFragment.mEtRegisterPhone.length() < 11) {
                mRegisteFragment.mEtRegisterYzm.setEnabled(false);
                return;
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mRegisteFragment.mEtRegisterYzm.getText().toString().length() == 6)
                mRegisteFragment.mEtRegisterPassword.setEnabled(true);
            mRegisteFragment.mEtRegisterPassword.setFocusable(true);
            try {
                if (mRegisteFragment.mEtRegisterYzm.getText().toString().length() == 6)
                    WindowUtil.closeInputMethod(mRegisteFragment.getActivity());
                mRegisteFragment.mEtRegisterPassword.setFocusable(true);
            } catch (Exception e) {
                return;
            }

        }
    }

    /**
     * 密码输入监听
     */
    static class PasswordTextWatcher implements TextWatcher {
        public CharSequence temp;
        public RegisteFragment mRegisteFragment;
        public WeakReference<RegisteFragment> mWeakReference;

        public PasswordTextWatcher(RegisteFragment registeFragment) {
            mWeakReference = new WeakReference<>(registeFragment);
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
            mRegisteFragment = mWeakReference.get();
            if (mRegisteFragment == null) return;
            if ((temp.length() >= 6 && temp.length() < 16) || temp.length() == 16) {
                mRegisteFragment.mBtnRegister.setEnabled(true);
                if (temp.length() == 16) try {
                    WindowUtil.closeInputMethod(mRegisteFragment.getActivity());
                } catch (Exception e) {
                    return;
                }
            } else mRegisteFragment.mBtnRegister.setEnabled(false);

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
