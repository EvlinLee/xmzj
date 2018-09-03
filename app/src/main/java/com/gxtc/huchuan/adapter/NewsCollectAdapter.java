package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by sjr on 2017/2/17.
 * 新闻收藏列表适配器
 */

public class NewsCollectAdapter extends BaseRecyclerAdapter<NewsBean> {
    private Context mContext;


    public NewsCollectAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;

    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final NewsBean newsBean) {
        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_collect_title);
        tvTitle.setText(newsBean.getTitle());

        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_collect_author);
        tvAuthor.setText(newsBean.getSource());

        //评论
        TextView tvComment = (TextView) holder.getView(R.id.tv_news_collect_comment);
        int comment = Integer.parseInt(newsBean.getCommentCount());
        if (comment <= 10000)
            tvComment.setText(newsBean.getCommentCount() + "评论");
        else
            tvComment.setText("10000+评论");

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_collect_time);
        tvTime.setText(DateUtil.showTimeAgo(newsBean.getDate()));

        //封面
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_collect_cover);
        ImageHelper.loadImage(mContext, ivCover, newsBean.getCover());


        //是否选中
        final CheckBox cb = (CheckBox) holder.getView(R.id.cb_news_collect);
        cb.setTag(newsBean);
        cb.setChecked(newsBean.isCheck());
        if (newsBean.isShow() == true) {
            cb.setVisibility(View.VISIBLE);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newsBean.isCheck()) {
                        NewsBean bean = (NewsBean) v.getTag();
                        EventBusUtil.post(new EventCollectSelectBean(false, position, holder));
                        bean.setCheck(false);
                    } else {
                        NewsBean bean = (NewsBean) v.getTag();
                        EventBusUtil.post(new EventCollectSelectBean(true, position, holder));
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
