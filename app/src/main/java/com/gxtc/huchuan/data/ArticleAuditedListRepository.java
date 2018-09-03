package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.mine.circle.article.ArticleAuditedListContract;
import com.gxtc.huchuan.ui.news.NewsItemContract;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 */

public class ArticleAuditedListRepository extends BaseRepository implements ArticleAuditedListContract.Source {

    @Override
    public void getData(int start, String id, int flag, String userCode, ApiCallBack<List<CircleNewsBean>> callBack) {
        if (1 == flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put("start", String.valueOf(start));
            map.put("groupId", id);
            Subscription sub = AllApi.getInstance().getNewsInCircle(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<CircleNewsBean>>>(callBack));
            addSub(sub);
        } else if (2 == flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userCode", userCode);
            map.put("start", String.valueOf(start));
            map.put("groupId", id);
            Subscription sub = AllApi.getInstance().getNewsInCircleAudit(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<CircleNewsBean>>>(callBack));
            addSub(sub);
        }
    }

}
