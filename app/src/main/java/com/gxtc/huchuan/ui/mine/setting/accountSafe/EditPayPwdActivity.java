package com.gxtc.huchuan.ui.mine.setting.accountSafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.im.redPacket.PayPwdResetActivity;
import com.gxtc.huchuan.utils.MD5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 隐私设置
 * Created by zzg on 2017/7/3.
 */
public class EditPayPwdActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.img_dot0)  ImageView dot0;
    @BindView(R.id.img_dot1)  ImageView dot1;
    @BindView(R.id.img_dot2)  ImageView dot2;
    @BindView(R.id.img_dot3)  ImageView dot3;
    @BindView(R.id.img_dot4)  ImageView dot4;
    @BindView(R.id.img_dot5)  ImageView dot5;
    @BindView(R.id.edit_pass) EditText  editPass;
    @BindView(R.id.tv_change_pwd)
    TextView tvChangePayPwd;
    @BindView(R.id.no_set_pay_pwd)
    TextView noSetPwdNote;

    private List<ImageView> dots;

    public boolean isEdPayPwd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pay_pwd);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("修改支付密码");
        isEdPayPwd=getIntent().getBooleanExtra("edit_pay_pwd",false);
        if(isEdPayPwd){
            tvChangePayPwd.setText("请输入新的支付密码");
        }
        editPass.setCursorVisible(false);

        dots = new ArrayList<>();
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);
        dots.add(dot5);

    }

    @OnClick({R.id.tv_reset_py_pwd})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.tv_reset_py_pwd:
                Intent intent = new Intent(this, PayPwdResetActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void initListener() {
        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView img = dots.get(start);
                if(img.getVisibility() == View.VISIBLE){
                    img.setVisibility(View.INVISIBLE);
                }else{
                    img.setVisibility(View.VISIBLE);
                }

                if(editPass.getText().toString().length() == 6){
                    //先验证，通过在修改支付密码
                    if(!isEdPayPwd){
                        validatePayPwd();
                    }else {
                        savaPassword();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void resetPassword(){
        Subscription sub =
                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                if(editPass.getText().length() != 0){
                                    editPass.setText("");
                                }
                                for(ImageView img : dots){
                                    img.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void savaPassword(){
        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        if(!TextUtils.isEmpty(editPass.getText().toString()) && editPass.getText().toString().length() == 6){
            String md5 = MD5Util.MD5Encode(editPass.getText().toString(),null);
            Subscription sub = CircleApi.getInstance().savePayPwd(token,md5)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(getBaseLoadingView() == null)    return;
                            getBaseLoadingView().hideLoading();
                            saveSuccess();
                        }

                        @Override
                        public void onError(String errorCode, String message){

                        }
                    }));

            RxTaskHelper.getInstance().addTask(this,sub);
        }
    }
    //验证密码
    private void validatePayPwd(){
        String token = UserManager.getInstance().getToken();
            String md5 = MD5Util.MD5Encode(editPass.getText().toString(),null);
            Subscription sub = CircleApi.getInstance().validatePayPwd(token,md5)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            Intent intent=new Intent(EditPayPwdActivity.this,EditPayPwdActivity.class);
                            intent.putExtra("edit_pay_pwd",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            EditPayPwdActivity.this.startActivity(intent);
                        }

                        @Override
                        public void onError(String errorCode, String message){
                            ToastUtil.showShort(EditPayPwdActivity.this,"验证失败");
                            resetPassword();
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void saveSuccess(){
        ToastUtil.showShort(this,"设置密码成功");
        User user = UserManager.getInstance().getUser();
        user.setHasPayPwd(1);
        UserManager.getInstance().updataUser(user);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
