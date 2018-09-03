package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/18 .
 */

public class VisitorAdapter extends BaseRecyclerTypeAdapter<VisitorBean> {

    public VisitorAdapter(Context mContext, List<VisitorBean> mDatas, int[] resId) {
        super(mContext, mDatas, resId);
    }

    @Override
    protected int getViewType(int position) {
        if (getDatas().get(position).isHead()){
            return 0;       //标题日期
        }else {
            return 1;       //没有标题
        }
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder h, int position, int type,
            VisitorBean bean) {
        ViewHolder holder = (ViewHolder) h;
        TextView  tvDate       = (TextView) holder.getView(R.id.tv_date);
        TextView  tvUserName   = (TextView) holder.getView(R.id.tv_user_name);
        TextView  tvBrowseTime = (TextView) holder.getView(R.id.tv_browseTime);
        ImageView ivHead       = (ImageView) holder.getView(R.id.iv_head);

        if (type == 0) {
            tvDate.setText(bean.getDateStr());
        }else {
            tvUserName.setText(bean.getName());
            tvBrowseTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getBrowseTime())));
            ImageHelper.loadImage(getmContext(),ivHead,bean.getHeadPic());
        }

    }
}
