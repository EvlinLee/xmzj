package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.view.View;

import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.utils.StringUtil;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/2/21.
 * 红包
 */

public class RedPacketMessageView implements ItemViewDelegate<Message> {
    private final Activity             mActivity;
    private       PopReward            mPopReward;
    private       View.OnClickListener l;

    public RedPacketMessageView(Activity activity) {
        mActivity = activity;
    }


    private static final String TAG = "RedPacketMessageView";

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_chat_red_packet;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "XM:RpMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(ViewHolder holder, Message message, int position) {
        RedPacketMessage content = (RedPacketMessage) message.getContent();

        String money = StringUtil.formatMoney(2,content.getPrice());

        holder.setText(R.id.tv_message, content.getContent()).setText(R.id.tv_red_packet_info,
                money + "元红包").setTag(R.id.ll_red_packet_area,
                content).setOnClickListener(R.id.ll_red_packet_area, l);
    }

    public RedPacketMessageView setOnClickListener(View.OnClickListener l) {
        this.l = l;
        return this;
    }
}
