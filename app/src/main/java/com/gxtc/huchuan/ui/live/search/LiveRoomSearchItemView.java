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
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 *
 */

public class LiveRoomSearchItemView implements ItemViewDelegate<SearchBean> {
    private static final String TAG = "TopicSearchItemView";
    private final Activity mActivity;

    public LiveRoomSearchItemView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_livesearch;
    }

    @Override
    public boolean isForViewType(SearchBean o, int position) {
        return "3".equals(o.getType());
    }

    @Override
    public void convert(final ViewHolder holder, final SearchBean o, int position) {
        List<LiveRoomBean> bean = o.getDatas();
        MyrecyclerViewAdapter myrecyclerViewAdapter=null;

        if (holder.getDataTag() == null) {
            android.support.v7.widget.RecyclerView recyclerView = holder.getView(R.id.rc_recyclerview);
             myrecyclerViewAdapter = new MyrecyclerViewAdapter(holder.getConvertView().getContext(), bean, R.layout.item_search_liveroom);
            holder.setDataTag(myrecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.getConvertView().getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            myrecyclerViewAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String id = ((MyrecyclerViewAdapter)holder.getDataTag()).getList().get(position).getId();
                    LiveHostPageActivity.startActivity(v.getContext(),"1", id);
                }
            });
            recyclerView.setAdapter((MyrecyclerViewAdapter) holder.getDataTag());
            holder.setText(R.id.tv_search_type_name, "课堂");
            holder.getView(R.id.tv_search_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("type","3");
                    map.put("searchKey",o.getSearchKey());
                    GotoUtil.goToActivityWithData(mActivity,LiveSearchMoreActivity.class,map);

                }
            });
        }else{
            myrecyclerViewAdapter=(MyrecyclerViewAdapter) holder.getDataTag();
            myrecyclerViewAdapter.changeDatas(bean);
        }

    }


    private static class MyrecyclerViewAdapter extends BaseRecyclerAdapter<LiveRoomBean> {


        @Override
        public int getItemCount() {
            return super.getItemCount() < 3 ? super.getItemCount() : 3;
        }

        public MyrecyclerViewAdapter(Context context, List<LiveRoomBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(ViewHolder holder, int position, LiveRoomBean liveRoomBean) {
            ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.iv_search_icon), liveRoomBean.getHeadpic(), R.drawable.person_icon_head_120);
            holder.setText(R.id.tv_search_title,liveRoomBean.getRoomname()).setText(R.id.tv_search_join_count,liveRoomBean.getFs()+"人次");
        }
    }
}
