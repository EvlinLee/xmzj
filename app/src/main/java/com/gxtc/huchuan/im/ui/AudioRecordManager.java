//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gxtc.huchuan.im.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Build.VERSION;
import android.os.Handler.Callback;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.im.provide.VoiceMessageAdapter;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.manager.AudioStateMessage;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.IRongCallback.ISendMessageCallback;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.TypingMessage.TypingMessageManager;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.message.VoiceMessage;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class AudioRecordManager implements Callback {
    private static final String TAG = "AudioRecordManager";
    private int RECORD_INTERVAL;
    private IAudioState mCurAudioState;
    private View mRootView;
    private Context mContext;
    private ConversationType mConversationType;
    private String mTargetId;
    private Handler mHandler;
    private AudioManager mAudioManager;
    private MediaRecorder mMediaRecorder;
    private Uri mAudioPath;
    private long smStartRecTime;
    private OnAudioFocusChangeListener mAfChangeListener;
    private PopupWindow mRecordWindow;
    private ImageView mStateIV;
    private TextView mStateTV;
    private TextView mTimerTV;
    IAudioState idleState;
    IAudioState recordState;
    IAudioState cancelState;
    IAudioState timerState;

    public static AudioRecordManager getInstance() {
        return AudioRecordManager.SingletonHolder.sInstance;
    }

    @TargetApi(21)
    private AudioRecordManager() {
        this.RECORD_INTERVAL = 60;
        this.idleState = new AudioRecordManager.IdleState();
        this.recordState = new AudioRecordManager.RecordState();
        this.cancelState = new AudioRecordManager.CancelState();
        this.timerState = new AudioRecordManager.TimerState();
        RLog.d("AudioRecordManager", "AudioRecordManager");
        if(VERSION.SDK_INT < 21) {
            try {
                TelephonyManager e = (TelephonyManager)RongContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
                e.listen(new PhoneStateListener() {
                    public void onCallStateChanged(int state, String incomingNumber) {
                        switch(state) {
                        case 1:
                            AudioRecordManager.this.sendEmptyMessage(6);
                        case 0:
                        case 2:
                        default:
                            super.onCallStateChanged(state, incomingNumber);
                        }
                    }
                }, 32);
            } catch (SecurityException var2) {
                var2.printStackTrace();
            }
        }

        this.mCurAudioState = this.idleState;
        this.idleState.enter();
    }

    public final boolean handleMessage(Message msg) {
        RLog.i("AudioRecordManager", "handleMessage " + msg.what);
        AudioStateMessage m;
        switch(msg.what) {
        case 2:
            this.sendEmptyMessage(2);
            break;
        case 7:
            m = AudioStateMessage.obtain();
            m.what = msg.what;
            m.obj = msg.obj;
            this.sendMessage(m);
            break;
        case 8:
            m = AudioStateMessage.obtain();
            m.what = 7;
            m.obj = msg.obj;
            this.sendMessage(m);
        }

        return false;
    }

    private void initView(View root) {
        this.mHandler = new Handler(root.getHandler().getLooper(), this);
        LayoutInflater inflater = LayoutInflater.from(root.getContext());
        View view = inflater.inflate(layout.rc_wi_vo_popup, (ViewGroup)null);
        this.mStateIV = (ImageView)view.findViewById(id.rc_audio_state_image);
        this.mStateTV = (TextView)view.findViewById(id.rc_audio_state_text);
        this.mTimerTV = (TextView)view.findViewById(id.rc_audio_timer);
        this.mRecordWindow = new PopupWindow(view, -1, -1);
        this.mRecordWindow.showAtLocation(root, 17, 0, 0);
        this.mRecordWindow.setFocusable(true);
        this.mRecordWindow.setOutsideTouchable(false);
        this.mRecordWindow.setTouchable(false);
    }

    private void setTimeoutView(int counter) {
        if(this.mRecordWindow != null) {
            this.mStateIV.setVisibility(View.GONE);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(string.rc_voice_rec);
            this.mStateTV.setBackgroundResource(drawable.rc_btn_voice);
            this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
            this.mTimerTV.setVisibility(View.VISIBLE);
        }

    }

    private void setRecordingView() {
        RLog.d("AudioRecordManager", "setRecordingView");
        if(this.mRecordWindow != null) {
            this.mStateIV.setVisibility(View.VISIBLE);
            this.mStateIV.setImageResource(drawable.rc_ic_volume_1);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(string.rc_voice_rec);
            this.mStateTV.setBackgroundResource(drawable.rc_btn_voice);
            this.mTimerTV.setVisibility(View.GONE);
        }

    }

    private void setCancelView() {
        RLog.d("AudioRecordManager", "setCancelView");
        if(this.mRecordWindow != null) {
            this.mTimerTV.setVisibility(View.GONE);
            this.mStateIV.setVisibility(View.VISIBLE);
            this.mStateIV.setImageResource(drawable.rc_ic_volume_cancel);
            this.mStateTV.setVisibility(View.VISIBLE);
            this.mStateTV.setText(string.rc_voice_cancel);
            this.mStateTV.setBackgroundResource(drawable.rc_corner_voice_style);
        }

    }

    private void destroyView() {
        RLog.d("AudioRecordManager", "destroyView");
        if(this.mRecordWindow != null) {
            this.mHandler.removeMessages(7);
            this.mHandler.removeMessages(8);
            this.mHandler.removeMessages(2);
            this.mRecordWindow.dismiss();
            this.mRecordWindow = null;
            this.mStateIV = null;
            this.mStateTV = null;
            this.mTimerTV = null;
            this.mHandler = null;
            this.mContext = null;
            this.mRootView = null;
        }

    }

    public void setMaxVoiceDuration(int maxVoiceDuration) {
        this.RECORD_INTERVAL = maxVoiceDuration;
    }

    public int getMaxVoiceDuration() {
        return this.RECORD_INTERVAL;
    }

    public void startRecord(View rootView, ConversationType conversationType, String targetId) {
        this.mRootView = rootView;
        this.mContext = rootView.getContext().getApplicationContext();
        this.mConversationType = conversationType;
        this.mTargetId = targetId;
        this.mAudioManager = (AudioManager)this.mContext.getSystemService(Context.AUDIO_SERVICE);
        if(this.mAfChangeListener != null) {
            this.mAudioManager.abandonAudioFocus(this.mAfChangeListener);
            this.mAfChangeListener = null;
        }

        this.mAfChangeListener = new OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                RLog.d("AudioRecordManager", "OnAudioFocusChangeListener " + focusChange);
                if(focusChange == -1) {
                    AudioRecordManager.this.mAudioManager.abandonAudioFocus(AudioRecordManager.this.mAfChangeListener);
                    AudioRecordManager.this.mAfChangeListener = null;
                    AudioRecordManager.this.sendEmptyMessage(6);
                }

            }
        };
        this.sendEmptyMessage(1);
        if(TypingMessageManager.getInstance().isShowMessageTyping()) {
            RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:VcMsg");
        }

    }

    public void willCancelRecord() {
        this.sendEmptyMessage(3);
    }

    public void continueRecord() {
        this.sendEmptyMessage(4);
    }

    public void stopRecord() {
        this.sendEmptyMessage(5);
    }

    public void destroyRecord() {
        AudioStateMessage msg = new AudioStateMessage();
        msg.obj = Boolean.valueOf(true);
        msg.what = 5;
        this.sendMessage(msg);
    }

    void sendMessage(AudioStateMessage message) {
        this.mCurAudioState.handleMessage(message);
    }

    void sendEmptyMessage(int event) {
        AudioStateMessage message = AudioStateMessage.obtain();
        message.what = event;
        this.mCurAudioState.handleMessage(message);
    }

    private void startRec() {
        RLog.d("AudioRecordManager", "startRec");

        try {
            this.muteAudioFocus(this.mAudioManager, true);
            this.mAudioManager.setMode(0);
            this.mMediaRecorder = new MediaRecorder();

            try {
                Resources e = this.mContext.getResources();
                int bps = e.getInteger(e.getIdentifier("rc_audio_encoding_bit_rate", "integer", this.mContext.getPackageName()));
                this.mMediaRecorder.setAudioSamplingRate(8000);
                this.mMediaRecorder.setAudioEncodingBitRate(bps);
            } catch (NotFoundException var3) {
                var3.printStackTrace();
            }

            this.mMediaRecorder.setAudioChannels(1);
            this.mMediaRecorder.setAudioSource(1);
            this.mMediaRecorder.setOutputFormat(3);
            this.mMediaRecorder.setAudioEncoder(1);
            this.mAudioPath = Uri.fromFile(new File(this.mContext.getCacheDir(), System.currentTimeMillis() + "temp.voice"));
            this.mMediaRecorder.setOutputFile(this.mAudioPath.getPath());
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
            Message e1 = Message.obtain();
            e1.what = 7;
            e1.obj = Integer.valueOf(10);
            this.mHandler.sendMessageDelayed(e1, (long)(this.RECORD_INTERVAL * 1000 - 10000));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    private boolean checkAudioTimeLength() {
        long delta = SystemClock.elapsedRealtime() - this.smStartRecTime;
        return delta < 1000L;
    }

    private void stopRec() {
        RLog.d("AudioRecordManager", "stopRec");

        try {
            this.muteAudioFocus(this.mAudioManager, false);
            if(this.mMediaRecorder != null) {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
                this.mMediaRecorder = null;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    private void deleteAudioFile() {
        RLog.d("AudioRecordManager", "deleteAudioFile");
        if(this.mAudioPath != null) {
            File file = new File(this.mAudioPath.getPath());
            if(file.exists()) {
                file.delete();
            }
        }

    }

    private void sendAudioFile() {
        RLog.d("AudioRecordManager", "sendAudioFile path = " + this.mAudioPath);
        if(this.mAudioPath != null) {
            File file = new File(this.mAudioPath.getPath());
            if(!file.exists() || file.length() == 0L) {
                RLog.e("AudioRecordManager", "sendAudioFile fail cause of file length 0 or audio permission denied");
                return;
            }

            int duration = (int)(SystemClock.elapsedRealtime() - this.smStartRecTime) / 1000;
            VoiceMessage voiceMessage = VoiceMessage.obtain(this.mAudioPath, duration);
            RongIM.getInstance().sendMessage(io.rong.imlib.model.Message.obtain(this.mTargetId, this.mConversationType, voiceMessage), (String)null, (String)null, new ISendMessageCallback() {
                public void onAttached(io.rong.imlib.model.Message message) {
                }

                public void onSuccess(io.rong.imlib.model.Message message) {
                    VoiceMessage content = (VoiceMessage) message.getContent();
                    Log.d("LiveConversationPresent", "content.getUri():" + content.getUri());
                    if (content.getBase64() != null) {
                        Log.d("LiveConversationPresent", content.getBase64());
                    }
                    uploadVoiceMessage(content);
                }

                public void onError(io.rong.imlib.model.Message message, ErrorCode errorCode) {
                }
            });
        }

    }


    private void uploadVoiceMessage(final VoiceMessage message) {
        File file = new File(message.getUri().getPath());


        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", com.gxtc.huchuan.data.UserManager.getInstance()
                        .getToken())
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse
                        ("audio*//**/*"), file)).build();
        LiveApi.getInstance().uploadIMFile(requestBody).subscribeOn(Schedulers.io()).map(new Func1<ApiResponseBean<List<String>>, io.rong.imlib.model.Message>() {
            @Override
            public io.rong.imlib.model.Message call(ApiResponseBean<List<String>> uploadFileBeanApiResponseBean) {
                List<String> datas = uploadFileBeanApiResponseBean.getResult();
                //                Log.d(TAG, result.getFileUrl().toString());
                VoiceMessageAdapter voiceMessageAdapter = null;
                for (String data : datas) {
                    voiceMessageAdapter = new VoiceMessageAdapter(message, data);
                }
                //                VoiceMessageAdapter voiceMessageAdapter = new
                // VoiceMessageAdapter(message, result
                //                        .getFileUrl().toString());


                io.rong.imlib.model.Message mObtain = io.rong.imlib.model.Message.obtain(mTargetId, mConversationType,
                        voiceMessageAdapter);
                mObtain.setObjectName(VoiceMessageAdapter.class.getAnnotation(MessageTag.class)
                        .value());
                mObtain.setExtra(message.getExtra());
                return mObtain;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<io.rong.imlib.model.Message>() {
            @Override
            public void call(io.rong.imlib.model.Message message) {
                Log.d(TAG, "call: " + new String(message.getContent().encode()));
                                addMessage(message);
            }
        });
    }


    private void addMessage(io.rong.imlib.model.Message message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", com.gxtc.huchuan.data.UserManager.getInstance().getToken());
        map.put("objectName", message.getObjectName());
        map.put("content", new String(message.getContent().encode()));
        map.put("targetType", message.getConversationType().getValue() + "");
        Log.d(TAG, "addMessage: " + new String(message.getContent().encode()));
        map.put("targetId", message.getTargetId());


        map.put("msgId",message.getTargetId()+System.currentTimeMillis());
        Log.d(TAG, map.toString());
        addMessage(map);
    }


    private void addMessage(HashMap<String, String> map) {
        LiveApi.getInstance().addMessage(map).subscribeOn(Schedulers.io()).subscribe(new
                ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "onSuccess: 修改ImKit 保存语音成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                Log.d("LiveConversationPresent", message);
                Log.d(TAG, "onError: 修改ImKit 保存语音失败");
            }
        }));
    }

    private void audioDBChanged() {
        if(this.mMediaRecorder != null) {
            int db = this.mMediaRecorder.getMaxAmplitude() / 600;
            switch(db / 5) {
            case 0:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_1);
                break;
            case 1:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_2);
                break;
            case 2:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_3);
                break;
            case 3:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_4);
                break;
            case 4:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_5);
                break;
            case 5:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_6);
                break;
            case 6:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_7);
                break;
            default:
                this.mStateIV.setImageResource(drawable.rc_ic_volume_8);
            }
        }

    }

    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if(VERSION.SDK_INT < 8) {
            RLog.d("AudioRecordManager", "muteAudioFocus Android 2.1 and below can not stop music");
        } else {
            if(bMute) {
                audioManager.requestAudioFocus(this.mAfChangeListener, 3, 2);
            } else {
                audioManager.abandonAudioFocus(this.mAfChangeListener);
                this.mAfChangeListener = null;
            }

        }
    }

    class TimerState extends IAudioState {
        TimerState() {
        }

        void handleMessage(AudioStateMessage msg) {
            RLog.d("AudioRecordManager", this.getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch(msg.what) {
            case 3:
                AudioRecordManager.this.setCancelView();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.cancelState;
            case 4:
            default:
                break;
            case 5:
                AudioRecordManager.this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        AudioRecordManager.this.stopRec();
                        AudioRecordManager.this.sendAudioFile();
                        AudioRecordManager.this.destroyView();
                    }
                }, 500L);
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                AudioRecordManager.this.idleState.enter();
                break;
            case 6:
                AudioRecordManager.this.stopRec();
                AudioRecordManager.this.destroyView();
                AudioRecordManager.this.deleteAudioFile();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                AudioRecordManager.this.idleState.enter();
                break;
            case 7:
                int counter = ((Integer)msg.obj).intValue();
                if(counter > 0) {
                    Message message = Message.obtain();
                    message.what = 8;
                    message.obj = Integer.valueOf(counter - 1);
                    AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
                    AudioRecordManager.this.setTimeoutView(counter);
                } else {
                    AudioRecordManager.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            AudioRecordManager.this.stopRec();
                            AudioRecordManager.this.sendAudioFile();
                            AudioRecordManager.this.destroyView();
                        }
                    }, 500L);
                    AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                }
            }

        }
    }

    class CancelState extends IAudioState {
        CancelState() {
        }

        void handleMessage(AudioStateMessage msg) {
            RLog.d("AudioRecordManager", this.getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch(msg.what) {
            case 1:
            case 2:
            case 3:
            default:
                break;
            case 4:
                AudioRecordManager.this.setRecordingView();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.recordState;
                AudioRecordManager.this.sendEmptyMessage(2);
                break;
            case 5:
            case 6:
                AudioRecordManager.this.stopRec();
                AudioRecordManager.this.destroyView();
                AudioRecordManager.this.deleteAudioFile();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                AudioRecordManager.this.idleState.enter();
                break;
            case 7:
                int counter = ((Integer)msg.obj).intValue();
                if(counter > 0) {
                    Message message = Message.obtain();
                    message.what = 8;
                    message.obj = Integer.valueOf(counter - 1);
                    AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
                } else {
                    AudioRecordManager.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            AudioRecordManager.this.stopRec();
                            AudioRecordManager.this.sendAudioFile();
                            AudioRecordManager.this.destroyView();
                        }
                    }, 500L);
                    AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                    AudioRecordManager.this.idleState.enter();
                }
            }

        }
    }

    class RecordState extends IAudioState {
        RecordState() {
        }

        void handleMessage(AudioStateMessage msg) {
            RLog.d("AudioRecordManager", this.getClass().getSimpleName() + " handleMessage : " + msg.what);
            switch(msg.what) {
            case 2:
                AudioRecordManager.this.audioDBChanged();
                AudioRecordManager.this.mHandler.sendEmptyMessageDelayed(2, 150L);
                break;
            case 3:
                AudioRecordManager.this.setCancelView();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.cancelState;
            case 4:
            default:
                break;
            case 5:
                final boolean checked = AudioRecordManager.this.checkAudioTimeLength();
                boolean activityFinished = false;
                if(msg.obj != null) {
                    activityFinished = ((Boolean)msg.obj).booleanValue();
                }

                if(checked && !activityFinished) {
                    AudioRecordManager.this.mStateIV.setImageResource(drawable.rc_ic_volume_wraning);
                    AudioRecordManager.this.mStateTV.setText(string.rc_voice_short);
                    AudioRecordManager.this.mHandler.removeMessages(2);
                }

                if(!activityFinished && AudioRecordManager.this.mHandler != null) {
                    AudioRecordManager.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            AudioRecordManager.this.stopRec();
                            if(!checked) {
                                AudioRecordManager.this.sendAudioFile();
                            }

                            AudioRecordManager.this.destroyView();
                            AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                        }
                    }, 500L);
                } else {
                    AudioRecordManager.this.stopRec();
                    if(!checked && activityFinished) {
                        AudioRecordManager.this.sendAudioFile();
                    }

                    AudioRecordManager.this.destroyView();
                    AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                }
                break;
            case 6:
                AudioRecordManager.this.stopRec();
                AudioRecordManager.this.destroyView();
                AudioRecordManager.this.deleteAudioFile();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                AudioRecordManager.this.idleState.enter();
                break;
            case 7:
                int counter = ((Integer)msg.obj).intValue();
                AudioRecordManager.this.setTimeoutView(counter);
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.timerState;
                if(counter > 0) {
                    Message message = Message.obtain();
                    message.what = 8;
                    message.obj = Integer.valueOf(counter - 1);
                    AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
                } else {
                    AudioRecordManager.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            AudioRecordManager.this.stopRec();
                            AudioRecordManager.this.sendAudioFile();
                            AudioRecordManager.this.destroyView();
                        }
                    }, 500L);
                    AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.idleState;
                }
            }

        }
    }

    class IdleState extends IAudioState {
        public IdleState() {
            RLog.d("AudioRecordManager", "IdleState");
        }

        void enter() {
            super.enter();
            if(AudioRecordManager.this.mHandler != null) {
                AudioRecordManager.this.mHandler.removeMessages(7);
                AudioRecordManager.this.mHandler.removeMessages(8);
                AudioRecordManager.this.mHandler.removeMessages(2);
            }

        }

        void handleMessage(AudioStateMessage msg) {
            RLog.d("AudioRecordManager", "IdleState handleMessage : " + msg.what);
            switch(msg.what) {
            case 1:
                AudioRecordManager.this.initView(AudioRecordManager.this.mRootView);
                AudioRecordManager.this.setRecordingView();
                AudioRecordManager.this.startRec();
                AudioRecordManager.this.smStartRecTime = SystemClock.elapsedRealtime();
                AudioRecordManager.this.mCurAudioState = AudioRecordManager.this.recordState;
                AudioRecordManager.this.sendEmptyMessage(2);
            default:
            }
        }
    }
    public abstract class IAudioState {
        public IAudioState() {
        }

        void enter() {
        }

        abstract void handleMessage(AudioStateMessage var1);
    }

    static class SingletonHolder {
        static AudioRecordManager sInstance = new AudioRecordManager();

        SingletonHolder() {
        }
    }
}
