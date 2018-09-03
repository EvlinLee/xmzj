package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BgPicBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/24.
 */

public class LiveBgSettingAdapter extends BaseRecyclerAdapter<BgPicBean> {

    public LiveBgSettingAdapter(Context context, List<BgPicBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, BgPicBean bgPicBean) {
        ImageView ivBg = (ImageView) holder.getView(R.id.iv_bg);
        ImageHelper.loadImage(context,ivBg,bgPicBean.getPicUrl(),R.drawable.live_foreshow_img_temp);

        ImageView imgSelect = (ImageView) holder.getView(R.id.img_bg_shadow);

        if(bgPicBean.isSelect()){
            imgSelect.setVisibility(View.VISIBLE);

        }else{
            imgSelect.setVisibility(View.INVISIBLE);
        }
    }
}
