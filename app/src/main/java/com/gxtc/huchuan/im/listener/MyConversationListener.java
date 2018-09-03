package com.gxtc.huchuan.im.listener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.im.ui.PicturePagerActivity;
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog;
import com.gxtc.huchuan.ui.im.share.ShareMessage;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongMessageItemLongClickActionManager;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.MessageItemLongClickAction;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 此类已弃用
 */
public class MyConversationListener implements RongIM.ConversationBehaviorListener{

    public MyConversationListener(){}

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        PersonalInfoActivity.startActivity(context,userInfo.getUserId());
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {

        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    //这里为了给原来的长按弹窗加 收藏功能
    @Override
    public boolean onMessageLongClick(final Context context, final View view, final Message message) {
        final UIMessage uiMessage                   = UIMessage.obtain(message);
        final List      messageItemLongClickActions = RongMessageItemLongClickActionManager.getInstance().getMessageItemLongClickActions(uiMessage);

        Collections.sort(messageItemLongClickActions, new Comparator<MessageItemLongClickAction>() {
            public int compare(MessageItemLongClickAction lhs, MessageItemLongClickAction rhs) {
                return rhs.priority - lhs.priority;
            }
        });

        final ArrayList<String> titles = new ArrayList<>();
        Iterator                var7   = messageItemLongClickActions.iterator();

        while(var7.hasNext()) {
            MessageItemLongClickAction action = (MessageItemLongClickAction)var7.next();
            titles.add(action.getTitle(context));
        }

        if(message.getContent() instanceof TextMessage
                || message.getContent() instanceof ImageMessage
                || message.getContent() instanceof ShareMessage){
            titles.add("收藏");
        }

        if(message.getContent() instanceof TextMessage
            || message.getContent() instanceof ImageMessage
            || message.getContent() instanceof VoiceMessage){
            titles.add("转发");
        }

        OptionsPopupDialog.newInstance(context, (String[]) titles.toArray(new String[titles.size()])).setOptionsPopupDialogListener(
                new OptionsPopupDialog.OnOptionsItemClickedListener() {
                    @Override
                    public void onOptionsItemClicked(int flag) {
                        if(flag < messageItemLongClickActions.size()){
                            ((MessageItemLongClickAction)messageItemLongClickActions.get(flag)).listener.onMessageItemLongClick(context, uiMessage);

                        }else{
                            //收藏
                            if(flag == messageItemLongClickActions.size() ){
                                collect(context, message);
                            }

                            //转发
                            if(flag == messageItemLongClickActions.size() + 1){
                                //ConversationListActivity.startActivity(context, Constant.SELECT_TYPE_RELAY);
                            }
                        }
                    }
                }).show();
        return true;
    }


    private void collect(Context context, Message message) {
        String bizType = "";
        String content = "";
        String token = UserManager.getInstance().getToken();
        HashMap<String,String> map = new HashMap<>();

        if( RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()) != null){
            map.put("userName", RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()).getName());
            map.put("userPic",RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId()).getPortraitUri().toString());
        }
        map.put("token",token);

        if(message.getContent() instanceof TextMessage){
            bizType = "6";
            content = ((TextMessage)message.getContent()).getContent();
            map.put("content",content);
        }

        if(message.getContent() instanceof ImageMessage){
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            bizType = "7";
            content = imageMessage.getThumUri().toString();
            map.put("content",content);
            map.put("bizType",bizType);
            getBitMap(context,content,map);
        }

        if(message.getContent() instanceof ShareMessage){
            ShareMessage imageMessage = (ShareMessage) message.getContent();
            if(imageMessage.getInfoType() .equals("4")){
                bizType = "8";
                map.put("bizId",imageMessage.getTypeId());
            }
        }

        map.put("bizType",bizType);
        savaCollect(map);
    }

    private void savaCollect(HashMap<String,String> map){
        DealSource mData = new DealRepository();
        mData.saveCollect(map, new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance().getBaseContext(),"收藏失败");
            }
        });
    }

    private void getBitMap(final Context context, final String url, final HashMap<String,String> map) {
        Observable.just(url)
                  .map(new Func1<String, Bitmap>() {
                        @Override
                        public Bitmap call(String s) {
                            try {
                                return  Glide.with(context)
                                        .asBitmap()
                                        .load(s)
                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {}

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil.showShort(context,"收藏失败");
                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            if(bitmap != null){
                                map.put("title",bitmap.getWidth() + "*" + bitmap.getHeight());           //图片宽高，iOS需要
                                savaCollect(map);
                            }
                        }
                    });

    }
}