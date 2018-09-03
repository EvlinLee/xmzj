package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.widget.RecyclerSpace;

import java.util.List;

public class MallLineImageAdapter extends BaseRecyclerAdapter<MallBean>{

    public MallLineImageAdapter(Context context, List<MallBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }


    @Override
    public void bindData(ViewHolder holder, int position, MallBean bean) {
        if(bean == null) return;
        ImageView iv = (ImageView) holder.getView(R.id.image);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv.getLayoutParams();
        params.height = (WindowUtil.getScreenW(context) * bean.getPicUrlY()) / bean.getPicUrlX();
        ImageHelper.loadImage(holder.getItemView().getContext(), iv, bean.getPicUrl());
    }
}
