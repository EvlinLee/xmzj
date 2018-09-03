package com.gxtc.huchuan.ui;

import android.text.TextUtils;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.UpdataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventConverListBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventMessageBean;
import com.gxtc.huchuan.bean.event.EventRefreshConversationBean;
import com.gxtc.huchuan.bean.event.EventUpdataBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.im.redPacket.RPOpenMessage;
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.RIMSoundHandler;
import com.gxtc.huchuan.utils.SystemTools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/5.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    private boolean loadUnread = false;

    private CircleSource mData;

    public MainPresenter(MainContract.View view) {
        mView = view;
        connectRongIm();                        //连接融云
        mData = new CircleRepository();
    }


    @Override
    public void checkUpdata() {
        final int code = SystemTools.getAppVersionCode(MyApplication.getInstance());
        AllApi.getInstance()
              .getAppVersion(0)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new ApiObserver<ApiResponseBean<UpdataBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data != null && data instanceof UpdataBean){
                            UpdataBean bean = (UpdataBean) data;
                            if(code < bean.getVersionCode()){
                                mView.showUpdata(bean);
                                EventBusUtil.postStickyEvent(new EventUpdataBean(bean));
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {

                    }
                }));
    }

    //连接融云
    @Override
    public void connectRongIm() {
        if(RongIMClient.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)
            return;

        User user = UserManager.getInstance().getUser();
        if (user == null) return;

        final String token = user.getImToken();
        RongImHelper.connect(MyApplication.getInstance(), token,
                new RongIMClient.ConnectCallback() {

                    // Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                    @Override
                    public void onTokenIncorrect() {
                        LogUtil.i("RongLog","onTokenIncorrect");
                    }

                    @Override
                    public void onSuccess(String s) {
                        RongImHelper.initDefaultConfig();

                        //收到消息是否处理完成，true 表示自己处理铃声和后台通知，false 走融云默认处理方式
                        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
                            @Override
                            public boolean onReceived(Message message, int i) {
                                EventBusUtil.post(new EventMessageBean(message,i));
                                LogUtil.i("剩余未读消息  :  " + i);//我们已经成为好友，快来一起聊天吧！ 以上为打招呼消息
                                if(message.getContent() instanceof RPOpenMessage || message.getContent() instanceof InformationNotificationMessage){
                                    return true;
                                }
                                if(i == 0){
                                    getConversationList();
                                }

                                return false;
                            }
                        });
                        getConversationList();
                        EventBusUtil.post(new EventRefreshConversationBean());  //切换账号登陆时候刷新会话列表
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        LogUtil.i("融云链接失败 mainActicity   " + errorCode);
                    }
                });
    }


    @Override
    public void destroy() {
        RongIM.setOnReceiveMessageListener(null);
        RxTaskHelper.getInstance().cancelTask(this);
    }


    private int unreadCount = 0;    //总的未读消息数量

    //获取会话列表数量
    @Override
    public void getConversationList(){
        RongIM.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                unreadCount = integer;
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    @Override
                    public void onSuccess(List<Conversation> conversations) {
                        LogUtil.i("getConversationList  ");
                        //获取未读消息数量，不包括屏蔽的消息
                        getUnreadCount(conversations);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        LogUtil.i(" getConversationList  onError " + errorCode.getMessage());
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtil.i(" getTotalUnreadCount  onError " + errorCode.getMessage());
            }
        });


    }


    //获取未读消息数量，不包括屏蔽的消息
    private void getUnreadCount(List<Conversation> conversations){
        if(conversations != null){
            if(conversations.size() == 0){
                mView.showUnreadMsg(0);
                return;
            }
            EventBus.getDefault().removeStickyEvent(EventConverListBean.class);
            EventBusUtil.postStickyEvent(new EventConverListBean(conversations.size()));

            ArrayList<Conversation> cover = new ArrayList<>();
            //获取已屏蔽的会话列表
            for(Conversation con : conversations){
                if(con != null && !TextUtils.isEmpty(con.getTargetId()) && con.getNotificationStatus() == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB){
                    cover.add(con);
                }
            }

            //根据已屏蔽的会话列表获取已屏蔽会话的未读数
            RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    if(mView != null){
                        mView.showUnreadMsg(unreadCount - integer);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {}

            }, (Conversation[]) cover.toArray(new Conversation[cover.size()]));

        }else{
            EventBus.getDefault().removeStickyEvent(EventConverListBean.class);
            EventBusUtil.postStickyEvent(new EventConverListBean(0));
        }
    }


    private RongIM.OnReceiveUnreadCountChangedListener mIUnReadMessageObserver = new RongIM.OnReceiveUnreadCountChangedListener() {

        @Override
        public void onMessageIncreased(int integer) {
            if(loadUnread){
                getConversationList();
            }
        }
    };

}
