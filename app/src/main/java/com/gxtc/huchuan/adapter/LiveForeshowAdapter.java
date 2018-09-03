package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;

import java.util.List;

/**
 * 直播预告适配器
 * Created by Gubr on 2017/2/10.
 */

public class LiveForeshowAdapter extends BaseRecyclerAdapter<ChatInfosBean> {


    public LiveForeshowAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ChatInfosBean o) {
        if (o == null) {
            return;
        }
        holder.setText(R.id.item_live_foreshow_title, o.getSubtitle())
                .setText(R.id.item_live_foreshow_count, o.getJoinCount()+"人报名");
        ImageHelper.loadImage(holder.getItemView().getContext(),
                holder.getImageView(R.id.item_live_foreshow_photo),
                o.getFacePic(),
                R.drawable.news_home_place_holder_video);

        TextView price = holder.getViewV2(R.id.item_live_foreshow_price);
        if (o.isFree()) {
            price.setText("免费");
            price.setTextColor(price.getContext().getResources().getColor(R.color.color_119b1e));
        } else {
            price.setText("￥"+o.getFee()+"元");
            price.setTextColor(price.getContext().getResources().getColor(R.color.color_price_ec6b46));
        }

    }


}
