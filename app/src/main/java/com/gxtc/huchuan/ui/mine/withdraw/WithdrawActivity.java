package com.gxtc.huchuan.ui.mine.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.pay.AccountSet;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.StringUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/5/4.
 *
 */

public class WithdrawActivity extends BaseTitleActivity implements WithdrawContract.View,
        View.OnClickListener {
    public static final  int    SET_ACCOUNT = 1 << 3;
    private static final String TAG         = "WithdrawActivity";

    @BindView(R.id.iv_pay_icon)          ImageView mIvPayIcon;
    @BindView(R.id.tv_account)           TextView  mTvAccount;
    @BindView(R.id.tv_withdraw_max)      TextView  mTvWithdrawMax;
    @BindView(R.id.et_money)             EditText  mEtMoney;
    @BindView(R.id.btn_submit)           Button    mBtnSubmit;
    @BindView(R.id.tv_add_account_label) TextView  mTvAddAccountLabel;
    @BindView(R.id.tv_percent)           TextView  mTvPercent;
    @BindView(R.id.tv_all_cash)           TextView  mTvCash;

    private WithdrawContract.Presenter mPresenter;
    private String                     mName;
    private String                     mAccount;
    private String                     mPercent = "-1";

    private double money  = 0;
    private double midFee = 0;      //中间费用
    private double upInt = 0;       //中间费用
    private double total = 0;       //中间费用
    private double usableBalance = 0;

    private String myMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
    }

    @Override
    public void initData() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("提现");

        myMoney = UserManager.getInstance().getUser().getUsableBalance();
        mTvWithdrawMax.setText("最多可转出" + myMoney + "元");
        new WithdrawPresenter(this);
        mPresenter.getAccountSetInfo();
    }

    @Override
    public void initListener() {
        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                money = StringUtil.toDouble(s.toString());
                usableBalance = StringUtil.toDouble(myMoney);

                Double aDouble = StringUtil.toDouble(mPercent);
                midFee = new BigDecimal(money).multiply(new BigDecimal(aDouble)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_UP).setScale(2, BigDecimal.ROUND_UP).doubleValue();
                total = StringUtil.toDouble(String.valueOf(midFee + money));
                total = Double.valueOf(StringUtil.formatMoney(2,total));
                if (money == 0) {
                    mTvPercent.setText("支付宝扣除" + mPercent + "%手续费");
                } else {
                    mTvPercent.setText("支付宝扣除" + mPercent + "%手续费 " + StringUtil.formatMoney(2,midFee) + "元");
                }
                if(usableBalance == money){ //全部提现
                    mBtnSubmit.setOnClickListener(WithdrawActivity.this);
                    mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
                } else if(total > usableBalance){
                    mBtnSubmit.setOnClickListener(null);
                    mBtnSubmit.setBackgroundResource(R.drawable.shape_cancel_btn);
                    ToastUtil.showShort(WithdrawActivity.this,"提现金额＋手续费不得大于可用余额");
                }else{
                    mBtnSubmit.setOnClickListener(WithdrawActivity.this);
                    mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
                }

            }
        });
    }


    AccountSet mbean;
    @Override
    public void showAccount(AccountSet bean) {
        mbean=bean ;
        mName = bean.getUserName();
        mAccount = bean.getUserAccount();
        setAccountTitle(bean.getUserAccount(), bean.getUserName());
    }


    private void setAccountTitle(String account, String name) {
        mTvAccount.setText(account + " (" + name + ")");
        mTvAddAccountLabel.setVisibility(View.GONE);
    }

    @Override
    public void showPercent(String percent,List<AccountSet> list) {
        mPercent = percent;
        mTvPercent.setText("支付宝扣除" + percent + "%手续费");
        if(list == null || list.size() <= 0){
            mTvAccount.setText("");
            mTvAddAccountLabel.setVisibility(View.VISIBLE);
            mTvCash.setTextColor(getResources().getColor(R.color.color_d5d5d5));
            mTvCash.setEnabled(false);
        }else {
            mTvCash.setTextColor(getResources().getColor(R.color.color_3582dd));
            mTvCash.setEnabled(true);
        }
    }

    @Override
    public void onSuccess(String message) {
        upUser();
    }

    private void upUser() {
        if (UserManager.getInstance().isLogin()) {
            MineApi.getInstance().getUserInfo(UserManager.getInstance().getToken()).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<User>>(new ApiCallBack<User>() {
                        @Override
                        public void onSuccess(User data) {
                            if (data != null) {
                                UserManager.getInstance().saveUser(data);
                                //UserManager.getInstance().updataUser(data);
                                setResult(RESULT_OK);
                                Toast.makeText(WithdrawActivity.this, "已提交提现申请", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            mBtnSubmit.setOnClickListener(WithdrawActivity.this);
                            mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
                            ToastUtil.showShort(WithdrawActivity.this, message);
                        }
                    }));
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

    @Override
    public void onError(String message) {
        ToastUtil.showShort(this,message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void setPresenter(WithdrawContract.Presenter presenter) {
        mPresenter = presenter;
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
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(this);
    }

    @OnClick({R.id.btn_submit, R.id.rl_account_area,R.id.tv_all_cash})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //申请提现
            case R.id.btn_submit:
                submit();
                break;

            //输入提现帐号
            case R.id.rl_account_area:
                if(mTvAddAccountLabel.getVisibility() == View.VISIBLE){
                    GotoUtil.goToActivityForResult(this, WithdrawInputActivity.class, SET_ACCOUNT);
                }else {
                    GotoUtil.goToActivity(this, WithdrawListActivity.class, SET_ACCOUNT,mbean);
                }
                break;

            //全部提现
            case R.id.tv_all_cash:
                if(!TextUtils.isEmpty(myMoney)){
                    Double aDouble = StringUtil.toDouble(mPercent) / 100;
                    money = new BigDecimal(myMoney).divide(new BigDecimal(aDouble + 1), 3, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                    mEtMoney.setText(money + "");

                }else {
                     ToastUtil.showShort(this,"你的余额为空，无法提现");
                }
                break;

        }
    }

    private void submit() {
        mBtnSubmit.setOnClickListener(null);
        mBtnSubmit.setBackgroundResource(R.drawable.shape_cancel_btn);
        if (!UserManager.getInstance().isLogin()) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        if (TextUtils.isEmpty(mEtMoney.getText())) {
            Toast.makeText(this, "提现金额不能为空", Toast.LENGTH_SHORT).show();
            mBtnSubmit.setOnClickListener(this);
            mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
            return;
        }

        if (money < 50) {
            Toast.makeText(this, "提现的金额要大于50元", Toast.LENGTH_SHORT).show();
            mBtnSubmit.setOnClickListener(this);
            mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
            return;
        }

        if (money > StringUtil.toDouble(myMoney)) {
            Toast.makeText(this, "提现的金额大于可用金额", Toast.LENGTH_SHORT).show();
            mBtnSubmit.setOnClickListener(this);
            mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
            return;
        }

        if (money + midFee > StringUtil.toDouble(myMoney)) {
            Toast.makeText(this, "提现金额＋手续费不得大于可用余额", Toast.LENGTH_SHORT).show();
            mBtnSubmit.setOnClickListener(this);
            mBtnSubmit.setBackgroundResource(R.drawable.btn_blue_selector);
            return;
        }

        if (mName == null) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAccount == null) {
            Toast.makeText(this, "帐号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
//        Double aDouble = Double.valueOf(mPercent);
//        double midfee  = money * aDouble / 100;
        //Double midfreeD = new BigDecimal(midfee).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("money", money + "");
        map.put("withdrawCashType", "2");
        map.put("userAccount", mAccount);
        map.put("userName", mName);
        map.put("midFee", midFee + "");

        mPresenter.submit(map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_ACCOUNT && resultCode == RESULT_OK) {
            if(data.getBooleanExtra("refresh",false)){
                mPresenter.getAccountSetInfo();
            }else {
                mName = data.getStringExtra("name");
                mAccount = data.getStringExtra("account");
                setAccountTitle(mAccount, mName);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
