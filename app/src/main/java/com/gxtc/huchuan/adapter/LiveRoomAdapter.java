package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * 直播首页课堂适配器
 * Created by Gubr on 2017/2/10.
 */

public class LiveRoomAdapter extends BaseRecyclerAdapter<ChatInfosBean> {

    private int height;
    public LiveRoomAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        height = (int) WindowUtil.getScaleHeight(16f,7.5f,context);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ChatInfosBean o) {
        if (o == null) {
            return;
        }
        TextView price = holder.getViewV2(R.id.item_live_room_price);
        if (o.isFree()) {
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_119b1e));
            price.setText("免费");
        } else {
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_fb4717));

            price.setVisibility(View.VISIBLE);
            price.setText(String.format("￥：%s", o.getFee()));
        }

        int integer = Integer.valueOf(o.getJoinCount());
        setTime(o, (TextView) holder.getViewV2(R.id.living_time));
        holder.setText(R.id.item_live_room_count, String.format("%s人次", o.getJoinCount())).setText(
                R.id.item_live_room_name, o.getChatRoomName()).setText(R.id.item_live_room_title,
                o.getSubtitle()).getView(R.id.iv_fire).setVisibility(
                integer > 20 ? View.VISIBLE : View.GONE);
        ImageHelper.loadHeadIcon(holder.getItemView().getContext(),
                holder.getImageView(R.id.item_live_room_head), R.drawable.live_head_icom_temp,
                o.getHeadPic());
        ImageView playStatus = holder.getViewV2(R.id.bg_play_status);
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id.item_live_room_image), o.getFacePic(), R.drawable.live_room_icon_temp);

        switch (o.getStatus()) {
            //1：预告，2：直播中，3：结束
            case "1":
                setDrawble(R.drawable.bg_unstart, playStatus);
                break;
            case "2":
                setDrawble(R.drawable.palying, playStatus);
                break;
            case "3":
                setDrawble(R.drawable.ended, playStatus);
                break;
        }

    }

    @Override
    public void ConfigView(View view, int resId) {
        if(resId == R.id.item_live_room_image){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
            params.height = height;
        }
    }

    private void setTime(ChatInfosBean data, TextView mTvStarttime) {
        if (!TextUtils.isEmpty(data.getEndtime())) {
            Long aLong = Long.valueOf(data.getEndtime());
            mTvStarttime.setText(DateUtil.formatTime(aLong, "MM-dd HH:mm"));
        } else {
            Long aLong = Long.valueOf(data.getStarttime());
            mTvStarttime.setText(DateUtil.formatTime(aLong, "MM-dd HH:mm"));
            long l = aLong - System.currentTimeMillis();
            if (l > 0) {
                String[] strings = DateUtil.countDown(l);
                if (!strings[0].equals("00")) {
                    mTvStarttime.setText(strings[0] + "天后");
                } else if (!strings[1].equals("00")) {
                    mTvStarttime.setText(strings[1] + "小时后");
                } else {
                    mTvStarttime.setText("进行中");
                }
            }
        }
    }

    public void setDrawble(int resId, ImageView playStatus) {
        Drawable db = context.getResources().getDrawable(resId);
        playStatus.setImageDrawable(db);
    }

}
