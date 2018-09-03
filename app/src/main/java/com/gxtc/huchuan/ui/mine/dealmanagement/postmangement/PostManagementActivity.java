package com.gxtc.huchuan.ui.mine.dealmanagement.postmangement;

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
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DealManagementAdapter;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventClassHistoryBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.deal.MyIssueDetailedActivity;
import com.gxtc.huchuan.ui.mine.deal.issueDeal.IssueDealActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Describe:
 * Created by ALing on 2017/5/17 0017.
 */

public class PostManagementActivity extends BaseTitleActivity implements View.OnClickListener, DealManagementContract.View{
    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.ll_del)
    LinearLayout mLlDel;

    private DealManagementContract.Presenter mPresenter;
    private DealManagementAdapter adapter;
    private int count = 0;
    private ArrayList<String> delList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
        EventBusUtil.register(this);
    }
    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_issue_list));
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_release_dynamic_icon_add, this);
        getBaseHeadView().showBackButton(this);

        mLlDel.setVisibility(View.GONE);
        mSwipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRcList.reLoadFinish();
                mPresenter.getData(true);
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });

    }

    @OnClick(R.id.ll_del)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;
            case R.id.HeadRightImageButton:
                GotoUtil.goToActivity(this,IssueDealActivity.class);
                break;
            case R.id.ll_del:
                showDeleteDialog();
                break;
        }
    }

    @Override
    public void initData() {
        new DealManagementPresenter(this);
        mPresenter.getData(false);
        delList = new ArrayList<>();
    }
    //删除
    private AlertDialog mDialog;
    private void showDeleteDialog() {
        if(count == 0){
            ToastUtil.showShort(PostManagementActivity.this,"请选择要删除的条目");
            return;
        }
        mDialog = DialogUtil.createDialog(this, "提示", "确定要删除"+ count + "条记录吗？", "取消",
                "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        if (UserManager.getInstance().isLogin()){
                            mPresenter.deleteDel(UserManager.getInstance().getToken(),delList);
                        }
                        mDialog.dismiss();
                    }

                });
        mDialog.show();
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<PurchaseListBean> datas) {
        adapter = new DealManagementAdapter(this,datas,R.layout.item_deal_management);
        mRcList.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int id = adapter.getList().get(position).getId();
                Intent intent = new Intent(PostManagementActivity.this, MyIssueDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA,id);
                startActivityForResult(intent,0);
            }
        });

        //长按出现删除
        adapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                ToastUtil.showShort(PostManagementActivity.this,"long");
                for (int i = 0; i < adapter.getList().size(); i++) {
                    adapter.getList().get(i).setShow(true);
                    mRcList.notifyItemChanged(i);
                }
                mLlDel.setVisibility(View.VISIBLE);
                Log.d("tag", "onItemLongClick: "+mLlDel.getVisibility()+".."+View.VISIBLE);
            }
        });

    }

    @Override
    public void showRefreshFinish(List<PurchaseListBean> datas) {
        if(adapter != null){
            mRcList.notifyChangeData(datas,adapter);
        }
    }

    @Override
    public void showLoadMore(List<PurchaseListBean> datas) {
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void showDelResult(List<PurchaseListBean> datas) {
        ToastUtil.showShort(this,"del success");

        for (int i = 0; i < adapter.getList().size(); i++) {
            adapter.getList().get(i).setShow(false);
            mRcList.notifyItemChanged(i);
        }
        mLlDel.setVisibility(View.GONE);
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeLayout.setRefreshing(false);
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
    public void setPresenter(DealManagementContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mPresenter.destroy();
        EventBusUtil.unregister(false);
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

    /**
     * 删除按钮
     */
    @Subscribe
    public void onEvent(EventClassHistoryBean bean) {
        String chatInfoId = bean.getChatInfoId();

        if (bean.isSelected()){
            count ++;
            delList.add(chatInfoId);
            ToastUtil.showShort(this,delList.size()+"");
        }else {
            count --;
            if (count < 0){
                count = 0;
            }
            delList.remove(chatInfoId);
            ToastUtil.showShort(this,delList.size()+"");
        }
    }
}
