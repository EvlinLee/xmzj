package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by sjr on 2017/3/30.
 * 自己写的文章列表适配器
 */

public class MineArticleAdapter extends BaseRecyclerAdapter<NewsBean>{

    private Context mContext;

    public MineArticleAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final NewsBean newsBean) {

        //        //文章状态 0：新提交，未审核；1：审核通过；2：审核不通过
        TextView ivProperty = (TextView) holder.getView(R.id.iv_news_collect_property);

        //草稿
        if(newsBean.getIsDeploy() == 1){
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            ivProperty.setText("草稿");
        }else {
            // 0：新提交，未审核
            if ("0".equals(newsBean.getAudit())) {
                ivProperty.setVisibility(View.VISIBLE);
                Drawable d = getContext().getResources().getDrawable(R.drawable.news_manage_icon_audit);
                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
                ivProperty.setCompoundDrawables(d,null,null,null);

                //1：审核通过
            } else if ("1".equals(newsBean.getAudit())) {
                ivProperty.setVisibility(View.GONE);

                //2：审核不通过
            } else if ("2".equals(newsBean.getAudit())) {
                ivProperty.setVisibility(View.VISIBLE);
                Drawable d = getContext().getResources().getDrawable(R.drawable.news_manage_icon_not_pass);
                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
                ivProperty.setCompoundDrawables(d,null,null,null);

            } else {
                ivProperty.setVisibility(View.GONE);
            }
        }

        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_collect_title);
        tvTitle.setText(newsBean.getTitle());

        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_collect_author);
        tvAuthor.setText(newsBean.getSource());

        //阅读量
        TextView tvReadCount = (TextView) holder.getView(R.id.tv_read_count);
        tvReadCount.setText("阅读:"+newsBean.getReadCount());
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

        TextView tvDelet = (TextView) holder.getView(R.id.tv_delete);
        TextView tvTongbu = (TextView) holder.getView(R.id.tv_tongbu);
        View contentLayout = holder.getView(R.id.content);
        tvDelet.setTag(newsBean);
        tvTongbu.setTag(newsBean);
        contentLayout.setTag(newsBean);

        tvDelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem(view,holder);
            }
        });
        tvTongbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem(view,holder);
            }
        });
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem(view,holder);
            }
        });

    }

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void onClickItem(View v, ViewHolder holder){
            View view = holder.getItemView();
            if(view != null){
                EasySwipeMenuLayout swipeMenuLayout = view.findViewById(R.id.swipeLayout);
                swipeMenuLayout.resetStatus();
            }

        if(mOnClickListener != null){
            mOnClickListener.onClick(v);
        }
    }

}
