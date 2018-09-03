package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.utils.StatusBarUtils;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.pop.PopShield;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;
import java.util.Locale;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by sjr on 2017/2/17.
 * 新闻列表适配器
 * 2017/3/28
 * 新增视频，从单布局变成多布局
 * 2017/4/23
 * 新增课程 交易 广告 。。。
 */

public class NewsAdapter extends BaseMoreTypeRecyclerAdapter<NewNewsBean> implements
        View.OnClickListener {

    List<NewNewsBean> mDatas;

    private Context mContext;

    private OnShieldListener mShieldListener;

    public NewsAdapter(Context mContext, List<NewNewsBean> datas, int... resid) {
        super(mContext, datas, resid);
        this.mDatas = datas;
        this.mContext = mContext;
    }

    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        if (bean == null || bean.getData() == null) return;
        //视频
        if (("news".equals(bean.getType()) && ("1".equals(bean.getData().getIsVideo())))) {
            fillViedeoData(holder, position, bean);

        //新闻
        } else if (("news".equals(bean.getType()) && ("0".equals(bean.getData().getIsVideo())))) {
            fillNewsData(holder, position, bean);

        //课程
        } else if ("chatInfo".equals(bean.getType())) {
            fillClassData(holder, position, bean);

        //交易
        } else if ("tradeInfo".equals(bean.getType())) {
            fillDealData(holder, position, bean);

        //专题
        } else if ("newsSpecial".equals(bean.getType())) {
            fillSpecial(holder, position, bean);

        //大图广告
        } else if ("ad".equals(bean.getType()) && (0 == bean.getData().getContentType())) {
            fillBigImageAdData(holder, position, bean);

        //推荐的文章
        } else if ("ad".equals(bean.getType()) && (1 == bean.getData().getContentType())) {
            fillRecommendAdData(holder, position, bean);

        //交易广告
        } else if ("ad".equals(bean.getType()) && (4 == bean.getData().getContentType())) {
            fillDealAdData(holder, position, bean);

        //课堂广告
        } else if ("ad".equals(bean.getType()) && (5 == bean.getData().getContentType())) {
            fillClassAdData(holder, position, bean);
        }

    }


    //新闻
    private void fillNewsData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_title);
        tvTitle.setText(bean.getData().getTitle());

        //文章类型
        ImageView ivProperty = (ImageView) holder.getView(R.id.iv_news_property);
        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
        tvAuthor.setText(bean.getData().getSource());
        if ("1".equals(bean.getData().getTypeSign())) {//置顶
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_top);
        } else if ("2".equals(bean.getData().getTypeSign())) {//热点
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_re);
        } else if ("3".equals(bean.getData().getTypeSign())) {//广告
            ivProperty.setVisibility(View.VISIBLE);
            ivProperty.setImageResource(R.drawable.news_icon_advertisement);
        } else {
            ivProperty.setVisibility(View.GONE);
//                ivProperty.setVisibility(View.VISIBLE);
//                ivProperty.setImageResource(R.drawable.news_icon_advertisement);
//                tvAuthor.setVisibility(View.GONE);
        }


        ImageView imgClose = (ImageView) holder.getView(R.id.iv_shield);
        imgClose.setTag(bean);
        imgClose.setOnClickListener(this);


        if (ivProperty.getVisibility() == View.GONE) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvAuthor.getLayoutParams();
            params.leftMargin = (int) getContext().getResources().getDimension(R.dimen.px20dp);
            imgClose.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvAuthor.getLayoutParams();
            params.leftMargin = (int) getContext().getResources().getDimension(R.dimen.px12dp);
            imgClose.setVisibility(View.GONE);
        }

        //阅读
        TextView tvCount = (TextView) holder.getView(R.id.tv_read_count);
        tvCount.setText("阅读:" + bean.getData().getReadCount());

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
        tvTime.setText(DateUtil.showTimeAgo(bean.getData().getDate()));

        //封面
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_cover);
        ImageHelper.loadImage(mContext, ivCover, bean.getData().getCover(), R.drawable.live_foreshow_img_temp, R.drawable.live_foreshow_img_temp);
    }

    //视频
    private void fillViedeoData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        //视频标题
        TextView tvVideoTitle = (TextView) holder.getView(R.id.tv_news_video_title);
        tvVideoTitle.setText(bean.getData().getTitle());

        JZVideoPlayerStandard player = (JZVideoPlayerStandard) holder.getView(R.id.play_news_video_cover);
        player.setUp(bean.getData().getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_LIST, "", bean.getData().getCover());
        ImageHelper.loadImage(mContext, player.thumbImageView, bean.getData().getCover());

        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_video_author);
        tvAuthor.setText(bean.getData().getSource());
        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_video_time);
        tvTime.setText(DateUtil.showTimeAgo(bean.getData().getDate()));

        ImageView imgClose = (ImageView) holder.getView(R.id.iv_shield);
        imgClose.setTag(bean);
        imgClose.setOnClickListener(this);

    }

    //课程
    private void fillClassData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        TextView price = (TextView) holder.getView(R.id.item_live_room_price);
        if (bean.getData().getIsfree() == null || "0".equals(bean.getData().getIsfree())) {
            if (bean.getData().getChatSeriesData() != null && !bean.getData().getChatSeriesData().getFee().equals("0")) {
                price.setText("￥" + bean.getData().getChatSeriesData().getFee());
            } else {
                price.setText("免费");
            }
        } else {
            price.setVisibility(View.VISIBLE);
            price.setText("￥" + bean.getData().getFee());
        }
        TextView tvJoinCount = (TextView) holder.getView(R.id.item_live_room_count);
        tvJoinCount.setText(String.format("%s人次", bean.getData().getJoinCount()));
        TextView tvRoomName = (TextView) holder.getView(R.id.item_live_room_name);

        TextView tvTitle = (TextView) holder.getView(R.id.item_live_room_title);
