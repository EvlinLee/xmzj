//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gxtc.huchuan.im.ui;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.Extra;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OnSendMessageListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.RongIMClient.SendImageMessageCallback;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.ImageMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SendImageManager {
    private static final String TAG = "SendImageManager";
    private ExecutorService executorService;
    private SendImageManager.UploadController uploadController;

    public static SendImageManager getInstance() {
        return SendImageManager.SingletonHolder.sInstance;
    }

    private SendImageManager() {
        this.executorService = this.getExecutorService();
        this.uploadController = new SendImageManager.UploadController();
    }

    public void sendImages(ConversationType conversationType, String targetId, List<Uri> imageList, boolean isFull) {
        RLog.d("SendImageManager", "sendImages " + imageList.size());
        Iterator i = imageList.iterator();

        while (i.hasNext()) {
            Uri image = (Uri) i.next();
            ImageMessage content = ImageMessage.obtain(image, image, isFull);
            OnSendMessageListener listener = RongContext.getInstance().getOnSendMessageListener();
            if (listener != null) {
                Message message = listener.onSend(Message.obtain(targetId, conversationType, content));
                if (message != null) {
                    RongIMClient.getInstance().insertMessage(conversationType, targetId, (String) null, message.getContent(), new ResultCallback<Message>() {
                        public void onSuccess(Message message) {
                            message.setSentStatus(SentStatus.SENDING);
                            RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), SentStatus.SENDING, (ResultCallback) null);
                            RongContext.getInstance().getEventBus().post(message);
                            SendImageManager.this.uploadController.execute(message);
                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
            } else {
                RongIMClient.getInstance().insertMessage(conversationType, targetId, (String) null, content, new ResultCallback<Message>() {
                    public void onSuccess(Message message) {
                        message.setSentStatus(SentStatus.SENDING);
                        RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), SentStatus.SENDING, (ResultCallback) null);
                        RongContext.getInstance().getEventBus().post(message);
                        SendImageManager.this.uploadController.execute(message);
                    }

                    public void onError(ErrorCode e) {
                    }
                });
            }
        }

    }

    public void cancelSendingImages(ConversationType conversationType, String targetId) {
        RLog.d("SendImageManager", "cancelSendingImages");
        if (conversationType != null && targetId != null && this.uploadController != null) {
            this.uploadController.cancel(conversationType, targetId);
        }

    }

    public void cancelSendingImage(ConversationType conversationType, String targetId, int messageId) {
        RLog.d("SendImageManager", "cancelSendingImages");
        if (conversationType != null && targetId != null && this.uploadController != null && messageId > 0) {
            this.uploadController.cancel(conversationType, targetId, messageId);
        }

    }

    public void reset() {
        this.uploadController.reset();
    }

    private ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(1, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), this.threadFactory("Rong SendMediaManager", false));
        }

        return this.executorService;
    }

    private ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            public Thread newThread(@Nullable Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    private class UploadController implements Runnable {
        final List<Message> pendingMessages = new ArrayList();
        Message executingMessage;

        public UploadController() {
        }

        public void execute(Message message) {
            List var2 = this.pendingMessages;
            synchronized (this.pendingMessages) {
                this.pendingMessages.add(message);
                if (this.executingMessage == null) {
                    this.executingMessage = (Message) this.pendingMessages.remove(0);
                    SendImageManager.this.executorService.submit(this);
                }

            }
        }

        public void reset() {
            RLog.w("SendImageManager", "Rest Sending Images.");
            List var1 = this.pendingMessages;
            synchronized (this.pendingMessages) {
                Iterator i = this.pendingMessages.iterator();

                while (true) {
                    if (!i.hasNext()) {
                        this.pendingMessages.clear();
                        break;
                    }
                    if (i.hasNext()) {
                        Message message = (Message) i.next();
                        message.setSentStatus(SentStatus.FAILED);
                        RongContext.getInstance().getEventBus().post(message);
                    }


                }
            }

            if (this.executingMessage != null) {
                this.executingMessage.setSentStatus(SentStatus.FAILED);
                RongContext.getInstance().getEventBus().post(this.executingMessage);
                this.executingMessage = null;
            }

        }

        public void cancel(ConversationType conversationType, String targetId) {
            List var3 = this.pendingMessages;
            synchronized (this.pendingMessages) {
                int count = this.pendingMessages.size();

                for (int i = 0; i < count; ++i) {
                    Message msg = (Message) this.pendingMessages.get(i);
                    if (msg.getConversationType().equals(conversationType) && msg.getTargetId().equals(targetId)) {
                        this.pendingMessages.remove(msg);
                    }
                }

                if (this.pendingMessages.size() == 0) {
                    this.executingMessage = null;
                }

            }
        }

        public void cancel(ConversationType conversationType, String targetId, int messageId) {
            List var4 = this.pendingMessages;
            synchronized (this.pendingMessages) {
                int count = this.pendingMessages.size();

                for (int i = 0; i < count; ++i) {
                    Message msg = (Message) this.pendingMessages.get(i);
                    if (msg.getConversationType().equals(conversationType) && msg.getTargetId().equals(targetId) && msg.getMessageId() == messageId) {
                        this.pendingMessages.remove(msg);
                        break;
                    }
                }

                if (this.pendingMessages.size() == 0) {
                    this.executingMessage = null;
                }

            }
        }

        private void polling() {
            List var1 = this.pendingMessages;
            synchronized (this.pendingMessages) {
                RLog.d("SendImageManager", "polling " + this.pendingMessages.size());
                if (this.pendingMessages.size() > 0) {
                    this.executingMessage = (Message) this.pendingMessages.remove(0);
                    SendImageManager.this.executorService.submit(this);
                } else {
                    this.pendingMessages.clear();
                    this.executingMessage = null;
                }

            }
        }

        public void run() {
//            RongIM.getInstance().sendImageMessage(this.executingMessage, (String)null, (String)null, new RongIMClient.SendImageMessageWithUploadListenerCallback() {
//
//
//                @Override
//                public void onAttached(Message message, RongIMClient.UploadImageStatusListener
//                        uploadImageStatusListener) {
//                    Uri localUri = ((ImageMessage) message.getContent()).getLocalUri();
//                    uploadImage(localUri, uploadImageStatusListener);
//                }
//
//                public void onError(Message message, ErrorCode code) {
//                    UploadController.this.polling();
//                }
//
//                public void onSuccess(Message message) {
//                    ImageMessage content = (ImageMessage) message.getContent();
//                    Log.d("LiveConversationPresent", content.getLocalUri().toString());
//                    Log.d("LiveConversationPresent", content.getThumUri().toString());
//                    Log.d("LiveConversationPresent", content.getRemoteUri().toString());
//                    content.setThumUri(content.getRemoteUri());
//                    content.setLocalUri(content.getRemoteUri());
//                    addMessage(message);
//                    UploadController.this.polling();
//                }
//
//                public void onProgress(Message message, int progress) {
//                }
//            });
            RongIM.getInstance().sendImageMessage(this.executingMessage, (String) null, (String) null, new SendImageMessageCallback() {
                public void onAttached(Message message) {
                }

                public void onError(Message message, ErrorCode code) {
                    UploadController.this.polling();
                }

                public void onSuccess(Message message) {
                    ImageMessage content = (ImageMessage) message.getContent();
                    Log.d("LiveConversationPresent", content.getLocalUri().toString());
                    Log.d("LiveConversationPresent", content.getThumUri().toString());
                    Log.d("LiveConversationPresent", content.getRemoteUri().toString());
                    uploadImage(content.getLocalUri(), message);
                    UploadController.this.polling();
                }

                public void onProgress(Message message, int progress) {
                }
            });
        }

        /**
         * 上传IM图片到自己的服务器
         */
        public void uploadImage(final Uri uri, final Message
                message) {
            String token = UserManager.getInstance().getToken();
            if (token == null) {

                return;
            }
            Log.d(TAG, "uri:" + uri);
            File file = new File(uri.getPath());
            MultipartBody build = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token", token)
                    .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse
                            ("image*//**//*"), file))
                    .build();
            LiveApi.getInstance().uploadIMFile(build)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<List<String>>>(new ApiCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> data) {
                            Log.d(TAG, "onSuccess: 修改IMKIt 上传图片成功");
                            if (data.size() > 0) {
                                String uriString = data.get(0);
                                Uri iamgeUri = Uri.parse(uriString);
                                ImageMessage content = (ImageMessage) message.getContent();
                                content.setLocalUri(uri);
//                                content.setThumUri(uri);
                                content.setRemoteUri(iamgeUri);
                                addMessage(message);
                            } else {

                            }

                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            Log.d(TAG, "上传图片失败" + message);

                        }
                    }));
        }

        private void addMessage(Message message) {
            HashMap<String, String> map = new HashMap<>();
            map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
            map.put("objectName", message.getObjectName());
            map.put("content", new String(message.getContent().encode()));
            map.put("targetType", message.getConversationType().getValue() + "");
            Log.d(TAG, "addMessage: " + new String(message.getContent().encode()));
            map.put("targetId", message.getTargetId());


            map.put("msgId", message.getTargetId() + System.currentTimeMillis());
            Log.d(TAG, map.toString());
            addMessage(map);
        }


        private void addMessage(HashMap<String, String> map) {
            Log.d(TAG, map.toString());
            LiveApi.getInstance().addMessage(map).subscribeOn(Schedulers.io()).subscribe(new
                    ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Log.d(TAG, "onSuccess: 修改IMKIt保存成功");
                }

                @Override
                public void onError(String errorCode, String message) {
                    Log.d("LiveConversationPresent", message);
                    Log.d(TAG, "onError: 修改IMKIt保存失败");
                }
            }));
        }

    }


    static class SingletonHolder {
        static SendImageManager sInstance = new SendImageManager();

        SingletonHolder() {
        }
    }
}
