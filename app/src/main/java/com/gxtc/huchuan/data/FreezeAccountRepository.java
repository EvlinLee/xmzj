package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.account.AccountWaterContract;
import com.gxtc.huchuan.ui.mine.account.FreezeAccountContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 未结算金额
 */

public class FreezeAccountRepository extends BaseRepository implements FreezeAccountContract.Source {


    @Override
    public void getData(int start, ApiCallBack<List<FreezeAccountBean>> callBack) {
        if (UserManager.getInstance().isLogin()) {
            Subscription sub = MineApi.getInstance().getNotSettleOrderList(UserManager.getInstance().getToken(),
                    String.valueOf(start))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<FreezeAccountBean>>>(callBack));
            addSub(sub);

        }

    }
}
