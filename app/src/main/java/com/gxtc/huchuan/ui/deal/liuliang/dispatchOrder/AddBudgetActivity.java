package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;

/**
 * 增加预算
 */
public class AddBudgetActivity extends BaseTitleActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_add_budget));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;
        }
    }
}
