package com.gxtc.huchuan;

import android.content.Context;

import com.gxtc.huchuan.im.provide.BlacklistMessage;
import com.gxtc.huchuan.im.provide.BlacklistMessageProvider;
import com.gxtc.huchuan.im.provide.CountDownMessage;
import com.gxtc.huchuan.im.provide.CountDownMessageProvider;
import com.gxtc.huchuan.im.provide.MyPrivateConversationProvider;
import com.gxtc.huchuan.im.provide.PPTMessage;
import com.gxtc.huchuan.im.provide.PPTMessageProvider;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.im.provide.RedPacketMessageProvider;
import com.gxtc.huchuan.im.provide.RemoveMessage;
import com.gxtc.huchuan.im.provide.RemoveMessageProvider;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.im.provide.SilentMessageProvider;
import com.gxtc.huchuan.ui.im.merge.MergeHistoryMessage;
import com.gxtc.huchuan.ui.im.merge.MergeHistoryMessageProvider;
import com.gxtc.huchuan.ui.im.postcard.PTMessage;
import com.gxtc.huchuan.ui.im.postcard.PTMessageProvider;
import com.gxtc.huchuan.ui.im.redPacket.RPMessage;
import com.gxtc.huchuan.ui.im.redPacket.RPMessageProvider;
import com.gxtc.huchuan.ui.im.redPacket.RPOpenMessage;
import com.gxtc.huchuan.ui.im.redPacket.RPOpenMessageProvider;
import com.gxtc.huchuan.ui.im.share.ShareMessage;
import com.gxtc.huchuan.ui.im.share.ShareMessageProvider;
import com.gxtc.huchuan.ui.im.system.ArticleMessage;
import com.gxtc.huchuan.ui.im.system.ArticleMessageProvider;
import com.gxtc.huchuan.ui.im.system.CircleMessage;
import com.gxtc.huchuan.ui.im.system.CircleMessageProvider;
import com.gxtc.huchuan.ui.im.system.ClassMessage;
import com.gxtc.huchuan.ui.im.system.ClassMessageProvider;
import com.gxtc.huchuan.ui.im.system.DealMessage;
import com.gxtc.huchuan.ui.im.system.DealMessageProvider;
import com.gxtc.huchuan.ui.im.system.MallMessage;
import com.gxtc.huchuan.ui.im.system.MallMessageProvider;
import com.gxtc.huchuan.ui.im.system.SystemMessage;
import com.gxtc.huchuan.ui.im.system.SystemMessageProvider;
import com.gxtc.huchuan.ui.im.system.TradeInfoMessage;
import com.gxtc.huchuan.ui.im.system.TradeInfoMessageProvider;
import com.gxtc.huchuan.ui.im.video.VideoMessage;
import com.gxtc.huchuan.ui.im.video.VideoMessageProvider;

import im.collect.CollectMessage;
import im.collect.CollectMessageProvider;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by Gubr on 2017/3/23.
 * 在这里 设置 融云的一些监听事件
 */

public class RongImContext implements RongIMClient.ConnectionStatusListener {


    private static RongImContext mRongCloudInstance;

    private RongImContext(Context context) {
        initRegisterMessage();
        initListener();
    }

    private void initRegisterMessage() {
        RongIM.registerMessageType(CountDownMessage.class);
        RongIM.registerMessageType(RedPacketMessage.class);
        RongIM.registerMessageType(SilentMessage.class);
        RongIM.registerMessageType(BlacklistMessage.class);
        RongIM.registerMessageType(RemoveMessage.class);
        RongIM.registerMessageType(PPTMessage.class);
        RongIM.registerMessageType(RPMessage.class);
        RongIM.registerMessageType(PTMessage.class);
        RongIM.registerMessageType(RPOpenMessage.class);
        RongIM.registerMessageType(ShareMessage.class);
        RongIM.registerMessageType(CollectMessage.class);
        RongIM.registerMessageType(ClassMessage.class);
        RongIM.registerMessageType(DealMessage.class);
        RongIM.registerMessageType(CircleMessage.class);
        RongIM.registerMessageType(ArticleMessage.class);
        RongIM.registerMessageType(SystemMessage.class);
        RongIM.registerMessageType(MallMessage.class);
        RongIM.registerMessageType(VideoMessage.class);
        RongIM.registerMessageType(TradeInfoMessage.class);
        RongIM.registerMessageType(MergeHistoryMessage.class);
        RongIM.registerMessageTemplate(new MergeHistoryMessageProvider());
        RongIM.registerMessageTemplate(new TradeInfoMessageProvider());
        RongIM.registerMessageTemplate(new VideoMessageProvider());
        RongIM.registerMessageTemplate(new MallMessageProvider());
        RongIM.registerMessageTemplate(new SystemMessageProvider());
        RongIM.registerMessageTemplate(new ArticleMessageProvider());
        RongIM.registerMessageTemplate(new CircleMessageProvider());
        RongIM.registerMessageTemplate(new ClassMessageProvider());
        RongIM.registerMessageTemplate(new DealMessageProvider());
        RongIM.registerMessageTemplate(new ShareMessageProvider());
        RongIM.registerMessageTemplate(new CollectMessageProvider());
        RongIM.registerMessageTemplate(new PTMessageProvider());
        RongIM.registerMessageTemplate(new CountDownMessageProvider());
        RongIM.registerMessageTemplate(new RedPacketMessageProvider());
        RongIM.registerMessageTemplate(new SilentMessageProvider());
        RongIM.registerMessageTemplate(new BlacklistMessageProvider());
        RongIM.registerMessageTemplate(new RemoveMessageProvider());
        RongIM.registerMessageTemplate(new PPTMessageProvider());
        RongIM.registerMessageTemplate(new RPOpenMessageProvider());
        RongIM.registerMessageTemplate(new RPMessageProvider());

        RongIM.getInstance().registerConversationTemplate(new MyPrivateConversationProvider());
    }


    public static void init(Context context) {
        if (mRongCloudInstance == null) {
            synchronized (RongImContext.class) {
                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongImContext(context);
                }
            }
        }
    }

    private void initListener() {
        RongIM.setConnectionStatusListener(this);
    }


    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {

            case CONNECTED://连接成功。

                break;

            case DISCONNECTED://断开连接。

                break;

            case CONNECTING://连接中。

                break;
            case NETWORK_UNAVAILABLE://网络不可用。

                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线

                break;
        }
    }
}
