package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.bean.UserRiseDataBean;

import java.util.List;

/**
 * Created by Steven on 17/2/23.
 */

public class UserRiseAdapter extends AbsBaseAdapter<UserRiseDataBean> {


    public UserRiseAdapter(Context context, List<UserRiseDataBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, UserRiseDataBean bean, int position) {

    }
}
