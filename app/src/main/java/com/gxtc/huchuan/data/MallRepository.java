package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MallDetailBean;
import com.gxtc.huchuan.bean.MallShopCartBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.MallApi;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MallRepository extends BaseRepository implements MallSource {

    @Override
    public void getAdvertise(ApiCallBack<List<MallBean>> mCallBack) {
        addSub(MallApi.getInstance().listByCarouse(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<MallBean>>>(mCallBack)));
    }

    @Override
    public void getTags(int start,ApiCallBack<List<MallBean>> mCallBack) {
        addSub(MallApi.getInstance().storeMenu(start,0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<MallBean>>>(mCallBack)));
    }

    @Override
    public void getLinesData(int start,ApiCallBack<List<MallBean>> mCallBack) {
        addSub(MallApi.getInstance().storeMenu(start,1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<MallBean>>>(mCallBack)));
    }

    @Override
    public void getGridData(int start,ApiCallBack<List<MallBean>> mCallBack) {
        addSub(MallApi.getInstance().storeMenu(start,2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<List<MallBean>>>(mCallBack)));
    }

    @Override
    public void getGoodsDetailed(String id, String token, ApiCallBack<MallDetailBean> callback) {
        addSub(MallApi.getInstance()
                      .getGoodsDetailed(id, token)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<MallDetailBean>>(callback)));
    }

    @Override
    public void addShopCart(@NotNull HashMap<String, String> map,
                            @NotNull ApiCallBack<Object> mCallBack) {
        addSub(MallApi.getInstance().addShopcart(map)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(mCallBack)));
    }

    @Override
    public void getShopCartList(String token, ApiCallBack<List<MallShopCartBean>> callback) {
        addSub(MallApi.getInstance()
                      .getShopCartList(token)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<List<MallShopCartBean>>>(callback)));
    }

    @Override
    public void removeGoods(String token, String id, ApiCallBack<Object> callback) {
        addSub(MallApi.getInstance()
                      .removeGoods(token,id)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(callback)));
    }

    @Override
    public void collectMall(Map<String, String> map, ApiCallBack<Object> callback) {
        addSub(AllApi.getInstance()
                     .saveCollection(map)
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribeOn(Schedulers.io())
                     .subscribe(new ApiObserver<ApiResponseBean<Object>>(callback)));
    }

    @Override
    public void getActivityData(int start, @NotNull ApiCallBack<List<MallBean>> mCallBack) {
        addSub(MallApi.getInstance().storeMenu(start,-1)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribeOn(Schedulers.io())
                      .subscribe(new ApiObserver<ApiResponseBean<List<MallBean>>>(mCallBack)));
    }
}
