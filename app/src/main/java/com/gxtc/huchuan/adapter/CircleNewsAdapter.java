package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;


/**
 * Created by sjr on 2017/2/17.
 * 圈子新闻列表适配器
 */

public class CircleNewsAdapter extends BaseRecyclerAdapter<CircleNewsBean> {

    List<CircleNewsBean> mDatas;

    private Context mContext;
    private String flag;
    private OnAuditListener listener;

    public CircleNewsAdapter(Context mContext, List<CircleNewsBean> datas, String flag, int resid) {
        super(mContext, datas, resid);
        this.mDatas = datas;
        this.mContext = mContext;
        this.flag = flag;
    }


    @Override
    public void bindData(ViewHolder holder, final int position, final CircleNewsBean bean) {

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

        //作者
        TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_author);
        tvAuthor.setText(bean.getSource());

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_news_time);
        tvTime.setText(DateUtil.showTimeAgo(bean.getDate()));


        //封面
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_news_cover);
        ImageHelper.loadImage(mContext, ivCover, bean.getCover(), R.drawable.live_foreshow_img_temp);

        if ("1".equals(flag)) {

            TextView tvAudit = (TextView) holder.getView(R.id.tv_audit);
            tvAudit.setVisibility(View.VISIBLE);
            tvAudit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAudit(position, bean);
                }
            });
        }
    }

    public void setOnAuditListener(OnAuditListener listener) {
        this.listener = listener;
    }

    public interface OnAuditListener {
        void onAudit(int position, CircleNewsBean bean);

    }
}