//            if( bean.getData().getChatSeriesData() != null){
//                //系列课的显示标题
//                tvTitle.setText(bean.getData().getChatSeriesData().getSeriesname());
//            }else {
//                tvTitle.setText(bean.getData().getSubtitle());
//            }

        TextView playStatus = (TextView) holder.getView(R.id.play_status);
//            if(TextUtils.isEmpty(bean.getData().getStatus())){
//                bean.getData().setStatus("3");
//            }
        String FacePic = "";
        //课程
        if (bean.getData().getChatSeriesData() == null) {
            FacePic = bean.getData().getFacePic();
            tvTitle.setText(bean.getData().getSubtitle());
            tvRoomName.setText(bean.getData().getName());
            playStatus.setVisibility(View.VISIBLE);
            //。1：预告，2：直播中，3：结束
            switch (bean.getData().getStatus()) {
                case "1":
                    setTime(playStatus, bean.getData());
                    break;
                case "2":
                    playStatus.setText("直播中");
                    break;
                case "3":
                    playStatus.setText("已开始");
                    break;
            }

            //系列课
        } else {
            tvTitle.setText(bean.getData().getChatSeriesData().getSeriesname());
            tvRoomName.setText(bean.getData().getName());
            playStatus.setVisibility(View.GONE);
            FacePic = bean.getData().getChatSeriesData().getHeadpic();
        }
        ImageView imageView = (ImageView) holder.getView(R.id.item_live_room_head);
        imageView.setVisibility(View.GONE);

        ImageHelper.loadImage(holder.getItemView().getContext(), (ImageView) holder.getView(R.id.item_live_room_image),
                FacePic, R.drawable.live_room_icon_temp, R.drawable.live_room_icon_temp);
    }

    //交易
    private void fillDealData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        ImageView imgIcon = (ImageView) holder.getView(R.id.img_head);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvType = (TextView) holder.getView(R.id.tv_type);
        TextView tvLiuyan = (TextView) holder.getView(R.id.tv_liuyan);
        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
        TextView tvLook = (TextView) holder.getView(R.id.tv_look);

        imgIcon.setVisibility(View.GONE);

        String status = bean.getData().getTradeType();
        SpannableStringBuilder s;
        //出售
        if ("0".equals(status)) {
            s = new SpannableStringBuilder("[出售]");
            //购买
        } else {
            s = new SpannableStringBuilder("[求购]");
        }
        ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_price_ec6b46));
        s.setSpan(span, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStatus.setText(s);

        String name = bean.getData().getUserName();
        //0不匿名   1匿名
        if (bean.getData().getAnonymous().equals("0")) {
            tvName.setText(name);
        } else {
            if (name.length() > 1) {
                tvName.setText(name.substring(0, 1) + "**");
            } else {
                tvName.setText(name + "**");
            }
        }

        String type = bean.getData().getTradeTypeSonName();
        tvType.setText(type);

        String time = bean.getData().getCreateTime();
        tvTime.setText(DateUtil.showTimeAgo(time));

        String title = bean.getData().getTitle();
        tvTitle.setText(title);

        String liuyan = bean.getData().getLiuYan();
        tvLiuyan.setText(liuyan + "留言");

        String look = bean.getData().getRead();
        tvLook.setText(" / " + look + "浏览");
        if (TextUtils.isEmpty(liuyan) || TextUtils.isEmpty(look)) {
            tvLiuyan.setVisibility(View.INVISIBLE);
            tvLook.setVisibility(View.INVISIBLE);
        }
    }

    //专题
    private void fillSpecial(ViewHolder holder, int position, NewNewsBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvPrice = (TextView) holder.getView(R.id.tv_prices);
        TextView tvCount = (TextView) holder.getView(R.id.tv_count);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        ImageView imgHead = (ImageView) holder.getView(R.id.img_face);

        NewNewsBean.DataBean dataBean = bean.getData();
//        if ("0".equals(dataBean.getIsSubscribe())) {//未订阅
            if (0 == dataBean.getIsFee()) {
                tvPrice.setTextColor(getContext().getResources().getColor(R.color.text_color_999));
                tvPrice.setText("免费");

            } else {
                tvPrice.setTextColor(getContext().getResources().getColor(R.color.color_fb4717));
                tvPrice.setText("￥" + StringUtil.formatMoney(2, dataBean.getPrice()));
            }
//        } else {
//            tvPrice.setTextColor(getContext().getResources().getColor(R.color.color_fb4717));
//            tvPrice.setText("已订阅");
//        }


        tvTitle.setText(dataBean.getName());
        tvCount.setText(String.format(Locale.CHINA, "%d人订阅", dataBean.getSubscribeSum()));
        tvTime.setText(DateUtil.showTimeAgo(String.valueOf(dataBean.getUpdateTime())));

        ImageHelper.loadImage(getContext(), imgHead, dataBean.getPic());
    }

    //大图广告
    private void fillBigImageAdData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_ads1_title);
        tvTitle.setText(bean.getData().getTitle());

        ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_ads1_cover);
        ImageHelper.loadImage(mContext, ivCover, bean.getData().getCover(), R.drawable.live_foreshow_img_temp, R.drawable.live_foreshow_img_temp);
    }

    //交易广告
    private void fillDealAdData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        DealListBean listBean = bean.getData().getTradeInfoVo();

        ImageView imgIcon = (ImageView) holder.getView(R.id.img_head);
        ImageView imgAd = (ImageView) holder.getView(R.id.iv_news_ads1_property);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvType = (TextView) holder.getView(R.id.tv_type);
        TextView tvLiuyan = (TextView) holder.getView(R.id.tv_liuyan);
        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
        TextView tvLook = (TextView) holder.getView(R.id.tv_look);

        imgIcon.setVisibility(View.GONE);
        imgAd.setVisibility(View.VISIBLE);

        String status = listBean.getTradeType();
        SpannableStringBuilder s;
        //出售
        if ("0".equals(status)) {
            s = new SpannableStringBuilder("[出售]");
            //购买
        } else {
            s = new SpannableStringBuilder("[求购]");
        }
        ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_price_ec6b46));
        s.setSpan(span, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStatus.setText(s);

        String name = listBean.getUserName();
        tvName.setText(name);

        String type = listBean.getTradeTypeSonName();
        tvType.setText(type);

        String time = listBean.getCreateTime();
        tvTime.setText(DateUtil.showTimeAgo(time));

        String title = listBean.getTitle();
        tvTitle.setText(title);

        String liuyan = listBean.getLiuYan();
        tvLiuyan.setText(liuyan + "留言");

        String look = listBean.getRead();
        tvLook.setText(" / " + look + "浏览");
    }

    //课程广告
    private void fillClassAdData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        ChatInfosBean chatBean = bean.getData().getChatInfoRespVo();
        ImageView imgAd = (ImageView) holder.getView(R.id.iv_news_ads1_property);

        imgAd.setVisibility(View.VISIBLE);

        TextView price = (TextView) holder.getView(R.id.item_live_room_price);
        if ("0".equals(chatBean.getIsfree())) {
            price.setVisibility(View.GONE);
        } else {
            price.setVisibility(View.VISIBLE);
            price.setText(String.format("￥：%s", chatBean.getFee()));
        }
        TextView tvJoinCount = (TextView) holder.getView(R.id.item_live_room_count);
        tvJoinCount.setText(String.format("%s人次", chatBean.getJoinCount()));
        TextView tvRoomName = (TextView) holder.getView(R.id.item_live_room_name);
        tvRoomName.setText(chatBean.getChatRoomName());

        TextView tvTitle = (TextView) holder.getView(R.id.item_live_room_title);
        tvTitle.setText(chatBean.getSubtitle());

        ImageView imageView = (ImageView) holder.getView(R.id.item_live_room_head);
        imageView.setVisibility(View.GONE);

        ImageHelper.loadImage(holder.getItemView().getContext(), (ImageView) holder.getView(R.id.item_live_room_image),
                chatBean.getFacePic(), R.drawable.live_room_icon_temp, R.drawable.live_room_icon_temp);

    }

    //推荐的文章
    private void fillRecommendAdData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, NewNewsBean bean) {
        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_news_title);
        tvTitle.setText(bean.getData().getNewsRespVo().getTitle());

        //文章类型
        ImageView ivProperty = (ImageView) holder.getView(R.id.iv_news_property);

        ivProperty.setVisibility(View.VISIBLE);
        ivProperty.setImageResource(R.drawable.news_icon_advertisement);

        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
        tvAuthor.setText(bean.getData().getNewsRespVo().getSource());

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
        tvTime.setText(DateUtil.showTimeAgo(bean.getData().getNewsRespVo().getDate()));

        //封面
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_cover);
        ImageHelper.loadImage(mContext, ivCover, bean.getData().getNewsRespVo().getCover(), R.drawable.live_foreshow_img_temp, R.drawable.live_foreshow_img_temp);
    }

    public void setTime(TextView view, NewNewsBean.DataBean o) {
        if (TextUtils.isEmpty(o.getStarttime())) return;
        Long aLong = 0L;
        try {
            aLong = Long.valueOf(o.getStarttime());
        } catch (NumberFormatException e) {
            aLong = 0L;
        }

        long l = aLong - System.currentTimeMillis();
        if (l > 0) {
            String[] strings = DateUtil.countDownNotAddZero(l);
            if (!strings[0].equals("0")) {
                view.setText(strings[0] + "天后");
            } else if (!strings[1].equals("0")) {
                view.setText(strings[1] + "小时后");
            } else if (!strings[2].equals("0")) {
                view.setText(strings[2] + "分后");
            } else if (!strings[3].equals("0")) {
                view.setText(strings[3] + "秒后");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        //视频
        if ("1".equals(mDatas.get(position).getData().getIsVideo()))
            return 0;

            //新闻
        else if (("news".equals(mDatas.get(position).getType()) && ("0".equals(mDatas.get(position).getData().getIsVideo())))) {
            return 1;

            //课程
        } else if ("chatInfo".equals(mDatas.get(position).getType())) {
            return 2;

            //交易
        } else if ("tradeInfo".equals(mDatas.get(position).getType())) {
            return 3;

            //专题
        } else if ("newsSpecial".equals(mDatas.get(position).getType())) {
            return 8;

            //大图广告
        } else if (("ad".equals(mDatas.get(position).getType())) && (0 == mDatas.get(position).getData().getContentType())) {
            return 4;

            //新闻广告
        } else if (("ad".equals(mDatas.get(position).getType())) && (1 == mDatas.get(position).getData().getContentType())) {
            return 5;

            //交易广告
        } else if (("ad".equals(mDatas.get(position).getType())) && (4 == mDatas.get(position).getData().getContentType())) {
            return 3;

            //课堂广告
        } else if (("ad".equals(mDatas.get(position).getType())) && (5 == mDatas.get(position).getData().getContentType())) {
            return 2;

        } else {
            return 9;
        }

    }


    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick())
            return;
        switch (v.getId()) {
            //点击屏蔽按钮
            case R.id.iv_shield:
                NewNewsBean bean = (NewNewsBean) v.getTag();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int x = 0;
                int y = 0;
                if (("news".equals(bean.getType()) && ("1".equals(bean.getData().getIsVideo())))) {
                    x = location[0] + v.getWidth() / 2 + WindowUtil.dip2px(getContext(), 2);
                    y = location[1] - StatusBarUtils.getHeight(getContext())
                            + v.getHeight() + WindowUtil.dip2px(getContext(), 15);

                } else {
                    x = location[0] + v.getWidth() / 2 + WindowUtil.dip2px(getContext(), 5);
                    y = location[1] - StatusBarUtils.getHeight(getContext())
                            + v.getHeight() + WindowUtil.dip2px(getContext(), 25);
                }
                int mode = Gravity.BOTTOM;
                if (location[1] >= WindowUtil.getScreenH(getContext()) - v.getHeight() - WindowUtil.dip2px(getContext(), 122)) {
                    mode = Gravity.TOP;
                }

                PopShield shieldDialog = new PopShield(getContext(), bean);
                shieldDialog.anchorView(v)
                        .showAnim(new PopEnterAnim().duration(200))
                        .dismissAnim(new PopExitAnim().duration(200))
                        .cornerRadius(4)
                        .gravity(mode)
                        .bubbleColor(Color.parseColor("#ffffff"))
                        .setBgAlpha(0.1f)
                        .location(x, y)
                        .setOnClickListener(this);
                shieldDialog.show();
                break;

            //屏蔽此条文章:
            case R.id.tv_shield_dynamic:
                if (mShieldListener != null) {
                    NewNewsBean temp = (NewNewsBean) v.getTag();
                    mShieldListener.onShieldArticle(temp.getData().getId());
                }
                break;


            //屏蔽此人文章:
            case R.id.tv_shield_user:
                if (mShieldListener != null) {
                    NewNewsBean temp = (NewNewsBean) v.getTag();
                    mShieldListener.onShieldUser(temp.getData().getUserCode());
                }
                break;
        }
    }


    public interface OnShieldListener {
        void onShieldArticle(String id);

        void onShieldUser(String userCode);
    }

    public void setOnShieldListener(OnShieldListener shieldListener) {
        mShieldListener = shieldListener;
    }
}
