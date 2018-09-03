package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse;

import com.gxtc.huchuan.data.deal.TextRepository;

/**
 * Created by Steven on 17/3/1.
 */

public class TextAllPresenter implements TextContract.AllPresenter {


    private TextContract.AllView    mView;
    private TextContract.Source     mData;


    public TextAllPresenter(TextContract.AllView mView) {
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
    }
}
