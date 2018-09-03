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
 */
class ConversationTextCollectItemView implements ItemViewDelegate<CollectionBean> {
    public ConversationTextCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.conversation_text_collectt;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "6".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        ConversationTextBean bean = collectionBean.getData();
        if (bean == null) return;
        TextView userName = holder.getView(R.id.tv_circle_home_name);
        userName.setText(collectionBean.getUserName());

        TextView content = holder.getView(R.id.tv_circle_home_three_content);
        content.setText(bean.getContent());

        TextView time = holder.getView(R.id.tv_news_collect_time);
        time.setText(DateUtil.showTimeAgo(collectionBean.getCreateTime()));
        content.setText(bean.getContent());

        ImageView picHead = holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picHead, collectionBean.getUserPic(), R.drawable.person_icon_head);

    }
}
