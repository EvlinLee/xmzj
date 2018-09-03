package com.gxtc.huchuan.ui.mine.collect;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

/**
 * Created by Gubr on 2017/6/5.
 */

class NewsCollectItemView implements ItemViewDelegate<CollectionBean> {
    public NewsCollectItemView(CollectActivity collectActivity) {}

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_news;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        return "1".equals(item.getType());
    }

    @Override
    public void convert(ViewHolder holder, CollectionBean collectionBean, int position) {
        NewsBean newsBean = (NewsBean) collectionBean.getData();
        if(newsBean == null)    return;
        //标题
        TextView tvTitle =  holder.getView(R.id.tv_news_collect_title);
        tvTitle.setText(newsBean.getTitle());

        //作者
        TextView tvAuthor =  holder.getView(R.id.tv_news_collect_author);
        tvAuthor.setText(newsBean.getSource());

        //评论
        TextView tvComment =  holder.getView(R.id.tv_news_collect_comment);
        if(newsBean.getCommentCount() != null && !TextUtils.isEmpty(newsBean.getCommentCount())){
            int comment = Integer.parseInt(newsBean.getCommentCount());
            if (comment <= 10000)
                tvComment.setText(newsBean.getCommentCount() + "评论");
            else
                tvComment.setText("10000+评论");
        }else {
                tvComment.setText("0评论");
        }

        //时间
        TextView tvTime =  holder.getView(R.id.tv_news_collect_time);
        tvTime.setText(DateUtil.showTimeAgo(newsBean.getDate()));

        //封面
        ImageView ivCover = holder.getView(R.id.iv_news_collect_cover);
        ImageHelper.loadImage(holder.getConvertView().getContext(), ivCover, newsBean.getCover());

        //头像
        ImageView picHead = holder.getView(R.id.iv_news_collect_property);
        ImageHelper.loadCircle(holder.getConvertView().getContext(), picHead, newsBean.getSourceicon(), R.drawable.person_icon_head);


        //是否选中
        final CheckBox cb =  holder.getView(R.id.cb_news_collect);
        cb.setTag(newsBean);
        cb.setChecked(newsBean.isCheck());
        if (newsBean.isShow() == true) {
            cb.setVisibility(View.VISIBLE);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsBean bean = (NewsBean) v.getTag();

                    if (bean.isCheck()) {
//                        EventBusUtil.post(new EventCollectSelectBean(false, position, holder));
                        bean.setCheck(false);
                    } else {
//                        EventBusUtil.post(new EventCollectSelectBean(true, position, holder));
                        bean.setCheck(true);
                    }
                }
            });
//            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//
//                    } else {
//
//                    }
//                }
//            });
        } else {
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
        }
    }
}
