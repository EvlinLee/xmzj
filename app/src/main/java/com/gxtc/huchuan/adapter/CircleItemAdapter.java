package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbBasePagerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MineCircleBean;

import java.util.List;

public class CircleItemAdapter extends AbBasePagerAdapter<List<MineCircleBean>> {

    private OnItemClickListener clickListener;

    public CircleItemAdapter(Context context, List<View> views, List<List<MineCircleBean>> datas) {
        super(context, views, datas);
    }

    @Override
    public void bindData(View view, List<MineCircleBean> mineCircleBeen, int position) {
        GridAdapter adapter = new GridAdapter(getContext(), mineCircleBeen, R.layout.item_circle_home_recommend);
        view.setTag(mineCircleBeen);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener(){
            @Override
            public void onItemClick(RecyclerView parentView, View v, int position) {
                if(clickListener != null){
                    List<MineCircleBean> beans = (List<MineCircleBean>) parentView.getTag();
                    clickListener.onItemClick(beans.get(position),position);
                }
            }
        });

    }

    public interface OnItemClickListener {
        void onItemClick(MineCircleBean bean, int position);
    }

    public void setOnItemClickListener(CircleItemAdapter.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }


    private class GridAdapter extends BaseRecyclerAdapter<MineCircleBean> {

        int width = 0;

        GridAdapter(Context context, List<MineCircleBean> datas, int itemLayoutId) {
            super(context, datas, itemLayoutId);
//            int margin = (int) context.getResources().getDimension(R.dimen.margin_small) + 10;
//            width = WindowUtil.getScreenWidth(context) / 4 - margin * 4;
        }

        @Override
        public void bindData(ViewHolder holder, int position, MineCircleBean bean) {
            TextView tvGroupName = (TextView) holder.getView(R.id.tv_group_name);
            //TextView tvName = (TextView) holder.getView(R.id.tv_name);
            ImageView imgHead = (ImageView) holder.getView(R.id.img_head);
            ImageView imgRecommend = (ImageView) holder.getView(R.id.img_recomend);

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgHead.getLayoutParams();
//            params.width = width;
//            params.height = width;

            tvGroupName.setText(bean.getGroupName());
            //tvName.setText(bean.getUserName());

            ImageHelper.loadRound(getContext(), imgHead, bean.getCover(), 5);

            //未加入圈子
//            if(0 == bean.getIsJoin()){
//                imgRecommend.setVisibility(View.VISIBLE);
//            }else{
//                imgRecommend.setVisibility(View.INVISIBLE);
//            }
        }
    }

}
