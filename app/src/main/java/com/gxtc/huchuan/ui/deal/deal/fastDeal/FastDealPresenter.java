package com.gxtc.huchuan.ui.deal.deal.fastDeal;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.FastDealBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FastDealPresenter implements FastDealContract.Presenter {

    private FastDealContract.View mView;
    private DealSource mData;

    public FastDealPresenter(FastDealContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void getAddressList(String token) {
        mView.showLoad();
        mData.getAddress(token, new ApiCallBack<List<AddressBean>>() {
            @Override
            public void onSuccess(List<AddressBean> data) {
                if(mView == null)   return;

                List<AddressBean> beans = data;
                if(beans != null && beans.size() != 0){
                    mView.showAddress(beans);
                }else{
                    mView.showEmpty();
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getOrderData(String token, int id) {
        mView.showLoad();
        mData.getFastDetailed(token, id, new ApiCallBack<FastDealBean>() {
            @Override
            public void onSuccess(FastDealBean data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showOrderData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void submitOrder(HashMap<String, String> map) {
        mView.showLoad();
        Subscription sub = DealApi.getInstance().submitOrder(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<GoodsDetailedBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(mView == null)   return;
                        GoodsDetailedBean bean = (GoodsDetailedBean) data;
                        mView.showLoadFinish();
                        mView.showSubmitSuccess(bean.getId());
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView == null)   return;
                        mView.showLoadFinish();
                        mView.showSubmitFailed(message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    /**
    *计算折扣之后的价格
    */
    @Override
    public double computeTotal(double money, double dbFree, double discount,boolean isAdd) {
        BigDecimal moneyB = new BigDecimal(money);
        BigDecimal danbaoB = new BigDecimal(dbFree);
        BigDecimal zhekouB = new BigDecimal(discount);
        //计算总价
        double total;
        if(isAdd){  //买家承担担保费
            total = moneyB.add(danbaoB).subtract(zhekouB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else{    //卖家承担担保费
//            total = moneyB.subtract(zhekouB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            total = moneyB.subtract(danbaoB).add(zhekouB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return total;
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
