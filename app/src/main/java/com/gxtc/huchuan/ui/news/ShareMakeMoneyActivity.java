package com.gxtc.huchuan.ui.news;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ShareMakeMoneyAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ShareDistributeDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.DistributeActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
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

/**
 * Created by 宋家任 on 2017/5/17.
 * 分享赚钱
 */

public class ShareMakeMoneyActivity extends BaseTitleActivity implements ShareMakeMoneyContract.View, View.OnClickListener {

    @BindView(R.id.rv_share_make_money)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_share_make_money)
    SwipeRefreshLayout swipeLayout;
    private static final String STRING_SORT_TYPE_OF_HOT = "1";
    private static final String STRING_SORT_TYPE_OF_NEW = "2";
    private static final String STRING_SORT_TYPE_OF_HEIGHT = "3";
    private String searchWord = "";
    private ShareMakeMoneyContract.Presenter mPresenter;
    private List<ShareMakeMoneyBean> mDatas;
    private ShareMakeMoneyAdapter mAdapter;
    private UMShareUtils mShareUtils;

    private Subscription sub;
    private TextView tvHot;
    private TextView tvNew;
    private TextView tvType;
    private TextView tvTotal;
    private String orderByType = "1";
    private String type = "0";
    private AlertDialog mAlertDialog;
    private LinearLayout llSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shar_make_money);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_share_make_money));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("收益明细", this);
    }

    @Override
    public void initListener() {

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecyclerView.reLoadFinish();
                if (TextUtils.isEmpty(searchWord)) {
                    mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, "");
                } else {
                    mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, searchWord);
                }
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (TextUtils.isEmpty(searchWord)) {
                    mPresenter.loadMrore(UserManager.getInstance().getToken(), type, orderByType, "");
                } else {
                    mPresenter.loadMrore(UserManager.getInstance().getToken(), type, orderByType, searchWord);
                }
            }
        });
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        mRecyclerView.addHeadView(getHeadSearch());
        mRecyclerView.addHeadView(getHeadView());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mAdapter = new ShareMakeMoneyAdapter(this, new ArrayList<ShareMakeMoneyBean>(), R.layout.item_share_make_money_activity);
        mRecyclerView.setAdapter(mAdapter);

        new ShareMakeMoneyPresenter(this);
        mPresenter.getData(false, UserManager.getInstance().getToken(), type, orderByType, "");
    }

    private View getHeadSearch() {
        View view = View.inflate(this, R.layout.layout_sharemakemoney_headsearch, null);
        llSearch = (LinearLayout) view.findViewById(R.id.ll_makemoney);
        llSearch.setOnClickListener(this);
        return view;
    }

    private View getHeadView() {
        View inflate = View.inflate(this, R.layout.share_make_mouney_layout_header, null);
        tvHot = (TextView) inflate.findViewById(R.id.tv_hot);
        tvNew = (TextView) inflate.findViewById(R.id.tv_new);
        tvType = (TextView) inflate.findViewById(R.id.tv_type);
        tvTotal = (TextView) inflate.findViewById(R.id.tatal);

        tvHot.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        tvType.setOnClickListener(this);
        tvTotal.setOnClickListener(this);
        return inflate;
    }

    private void setColor() {
        tvHot.setTextColor(getResources().getColor(R.color.text_color_666));
        tvNew.setTextColor(getResources().getColor(R.color.text_color_666));
        tvTotal.setTextColor(getResources().getColor(R.color.text_color_666));
    }

    @Override
    public void showData(List<ShareMakeMoneyBean> datas) {

        mDatas.clear();
        mDatas.addAll(datas);
        mRecyclerView.notifyChangeData(mDatas, mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ShareMakeMoneyBean bean = mAdapter.getList().get(position);

                if ("chatInfo".equals(bean.getType())) {
                    goChat(bean);

                } else if ("group".equals(bean.getType())) {
                    goGroup(bean);

                } else if ("chatSeries".equals(bean.getType())) {
                    SeriesActivity.startActivity(ShareMakeMoneyActivity.this, bean.getId());
                }

            }
        });

        mAdapter.setOnShareListener(new ShareMakeMoneyAdapter.OnShareListener() {
            @Override
            public void onShare(int position, final ShareMakeMoneyBean bean) {

                if (!UserManager.getInstance().isLogin(ShareMakeMoneyActivity.this)) return;
                mShareUtils = new UMShareUtils(ShareMakeMoneyActivity.this);
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
                            ClipboardManager cmb = (ClipboardManager) ShareMakeMoneyActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(bean.getShareUrl().trim());
                            ToastUtil.showShort(ShareMakeMoneyActivity.this, "已复制");
                        }

                        //二维码
                        if (flag == 1) {
                            ErWeiCodeActivity.startActivity(ShareMakeMoneyActivity.this, finalQrType, StringUtil.toInt(bean.getId()), "");
                        }
                    }
                });
            }
        });
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
                        ToastUtil.showShort(ShareMakeMoneyActivity.this, message);
                    }
                }));
    }


    private void chatItemClick(ChatInfosBean bean) {
        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(ShareMakeMoneyActivity.this, bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(ShareMakeMoneyActivity.this, bean.getId());
                } else {
                    SeriesActivity.startActivity(ShareMakeMoneyActivity.this, bean.getChatSeries(), true);
                }
            } else {
                GotoUtil.goToActivity(ShareMakeMoneyActivity.this, LoginAndRegisteActivity.class);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(ShareMakeMoneyActivity.this, bean.getId());
                } else {
                    //这里提示加入圈子
//                    joinGroupDialog(bean.getGroupId());
                    Intent intent = new Intent(ShareMakeMoneyActivity.this, CircleJoinActivity.class);
                    intent.putExtra("byLiveId", (int) bean.getGroupId());
                    startActivity(intent);
                }
            } else {
                GotoUtil.goToActivity(ShareMakeMoneyActivity.this, LoginAndRegisteActivity.class);
            }
        } else {
            LiveIntroActivity.startActivity(ShareMakeMoneyActivity.this, bean.getId());
        }
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
            Intent intent = new Intent(ShareMakeMoneyActivity.this, CircleJoinActivity.class);
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
            GotoUtil.goToActivity(ShareMakeMoneyActivity.this, CircleMainActivity.class, 0, circleBean);
        }
    }


    @Override
    public void showMoneyList(List<PreProfitBean> datas) {

    }

    private void setTypeData(final Activity mContext, View view) {
        final ShareDistributeDialog dialog = new ShareDistributeDialog(mContext);
        dialog.anchorView(view)
                .location(10, -50)
                .showAnim(new PopEnterAnim().duration(200))
                .dismissAnim(new PopExitAnim().duration(200))
                .gravity(Gravity.BOTTOM)
                .cornerRadius(4F)
                .bubbleColor(Color.parseColor("#ffffff")).show();
        dialog.setListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = String.valueOf(position);
                mRecyclerView.reLoadFinish();
                mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, searchWord);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.headRightButton:
