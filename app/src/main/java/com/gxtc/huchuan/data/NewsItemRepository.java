package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.news.NewsItemContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.ui.MainActivity.STATISTICS;

/**
 * Created by sjr on 2017/2/15.
 */

public class NewsItemRepository extends BaseRepository implements NewsItemContract.Source {


    @Override
    public void getData(int start, String id, int flag, String userCode,String clickType, final ApiCallBack<List<NewNewsBean>> callBack) {
        if (1 == flag) {
            String token = "";
            if (UserManager.getInstance().isLogin()) {
                token = UserManager.getInstance().getToken();
            }

            //如果是首页那么需要加推荐的内容，
            if("-1".equals(id)){
                Subscription sub = AllApi.getInstance().getNewsList(token, id, String.valueOf(start), "15", 0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<NewNewsBean>>>(callBack));
                addSub(sub);

            //其他分类的是不需要有推荐内容的
            }else {
                Subscription sub = AllApi.getInstance().getNews(token, id, String.valueOf(start),clickType, "0")
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(
                                                 new ApiCallBack<List<NewsBean>>() {
                                                     @Override
                                                     public void onSuccess(List<NewsBean> data) {
                                                         List<NewNewsBean> temp = new ArrayList<>();
                                                         for(NewsBean bean: data){
                                                             NewNewsBean newBean = new NewNewsBean();
                                                             newBean.setType("news");

                                                             NewNewsBean.DataBean dataBean = new NewNewsBean.DataBean();
                                                             dataBean.setNewsData(bean);
                                                             newBean.setData(dataBean);
                                                             temp.add(newBean);
                                                         }

                                                         callBack.onSuccess(temp);
                                                     }

                                                     @Override
                                                     public void onError(String errorCode, String message) {
                                                         callBack.onError(errorCode, message);
                                                     }
                                                 }));
                addSub(sub);
            }

        } else if (2 == flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userCode", userCode);
            map.put("start", String.valueOf(start));
            Subscription sub = AllApi.getInstance().getNewsInMine(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack));
            addSub(sub);
        }

    }

    @Override
    public void shieldType(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        addSub(
            AllApi.getInstance()
                  .shieldArticle(map)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}
