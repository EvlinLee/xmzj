package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.huchuan.data.deal.TextRepository;

/**
 * Created by Steven on 17/2/28.
 */

public class TextSinglePresenter implements TextContract.SinglePresenter {

    private TextContract.SingleView mView;
    private TextContract.Source     mData;

    public TextSinglePresenter(TextContract.SingleView mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new TextRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }

    @Override
    public void changeTab(LinearLayout layout,int index) {
        if(layout != null && layout.getChildCount() > 0){
            for (int i = 0; i < layout.getChildCount(); i++) {
                View v = layout.getChildAt(i);
                if(i == index){
                    v.setSelected(true);
                }else{
                    v.setSelected(false);
                }
            }
        }
    }
}
