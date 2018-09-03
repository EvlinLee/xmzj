package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChooseClassifyBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/15.
 */

public class ChooseClassifyAdapter extends BaseRecyclerAdapter<ChooseClassifyBean> implements View.OnClickListener{
    public ChooseClassifyAdapter(Context context, List<ChooseClassifyBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final ChooseClassifyBean bean) {

        TextView tvSeriesType = (TextView) holder.getView(R.id.tv_series_type);
        tvSeriesType.setText(bean.getTypeName());


        final ImageView ivCheck = (ImageView) holder.getView(R.id.iv_check);
        ivCheck.setTag(bean);

        if(bean.isSelect()){
            ivCheck.setVisibility(View.VISIBLE);

        }else{
            ivCheck.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_check:

                break;
        }
    }
}
