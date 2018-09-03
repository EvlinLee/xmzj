package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CopywritingBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder.CopywritingContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/3/2.
 */

public class CopywritingRepository extends BaseRepository implements CopywritingContract.Source {

    @Override
    public void getData(final ApiCallBack<List<CopywritingBean>> callBack) {
        Subscription sub = Observable.timer(800, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        List<CopywritingBean> datas = new ArrayList<CopywritingBean>();
                        datas.add(new CopywritingBean());
                        datas.add(new CopywritingBean());
                        datas.add(new CopywritingBean());
                        datas.add(new CopywritingBean());
                        callBack.onSuccess(datas);
                    }
                });
        addSub(sub);
    }
}
