package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveListBean;

import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 */

public class LiveListAdapter extends BaseRecyclerAdapter<LiveListBean> {
    public LiveListAdapter(Context context, List<LiveListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, LiveListBean o) {
        holder.setText(R.id.tv_item_live_list_title, "第二季第3期 过每体质，如何做好")
                .setText(R.id.tv_item_live_list_content, "陈伟健康说-健康说频道")
                .setText(R.id.tv_item_live_list_people, "200578人次");
//                .setText(R.id.tv_item_live_list_count, "142条语音");
        boolean isLiving = true;
        holder.getView(R.id.iv_item_live_list_isliving).setVisibility(isLiving ? View.VISIBLE : View.GONE);
//        holder.getView(R.id.tv_item_live_list_count).setVisibility(isLiving ? View.GONE : View.VISIBLE);
        ImageHelper.loadImage(MyApplication.getInstance(), (ImageView) holder.getViewV2(R.id.iv_item_live_list_image),"www.baidu.com",R.drawable.live_class_icon_temp);

    }


}
