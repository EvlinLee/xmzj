package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.bean.TextSingleListBean;

import java.util.List;

/**
 * Created by Steven on 17/2/28.
 */

public class TextSingleAdapter extends AbsBaseAdapter<TextSingleListBean> {

    public TextSingleAdapter(Context context, List<TextSingleListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, TextSingleListBean textSingleListBean, int position) {

    }


}
