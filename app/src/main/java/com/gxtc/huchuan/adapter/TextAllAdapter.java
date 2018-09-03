package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.bean.TextAllListBean;

import java.util.List;

/**
 * Created by Steven on 17/3/1.
 */

public class TextAllAdapter extends AbsBaseAdapter<TextAllListBean> {


    public TextAllAdapter(Context context, List<TextAllListBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, TextAllListBean textAllListBean, int position) {

    }
}
