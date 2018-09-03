package com.gxtc.huchuan.im.provide;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.huchuan.R;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * 这个类 占个位置  融云自主义 消息 需要 提供的 界面
 *
 * Created by Gubr on 2017/3/10.
 */
@ProviderTag(messageContent = PPTMessage.class, showReadState = true)
public class PPTMessageProvider extends IContainerItemProvider.MessageProvider<CountDownMessage> {
    private static final String TAG = "CountDownMessageProvide";

    @Override
    public void bindView(View view, int i, CountDownMessage countDownMessage, UIMessage uiMessage) {

    }

    @Override
    public Spannable getContentSummary(CountDownMessage countDownMessage) {
        return null;
    }

    @Override
    public void onItemClick(View view, int i, CountDownMessage countDownMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, CountDownMessage countDownMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_count_down_layout, null);
        ViewHolder holder = new ViewHolder();
        holder.message=view;
        view.setTag(holder);
        return view;
    }


    private static class ViewHolder{
        View message;
    }
}
