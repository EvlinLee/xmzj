package com.gxtc.huchuan.ui.mine.collect;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.utils.DateUtil;

/**
 * Created by sjr on 2018/5/31.
 */

class SpecialtItemView implements ItemViewDelegate<CollectionBean> {
    public SpecialtItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_news;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "13".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean bean, int position) {
        SpecialBean specialBean =  bean.getData();
        //标题
        TextView tvTitle =  holder.getView(R.id.tv_news_collect_title);
        tvTitle.setText(specialBean.getName());

        //作者
//        TextView tvAuthor =  holder.getView(R.id.tv_news_collect_author);
//        tvAuthor.setText(newsBean.getSource());

        //时间
        TextView tvTime =  holder.getView(R.id.tv_news_collect_time);
        tvTime.setText(DateUtil.showTimeAgo(bean.getCreateTime()));

        //封面
//        ImageView ivCover = holder.getView(R.id.iv_news_collect_cover);
//        ImageHelper.loadImage(holder.getConvertView().getContext(), ivCover, specialBean.getCover());

        //头像
        ImageView picHead = holder.getView(R.id.iv_news_collect_property);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picHead, specialBean.getPic(), R.drawable.person_icon_head);



    }
}
