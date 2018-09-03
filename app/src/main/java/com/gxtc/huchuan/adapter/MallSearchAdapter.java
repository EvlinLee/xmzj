package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ArticleListBean;
import com.gxtc.huchuan.bean.MallBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzg on 17/3/21.
 */

public class MallSearchAdapter extends BaseRecyclerAdapter<MallBean>{


    public MallSearchAdapter(Context context, ArrayList<MallBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, MallBean bean) {
       
    }
}
