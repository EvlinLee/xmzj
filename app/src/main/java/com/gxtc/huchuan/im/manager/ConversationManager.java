package com.gxtc.huchuan.im.manager;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.bean.RemoteMessageBean;
import com.gxtc.huchuan.im.bean.RemoteMessageBeanDao;
import com.gxtc.huchuan.im.bean.RemoteMessageBeanDao.Properties;
import com.gxtc.huchuan.im.bean.dao.PlayHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/13.
 * <p>
 * 用来下载服务器上的聊天记录
 * 单例 模式
 * 1. 输入chatinfosbean 判断是否跟现在是同一个
 * 是   不做操作
 * 否   清空聊天记录  去服务器下载新的历史数据
 * <p>
 * 下载的聊天记录放在messageManager里
 */

public class ConversationManager {

    public  ItemScrollBean mItemScrollBean;
    private ChatInfosBean  bean;
    public  int            scrollYdistance;
    public  int            scrollPosition;
    private Uri            currentUri;

    private ConversationManager() {
        mRemoteMessageBeanDao = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao();
        mItemScrollBean = new ItemScrollBean();
    }

    //因为聊天记录在服务器里  又不能一次全部下载   如果在接到IM消息后  还去服务器拿历史消息会 造成消息错乱 所以  在拿 到
    private boolean isCanLoadRemoteMessage = true;
    private RemoteMessageBeanDao mRemoteMessageBeanDao;


    public void init(ChatInfosBean bean) {
        if (this.bean != null && !bean.getId().equals(this.bean.getId())) {
            MessageManager.getInstance().reinit();

            scrollYdistance = -1;
            scrollPosition = -1;
            isCanLoadRemoteMessage = true;
        }
        this.bean = bean;
    }


    public void setCanLoadRemoteMessage(boolean flag) {
        isCanLoadRemoteMessage = flag;
    }

    public boolean isCanLoadRemoteMessage() {
        return isCanLoadRemoteMessage;
    }

    /**
     * 这个方法  用来 播放课程里的语音消息
     */
    public void StartPlayVoiceMessage() {
        //从数据库里  根据课程id 拿到最后播放的uri  。。//数据库里不要保存话音的播放进度  因为会对数据库大量操作。
        if(bean == null)
            return;

        //PlayHistory load = GreenDaoHelper.getInstance().getSeeion().getPlayHistoryDao().load(Long.valueOf(bean.getId()));
        //这里不要记录
        PlayHistory load = null;
        Uri uri = null;
        if (load != null) {
            String uri1 = load.getUri();
            uri = Uri.parse(uri1);
        } else {
            VoiceTimeBean firstVoiceMessage = MessageManager.getInstance().getFirstVoiceMessage();
            if (firstVoiceMessage != null) {
                uri = firstVoiceMessage.getUri();
            }
        }

        if (uri != null)
            AudioPlayManager.getInstance().startPlay(MyApplication.getInstance(), uri);
    }

    /**
     * 获取当前保存的是哪个课程
     * @return
     */
    public ChatInfosBean getChatInfosBean() {
        return bean;
    }


