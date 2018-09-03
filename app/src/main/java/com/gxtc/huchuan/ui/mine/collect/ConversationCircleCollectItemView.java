package com.gxtc.huchuan.ui.mine.collect;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.utils.DateUtil;

/**
 * Created by Gubr on 2017/6/5.
 *
 */
class ConversationCircleCollectItemView implements ItemViewDelegate<CollectionBean> {
    public ConversationCircleCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.conversation_circle;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "8".equals(item.getType()) ;
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        CircleBean bean = collectionBean.getData();
        if(bean == null)    return;
        TextView userName= holder.getView(R.id.tv_circle_home_name);
        userName.setText(bean.getOwner());

        TextView time= holder.getView(R.id.tv_news_collect_time);
        time.setText(DateUtil.showTimeAgo(collectionBean.getCreateTime()));

        TextView circleContent= holder.getView(R.id.tv_circle_info);
        circleContent.setText(bean.getGroupName());

        TextView content= holder.getView(R.id.content);
        content.setMaxLines(1);
        content.setEllipsize(TextUtils.TruncateAt.END);
        content.setText(bean.getContent());
        ImageView picContent= holder.getView(R.id.collect_image);
        ImageHelper.loadImage(holder.getConvertView().getContext(),picContent,bean.getCover());

        ImageView picHead= holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadCircle(holder.getConvertView().getContext(),picHead,bean.getOwnerPic(), R.drawable.person_icon_head);

    }
}
