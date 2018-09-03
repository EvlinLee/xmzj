package com.gxtc.huchuan.ui.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventPayResultBean;
import com.gxtc.huchuan.bean.pay.OrderNoBean;
import com.gxtc.huchuan.bean.pay.OrderRePayBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.PayBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.RedPacketPayDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.im.redPacket.PayPwdSettingActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.ClickUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 支付页面
 */
public class PayActivity extends BaseTitleActivity implements PayConstant.View, RedPacketPayDialog.OnInputListener {
    private static final String TAG = "PayActivity";
    private final int ALI = 0x01;
    private final int WX = 0x02;
    private final int MONEY = 0x03;

    private final int REQUEST_SETTING_PWD = 0X03;

    @BindView(R.id.tv_pay_title)
    TextView tvTitle;
    @BindView(R.id.tv_pay_money)
    TextView tvMoney;
    @BindView(R.id.tv_pay_money_des)
    TextView tvMyBalance;    //我的可用余额
    @BindView(R.id.rl_pay_alipay)
    RelativeLayout rlAlipay;
    @BindView(R.id.rl_pay_wx)
    RelativeLayout rlWx;
    @BindView(R.id.rl_pay_money)
    RelativeLayout rlMoney;
    @BindView(R.id.rb_pay_alipay)
    RadioButton rbAlipay;
    @BindView(R.id.rb_pay_wx)
    RadioButton rbWx;
    @BindView(R.id.rb_pay_money)
    RadioButton rbMoney;
    @BindView(R.id.btn_pay_now)
    Button btnPay;

    private String payType = "ALIPAY";  //WX, ALIPAY, BALANCE

    private String goodsname, goodsprice, orderNo, token;

    private double totalPrice;

    private OrdersRequestBean requestBean;

    private PayConstant.Presenter mPresenter;

    private ProgressDialog mDialog;

    private RedPacketPayDialog mPayDialog;

