package com.gxtc.huchuan.ui.mine.personalhomepage.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PersonalNewsMoreAdapter;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Describe: 个人新闻更多列表/用户新闻更多列表
 * Created by ALing on 2017/4/10 .
 */

public class PersonalNewsMoreActivity extends BaseTitleActivity implements PersonalHomePageMoreContract.View{
    private static final String TAG = PersonalNewsMoreActivity.class.getSimpleName();
    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private String mUserCode;
    private int start = 0;
    private HashMap<String,String> map;

    private PersonalHomePageMoreContract.Presenter mPresenter;
    private PersonalNewsMoreAdapter adapter;
    private List<NewsBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_homepage_more);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_more_news));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        list = new ArrayList<>();
        adapter = new PersonalNewsMoreAdapter(this,list, R.layout.item_homepage_news);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                下拉刷新
                if (!TextUtils.isEmpty(mUserCode)){
                    map = new HashMap<String, String>();
                    map.put("userCode",mUserCode );
                    map.put("start", String.valueOf(start));
                    mPresenter.getUserNewsList(mUserCode,true);
                }else {
                    map = new HashMap<String, String>();
                    map.put("token", UserManager.getInstance().getToken());
                    map.put("start", String.valueOf(start));
                    mPresenter.getSelfNewsList(true);
                }
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(mUserCode)){
                    start += 15;
                    map = new HashMap<String, String>();
                    map.put("userCode",mUserCode );
                    map.put("start", String.valueOf(start));
                    mPresenter.loadMrore("news",null);
                }else {
                    start += 15;
                    map = new HashMap<String, String>();
                    map.put("token", UserManager.getInstance().getToken());
                    map.put("start", String.valueOf(start));
                    mPresenter.loadMrore("news",mUserCode);
                }
            }
        });
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                NewsBean newsBean = adapter.getList().get(position);
                Intent intent = new Intent(PersonalNewsMoreActivity.this, NewsWebActivity.class);
                intent.putExtra("data", newsBean);
                PersonalNewsMoreActivity.this.startActivity(intent);
            }
        });

    }
    @Override
    public void initData() {
        super.initData();
        new PersonalHomePageMorePresenter(this);

        mUserCode = getIntent().getStringExtra(Constant.INTENT_DATA);

        if (!TextUtils.isEmpty(mUserCode)) {
            map = new HashMap<String, String>();
            map.put("userCode", mUserCode);
            map.put("start", String.valueOf(start));
            mPresenter.getUserNewsList(mUserCode,false);
        } else {
            map = new HashMap<String, String>();
            Log.d("tag", "initData: "+UserManager.getInstance().getToken());
            map.put("token", UserManager.getInstance().getToken());
            map.put("start", String.valueOf(start));
            mPresenter.getSelfNewsList(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void tokenOverdue() {

        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showHomePageGroupInfoList(List<CircleHomeBean> list) {
    }

    @Override
    public void showDZSuccess(int id) {
    }

    @Override
    public void showSelfNewsList(List<NewsBean> list) {

        //刷新
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(list,adapter);
    }

    @Override
    public void showUserNewsList(List<NewsBean> list) {

        //刷新
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(list,adapter);
    }

    @Override
    public void showSelfChatInfoList(List<HomePageChatInfo> list) {

    }

    @Override
    public void showUserChatInfoList(List<HomePageChatInfo> list) {

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
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }

}
