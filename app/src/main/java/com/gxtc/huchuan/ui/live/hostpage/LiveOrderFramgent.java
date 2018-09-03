package com.gxtc.huchuan.ui.live.hostpage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveManageOrderAdapter;
import com.gxtc.huchuan.adapter.LiveMemberManagerAdapter;
import com.gxtc.huchuan.adapter.SeriesAndTopicRecordAdapter;
import com.gxtc.huchuan.bean.ClassOrderBean;
import com.gxtc.huchuan.bean.ClassOrderHeadBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.mine.classroom.classorderdetail.ClassOrderDetail;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.PurchaseRecordContract;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.PurchaseRecordPresenter;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * 来自 苏修伟 on 2018/4/23 .
 * 课程管理 -> 课程订单
 */

public class LiveOrderFramgent extends BaseTitleFragment{
    @BindView(R.id.recyclerview) RecyclerView mRcList;
    @BindView(R.id.swipeLayout)  SwipeRefreshLayout swipeRefreshLayout;

    private LiveManageOrderAdapter   adapter;

    private TextView tvMonet;
    private TextView tvCount;
    private TextView tvNote;

    private String chatRoomId;
    private int start = 0;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_swipe_recyclerview, container, false);
        return view;
    }
    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getArguments();
        chatRoomId = bundle.getString("RoomId");
        initRecyclerView();
    }

    @Override
    public void initListener() {
        super.initListener();
        swipeRefreshLayout.setEnabled(false);
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                start += 15;
                getlistdata(true);
            }
        });

    }

    private void initRecyclerView() {
        View headView = LayoutInflater.from(getContext()).inflate( R.layout.head_class_order, null);
        tvMonet = headView.findViewById(R.id.tv_money);
        tvCount = headView.findViewById(R.id.income_count);
        tvNote = headView.findViewById(R.id.income_note);

        mRcList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        mRcList.addHeadView(headView);
        adapter = new LiveManageOrderAdapter(getContext(), new ArrayList<ClassOrderBean>(), R.layout.item_class_order);
        mRcList.setAdapter(adapter);
        hideContentView();
        getHeaddata();
        getlistdata(false);
    }

    private void setHeadViewData(ClassOrderHeadBean bean){
        tvMonet .setText(StringUtil.formatMoney(2,bean.getRealIncome())+"元");
        tvCount .setText("共有"+bean.getCount()+"笔");
        tvNote .setText("总收入");
    }

    private void getHeaddata(){
        HashMap map = new HashMap<String, String>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("chatRoomId",chatRoomId+"");

        Subscription sub = LiveApi.getInstance().getHeadLiveManageOrderData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ClassOrderHeadBean>>>(new
        ApiCallBack<List<ClassOrderHeadBean>> () {
            @Override
            public void onSuccess(List<ClassOrderHeadBean> data) {
                if(data != null && data.size()>0)
                    setHeadViewData(data.get(0));
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(), message);
            }


        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void getlistdata(final Boolean isLoadMore) {

        if (!isLoadMore) {
            getBaseLoadingView().showLoading();
        }
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("token",UserManager.getInstance().getToken());
        map.put("chatRoomId",chatRoomId+"");
        map.put("start",start + "");
        map.put("pageSize","15");

        Subscription sub = LiveApi.getInstance().getLiveManageOrderList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ClassOrderBean>>>(new ApiCallBack<ArrayList<ClassOrderBean>>() {
                    @Override
                    public void onSuccess(ArrayList<ClassOrderBean> data) {
                        if(isLoadMore) {
                            if (data != null && data.size() > 0) {
                                mRcList.changeData(data, adapter);
                            } else {
                                mRcList.loadFinish();
                            }

                        }else{
                            getBaseLoadingView().hideLoading();
                            showContentView();
                            if (data != null && data.size() > 0) {

                                mRcList.notifyChangeData(data, adapter);
                            } else {
                                getBaseEmptyView().showEmptyContent();
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mRcList != null) {
                            if (!isLoadMore) {
                                getBaseLoadingView().hideLoading();
                            }else{
                                mRcList.loadFinish();
                            }

                        }
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
        }));
        RxTaskHelper.getInstance().addTask(this, sub);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);

    }
}
