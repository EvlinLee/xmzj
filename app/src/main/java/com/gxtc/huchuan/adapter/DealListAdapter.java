package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class DealListAdapter extends BaseRecyclerAdapter<DealListBean> {

    private SimpleDateFormat sdf;

    public DealListAdapter(Context context, List<DealListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    }

    @Override
    public void bindData(ViewHolder holder, int position, DealListBean bean) {
        //ImageView imgTop = (ImageView) holder.getView(R.id.img_top);
        ImageView imgIcon = (ImageView) holder.getView(R.id.img_head);
        TextView imgStatus = (TextView) holder.getView(R.id.img_status);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvType = (TextView) holder.getView(R.id.tv_type);
        TextView tvLiuyan = (TextView) holder.getView(R.id.tv_liuyan);
        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
        TextView  tvLook    = (TextView) holder.getView(R.id.tv_look);

        String status = bean.getTradeType();
        SpannableStringBuilder s ;
        //出售
        if("0".equals(status)){
            s = new SpannableStringBuilder("[出售]");
            //购买
        }else{
            s = new SpannableStringBuilder("[求购]");
        }
        ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_price_ec6b46));
        s.setSpan(span,1,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStatus.setText(s);

        String isTop = bean.getIsTop();
        if("0".equals(isTop)){
            //imgTop.setVisibility(View.GONE);
            String title = bean.getTitle();
            tvTitle.setText(title);
        }else{
            //imgTop.setVisibility(View.VISIBLE);
            SpannableString sb = new SpannableString("置顶"  + "   " + bean.getTitle());
            ImageSpan       imageSpan = new ImageSpan(getContext(), R.drawable.deal_icon_top, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(imageSpan,0,2,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitle.setText(sb);
        }

        String name = bean.getUserName();
        //0不匿名   1匿名
        if(bean.getAnonymous().equals("0")){
            tvName.setText(name);
        }else{
            if(name.length()>1){
                tvName.setText(name.substring(0,1) + "**");
            }else{
                tvName.setText(name + "**");
            }
        }

        /*String isFinish = bean.getIsfinish();
        if ("2".equals(isFinish)) {
            imgStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageResource(R.drawable.deal_homepage_sell);
        }else if ("1".equals(bean.getIsfinish())){
            imgStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageResource(R.drawable.deal_homepage_in_progress);

        }else{
            imgStatus.setVisibility(View.INVISIBLE);

        }*/

        if("0".equals(bean.getRemainNum())){
            imgStatus.setVisibility(View.VISIBLE);
            imgStatus.setText("已售完");
            imgStatus.setBackgroundResource(R.drawable.deal_homepage_sell_bottom);
            imgStatus.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            int wordOff = Integer.parseInt(bean.getWorkOff());
            if(wordOff > 0 && wordOff <= 99 ){
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setText("已售出"+bean.getWorkOff()+"个");
                imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom);
                imgStatus.setTextColor(context.getResources().getColor(R.color.white));
            }else {
                if(wordOff == 0){
                    imgStatus.setVisibility(View.INVISIBLE);
                }else {
                    imgStatus.setVisibility(View.VISIBLE);
                    imgStatus.setText("已售出99+");
                    imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom);
                    imgStatus.setTextColor(context.getResources().getColor(R.color.white));
                }
            }
        }
        String type = bean.getTradeTypeSonName();
        tvType.setText(type);

        String time = bean.getCreateTime();
        LogUtil.i("time : " + time);
        tvTime.setText(DateUtil.diffCurTime(Long.valueOf(time),"MM-dd  HH:mm:ss"));

        String liuyan = bean.getLiuYan();
        tvLiuyan.setText(liuyan+"留言");

        String look = bean.getRead();
        tvLook.setText(" / " + look + "浏览");

        ImageHelper.loadCircle(getContext(),imgIcon,bean.getUserPic(),R.drawable.person_icon_head);

    }
}
