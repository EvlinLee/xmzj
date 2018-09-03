package com.gxtc.huchuan.ui.im.redPacket;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 找回支付密码页面
 */
public class PayPwdResetActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.et_phone)     TextInputEditText editPhone;
    @BindView(R.id.et_yzm)       TextInputEditText editYzm;
    @BindView(R.id.btn_send_yzm) Button            btnYzm;
    @BindView(R.id.btn_findback) Button            btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pwd_reset);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("找回支付密码");
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initListener() {
        editYzm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)){
                    btnOk.setBackgroundResource(R.color.background_tab_pressed);
                }else{
                    btnOk.setBackgroundResource(R.drawable.select_btn_blue);
                }
            }
        });
    }

    @OnClick({R.id.btn_send_yzm, R.id.btn_findback})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //发送验证码
            case R.id.btn_send_yzm:
                sendYzm();
                break;

            //跳转至设置密码界面
            case R.id.btn_findback:
                gotoReset();
                break;
        }
    }

    private void gotoReset() {
        String phone = editPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort(this, "请填写手机号");
            return;
        }

        String verCode = editYzm.getText().toString();
        if (TextUtils.isEmpty(verCode)) {
            ToastUtil.showShort(this, "请填写验证码");
            return;
        }

        Intent intent = new Intent(this,PayPwdSettingActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("yzm",verCode);
        startActivityForResult(intent,0);

    }

    //发送验证码
    private void sendYzm() {
        String phone = editPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort(this, "请填写手机号");
            return;
        }
        btnYzm.setOnClickListener(null);
        btnYzm.setTextColor(getResources().getColor(R.color.text_color_999));

        Subscription sub = MineApi.getInstance()
                                  .getValidateCode(phone, "1")
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(
                                    new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                        @Override
                                        public void onSuccess(Object data) {
                                            if(btnOk == null)   return;
                                            ToastUtil.showShort(PayPwdResetActivity.this,"验证码已发送至您的手机");
                                            startCountdown();
                                        }

                                        @Override
                                        public void onError(String errorCode, String message) {
                                            if(btnOk == null)   return;
                                            ToastUtil.showShort(PayPwdResetActivity.this,message);
                                        }
                                  }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private int time = 60;
    private Subscription sub;
    //开始倒计时
    private void startCountdown() {
        sub =
            Observable.interval(1, TimeUnit.SECONDS)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Action1<Long>() {
                          @Override
                          public void call(Long aLong) {
                              time --;
                              if(time == 0){
                                  sub.unsubscribe();
                                  btnYzm.setTextColor(getResources().getColor(R.color.text_color_999));
                                  btnYzm.setOnClickListener(PayPwdResetActivity.this);
                                  btnYzm.setText("获取验证码");
                                  time = 60;
                                  return;
                              }
                              btnYzm.setText(time + "s");
                          }
                      });

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
