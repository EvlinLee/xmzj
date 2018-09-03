package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/3/15.
 * 推送 信息 体
 */


public class ReceivedMessageView implements ItemViewDelegate<Message>, View.OnClickListener {
    private Activity mActivity;

    public ReceivedMessageView(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_chat_received_push;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "XM:ReMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(ViewHolder holder, Message message, int position) {
        String senderUserId = message.getSenderUserId();
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
