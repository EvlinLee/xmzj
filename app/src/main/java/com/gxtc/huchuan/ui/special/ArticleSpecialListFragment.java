package com.gxtc.huchuan.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ArticleSpecialAdapter;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.ArticleSpecialBean;
import com.gxtc.huchuan.ui.news.NewsWebActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 来自 苏修伟 on 2018/5/12.
 * 文章专题列表
 */
public class ArticleSpecialListFragment extends BaseLazyFragment implements ArticleSpecialAdapter.OnItemClickLisntener, ArticleSpecialContract.View {

    @BindView(R.id.swipelayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_article_list)
    RecyclerView mArticleList;

    private ArticleSpecialAdapter mAdapter;
    private ArticleSpecialContract.Presenter presenter;

    private String specialId;
    private String isPay;//是否支付0=未购买，1=已购买
    private Map<String, String> map;
    public boolean loadMore;

    @Override
    protected void lazyLoad() {
        if (presenter == null) {
            new ArticleSpecialPresenter(this);
        }
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        if (bundle == null) return;
        specialId = bundle.getString("specialId");
        isPay = bundle.getString("isPay");
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_special_article, container, false);
        return view;
    }

    @Override
    public void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMore = false;
                mArticleList.reLoadFinish();
                presenter.getArticleSpeialList(true, map);
            }
        });

        mArticleList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore = true;
                presenter.getArticleSpeialList(false, map);
            }
        });

    }

    @Override
    public void initData() {
        initRecyclerView();
        map = new HashMap<>();
        map.put("id", specialId);
        map.put("type", "1");//0=全部，1=文章，2=视频
        presenter.getArticleSpeialList(true, map);
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setColorSchemeColors(new int[]{
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4});
        mArticleList.setLoadMoreView(R.layout.model_footview_loadmore);
        mArticleList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ArticleSpecialAdapter(getContext(), new ArrayList<ArticleSpecialBean>(), R.layout.item_specia_article,isPay);
        mAdapter.setOnItemClickLisntener(this);
        mArticleList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
        ArticleSpecialBean bean = mAdapter.getList().get(position);
    }


    @Override
    public void showData(List<ArticleSpecialBean> datas) {
        if (!loadMore) {
            swipeRefreshLayout.setRefreshing(false);
            mArticleList.notifyChangeData(datas, mAdapter);
        } else {
            mArticleList.changeData(datas, mAdapter);
        }
    }

    @Override
    public void setPresenter(ArticleSpecialContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mArticleList.loadFinishNotView();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(MyApplication.getInstance(), info);
    }

    @Override
    public void showNetError() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
