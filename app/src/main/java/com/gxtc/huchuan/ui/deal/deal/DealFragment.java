package com.gxtc.huchuan.ui.deal.deal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.ui.mine.deal.issueList.IssueListActivity;
import com.gxtc.huchuan.ui.mine.deal.orderList.PurchaseListActivity;
import com.gxtc.huchuan.utils.TextLineUtile;
import com.gxtc.huchuan.widget.DealFragmentHeadView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * 交易
 * Created by Steven on 17/2/13.
 */

public class DealFragment extends BaseTitleFragment implements DealContract.View,
        View.OnClickListener {

    @BindView(R.id.swipe_deal)    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.re_deal)       RecyclerView       mRecyclerView;

    private DealData data;
    public  static  final String STRING_INSTANT_STATUS = "4";
    private Deal1LevelAdapter      listAdapter;
    private DealContract.Presenter mPresenter;
    private DealFragmentHeadView   mDealFragmentHeadView;
    private int correntPosition = -1;
    private Bundle bundle;

    private View header;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_deal, container, false);

        Drawable d = getResources().getDrawable(R.drawable.deal_home_icon_fatie);
        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
        int showbar = getArguments().getInt("showBar", 0);
        if(showbar == 0) {
            getBaseHeadView().showTitle("交易");
            getBaseHeadView().showHeadRightButton("发帖", this);
            getBaseHeadView().getHeadRightButton().setCompoundDrawables(d, null, null, null);
            header = getLayoutInflater().inflate(R.layout.search_head_view, getBaseHeadView().getParentView(), false);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, R.id.headBackButton);
            params.addRule(RelativeLayout.LEFT_OF, R.id.headRightLinearLayout);
            getBaseHeadView().getParentView().addView(header);
            header.findViewById(R.id.search_layout).setBackground(getResources().getDrawable(R.drawable.shape_search_btn));
            header.findViewById(R.id.et_input_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewSearchActivity.jumpToSearch(getActivity(), NewSearchActivity.TYPE_DEAL);
                }
            });
        }
        return view;
    }

    @Override
    public void initListener() {
        swipeLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getHomeData();
            }
        });
        if(header != null) {
//            if (bundle != null) {
//                getBaseHeadView().showCancelBackButton("帮助", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CommonWebViewActivity.startActivity(getActivity(), Constant.ABOUTLINK + "2", "帮助");
//                    }
//                });
//            } else {
                getBaseHeadView().showBackButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
//            }

            header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
                    params.topMargin = (int) ((getResources().getDimension(R.dimen.actionBar_height) - header.getHeight()) / 2);
                    header.setLayoutParams(params);
                    header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headRightButton:
                if(!UserManager.getInstance().isLogin(getActivity())){
                    return;
                }
                GotoUtil.goToActivity(this, IssueDealActivity.class);
                break;
        }
    }

    @Override
    public void initData() {
        EventBusUtil.register(this);
        new DealPresenter(this);
        initHeadView();
        mPresenter.getHomeData();
    }

    private void initHeadView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mDealFragmentHeadView = new DealFragmentHeadView(getActivity(), mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.addHeadView(mDealFragmentHeadView);
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadmore(listAdapter.getItemCount());
            }
        });

        mDealFragmentHeadView.setDealHeadListener(new DealFragmentHeadView.DealHeadListener() {
            //发布帖子
            @Override
            public void onFBTZ() {
                gotoIssue();
            }

            //我的帖子
            @Override
            public void onWDTZ() {
                gotoIssueList();
            }

            //交易管理
            @Override
            public void onJYGL() {
                gotoOrderList();
            }

            //交易会话
            @Override
            public void onYJHH() {
                gotoMsgList();
            }

            @Override
            public void onCheckAll() {
                mPresenter.changeShowType("");
            }

            @Override
            public void onCheckBuy() {
                mPresenter.changeShowType("1");
            }

            @Override
            public void onCheckSell() {
                mPresenter.changeShowType("0");
            }
        });
    }

    private void gotoMsgList() {
        if(UserManager.getInstance().isLogin(getActivity())){
            GotoUtil.goToActivity(getActivity(),ConversationListActivity.class);
        }
    }

    private void gotoOrderList() {
        if(UserManager.getInstance().isLogin(getActivity())){

            GotoUtil.goToActivity(getActivity(), PurchaseListActivity.class);
        }
    }

    private void gotoIssueList(){
        if(UserManager.getInstance().isLogin(getActivity())){
            GotoUtil.goToActivity(getActivity(), IssueListActivity.class);
        }
    }

    private void gotoIssue(){
        if(UserManager.getInstance().isLogin(getActivity())){
            GotoUtil.goToActivity(getActivity(), IssueDealActivity.class);
        }
    }

    @Override
    public void showData(DealData data) {
        if (data == null) {
            showEmpty();
            return;
        }
        this.data = data;
        getBaseEmptyView().hideEmptyView();
        mDealFragmentHeadView.setHeadDealTab(data);


        if (listAdapter == null) {
            listAdapter = new Deal1LevelAdapter(getContext(), data.getInfos(), R.layout.deal_list_home_page);

            listAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if(listAdapter.getList().get(position).getIsRecommendEntry() == null || "0".equals(listAdapter.getList().get(position).getIsRecommendEntry())){
                            correntPosition = position;
                            Intent intent = new Intent(getActivity(), GoodsDetailedActivity.class);
                            intent.putExtra(Constant.INTENT_DATA, listAdapter.getList().get(position).getId());
                            startActivityForResult(intent,101);
                        }else {
                            Intent intent = new Intent(getActivity(), DealRecomendActivity.class);
                            startActivityForResult(intent,101);
                        }
                    }
                });
            mRecyclerView.setAdapter(listAdapter);
        } else {
            mRecyclerView.notifyChangeData(data.getInfos(),listAdapter);
        }

    }

    @Override
    public void showAdvertise(List<NewsAdsBean> data) {
        if (mDealFragmentHeadView != null) {
            mDealFragmentHeadView.setCbDealBanner(data);
        }
    }

    @Override
    public void showloadmore(DealData data) {
        if (mRecyclerView != null && listAdapter != null) {
            mRecyclerView.changeData(data.getInfos(), listAdapter);
        }
    }

    @Override
    public void showReloadmre(DealData data) {
        showData(data);
    }

    @Override
    public void showLoadMoreFinish() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void showLoad() {
        if(listAdapter == null){
            getBaseLoadingView().showLoading();
        }
    }

    @Override
    public void showLoadFinish() {
        swipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mRecyclerView.reLoadFinish();
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getContext(),info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseEmptyView().hideEmptyView();
                mPresenter.getHomeData();
            }
        });
    }

    @Override
    public void setPresenter(DealContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Subscribe
    public void onEvent(EventScorllTopBean bean){
        if(bean.position == 3 && listAdapter != null && mRecyclerView != null){
            mRecyclerView.scrollToPosition(0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        if(requestCode == 101){
            if(data != null){
                int commentCount = data.getIntExtra("commentCount",-1);
                int readCount = data.getIntExtra("readCount",-1);
                if(correntPosition != -1){
                    if(commentCount != -1){
                        listAdapter.getList().get(correntPosition).setLiuYan(commentCount+"");
                    }
                    if(readCount != -1){
                        listAdapter.getList().get(correntPosition).setRead(readCount+"");
                    }
                    mRecyclerView.notifyItemChanged(correntPosition);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBusUtil.unregister(this);
        TextLineUtile.clearTextLineCache();
        mPresenter.destroy();
        super.onDestroy();
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        super.onGetBundle(bundle);
        this.bundle = bundle;
    }
}
