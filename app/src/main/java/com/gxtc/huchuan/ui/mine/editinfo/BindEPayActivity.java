package com.gxtc.huchuan.ui.mine.editinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.utils.RegexUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2017/2/23.
 * 编辑昵称
 */

public class BindEPayActivity extends BaseTitleActivity {
    @BindView(R.id.et_user_name)
    TextInputEditText mEtUserName;
    @BindView(R.id.et_nike_name)
    TextInputEditText mEtNikeName;
    @BindView(R.id.iv_delete)
    ImageView mIvDelete;

    private String mNikeName;
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_epay_number);
        initData();
    }

    @Override
    public void initData() {
        super.initData();
        getBaseHeadView().showTitle(getString(R.string.title_epay_card));
        getBaseHeadView().showCancelBackButton(getString(R.string.label_cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton(getString(R.string.label_save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nikeNameSave();
            }
        });
        mEtNikeName.addTextChangedListener(new EditTextWatcher());
    }

    @OnClick({R.id.iv_delete})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_delete:
                mEtNikeName.setText("");
                break;
        }
    }

    private void nikeNameSave() {
        if (TextUtils.isEmpty(mEtNikeName.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.epay_number_canot_empty));
        }else {

//            finish();
            BindEpay();
        }
    }

    private void BindEpay() {
        if (TextUtils.isEmpty(mEtNikeName.getText().toString())){
            ToastUtil.showShort(this,"账号不能为空");
            return;
        }

        if (TextUtils.isEmpty(mEtUserName.getText().toString())){
            ToastUtil.showShort(this,"用户名不能为空");
            return;
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("withdrawCashType","2");//账户类型。1：微信；2：支付宝；3：银行卡
        map.put("userAccount",mEtNikeName.getText().toString());
        map.put("userName",mEtUserName.getText().toString());
        sub=MineApi.getInstance().bindAccount(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(BindEPayActivity.this,"绑定成功");
                        setResult();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(BindEPayActivity.this,message);
                    }
                }));
    }

    public  void setResult(){
        Intent intent=new Intent();
        intent.putExtra("paynumber",mEtNikeName.getText().toString());
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sub != null && sub.isUnsubscribed()){
            sub.unsubscribe();
        }
    }

    /**
     * 编辑框监听
     */
    class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mEtNikeName.getText().toString())) {
                mIvDelete.setVisibility(View.VISIBLE);
            } else {
                mIvDelete.setVisibility(View.INVISIBLE);
            }
        }
    }
}
