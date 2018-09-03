package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.view.View;

import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.provide.CountDownMessage;
import com.gxtc.huchuan.utils.DateUtil;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/3/8.
 */

public class CountDownMessageView implements ItemViewDelegate<Message> {
    private Activity mActivity;

    public CountDownMessageView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_count_down_layout;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "XM:CdMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(ViewHolder holder, Message message, int position) {
        final CountDownMessage content = (CountDownMessage) message.getContent();
        Long millisInFuture = Long.valueOf(content.getTimestamp());
        holder.setText(R.id.start_time, "本次直播将于" + DateUtil.formatTime(millisInFuture, "yyyy年MM月dd日 HH:mm") + " 开始");
        holder.setText(R.id.topic_title, content.getContent());
        holder.setOnClickListener(R.id.tv_see_introduce, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }

}
