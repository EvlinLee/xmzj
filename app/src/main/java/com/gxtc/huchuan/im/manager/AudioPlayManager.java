package com.gxtc.huchuan.im.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.bean.dao.PlayHistory;
import com.gxtc.huchuan.im.event.PlayEvent;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imlib.model.Message;
import io.rong.message.VoiceMessage;


/**
 * Created by Gubr on 2017/2/24.
 */

public class AudioPlayManager implements SensorEventListener {

    private static final String                   TAG        = "AudioPlayManager";
    private static final String                   TAG2       = "AudioPlayManager2";
    public static final String ACTION_NOT_MOREVUDIOMESSAGE="com.gxtc.huchuan.action_not_morevudiomessage";
    //    private List<Message> datas = new ArrayList<>();
    private              List<IAudioPlayListener> mListeners = new ArrayList<>();
    private Handler mHandler;
    private VoiceTimeBean mVoiceTimeBean = null;

    public static final String ACTION_PAUSE               = "com.gxtc.huchuan.pause";
    public static final String ACTION_PLAY                = "com.gxtc.huchuan.play";
    public static final String ACTION_PREV                = "com.gxtc.huchuan.prev";
    public static final String ACTION_NEXT                = "com.gxtc.huchuan.next";
    public static final String ACTION_NOT_NEXT            = "com.gxtc.huchuan.notnext";
    public static final String ACTION_NOTIFICATION_DELETE = "com.gxtc.huchuan.notification_delete";
    public static final String ACTION_STOP_CASTING        = "com.gxtc.huchuan.stop_cast";


    public static final int PLAY_START    = 1;
    public static final int PLAY_STOP     = 1 << 1;
    public static final int PLAY_INTG     = 1 << 2;
    public static final int PLAY_COMPLETE = 1 << 3;
    public static final int PLAY_PAUSE    = 1 << 4;

    private PendingIntent mPauseIntent;
    private PendingIntent mPlayIntent;
    private PendingIntent mNotificationDelete;
    private PendingIntent mNextIntent;


    private boolean isPlaying;
    private boolean registernotifaction;

    private boolean isAutoPlay;
    private int     mTopicId;


    private Context mContext;

    private MediaPlayer                             mMediaPlayer;
    private Uri                                     playingUri;
    private Sensor                                  mSensor;
    private SensorManager                           mSensorManager;
    private AudioManager                            mAudioManager;
    private PowerManager                            mPowerManager;
    private PowerManager.WakeLock                   mWakeLock;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private NotificationManagerCompat               mNotificationManager;


    private final Timer mTimer = new Timer();
    private TimerTask mTimerTask;

    private void sendCustomerNotification(int command) {
        if (mNotificationManager == null) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(ConversationManager.getInstance().getChatInfosBean().getChatRoomName());
        builder.setContentText("自定义通知栏示例");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_template_customer);
        remoteViews.setTextViewText(R.id.title, ConversationManager.getInstance().getChatInfosBean().getChatRoomName());
        remoteViews.setTextViewText(R.id.text, ConversationManager.getInstance().getChatInfosBean().getSubtitle());
        initPendingIntent();

        if (command == PLAY_START) {
            remoteViews.setImageViewResource(R.id.btn1, R.drawable.ic_pause_white);
            remoteViews.setOnClickPendingIntent(R.id.btn1, mPauseIntent);

        } else if (command == PLAY_PAUSE || command == PLAY_STOP) {
            remoteViews.setImageViewResource(R.id.btn1, R.drawable.ic_play_arrow_white_18dp);
            remoteViews.setOnClickPendingIntent(R.id.btn1, mPlayIntent);
        }

        remoteViews.setOnClickPendingIntent(R.id.btn2, mNextIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn3, mNotificationDelete);

