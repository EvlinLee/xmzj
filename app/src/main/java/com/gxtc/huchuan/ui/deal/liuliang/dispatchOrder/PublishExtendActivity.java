package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;

/**
 * 发布推广
 */
public class PublishExtendActivity extends BaseTitleActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_extend);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.title_publish_extend));
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
