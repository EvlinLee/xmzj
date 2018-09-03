package com.gxtc.huchuan.ui.mine.focus;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 关注列表
 */

public class FocusRepository extends BaseRepository implements FocusContract.Source {

    @Override
    public void getData(final int start, String id,String groupChatId, final ApiCallBack<List<FocusBean>> callBack) {
        //肯定是登录情况才能走到这里，多个if保险而已
        Subscription sub = AllApi.getInstance().getFocus(UserManager.getInstance().getUserCode(),id,
                String.valueOf(start),groupChatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<FocusBean>>>(callBack));
        addSub(sub);

    }

}
