package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ArticleListAdapter;
import com.gxtc.huchuan.bean.ArticleListBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 历史文案列表
 */
public class ArticleListActivity extends BaseTitleActivity implements View.OnClickListener,ArticleListContract.View {

    @BindView(R.id.swipe_article)      SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.rl_article)         RecyclerView        listView;

    private ArticleListAdapter adapter;
    private ArticleListContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_select_article));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("保存",this);

        refreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        adapter = new ArticleListAdapter(this,new ArrayList<ArticleListBean>(),R.layout.item_article_list);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                break;
        }
    }

    @Override
    public void initData() {
        new ArticleListPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<ArticleListBean> datas) {
        listView.notifyChangeData(datas,adapter);
        listView.setAdapter(adapter);
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<ArticleListBean> datas) {
        listView.notifyChangeData(datas,adapter);
    }


    //加载更多
    @Override
    public void showLoadMore(List<ArticleListBean> datas) {
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
    public void setPresenter(ArticleListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
