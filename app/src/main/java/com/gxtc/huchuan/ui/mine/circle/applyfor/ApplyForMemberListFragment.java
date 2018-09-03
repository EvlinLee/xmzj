package com.gxtc.huchuan.ui.mine.circle.applyfor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ApplyForMemberListAdapter;
import com.gxtc.huchuan.bean.ApplyForBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.List;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sjr on 2017/6/10.
 * 审核成员列表
 */

public class ApplyForMemberListFragment extends BaseTitleFragment implements ApplyForMemberListContract.View {

    @BindView(R.id.rv_apply_for)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_apply_for)
    SwipeRefreshLayout swApplyFor;

    private ApplyForMemberListContract.Presenter mPresenter;

    private ApplyForMemberListAdapter mAdapter;

    private AlertDialog mDialog;

    Subscription sub;


    public static ApplyForMemberListFragment getInstance(int groupId){
        ApplyForMemberListFragment mApplyForMemberListFragment = new ApplyForMemberListFragment();
        Bundle bundle =  new Bundle();
        bundle.putInt("groupId",groupId);
        mApplyForMemberListFragment.setArguments(bundle);
        return mApplyForMemberListFragment;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_apply_for_member_list,null);
    }


    @Override
    public void initData() {
        int groupId = getArguments().getInt("groupId");
        swApplyFor.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
        initRecyCleView();
        new ApplyMemberListPresenter(groupId, this);
        mPresenter.getData(false);
    }

    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
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
        mAdapter = new ApplyForMemberListAdapter(getContext(), datas, R.layout.item_apply_for_member_list);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnAuditListener(new ApplyForMemberListAdapter.OnAuditListener() {
            @Override
            public void onAudit(final ApplyForBean bean, final int position) {
                mDialog = DialogUtil.createAuseDialog(getActivity(), "验证信息",bean.getContent(), "不同意", "同意",
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
                        },true);
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
                            ToastUtil.showShort(getContext(), message);
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
    public void showLoad() {}

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
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getContext(), info);
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
    public void onDestroy() {
        super.onDestroy();
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
