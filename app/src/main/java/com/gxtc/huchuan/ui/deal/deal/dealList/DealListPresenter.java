package com.gxtc.huchuan.ui.deal.deal.dealList;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.data.deal.DealListRepository;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DealListPresenter implements DealListContract.Presenter {

    private DealListContract.View   mView;
    private DealListContract.Source mData;

    private int start = 0;

    public DealListPresenter(DealListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealListRepository();
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void getData(final boolean isRefresh, int typeId, List<DealTypeBean> subsMap,
            List<DealTypeBean> udefs) {
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("typeId", typeId + "");
        map = fillParam(map,subsMap,udefs);

        mData.getData(map, new ApiCallBack<List<DealListBean>>() {

            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null)   return;

                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void getType(int id) {
        Subscription sub = Observable.concat(DealApi.getInstance().getDealSubType(id),
                DealApi.getInstance().getUdefType(id)).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new ApiObserver<ApiResponseBean<List<DealTypeBean>>>(new ApiCallBack() {

                    List<DealTypeBean> subs;
                    List<DealTypeBean> udefs;

                    @Override
                    public void onCompleted() {
                        if (subs != null && udefs != null) {
                            mView.showType(subs, udefs);
                        }
                    }

                    @Override
                    public void onSuccess(Object data) {
                        List<DealTypeBean> temp;
                        if (data instanceof List) {
                            temp = (List<DealTypeBean>) data;
                            if (subs == null) {
                                subs = temp;
                            } else {
                                udefs = temp;
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView == null)   return;
                        mView.showError(message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    @Override
    public void loadMrore(int typeId, List<DealTypeBean> subsMap, List<DealTypeBean> udefs) {
        start += 15;

        HashMap<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("typeId", typeId + "");
        map = fillParam(map,subsMap,udefs);

        mData.getData(map, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if (data == null || data.size() == 0) {
                    start -= 15;
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                start -= 15;
                mView.showError(message);
            }
        });
    }

    //过滤数据
    @Override
    public void filterData(int typeId, List<DealTypeBean> subsMap, List<DealTypeBean> udefs) {
        start = 0;
        HashMap<String, String> map = new HashMap<>();
        map.put("start", start + "");
        map.put("typeId", typeId + "");
        map = fillParam(map,subsMap,udefs);

        mData.getData(map, new ApiCallBack<List<DealListBean>>() {
            @Override
            public void onSuccess(List<DealListBean> data) {
                if(mView == null)   return;
                if (data == null || data.size() == 0) {
                    mView.showRefreshFinish(new ArrayList<DealListBean>());
                    return;
                }
                mView.showRefreshFinish(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void getPlateDescripte(String tradeTypeId) {
        mData.getPlateDescripte(tradeTypeId, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showPlateDescripte(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    private HashMap<String, String> fillParam(HashMap<String, String> map, List<DealTypeBean> subsMap, List<DealTypeBean> udefs) {
        if (subsMap != null && udefs != null) {

            //子类型ID。多个用逗号拼接。
            StringBuffer sb = new StringBuffer();

            if(subsMap != null && subsMap.size() > 0){
                for (DealTypeBean.TypesBean typeBean : subsMap.get(0).getTypes()) {
                    if(typeBean.isSelect()){
                        sb.append(typeBean.getCode()).append(",");
                    }
                }
            }
            if(sb.length() != 0){
                sb.delete(sb.length() - 1,sb.length());
                map.put("typeSonId",sb.toString());
            }

            //查询条件(二维数组json字符串，格式：字段名，查询条件，值) parameter=[["udef1","like","www"],["udef2",">","300"]]。
            ArrayList<ArrayList<String>> list2 = new ArrayList<>();
            for(DealTypeBean typeBean : udefs){

                //0:编辑框，1：城市，2：时间，3，下拉，4，多选，5，单选
                int type = typeBean.getUdfType();
                if(type == 0 || type == 1 || type == 2){
                    String content = typeBean.getContent();
                    if (!TextUtils.isEmpty(content)) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(typeBean.getTradeField());
                        list.add(typeBean.getChoice());
                        list.add(content);
                        list2.add(list);
                    }
                }

                if(type == 4 || type == 5){
                    for(DealTypeBean.TypesBean bean : typeBean.getTypes()){
                        if(bean.isSelect()){
                            ArrayList<String> list = new ArrayList<>();
                            String code = bean.getCode().split(",")[0];
                            list.add(code);
                            list.add(bean.getChoice());
                            list.add(bean.getTitle());
                            list2.add(list);
                        }
                    }
                }
            }
            if(list2.size() != 0){
                String param = GsonUtil.objectToJson(list2, new TypeToken<ArrayList<ArrayList<String>>>(){}.getType());
                map.put("parameter",param);
            }

        }
        return map;
    }
}