    private OrderRePayBean rePayBean;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }


    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_pay));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        double balance = 0;
        try {
            balance = Double.valueOf(UserManager.getInstance().getUser().getUsableBalance());
            tvMyBalance.setText("可用余额: ¥" + balance);
        } catch (Exception e) {
            rbMoney.setEnabled(false);
            rbMoney.setFocusable(false);
            rbMoney.setChecked(false);
            rbAlipay.performClick();
        }

        flag = getIntent().getStringExtra("flag");
        requestBean = (OrdersRequestBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
        requestBean.setPayType(payType);
        double money = Double.valueOf(requestBean.getTotalPrice());

        //保留2个小数，往上升，206.99999997->207
        BigDecimal mData = new BigDecimal(String.valueOf(money)).setScale(2, BigDecimal.ROUND_HALF_UP);
        totalPrice = mData.doubleValue();
        requestBean.setTotalPrice(totalPrice + "");
        tvTitle.setText("商品名称: " + requestBean.getGoodsName());
        tvMoney.setText("商品总金额: " + Double.valueOf(requestBean.getTotalPrice()) / 100 + " 元");

        //如果可以使用余额支付的话
        if (balance >= Double.valueOf(requestBean.getTotalPrice()) / 100) {
            //rbMoney.performClick();
            onClick(rbMoney);
        } else {
            rbMoney.setEnabled(false);
            rbMoney.setFocusable(false);
            rbMoney.setChecked(false);
            rbMoney.setOnClickListener(null);
            rlMoney.setOnClickListener(null);
            onClick(rbAlipay);
            //rbAlipay.performClick();
        }

        if ("UR".equals(requestBean.getTransType())) {
            rlMoney.setVisibility(View.GONE);
            onClick(rlAlipay);
            //rlAlipay.performClick();
            btnPay.setText(getString(R.string.pay_recharge));
        }
        EventBusUtil.register(this);
    }


    @Override
    public void initListener() {

    }

    @OnClick({R.id.rl_pay_alipay,
            R.id.rl_pay_wx,
            R.id.rl_pay_money,
            R.id.rb_pay_wx,
            R.id.rb_pay_money,
            R.id.rb_pay_alipay,
            R.id.btn_pay_now})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_pay_alipay:
            case R.id.rl_pay_alipay:
                if (rePayBean instanceof OrderRePayBean) {
                    payType = PayConstant.PAY_TYPE_ALIPAY;
                    rePayBean.setPayType(payType);
                    changeBtnState(ALI);
                } else {
                    payType = PayConstant.PAY_TYPE_ALIPAY;
                    requestBean.setPayType(payType);
                    changeBtnState(ALI);
                }
                break;

            case R.id.rb_pay_wx:
            case R.id.rl_pay_wx:
                if (rePayBean instanceof OrderRePayBean) {
                    payType = PayConstant.PAY_TYPE_WX;
                    rePayBean.setPayType(payType);
                    changeBtnState(WX);
                } else {
                    payType = PayConstant.PAY_TYPE_WX;
                    requestBean.setPayType(payType);
                    changeBtnState(WX);
                }
                break;

            case R.id.rl_pay_money:
            case R.id.rb_pay_money:
                if (rePayBean instanceof OrderRePayBean) {
                    payType = PayConstant.PAY_TYPE_BALANCE;
                    rePayBean.setPayType(payType);
                    changeBtnState(MONEY);
                } else {
                    payType = PayConstant.PAY_TYPE_BALANCE;
                    requestBean.setPayType(payType);
                    changeBtnState(MONEY);
                }
                break;

            case R.id.btn_pay_now:
                if (!ClickUtil.isFastClick()) {
                    GoToPay();
                }
                break;
        }
    }


    public void GoToPay() {
        if (isNetworkConnected(this)) {
            //根据订单号是否为空判定是否为重新支付
            if (requestBean.getOrderNo() == null) {
                //生成订单支付
                if (!"BALANCE".equals(payType)) {
                    mPresenter.getOrders(this, payType, requestBean);
                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDialog.setMessage("正在跳转支付界面，请稍等..");
                    mDialog.show();

                } else {
                    showPayDialog();
                }
            } else {
                //重新支付
                rePayBean = new OrderRePayBean();

                HashMap<String, String> map = new HashMap<>();

                rePayBean.setGoodsname(requestBean.getGoodsName());
                rePayBean.setGoodsprice(requestBean.getTotalPrice());
                rePayBean.setOrderNo(requestBean.getOrderNo());
                rePayBean.setToken(UserManager.getInstance().getToken());
                rePayBean.setTransType(requestBean.getTransType());
                rePayBean.setPayType(payType);
                rePayBean.setTotalPrice(requestBean.getTotalPrice());
                rePayBean.setExtra(requestBean.getExtra());

                if (!"BALANCE".equals(payType)) {
                    mPresenter.getRePayOrder(this, payType, rePayBean);
                    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mDialog.setMessage("正在跳转支付界面，请稍等..");
                    mDialog.show();
                } else {
                    showPayDialog();
                }
            }
        } else {
            ToastUtil.showShort(this, "网络连接有误");
        }

    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void showPayDialog() {
        //没有支付密码，去设置
        User user = UserManager.getInstance().getUser();
        if (user != null && user.getHasPayPwd() == 0) {
            GotoUtil.goToActivityForResult(this, PayPwdSettingActivity.class, REQUEST_SETTING_PWD);
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("rewardAmt", totalPrice / 100 + "");
        mPayDialog = new RedPacketPayDialog(this, map, RedPacketPayDialog.PAY_TYPE_NORMAL);
        mPayDialog.setOnInputListener(this);
        mPayDialog.show();
    }

    @Override
    public void inputFinish(String password) {
        mPayDialog.dismiss();
        requestBean.setPayPassword(password);
        if (requestBean.getOrderNo() == null) {
            mPresenter.getOrders(this, payType, requestBean);
        } else {
            rePayBean.setPayPwd(requestBean.getPayPassword());
            mPresenter.getRePayOrder(this, payType, rePayBean);
        }
    }

    @Subscribe
    public void onPayResult(OrderNoBean bean) {
        orderNo = bean.OrderNo;//仅在微信支付时调用
    }

    @Subscribe
    public void onPayResult(EventPayResultBean bean) {
        final int code = bean.status;
        if (bean.orderNo != null) {
            orderNo = bean.orderNo;
        }
        switch (code) {
            //去后台查询是否支付成功
            case PayConstant.ERR_CODE_PAY_FINISH:
                mPresenter.isPaySuccessful(new ApiCallBack<Void>() {

                    @Override
                    public void onSuccess(Void data) {
                        showPaySuccecc(code, orderNo);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(PayActivity.this, "支付失败");
                        EventBus.getDefault().post(new PayBean(false));
                        PayActivity.this.finish();
                        mDialog.dismiss();
                    }
                });
                break;

            case PayConstant.ERR_CODE_PAY_FAILURE:
                ToastUtil.showShort(this, "请再次选择支付方式并重新确认支付");
                mDialog.dismiss();
                break;

            case PayConstant.ERR_CODE_PAY_CANCEL:
                ToastUtil.showShort(this, "取消支付");
                mDialog.dismiss();
                break;

        }
    }

    private void upUser() {
        if (UserManager.getInstance().isLogin()) {
            MineApi.getInstance()
                    .getUserInfo(UserManager.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<User>>(new ApiCallBack<User>() {
                        @Override
                        public void onSuccess(User data) {
                            if (data != null) {
//                                UserManager.getInstance().saveUser(data);
                                UserManager.getInstance().updataUser(data);
                                PayActivity.this.finish();
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            Log.d(TAG, "onError: 更新用户信息出错");
                        }
                    }));
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }

    }


    @Override
    public void initData() {
        new PayPresenter(this);
        mDialog = new ProgressDialog(this);
    }

    @Override
    public void showPaySuccecc(int code, String orderId) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA, code);
        PayActivity.this.setResult(PayConstant.PAY_RESULT, intent);
        EventBus.getDefault().post(new PayBean(true, orderId));
        upUser();
        mDialog.dismiss();

    }

    @Override
    public void showPayCallBackFinish() {
        ToastUtil.showShort(this, "支付成功，查询服务器订单状态");
    }

    @Override
    public void showPayFailure() {
        ToastUtil.showShort(this, "支付失败");
    }

    @Override
    public void showPayCallBackCancel() {
        ToastUtil.showShort(this, "取消支付");
    }


    @Override
    public void showNetError() {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showEmpty() {
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showReLoad() {
    }

    @Override
    public void setPresenter(PayConstant.Presenter presenter) {
        mPresenter = presenter;
    }

    private void changeBtnState(int tag) {
        rbAlipay.setChecked(tag == ALI);
        rbWx.setChecked(tag == WX);
        rbMoney.setChecked(tag == MONEY);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_PWD && resultCode == RESULT_OK) {
            showPayDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }


    @Override
    public void tokenOverdue() {
        ToastUtil.showShort(this, getString(R.string.token_overdue));
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    public static void startActivity(Context context, OrdersRequestBean bean) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(Constant.INTENT_DATA, bean);
        context.startActivity(intent);
    }

    public static void startActivity(Activity mActivity, OrdersRequestBean bean, Integer requestCode, String flag) {
        Intent intent = new Intent(mActivity, PayActivity.class);
        intent.putExtra(Constant.INTENT_DATA, bean);
        intent.putExtra("flag", flag);//区分是否是从商城过来的 //0不是 1是
        mActivity.startActivityForResult(intent, requestCode);
    }

}
