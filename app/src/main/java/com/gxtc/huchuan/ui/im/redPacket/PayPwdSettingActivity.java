package com.gxtc.huchuan.ui.im.redPacket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.utils.MD5Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 支付密码设置界面
 */
public class PayPwdSettingActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.img_dot0)  ImageView dot0;
    @BindView(R.id.img_dot1)  ImageView dot1;
    @BindView(R.id.img_dot2)  ImageView dot2;
    @BindView(R.id.img_dot3)  ImageView dot3;
    @BindView(R.id.img_dot4)  ImageView dot4;
    @BindView(R.id.img_dot5)  ImageView dot5;
    @BindView(R.id.edit_pass) EditText  editPass;
    @BindView(R.id.tv_label)  TextView  tvLabel;
    @BindView(R.id.btn_ok)    Button    btn;

    private List<ImageView> dots;

    private String password = "";
    private String confirmPw = "";

    private String phone ;
    private String yzm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pwd_setting);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("设置支付密码");
        getBaseHeadView().showBackButton(this);
        editPass.setCursorVisible(false);

        dots = new ArrayList<>();
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);
        dots.add(dot5);

        phone = getIntent().getStringExtra("phone");
        yzm = getIntent().getStringExtra("yzm");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.btn_ok:
                if(TextUtils.isEmpty(yzm)){
                    savaPassword();
                }else{
                    changePassword();
                }
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

                if(password.length() == 6 && confirmPw.length() == 6 && password.equals(confirmPw)){
                    btn.setOnClickListener(PayPwdSettingActivity.this);
                    btn.setBackgroundResource(R.drawable.select_btn_blue);
                }else{
                    btn.setOnClickListener(null);
                    btn.setBackgroundResource(R.color.background_tab_pressed);
                }

                if(password.length() != 6){
                    password = s.toString();
                }else{
                    confirmPw = s.toString();
                }

                //再次输入密码确认
                if(TextUtils.isEmpty(confirmPw) && !TextUtils.isEmpty(password) && password.length() == 6){
                    resetPassword();
                    tvLabel.setText("请再次输入密码");
                }

                if(!TextUtils.isEmpty(confirmPw) && confirmPw.length() == 6){
                    checkPassword();
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

    private void checkPassword(){
        if(!TextUtils.isEmpty(password)){
            if(password.equals(confirmPw)){
                editPass.setEnabled(false);
                editPass.setFocusable(false);
                tvLabel.setText("");
                btn.setOnClickListener(this);
                btn.setBackgroundResource(R.drawable.select_btn_blue);
            }else{
                ToastUtil.showShort(this,"两次输入密码不一致，请重新输入");
                resetPassword();
                confirmPw = "";
            }
        }
    }


    private void savaPassword(){
        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        if(!TextUtils.isEmpty(confirmPw) && confirmPw.length() == 6){
            String md5 = MD5Util.MD5Encode(confirmPw,null);
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
                                            public void onError(String errorCode, String message) {
                                                if(getBaseLoadingView() == null)    return;
                                                getBaseLoadingView().hideLoading();
                                                ToastUtil.showShort(PayPwdSettingActivity.this,message);
                                            }
                                        }));

            RxTaskHelper.getInstance().addTask(this,sub);
        }
    }

    //重置密码
    private void changePassword(){
        if(!TextUtils.isEmpty(confirmPw) && confirmPw.length() == 6){
            String md5 = MD5Util.MD5Encode(confirmPw,null);
            HashMap<String,String> map = new HashMap<>();
            map.put("phone",phone);
            map.put("checkCode",yzm);
            map.put("password",md5);
            map.put("type","1");
            Subscription sub =
                    MineApi.getInstance().changePws(map)
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                       @Override
                                       public void onSuccess(Object data) {
                                           setResult(RESULT_OK);
                                           ToastUtil.showShort(PayPwdSettingActivity.this,"设置密码成功");
                                           finish();
                                       }

                                       @Override
                                       public void onError(String errorCode, String message) {
                                           ToastUtil.showShort(PayPwdSettingActivity.this,message);
                                           if("10014".equals(errorCode)){
                                               finish();
                                           }
                                       }
                                   })
                           );

            RxTaskHelper.getInstance().addTask(this,sub);
        }
    }

    private void saveSuccess(){
        ToastUtil.showShort(this,"设置密码成功");
        User user = UserManager.getInstance().getUser();
        user.setHasPayPwd(1);
        UserManager.getInstance().updataUser(user);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
