package com.gxtc.huchuan.ui.mine.collect;

import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

class VideoCollectItemView implements ItemViewDelegate<CollectionBean> {

    public VideoCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_video;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "11".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        ConversationTextBean bean = collectionBean.getData();

        ImageView picRound = holder.getView(R.id.iv_head_img);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picRound, collectionBean.getUserPic(), R.drawable.person_icon_head);

        TextView title = holder.getView(R.id.tv_name);
        title.setText(collectionBean.getUserName());

        ImageView imgFace = holder.getView(R.id.img_face);
        ImageHelper.loadImage(holder.getConvertView().getContext(), imgFace, bean.getTitle());

        TextView time= holder.getView(R.id.tv_news_collect_time);
        time.setText(DateUtil.showTimeAgo(collectionBean.getCreateTime()));
    }
}
