package com.gxtc.huchuan.ui.search;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.dao.SearchHistory;
import com.gxtc.huchuan.data.SearchRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/3/6.
 */

public class SearchPresenter implements SearchContract.Presenter{

    private SearchContract.View     mView;
    private SearchContract.Source   mData;


    public SearchPresenter(SearchContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new SearchRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        mData.destroy();
    }

    @Override
    public void getSearch(HashMap<String, String> map) {
        if(mView == null)return;
        mView.showLoad();
        mData.getSearch(map, new ApiCallBack<List<SearchBean>>() {
            @Override
            public void onSuccess(List<SearchBean> data) {
                if(mView == null)return;
                mView.showLoadFinish();
                if (data.size() == 0){
                    mView.showEmpty();
                }else {
                    mView.showSearchResult(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getHistory() {
        Observable.just(mData.getHistory())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<SearchHistory>>() {
                    @Override
                    public void call(List<SearchHistory> ziweiUserBeen) {
                        if(mView == null)return;
                        mView.showHistory(ziweiUserBeen);
                    }
                });
    }

    @Override
    public void saveHistory(final SearchHistory bean) {
        Observable.just(mData.saveHistory(bean))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(mView == null)return;
                        if(aLong != -1){
                            List<SearchHistory> beans = new ArrayList<SearchHistory>();
                            beans.add(bean);
                            mView.showHistory(beans);
                        }
                    }
                });
    }

    @Override
    public void delHistory() {
        Observable.just(mData.delHistory())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mView == null)return;
                        mView.showHistory(null);
                    }
                });
    }

}
