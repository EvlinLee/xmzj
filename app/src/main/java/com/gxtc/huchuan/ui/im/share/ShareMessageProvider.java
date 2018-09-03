package com.gxtc.huchuan.ui.im.share;

import android.app.Activity;
import android.content.Context;
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
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 分享消息提供者
 */
@ProviderTag(messageContent = ShareMessage.class, showReadState = true)
public class ShareMessageProvider extends IContainerItemProvider.MessageProvider<ShareMessage> {

    private Context            mContext;
    private CircleShareHandler mShareUtil;

    @Override
    public void bindView(View view, int i, ShareMessage sMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        //发送方
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.layoutContent.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);

        //接收方
        } else {
            holder.layoutContent.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
        }
        ImageHelper.loadImage(MyApplication.getInstance(),holder.img,sMessage.getTypeCover(),R.drawable.live_list_place_holder_120);
        String title = sMessage.getTypeTitle() + "";
        holder.tvTitle.setText(title);

        String type = "";
        String infoType = sMessage.getInfoType();
        if(CircleShareHandler.SHARE_NEW.equals(infoType)){
            type = "文章";
        }

        if(CircleShareHandler.SHARE_CLASS.equals(infoType)
                || CircleShareHandler.SHARE_CLASS_ADMIN.equals(infoType)
                || CircleShareHandler.SHARE_FREE_CLASS.equals(infoType)
                || CircleShareHandler.SHARE_TEACHER.equals(infoType)){
            type = "课程";
        }

        if(CircleShareHandler.SHARE_DEAL.equals(infoType)){
            type = "交易";
        }

        if(CircleShareHandler.SHARE_CIRCLE.equals(infoType)
                || CircleShareHandler.SHARE_CIRCLE_ADMIN.equals(infoType)
                || CircleShareHandler.SHARE_FREE_CIRCLE.equals(infoType)){
            type = "圈子";
        }

        if(CircleShareHandler.SHARE_SERIES.equals(infoType) || CircleShareHandler.SHARE_FREE_SERIES.equals(infoType)){
            type = "系列课";
        }

        if(CircleShareHandler.SHARE_MALL.equals(infoType)){
            type = "商品";
        }

        if(CircleShareHandler.SHARE_LIVE.equals(infoType)){
            type = "课堂";
        }

        if(CircleShareHandler.SHARE_SPECIAL.equals(infoType)){
            type = "专题";
        }
        holder.tvType.setText(type);
    }

    @Override
    public Spannable getContentSummary(ShareMessage message) {
        String title = message.getTypeTitle();
        if(TextUtils.isEmpty(title)) title = "";
        return new SpannableString(title);
    }


    @Override
    public void onItemClick(View view, int i, ShareMessage shareMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(final View view, int i, ShareMessage shareMessage, UIMessage uiMessage) {
        String [] items = new String[]{"发送好友"};
        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                //在会话页面发送转发好友消息
                if(view.getContext() instanceof Activity){
                    ConversationListActivity.startActivity((Activity) view.getContext(), ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE);
                }
            }
        }).show();

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        mContext = context;
        View       view   = LayoutInflater.from(context).inflate(R.layout.item_share_message, null);
        ViewHolder holder = new ViewHolder();
        holder.layoutContent =  view.findViewById(R.id.layout_content);
        holder.img = (ImageView) view.findViewById(R.id.img_share);
        holder.tvTitle = (TextView) view.findViewById(R.id.tv_share);
        holder.tvType = (TextView) view.findViewById(R.id.tv_type);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        View      layoutContent;
        ImageView img;
        TextView  tvTitle;
        TextView  tvType;
    }
}
