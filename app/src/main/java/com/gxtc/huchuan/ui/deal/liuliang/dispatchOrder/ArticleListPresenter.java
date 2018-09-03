package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import com.gxtc.huchuan.bean.ArticleListBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Steven on 17/3/21.
 *
 */

public class ArticleListPresenter implements ArticleListContract.Presenter {

    private ArticleListContract.View mView;

    private int start;

    public ArticleListPresenter(ArticleListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
    }


    @Override
    public void getData(boolean isRefresh) {
        if(isRefresh){
            start = 0;
        }else{
            mView.showLoad();
        }

        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(mView == null)   return;
                        List<ArticleListBean> list = new ArrayList<ArticleListBean>();
                        for(int i = 0 ; i < 20 ; i++){
                            list.add(new ArticleListBean());
                        }
                        mView.showLoadFinish();
                        mView.showData(list);

                    }
                });
    }

    @Override
    public void loadMrore() {

    }
}
