package com.gxtc.huchuan.ui.live.search;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveRoomNewAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */

public class TopicSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "TopicSearchItemView";
    private final Activity mActivity;

    private CircleShareHandler mShareHandler;

    public TopicSearchItemView(Activity activity) {
        mActivity = activity;
        mShareHandler = new CircleShareHandler(mActivity);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "2".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
            List<ChatInfosBean> bean                  = o.getDatas();
            android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
            final LiveRoomNewAdapter myrecyclerViewAdapter = new LiveRoomNewAdapter(holder.getConvertView().getContext(), bean, R.layout.item_live_new_room);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(
                    new BaseRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            String id = myrecyclerViewAdapter.getList().get(position).getId();
                            mShareHandler.getLiveInfo(id,null);
                        }
                    });
            recyclerView.setAdapter(myrecyclerViewAdapter);
            holder.setText(R.id.tv_search_type_name, "课程");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type", "2");
                    map.put("searchKey", o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity, LiveSearchMoreActivity.class, map);
                }
            });
        }
    }

