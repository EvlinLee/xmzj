package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.serializer.BeanContext;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.ui.mall.order.MallOrderDetailActivity;
import com.gxtc.huchuan.utils.ClipboardUtil;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.List;

/**
 * 来自 苏修伟 on 2018/4/23.
 */
public class AllOrderAdapter extends BaseMoreTypeRecyclerAdapter<AllPurchaseListBean> {


    public AllOrderAdapter(Context mContext, List<AllPurchaseListBean> datas, int... resid) {
        super(mContext, datas, resid);
    }

    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, AllPurchaseListBean allOrderBean) {
        switch (allOrderBean.getType()){

            case 1:
                noMall(holder, position, allOrderBean, "课程");
                break;
            case 2:
                noMall(holder, position, allOrderBean, "系列课");
                break;
            case 3:
                noMall(holder, position, allOrderBean, "圈子");
                break;
            case 4:
                noMall(holder, position, allOrderBean ,"交易");
                break;
            case 5:
                Mall(holder, position, allOrderBean);
                break;
        }
    }

    private void noMall(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, AllPurchaseListBean allOrderBean,String title){
        TextView name = (TextView) holder.getView(R.id.tv_name);
        TextView mun = (TextView) holder.getView(R.id.tv_mun);
        name.setText(title);
        TextView status = (TextView) holder.getView(R.id.tv_status);
        ImageView imghead = (ImageView) holder.getView(R.id.img_head);
        TextView price = (TextView) holder.getView(R.id.price);
        TextView number = (TextView) holder.getView(R.id.tv_time);
        TextView tvSubtitle = (TextView) holder.getView(R.id.tv_type);
        TextView goodsName = (TextView) holder.getView(R.id.tv_goods_name);
        TextView orderno = (TextView) holder.getView(R.id.tv_order_no);
        String textstatus = "";
        String subtitle = allOrderBean.getTitle();
        String subtitle2 = allOrderBean.getAssistantTitle();
        String time = DateUtil.stampToDate(allOrderBean.getCreateTime() + "");
        String fee = StringUtil.formatMoney(2 , allOrderBean.getFee());
        String cover = allOrderBean.getCover();
        final String no = allOrderBean.getOrderNo();
        switch (allOrderBean.getType()){
            case 1:
            case 2:

                if(allOrderBean.getIsRefund() == 3){
                    textstatus = "退款被拒";
                }else if(allOrderBean.getIsRefund() == 2){
                    textstatus = "已退款";
                }else if(allOrderBean.getIsRefund() == 1){
                    textstatus = "退款中";
                }else if(allOrderBean.getIsSett() == 1){
                    textstatus = "已结算";
                }else if(allOrderBean.getIsPay() == 1){
                    textstatus = "已支付";
                }else{
                    textstatus = "未支付";
                }
//                if(allOrderBean.getIsPay() == 1){
//                    textstatus = "已开始";
//                }else{
//                    textstatus = "未开始";
//                }

                break;
            case 3:
                if(allOrderBean.getIsSett() == 1){
                    textstatus = "已结算";
                }else if(allOrderBean.getIsPay() == 1){
                    textstatus = "已支付";
                }else{
                    textstatus = "未支付";
                }

                break;
            case 4:
                switch (allOrderBean.getOrderStart()){
                    case 0:
                        textstatus = "待卖家同意";
                        break;

                    case 1:
                        textstatus = "卖家同意";
                        break;

                    case 2:
                        textstatus = "卖家不同意";
                        break;

                    case 3:
                        textstatus = "买家支付";
                        break;

                    case 4:
                        textstatus = "卖家交付";
                        break;

                    case 5:
                        textstatus = "交易完成";
                        break;

                    case 10:
                        textstatus = "交易关闭";
                        break;

                }
                break;
        }
        status.setText(textstatus);
        tvSubtitle.setText(subtitle2);
        mun.setText("×" + (allOrderBean.getNumber()== 0? 1 : allOrderBean.getNumber()));
//        ImageHelper.loadCircle(context, imghead, bean.getFacePic());
        ImageHelper.loadRound(mContext,imghead, cover, 5);
        goodsName.setText(subtitle);
        number.setText( time.substring(0, time.length() - 3));
        TextView sum = (TextView) holder.getView(R.id.tv_money);
        sum.setText("金额：" + fee);
        price.setText("金额：" + fee);
        orderno.setText("订单号：" + no);
        holder.getView(R.id.order_layout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardUtil.copyText(no);
                return false;
            }
        });
    }

    private void Mall(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, final AllPurchaseListBean allOrderBean){
        TextView name = (TextView) holder.getView(R.id.tv_name);
        name.setText("商城");
        TextView status = (TextView) holder.getView(R.id.tv_status);
        switch (allOrderBean.getOrderStart()){
            case 0:
                status .setText("已取消");
                break;

            case 1:
                status .setText("未支付");
                break;

            case 2:
                status .setText("待发货");
                break;

            case 3:
                status .setText("待收货");
                break;

            case 4:
                status .setText("待评价");
                break;

            case 5:
                status .setText("已完成");
                break;
        }
        TextView order = (TextView) holder.getView(R.id.tv_order_no);
        TextView number = (TextView) holder.getView(R.id.tv_time);
        String time = DateUtil.stampToDate(allOrderBean.getCreateTime() + "", "yyyy-MM-dd HH:mm") ;
        number.setText(time);
        order.setText("订单号：" + allOrderBean.getOrderNo());
        RecyclerView mRecyclerView = (RecyclerView) holder.getView(R.id.mall_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        List<AllPurchaseListBean.Commodity> commodity =  allOrderBean.getExtra();
        OrderMallItemAdater adater = new OrderMallItemAdater(mContext, commodity ,R.layout.mall_order_item_layout);
        mRecyclerView.setAdapter(adater);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(mContext,1),mContext.getResources().getColor(R.color.white)));
        TextView sum = (TextView) holder.getView(R.id.tv_money);
        sum.setText("金额：" + StringUtil.formatMoney(2 , allOrderBean.getFee()) );
        adater.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MallOrderDetailActivity.Companion.junmpToOrderDetailActivity(mContext,allOrderBean.getOrderNo());
            }
        });
        holder.getView(R.id.order_layout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardUtil.copyText(allOrderBean.getOrderNo());
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(super.getDatas().get(position).getType() == 5)
            return 1;
        return 0;

    }
}
