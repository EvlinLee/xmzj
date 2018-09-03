package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbBasePagerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.widget.OtherGridView;

import java.util.List;

public class DealTabPageAdapter extends AbBasePagerAdapter<List<DealTypeBean>> {
    private OnItemClickListener clickListener;

    public DealTabPageAdapter(Context context, List<View> views, List<List<DealTypeBean>> datas) {
        super(context, views, datas);
    }

    @Override
    public void bindData(View view, List<DealTypeBean> dealTypeBeen, int position) {
        GridAdapter adapter = new GridAdapter(getContext(), dealTypeBeen, R.layout.item_live_head);
        view.setTag(dealTypeBeen);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener(){
            @Override
            public void onItemClick(RecyclerView parentView, View v, int position) {
                if(clickListener != null){
                    List<DealTypeBean> bean = (List<DealTypeBean>) parentView.getTag();
                    clickListener.onItemClick(bean.get(position),position);
                }
            }
        });

    }


    private class GridAdapter extends BaseRecyclerAdapter<DealTypeBean> {

        GridAdapter(Context context, List<DealTypeBean> list, int itemLayoutId) {
            super(context, list, itemLayoutId);
        }

        @Override
        public void bindData(ViewHolder holder, int position, DealTypeBean dealTypeBean) {
            TextView tvName = (TextView) holder.getView(R.id.item_live_head_title);
            ImageView    img    = (ImageView) holder.getView(R.id.item_live_head_icon);

            tvName.setText(dealTypeBean.getTypeName());
            ImageHelper.loadImage(getContext(),img,dealTypeBean.getPic());
        }
    }

    public interface OnItemClickListener {

        void onItemClick(DealTypeBean bean ,int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
