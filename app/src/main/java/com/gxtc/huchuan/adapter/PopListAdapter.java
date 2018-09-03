package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;

import com.gxtc.commlibrary.base.AbTypeBaseAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MemberBean;

import java.util.List;

/**
 * Created by zzg on 2017/12/2
 */

public class PopListAdapter extends BaseRecyclerAdapter<MallBean> {

    public PopListAdapter(Context context, List<MallBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int type, MallBean o) {
        holder.setText(R.id.tv_title,o.getName());
        if(getList() != null && getList().size() == 1)
        holder.getView(R.id.tv_title).setPadding(0,0,0,0);
    }
}
