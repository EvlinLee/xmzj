package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;

import java.util.List;

/**
 * 直播头适配器
 * Created by Gubr on 2017/2/10.
 */

public class LiveHeadIconAdapter extends BaseRecyclerAdapter<LiveHeadTitleBean> {


    public LiveHeadIconAdapter(Context context, List<LiveHeadTitleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, LiveHeadTitleBean o) {
        if (position!=7){
            holder.setImage(R.id.item_live_head_icon,R.drawable.live_head_icom_temp).setText(R.id.item_live_head_title,o.getTypeName());
        }else{
            holder.setImage(R.id.item_live_head_icon,R.drawable.live_head_icom_temp).setText(R.id.item_live_head_title,"更多");
        }
    }

    @Override
    public int getItemCount() {
        if (getList() != null) {
            if (getList().size()>8) {
                return 8;
            }
        }
        return super.getItemCount();
    }
}
