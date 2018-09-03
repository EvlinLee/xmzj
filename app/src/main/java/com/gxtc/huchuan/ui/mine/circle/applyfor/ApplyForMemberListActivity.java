package com.gxtc.huchuan.ui.mine.circle.applyfor;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ApplyForMemberListAdapter;
import com.gxtc.huchuan.adapter.MineArticleAdapter;
import com.gxtc.huchuan.bean.ApplyForBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.news.MineArticleActivity;
import com.gxtc.huchuan.ui.mine.news.MineArticleContract;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sjr on 2017/6/10.
 * 审核成员列表
 */

public class ApplyForMemberListActivity extends BaseTitleActivity implements ApplyForMemberListContract.View {

    @BindView(R.id.rv_apply_for)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_apply_for)
    SwipeRefreshLayout swApplyFor;

    private ApplyForMemberListContract.Presenter mPresenter;

    private ApplyForMemberListAdapter mAdapter;

    private AlertDialog mDialog;

    Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_member_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyForMemberListActivity.this.finish();
            }
        });
        getBaseHeadView().showTitle(getString(R.string.title_apply_for));
        swApplyFor.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
        initRecyCleView();

    }

    @Override
    public void initData() {
        int groupId = getIntent().getIntExtra("group_id", -1);

        new ApplyMemberListPresenter(groupId, this);
        mPresenter.getData(false);


    }

    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);


    }

    @Override
    public void initListener() {
        swApplyFor.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRecyclerView.reLoadFinish();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });


    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<ApplyForBean> datas) {
        mAdapter = new ApplyForMemberListAdapter(this, datas, R.layout.item_apply_for_member_list);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnAuditListener(new ApplyForMemberListAdapter.OnAuditListener() {
            @Override
            public void onAudit(final ApplyForBean bean, final int position) {
                mDialog = DialogUtil.createDialog2(ApplyForMemberListActivity.this, "是否同意该成员的申请？", "不同意", "同意",
                        new DialogUtil.DialogClickListener() {
                            @Override
                            public void clickLeftButton(View view) {
                                isAudit(bean, 2, position);
                                mDialog.dismiss();
                            }

                            @Override
                            public void clickRightButton(View view) {
                                isAudit(bean, 1, position);
                                mDialog.dismiss();
                            }
                        });
                mDialog.show();
            }
        });
    }

    private void isAudit(ApplyForBean bean1, int isAudit, final int position) {
        if (UserManager.getInstance().isLogin()) {
            sub = CircleApi.getInstance().auditJoin(bean1.getGroupId(),
                    UserManager.getInstance().getToken(),
                    bean1.getId(), isAudit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            mRecyclerView.removeData(mAdapter, position);
                            if (mAdapter.getList().size() == 0) {
                                getBaseEmptyView().showEmptyView();
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(ApplyForMemberListActivity.this, message);
                        }
                    }));
        }
    }

    @Override
    public void showRefreshFinish(List<ApplyForBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<ApplyForBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void setPresenter(ApplyForMemberListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        swApplyFor.setRefreshing(false);
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
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                getBaseLoadingView().hideLoading();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null && sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
