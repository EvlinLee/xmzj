package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ProfitListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.liuliang.profit.ProfitContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Steven on 17/3/22.
 */

public class ProfitRepository extends BaseRepository implements ProfitContract.Source {

    @Override
    public void getData(int start, final ApiCallBack<List<ProfitListBean>> callBack) {
        Subscription sub = Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        List<ProfitListBean> list = new ArrayList<ProfitListBean>();
                        for(int i = 0 ; i< 15 ; i++){
                            list.add(new ProfitListBean());
                        }
                        callBack.onSuccess(list);
                    }
                });

        addSub(sub);
    }
}
