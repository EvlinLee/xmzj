package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse;

import com.gxtc.huchuan.data.deal.MsgRepository;

/**
 * Created by Steven on 17/3/1.
 */

public class MsgAnalysePresenter implements MsgContract.AnalysePresenter {

    private MsgContract.AnalyseView mView;
    private MsgContract.Source      mData;

    public MsgAnalysePresenter(MsgContract.AnalyseView mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MsgRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }
}
