package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;
import java.util.Locale;

/**
 * Created by sjr on 2017/2/17.
 * 分享赚钱
 */

public class ShareMakeMoneyAdapter extends BaseRecyclerAdapter<ShareMakeMoneyBean> {
    private Context mContext;

    private OnShareListener listener;

    public ShareMakeMoneyAdapter(Context context, List<ShareMakeMoneyBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;

    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final ShareMakeMoneyBean bean) {
        Drawable drawable = null;
        //标题
        TextView tvTitle = (TextView) holder.getView(R.id.tv_share_make_money_title);
        tvTitle.setText(bean.getTitle());
        if(bean.getType() == null)  return;
        //课程
        if ("chatInfo".equals(bean.getType())) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.person_live_distribution_icon_topic);
            if(drawable != null){
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvTitle.setCompoundDrawables(drawable, null, null, null);
            }

        } else if ("group".equals(bean.getType())) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.person_live_distribution_icon_circle);
            if(drawable != null){
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvTitle.setCompoundDrawables(drawable, null, null, null);
            }

        } else{
            drawable = ContextCompat.getDrawable(mContext, R.drawable.person_live_distribution_icon_topic);
            if(drawable != null){
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvTitle.setCompoundDrawables(drawable, null, null, null);
            }
        }


        //描述
        TextView tvDesc = (TextView) holder.getView(R.id.tv_share_make_money_groupdesc);

        //封面
        if(bean.getFacePic() != null) {
            ImageView ivCover = (ImageView) holder.getView(R.id.iv_share_make_money_cover);
            ImageHelper.loadImage(mContext, ivCover, bean.getFacePic());
        }

        TextView tvMiaosu = (TextView) holder.getView(R.id.tv_share_make_money_miaosu);
        TextView tvRenci = (TextView) holder.getView(R.id.tv_share_make_money_renci);

        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
        tvStatus.setVisibility(View.GONE);

        if ("group".equals(bean.getType())) {
            tvDesc.setVisibility(View.VISIBLE);
            tvRenci.setVisibility(View.VISIBLE);
            tvMiaosu.setVisibility(View.GONE);
            tvDesc.setText(bean.getGroupDesc());
            tvRenci.setText("人数" + bean.getJoinCount());

        } else if ("chatInfo".equals(bean.getType())) {
            tvDesc.setText(bean.getChatRoomName());
            tvMiaosu.setText(bean.getJoinCount() + "人次");
            tvMiaosu.setVisibility(View.VISIBLE);
            tvRenci.setVisibility(View.GONE);

        } else if("chatSeries".equals(bean.getType())){
            tvStatus.setVisibility(View.VISIBLE);
            tvDesc.setText(bean.getChatRoomName());
            tvMiaosu.setText(bean.getJoinCount() + "人次");
            tvMiaosu.setVisibility(View.VISIBLE);
            tvRenci.setVisibility(View.GONE);
            tvTitle.setCompoundDrawables(null, null, null, null);
        }

        //价格
        TextView tvPrice = (TextView) holder.getView(R.id.tv_share_money_money);
        try {
            double p = Double.valueOf(bean.getFee());
            String temp = String.format(Locale.CHINA, "%.2f", p);
            tvPrice.setText(" / ¥ " + temp );
        }catch (Exception e){
            e.printStackTrace();
            tvPrice.setText(" / ¥ 0 ");
        }



        TextView tvYongjin = (TextView) holder.getView(R.id.tv_share_make_money_commission);
        tvYongjin.setText(bean.getCommission());

        TextView tvShare = (TextView) holder.getView(R.id.btn_share_make_money_share);
        tvShare.setTag(bean);
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareMakeMoneyBean bean1 = (ShareMakeMoneyBean) v.getTag();
                listener.onShare(position, bean1);
            }
        });
    }

    public void setOnShareListener(OnShareListener listener) {
        this.listener = listener;
    }

    public interface OnShareListener {
        void onShare(int position, ShareMakeMoneyBean bean);
    }

}
