package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AllOrderAdapter;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity;
import com.gxtc.huchuan.ui.mall.order.MallOrderDetailActivity;
import com.gxtc.huchuan.ui.mine.classroom.classorderdetail.ClassOrderDetail;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * 来自 苏修伟 on 2018/4/23.
 * 订单列表 -> 全部订单
 *
 */

public class AllOrderFragment extends BaseTitleFragment implements PurchaseRecordContract.View{
    @BindView(R.id.rc_list) RecyclerView mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private PurchaseRecordContract.Presenter mPresenter;
    private AllOrderAdapter                adapter;

    private int start = 0;
    private String type = "0";
    private AlertDialog mAlertDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.common_recyclerview, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        type = getArguments().getString("type") == null ? "0" : getArguments().getString("type");
        new PurchaseRecordPresenter(this);
        mPresenter.getAllOrderData(false, type, start);

        initRecyclerView();
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0;
                mPresenter.getAllOrderData(true,type, start);
                mRcList.reLoadFinish();
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                start += 15;
                mPresenter.getAllOrderData(false,type, start);
            }
        });

    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        mRcList.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        adapter = new AllOrderAdapter(getActivity(),new ArrayList<AllPurchaseListBean>(),new int[]{R.layout.item_list_purchase3, R.layout.mall_order_status_item_layout});
        mRcList.setAdapter(adapter);
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog =  DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {

    }

    @Override
    public void showCircleData(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showDealData(List<PurchaseListBean> datas) { }

    @Override
    public void showAllOrderData(List<AllPurchaseListBean> datas) {
        mSwipeLayout.setRefreshing(false);
//        adapter.notifyChangeData(datas);
        if(datas.size() ==0)
            mRcList.loadFinish();
        if(start == 0){
            adapter.getDatas().clear();
            mRcList.notifyChangeData(datas, adapter);
        }else{
            mRcList.changeData(datas, adapter);
        }

        adapter.setOnReItemOnClickListener(new
                  BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
                  @Override
                  public void onItemClick(View v, int position) {
                      AllPurchaseListBean bean = adapter.getDatas().get(position);
                      switch (bean.getType()){
                          case 1:
                          case 2:
                          case 3:
                              GotoUtil.goToActivity(getActivity(),ClassOrderDetail.class,100, bean);
                              break;

                          case 4:
                              gotoOrderDetailed(bean);
                              break;

                          case 5:
                              Intent intent = new Intent(getActivity(), MallOrderDetailActivity.class);
                              intent.putExtra("orderNo", bean.getOrderNo());
                              startActivity(intent);
                              break;
                      }
                  }
                  });
    }




    @Override
    public void showLoadMoreSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {

    }



    // 交易的  跳转订单详情页
    private void gotoOrderDetailed(AllPurchaseListBean bean){
        Intent intent ;
        //是否是买家。0：买家；1：卖家
//        if(bean.getBuyer() == 0){
            intent = new Intent(getActivity(), OrderDetailedBuyerActivity.class);
//        }else {
//            intent = new Intent(getActivity(), OrderDetailedActivity.class);
//        }
        intent.putExtra(Constant.INTENT_DATA,bean.getId() + "");
        startActivityForResult(intent,110);
    }

    @Override
    public void showLoadMoreCircle(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showLoadMoreDeal(List<PurchaseListBean> datas) { }

    @Override
    public void showNoMore() {
    }

    @Override
    public void setPresenter(PurchaseRecordContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {
        mPresenter.getAllOrderData(false,type, start);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getActivity(),info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
