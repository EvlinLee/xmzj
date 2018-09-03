package com.gxtc.huchuan.ui.mine.dealmanagement;

import android.os.Bundle;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.mine.deal.fastList.FastListActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.ui.mine.deal.issueList.IssueListActivity;
import com.gxtc.huchuan.ui.mine.deal.orderList.PurchaseListActivity;
import com.gxtc.huchuan.ui.mine.deal.refund.RefundListActivity;

import butterknife.OnClick;

/**
 * Describe:
 * Created by ALing on 2017/5/15 .
 */

public class DealManagementActivity extends BaseTitleActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_management);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_deal_management));
        getBaseHeadView().showBackButton(this);
    }

    @OnClick({R.id.tv_deal_post,
            R.id.tv_deal_management,
            R.id.tv_refund_list,
            R.id.tv_deal_issue,
            R.id.tv_deal_fast})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //我的帖子
            case R.id.tv_deal_post:
                GotoUtil.goToActivity(this,IssueListActivity.class);
                break;

            //交易管理
            case R.id.tv_deal_management:
                GotoUtil.goToActivity(this, PurchaseListActivity.class);
                break;

            //申请退款
            case R.id.tv_refund_list:
                GotoUtil.goToActivity(this, RefundListActivity.class);
                break;

            //发布帖子
            case R.id.tv_deal_issue:
                GotoUtil.goToActivity(this, IssueDealActivity.class);
                break;

            //快速交易
            case R.id.tv_deal_fast:
                GotoUtil.goToActivity(this, FastListActivity.class);
                break;
        }
    }
}
