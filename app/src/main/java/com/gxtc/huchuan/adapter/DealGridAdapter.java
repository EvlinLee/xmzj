package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealTypeBean;

import java.util.List;

public class DealGridAdapter extends AbsBaseAdapter<DealTypeBean> {

    private int width ;    //计算图片宽度
    private int margin;

    public DealGridAdapter(Context context, List<DealTypeBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);

        int screenWidth = WindowUtil.getScreenWidth(context);
        margin = (int) context.getResources().getDimension(R.dimen.margin_small);
        width = (screenWidth - margin * 7) / 2;
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, DealTypeBean bean, int position) {
        TextView tvName = (TextView) holder.getView(R.id.item_live_head_title);
        ImageView img = (ImageView) holder.getView(R.id.item_live_head_icon);

        tvName.setText(bean.getTypeName());
        ImageHelper.loadImage(getContext(),img,bean.getPic());

    }


}