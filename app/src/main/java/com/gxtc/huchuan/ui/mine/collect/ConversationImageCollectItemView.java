package com.gxtc.huchuan.ui.mine.collect;

import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.ConversationTextBean;
import com.gxtc.huchuan.utils.DateUtil;

/**
 * Created by Gubr on 2017/6/5.
 *
 */
class ConversationImageCollectItemView implements ItemViewDelegate<CollectionBean> {
    public ConversationImageCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.conversation_image;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "7".equals(item.getType()) ;
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        ConversationTextBean bean = collectionBean.getData();
        if(bean == null)    return;
        TextView userName= holder.getView(R.id.tv_circle_home_name);
        userName.setText(collectionBean.getUserName());

        TextView time= holder.getView(R.id.tv_news_collect_time);
        time.setText(DateUtil.showTimeAgo(collectionBean.getCreateTime()));

        ImageView picContent= holder.getView(R.id.collect_image);
        ImageHelper.loadImage(holder.getConvertView().getContext(),picContent,bean.getContent());

        ImageView picHead= holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadCircle(holder.getConvertView().getContext(),picHead,collectionBean.getUserPic(), R.drawable.person_icon_head);

    }
}
