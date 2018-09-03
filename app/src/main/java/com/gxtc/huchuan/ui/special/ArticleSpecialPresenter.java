package com.gxtc.huchuan.ui.special;

import android.text.TextUtils;

import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.ArticleSpecialBean;
import com.gxtc.huchuan.data.SpecialRepository;
import com.gxtc.huchuan.data.SpecialSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 苏修伟 on 2018/5/12.
 */
public class ArticleSpecialPresenter implements ArticleSpecialContract.Presenter  {

    private ArticleSpecialContract.View view;
    private SpecialSource source;
    private int start = 0;

    public ArticleSpecialPresenter(ArticleSpecialContract.View view){
        this.view = view;
        view.setPresenter(this);
        source = new SpecialRepository();
    }

    @Override
    public void getArticleSpeialList(boolean Refresh, Map<String, String> map) {
        if(Refresh){
            start = 0;
        }else {
            start = start + 15;
        }
        map.put("start",String.valueOf(start));
        source.getSpecialType(map, new ApiCallBack<List<ArticleSpecialBean>>() {
            @Override
            public void onSuccess(List<ArticleSpecialBean> data) {
                if(data == null || data.size() == 0){
                    if(start == 0){
                        view.showEmpty();
                    }else {
                        view.showLoadFinish();
                    }
                    return;
                }
                view.showData(data);

            }

            @Override
            public void onError(String errorCode, String message) {
                view.showError(message);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        source.destroy();
    }
}
