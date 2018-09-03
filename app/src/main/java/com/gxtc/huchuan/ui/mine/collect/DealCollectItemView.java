package com.gxtc.huchuan.ui.mine.collect;

import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

class DealCollectItemView implements ItemViewDelegate<CollectionBean> {

    public DealCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_deal;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "3".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        DealListBean   bean     = collectionBean.getData();

        ImageView picRound = holder.getView(R.id.iv_news_collect_property);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picRound, bean.getUserPic(), R.drawable.person_icon_head);

        ImageView imgFace = holder.getView(R.id.img_face);
        ImageHelper.loadImage(holder.getConvertView().getContext(), imgFace, bean.getPicUrl());

        TextView title = holder.getView(R.id.tv_item_live_list_content);
        title.setText(bean.getUserName());
        holder.setText(R.id.tv_title, bean.getTitle()).setText(R.id.tv_time, DateUtil.showTimeAgo(bean.getCreateTime()));
    }
}
