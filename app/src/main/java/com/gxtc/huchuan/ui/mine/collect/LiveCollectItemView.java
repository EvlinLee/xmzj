package com.gxtc.huchuan.ui.mine.collect;

import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectMallDetailBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.utils.DateUtil;

class LiveCollectItemView implements ItemViewDelegate<CollectionBean> {

    public LiveCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_live_room;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "12".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        LiveRoomBean bean = collectionBean.getData();

        ImageView picRound = holder.getView(R.id.iv_news_collect_property);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picRound, bean.getHeadpic(), R.drawable.person_icon_head);

        ImageView imgFace = holder.getView(R.id.img_face);
        ImageHelper.loadImage(holder.getConvertView().getContext(), imgFace, bean.getBakpic(), R.drawable.person_icon_head);

        TextView title = holder.getView(R.id.tv_name);
        title.setText(bean.getName());

        TextView introduce = holder.getView(R.id.tv_introduce);
        introduce.setText(bean.getIntroduce());

        holder.setText(R.id.tv_title, bean.getName()+"的课堂").setText(R.id.tv_time, DateUtil.showTimeAgo(collectionBean.getCreateTime()));
    }
}
