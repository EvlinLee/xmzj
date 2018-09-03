package com.gxtc.huchuan.ui.circle.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleArticleAdapter;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.mine.editorarticle.ArticleResolveActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子-文章
 * Created by Steven on 17/4/25.
 */

public class ArticleActivity extends BaseTitleActivity implements ArticleContract.View,
        View.OnClickListener,TextWatcher{

    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    private CircleArticleAdapter      mAdapter;
    private ArticleContract.Presenter mPresenter;
    private int                       mCircleid;
    private String                    mUserCode;
    public String searchKey;
    public boolean searchLoadMore;
    public List<NewNewsBean.DataBean> tempDatas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
    }


    @Override
    public void initData() {
        mCircleid = getIntent().getIntExtra("circleid", 0);
        mUserCode = getIntent().getStringExtra("userCode");
        getBaseHeadView().showTitle("文章");
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_circle_manage_icon_add,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArticleResolveActivity.startActivity(ArticleActivity.this,null,mCircleid);
                    }
                });

        swipeLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        SearchView searchView = new SearchView(this);
        listView.addHeadView(searchView);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WindowUtil.closeInputMethod(ArticleActivity.this);
                mPresenter.getSeachData(false,query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        new ArticlePresenter(this, mCircleid, mUserCode);
        mPresenter.getData(false);
    }

    public static void startActivity(Context context, String userCode, int circleid) {
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra("circleid", circleid);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this,LoginAndRegisteActivity.class);
    }

    @Override
    public void initListener() {

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                if(TextUtils.isEmpty(searchKey)){
                    mPresenter.getData(true);
                    listView.reLoadFinish();
                }else {
                    searchLoadMore = false;
                    mPresenter.getSeachData(false,searchKey);
                }
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(TextUtils.isEmpty(searchKey)){
                    mPresenter.loadMrore();
                }else {
                    searchLoadMore = true;
                    mPresenter.getSeachData(true,searchKey);
                }
            }
        });
    }


    @Override
    public void showData(List<NewNewsBean.DataBean> datas) {
        if (mAdapter == null) {
            tempDatas.addAll(datas);
            mAdapter = new CircleArticleAdapter(this, datas, R.layout.item_news_video_fragment,
                    R.layout.item_news_fragment, R.layout.item_live_room,
                    R.layout.item_deal_2_level, R.layout.item_news_ads1_fragment,
                    R.layout.item_news_fragment);
            listView.setAdapter(mAdapter);
            mAdapter.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    NewNewsBean.DataBean data = mAdapter.getDatas().get((Integer) v.getTag());
                    removeData(mCircleid, data.getId(), (Integer) v.getTag());
                    return false;
                }
            });
            mAdapter.setOnReItemOnClickListener(
                    new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                            Intent   intent = new Intent(ArticleActivity.this,
                                    NewsWebActivity.class);
                            NewsBean bean   = new NewsBean();

                            NewNewsBean.DataBean data = mAdapter.getDatas().get(position);
                            bean.setId(data.getId());
                            bean.setSource(data.getSource());
                            bean.setIsVideo(data.getIsVideo());
                            bean.setVideoUrl(data.getVideoUrl());
                            bean.setCommentCount(data.getCommentCount());
                            bean.setThumbsupCount(data.getThumbsupCount());
                            bean.setIsThumbsup(data.getIsThumbsup());
                            bean.setIsCollect(data.getIsCollect());
                            bean.setCover(data.getCover());
                            bean.setTitle(data.getTitle());
                            bean.setDigest(data.getDigest());
                            bean.setUserCode(data.getUserCode());
                            bean.setSourceicon(data.getSourceicon());
                            bean.setRedirectUrl(data.getRedirectUrl());
                            intent.putExtra("data", bean);
                            startActivity(intent);

                        }
                    });
        }

    }

    @Override
    public void showRefreshFinish(List<NewNewsBean.DataBean> datas) {
        listView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<NewNewsBean.DataBean> datas) {
        tempDatas.addAll(datas);
        listView.changeData(datas, mAdapter);

    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showSeachData(final List<NewNewsBean.DataBean> datas) {
        if(datas != null && datas.size() > 0){
            if(!searchLoadMore){
                listView.notifyChangeData(datas, mAdapter);
            }else {
                listView.changeData(datas, mAdapter);
            }
        }else {
            listView.loadFinishNotView();
        }

    }


    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
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

    }

    /**
     * 加载更多时网络错误，直接打吐司
     *
     * @param info
     */
    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
        getBaseLoadingView().hideLoading();
    }

    /**
     * 初始网络错误，点击重新加载
     */
    @Override
    public void showNetError() {


    }

    @Override
    public void setPresenter(ArticleContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Subscribe
    public void onEvent(Object o) {
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        searchKey = s.toString();
        if(TextUtils.isEmpty(searchKey)){
            listView.notifyChangeData(tempDatas,mAdapter);
        }
    }

    private AlertDialog mDialog;

    private void removeData(final int groupids, final String paramId, final int position) {

        mDialog = DialogUtil.showInputDialog(this, false, "", "确定取消同步吗", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelGroup(groupids, paramId, position);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    //取消圈子同步 同步类型1、文章；2、课堂；3、文件；6.系列课
    private void CancelGroup(final int groupids, String paramId, final int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("groupId", groupids+"");
        map.put("paramId", paramId+"");
        map.put("type", "1");
        Subscription sub = MineApi.getInstance().cancelGroupAppendData(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(ArticleActivity.this, "取消同步成功");
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(ArticleActivity.this, message);

                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }
}
