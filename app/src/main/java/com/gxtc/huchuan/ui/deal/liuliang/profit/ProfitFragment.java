package com.gxtc.huchuan.ui.deal.liuliang.profit;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ProfitListAdapter;
import com.gxtc.huchuan.bean.ProfitListBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Steven on 17/3/21.
 * 接单盈利
 */
public class ProfitFragment extends BaseTitleFragment implements ProfitContract.View{

    @BindView(R.id.swipe_profit)           SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.rl_profit)              RecyclerView        listView;

    private ProfitListAdapter adapter;
    private ProfitContract.Presenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_profit,container,false);
        return view;
    }


    @Override
    public void initData() {
        adapter = new ProfitListAdapter(getContext(),new ArrayList<ProfitListBean>(),R.layout.item_list_profit);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL,false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        new ProfitPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<ProfitListBean> datas) {
        listView.notifyChangeData(datas,adapter);
        listView.setAdapter(adapter);
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<ProfitListBean> datas) {
        listView.notifyChangeData(datas,adapter);
    }


    //加载更多
    @Override
    public void showLoadMore(List<ProfitListBean> datas) {
        listView.changeData(datas,adapter);
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        refreshLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
        if(adapter != null){
            listView.notifyChangeData(new ArrayList(),adapter);
        }
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
    public void setPresenter(ProfitContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
