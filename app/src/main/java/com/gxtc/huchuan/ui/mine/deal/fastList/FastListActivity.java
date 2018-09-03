package com.gxtc.huchuan.ui.mine.deal.fastList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FastListAdapter;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.ui.deal.guarantee.GuaranteeDetailedActivity;
import com.gxtc.huchuan.ui.mine.deal.issueList.IssueListContract;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * 快速交易页面
 * 苏修伟
 */
public class FastListActivity extends BaseTitleActivity implements IssueListContract.View, View.OnClickListener {

    @BindView(R.id.swipelayout)          SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.recyclerView)         RecyclerView        listView;

    private IssueListContract.Presenter mPresenter;
    private FastListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_fast_list));
        getBaseHeadView().showBackButton(this);
//        getBaseHeadView().showHeadRightImageButton(R.drawable.person_release_dynamic_icon_add,this);

        refreshLayout.setColorSchemeResources(Constant.REFRESH_COLOR);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.getData(true);
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

//            case R.id.HeadRightImageButton:
//                Intent intent = new Intent(this, ApplyGuaranteeActivity.class);
//                startActivity(intent);
//                break;
        }
    }

    @Subscribe
    public void onEvent(EventClickBean bean){
    }

    @Override
    public void initData() {
        new FastListPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<PurchaseListBean> datas) {
        adapter = new FastListAdapter(this,datas,R.layout.item_list_fast);
        listView.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                GuaranteeDetailedActivity.startActivity(FastListActivity.this, adapter.getList().get(position).getId() + "");
            }
        });

        adapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                showDeletDialog(position);
            }
        });
    }

    VertifanceFlowDialog mVertifanceFlowDialog;


    //删除
    private int position;
    private AlertDialog deleteDialog;
    private void showDeletDialog(final int position) {
        this.position = position;
        if(deleteDialog == null){
            deleteDialog = DialogUtil.showInputDialog(this, false, "", "确认删除这条交易记录？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.deleteDeal(adapter.getList().get(FastListActivity.this.position).getId());
                    deleteDialog.dismiss();
                }
            });
        }else{
            deleteDialog.show();
        }

    }

    @Override
    public void showRefreshFinish(List<PurchaseListBean> datas) {
        if(adapter != null){
            listView.notifyChangeData(datas,adapter);
        }
    }

    @Override
    public void showLoadMore(List<PurchaseListBean> datas) {
        listView.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showDeleteSuccess(int id) {
        for (int i = 0; i < adapter.getList().size(); i++) {
            if(id == adapter.getList().get(i).getId()){
                listView.removeData(adapter,i);
            }
        }
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        refreshLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        if(adapter == null){
            getBaseEmptyView().showEmptyContent();
        }
    }

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        if(adapter == null){
            getBaseEmptyView().showEmptyContent(info);
        }else{
            ToastUtil.showShort(this,info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(false);
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    public void setPresenter(IssueListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        if(mVertifanceFlowDialog != null){
            CheaekUtil.getInstance().cancelTask(this);
            mVertifanceFlowDialog = null;
        }
        mPresenter.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

}
