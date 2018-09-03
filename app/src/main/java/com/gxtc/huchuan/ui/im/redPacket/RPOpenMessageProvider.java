package com.gxtc.huchuan.ui.im.redPacket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 */

@ProviderTag(messageContent = RPOpenMessage.class, showSummaryWithName = false,showPortrait = false,centerInHorizontal = true)
public class RPOpenMessageProvider extends IContainerItemProvider.MessageProvider<RPOpenMessage>{

    @Override
    public void bindView(View view, int i, RPOpenMessage rpOpenMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if(rpOpenMessage != null && rpOpenMessage.getUserInfo() != null) {
            String currUser = UserManager.getInstance().getUserCode();
            String openUser = rpOpenMessage.getUserInfo().getUserId();
            String sendUser = rpOpenMessage.getSendNickId();

            if(TextUtils.isEmpty(currUser) || TextUtils.isEmpty(openUser) || TextUtils.isEmpty(sendUser)) {
                LogUtil.i("id不能为空!!!");
                return;
            }
            String          temp;
            SpannableString spannableString;
            //自己的领取消息
            if(currUser.equals(openUser)) {

                //自己领取自己发的红包
                if(currUser.equals(sendUser)) {
                    if("1".equals(rpOpenMessage.getIsGetDone())) {
                        temp = "你领取了自己的红包，你的红包已被领完";
                        spannableString = new SpannableString(temp);
                        spannableString.setSpan(new TextClickSpan(), temp.length() - 6, temp.length() - 4, 33);
                    } else {
                        temp = "你领取了自己的红包";
                        spannableString = new SpannableString(temp);
                        spannableString.setSpan(new TextClickSpan(), temp.length() - 2, temp.length(), 33);
                    }
                //自己领取别人发的红包
                } else {
                    String name = "";
                    if(TextUtils.isEmpty(name)) {
                        name = rpOpenMessage.getSendNickName();
                    }
                    temp = "你领取了" + name + "的红包";
                }

                spannableString = new SpannableString(temp);
                spannableString.setSpan(new TextClickSpan(), temp.length() - 2, temp.length(), 33);
                holder.packet_message.setText(spannableString);
                holder.packet_message.setTag(rpOpenMessage.getRedId());
                holder.packet_message.setMovementMethod(LinkMovementMethod.getInstance());

            //别人领取你的红包消息
            } else if(currUser.equals(sendUser)) {
                if("1".equals(rpOpenMessage.getIsGetDone())) {
                    temp = rpOpenMessage.getUserInfo().getName() + "领取了你的红包，你的红包已被领完";
                    spannableString = new SpannableString(temp);
                    spannableString.setSpan(new TextClickSpan(), temp.length() - 6, temp.length() - 4, 33);
                } else {
                    temp = rpOpenMessage.getUserInfo().getName() + "领取了你的红包";
                    spannableString = new SpannableString(temp);
                    spannableString.setSpan(new TextClickSpan(), temp.length() - 2, temp.length(), 33);
                }

                holder.packet_message.setText(spannableString);
                holder.packet_message.setTag(rpOpenMessage.getRedId());
                holder.packet_message.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public Spannable getContentSummary(RPOpenMessage rpOpenMessage) {
        if(rpOpenMessage != null && rpOpenMessage.getUserInfo() != null) {
            String currUser = UserManager.getInstance().getUserCode();
            String openUser = rpOpenMessage.getUserInfo().getUserId();
            String sendUser = rpOpenMessage.getSendNickId();
            if(TextUtils.isEmpty(currUser) || TextUtils.isEmpty(openUser) || TextUtils.isEmpty(sendUser)) {
                LogUtil.i("id不能为空!!!");
                return null;
            }
            String temp;
            //自己的领取消息
            if(currUser.equals(openUser)) {

                //自己领取自己的红包
                if(currUser.equals(sendUser)) {
                    if("1".equals(rpOpenMessage.getIsGetDone())) {
                        temp = "你领取了自己的红包，你的红包已被领完";
                    } else {
                        temp = "你领取了自己的红包";
                    }

                //自己领取别人发的红包
                } else {
                    String name = "";
                    if(TextUtils.isEmpty(name)) {
                        name = rpOpenMessage.getSendNickName();
                    }
                    temp = "你领取了" + name + "的红包";
                }
                return new SpannableString(temp);
            }
            if(currUser.equals(sendUser)) {
                if("1".equals(rpOpenMessage.getIsGetDone())) {
                    temp = rpOpenMessage.getUserInfo().getName() + "领取了你的红包，你的红包已被领完";
                } else {
                    temp = rpOpenMessage.getUserInfo().getName() + "领取了你的红包";
                }
                return new SpannableString(temp);
            }
        }
        return new SpannableString("[红包消息]");
    }

    @Override
    public void onItemClick(View view, int i, RPOpenMessage rpOpenMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, RPOpenMessage rpOpenMessage,
            UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_open_rp_message, null);
        ViewHolder holder = new ViewHolder();
        holder.packet_message = (TextView)view.findViewById(R.id.packet_message);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        TextView packet_message;

        ViewHolder() {
        }
    }

    private class TextClickSpan extends ClickableSpan{

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#fa9d3b"));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            String id     = (String) widget.getTag();
            Intent intent = new Intent(widget.getContext(), RedPacketDetailedActivity.class);
            intent.putExtra("id", id);
            widget.getContext().startActivity(intent);
        }
    }

}
