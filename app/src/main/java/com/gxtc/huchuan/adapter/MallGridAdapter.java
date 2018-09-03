package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.NewsAdsBean;

import java.util.List;

public class MallGridAdapter extends AbsBaseAdapter<MallBean> {

    private Context context;

    public MallGridAdapter(Context context, List<MallBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, MallBean bean, int position) {
        ImageView img = (ImageView) holder.getView(R.id.image);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams();
        params.height = ((WindowUtil.getScreenW(context)/4) * bean.getPicUrlY()) / bean.getPicUrlX();
        ImageHelper.loadImage(context,img,bean.getPicUrl());
    }


}