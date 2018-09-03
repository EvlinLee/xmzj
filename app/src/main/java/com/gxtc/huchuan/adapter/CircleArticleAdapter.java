package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleArticleBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.RelativeDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by sjr on 2017/2/17.
 * 圈子文章适配器
 * 2017/3/28
 * 新增视频，从单布局变成多布局
 * 2017/4/23
 * 新增课程 交易 广告 。。。
 */

public class CircleArticleAdapter extends BaseMoreTypeRecyclerAdapter<NewNewsBean.DataBean> {

    List<NewNewsBean.DataBean> mDatas;

    private Context mContext;

    private View.OnLongClickListener mOnClickListener;

    public CircleArticleAdapter(Context mContext, List<NewNewsBean.DataBean> datas, int... resid) {
        super(mContext, datas, resid);
        this.mDatas = datas;
        this.mContext = mContext;

    }


    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position,
            NewNewsBean.DataBean bean) {

        if ("1".equals(bean.getIsVideo())) {//视频
            //视频标题
            TextView tvVideoTitle = (TextView) holder.getView(R.id.tv_news_video_title);
            tvVideoTitle.setText(bean.getTitle());

            JZVideoPlayerStandard player = (JZVideoPlayerStandard) holder.getView(R.id.play_news_video_cover);
            player.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_LIST, "", bean.getCover());
            ImageHelper.loadImage(mContext, player.thumbImageView, bean.getCover());

            //作者图标
//        ImageView ivAuthorIcon = (ImageView) holder.getView(R.id.iv_news_video_sourceicon);
//        ImageHelper.loadImage(mContext, ivAuthorIcon, bean.getSourceicon());

            //作者
            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_video_author);
            tvAuthor.setText(bean.getSource());
            //时间
            TextView tvTime = (TextView) holder.getView(R.id.tv_news_video_time);
            tvTime.setText(DateUtil.showTimeAgo(bean.getDate()));

        } else if ("0".equals(bean.getIsVideo())) {//新闻
            holder.getView(R.id.iv_shield).setVisibility(View.GONE);

            //标题
            TextView tvTitle = (TextView) holder.getView(R.id.tv_news_title);
            tvTitle.setText(bean.getTitle());

            //文章类型
            ImageView ivProperty = (ImageView) holder.getView(R.id.iv_news_property);
            if ("1".equals(bean.getTypeSign())) {//置顶
                ivProperty.setVisibility(View.VISIBLE);
                ivProperty.setImageResource(R.drawable.news_icon_top);
            } else if ("2".equals(bean.getTypeSign())) {//热点
                ivProperty.setVisibility(View.VISIBLE);
                ivProperty.setImageResource(R.drawable.news_icon_re);
            } else if ("3".equals(bean.getTypeSign())) {//广告
                ivProperty.setVisibility(View.VISIBLE);
                ivProperty.setImageResource(R.drawable.news_icon_advertisement);
            } else {
                ivProperty.setVisibility(View.GONE);
            }

            //作者图标
//        ImageView ivAuthorIcon = (ImageView) holder.getView(R.id.iv_news_sourceicon);
//        ImageHelper.loadImage(mContext, ivAuthorIcon, bean.getSourceicon());

            //作者
            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
            tvAuthor.setText(bean.getSource());


            //评论
//        TextView tvComment = (TextView) holder.getView(R.id.tv_news_comment);
//        int comment = Integer.parseInt(bean.getCommentCount());
//        if (comment <= 10000)
//            tvComment.setText(bean.getCommentCount() + "评论");
//        else
//            tvComment.setText("10000+评论");

            //时间
            TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
            tvTime.setText(DateUtil.showTimeAgo(bean.getDate()));
            View view = holder.getItemView();
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mOnClickListener != null){
                        mOnClickListener.onLongClick(v);
                    }
                    return false;
                }
            });


