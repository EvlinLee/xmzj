package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;

import java.util.List;

/**
 * Created by zzg on 2018/1/17
 */

public class ThumpVosDetailAdapter extends BaseRecyclerAdapter<ThumbsupVosBean> {

    public ThumpVosDetailAdapter(Context context, List<ThumbsupVosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }


    @Override
    public void bindData(ViewHolder holder, final int position, ThumbsupVosBean bean) {
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.thumb_img), bean.getHeadPic(), R.drawable.news_home_place_holder_video);
    }
}
