package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.ChangePwsRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.loginandregister.changepsw.ChangePswContract;

import butterknife.BindView;

/**
 * 绑定微信号
 */
public class BindWXActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.edit_phone) EditText editPhone;
    @BindView(R.id.edit_code)  EditText editCode;

    private ChangePswContract.Source mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wx);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("绑定微信号");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("完成",this);
    }

    @Override
    public void initData() {
        mData = new ChangePwsRepository();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                submit();
                break;
        }
    }


    private void submit(){
        String name = editPhone.getText().toString();
        String code = editCode.getText().toString();

        if(TextUtils.isEmpty(name)){
            ToastUtil.showShort(this,"请输入微信名");
            return;
        }

        if(TextUtils.isEmpty(code)){
            ToastUtil.showShort(this,"请输入微信号");
            return;
        }

        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mData.bindWx(token, name, code, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(BindWXActivity.this,"绑定成功");
                finish();
            }

            @Override
            public void onError(String errorCode, String message) {
                getBaseLoadingView().hideLoading();
                ToastUtil.showShort(BindWXActivity.this,message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.destroy();
    }
}
