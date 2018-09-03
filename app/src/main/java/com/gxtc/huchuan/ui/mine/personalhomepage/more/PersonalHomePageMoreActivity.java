package com.gxtc.huchuan.ui.mine.personalhomepage.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.event.EventPersonalHomePageBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:个人主页  > 更多
 * Created by ALing on 2017/4/10 .
 */

public class PersonalHomePageMoreActivity extends BaseTitleActivity implements PersonalHomePageMoreContract.View{
    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private String mType;
    private String mUserCode;
    private int start = 0;
    private HashMap<String,String> map;

    private PersonalHomePageMoreContract.Presenter mPresenter;
    private EventPersonalHomePageBean stickyEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_homepage_more);
    }

    @Override
    public void initView() {
        super.initView();
        initRecyclerView();
        EventBusUtil.register(this);
        stickyEvent = EventBus.getDefault().getStickyEvent(EventPersonalHomePageBean.class);
        mUserCode = stickyEvent.getUserCode();
    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mPresenter.getData(true);     //刷新重新获取数据
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
//                mPresenter.loadMore();       //加载更多
            }
        });
//        adapter = new ClassRoomMeassageAdapter(this,new ArrayList<ClassMyMessageBean>(),R.layout.item_classroom_mymessage);
//        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//
//            }
//        });

    }

    @Override
    public void initData() {
        super.initData();
        new PersonalHomePageMorePresenter(this);
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        Log.d("tag", "initData: "+mType + "??"+ mUserCode);

        switch (mType){
            //新闻
            case "news":

                break;
            //直播课程
            case "chatInfo":
                map = new HashMap<String, String>();
                if (!TextUtils.isEmpty(mUserCode)){
                    map.put("userCode",mUserCode);
                    map.put("start",String.valueOf(start));
                    mPresenter.getUserChatInfoList(mUserCode,false);
                }else {
                    map.put("token", UserManager.getInstance().getToken());
                    map.put("start",String.valueOf(start));
                    mPresenter.getSelfChatInfoList(false);
                }


                break;
            //交易信息
            case "tradeInfo":

                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
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

    }

    @Override
    public void showUserNewsList(List<NewsBean> list) {

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

    }

    @Override
    public void setPresenter(PersonalHomePageMoreContract.Presenter presenter) {

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