    public void getremoteHistory(int pageSize, String msgType, @NonNull CallBack callBack) {
        if (bean != null && isCanLoadRemoteMessage) {
            List<Message> messages = MessageManager.getInstance().getMessages();

            if (messages != null && messages.size() > 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
                map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
                map.put("targetId", bean.getId());
                map.put("start", 0 + "");
                map.put("pageSize", pageSize + "");
                map.put("msgRankType", "1");


                /**
                 * isBackLoadMsg
                 * 这个方法不是拿全部消息的！！！！！
                 * 这个表示是判断是否是从后台播放语音的时候  从服务器拿数据的标志
                 * 因为后台播放语音的时候拿的数据 只拿 语音消息 有可能语音消息之前参杂有图片消息文字消息
                 * 这时候再进入课堂页面听课的话  就拿不到前面的消息了 ，这时候需要根据这个标志去后台从新请求课堂页面全部的消息回来
                 */
                Message lastMsg = messages.get(messages.size() - 1);
                if( lastMsg != null){
                    Extra extra = MessageFactory.getExtrabyMessage(lastMsg);
                    if (extra != null
                            && bean.getMessageNum() < MessageManager.getInstance().getMessages().size()
                            && !"XM:CdMsg".equals(lastMsg.getObjectName())
                            && !"XM:IvMsg".equals(lastMsg.getObjectName())) {

                        //这里比较特殊才要传2的
                        map.put("msgId", extra.getMsgId());
                        map.put("msgRankType", "2");
                        map.put("isContain", "0");
                        map.put("roleType", "1");
                    }
                }
                requestMessage(map,callBack);
                return;

            } else {
                for (int i = messages.size() - 1; i >= 0; i--) {
                    Message message = messages.get(i);
                    switch (message.getObjectName()) {
                        //只要是服务器上的消息类型 就不再加载
                        case "RC:ImgMsg":
                        case "RC:TxtMsg":
                        case "RC:VcMsg":
                        case "XM:RpMsg":
                            callBack.onCancel();
                            return;
                    }
                }
            }
            remoteHistory(0, pageSize, msgType,callBack);
        }
    }

    boolean isRefresh;


    /**
     * 下拉 传入消息id  获取 上部 消息
     *
     * @param msgId
     * @param roleType
     * @param msgRankType
     */
    @Deprecated
    public void upRemoteHistory(String msgId, String roleType, String msgRankType, @NonNull final CallBack callBack) {
        if (!isCanLoadRemoteMessage && !isRefresh) {
            callBack.onCancel();
            return;
        }
        List<RemoteMessageBean> list = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                Properties.MsgId.eq(msgId)).limit(1).build().list();

