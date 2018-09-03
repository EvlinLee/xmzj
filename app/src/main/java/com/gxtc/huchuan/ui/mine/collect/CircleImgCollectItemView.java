package com.gxtc.huchuan.ui.mine.collect;

import android.view.View;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleHomeImgAdapter;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.widget.ScrollGridLayoutManager;

/**
 * Created by Gubr on 2017/6/5.
 */

class CircleImgCollectItemView extends CircleCollectItemView<CollectionBean> {
    public CircleImgCollectItemView(CollectActivity collectActivity,RecyclerView recyclerView) {
        super(collectActivity,recyclerView);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_circle_img;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        if ("4".equals(item.getType())){
            CircleHomeBean itemdata =  item.getData();
            if (2==itemdata.getType()) {
                return true;
            }else if (3==(itemdata.getType())){
                return  true;
            }else if (4==(itemdata.getType())){
                return true;
            }else if (5==itemdata.getType()){
                return true;
            }
        }
        return false;
    }



    @Override
    protected void convertContent(ViewHolder holder, final CircleHomeBean bean, int position) {
        android.support.v7.widget.RecyclerView   recyclerView = (android.support.v7.widget.RecyclerView) holder.getView(R.id.rv_circle_home_item);
        recyclerView.setTag(bean);

        recyclerView.setNestedScrollingEnabled(false);
        holder.getView(R.id.layout_time_share).setVisibility(View.GONE);
        CircleHomeImgAdapter   mAdapter = new CircleHomeImgAdapter(holder.getConvertView().getContext(), bean.getPicList(),
                R.layout.item_circle_home_img2, R.layout.item_circle_home_img);

        ScrollGridLayoutManager manager;
        if (bean.getPicList().size() == 1) {
            manager = new ScrollGridLayoutManager(holder.getConvertView().getContext(), 1);

        } else if (bean.getPicList().size() != 4) {
            manager = new ScrollGridLayoutManager(holder.getConvertView().getContext(), 3);

        } else {
            manager = new ScrollGridLayoutManager(holder.getConvertView().getContext(), 2);
        }

        manager.setScrollEnabled(false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                goImgActivity(bean, position);
            }
        });
    }
}
