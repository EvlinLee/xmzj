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
 * 接口要换新的了  所以适配器要重新换掉
 * @see UnifyClassAdapter
 */
@Deprecated
public class LiveRoomNewAdapter extends BaseRecyclerAdapter<ChatInfosBean> {

    public LiveRoomNewAdapter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);

    }

    @Override
    public void bindData(ViewHolder holder, int position, ChatInfosBean o) {
        if (o == null) {
            return;
        }
        TextView price = holder.getViewV2(R.id.item_live_room_price);
        if (o.isFree()) {
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.text_color_999));
            price.setText("免费");
        } else {
            price.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_fb4717));

            price.setVisibility(View.VISIBLE);
            price.setText(String.format("￥%s", o.getFee()));
        }

        TextView tvTime = (TextView) holder.getViewV2(R.id.living_time);
        if("1".equals(o.getShowinfo())){
            tvTime.setText("已开始");
        }else {
            setTime(o, tvTime);
        }

        holder.setText(R.id.item_live_room_name, o.getChatRoomName()).setText(R.id.item_live_room_title, o.getSubtitle());

        ImageView imgFace = holder.getImageView(R.id.item_live_room_image);
        ImageHelper.loadImage(getContext(), imgFace , o.getFacePic(), R.drawable.live_room_icon_temp);

        TextView status = holder.getViewV2(R.id.tv_item_live_list_status);
        //系列课标志
        if("0".equals(o.getChatSeries())){
            status.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
        }else{
            status.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.GONE);
        }

        TextView tvCount = holder.getViewV2(R.id.living_count);
        tvCount.setText(o.getJoinCount() + "人次");
        tvCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void ConfigView(View view, int resId) {

    }

    private void setTime(ChatInfosBean data, TextView mTvStarttime) {
        if(TextUtils.isEmpty(data.getStarttime())) return;
        Long aLong = Long.valueOf(data.getStarttime());

        if(!TextUtils.isEmpty(data.getEndtime())){
            mTvStarttime.setText("已开始");

        }else{
            mTvStarttime.setText(DateUtil.formatTime(aLong, "MM-dd HH:mm"));
            long l = aLong - System.currentTimeMillis();
            if (l > 0) {
                String[] strings = DateUtil.countDownNotAddZero(l);
                if (!strings[0].equals("0")) {
                    mTvStarttime.setText(strings[0] + "天");

                } else if (!strings[1].equals("0")) {
                    mTvStarttime.setText(strings[1] + "小时");

                } else if(!strings[2].equals("0")) {
                    mTvStarttime.setText(strings[2] + "分");

                }else{
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