        if (list.size() > 0) {
            RemoteMessageBean remoteMessageBean = list.get(0);
            List<RemoteMessageBean> list1 = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                    Properties.Id.lt(remoteMessageBean.getId()),
                    Properties.TargetId.eq(Integer.valueOf(bean.getId())),
                    Properties.ShowType.eq(roleType)).orderDesc(Properties.Id).limit(
                    15).build().list();
            if (list1.size() > 0) {
                Observable.just(list1).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).map(
                        new Func1<List<RemoteMessageBean>, List<Message>>() {
                            @Override
                            public List<Message> call(List<RemoteMessageBean> remoteMessageBeen) {
                                return MessageFactory.createNotInsertDao(remoteMessageBeen);
                            }
                        }).subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        Collections.reverse(messages);
                        callBack.onSuccess(messages);
                    }
                });
                return;
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
        map.put("targetId", bean.getId());
        map.put("roleType", roleType);
        map.put("start", "0");
        map.put("msgId", msgId);
        map.put("msgRankType", msgRankType);

        requestMessage(map,callBack);
    }

    /**
     * 上拉 加载下部数据
     *
     * @param msgId
     * @param roleType
     * @param msgRankType
     * @param callBack
     */
    @Deprecated
    public void downRmoteHistory(String msgId, String roleType, String msgRankType,
            @NonNull final CallBack callBack) {
        if (!isCanLoadRemoteMessage && !isRefresh) {
            callBack.onCancel();
            return;
        }
        //查询最后一条聊天信息
        List<RemoteMessageBean> list = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                Properties.MsgId.eq(msgId)).limit(1).build().list();

        if (list.size() > 0) {
            RemoteMessageBean remoteMessageBean = list.get(0);
            List<RemoteMessageBean> list1 = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                    Properties.ShowType.eq(roleType),
                    Properties.TargetId.eq(Integer.valueOf(bean.getId())),
                    Properties.Id.gt(remoteMessageBean.getId())).orderDesc(Properties.Id).limit(
                    30).build().list();
            if (list1.size() > 0) {
                Observable.just(list1).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).map(
                        new Func1<List<RemoteMessageBean>, List<Message>>() {
                            @Override
                            public List<Message> call(List<RemoteMessageBean> remoteMessageBeen) {
                                return MessageFactory.createNotInsertDao(remoteMessageBeen);
                            }
                        }).subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        Collections.reverse(messages);
                        callBack.onSuccess(messages);
                    }
                });
                return;
            }
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
        map.put("targetId", bean.getId());
        map.put("roleType", roleType);
        map.put("start", "0");
        map.put("pageSize", "10000");
        map.put("msgId", msgId);
        map.put("msgRankType", msgRankType);

        LiveApi.getInstance().getMessageRecordList(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).map(
                new Func1<ApiResponseBean<List<RemoteMessageBean>>, List<Message>>() {
                    @Override
                    public List<Message> call(ApiResponseBean<List<RemoteMessageBean>> listApiResponseBean) {
                        return MessageFactory.create(listApiResponseBean.getResult());
                    }
                }).subscribe(new Observer<List<Message>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(e.getMessage());
            }


            @Override
            public void onNext(List<Message> messages) {
//                MessageManager.getInstance().addMessages(messages);
                callBack.onSuccess(messages);
            }
        });
    }

    /**
     * 这个方法使用来 在后台播放语音  自动加载更多语音的时候调用的
     * @param msgId
     * @param roleType
     * @param msgRankType
     */
    public void downRmoteHistoryByBalckPlay(final String msgId, final String roleType, final String msgRankType) {

        if(TextUtils.isEmpty(msgId) || TextUtils.isEmpty(roleType) || TextUtils.isEmpty(msgRankType)){
            return;
        }

        if (!isCanLoadRemoteMessage && !isRefresh) {
            return;
        }
        List<RemoteMessageBean> list = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                Properties.MsgId.eq(msgId)).limit(1).build().list();

        if (list.size() > 0) {
            RemoteMessageBean remoteMessageBean = list.get(0);
            List<RemoteMessageBean> list1 = GreenDaoHelper.getInstance().getSeeion().getRemoteMessageBeanDao().queryBuilder().where(
                    Properties.ShowType.eq(roleType),
                    Properties.TargetId.eq(Integer.valueOf(bean.getId())),
                    Properties.Id.gt(remoteMessageBean.getId())).orderDesc(Properties.Id).limit(
                    30).build().list();

            if (list1.size() > 0) {
                Observable.just(list1)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .map(new Func1<List<RemoteMessageBean>, List<Message>>() {
                                @Override
                                public List<Message> call(List<RemoteMessageBean> remoteMessageBeen) {
                                    return MessageFactory.createNotInsertDao(remoteMessageBeen);
                                }
                            })
                          .subscribe(new Observer<List<Message>>() {
                                @Override
                                public void onCompleted() {}

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onNext(List<Message> messages) {
                                    //Collections.reverse(messages);

                                    boolean flag = true;
                                    for (Message message : messages) {
                                        if ("RC:VcMsg".equals(message.getObjectName())) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    MessageManager.getInstance().addMessages(messages);

                                    if (flag) {
                                        Message lastMessage = MessageManager.getInstance().getLastMessage();
                                        Extra   extra       = null;
                                        if (lastMessage != null) {
                                            extra = MessageFactory.getExtrabyMessage(lastMessage);
                                        }
                                        if (extra != null) {
                                            downRmoteHistoryByBalckPlay(extra.getMsgId(), roleType, msgRankType);
                                        }
                                    } else {
                                        Intent intent = new Intent(AudioPlayManager.ACTION_NEXT);
                                        MyApplication.getInstance().sendBroadcast(intent);
                                    }


                                }
                });
                return;
            }
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
        map.put("targetId", bean.getId());
        map.put("roleType", roleType);
        map.put("start", "0");
        map.put("pageSize", "15");
        map.put("msgId", msgId);
        map.put("msgRankType", msgRankType);
        map.put("objectName", "RC:VcMsg");
        LiveApi.getInstance()
               .getMessageRecordList(map)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .map(new Func1<ApiResponseBean<List<RemoteMessageBean>>, List<Message>>() {
                    @Override
                    public List<Message> call(ApiResponseBean<List<RemoteMessageBean>> listApiResponseBean) {
                        return MessageFactory.create(listApiResponseBean.getResult());
                    }
                })
               .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }


                    @Override
                    public void onNext(List<Message> messages) {
                        boolean flag = true;
                        for (Message message : messages) {
                            if ("RC:VcMsg".equals(message.getObjectName())) {
                                flag = false;
                                break;
                            }
                        }
                        MessageManager.getInstance().addMessages(messages);
                        if (messages.size() == 0) {
                            Intent intent = new Intent(AudioPlayManager.ACTION_NOT_MOREVUDIOMESSAGE);
                            MyApplication.getInstance().sendBroadcast(intent);
                            return;
                        }
                        if (flag) {
                            Message lastMessage = MessageManager.getInstance().getLastMessage();
                            Extra   extra       = null;
                            if (lastMessage != null) {
                                extra = MessageFactory.getExtrabyMessage(lastMessage);
                            }
                            if (extra != null) {
                                downRmoteHistoryByBalckPlay(extra.getMsgId(), roleType, msgRankType);
                            }
                        } else {
                            Intent intent = new Intent(AudioPlayManager.ACTION_NEXT);
                            MyApplication.getInstance().sendBroadcast(intent);
                        }
                    }
                });
    }


    private PlayHistory getPlaryHistory(String id) {
        PlayHistory load = GreenDaoHelper.getInstance().getSeeion().getPlayHistoryDao().load(Long.valueOf(id));
        return load;
    }

    public void remoteHistory(int start, int pageSize, String msgType, final CallBack callBack) {
        if (!isCanLoadRemoteMessage && !isRefresh) {
            callBack.onCancel();
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("targetType", Conversation.ConversationType.CHATROOM.getValue() + "");
        map.put("targetId", bean.getId());
        map.put("start", start + "");
        map.put("pageSize", pageSize + "");
        map.put("roleType", "1");

        //在直播室获取聊天消息 msgRankType 传 1 不传msgid
        if(TextUtils.isEmpty(msgType)){
            map.put("msgRankType", "1");

        //在外面获取聊天消息 msgRankType 传 2  同时msgid ＝ 0
        }else{
            map.put("msgRankType", "2");
            map.put("msgId", "0");
            map.put("objectName", msgType);
        }

        requestMessage(map,callBack);

    }

    //从自己服务器拿数据
    private void requestMessage(final HashMap<String, String> map, final CallBack callBack){
        LiveApi.getInstance()
               .getMessageRecordList(map)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .map(new Func1<ApiResponseBean<List<RemoteMessageBean>>, List<Message>>() {
                   @Override
                   public List<Message> call(ApiResponseBean<List<RemoteMessageBean>> listApiResponseBean) {
                       //这里要把顺序倒过来
                       if (listApiResponseBean == null || listApiResponseBean.getResult() == null)
                           return null;

                       //这里如果从后台播放的课程就不需要倒序播放了
                       if(map.get("objectName") == null){
                            Collections.reverse(listApiResponseBean.getResult());
                       }

                       if (listApiResponseBean.getResult().size() > 0) {
                           return MessageFactory.create(listApiResponseBean.getResult());
                       } else {
                           return new ArrayList<Message>();
                       }
                   }
               })
               .subscribe(new Observer<List<Message>>() {
                   @Override
                   public void onCompleted() {}

                   @Override
                   public void onError(Throwable e) {
                       if(callBack == null) return;
                       e.printStackTrace();
                       callBack.onError(e.getMessage());
                   }

                   @Override
                   public void onNext(List<Message> messages) {
                       if(callBack == null) return;
                       callBack.onSuccess(messages);
                   }
               });
    }


    public interface CallBack {
        void onSuccess(List<Message> messages);

        void onError(String message);

        void onCancel();
    }


    public interface LoadSuccessfulListener {
        public void onSuccessful();
    }


    public static ConversationManager getInstance() {
        return SingletonHolder.sInstance;
    }


    static class SingletonHolder {
        static ConversationManager sInstance = new ConversationManager();
    }


    public class ItemScrollBean {
        public boolean flag;
        int position;
        int scrollTop;

        public void setItemPosition(int position, int scrollTop) {
            flag = true;
            this.position = position;
            this.scrollTop = scrollTop;

        }

        public void reinitPosition() {
            flag = false;
            setItemPosition(-1, -1);
        }

        public int getPosition() {
            return position;
        }

        public int getScrollTop() {
            return scrollTop;
        }
    }

}
