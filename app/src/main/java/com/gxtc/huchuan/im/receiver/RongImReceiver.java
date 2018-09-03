package com.gxtc.huchuan.im.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ActivityUtils;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * 融云消息推送接收器
 * app在前台是不走推送的  所以不会有通知栏通知
 */
public class RongImReceiver extends PushMessageReceiver {

     @Override
     public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
         if(TextUtils.isEmpty(message.getPushContent())){
             return true;
         }
        return false;
     }

     @Override
     public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
         List<Intent> intents = new ArrayList<>();

         if(!ActivityUtils.isActivityExists(context.getPackageName(),".ui.MainActivity",context)){
             Intent intentMain = new Intent(context, MainActivity.class);
             intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             intents.add(intentMain);
         }

         //如果app没有连接融云，跳转到会话界面是没消息的
         Intent intent = new Intent(context, ConversationActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         Uri.Builder builder = Uri.parse("rong://" + context.getPackageName()).buildUpon();
         builder.appendPath("conversation").appendPath(message.getConversationType().getName().toLowerCase())
                .appendQueryParameter("targetId",message.getTargetId())
                .appendQueryParameter("title", message.getTargetUserName());

         Uri uri = builder.build();
         intent.setData(uri);

         intents.add(intent);
         startActivity(context,intents);

         return true;
     }

    private void startActivity(Context context, List<Intent> intents){
        Intent intentArr [] = new Intent[intents.size()];
        for (int i = 0; i < intents.size(); i++) {
            intentArr [i] = intents.get(i);
        }
        context.startActivities(intentArr);
    }
}