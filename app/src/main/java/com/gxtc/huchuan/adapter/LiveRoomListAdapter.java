package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;

import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */

public class LiveRoomListAdapter extends BaseRecyclerAdapter<FollowLecturerBean> {



    public LiveRoomListAdapter(Context context, List<FollowLecturerBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final FollowLecturerBean bean) {
        ImageHelper.loadRound(getContext(), holder.getImageView(R.id.item_live_liveroomlist_head), bean.getHeadPic(), 5);

        holder.setText(R.id.item_live_liveroomlist_roomname,bean.getName())
                .setText(R.id.item_live_liveroomlist_attention_count,"粉丝：" + bean.getFsCount())
                .setText(R.id.item_live_liveroomlist_live_count, "课程：" + bean.getNum());


//        TextView attention = holder.getViewV2(R.id.item_live_liveroomlist_live_count);
//        attention.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (l != null) {
//                    l.onFollowCick(bean.getId(),position);
//                }
//            }
//        });


    }
    OnFollowClickListener l;

    public void setOnFollowClickListener(OnFollowClickListener l) {
        this.l = l;
    }

    public static interface OnFollowClickListener{
       void  onFollowCick(String id,int position);
    }



}
