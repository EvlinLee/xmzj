package com.gxtc.huchuan.ui.mine.personalhomepage.classroom;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.base.EmptyView;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PersonalTopicMoreAdapter;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalHomePageMoreContract;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalHomePageMorePresenter;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/4/20 .
 */

@SuppressLint("ValidFragment")
public class ClassRoomFragment extends BaseTitleFragment implements
        PersonalHomePageMoreContract.View {

    @BindView(R.id.rc_list)         RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.base_empty_area) View               emptyView;
    @BindView(R.id.scrollView)      NestedScrollView   mScrollView;

    private EmptyView mEmptyView;

    private String mUserCode;
    private int start = 0;
    private HashMap<String, String> map;

    private PersonalHomePageMoreContract.Presenter mPresenter;
    private PersonalTopicMoreAdapter               adapter;
    private List<HomePageChatInfo>                 list;

    private CircleShareHandler mShareHandler;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_personal_homepage, container, false);
        return view;
    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        mRcList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        list = new ArrayList<>();
        //adapter = new PersonalTopicMoreAdapter(getActivity(), list, R.layout.item_homepage_topic);
        adapter = new PersonalTopicMoreAdapter(getActivity(), list, R.layout.item_live_new_room);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                下拉刷新
                if (!TextUtils.isEmpty(mUserCode)) {
                    mPresenter.getUserChatInfoList(mUserCode, true);
                } else {
                    mPresenter.getSelfChatInfoList(true);
                }
                mRcList.reLoadFinish();
            }
        });

        initRecyclerView();

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(mUserCode)) {
                    mPresenter.loadMrore("chatInfo", mUserCode);
                } else {
                    mPresenter.loadMrore("chatInfo", mUserCode);
                }
            }
        });
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String id = adapter.getList().get(position).getId();
                mShareHandler.getLiveInfo(id, null);
            }
        });

    }

    @Override
    public void initData() {
        mEmptyView = new EmptyView(emptyView);
        mShareHandler = new CircleShareHandler(getContext());

        new PersonalHomePageMorePresenter(this);

        getChatInfoListData();
    }

    private void getChatInfoListData() {
        if (!TextUtils.isEmpty(mUserCode)) {
            map = new HashMap<String, String>();
            map.put("userCode", mUserCode);
            map.put("start", String.valueOf(start));
            mPresenter.getUserChatInfoList(mUserCode, false);
        } else {
            map = new HashMap<String, String>();
            Log.d("tag", "initData: " + UserManager.getInstance().getToken());
            map.put("token", UserManager.getInstance().getToken());
            map.put("start", String.valueOf(start));
            mPresenter.getSelfChatInfoList(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mShareHandler.destroy();
    }

    @Override
    public void tokenOverdue() {

        GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
    }

    @Override
    public void showHomePageGroupInfoList(List<CircleHomeBean> list) {
    }

    @Override
    public void showDZSuccess(int id) {}

    @Override
    public void showSelfNewsList(List<NewsBean> list) {
    }

    @Override
    public void showUserNewsList(List<NewsBean> list) {
    }

    @Override
    public void showSelfChatInfoList(List<HomePageChatInfo> list) {
        mSwipeLayout.setRefreshing(false);
        //        mScrollView.setVisibility(View.GONE);
        mEmptyView.hideEmptyView();
        adapter.notifyChangeData(list);
        mRcList.setAdapter(adapter);

    }

    @Override
    public void showUserChatInfoList(List<HomePageChatInfo> list) {
        mSwipeLayout.setRefreshing(false);
        //        mScrollView.setVisibility(View.GONE);
        mEmptyView.hideEmptyView();
        adapter.notifyChangeData(list);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void showSelfDealList(List<DealListBean> list) {

    }

    @Override
    public void showUserDealList(List<DealListBean> list) {

    }

    @Override
    public void showLoadMoreNewsList(List<NewsBean> list) {

    }

    @Override
    public void showLoadMoreChatInfoList(List<HomePageChatInfo> list) {
        if (adapter != null) mRcList.changeData(list, adapter);
    }

    @Override
    public void showLoadMoreHomePageGroupInfoList(List<CircleHomeBean> list) {

    }

    @Override
    public void showLoadMoreDealList(List<DealListBean> list) {

    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(PersonalHomePageMoreContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        mScrollView.setVisibility(View.VISIBLE);
        mEmptyView.showEmptyContent();
    }

    @Override
    public void showReLoad() {
        getChatInfoListData();
    }

    @Override
    public void showError(String info) {
        mSwipeLayout.setRefreshing(false);
        ToastUtil.showShort(getActivity(), info);
    }

    @Override
    public void showNetError() {
        mScrollView.setVisibility(View.VISIBLE);
        mEmptyView.showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        if (bundle != null) {
            mUserCode = (String) bundle.get(Constant.INTENT_DATA);
        }
    }
}