        builder.setContent(remoteViews);
        builder.setDeleteIntent(mNotificationDelete);
        Notification notification = builder.build();
        mNotificationManager.notify(8, notification);
        if (notification != null && !registernotifaction) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PREV);
            filter.addAction(ACTION_NOT_NEXT);
            filter.addAction(ACTION_STOP_CASTING);
            filter.addAction(ACTION_NOT_MOREVUDIOMESSAGE);
            filter.addAction(ACTION_NOTIFICATION_DELETE);
            mContext.registerReceiver(new MyBroadcastReceiver(this), filter);
            registernotifaction = true;
        }

    }


    private void initPendingIntent() {
        Intent Intent1 = new Intent(ACTION_PLAY);
        mPlayIntent = PendingIntent.getBroadcast(mContext, 5, Intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent Intent2 = new Intent(ACTION_NEXT);
        mNextIntent = PendingIntent.getBroadcast(mContext, 6, Intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent Intent3 = new Intent(ACTION_PAUSE);
        mPauseIntent = PendingIntent.getBroadcast(mContext, 7, Intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent Intent4 = new Intent(ACTION_NOTIFICATION_DELETE);
        mNotificationDelete = PendingIntent.getBroadcast(mContext, 8, Intent4, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static class MyBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<AudioPlayManager> mWeakReference ;
        private AudioPlayManager mAudioPlayManager;
        public MyBroadcastReceiver(AudioPlayManager mAudioPlayManager) {
            mWeakReference = new WeakReference<>(mAudioPlayManager);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mAudioPlayManager = mWeakReference.get();
            if(mAudioPlayManager == null) return;
            final String action = intent.getAction();
            switch (action) {
                case ACTION_PAUSE:
                    mAudioPlayManager.pausePlay();
                    break;

                case ACTION_PLAY:
                    mAudioPlayManager.startPlay( mAudioPlayManager.mContext,  mAudioPlayManager.puseUri);
                    break;

                case ACTION_NEXT:
                    if ( mAudioPlayManager.getPlayingUri() == null) {
                        mAudioPlayManager.startPlay( mAudioPlayManager.mContext,  mAudioPlayManager.puseUri);
                    } else {
                        mAudioPlayManager.playNext();
                    }
                    break;

                case ACTION_PREV:
                    break;

                case ACTION_NOTIFICATION_DELETE:
                    mAudioPlayManager.mNotificationManager.cancel(8);
                    mAudioPlayManager.mContext.unregisterReceiver(this);
                    mAudioPlayManager.registernotifaction = false;
                    break;

                case ACTION_NOT_NEXT:
                    Message lastMessage = MessageManager.getInstance().getLastMessage();
                    Extra  extra       = null;
                    if (lastMessage != null) {
                        extra = MessageFactory.getExtrabyMessage(lastMessage);
                    }
                    if (extra!=null){
                        ConversationManager.getInstance().downRmoteHistoryByBalckPlay(extra.getMsgId(),"1", "2");
                        return;
                    }
                    break;

                case ACTION_NOT_MOREVUDIOMESSAGE:
                    EventBusUtil.post(new PlayEvent( mAudioPlayManager.currBean ,PlayEvent.PAUSE_STATUS));
                    Toast.makeText(MyApplication.getInstance(), "没有更多语音了", Toast.LENGTH_SHORT).show();
                    mAudioPlayManager.reset();
                    break;

                default:
            }
        }
    }


    /**
     * 当前播放器是否在播放
     *
     * @return
     */
    public boolean isAudioPlaying() {
        return isPlaying;
    }

    static class  MyHandler extends Handler{
        private WeakReference<AudioPlayManager> mWeakReference ;
        private AudioPlayManager mAudioPlayManager;

        public MyHandler(AudioPlayManager mAudioPlayManager) {
            mWeakReference = new WeakReference<>(mAudioPlayManager);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            mAudioPlayManager = mWeakReference.get();
            if(mAudioPlayManager == null) return;
            switch (msg.what) {
                case PLAY_START:
                    if (mAudioPlayManager.mMediaPlayer != null && mAudioPlayManager.mVoiceTimeBean != null) {
                        mAudioPlayManager.isPlaying = true;
                        mAudioPlayManager.mVoiceTimeBean.setPlaying(true);
                        mAudioPlayManager.mVoiceTimeBean.setPlayed(false);
                        mAudioPlayManager.mVoiceTimeBean.setDuration(mAudioPlayManager.mMediaPlayer.getDuration());
                        mAudioPlayManager.mVoiceTimeBean.setCurrentposition(mAudioPlayManager.mMediaPlayer.getCurrentPosition());
                        mAudioPlayManager.savePrlayHistory(mAudioPlayManager.mVoiceTimeBean.getVoiceMessage());
                        mAudioPlayManager.sendCustomerNotification(PLAY_START);

                    }
                    break;

                case PLAY_STOP:
                    mAudioPlayManager.isPlaying = false;
                    if (mAudioPlayManager.mVoiceTimeBean != null) {
                        mAudioPlayManager.mVoiceTimeBean.setPlaying(false);
                        mAudioPlayManager.sendCustomerNotification(PLAY_STOP);
                    }
                    break;

                case PLAY_INTG:
                    if (mAudioPlayManager.mMediaPlayer != null && mAudioPlayManager.mVoiceTimeBean != null) {
                        mAudioPlayManager.mVoiceTimeBean.setCurrentposition(mAudioPlayManager.mMediaPlayer.getCurrentPosition());
                    }
                    break;

                case PLAY_PAUSE:
                    mAudioPlayManager.isPlaying = false;
                    if (mAudioPlayManager.mVoiceTimeBean != null) {
                        mAudioPlayManager.mVoiceTimeBean.setPlaying(false);
                        mAudioPlayManager.sendCustomerNotification(PLAY_PAUSE);
                    }
                    break;

                case PLAY_COMPLETE:
                    if (mAudioPlayManager.mVoiceTimeBean != null) {
                        mAudioPlayManager.mVoiceTimeBean.setCurrentposition(mAudioPlayManager.mVoiceTimeBean.getDuration());
                        mAudioPlayManager.mVoiceTimeBean.setPlaying(false);
                        mAudioPlayManager.mVoiceTimeBean.setPlayed(true);
                        mAudioPlayManager.sendCustomerNotification(PLAY_STOP);

                        for (IAudioPlayListener listener : mAudioPlayManager.mListeners) {
                            listener.PlayEvent(mAudioPlayManager.mVoiceTimeBean);
                        }
                    }
                    if (!mAudioPlayManager.playNext()) {
//                            mContext = null;
//                            reset();
                    }
                    return;
            }

            for (IAudioPlayListener listener : mAudioPlayManager.mListeners) {
                listener.PlayEvent(mAudioPlayManager.mVoiceTimeBean);
            }

        }
    }
    private AudioPlayManager() {
        mHandler = new MyHandler(this);
        isAutoPlay = SpUtil.getBoolean(MyApplication.getInstance(), Constant.AUTO_PLAY_NEXT_VOICEMASSAGE, true);
    }

    public static AudioPlayManager getInstance() {
        return SingletonHolder.sInstance;
    }

    //    public void addPlay(Message item) {
    //        datas.add(item);
    ////        }
    //    }

    //    public void removePlay(Message item) {
    ////        if (datas.contains(item)){
    //        datas.remove(item);
    ////        }
    //    }

    public void addPlayListener(IAudioPlayListener audioPlayListener) {
        mListeners.add(audioPlayListener);

    }

    public void removePlayListener(IAudioPlayListener audioPlayListener) {
        mListeners.remove(audioPlayListener);
    }


    private static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();
    }


    /**
     * 判断当前播放uri是哪个
     *
     * @param uri
     * @return
     */
    public boolean isUriPlaying(Uri uri) {
        return mMediaPlayer != null && uri.getPath().equals(playingUri.getPath());
    }


    public void seekTo(int position) {
        //        mTimer.cancel();
        if (mMediaPlayer != null && mMediaPlayer.getDuration() >= position) {
            mMediaPlayer.seekTo(position);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }


    public void setTopicId(int topicId) {
        mTopicId = topicId;
    }

    //    public void setTopic


    public Uri getPlayingUri() {
        return playingUri;
    }


    public void startPlay(Context context, int position) {
        VoiceMessage voiceMessage = MessageManager.getInstance().getVoiceMessage(position);
        startPlay(context, voiceMessage.getUri());
    }

    private ChatInfosBean currBean;
    public void startPlay(ChatInfosBean inBean) {
        currBean = inBean;
        ChatInfosBean chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
        if(chatInfosBean != null && currBean.getId().equals(chatInfosBean.getId())){
            ConversationManager.getInstance().StartPlayVoiceMessage();

        }else{
            ConversationManager.getInstance().init(currBean);
            ConversationManager.getInstance().getremoteHistory(
                    15,
                    "RC:VcMsg",
                    new ConversationManager.CallBack() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            if(messages != null && messages.size() > 0){
                                /**
                                 * isBackLoadMsg
                                 * 这个表示是判断是否是从后台播放语音的时候  从服务器拿数据的标志
                                 * 因为后台播放语音的时候拿的数据 只拿 语音消息 有可能语音消息之前参杂有图片消息文字消息
                                 * 这时候再进入课堂页面听课的话  就拿不到前面的消息了 ，这时候需要根据这个标志去后台从新请求课堂页面全部的消息回来
                                 */
                                MessageManager.getInstance().addMessages(messages);
                                ConversationManager.getInstance().StartPlayVoiceMessage();
                            }else{
                                EventBusUtil.post(new PlayEvent(currBean,PlayEvent.PAUSE_STATUS));
                                ToastUtil.showShort(MyApplication.getInstance(),"该课堂暂无语音消息");
                            }
                        }

                        @Override
                        public void onError(String message) {}

                        @Override
                        public void onCancel() {}
                    });
        }
    }


    public void startPlay(Context context, final Uri audioUri) {
        if (mNotificationManager == null)
            mNotificationManager = NotificationManagerCompat.from(context);

        if (context != null && playingUri != null && playingUri == audioUri && mMediaPlayer != null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mHandler != null && mMediaPlayer != null && isPlaying) {
                        mHandler.sendEmptyMessage(PLAY_INTG);
                    }
                }
            };
            mMediaPlayer.start();
            return;
        }


        if (context != null && audioUri != null) {
            mContext = context;
            timerRest();
            if (mVoiceTimeBean != null) {
                mVoiceTimeBean.setPlaying(false);

                EventBusUtil.post(mVoiceTimeBean);
            }


            mHandler.sendEmptyMessage(PLAY_STOP);

            this.resetMediaPlayer();
            this.afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (mAudioManager != null && focusChange == -1) {
                        mAudioManager.abandonAudioFocus(afChangeListener);
                        afChangeListener = null;
                        resetMediaPlayer();
                    }
                }
            };

            try {
                this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (!mAudioManager.isWiredHeadsetOn()) {//如果没有用耳机 就监听距离  自动转成听筒模式
                    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                    mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);//距离传感器
                    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                this.muteAudioFocus(this.mAudioManager, true);
                //                this.mPlayListener = playListener;
                this.playingUri = audioUri;
                this.puseUri = audioUri;
                this.mVoiceTimeBean = MessageManager.getInstance().getVoiceTimeBean(playingUri);
                if (mVoiceTimeBean == null) return;
                this.mMediaPlayer = new MediaPlayer();
                this.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mHandler.sendEmptyMessage(PLAY_COMPLETE);
                    }
                });
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        reset();
                        return true;
                    }
                });
                if (audioUri.getScheme().equals("http")) {
                    this.mMediaPlayer.setDataSource(audioUri.toString());
                } else {
                    this.mMediaPlayer.setDataSource(mContext, audioUri);
                }

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        if (!mVoiceTimeBean.isPlayed() && mVoiceTimeBean.getDuration() > 0 && mVoiceTimeBean.getCurrentposition() > 0) {
                            seekTo(mVoiceTimeBean.getCurrentposition());
                        }
                        mp.start();
                        EventBusUtil.post(new PlayEvent(ConversationManager.getInstance().getChatInfosBean(), PlayEvent.START_STATUS));
                        mHandler.sendEmptyMessage(PLAY_START);
                        mTimer.schedule(mTimerTask, 0, 300);


                    }
                });
                mMediaPlayer.prepareAsync();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mHandler != null && mMediaPlayer != null && isPlaying) {
                            mHandler.sendEmptyMessage(PLAY_INTG);
                        }
                    }
                };

            } catch (IOException e) {
                e.printStackTrace();

                if (mHandler != null) {
                    mHandler.sendEmptyMessage(PLAY_STOP);
                }

                reset();
            }
        }

    }


    private Uri puseUri;

    public void pausePlay() {
        puseUri = playingUri;
        mHandler.sendEmptyMessage(PLAY_PAUSE);
        EventBusUtil.post(new PlayEvent(ConversationManager.getInstance().getChatInfosBean(), PlayEvent.PAUSE_STATUS));
        resetMediaPlayer();
        resetAudioPlayManager();
        timerRest();
    }


    public void stopPlay() {
        mHandler.sendEmptyMessage(PLAY_STOP);
        EventBusUtil.post(new PlayEvent(ConversationManager.getInstance().getChatInfosBean(), PlayEvent.PAUSE_STATUS));
        reset();
    }


    public void reinit() {
        reset();
        removeAllListener();
        //        datas.clear();
    }

    private void reset() {
        EventBusUtil.post(mVoiceTimeBean);
        resetMediaPlayer();
        resetAudioPlayManager();
        timerRest();
    }

    public void timerRest() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    private void resetMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void resetAudioPlayManager() {
        if (mAudioManager != null) {
            muteAudioFocus(mAudioManager, false);
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        this.mSensorManager = null;
        this.mSensor = null;
        this.mPowerManager = null;
        mAudioManager = null;
        mWakeLock = null;
        //        mPlayListener = null;
        playingUri = null;
        isPlaying = false;
        //        mVoiceTimeBean = null;
    }


    public void removeAllListener() {
        mListeners.clear();
    }


    /**
     * @return true  表示播放下一条成功  false 表示已经没有下一条了。
     */
    public boolean playNext() {
        Boolean aBoolean = SpUtil.getBoolean(MyApplication.getInstance(), Constant.AUTO_PLAY_NEXT_VOICEMASSAGE, true);

        VoiceTimeBean nextVoicMessage = MessageManager.getInstance().getNextVoicMessage(playingUri);
        if (nextVoicMessage != null && aBoolean) {
            mVoiceTimeBean = nextVoicMessage;
            startPlay(mContext, mVoiceTimeBean.getUri());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 重新播放
     */
    private void replay() {
        try {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.setAudioStreamType(AudioManager.MODE_NORMAL);
            this.mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.setDataSource(mContext, playingUri);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < 8) {

        } else {
            if (bMute) {
                audioManager.requestAudioFocus(this.afChangeListener, 3,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            } else {
                audioManager.abandonAudioFocus(this.afChangeListener);
                this.afChangeListener = null;
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (this.mSensor != null && this.mMediaPlayer != null) {
            if (this.mMediaPlayer.isPlaying()) {
                if ((double) range > 0.0D) {
                    if (!isScreen) return;
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    mAudioManager.setSpeakerphoneOn(true);
                    final int position = this.mMediaPlayer.getCurrentPosition();
                    try {
                        this.mMediaPlayer.reset();
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                        mMediaPlayer.setDataSource(mContext, playingUri);
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.seekTo(position);
                            }
                        });
                        mMediaPlayer.setOnSeekCompleteListener(
                                new MediaPlayer.OnSeekCompleteListener() {
                                    @Override
                                    public void onSeekComplete(MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                        mMediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.setScreenOn();

                } else {
                    this.setScreenOff();
                    if (Build.VERSION.SDK_INT >= 11) {
                        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    } else {
                        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    }
                    mAudioManager.setSpeakerphoneOn(false);
                    replay();
                }
            } else if ((double) range > 0.0D) {
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(true);
                setScreenOn();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isScreen;

    /**
     * 保持屏幕长亮
     */
    private void setScreenOff() {
        isScreen = true;
        if (this.mWakeLock == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                mWakeLock = this.mPowerManager.newWakeLock(
                        PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            }
            if (this.mWakeLock != null) {
                mWakeLock.acquire();
            }
        }
    }

    /**
     * 取消屏幕长亮
     */
    private void setScreenOn() {
        isScreen = false;
        if (this.mWakeLock != null) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    private int getCurrentPosition() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;

    }


    public void savePrlayHistory(VoiceMessage message) {
        ChatInfosBean chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
        String        extraStr      = message.getExtra();
        Log.d(TAG, extraStr);
        Extra extra = null;
        if (extraStr != null) {
            extra = new Extra(extraStr);
        }
        if (chatInfosBean != null && extra != null) {
            PlayHistory playHistory = new PlayHistory(Long.valueOf(chatInfosBean.getId()), extra.getMsgId(), message.getUri().toString());
            GreenDaoHelper.getInstance().getSeeion().getPlayHistoryDao().insertOrReplace(
                    playHistory);
        }
    }


    public void setAutoPlayNextVoiceMessage(boolean isAuto) {
        isAutoPlay = isAuto;
        SpUtil.putBoolean(MyApplication.getInstance().getApplicationContext(),
                Constant.AUTO_PLAY_NEXT_VOICEMASSAGE, isAuto);
    }
}
