package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.bean.CircleArticleBean;

import java.util.List;


/**
 * Created by Steven on 17/4/26.
 */

public class CircleVideoAdapter extends BaseRecyclerAdapter<CircleArticleBean> {


    public CircleVideoAdapter(Context context, List<CircleArticleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleArticleBean circleArticleBean) {

    }
}
