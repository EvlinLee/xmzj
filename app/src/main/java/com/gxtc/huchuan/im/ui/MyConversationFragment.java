package com.gxtc.huchuan.im.ui;

import android.content.Context;

import com.gxtc.huchuan.im.extension.CollectAction;
import com.gxtc.huchuan.im.extension.DeleteAtion;
import com.gxtc.huchuan.im.extension.MoreAction;
import com.gxtc.huchuan.ui.im.video.VideoMessage;
import com.melink.bqmmplugin.rc.EmojiMessage;
import com.melink.bqmmplugin.rc.GifMessage;
import com.melink.bqmmplugin.rc.bqmmsdk.widget.BQMMMessageText;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/24.
 */
public class MyConversationFragment extends CopyConversationFragment {

    @Override
    public List<IClickActions> getMoreClickActions() {
        List<IClickActions> actions = new ArrayList<>();
        actions.add(new DeleteAtion());
        actions.add(new CollectAction());
        actions.add(new MoreAction());
        return actions;
    }

    @Override
    public MessageListAdapter onResolveAdapter(Context context) {
        return new MyConversationAdapter(context);
    }

    class MyConversationAdapter extends MessageListAdapter{

        public MyConversationAdapter(Context context) {
            super(context);
        }

        //设置可以允许多选的消息
        @Override
        protected boolean allowShowCheckButton(Message message) {
            MessageContent content = message.getContent();
            return (content instanceof TextMessage
                    || content instanceof VoiceMessage
                    || content instanceof ImageMessage
                    || content instanceof EmojiMessage
                    || content instanceof GifMessage
                    || content instanceof VideoMessage)
                    && super.allowShowCheckButton(message);
        }
    }

}
