package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsHeadBean;


import java.util.List;

/**
 * Created by 宋家任 on 2016/11/30.
 * 资讯八个头部数据的适配器
 */

public class NewsHeadRecyleViewAdapter extends BaseRecyclerAdapter<NewsHeadBean> {
    /**
     * @param context
     * @param mDatas
     * @param itemLayoutId
     */
    public NewsHeadRecyleViewAdapter(Context context, List<NewsHeadBean> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, NewsHeadBean newsHeadBean) {
        ImageView iv = (ImageView) holder.getView(R.id.iv_shop_head);
        iv.setImageResource(newsHeadBean.getIv());
        TextView tvTitle = (TextView) holder.getView(R.id.tv_shop_head);
        tvTitle.setText(newsHeadBean.getTitle());
    }
}
