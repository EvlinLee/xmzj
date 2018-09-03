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
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.pop.PopPosition;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/23.
 * 编辑昵称
 */

public class EditCardNumberActivity extends BaseTitleActivity {
    @BindView(R.id.et_user_name)
    TextInputEditText edUserName;
    @BindView(R.id.choose_bank)
    TextInputEditText mChoosebank;
    @BindView(R.id.et_number)
    TextInputEditText edNumber;
    @BindView(R.id.iv_delete)
    ImageView mIvDelete;
    PopPosition popType;
    String [] dtat;
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card_number);
        initData();
    }

    @Override
    public void initView() {
        super.initView();
        popType = new PopPosition(this, R.layout.pop_ts_position);
        popType.setTitle("选择银行");
    }

    @Override
    public void initData() {
        super.initData();
        getBaseHeadView().showTitle(getString(R.string.title_bind_card));
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
        dtat=new String[]{"中国银行", "中国农业银行","中国交通银行","中国建设银行","中国工商银行","中国光大银行","中国浦发银行","中国招商银行"};
        popType.setData(dtat);
        edNumber.addTextChangedListener(new EditTextWatcher());
    }

    @Override
    public void initListener() {
        super.initListener();
        popType.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                mChoosebank.setText(dtat[newVal]);
            }
        });
    }

    @OnClick({R.id.iv_delete,R.id.choose_bank})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_delete:
                edNumber.setText("");
                break;
            case R.id.choose_bank:
                popType.showPopOnRootView(this);
                break;
        }
    }

    private void nikeNameSave() {
        if (TextUtils.isEmpty(edNumber.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.card_number_canot_empty));
        }else if (TextUtils.isEmpty(edUserName.getText().toString())){
            ToastUtil.showShort(this,getString(R.string.card_username_canot_empty));
        }else {
            BindCardNumber();
        }
    }

    private void BindCardNumber() {
        if (TextUtils.isEmpty(edNumber.getText().toString())){
            ToastUtil.showShort(this,"账号不能为空");
            return;
        }

        if (TextUtils.isEmpty(edUserName.getText().toString())){
            ToastUtil.showShort(this,"用户名不能为空");
            return;
        }
        HashMap<String,String> map=new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("withdrawCashType","3");//账户类型。1：微信；2：支付宝；3：银行卡
        map.put("userAccount",edNumber.getText().toString());
        if (!TextUtils.isEmpty(mChoosebank.getText().toString())){
            map.put("openingBank",mChoosebank.getText().toString());
        }
        map.put("userName",edUserName.getText().toString());
        sub= MineApi.getInstance().bindAccount(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(EditCardNumberActivity.this,"绑定成功");
                        setResult();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(EditCardNumberActivity.this,message);
                    }
                }));
    }

    private void setResult() {
        Intent intent=new Intent();
        intent.putExtra("number",edNumber.getText().toString());
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
            if (!TextUtils.isEmpty(edNumber.getText().toString())) {
                mIvDelete.setVisibility(View.VISIBLE);
            } else {
                mIvDelete.setVisibility(View.INVISIBLE);
            }
        }
    }
}
