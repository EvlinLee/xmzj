package com.gxtc.huchuan.ui.mine.account;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.utils.DateUtil;

import org.jsoup.helper.DataUtil;

import butterknife.BindView;

/**
 * Created by sjr on 2017/4/20.
 * 流水详情
 */

public class AccountWaterDetailsActivity extends BaseTitleActivity {

    @BindView(R.id.tv_account_water_details_typenumber) TextView tvTypeNumber;
    @BindView(R.id.tv_account_water_details_money)      TextView tvMoney;
    @BindView(R.id.tv_account_water_details_type)       TextView tvType;
    @BindView(R.id.tv_account_water_details_time)       TextView tvTime;
    @BindView(R.id.tv_account_water_details_odd)        TextView tvOdd;
    @BindView(R.id.tv_account_water_details_mark)       TextView tvMark;
    @BindView(R.id.tv_source_layout)                      View tvSourceLayout;
    @BindView(R.id.tv_source)                              TextView tvSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_water_details);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountWaterDetailsActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_account_water_details));
    }

    @Override
    public void initData() {
        AccountWaterBean bean = (AccountWaterBean) getIntent().getSerializableExtra(
                "AccountWaterBean");
        if (bean != null) {
            if (bean.getStreamMoney().startsWith("-")) {
                tvTypeNumber.setText("出账金额");

                tvMoney.setTextColor(getResources().getColor(R.color.text_color_333));
                tvMoney.setText(bean.getStreamMoney().substring(1));

                tvType.setText("支出");
            } else {
                tvMoney.setTextColor(getResources().getColor(R.color.green_btn_nornal));

                tvTypeNumber.setText("入账金额");
                tvMoney.setText(bean.getStreamMoney());

                tvType.setText("收入");
            }

            tvTime.setText(DateUtil.stampToDate(bean.getCreateTime()));

            tvOdd.setText(bean.getOrderId());

            if("11".equals(bean.getType())){
                tvSourceLayout.setVisibility(View.GONE);
                tvMark.setText("自己提现");
            }else {
                tvSourceLayout.setVisibility(View.VISIBLE);
                tvSource.setText(bean.getBusinessName());
                tvMark.setText(bean.getRemark());
            }

        }
    }
}
