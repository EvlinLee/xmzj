package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.bean.ArticleListBean;

import java.util.List;

/**
 * Created by Steven on 17/3/21.
 */

public class ArticleListAdapter extends BaseRecyclerAdapter<ArticleListBean>{


    public ArticleListAdapter(Context context, List<ArticleListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ArticleListBean bean) {

    }
}
