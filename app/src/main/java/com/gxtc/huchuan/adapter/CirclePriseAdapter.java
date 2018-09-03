package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ThumbsupVosBean;

import java.util.List;


/**
 * Describe:
 * Created by ALing on 2017/5/18 .
 */

public class CirclePriseAdapter extends BaseRecyclerTypeAdapter<ThumbsupVosBean> {

    public CirclePriseAdapter(Context mContext, List<ThumbsupVosBean> mDatas, int[] resId) {
        super(mContext, mDatas, resId);
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }


    @Override
    protected void bindData(RecyclerView.ViewHolder h, int position, int type,
                            ThumbsupVosBean bean) {
        ViewHolder holder = (ViewHolder) h;
        TextView  tvUserName   = (TextView) holder.getView(R.id.tv_user_name);
        ImageView ivHead       = (ImageView) holder.getView(R.id.iv_head);

            tvUserName.setText(bean.getUserName());
            ImageHelper.loadImage(getmContext(),ivHead,bean.getHeadPic());

    }
}
