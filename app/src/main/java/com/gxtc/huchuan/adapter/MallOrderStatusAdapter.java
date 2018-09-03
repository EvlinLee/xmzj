package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.OrderBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.ui.mall.order.MallOrderDetailActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.SystemUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zzg on 17/3/21.
 */

public class MallOrderStatusAdapter extends BaseRecyclerAdapter<OrderBean>{

    public OnClickBtnLisner mOnClickBtnLisner;

    public void setOnClickBtnListener(OnClickBtnLisner onClickBtnLisner){
        this.mOnClickBtnLisner = onClickBtnLisner;
    }

    public MallOrderStatusAdapter(Context context, List<OrderBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final OrderBean bean) {
      TextView name = (TextView) holder.getView(R.id.tv_name);
      name.setText(bean.getName());
      TextView status = (TextView) holder.getView(R.id.tv_status);
      TextView order = (TextView) holder.getView(R.id.tv_order_no);
//        TextView anotheOrder = (TextView) holder.getView(R.id.anothe_order);
//        TextView sure = (TextView) holder.getView(R.id.sure);
//        TextView cancel = (TextView) holder.getView(R.id.cancel);
//        TextView pay = (TextView) holder.getView(R.id.pay);
//      ImageView typelogo = holder.getImageView(R.id.iv_type_logo);
//      typelogo.setImageResource(R.drawable.discover_icon_shop1);
        //0:已取消 1:待付款 2:待发货 3:已发货 4：已退款 5：已结束
      switch (bean.getOrderStatus()){
          case "1":

              status.setText("待付款" );
//              cancel.setText("取消");
//              anotheOrder.setVisibility(View.GONE);
//              sure.setVisibility(View.GONE);
//              cancel.setVisibility(View.VISIBLE);
//              pay.setVisibility(View.VISIBLE);
              break;
          case "2":
              status.setText("待发货" );
//              anotheOrder.setVisibility(View.VISIBLE);
//              sure.setVisibility(View.GONE);
//              cancel.setVisibility(View.GONE);
//              pay.setVisibility(View.GONE);
              break;
          case "3":
              status.setText("已发货" );
//              anotheOrder.setVisibility(View.VISIBLE);
//              sure.setVisibility(View.VISIBLE);
//              cancel.setVisibility(View.GONE);
//              pay.setVisibility(View.GONE);
              break;
          case "4":
              status.setText("已退款" );
//              anotheOrder.setVisibility(View.VISIBLE);
//              sure.setVisibility(View.GONE);
//              cancel.setVisibility(View.VISIBLE);
//              cancel.setText("删除");
//              pay.setVisibility(View.GONE);
              break;
          case "5":
              status.setText("已完成" );
//              anotheOrder.setVisibility(View.VISIBLE);
//              sure.setVisibility(View.GONE);
//              cancel.setVisibility(View.VISIBLE);
//              cancel.setText("删除");
//              pay.setVisibility(View.GONE);
              break;

      }
        TextView number = (TextView) holder.getView(R.id.tv_time);
        number.setText( bean.getCreateTime().substring(0, bean.getCreateTime().length() - 3));
        order.setText("订单号：" + bean.getOrderId());
        RecyclerView mRecyclerView = (RecyclerView) holder.getView(R.id.mall_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
       ; OrderMallItemAdater adater = new OrderMallItemAdater(context,getData(bean.getStoreOrderList()),R.layout.mall_order_item_layout);
        mRecyclerView.setAdapter(adater);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(context,1),context.getResources().getColor(R.color.white)));
        TextView sum = (TextView) holder.getView(R.id.tv_money);
        sum.setText("金额：" + StringUtil.formatMoney(2 , bean.getMoney()) );
        adater.setOnReItemOnClickListener(new OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MallOrderDetailActivity.Companion.junmpToOrderDetailActivity(context,bean.getOrderId());
            }
        });
//        anotheOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnClickBtnLisner.anotherOrder(bean);
//            }
//        });
//        sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnClickBtnLisner.sure(bean,position);
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnClickBtnLisner.cancel(bean,position);
//            }
//        });
//        pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnClickBtnLisner.pay(bean);
//            }
//        });
    }

    public List<AllPurchaseListBean.Commodity> getData(List<OrderBean.OrderMallBean> data){
        List<AllPurchaseListBean.Commodity> list = new ArrayList<>();
        AllPurchaseListBean allPurchaseListBean = new AllPurchaseListBean();
        for(OrderBean.OrderMallBean bean : data){
            AllPurchaseListBean.Commodity commodity = allPurchaseListBean.getNewCommodity();
            commodity.setPic(bean.getPicUrl());
            commodity.setPrice(bean.getPrice());
            commodity.setSum(bean.getSum());
            commodity.setTitle(bean.getName());
            commodity.setFormatName(bean.getPriceName());
            list.add(commodity);
        }
        return list;
    }

    public interface OnClickBtnLisner{
        public void anotherOrder(OrderBean bean);
        public void sure(OrderBean bean,int posithon);
        public void cancel(OrderBean bean,int posithon);
        public void pay(OrderBean bean);
    }
    private SpannableString setLocalTextColor(String text1, String text2){

        SpannableString ss2 = new SpannableString(text1 + text2);
        TextAppearanceSpan dealSpan2 = new TextAppearanceSpan(context, R.style.circle_invite_text_color);
        ss2.setSpan(dealSpan2, text1.length(), text1.length()+text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss2;
    }

}
