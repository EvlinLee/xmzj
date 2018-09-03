package com.gxtc.huchuan.ui.mine.collect;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

/**
 * Created by Gubr on 2017/6/5.
 */

class TopicCollectItemView implements ItemViewDelegate<CollectionBean> {

    public TopicCollectItemView(CollectActivity collectActivity) {

    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_live;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "2".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        ChatInfosBean o = collectionBean.getData();

        ImageView picRound = holder.getView(R.id.iv_news_collect_property);
        TextView time = holder.getView(R.id.tv_news_collect_time);
        time.setText(DateUtil.showTimeAgo(o.getStarttime()));
        TextView title = holder.getView(R.id.tv_item_live_list_title);
        holder.setText(R.id.tv_item_live_list_title, o.getSubtitle())
              .setText(R.id.tv_item_live_list_content, o.getChatRoomName());

        boolean  isLiving = "2".equals(o.getStatus());
        boolean  isEnd    = "3".endsWith(o.getStatus());
        TextView price    = holder.getView(R.id.tv_item_live_list_price);

        if (!"0".equals(o.getChatSeries())) {
            price.setVisibility(View.GONE);
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_series_topic);

        } else if ("1".equals(o.getIsForGrop())) {
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_circle_topic);


            price.setVisibility(View.GONE);
        } else {
            setDrawableLeft(title, o.getSubtitle(), R.drawable.live_list_icon_topic);


            if (o.isFree()) {
                price.setText("免费");
                price.setTextColor(holder.getConvertView().getResources().getColor(R.color.color_119b1e));
            } else {
                price.setText(o.getFee() + "元");
                price.setTextColor(holder.getConvertView().getResources().getColor(R.color.color_fb4717));
            }
        }
        ImageHelper.loadImage(holder.getConvertView().getContext(), holder.getImageView(R.id.iv_item_live_list_head), o.getChatRoomHeadPic(), R.drawable.person_icon_head);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picRound, o.getHeadPic(), R.drawable.person_icon_head);
        TextView status = holder.getView(R.id.tv_item_live_list_status);
        switch (o.getStatus()) {
            //1：预告，2：直播中，3：结束
            case "1":
                status.setText("预告中");
                break;
            case "2":
                status.setText("进行中");
                break;
            case "3":
                status.setText("已开始");
                break;
        }


        ImageHelper.loadImage(MyApplication.getInstance(),
                (ImageView) holder.getView(R.id.iv_item_live_list_image), o.getFacePic(),
                R.drawable.live_list_place_holder);

    }


    private void setDrawableLeft(TextView textView, String coomtemt, int drawableId) {
        textView.setText(coomtemt);

    }
}
