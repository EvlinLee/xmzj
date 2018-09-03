package com.gxtc.huchuan.data.pay;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.PayApi;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 16/12/4.
 */
public class PayImp extends BaseRepository implements Ipay {

    @Override
    public void getOrders(HashMap<String, String> map, ApiCallBack<OrdersResultBean> callBack) {
        addSub(PayApi.getInstance().getOrder(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<OrdersResultBean>>(callBack)));
    }

    //二次支付，目前只在软件商城使用
    @Override
    public void getRePayOrder(HashMap<String, String> map, ApiCallBack<OrdersResultBean> callBack) {
        addSub(PayApi.getInstance()
                .repay(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<OrdersResultBean>>(callBack)));
    }

    @Override
    public void validateOrder(String out_trade_no, ApiCallBack<Void> callBack) {
        addSub(PayApi.getInstance().validateOrder(out_trade_no).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack)));
    }
}
