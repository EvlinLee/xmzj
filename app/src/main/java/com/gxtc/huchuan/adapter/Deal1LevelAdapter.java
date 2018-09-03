package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Exchanger;

public class Deal1LevelAdapter extends BaseRecyclerAdapter<DealListBean> {

    public Deal1LevelAdapter(Context context, List<DealListBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, DealListBean bean) {

        if(bean == null) return;
        ImageView imgFace = (ImageView) holder.getView(R.id.img_face);
        TextView  imgStatus = (TextView) holder.getView(R.id.img_status);
        TextView  tvTitle   = (TextView) holder.getView(R.id.tv_title);
        TextView  tvName    = (TextView) holder.getView(R.id.tv_name);
        TextView  tvTime    = (TextView) holder.getView(R.id.tv_time);
        TextView  tvType    = (TextView) holder.getView(R.id.tv_type);
        TextView  tvLiuyan  = (TextView) holder.getView(R.id.tv_liuyan);
        TextView  tvStatus  = (TextView) holder.getView(R.id.tv_status);
        TextView  tvLook    = (TextView) holder.getView(R.id.tv_look);

        String                 status = bean.getTradeType();
        SpannableStringBuilder s;
        //出售
        if(status != null) {
            if ("0".equals(status)) {
                s = new SpannableStringBuilder("[出售]");
                //购买
            } else {
                s = new SpannableStringBuilder("[求购]");
            }
            ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_price_ec6b46));
            s.setSpan(span, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvStatus.setText(s);
        }

        String isTop = bean.getIsTop();
        if(isTop != null) {
            if ("0".equals(isTop)) {
                String title = bean.getTitle();
                tvTitle.setText(title);
            } else {
                SpannableString sb = new SpannableString("置顶" + " " + bean.getTitle());
                ImageSpan imageSpan = new ImageSpan(getContext(), R.drawable.deal_icon_top, ImageSpan.ALIGN_BASELINE);
                sb.setSpan(imageSpan, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTitle.setText(sb);
            }
        }

        String name = bean.getUserName();
        //0不匿名   1匿名
        if(bean.getAnonymous() != null) {
            if (bean.getAnonymous().equals("0")) {
                tvName.setText(name);
            } else {
                if (name.length() > 1) {
                    tvName.setText(name.substring(0, 1) + "**");
                } else {
                    tvName.setText(name + "**");
                }
            }
        }
        if(bean.getRemainNum() != null) {
            if ("0".equals(bean.getRemainNum())) {
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setText("已售完");
                imgStatus.setBackgroundResource(R.drawable.deal_homepage_sell_bottom);
                imgStatus.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                if (bean.getWorkOff() != null) {
                    int wordOff = Integer.parseInt(bean.getWorkOff());
                    if (wordOff > 0 && wordOff <= 99) {
                        imgStatus.setVisibility(View.VISIBLE);
                        imgStatus.setText("已售" + bean.getWorkOff());
                        imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom);
                        imgStatus.setTextColor(context.getResources().getColor(R.color.white));
                    } else {
                        if (wordOff == 0) {
                            imgStatus.setVisibility(View.GONE);
                        } else {
                            imgStatus.setVisibility(View.VISIBLE);
                            imgStatus.setText("已售99+");
                            imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom);
                            imgStatus.setTextColor(context.getResources().getColor(R.color.white));
                        }
                    }
                } else {
                    imgStatus.setVisibility(View.GONE);
                }

            }
        }
        String type = bean.getTradeTypeSonName();
        tvType.setText(type);

        String time = bean.getCreateTime();
        tvTime.setText(DateUtil.diffCurTime(Long.valueOf(time), "MM-dd  HH:mm"));

        String liuyan = bean.getLiuYan();
        tvLiuyan.setText(liuyan + "留言");

        String look = bean.getRead();
        tvLook.setText(" / " + look + "浏览");

        ImageHelper.loadImage(getContext(),imgFace,bean.getPicUrl(),R.drawable.circle_place_holder_246);

    }
}
