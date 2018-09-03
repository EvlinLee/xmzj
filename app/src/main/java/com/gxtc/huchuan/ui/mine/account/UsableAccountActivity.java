package com.gxtc.huchuan.ui.mine.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
import com.gxtc.huchuan.ui.mine.incomedetail.InComeDetailNewActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.recharge.RechargeActivity;
import com.gxtc.huchuan.ui.mine.withdraw.WithdrawActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.StringUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/3/23.
 * 可用余额页面
 */

public class UsableAccountActivity extends BaseTitleActivity {
    private static final String TAG = "UsableAccountActivity";
    private static final int RECHARGE_REQUEST = 1 << 3;
    private static final int WITHDRAW_REQUEST = 1 << 4;

    @BindView(R.id.tv_freeze_account_details_create_time)
    TextView tvCreateTime;
    @BindView(R.id.ll_usable_recharge)
    LinearLayout llUsableRecharge;
    @BindView(R.id.ll_usable_withdraw)
    LinearLayout llUsableWithdraw;
    @BindView(R.id.ll_usable_rmb)
    LinearLayout llUsableRmb;
    @BindView(R.id.tv_usable)
    TextView tvUsable;
    @BindView(R.id.ll_usable_account_root)
    RelativeLayout llUsableAccountRoot;
    @BindView(R.id.tv_frozen_amount)
    TextView mTvFrozenAmount;
    @BindView(R.id.tv_take_money)
    TextView tvTakeMoney;

    private int requestCode = 1688;
    private VertifanceFlowDialog mVertifanceFlowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usable_account);
    }

    @Override
    public void initData() {
        initHeadView();
        usableAccount();
    }

    private void initHeadView() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.navigation_icon_back_white);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) getResources().getDimension(R.dimen.margin_medium);
        lp.topMargin = (int) (getStatusBarHeight() + getResources().getDimension(R.dimen.margin_middle));
        imageView.setPadding(10, 10, 10, 10);
        imageView.setLayoutParams(lp);
        llUsableAccountRoot.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsableAccountActivity.this.finish();
            }
        });

    }

    /**
     * 余额
     */
    private void usableAccount() {
        //理论上来说能进到这里就已经是登录状态了，下面这个方法是为了处理某些可能发生的极端情况
        if (UserManager.getInstance().isLogin()) {
            String token = UserManager.getInstance().getToken();
            Subscription sub = MineApi.getInstance().getUserInfo(token).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<User>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            User user = (User) data;
                            UserManager.getInstance().saveUser(user);
                            String myMoney = UserManager.getInstance().getUser().getUsableBalance();
                            String amount = UserManager.getInstance().getUser().getFrozenBalance();
                            String takeMoney = UserManager.getInstance().getUser().getDeposit();
                            tvUsable.setText(StringUtil.formatMoney(2, myMoney));
                            mTvFrozenAmount.setText(StringUtil.formatMoney(2, amount));
                            tvTakeMoney.setText(StringUtil.formatMoney(2, takeMoney));
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LogUtil.i("redPacketIssued getUserInfo  : " + message);
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this, sub);
        } else {
            startActivityForResult(new Intent(this, LoginAndRegisteActivity.class), requestCode);
        }
    }

    @OnClick({R.id.ll_usable_recharge,
            R.id.ll_usable_withdraw,
            R.id.ll_usable_rmb,
            R.id.rl_frozen_amount,
            R.id.tv_income_detail,
            R.id.take_money_record})
    public void onClick(View view) {
        switch (view.getId()) {
            //充值
            case R.id.ll_usable_recharge:
                GotoUtil.goToActivityForResult(this, RechargeActivity.class, RECHARGE_REQUEST);
                break;

            //提现
            case R.id.ll_usable_withdraw:
                if ("0".equals(UserManager.getInstance().getIsRealAudit()))
                    showDialog();
                else if ("1".equals(UserManager.getInstance().getIsRealAudit()))
                    GotoUtil.goToActivityForResult(this, WithdrawActivity.class, WITHDRAW_REQUEST);
                else
                    showVertifanceDialog();
                break;

            //账户流水
            case R.id.ll_usable_rmb:
                GotoUtil.goToActivity(this, AccountWaterActivity.class);
                break;

            //待结算金额
            case R.id.rl_frozen_amount:
                GotoUtil.goToActivity(this, FreezeAccountActivity.class);
                break;

            case R.id.take_money_record:
                GotoUtil.goToActivity(this, WithdrawRecordActivity.class);
                break;

            case R.id.tv_income_detail:
                GotoUtil.goToActivity(this, InComeDetailNewActivity.class);
                break;
        }
    }

    private void showDialog() {
        DialogUtil.VerificationDialog(this,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.btn_issue) {
                            GotoUtil.goToActivityForResult(UsableAccountActivity.this, VertifanceActivity.class, 100);
                        }
                    }
                });
    }

    private void showVertifanceDialog() {
        if (mVertifanceFlowDialog == null) {
            mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("3".equals(UserManager.getInstance().getIsRealAudit())) {
                    GotoUtil.goToActivity(UsableAccountActivity.this, VertifanceActivity.class);
                }
                mVertifanceFlowDialog.dismiss();
            }
        });
        String status = UserManager.getInstance().getIsRealAudit();
        switch (status) {
            case "2":
                mVertifanceFlowDialog.setFlowStatus("系统正在实名认证审核中，请耐心等待");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case "3":
                mVertifanceFlowDialog.setFlowStatus("审核不通过");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
                break;
        }
    }

    //先留着，暂时没有做未审核提现列表里
    public void checkCicle(final MineCircleBean mineCircleBean) {
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(), UserManager.getInstance().getUserCode(), Constant.STATUE_LINKTYPE_CIRCLE, new ApiCallBack<CheckBean>() {

            @Override
            public void onSuccess(CheckBean data) {
                if (data == null) return;
                switch (mineCircleBean.getIsShow()) {
                    case 2:
                        mVertifanceFlowDialog.setFlowStatus("审核不通过:" + data.getContent());
                        break;
                    case 3:
                        mVertifanceFlowDialog.setFlowStatus("审核失败:" + data.getContent());//审核失败
                        break;
                }
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(UsableAccountActivity.this, message);
            }
        }).addTask(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            tvUsable.setText(UserManager.getInstance().getUser().getUsableBalance());
        }
        if (requestCode == RECHARGE_REQUEST && resultCode == RESULT_OK) {
            tvUsable.setText(UserManager.getInstance().getUser().getUsableBalance());
            return;
        }
        if (requestCode == WITHDRAW_REQUEST && resultCode == RESULT_OK) {
            tvUsable.setText(UserManager.getInstance().getUser().getUsableBalance());
            mTvFrozenAmount.setText(UserManager.getInstance().getUser().getFrozenBalance());
            tvTakeMoney.setText(UserManager.getInstance().getUser().getDeposit());
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
