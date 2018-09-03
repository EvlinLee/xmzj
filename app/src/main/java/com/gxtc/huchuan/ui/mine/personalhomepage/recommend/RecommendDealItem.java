/*
package com.gxtc.huchuan.ui.mine.personalhomepage.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;

import java.util.List;

*/
/**
 * Describe:
 * Created by ALing on 2017/4/12 .
 *//*


public class RecommendDealItem implements ItemViewDelegate<PersonalHomeDataBean> {
    private Activity mContext = null;
    public RecommendDealItem(Activity context) {
        mContext = context;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_homepage_layout;
    }

    @Override
    public boolean isForViewType(PersonalHomeDataBean item, int position) {
        return "tradeInfo".equals(item.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final PersonalHomeDataBean personalHomeDataBean, int position) {
        List<DealListBean> bean = personalHomeDataBean.getData();
        MyrecyclerViewAdapter myrecyclerViewAdapter=null;
        if (holder.getDataTag() == null) {
            RecyclerView recyclerView = holder.getView(R.id
                    .rc_list);
            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext()
                    , bean, R.layout.item_homepage_deal);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView()
                    .getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter
                    .OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    DealListBean dealListBean = ((MyrecyclerViewAdapter) holder.getDataTag()).getList().get(position);
                    Intent intent = new Intent(mContext, MallDetailedActivity.class);
                    intent.putExtra(Constant.INTENT_DATA,dealListBean);
                    mContext.startActivity(intent);
                }
            });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_label_des, "交易");
            holder.getView(R.id.tv_more).setVisibility(View.GONE);

        }else{
            myrecyclerViewAdapter=(MyrecyclerViewAdapter) holder.getDataTag();
            myrecyclerViewAdapter.changeDatas(bean);
        }
    }
    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<DealListBean> {


        @Override
        public int getItemCount() {
            return super.getItemCount() < 3 ? super.getItemCount() : 3;
        }

        public MyrecyclerViewAdapter(Context context, List<DealListBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(BaseRecyclerAdapter.ViewHolder holder, int position, DealListBean dealBean) {
            holder.setText(R.id.tv_title,dealBean.getTitle());
                    */
/*.setText(R.id.tv_username,dealBean.getUserName())
                    .setText(R.id.tv_create_time,dealBean.getCreateTime());*//*

        }
    }
}
*/
