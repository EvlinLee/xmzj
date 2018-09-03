package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class Deal1LevelAdapterOld/* extends AbsBaseAdapter<DealListBean>*/ {

    private SimpleDateFormat sdf;

    public Deal1LevelAdapterOld(Context context, List<DealListBean> datas, int itemLayoutId) {
//        super(context, datas, itemLayoutId);
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    }

//    @Override
//    public void bindData(ViewHolder holder, DealListBean bean, int position) {
//        ImageView imgTop = (ImageView) holder.getView(R.id.img_top);
//        ImageView imgIcon = (ImageView) holder.getView(R.id.img_head);
//        ImageView imgStatus = (ImageView) holder.getView(R.id.img_status);
//        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
//        TextView tvName = (TextView) holder.getView(R.id.tv_name);
//        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
//        TextView tvType = (TextView) holder.getView(R.id.tv_type);
//        TextView tvLiuyan = (TextView) holder.getView(R.id.tv_liuyan);
//        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
//        TextView tvLook = (TextView) holder.getView(R.id.tv_look);
//
//        String status = bean.getTradeType();
//        SpannableStringBuilder s ;
//        //出售
//        if("0".equals(status)){
//            s = new SpannableStringBuilder("[出售]");
//        //购买
//        }else{
//            s = new SpannableStringBuilder("[求购]");
//        }
//        ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_price_ec6b46));
//        s.setSpan(span,1,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvStatus.setText(s);
//
//        String isTop = bean.getIsTop();
//        if("0".equals(isTop)){
//            imgTop.setVisibility(View.GONE);
//        }else{
//            imgTop.setVisibility(View.VISIBLE);
//        }
//
//        String name = bean.getUserName();
//        //0不匿名   1匿名
//        if(bean.getAnonymous().equals("0")){
//            tvName.setText(name);
//        }else{
//            if(name.length()>1){
//                tvName.setText(name.substring(0,1) + "**");
//            }else{
//                tvName.setText(name + "**");
//            }
//        }
//
//        String isFinish = bean.getIsfinish();
//        if ("2".equals(isFinish)) {
//            imgStatus.setVisibility(View.VISIBLE);
//            imgStatus.setImageResource(R.drawable.deal_homepage_sell);
//        }else if ("1".equals(bean.getIsfinish())){
//            imgStatus.setVisibility(View.VISIBLE);
//            imgStatus.setImageResource(R.drawable.deal_homepage_in_progress);
//
//        }else{
//            imgStatus.setVisibility(View.INVISIBLE);
//
//        }
//
//        String type = bean.getTradeTypeSonName();
//        tvType.setText(type);
//
//        String time = bean.getCreateTime();
//        tvTime.setText(DateUtil.diffCurTime(Long.valueOf(time),"MM-dd  HH:mm:ss"));
//
//        String title = bean.getTitle();
//        tvTitle.setText(title);
//
//        String liuyan = bean.getLiuYan();
//        tvLiuyan.setText(liuyan+"留言");
//
//        String look = bean.getRead();
//        tvLook.setText(" / " + look + "浏览");
//
//        ImageHelper.loadCircle(getContext(),imgIcon,bean.getUserPic(),R.drawable.person_icon_head);
//
//    }
}
