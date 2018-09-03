package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FlowListBean;

import java.util.List;

/**
 * Created by Steven on 17/2/14.
 */

public class FlowListAdapter extends BaseRecyclerAdapter<FlowListBean> implements View.OnClickListener {

    public FlowListAdapter(Context context, List<FlowListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, FlowListBean bean) {
        ImageView imgSelect = (ImageView) holder.getView(R.id.img_flow_shade);

        if(bean.isSelect()){
            imgSelect.setVisibility(View.VISIBLE);

        }else{
            imgSelect.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }


}
