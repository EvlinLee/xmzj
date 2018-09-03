package com.gxtc.huchuan.ui.mine.recharge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.ui.pay.PayConstant;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/5/4.
 * 余额充值
 */

public class RechargeActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.account_balance) TextView mAccountBalance;
    @BindView(R.id.et_money)        EditText mEtMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
    }

    @Override
    public void initData() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle("充值");
        if (UserManager.getInstance().isLogin()) {
            mAccountBalance.setText("账户可用余额：" + UserManager.getInstance().getUser().getUsableBalance() + "元");
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        goPay();
    }

    public void goPay() {
        WindowUtil.closeInputMethod(this);
        if (TextUtils.isEmpty(mEtMoney.getText())) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        String s       = mEtMoney.getText().toString();
        Double aDouble = Double.valueOf(s);
        if (aDouble <= 0) {
            Toast.makeText(this, "充值金额不能为0", Toast.LENGTH_SHORT).show();
            return;
        }

        if(aDouble > 50000){
            Toast.makeText(this, "单次充值金额不能超过50000", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal moneyB = new BigDecimal(s);

        //计算总价
        double total = (moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
        OrdersRequestBean mRequestBean = new OrdersRequestBean();
        mRequestBean.setToken(UserManager.getInstance().getToken());
        mRequestBean.setTransType("UR");
        mRequestBean.setTotalPrice(total + "");
        mRequestBean.setExtra("");
        mRequestBean.setGoodsName("充值");
        GotoUtil.goToActivity(RechargeActivity.this, PayActivity.class, Constant.INTENT_PAY_RESULT, mRequestBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PayConstant.PAY_RESULT && requestCode == Constant.INTENT_PAY_RESULT) {
            Toast.makeText(this, "充值成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            if (UserManager.getInstance().isLogin() && mAccountBalance != null) {
                mAccountBalance.setText("账户可用余额：" + UserManager.getInstance().getUser().getUsableBalance() + "元");
                finish();
            } else {
                GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }
}
