package com.gxtc.huchuan.ui.news;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ShareMakeMoneyAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.SearchView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//分销内容搜索
public class ShareMakeMoneySearchActivity extends BaseTitleActivity implements ShareMakeMoneyContract.View, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_search_result)
    RecyclerView rvSearchResult;
    @BindView(R.id.sf_search)
    SwipeRefreshLayout sfSearch;

    private String searchWord;

    private SearchView mSearchView;

    private ShareMakeMoneyContract.Presenter mPresenter;

    private ShareMakeMoneyAdapter mAdapter;

    private Subscription sub;

    private UMShareUtils mShareUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharemoney_search);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("搜索");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchView = new SearchView(this);
        mSearchView.getTvHint().setText("搜索分销内容");
        rvSearchResult.addHeadView(mSearchView);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        rvSearchResult.setLoadMoreView(R.layout.model_footview_loadmore);
        mAdapter = new ShareMakeMoneyAdapter(this, new ArrayList<ShareMakeMoneyBean>(), R.layout.item_share_make_money_activity);
        rvSearchResult.setAdapter(mAdapter);
        KeyboardUtils.showSoftInput(this,mSearchView.getEditSearch());
        new ShareMakeMoneyPresenter(this);
    }

    @Override
    public void initListener() {
        sfSearch.setOnRefreshListener(this);
        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    ToastUtil.showShort(ShareMakeMoneySearchActivity.this, "请输入搜索内容");
                } else {
                    searchWord = query;
                    rvSearchResult.reLoadFinish();
                    WindowUtil.closeInputMethod(ShareMakeMoneySearchActivity.this);
                    mPresenter.getData(true, UserManager.getInstance().getToken(), "0", "", searchWord);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        rvSearchResult.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(searchWord)) {
                    mPresenter.loadMrore(UserManager.getInstance().getToken(), "0", "", searchWord);
                }
            }
        });
    }

    //刷新
    @Override
    public void onRefresh() {
        rvSearchResult.reLoadFinish();
        mPresenter.getData(true, UserManager.getInstance().getToken(), "0", "", searchWord);
    }

    /**
     * 跳转话题
     *
     * @param bean
     */
    private void goChat(final ShareMakeMoneyBean bean) {
        sub = LiveApi.getInstance().getChatInfosBean(UserManager.getInstance().getToken(),
                bean.getId()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<ChatInfosBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ChatInfosBean bean1 = (ChatInfosBean) data;
                        chatItemClick(bean1);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(ShareMakeMoneySearchActivity.this, message);
                    }
                }));
    }

    /**
     * 跳转圈子
     *
     * @param bean
     */
    private void goGroup(ShareMakeMoneyBean bean) {
        if (!UserManager.getInstance().isLogin(this)) return;
        //未加入
        if ("0".equals(bean.getJoinGroup())) {
            int id = Integer.valueOf(bean.getId());
            Intent intent = new Intent(ShareMakeMoneySearchActivity.this, CircleJoinActivity.class);
            intent.putExtra("byLiveId", id);
//             intent.putExtra("name", bean.getTitle());
//            intent.putExtra("url", bean.getGroupUrl());
//            intent.putExtra("isAudit","0");
//            intent.putExtra("id", id);
//            intent.putExtra(Constant.INTENT_DATA,bean.getFee());
            startActivityForResult(intent, 0);
        } else {
            CircleBean circleBean = new CircleBean();
            circleBean.setGroupName(bean.getTitle());
            circleBean.setId(Integer.valueOf(bean.getId()));
            GotoUtil.goToActivity(ShareMakeMoneySearchActivity.this, CircleMainActivity.class, 0, circleBean);
        }
    }

    private void chatItemClick(ChatInfosBean bean) {
        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getId());
                } else {
                    SeriesActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getChatSeries(), true);
                }
            } else {
                GotoUtil.goToActivity(ShareMakeMoneySearchActivity.this, LoginAndRegisteActivity.class);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getId());
                } else {
                    //这里提示加入圈子
//                    joinGroupDialog(bean.getGroupId());
                    Intent intent = new Intent(ShareMakeMoneySearchActivity.this, CircleJoinActivity.class);
                    intent.putExtra("byLiveId", (int) bean.getGroupId());
                    startActivity(intent);
                }
            } else {
                GotoUtil.goToActivity(ShareMakeMoneySearchActivity.this, LoginAndRegisteActivity.class);
            }
        } else {
            LiveIntroActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getId());
        }
    }

    /******************************************************网络数据********************************************************/

    @Override
    public void showData(List<ShareMakeMoneyBean> datas) {

    }

    @Override
    public void showMoneyList(List<PreProfitBean> datas) {

    }

    @Override
    public void showRefreshFinish(List<ShareMakeMoneyBean> datas) {
        rvSearchResult.notifyChangeData(datas, mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ShareMakeMoneyBean bean = mAdapter.getList().get(position);

                if ("chatInfo".equals(bean.getType())) {
                    goChat(bean);

                } else if ("group".equals(bean.getType())) {
                    goGroup(bean);

                } else if ("chatSeries".equals(bean.getType())) {
                    SeriesActivity.startActivity(ShareMakeMoneySearchActivity.this, bean.getId());
                }

            }
        });

        mAdapter.setOnShareListener(new ShareMakeMoneyAdapter.OnShareListener() {
            @Override
            public void onShare(int position, final ShareMakeMoneyBean bean) {

                if (!UserManager.getInstance().isLogin(ShareMakeMoneySearchActivity.this)) return;
                mShareUtils = new UMShareUtils(ShareMakeMoneySearchActivity.this);
                SHARE_MEDIA[] platform = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};

                int qrType = -1;

                if ("chatInfo".equals(bean.getType())) {
                    mShareUtils.shareMakeMoney(bean.getFacePic(), bean.getChatRoomName(), bean.getTitle(), bean.getShareUrl(), platform);
                    qrType = ErWeiCodeActivity.TYPE_CLASSROOM;

                } else if ("group".equals(bean.getType())) {
                    mShareUtils.shareMakeMoney(bean.getFacePic(), bean.getTitle(), bean.getGroupDesc(), bean.getShareUrl(), platform);
                    qrType = -1;

                } else if ("chatSeries".equals(bean.getType())) {
                    mShareUtils.shareMakeMoney(bean.getFacePic(), bean.getChatRoomName(), bean.getTitle(), bean.getShareUrl(), platform);
                    qrType = ErWeiCodeActivity.TYPE_SERIES;
                }

                final int finalQrType = qrType;
                mShareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                    @Override
                    public void onItemClick(int flag) {
                        //复制链接
                        if (flag == 0) {
                            ClipboardManager cmb = (ClipboardManager) ShareMakeMoneySearchActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(bean.getShareUrl().trim());
                            ToastUtil.showShort(ShareMakeMoneySearchActivity.this, "已复制");
                        }

                        //二维码
                        if (flag == 1) {
                            ErWeiCodeActivity.startActivity(ShareMakeMoneySearchActivity.this, finalQrType, StringUtil.toInt(bean.getId()), "");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void showMoneyListRefreshFinish(List<PreProfitBean> datas) {

    }

    @Override
    public void showLoadMore(List<ShareMakeMoneyBean> datas) {
        rvSearchResult.changeData(datas, mAdapter);
    }

    @Override
    public void showLoadMoreMoneyList(List<PreProfitBean> datas) {

    }

    @Override
    public void showNoMore() {
        rvSearchResult.loadFinish();
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        sfSearch.setRefreshing(false);
    }

    @Override
    public void showEmpty() {
        ToastUtil.showShort(this, getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        sfSearch.setRefreshing(false);
    }

    @Override
    public void showNetError() {
        sfSearch.setRefreshing(false);
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
    }

    @Override
    public void setPresenter(ShareMakeMoneyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
