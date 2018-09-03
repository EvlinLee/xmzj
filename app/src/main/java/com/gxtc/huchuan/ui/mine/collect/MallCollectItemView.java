package com.gxtc.huchuan.ui.mine.collect;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

class MallCollectItemView implements ItemViewDelegate<CollectionBean> {

    public MallCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_mall;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "10".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        CollectMallDetailBean bean = collectionBean.getData();

        ImageView picRound = holder.getView(R.id.iv_news_collect_property);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picRound, collectionBean.getUserPic());

        ImageView imgFace = holder.getView(R.id.img_face);
        if( bean != null){
            ImageHelper.loadImage(holder.getConvertView().getContext(), imgFace, bean.getFacePic(), R.drawable.person_icon_head);
        }

        TextView title = holder.getView(R.id.tv_name);
        title.setText(collectionBean.getUserName());

        holder.setText(R.id.tv_title, bean.getStoreName()).setText(R.id.tv_time, DateUtil.showTimeAgo(collectionBean.getCreateTime()));
    }
}
