package com.gxtc.huchuan.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.search.LiveSearchMoreActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/4.
 */

public class DealSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "DealSearchItemView";
    private final Activity mActivity;

    public DealSearchItemView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "4".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
            List<DealListBean> bean = o.getDatas();
            android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
            final Deal1LevelAdapter myrecyclerViewAdapter = new Deal1LevelAdapter(mActivity, bean, R.layout.deal_list_home_page);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(myrecyclerViewAdapter);
            myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter
                    .OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    DealListBean dealListBean = myrecyclerViewAdapter.getList().get(position);
                    Intent intent = new Intent(mActivity, GoodsDetailedActivity.class);
                    intent.putExtra(Constant.INTENT_DATA,dealListBean);
                    mActivity.startActivity(intent);
                }
            });
            holder.setText(R.id.tv_search_type_name, "交易");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type","4");
                    map.put("searchKey",o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity,LiveSearchMoreActivity.class,map);
                }
            });
        }
    }