//        tvTime.setText(bean.getDate());

            //封面
            ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_cover);
            ImageHelper.loadImage(mContext, ivCover, bean.getCover(),
                    R.drawable.live_foreshow_img_temp);
        } /*else if ("chatInfo".equals(bean.getType())) {//课程

            TextView price = (TextView) holder.getView(R.id.item_live_room_price);
            if ("0".equals(bean.getIsfree())) {
                price.setVisibility(View.GONE);
            } else {
                price.setVisibility(View.VISIBLE);
                price.setText(String.format("￥：%s", bean.getFee()));
            }
//
            TextView tvJoinCount = (TextView) holder.getView(R.id.item_live_room_count);
            tvJoinCount.setText(String.format("%s人次", bean.getJoinCount()));
//
            TextView tvRoomName = (TextView) holder.getView(R.id.item_live_room_name);
            tvRoomName.setText(bean.getChatRoomName());

            TextView tvTitle = (TextView) holder.getView(R.id.item_live_room_title);
            tvTitle.setText(bean.getSubtitle());

            ImageView imageView = (ImageView) holder.getView(R.id.item_live_room_head);
            imageView.setVisibility(View.GONE);

            ImageHelper.loadImage(holder.getItemView().getContext(),
                    (ImageView) holder.getView(R.id.item_live_room_image),
                    bean.getFacePic(), R.drawable.live_room_icon_temp);
        } else if ("tradeInfo".equals(bean.getType())) {//交易
            ImageView imgTop   = (ImageView) holder.getView(R.id.img_top);
            ImageView imgIcon  = (ImageView) holder.getView(R.id.img_head);
            TextView  tvTitle  = (TextView) holder.getView(R.id.tv_title);
            TextView  tvName   = (TextView) holder.getView(R.id.tv_name);
            TextView  tvTime   = (TextView) holder.getView(R.id.tv_time);
            TextView  tvType   = (TextView) holder.getView(R.id.tv_type);
            TextView  tvLiuyan = (TextView) holder.getView(R.id.tv_liuyan);
            TextView  tvStatus = (TextView) holder.getView(R.id.tv_status);
            TextView  tvLook   = (TextView) holder.getView(R.id.tv_look);

            imgTop.setVisibility(View.GONE);
            imgIcon.setVisibility(View.GONE);

            String                 status = bean.getTradeType();
            SpannableStringBuilder s;
            //出售
            if ("0".equals(status)) {
                s = new SpannableStringBuilder("[出售]");
                //购买
            } else {
                s = new SpannableStringBuilder("[求购]");
            }
            ForegroundColorSpan span = new ForegroundColorSpan(
                    mContext.getResources().getColor(R.color.color_price_ec6b46));
            s.setSpan(span, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvStatus.setText(s);

            String name = bean.getUserName();
            //0不匿名   1匿名
            if (bean.getAnonymous().equals("0")) {
                tvName.setText(name);
            } else {
                if (name.length() > 1) {
                    tvName.setText(name.substring(0, 1) + "**");
                } else {
                    tvName.setText(name + "**");
                }
            }

            String type = bean.getTradeTypeSonName();
            tvType.setText(type);

            String time = bean.getCreateTime();
            tvTime.setText(DateUtil.showTimeAgo(time));

            String title = bean.getTitle();
            tvTitle.setText(title);

            String liuyan = bean.getLiuYan();
            tvLiuyan.setText(liuyan + "留言");

            String look = bean.getRead();
            tvLook.setText(" / " + look + "浏览");

        } else if ("ad".equals(bean.getType()) && (0 == bean.getContentType())) {//大图广告

            TextView tvTitle = (TextView) holder.getView(R.id.tv_news_ads1_title);
            tvTitle.setText(bean.getTitle());

            ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_ads1_cover);
            ImageHelper.loadImage(mContext, ivCover, bean.getCover());
            //作者图标
//        ImageView ivAuthorIcon = (ImageView) holder.getView(R.id.iv_news_video_sourceicon);
//        ImageHelper.loadImage(mContext, ivAuthorIcon, bean.getSourceicon());

            //作者
//            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_ads1_author);
//            tvAuthor.setText(bean.getSource());
            //时间
//            TextView tvTime = (TextView) holder.getView(R.id.tv_news_video_time);
//            tvTime.setText(DateUtil.showTimeAgo(bean.getDate()));
        } else if ("ad".equals(bean.getType()) && (1 == bean.getContentType())) {//推荐的文章
            //标题
            TextView tvTitle = (TextView) holder.getView(R.id.tv_news_title);
            tvTitle.setText(bean.getNewsRespVo().getTitle());

            //文章类型
            ImageView ivProperty = (ImageView) holder.getView(R.id.iv_news_property);

            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_advertisement);


            //作者图标
//        ImageView ivAuthorIcon = (ImageView) holder.getView(R.id.iv_news_sourceicon);
//        ImageHelper.loadImage(mContext, ivAuthorIcon, bean.getSourceicon());

            //作者
            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
            tvAuthor.setText(bean.getNewsRespVo().getSource());


            //评论
//        TextView tvComment = (TextView) holder.getView(R.id.tv_news_comment);
//        int comment = Integer.parseInt(bean.getCommentCount());
//        if (comment <= 10000)
//            tvComment.setText(bean.getCommentCount() + "评论");
//        else
//            tvComment.setText("10000+评论");

            //时间
            TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
            tvTime.setText(DateUtil.showTimeAgo(bean.getNewsRespVo().getDate()));


//        tvTime.setText(bean.getDate());

            //封面
            ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_cover);
            ImageHelper.loadImage(mContext, ivCover, bean.getNewsRespVo().getCover(),
                    R.drawable.live_foreshow_img_temp);
        }*/

    }


    @Override
    public int getItemViewType(int position) {
        if ("1".equals(mDatas.get(position).getIsVideo()))//视频
            return 0;
        else {
            return 1;
        }/* if (("news".equals(mDatas.get(position).getType()) && ("0".equals(
                mDatas.get(position).getIsVideo())))) {//新闻
            return 1;
        } else if ("chatInfo".equals(mDatas.get(position).getType())) {//课程
            return 2;
        } else if ("tradeInfo".equals(mDatas.get(position).getType())) {//交易
            return 3;
        } else if (("ad".equals(mDatas.get(position).getType())) && (0 == mDatas.get(
                position).getContentType())) {//大图广告
            return 4;
        } else if (("ad".equals(mDatas.get(position).getType())) && (1 == mDatas.get(
                position).getContentType())) {
            return 5;
        } else {
            throw new IllegalArgumentException("服务器返回更多的类型，注意跟服务器协商修改代码！！");
        }*/


    }
    public void setOnLongClickListener(View.OnLongClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


}
