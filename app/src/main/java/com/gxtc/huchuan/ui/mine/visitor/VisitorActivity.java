package com.gxtc.huchuan.ui.mine.visitor;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.VisitorAdapter;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:访客
 * Created by ALing on 2017/5/18 .
 */

public class VisitorActivity extends BaseTitleActivity implements VisitorContract.View,
        View.OnClickListener {

    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.tv_all_visitor_count)   TextView       mTvAllVisitorCount;
    @BindView(R.id.tv_today_visitor_count) TextView       mTvTodayVisitorCount;

    private VisitorAdapter adapter;
    private VisitorContract.Presenter mPresenter;
    private VisitorBean               mVisitorBean;
    private List<VisitorBean>         list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_visitor));
        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initData() {
        super.initData();
        new VisitorPresenter(this);
        getData();

    }

    private void getData() {
        mPresenter.getData(false);
        mPresenter.getUserBrowseCount();
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore();       //加载更多
            }
        });

        list = new ArrayList<>();
        adapter = new VisitorAdapter(this, list, new int[]{R.layout.item_visitor,R.layout.item_visitor1});
        mRcList.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerTypeAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mVisitorBean = adapter.getDatas().get(position);
                if (!mVisitorBean.isHead()){
                    PersonalInfoActivity.startActivity(VisitorActivity.this, mVisitorBean.getUserCode());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

        }
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<VisitorBean> datas) {
        //假设里面有好多Bean
        list = datas;
        dataDivideGroup(list);
        mRcList.notifyChangeData(list, adapter);
    }

    private void dataDivideGroup(List<VisitorBean> list) {
        //先排个序
        Collections.sort(list, new Comparator<VisitorBean>() {
            @Override
            public int compare(VisitorBean o1, VisitorBean o2) {
                if (Long.valueOf(o1.getBrowseTime()) > Long.valueOf(o2.getBrowseTime()))    return -1;
                if (Long.valueOf(o1.getBrowseTime()) < Long.valueOf(o2.getBrowseTime()))    return 1;
                return 0;
            }
        });
        String groupName = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) //获取第一次的取值
            {
                groupName = list.get(0).getDateStr();
                list.add(0, new VisitorBean(true, groupName)); //在前一个位置插入null
            }
            VisitorBean visitorBean = list.get(i);
            if (!visitorBean.getDateStr().equals(groupName)) {
                //一直迭代 如果发现不一样 //那就是下一个分组的日期
                groupName = visitorBean.getDateStr();
                list.add(i , new VisitorBean(true, groupName)); //在前一个位置插入null
            }
        }
        Log.d("tag", "showData: "+list.size() );
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<VisitorBean> datas) {
//        adapter = new VisitorAdapter(this, list, new int[]{R.layout.item_visitor,R.layout.item_visitor1});
//        mRcList.setAdapter(adapter);
        list.removeAll(list);
        list = datas;
        dataDivideGroup(list);
        mRcList.notifyChangeData(list, adapter);
    }

    @Override
    public void showUserBrowserCount(VisitorBean bean) {
        mTvAllVisitorCount.setText("总浏览量："+bean.getTotalCount());
        mTvTodayVisitorCount.setText("今日浏览量："+bean.getTodayCount());
    }


    //加载更多
    @Override
    public void showLoadMore(List<VisitorBean> datas) {
        list = datas;
        dataDivideGroup(list);
        mRcList.changeData(datas, adapter);
    }

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
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
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
    public void setPresenter(VisitorContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
