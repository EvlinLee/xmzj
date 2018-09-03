package com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.StatisticVisitorAdapter;
import com.gxtc.huchuan.adapter.VisitorAdapter;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.mine.visitor.VisitorContract;
import com.gxtc.huchuan.ui.mine.visitor.VisitorPresenter;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zzg on 2018/3/18 .
 */

public class StatisticVisitorFragment extends BaseTitleFragment implements StatisticVisitorContract.View{

    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.ll_visitor_count) View view;

    private StatisticVisitorAdapter adapter;
    private StatisticVisitorContract.Present mPresenter;
    private VisitorBean               mVisitorBean;
    private List<VisitorBean>         list;

    public int dateType ;
    public int checkType;
    public int groupId;
    public int start;

    @Override
    protected void onGetBundle(Bundle bundle) {
        super.onGetBundle(bundle);
        dateType = bundle.getInt("dateType");
        checkType = bundle.getInt("checkType");
        groupId = bundle.getInt("groupId");
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view  = inflater.inflate(R.layout.activity_visitor,container,false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        view.setVisibility(View.GONE);
        new StatisticVisitorPresenter(this);
        getData();

    }

    private void getData() {
        mPresenter.getData(UserManager.getInstance().getToken(),groupId,start,dateType,checkType,false);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0;
                mPresenter.getData(UserManager.getInstance().getToken(),groupId,start,dateType,checkType,true);;     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                start = start + 15;
                mPresenter.getData(UserManager.getInstance().getToken(),groupId,start,dateType,checkType,false);;
            }
        });

        list = new ArrayList<>();
        adapter = new StatisticVisitorAdapter(getContext(), list, R.layout.statistic_visitor_item_layout);
        mRcList.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mVisitorBean = adapter.getList().get(position);
                if(mVisitorBean.getCount() > 0){
                    StatisticVisitorDetailActivity.jumpToStatisticVisitorDetailActivity(getActivity(),groupId,checkType,mVisitorBean.getTimeSection());
                }else {
                    if(dateType == StatisticVisitorActivity.getTIME_OF_DAY()){
                        ToastUtil.showShort(MyApplication.getInstance(),"本日访客为零");
                    }
                    if(dateType == StatisticVisitorActivity.getTIME_OF_MOUTH()){
                        ToastUtil.showShort(MyApplication.getInstance(),"本月访客为零");
                    }
                }
            }
        });
    }




    @Override
    public void showData(List<VisitorBean> datas) {
        if(start == 0){
            mRcList.notifyChangeData(datas, adapter);
        }else {
            mRcList.changeData(datas, adapter);
        }
    }

    //下拉刷新
    @Override
    public void showRefreshFinish(List<VisitorBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas, adapter);
    }

    //加载更多
    @Override
    public void showLoadMore(List<VisitorBean> datas) {}

    //没有加载更多数据
    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        if(start == 0){
            getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
        }else {
            mRcList.loadFinish();
        }
    }

    @Override
    public void showReLoad() {
        getData();
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    public void setPresenter(StatisticVisitorContract.Present presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
