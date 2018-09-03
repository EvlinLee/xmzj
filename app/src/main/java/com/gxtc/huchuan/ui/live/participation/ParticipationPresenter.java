package com.gxtc.huchuan.ui.live.participation;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/29.
 *
 */

public class ParticipationPresenter extends BaseRepository implements ParticipationContract.Presenter {


    private ParticipationContract.View mView;

    public ParticipationPresenter(ParticipationContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }


    private boolean loading = false;

    @Override
    public void getData(final boolean isRefresh, int start) {
        if (loading) {
            return;
        }
        if(start == 0){
            mView.showLoad();
        }
        loading = true;
        String token = UserManager.getInstance().getToken();
        addSub(
                LiveApi.getInstance()
                       .getChatUserRecordList(token, String.valueOf(start))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(new ApiCallBack<List<ChatInfosBean>>() {


                            @Override
                            public void onSuccess(List<ChatInfosBean> data) {
                                if(mView == null)   return;
                                mView.showLoadFinish();
                                if (isRefresh) {
                                    mView.showRefreshFinish(data);
                                } else {
                                    mView.showLoadMore(data);
                                }
                                loading = false;
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ErrorCodeUtil.handleErr(mView,errorCode,message);
                                loading = false;
                            }
                        })));

    }

    @Override
    public void getData(boolean isRefresh) {

    }


    @Override
    public void getChatInfos(int id) {
        mView.showLoad();
        String token = UserManager.getInstance().getToken();
        Subscription sub =
                LiveApi.getInstance().getChatInfosBean(token, id + "")
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new ApiObserver<ApiResponseBean<ChatInfosBean>>(new ApiCallBack<ChatInfosBean>() {
                           @Override
                           public void onSuccess(ChatInfosBean data) {
                               if(mView != null){
                                   mView.showLoadFinish();
                                   mView.showChatInfoSuccess(data);
                               }
                           }

                           @Override
                           public void onError(String errorCode, String message) {
                               if(mView != null){
                                   mView.showLoadFinish();
                                   mView.showError(message);
                               }
                           }
                       }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }
}
