package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.FlowListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.liuliang.FlowContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Steven on 17/2/13.
 */

public class FlowRepository extends BaseRepository implements FlowContract.Source {

    @Override
    public void getData(final int start, final ApiCallBack<List<FlowListBean>> callBack) {

        final List<FlowListBean> lists = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            lists.add(new FlowListBean());
        }

        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                        if(start == 2){
                            callBack.onSuccess(Collections.<FlowListBean>emptyList());      //模拟没有更多数据情况

                        }else{
                            callBack.onSuccess(lists);
                        }

                    }
                });
    }
}
