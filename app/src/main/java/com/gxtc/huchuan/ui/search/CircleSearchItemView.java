package com.gxtc.huchuan.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleListAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleListActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.search.LiveSearchMoreActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/4/4.
 */

public class CircleSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "CircleSearchItemView";
    private final Activity mActivity;

    public CircleSearchItemView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "6".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
        List<CircleBean> bean = o.getDatas();

        android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
//            myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder
//                    .getConvertView().getContext(), bean, R.layout.item_search_circle);
        final CircleListAdapter myrecyclerViewAdapter = new CircleListAdapter(mActivity, bean, R.layout.item_circle_find_list);
        holder.setDataTag(myrecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CircleBean circleBean = myrecyclerViewAdapter.getList().get(
                        position);
                String url     = circleBean.getJoinUrl();
                String name    = circleBean.getGroupName();
                double money   = circleBean.getFee();
                int    id      = circleBean.getId();
                String isAudit = circleBean.getIsAudit();

                if (circleBean.getIsJoin() == 1) {
                    Intent intent = new Intent(mActivity, CircleMainActivity.class);
                    intent.putExtra("groupId", circleBean.getId());
                    mActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent(mActivity, CircleJoinActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("isAudit", isAudit);
                    intent.putExtra(Constant.INTENT_DATA, money);
                    mActivity.startActivityForResult(intent, 0);
                }
            }
        });
        recyclerView.setAdapter(myrecyclerViewAdapter);
        holder.setText(R.id.tv_search_type_name, "圈子");
        holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "6");
                map.put("searchKey", o.getSearchKey());
                GotoUtil.goToActivityWithData(mActivity, LiveSearchMoreActivity.class, map);

            }
        });

    }
}