package com.gxtc.huchuan.ui.circle.homePage.invitation;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleInviteRankAdpter;
import com.gxtc.huchuan.bean.CircleRankBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/27.
 * 圈子邀请-佣金排名
 * 苏修伟
 */

public class CircleRankFragment  extends BaseTitleFragment{

    @BindView(R.id.rv_rank) RecyclerView recyclerView;

    private CircleInviteRankAdpter madapter;

    private String id ;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_circle_rank,container,false);
    }

    @Override
    public void initData() {
        madapter = new CircleInviteRankAdpter(getContext(), new ArrayList<CircleRankBean>(), R.layout.fragment_circle_rank_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addFootView(R.layout.model_footview_loadmore);
        recyclerView.setAdapter(madapter);

        madapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        recyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getData(true);
            }
        });

        getData(false);
    }


    private void getData(final boolean isLoadMore){
       if(!isLoadMore){
           getBaseLoadingView().showLoading();
       }

       String token = UserManager.getInstance().getToken();
       int start = isLoadMore ? madapter.getItemCount() + 15 : 0;

       HashMap<String, String> map = new HashMap<>();
       map.put("token", token);
       map.put("groupId", id);
       map.put("start", start + "");

       Subscription sub =
               CircleApi.getInstance()
                        .getInviteTop(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<CircleRankBean>>>(new ApiCallBack<List<CircleRankBean>>() {
                           @Override
                           public void onSuccess(List<CircleRankBean> datas) {
                               if(recyclerView != null){
                                   if(isLoadMore){
                                       if(datas != null && datas.size() > 0){
                                           recyclerView.changeData(datas, madapter);
                                       }else{
                                           recyclerView.loadFinish();
                                       }

                                   }else{
                                       getBaseLoadingView().hideLoading();
                                       if(datas != null && datas.size() > 0){
                                           recyclerView.notifyChangeData(datas, madapter);
                                       }else{
                                           getBaseEmptyView().showEmptyContent();
                                       }
                                   }
                               }
                           }

                           @Override
                           public void onError(String errorCode, String message) {
                               if(recyclerView != null){
                                   if(!isLoadMore){
                                       getBaseLoadingView().hideLoading();
                                   }
                                   ToastUtil.showShort(getContext(), message);
                               }
                           }
                       }));

       RxTaskHelper.getInstance().addTask(this, sub);
   }

    @Override
    protected void onGetBundle(Bundle bundle) {
        id = bundle.getString("id");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