//                GotoUtil.goToActivity(this, PreProfitActivity.class);
                if (!UserManager.getInstance().isLogin(this)) return;
                DistributeActivity.jumpToDistributeActivity(this, 4);
                break;
            case R.id.tv_type:
                setTypeData(this, v);
                break;
            case R.id.tv_hot:
                setColor();
                mRecyclerView.reLoadFinish();
                orderByType = STRING_SORT_TYPE_OF_HOT;
                tvHot.setTextColor(Color.parseColor("#2b8cff"));
                mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, "");
                break;
            case R.id.tv_new:
                setColor();
                mRecyclerView.reLoadFinish();
                orderByType = STRING_SORT_TYPE_OF_NEW;
                tvNew.setTextColor(Color.parseColor("#2b8cff"));
                mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, "");
                break;
            case R.id.tatal:
                setColor();
                mRecyclerView.reLoadFinish();
                orderByType = STRING_SORT_TYPE_OF_HEIGHT;
                tvTotal.setTextColor(Color.parseColor("#2b8cff"));
                mPresenter.getData(true, UserManager.getInstance().getToken(), type, orderByType, "");
                break;
            case R.id.ll_makemoney:
                GotoUtil.goToActivity(this, ShareMakeMoneySearchActivity.class);
                break;
        }
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(this, false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(ShareMakeMoneyActivity.this);
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(ShareMakeMoneyContract.Presenter presenter) {
        mPresenter = presenter;
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

    @Override
    public void showError(String info) {
        if (mDatas.size() > 0) {//有数据加载更多的时候出现错误
            ToastUtil.showShort(this, info);
            getBaseLoadingView().hideLoading();
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }


    @Override
    public void showRefreshFinish(List<ShareMakeMoneyBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showMoneyListRefreshFinish(List<PreProfitBean> datas) {
    }

    @Override
    public void showLoadMore(List<ShareMakeMoneyBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showLoadMoreMoneyList(List<PreProfitBean> datas) {
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
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
