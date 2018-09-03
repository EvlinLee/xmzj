package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatSeriesBean;
import com.gxtc.huchuan.bean.LiveRoomBean;

import java.util.List;

/**
 * Created by sjr on 2017/3/9.
 * 系列课适配器
 */

public class SeriesCourseAdapter extends BaseRecyclerAdapter<ChatSeriesBean> {
    private Context mContext;
    private View.OnClickListener moreListener;
    private LiveRoomBean mLiveRoomBean;

    public SeriesCourseAdapter(Context context, List<ChatSeriesBean> list, int itemLayoutId, LiveRoomBean bean) {
        super(context, list, itemLayoutId);
        this.mContext = context;
        this.mLiveRoomBean = bean;
    }
    public void setMoreListener(View.OnClickListener moreListener) {
        this.moreListener = moreListener;
    }

    @Override
    public void bindData(ViewHolder holder,final int position, final ChatSeriesBean bean) {
        //系列课图片
        ImageView imageView = (ImageView) holder.getView(R.id.iv_item_topic_image);
        ImageHelper.loadImage(mContext, imageView, bean.getHeadpic(), R.drawable.circle_place_holder_246);

        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_item_topic_title);
        tvTitle.setText(bean.getSeriesname());

        //购买人数
        TextView tvPerson = (TextView) holder.getView(R.id.tv_item_topic_people);
        tvPerson.setText(bean.getBuyCount() + "人次");

        //购买人数
        TextView tvToatal= (TextView) holder.getView(R.id.tv_total);
        tvToatal.setText("共有"+bean.getChatInfoCount()+"节");


        //价格
        TextView tvPrice = (TextView) holder.getView(R.id.tv_item_topic_price);
        if ("0".equals(bean.getFee())) {//免费
            tvPrice.setTextColor(mContext.getResources().getColor(R.color.series_free));
            tvPrice.setText("免费");
        } else {
            tvPrice.setTextColor(mContext.getResources().getColor(R.color.series_no_free));
            tvPrice.setText("￥"+bean.getFee()+"元");
        }

        TextView tvMore = (TextView) holder.getView(R.id.tv_item_topic_manager);

        if("1".equals(this.mLiveRoomBean.getIsSelf())){
            tvMore.setVisibility(View.VISIBLE);
            tvMore.setTag(position);
            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(moreListener != null){
                        moreListener.onClick(v);
                    }
                }
            });
        }else{
            tvMore.setVisibility(View.GONE);
        }


    }
}
