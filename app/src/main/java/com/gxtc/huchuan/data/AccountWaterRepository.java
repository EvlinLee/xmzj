package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.account.AccountWaterContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 账单流水
 */

public class AccountWaterRepository extends BaseRepository implements AccountWaterContract.Source {


    @Override
    public void getData(int start, ApiCallBack<List<AccountWaterBean>> callBack) {
        if (UserManager.getInstance().isLogin()) {
            Subscription sub = MineApi.getInstance().userStreamList(UserManager.getInstance().getToken(),
                    String.valueOf(start),null,"",null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<AccountWaterBean>>>(callBack));
            addSub(sub);

        }

    }
}
