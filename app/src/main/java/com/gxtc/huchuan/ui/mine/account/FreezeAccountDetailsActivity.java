package com.gxtc.huchuan.ui.mine.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;

import butterknife.BindView;

/**
 * Created by sjr on 2017/3/23.
 * 冻结账单详情
 */

public class FreezeAccountDetailsActivity extends BaseTitleActivity {

    @BindView(R.id.tv_freeze_account_details_title)       TextView tvFreezeAccountDetailsTitle;
    @BindView(R.id.tv_freeze_account_details_id)          TextView tvFreezeAccountDetailsId;
    @BindView(R.id.tv_freeze_account_details_create_time) TextView tvFreezeAccountDetailsCreateTime;
    @BindView(R.id.tv_freeze_account_details_deal_type)   TextView tvFreezeAccountDetailsDealType;
    @BindView(R.id.tv_freeze_account_details_balance)     TextView tvFreezeAccountDetailsBalance;
    @BindView(R.id.tv_freeze_account_details_canuse)      TextView tvFreezeAccountDetailsCanuse;
    @BindView(R.id.tv_freeze_account_flat_fee)            TextView tvFreezeAccountDetailsFlatFee;
    @BindView(R.id.tv_name)                               TextView tvName;

    private FreezeAccountBean mBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_account_details);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreezeAccountDetailsActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_wjs_details));
    }

    @Override
    public void initData() {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = getIntent();
            mBean = (FreezeAccountBean) intent.getSerializableExtra("freeze_account");
            if (mBean != null) {
                tvName.setText(mBean.getOrderInfo().getFbUserName());

                //课程订单
                if ("chatSignup".equals(mBean.getOrderType())) {
                    tvFreezeAccountDetailsTitle.setText(mBean.getOrderInfo().getChatInfoTitle());
                    tvFreezeAccountDetailsDealType.setText("课程订单");
                    tvFreezeAccountDetailsId.setText(mBean.getOrderInfo().getOrderId());
                    tvFreezeAccountDetailsCreateTime.setText(
                            DateUtil.stampToDate(mBean.getOrderInfo().getCreatetime()));
                    tvFreezeAccountDetailsFlatFee.setText("￥" + mBean.getOrderInfo().getMidFee());

                //购买的系列课订单
                } else if ("chatSeries".equals(mBean.getOrderType())) {
                    tvFreezeAccountDetailsTitle.setText(mBean.getOrderInfo().getChatSeriesName());
                    tvFreezeAccountDetailsDealType.setText("系列课订单");
                    tvFreezeAccountDetailsId.setText(mBean.getOrderInfo().getOrderId());
                    tvFreezeAccountDetailsCreateTime.setText(
                            DateUtil.stampToDate(mBean.getOrderInfo().getCreatetime()));
                    tvFreezeAccountDetailsFlatFee.setText("￥" + mBean.getOrderInfo().getMidFee());

                //交易
                } else if ("tradeOrder".equals(mBean.getOrderType())) {
                    tvFreezeAccountDetailsTitle.setText(mBean.getOrderInfo().getTradeInfoTitle());
                    tvFreezeAccountDetailsDealType.setText("交易订单");
                    tvFreezeAccountDetailsId.setText(mBean.getOrderInfo().getOrderId());
                    tvFreezeAccountDetailsCreateTime.setText(DateUtil.stampToDate(
                            String.valueOf(mBean.getOrderInfo().getCreateTime())));
                    tvFreezeAccountDetailsFlatFee.setText("￥" + mBean.getOrderInfo().getDbFee());

                } else if ("groupJoin".equals(mBean.getOrderType())) {
                    tvFreezeAccountDetailsTitle.setText(mBean.getOrderInfo().getGroupName());
                    tvFreezeAccountDetailsDealType.setText("圈子订单");
                    tvFreezeAccountDetailsId.setText(mBean.getOrderInfo().getOrderId());
                    tvFreezeAccountDetailsCreateTime.setText(DateUtil.stampToDate(
                            String.valueOf(mBean.getOrderInfo().getCreateTime())));
                    tvFreezeAccountDetailsFlatFee.setText("￥" + mBean.getOrderInfo().getMidFee());

                } else if ("invite".equals(mBean.getOrderType())) {
                    tvFreezeAccountDetailsTitle.setText(mBean.getOrderInfo().getTitle());
                    tvFreezeAccountDetailsDealType.setText("分享佣金");
                    tvFreezeAccountDetailsId.setText(mBean.getOrderInfo().getOrderId() + "");
                    tvFreezeAccountDetailsCreateTime.setText(DateUtil.stampToDate(
                            String.valueOf(mBean.getOrderInfo().getCreateTime())));
                    tvFreezeAccountDetailsFlatFee.setText("无");
                }
            }
        } else
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void initListener() {
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBean != null){
                    PersonalInfoActivity.startActivity(FreezeAccountDetailsActivity.this,mBean.getOrderInfo().getUserCode());
                }
            }
        });
    }
}
