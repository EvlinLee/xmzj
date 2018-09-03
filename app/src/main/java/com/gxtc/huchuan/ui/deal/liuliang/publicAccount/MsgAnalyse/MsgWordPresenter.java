package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse;

import com.gxtc.huchuan.data.deal.MsgRepository;

/**
 * Created by Steven on 17/3/1.
 */

public class MsgWordPresenter implements MsgContract.WordPresenter {

    private MsgContract.WordView  mView;
    private MsgContract.Source    mData;

    public MsgWordPresenter(MsgContract.WordView mView) {
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
