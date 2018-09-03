package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by zzg on 2018/3/20 .
 */

public class StatisticVisitorAdapter extends BaseRecyclerAdapter<VisitorBean> {

    public StatisticVisitorAdapter(Context mContext, List<VisitorBean> mDatas, int resId) {
        super(mContext, mDatas, resId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, VisitorBean visitorBean) {
          holder.setText(R.id.viditor_count,"访客:"+visitorBean.getCount());
          holder.setText(R.id.time,visitorBean.getDateName());
    }
}
