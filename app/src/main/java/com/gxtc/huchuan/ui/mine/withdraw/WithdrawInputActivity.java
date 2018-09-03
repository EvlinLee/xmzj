package com.gxtc.huchuan.ui.mine.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.editinfo.BindEPayActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/5/4.
 */

public class WithdrawInputActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.et_name)    EditText mEtName;
    @BindView(R.id.et_account) EditText mEtAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_input_account);

    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("提现");
        getBaseHeadView().showBackButton(this);
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        if (mEtName.getText().toString().length() == 0) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        } else if (mEtAccount.getText().toString().length() == 0) {
            Toast.makeText(this, "帐号不能为空", Toast.LENGTH_SHORT).show();
        } else {
            BindEpay();
        }
    }
    Subscription sub;
    private void BindEpay() {
        if (TextUtils.isEmpty(mEtAccount.getText().toString())){
            ToastUtil.showShort(this,"账号不能为空");
            return;
        }

        if (TextUtils.isEmpty(mEtName.getText().toString())){
            ToastUtil.showShort(this,"用户名不能为空");
            return;
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("withdrawCashType","2");//账户类型。1：微信；2：支付宝；3：银行卡
        map.put("userAccount",mEtAccount.getText().toString());
        map.put("userName",mEtName.getText().toString());
        sub= MineApi.getInstance().bindAccount(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(WithdrawInputActivity.this,"绑定成功");
                        Intent intent = new Intent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(WithdrawInputActivity.this,message);
                    }
                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sub != null && !sub.isUnsubscribed()){
            sub.unsubscribe();
        }
    }
}
