package com.gxtc.huchuan.ui.mine.circle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NoticeListAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.homePage.CircleNoticeActivity;
import com.gxtc.huchuan.ui.mine.withdraw.WithdrawListActivity;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 圈子公告列表
 */
public class NoticeListActivity extends BaseTitleActivity implements NoticeListContract.View,
        View.OnClickListener {

    @BindView(R.id.recyclerView) RecyclerView       listView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    private int id;

    private NoticeListAdapter            mAdapter;
    private NoticeListContract.Presenter mPresenter;
    boolean canEdit;
    private String targetId;
    private Subscription sub;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("公告");
        getBaseHeadView().showBackButton(this);

         canEdit = getIntent().getBooleanExtra("edit",false);
        targetId = getIntent().getStringExtra("targetId");
        if(canEdit) getBaseHeadView().showHeadRightButton("编辑",this);

        swipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);

        mAdapter = new NoticeListAdapter(this, new ArrayList<CircleBean>(), R.layout.item_notice);
        listView.setAdapter(mAdapter);

    }

    @Override
    public void initListener() {
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = mAdapter.getList().get(position).getId();
                GotoUtil.goToActivity(NoticeListActivity.this, CircleNoticeActivity.class,0,id);
            }
        });
        mAdapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
            @Override
            public void onItemLongClick(View v, final int position) {
                if(canEdit){
                    dialog= DialogUtil.showDeportDialog(NoticeListActivity.this, false, null, "确定要删除", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(v.getId() == R.id.tv_dialog_confirm){
                                deleteNotice(mAdapter.getList().get(position).getId(),position);
                            }
                            dialog.dismiss();
                        }
                    });
                }else {
                    ToastUtil.showShort(NoticeListActivity.this,"你不是管理员或是圈主");
                }
            }
        });
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.refreshData();
            }
        });
    }

    private void deleteNotice(int id,final int position) {
        sub=CircleApi.getInstance()
                .deleteNotice(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ApiResponseBean<Object>>() {
                    @Override
                    public void onCompleted() {
                        EventBusUtil.post(true);
                        ToastUtil.showShort(NoticeListActivity.this,"删除成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShort(NoticeListActivity.this,e.getMessage());
                    }

                    @Override
                    public void onNext(ApiResponseBean<Object> objectApiResponseBean) {
                        listView.removeData(mAdapter,position);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                Intent intent = new Intent(this, IssueNoticeActivity.class);
                intent.putExtra("groud_id", id);
                intent.putExtra("targetId", targetId);
                startActivityForResult(intent,100);
                break;
        }
    }

    @Override
    public void initData() {
        id = getIntent().getIntExtra(Constant.INTENT_DATA, 0);
        new NoticeListPresenter(this);
        mPresenter.getData(id);
    }

    @Override
    public void showRefreshData(List<CircleBean> datas) {
        getBaseEmptyView().hideEmptyView();
        listView.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showLoadMoreData(List<CircleBean> datas) {
        listView.changeData(datas,mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        listView.loadFinish();
    }

    @Override
    public void showData(List<CircleBean> datas) {
        getBaseEmptyView().hideEmptyView();
        listView.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void setPresenter(NoticeListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        if(mAdapter.getItemCount() == 0){
            getBaseLoadingView().hideLoading();
        }else{
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if(sub != null && !sub.isUnsubscribed()){
            sub.unsubscribe();
        }
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(id);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        if(requestCode == 100){
            listView.reLoadFinish();
            mPresenter.refreshData();
        }
    }

    public static void startActivity(Activity activity, int id, boolean canEdit ){
        Intent intent = new Intent(activity,NoticeListActivity.class);
        intent.putExtra(Constant.INTENT_DATA,id);
        intent.putExtra("edit",canEdit);
        activity.startActivity(intent);
    }


}
