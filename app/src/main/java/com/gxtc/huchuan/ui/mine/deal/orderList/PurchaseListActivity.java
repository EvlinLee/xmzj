package com.gxtc.huchuan.ui.mine.deal.orderList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DealRecordAdapter;
import com.gxtc.huchuan.adapter.PurchaseListAdapter;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * 订单列表页面
 */
public class PurchaseListActivity extends BaseTitleActivity implements PurchaseListContract.View, View.OnClickListener {

    @BindView(R.id.swipelayout)          SwipeRefreshLayout  refreshLayout;
    @BindView(R.id.recyclerView)         RecyclerView        listView;

    private PurchaseListContract.Presenter mPresenter;
    private DealRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_order_list));
        getBaseHeadView().showBackButton(this);

        refreshLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
//        listView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(this,10), Color.parseColor("#ECEDEE")));
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

        if(bean.bean instanceof Boolean){
         
            mPresenter.getData(false);
        }
    }

    @Override
    public void initData() {
        new PurchaseListPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<PurchaseListBean> datas) {
//        adapter = new PurchaseListAdapter(this,datas,new int[]{R.layout.item_list_purchase,R.layout.item_list_purchase2});
        adapter = new DealRecordAdapter(this,datas, R.layout.item_list_purchase3);
        listView.setAdapter(adapter);
        listView.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                gotoOrderDetailed(adapter.getList().get(position));
            }
        });
//        adapter.setOnReItemOnClickListener(new BaseRecyclerTypeAdapter.OnReItemOnClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                gotoOrderDetailed(adapter.getList().get(position));
//            }
//        });
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
                getBaseEmptyView().hideEmptyView();
                mPresenter.getData(false);
            }
        });
    }

    @Override
    public void setPresenter(PurchaseListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mPresenter.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constant.ResponseCode.ORDER_CANCEL && data != null){
            String id = data.getStringExtra(Constant.INTENT_DATA);
            for (int i = 0; i < adapter.getList().size(); i++) {
                if(String.valueOf(adapter.getList().get(i).getId()).equals(id)){
                    listView.removeData(adapter,i);
                }
            }
        }
        if(resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE){
            mPresenter.getData(false);
        }
    }

    //跳转订单详情页
    private void gotoOrderDetailed(PurchaseListBean bean){
        Intent intent ;
        //是否是买家。0：买家；1：卖家
        if(bean.getBuyer() == 0){
            intent = new Intent(this, OrderDetailedBuyerActivity.class);
        }else {
            intent = new Intent(this, OrderDetailedActivity.class);
        }
        intent.putExtra(Constant.INTENT_DATA,bean.getId() + "");
        startActivityForResult(intent,110);
    }

}
