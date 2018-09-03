package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.bean.event.EventDustbinRecoverBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.mine.setting.dustbin.DustbinListContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class DustbinRepository extends BaseRepository implements DustbinListContract.Source {

    private  int type;
    @Override
    public void getData(String token, int start, ApiCallBack<List<DustbinListBean>> callBack) {
        addSub(AllApi.getInstance().getDustbinList(token, start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<DustbinListBean>>>(callBack)));
    }




    @Override
    public void HuiFuDustbin(String token,int id,int type,ApiCallBack<Object> callBack) {
        addSub(AllApi.getInstance().HuiFuDustbin(token,id,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}



