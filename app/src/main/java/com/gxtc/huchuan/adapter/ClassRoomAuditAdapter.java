package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;

import java.util.List;

/**
 * 直播首页课堂适配器
 * Created by Gubr on 2017/2/10.
 */

public class ClassRoomAuditAdapter extends BaseRecyclerAdapter<ChatInfosBean> {
    private static final String TAG = "ClassRoomAuditAdapter";

    public ClassRoomAuditAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, final ChatInfosBean o) {
        if (o == null) {
            return;
        }
        TextView price = holder.getViewV2(R.id.item_live_room_price);
        if (o.isFree()) {
//            price.setVisibility(View.GONE);
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_119b1e));
            price.setText("免费");
        } else {
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_fb4717));
            price.setVisibility(View.VISIBLE);
            price.setText(String.format("￥：%s", o.getFee()));
        }
        Integer integer = Integer.valueOf(o.getJoinCount());

            holder.getView(R.id.item_live_room_content).setVisibility(TextUtils.isEmpty(o.getDesc())?View.GONE:View.VISIBLE);
//              .setText(R.id.item_live_room_content,o.getDesc())

        holder.setText(R.id.item_live_room_count, String.format("%s人次", o.getJoinCount()))
                .setText(R.id.item_live_room_name, o.getChatRoomName())
                .setText(R.id.item_live_room_title, o.getSubtitle())
                .getView(R.id.iv_fire).setVisibility(integer.intValue()>20?View.VISIBLE:View.GONE);
        ImageHelper.loadHeadIcon(holder.getItemView().getContext(),holder.getImageView(R.id.item_live_room_head),R.drawable.live_head_icom_temp,o.getHeadPic());
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.item_live_room_image), o.getFacePic(), R.drawable.live_room_icon_temp);

        holder.setOnClick(R.id.btn_submit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: lala");
                if (l != null) {
                    l.onAuditClick(o);
                }
            }
        });
//        holder.setImage(R.id.item_live_room_image, R.drawable.live_room_icon_temp)
//                .setImage(R.id.item_live_room_head, R.drawable.live_head_icom_temp)
//                .setText(R.id.item_live_room_name, "小Y老师的家")
//                .setText(R.id.item_live_room_price, "￥:9.9")
//                .setText(R.id.item_live_room_title, "微课！新年规划九宫格法--找到这9种资源，2017想不成功也难！")
//                .setText(R.id.item_live_room_intro, "当我们意识到为时已晚的时候，恰恰是在最早的时候...")
//                .setText(R.id.item_live_room_count,"295人在线");
    }
    private OnAuditListener l;

    public void setOnAuditListener(OnAuditListener l) {
        this.l = l;
    }

    public static interface OnAuditListener{
        public void onAuditClick(ChatInfosBean bean);
    }


}
