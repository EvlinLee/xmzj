package com.gxtc.huchuan.ui.im.postcard;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * zzg
 */
@ProviderTag(messageContent = PTMessage.class, showReadState = true)
public class PTMessageProvider extends IContainerItemProvider.MessageProvider<PTMessage> {

    private Context            mContext;
    @Override
    public void bindView(View view, int i, PTMessage sMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        //发送方
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.layoutContent.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);

        //接收方
        } else {
            holder.layoutContent.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
        }
        ImageHelper.loadImage(mContext,holder.img,sMessage.getHeadPic(),R.drawable.live_list_place_holder_120);
        String title = sMessage.getName() ;
        holder.tvTitle.setText(title);

    }

    @Override
    public Spannable getContentSummary(PTMessage message) {
        String content = TextUtils.isEmpty(message.getName()) ? " " : message.getName();
        return new SpannableString(content);
    }


    @Override
    public void onItemClick(View view, int i, PTMessage PTMessage, UIMessage uiMessage) {
        if(!TextUtils.isEmpty(PTMessage.getUserCode())) {
            PersonalInfoActivity.startActivity(mContext, PTMessage.getUserCode());
        }
    }

    @Override
    public void onItemLongClick(View view, int i, PTMessage PTMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        mContext = context;
        View       view   = LayoutInflater.from(context).inflate(R.layout.item_post_card_message, null);
        ViewHolder holder = new ViewHolder();
        holder.layoutContent =  view.findViewById(R.id.layout_content);
        holder.img = (ImageView) view.findViewById(R.id.img_share);
        holder.tvTitle = (TextView) view.findViewById(R.id.tv_share);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        View      layoutContent;
        ImageView img;
        TextView  tvTitle;
    }
}
