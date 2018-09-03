package com.gxtc.huchuan.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.KeyboardDialog;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.im.redPacket.PayPwdResetActivity;
import com.gxtc.huchuan.ui.mine.recharge.RechargeActivity;
import com.gxtc.huchuan.utils.MD5Util;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/6.
 * 红包支付弹窗
 */

public class RedPacketPayDialog extends KeyboardDialog {

    public static final int PAY_TYPE_RP = 1;
    public static final int PAY_TYPE_NORMAL = 2;
    public final String TYPE = "type";

    @BindView(R.id.iv_exit)          ImageView imgClose;
    @BindView(R.id.img_head)         ImageView imgHead;
    @BindView(R.id.tv_money)         TextView  tvMoney;
    @BindView(R.id.tv_my_money)      TextView  tvMyMoney;
    @BindView(R.id.tv_recharge)      TextView  tvRecharge;
    @BindView(R.id.tv_pay_title)     TextView  tvPayTitle;
    @BindView(R.id.edit_pass)        EditText  editPass;
    @BindView(R.id.img_dot0)         ImageView dot0;
    @BindView(R.id.img_dot1)         ImageView dot1;
    @BindView(R.id.img_dot2)         ImageView dot2;
    @BindView(R.id.img_dot3)         ImageView dot3;
    @BindView(R.id.img_dot4)         ImageView dot4;
    @BindView(R.id.img_dot5)         ImageView dot5;

    private List<ImageView> dots;
    private HashMap<String,String> param;

    private String money;
    private String password;

    private int payType;
    private double moneyD = 0;
    private double payMoney = 0;

    private OnRedPacketListener mListener;
    private OnInputListener mInputListener;

    private CompositeSubscription mSubscriptions;

    public RedPacketPayDialog(@NonNull Context context,HashMap<String,String> param, int type) {
        super(context,R.style.dialog_Translucent);
        payMoney = Double.valueOf(param.get("rewardAmt"));
        this.param = param;
        this.payType = type;
        initView();
        initListener();
        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(Observable.timer(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editPass,InputMethodManager.SHOW_FORCED);
                }
        }));
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.dialog_rp_pay, null);
        ButterKnife.bind(this, view);
        setContentView(view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowUtil.getScreenWidth(getContext());
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = 70;

        editPass.setCursorVisible(false);

        dots = new ArrayList<>();
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);
        dots.add(dot5);

        ImageHelper.loadCircle(getContext(),imgHead, UserManager.getInstance().getHeadPic());

        payMoney = new BigDecimal(payMoney).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        tvMoney.setText("¥" + payMoney);

        User user = UserManager.getInstance().getUser();
        if(user != null){
            money = TextUtils.isEmpty(user.getUsableBalance()) ? "0" : user.getUsableBalance();
            moneyD = new BigDecimal(money).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            tvMyMoney.setText("¥" + moneyD);

            if(moneyD < payMoney ){
                tvRecharge.setVisibility(View.VISIBLE);
                tvRecharge.setText("余额不足，去充值");
            }
        }

        if(payType == PAY_TYPE_NORMAL){
            tvPayTitle.setText("支付金额");
        }
    }

    private void initListener() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mSubscriptions.unsubscribe();
            }
        });

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

                password = s.toString();
                if(s.length() == 6){
                    //红包支付
                    if(payType == PAY_TYPE_RP){
                        issueRedPacket();
                    //余额支付
                    }else{
                        if(mInputListener != null)  mInputListener.inputFinish(password);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    //发送红包
    private void issueRedPacket(){
        if(moneyD < payMoney){
            tvRecharge.setVisibility(View.VISIBLE);
            tvRecharge.setText("余额不足，去充值");
            return;
        }
        String md5 = MD5Util.MD5Encode(password,null);
        param.put("payPassword",md5);
        Subscription sub =
                CircleApi.getInstance()
                        .issueRedPacket(param)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<RedPacketBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                ToastUtil.showShort(getContext(),"支付成功");
                                RedPacketBean bean = (RedPacketBean) data;
                                if(mListener != null)
                                    mListener.redPacketIssued(bean);
                                dismiss();
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(getContext(),message);
                                //支付密码不正确
                                if("10176".equals(errorCode)){
                                    tvRecharge.setVisibility(View.VISIBLE);
                                    tvRecharge.setText("支付密码不正确");
                                    editPass.setText("");
                                    resetDots();
                                }
                                //用户余额不足
                                if("10072".equals(errorCode)){
                                    tvRecharge.setVisibility(View.VISIBLE);
                                    tvRecharge.setText("余额不足，去充值");
                                    editPass.setText("");
                                    resetDots();
                                }

                                if(mListener != null)
                                    mListener.redPacketIssuedFailed(message);
                            }
                        }));
        mSubscriptions.add(sub);
    }

    @OnClick({R.id.btn_recharge,
            R.id.iv_exit,
            R.id.tv_find_password,
            R.id.tv_recharge,})
    public void onClick(View v) {
        switch (v.getId()) {

            //忘记密码
            case R.id.tv_find_password:
                Intent intentFind = new Intent(getContext(),PayPwdResetActivity.class);
                getContext().startActivity(intentFind);
                break;

            //去充值
            case R.id.btn_recharge:
            case R.id.tv_recharge:
                if(mListener != null){
                   mListener.gotoRecharge();
                }
                break;

            case R.id.iv_exit:
                dismiss();
                break;
        }
    }

    private void resetDots(){
        for(ImageView img : dots){
            if(img.getVisibility() == View.VISIBLE){
                img.setVisibility(View.INVISIBLE);
            }
        }
    }


    //充值之后 刷新下钱数
    public void refreshMoney(){
        if(!isShowing()){
            return;
        }
        payMoney = new BigDecimal(payMoney).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        tvMoney.setText("¥" + payMoney);

        User user = UserManager.getInstance().getUser();
        if(user != null){
            money = TextUtils.isEmpty(user.getUsableBalance()) ? "0" : user.getUsableBalance();
            moneyD = new BigDecimal(money).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            tvMyMoney.setText("¥" + moneyD);

            if(moneyD < payMoney ){
                tvRecharge.setVisibility(View.VISIBLE);
                tvRecharge.setText("余额不足，去充值");
            }
        }

        if(payType == PAY_TYPE_NORMAL){
            tvPayTitle.setText("支付金额");
        }
    }

    public interface OnInputListener{
        void inputFinish(String password);
    }

    public void setOnInputListener(OnInputListener inputListener) {
        mInputListener = inputListener;
    }

    public interface OnRedPacketListener{
        void redPacketIssued(RedPacketBean bean);
        void redPacketIssuedFailed(String msg);
        void gotoRecharge();
    }

    public void setOnRedPacketListener(OnRedPacketListener listener) {
        mListener = listener;
    }
}
