package com.gxtc.huchuan.im.provide;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.gxtc.huchuan.R;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.GroupConversationProvider;
import io.rong.imkit.widget.provider.PrivateConversationProvider;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/7.
 */
@ConversationProviderTag(
        conversationType = "group",
        portraitPosition = 1
)
public class MyPrivateConversationProvider extends GroupConversationProvider {

    @Override
    public void bindView(View view, int position, UIConversation data) {
        super.bindView(view, position, data);

        if(data.getConversationType() == Conversation.ConversationType.GROUP){
            String tempName = "";
            if(RongUserInfoManager.getInstance().getGroupInfo(data.getConversationTargetId()) == null) {
                tempName = "";
            } else {
                tempName = RongUserInfoManager.getInstance().getGroupInfo(data.getConversationTargetId()).getName();
            }
            String span = "[图片]";
            String name = span + " " +tempName;
            Drawable d = null;
                if(data.getConversationTargetId().contains("user")){
                    d = view.getContext().getResources().getDrawable(R.drawable.icon_list_group);
                }else{
                    d = view.getContext().getResources().getDrawable(R.drawable.icon_list_circle);
                }
            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            ImageSpan imageSpan = new ImageSpan(d,ImageSpan.ALIGN_BASELINE);
            SpannableString spannableString = new SpannableString(name);
            spannableString.setSpan(imageSpan,0,span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView tv = (TextView) view.findViewById(R.id.rc_conversation_title);
            tv.setText(spannableString);
        }

    }
}

