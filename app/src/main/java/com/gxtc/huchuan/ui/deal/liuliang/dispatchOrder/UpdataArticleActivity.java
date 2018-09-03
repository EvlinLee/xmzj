package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改文案页面
 */
public class UpdataArticleActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.btn_select_article)          RelativeLayout btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_article);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_updata_article));
        getBaseHeadView().showBackButton(this);
    }


    @OnClick({R.id.btn_select_article})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            //选择历史文案
            case R.id.btn_select_article:
                GotoUtil.goToActivity(this,ArticleListActivity.class);
                break;
        }
    }
}
