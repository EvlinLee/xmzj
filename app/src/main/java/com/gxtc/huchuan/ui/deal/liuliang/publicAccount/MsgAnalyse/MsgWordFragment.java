package com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.R;

/**
 * 消息分析－消息关键词
 * Created by Steven on 17/2/23.
 */

public class MsgWordFragment extends BaseTitleFragment implements MsgContract.WordView{

    private MsgContract.WordPresenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_msg_word,container,false);
        return view;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        new MsgWordPresenter(this);
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void setPresenter(MsgContract.WordPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
