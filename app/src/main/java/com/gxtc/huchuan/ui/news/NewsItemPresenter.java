package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.data.NewsItemRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gxtc.huchuan.ui.MainActivity.STATISTICS;
import static com.gxtc.huchuan.ui.MainActivity.STATISTICS_EMTPT;

/**
 * Created by sjr on 2017/2/15.
 */

public class NewsItemPresenter implements NewsItemContract.Presenter {

    private int start = 0;
    private int flag;

    private NewsItemContract.Source mData;
    private NewsItemContract.View mView;

    private String id;
    private String clickType;
    private String userCode;

    private List<NewNewsBean> cacheDatas;

    public NewsItemPresenter(NewsItemContract.View mView, String id, int flag, String userCode,String clickType) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new NewsItemRepository();
        this.id = id;
        this.flag = flag;
        this.clickType = clickType;
        this.userCode = userCode;
        cacheDatas = new ArrayList<>();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        mData.destroy();
    }


    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            clickType = STATISTICS_EMTPT;
        } else {
            if(mView == null)return;
            clickType = STATISTICS;
            mView.showLoad();
        }

        if(cacheDatas.size() != 0 && !isRefresh){
            mView.showLoadFinish();
            mView.showData(cacheDatas);
            return;
        }



        mData.getData(start, id, flag, userCode,clickType, new ApiCallBack<List<NewNewsBean>>() {
            @Override
            public void onSuccess(List<NewNewsBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                    cacheDatas.clear();
                    cacheDatas.addAll(data);
                } else {
                    mView.showData(data);
                    cacheDatas.addAll(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        LogUtil.i("start  =======  " + start);
        mData.getData(start, id, flag, userCode,STATISTICS_EMTPT, new ApiCallBack<List<NewNewsBean>>() {

            @Override
            public void onSuccess(List<NewNewsBean> data) {
                if(mView == null)   return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
                cacheDatas.addAll(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showError(message);
            }
        });
    }


    /**
     * 屏蔽文章
     * @param newsId 文章Id  屏蔽或解除文章时候传
     * @param targetUserCode 被屏蔽人新媒号, 屏蔽或解除用户的时候传
     * @param type 1屏蔽文章 2屏蔽用户 解除屏蔽用户用userScren/deleteList.do接口
     * @param shieldType 操作类型， 0解除操作否则为屏蔽操作/屏蔽默认传个1
     */
    @Override
    public void shieldType(final String newsId, final String targetUserCode, int type, String shieldType) {
        String token = UserManager.getInstance().getToken();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("type", type + "");
        map.put("shieldType", "1");

        //屏蔽文章
        if(type == 1){
            map.put("newsId", newsId);

        //屏蔽用户
        }else{
            map.put("targetUserCode", targetUserCode);
        }

        mData.shieldType(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView != null ){
                    mView.showShieldSuccess(newsId, targetUserCode);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView != null){
                    mView.showShieldError(newsId, targetUserCode, message);
                }
            }
        });
    }
}
