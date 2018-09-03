package com.gxtc.huchuan.ui.mine.deal.issueList;

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
import com.gxtc.huchuan.adapter.IssueListAdapter;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.mine.deal.MyIssueDetailedActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.utils.DialogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * 我的帖子页面
 */
public class IssueListActivity extends BaseTitleActivity implements IssueListContract.View, View.OnClickListener {

    @BindView(R.id.swipelayout)          SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.recyclerView)         RecyclerView        listView;

    private IssueListContract.Presenter mPresenter;
    private IssueListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_issue_list));
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
//                GotoUtil.goToActivityForResult(this,IssueDealActivity.class,0);
//                break;
        }
    }

    @Subscribe
    public void onEvent(EventClickBean bean){
        if(bean.action.equals("去支付")){
            ToastUtil.showShort(this,bean.action);
        }

        if(bean.action.equals("确认收货")){
            ToastUtil.showShort(this,bean.action);
        }
    }

    @Override
    public void initData() {
        new IssueListPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<PurchaseListBean> datas) {
        adapter = new IssueListAdapter(this,datas,R.layout.item_list_issue);
        listView.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (adapter.getList().get(position).getIsFinish()){
                    //审核不通过
                    case 1:
                        showVertifanceDialog(adapter.getList().get(position));
                        break;

                    default:
                        int id = adapter.getList().get(position).getId();
                        int isFinish = adapter.getList().get(position).getIsFinish();
                        Intent intent = new Intent(IssueListActivity.this, MyIssueDetailedActivity.class);
                        intent.putExtra(Constant.INTENT_DATA,id);
                        intent.putExtra("isFinish",isFinish);
                        startActivityForResult(intent,0);
                        break;
                }
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
    private void showVertifanceDialog(final PurchaseListBean bean) {
        if(mVertifanceFlowDialog == null){
            mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = bean.getId();
                int isFinish = bean.getIsFinish();
                Intent intent = new Intent(IssueListActivity.this, MyIssueDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA,id);
                intent.putExtra("isFinish",isFinish);
                startActivityForResult(intent,0);
                mVertifanceFlowDialog.dismiss();
            }
        });
        checkCicle(bean);
    }

    public void checkCicle(final PurchaseListBean bean){
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(), bean.getId()+"" , Constant.STATUE_LINKTYPE_TRANSACTION, new ApiCallBack<CheckBean>() {

            @Override
            public void onSuccess(CheckBean data) {
                if(data == null) return;
                mVertifanceFlowDialog.setFlowStatus(getString(R.string.issue_deal_faild) + data.getContent());
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_2);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(IssueListActivity.this,message);
            }
        }).addTask(this);
    }

    //删除帖子
    private int position;
    private AlertDialog deleteDialog;
    private void showDeletDialog(final int position) {
        this.position = position;
        if(deleteDialog == null){
            deleteDialog = DialogUtil.showInputDialog(this, false, "", "确认删除这条帖子？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.deleteDeal(adapter.getList().get(IssueListActivity.this.position).getId());
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

        if(resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE){
            mPresenter.getData(false);
        }

        if(resultCode == Constant.ResponseCode.NORMAL_FLAG){
            mPresenter.getData(false);
        }
    }

}
