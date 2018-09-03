package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.bean.MsgAnalyseListBean;

import java.util.List;

/**
 * Created by Steven on 17/3/1.
 */

public class MsgAnalyseAdapter extends AbsBaseAdapter<MsgAnalyseListBean> {

    public MsgAnalyseAdapter(Context context, List<MsgAnalyseListBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, MsgAnalyseListBean msgAnalyseListBean, int position) {

    }
}
