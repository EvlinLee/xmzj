package com.gxtc.huchuan.im.manager;

import android.util.Log;

import com.gxtc.huchuan.data.*;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;

import java.util.HashMap;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/4/21.
 * 暂定  专门用来上传消息到我们服务器
 */

public class MessageUploadMyService {
    private static final String TAG = "MessageUploadMyService";


    public void addMessage(Message message,CallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("objectName", message.getObjectName());
        map.put("content", new String(message.getContent().encode()));
        map.put("targetType", message.getConversationType().getValue() + "");
        map.put("targetId", message.getTargetId());
        Extra extra = new Extra(message.getExtra());
        map.put("showType","2".equals(extra.getSenderType())?"2":"1");
        map.put("msgId", extra.getMsgId());
        addMessage(map,callBack);
    }

    public static void removeMessage(Message message, final CallBack callBack){
        HashMap<String, String> map = new HashMap<>();
        Extra                   extra = MessageFactory.getExtrabyMessage(message);
        if (extra!=null) {

            LiveApi.getInstance().delMessage(UserManager.getInstance().getToken(),extra.getMsgId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new ApiObserver<ApiResponseBean<Void>>(
                    new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if (callBack!=null){
                                callBack.onSuccess();
                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if (callBack != null) {
                                callBack.onError(message);
                            }
                        }
                    }));
        }
    }


    private void addMessage(HashMap<String, String> map, final CallBack callBack) {
        LiveApi.getInstance().addMessage(map).subscribeOn(Schedulers.io()).subscribe(new
                ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
            @Override
            public void onSuccess(Object data) {
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if (callBack != null) {
                    callBack.onError(message);
                }
            }
        }));
    }

    public interface CallBack {
        void onSuccess();

        void onError(String message);

        void onCancel();
    }


    public static MessageUploadMyService getInstance() {
        return MessageUploadMyService.SingletonHolder.sInstance;
    }


    static class SingletonHolder {
        static MessageUploadMyService sInstance = new MessageUploadMyService();
    }
}
