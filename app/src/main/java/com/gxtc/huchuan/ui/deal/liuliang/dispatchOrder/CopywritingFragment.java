package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CopywritingAdapter;
import com.gxtc.huchuan.bean.CopywritingBean;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Steven on 17/3/2.
 * 文案列表
 */
public class CopywritingFragment extends BaseLazyFragment implements CopywritingContract.View, View.OnClickListener {

    @BindView(R.id.rv_order)        RecyclerView        listView;
    @BindView(R.id.swiper_order)    SwipeRefreshLayout  refreshLayout;

    private CopywritingAdapter mAdapter;

    private CopywritingContract.Presenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_copywriting, container, false);
        return view;
    }

    @Override
    public void initData() {
        new CopywritingPresenter(this);
        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        listView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));


    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //取消推广
            case R.id.btn_cancel:
                break;

            //增加预算
            case R.id.btn_add:
                GotoUtil.goToActivity(getActivity(),AddBudgetActivity.class);
                break;

            //修改文案
            case R.id.btn_updata:
                GotoUtil.goToActivity(getActivity(), UpdataArticleActivity.class);
                break;

            //修改设置
            case R.id.btn_xiugai:
                GotoUtil.goToActivity(getActivity(),UpdataOrderActivity.class);
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoad();
    }

    @Override
    protected void lazyLoad() {
        mPresenter.getData();
    }

    @Override
    public void showData(List<CopywritingBean> datas) {
        if(listView != null){
            mAdapter = new CopywritingAdapter(getContext(),datas,R.layout.item_copywriting);
            mAdapter.setOnClickListener(this);
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        if(refreshLayout != null){
            refreshLayout.setRefreshing(false);
            getBaseLoadingView().hideLoading();
        }
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void setPresenter(CopywritingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();

    }



}
