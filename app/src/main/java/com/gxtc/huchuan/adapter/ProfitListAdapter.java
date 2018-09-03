package com.gxtc.huchuan.adapter;

import android.content.Context;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.bean.ProfitListBean;
import java.util.List;

public class ProfitListAdapter extends BaseRecyclerAdapter<ProfitListBean>{

    public ProfitListAdapter(Context context, List<ProfitListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ProfitListBean profitListBean) {

    }
}
