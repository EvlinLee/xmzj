package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:个人主页 新闻列表 > 更多
 * Created by ALing on 2017/4/10 .
 */

public class PersonalNewsMoreAdapter extends BaseRecyclerAdapter<NewsBean> {

    public PersonalNewsMoreAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, NewsBean newsBean) {
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id
                .iv_news_cover), newsBean.getCover(), R.mipmap.ic_launcher);
        //文章类型
        ImageView ivProperty = (ImageView) holder.getView(R.id.iv_news_property);
        if ("1".equals(newsBean.getTypeSign())) {//置顶
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_top);
        } else if ("2".equals(newsBean.getTypeSign())) {//热点
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_re);
        } /*else if ("3".equals(newsBean.getTypeSign())) {//广告
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_advertisement);
        } */else {
            ivProperty.setVisibility(View.GONE);
        }

        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_title);
        tvTitle.setText(newsBean.getTitle());
        TextView tvReadcount = (TextView) holder.getView(R.id.tv_news_readcount);
        tvReadcount.setText("阅读:"+newsBean.getReadCount());
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
        tvAuthor.setText(newsBean.getSource());
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
        tvTime.setText(DateUtil.showTimeAgo(newsBean.getDate()));
    }
}
