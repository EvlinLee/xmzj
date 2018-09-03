package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.view.View;

import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;

import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/4/29.
 */

public abstract class  AbsMessageView implements ItemViewDelegate<Message> {
    protected Activity mActivity;
    public AbsMessageView(Activity activity) {
        mActivity=activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return false;
    }

    @Override
    public void convert(ViewHolder holder, Message message, int position) {

    }


    public interface MessageViewListener  extends View.OnClickListener{
        boolean onLongClick(View v,Message message);
    }

    protected MessageViewListener l;

    public AbsMessageView setMessageViewListener(MessageViewListener l) {
        this.l = l;
        return this;
    }

    public Activity getActivity() {
        return mActivity;
    }

}
