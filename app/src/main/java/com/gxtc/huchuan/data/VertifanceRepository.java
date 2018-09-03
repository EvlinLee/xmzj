package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.pay.AccountBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceContract;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class VertifanceRepository extends BaseRepository implements VertifanceContract.Source {

    @Override
    public void Vertifance(ApiCallBack<Object>callBack,HashMap<String,String> map) {
        addSub(MineApi.getInstance().userRealAudit(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));

//        HashMap<String,String> map = new HashMap<>();
//        map.put("token",token);
//        map.put("token",token);
//        map.put("token",token);
//        map.put("token",token);
//        if(!TextUtils.isEmpty(token)){
//            map.put("token",token);
//        }
    }

    @Override
    public void VertifanceAccoun(ApiCallBack<List<AccountBean>> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().getAccountSet(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<AccountBean>>>(callBack)));
    }


}
