package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改手机号
 * Created by zzg on 2017/7/3.
 */
public class ValitificationCodeActivity extends BaseTitleActivity {

    @BindView(R.id.item)            LinearLayout item;
    @BindView(R.id.input_phone_num) EditText     edPhoneNumber;
    @BindView(R.id.et_val_code)     EditText     edCalCode;

    private ChangePswContract.Source mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valitification_code_layout);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("修改手机号");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        mData = new ChangePwsRepository();
    }

    @OnClick({R.id.btn_submit})
    public void onClick(View v){
        if (ClickUtil.isFastClick()) return;
        switch (v.getId()){
            case R.id.btn_submit:
                changePhone();
                break;
        }
    }


    private void changePhone(){
        final String phone    = edPhoneNumber.getText().toString();
        String       password = edCalCode.getText().toString();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.showShort(this,"请输入手机号");
            return;
        }
        if(!RegexUtils.isMobileSimple(phone)){
            ToastUtil.showShort(this,"请输入正确的手机号");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showShort(this,"请输入密码");
            return;
        }

        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mData.changePhone(token, phone.trim(), password.trim(), new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(ValitificationCodeActivity.this,"修改手机号成功");
                User user = UserManager.getInstance().getUser();
                user.setPhone(phone);
                UserManager.getInstance().saveUser(user);
                finish();
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(ValitificationCodeActivity.this,message);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
    }
}
