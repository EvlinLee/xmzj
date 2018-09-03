package com.gxtc.huchuan.im;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gubr on 2017/2/15.
 * 录音的控件  有长按模式 跟短按模式
 */

public class VoicRecordView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "VoicRerodView";

    private ViewGroup controller;
    private TextView oneModel;
    private TextView tips;
    private TextView longModel;
    private ImageView stopVoice;
    private ImageView oneStartVoice;
    private LinearLayout mTimeArea;
    private TextView mVoiceRecordHint;
    private TextView mVoiceCancel;
    private TextView mStartSecondTime;

    private boolean isLongModel;
    private boolean isStatTime = false;
    private boolean isTimeOut = false;

    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;

    private int startTime;
    private int endTime;
    private boolean mIsAnimActive;
    private ViewGroup mModelArea;
    private boolean isOneModelStop = false;
    private ImageView longStartVoice;
    private Dialog mRecordCancel;

    private static int secondTime = 0;
    private static final int msgKey1 = 1;
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler{
        VoicRecordView mVoicRecordView;
        WeakReference<VoicRecordView> mViewWeakReference;

        public MyHandler(VoicRecordView voicRecordView) {
            mViewWeakReference = new WeakReference<>(voicRecordView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mVoicRecordView = mViewWeakReference.get();
            if(mVoicRecordView == null) return;
            switch (msg.what) {
                case msgKey1:
                    mVoicRecordView.mStartSecondTime.setText(String.valueOf(++secondTime));
                    //按钮模式下 超过60s自动发送语音出去并启用下一条语音
                    if(!mVoicRecordView.isLongModel && secondTime == 60){
                        mVoicRecordView.onOneModelStop();
                        mVoicRecordView.onOneModelSend();
                        mVoicRecordView.onOneModelStart();
                    }

                    //长按模式下，超过60s自动发送语音出去
                    if(mVoicRecordView.isLongModel && secondTime == 60){
                        mVoicRecordView.isTimeOut = true;
                        mVoicRecordView.onLongModeUp(false);
                    }
                    LogUtil.i("secondTime  :  " + secondTime);
                    break;
            }
        }
    }



    public VoicRecordView(Context context) {
        this(context, null);
    }

    public VoicRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
    }

    private void initView() {
        controller = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_voice_controller, this, false);
        mModelArea = (ViewGroup) controller.findViewById(R.id.rl_model_area);
        oneModel = (TextView) controller.findViewById(R.id.tv_voice_click_model);
        tips = (TextView) controller.findViewById(R.id.tv_voice_tips3);
        longModel = (TextView) controller.findViewById(R.id.tv_voice_long_touch_model);
        longStartVoice = (ImageView) controller.findViewById(R.id.long_model_start_voice);
        oneStartVoice = (ImageView) controller.findViewById(R.id.start_voice);
        stopVoice = (ImageView) controller.findViewById(R.id.voice_animation);
        mTimeArea = (LinearLayout) controller.findViewById(R.id.ll_time_area);
        mVoiceRecordHint = (TextView) controller.findViewById(R.id.tv_voice_record_hint);
        mVoiceCancel = (TextView) controller.findViewById(R.id.tv_voice_cancel);
        mStartSecondTime = (TextView) controller.findViewById(R.id.tv_record_start_second_time);
        addView(controller);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    public boolean isLongModel() {
        return isLongModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_voice_click_model:
                setOneModel();
                break;
            case R.id.tv_voice_long_touch_model:
                setLongModel();
                break;
            case R.id.tv_voice_cancel:
                showcancelDialog();
                break;
            case R.id.voice_animation:
                //如果按过一次了 就是显示 点击发送  再点一次就是发送了。
                if (isOneModelStop) {
                    onOneModelSend();
                } else {
                    onOneModelStop();
                }
                break;
            case R.id.start_voice:
                onOneModelStart();
                break;
        }
    }

    private void cancelOneModeSend() {
        stopVoice.setImageResource(R.drawable.icon_voice_stop);
        stopVoice.setVisibility(GONE);
        mStartSecondTime.setText(String.valueOf(secondTime = 0));
    }



    View view;
    View cancel;
    View okview;

    private void showcancelDialog() {
        if (mRecordCancel == null) {
            mRecordCancel = new Dialog(getContext(), R.style.MyDialogStyle);
             view = inflate(getContext(), R.layout.dialog_voicerecord_cancel, null);
            mRecordCancel.setContentView(view);
            cancel = view.findViewById(R.id.tv_cancel);
            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecordCancel.dismiss();
                }
            });
            okview = view.findViewById(R.id.tv_asok);
            okview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecordCancel.dismiss();
                    onOneModelCancel();
                }
            });
        }
        mRecordCancel.show();

    }

    /**
     * 开始计时;
     */
    private void onStartTime() {
        mTimeArea.setVisibility(VISIBLE);
        mStartSecondTime.setText(String.valueOf(secondTime = 0));
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.what = msgKey1;
                mHandler.sendMessage(message);
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }


    private void hindAndStopTime() {
        mTimeArea.setVisibility(GONE);
        onStopTime();
    }


    private void showTimeArea() {
        mTimeArea.setVisibility(VISIBLE);
    }


    private void onStopTime() {
        secondTime = 0;
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }


    /**
     * 按钮模式开始
     * 隐藏 start按钮  显示 停止按钮
     * 计时 开始
     */
    private void onOneModelStart() {
        if (!isLongModel) {
            oneStartVoice.setVisibility(GONE);
            stopVoice.setImageResource(R.drawable.icon_voice_stop);
            stopVoice.setVisibility(VISIBLE);
            mModelArea.setVisibility(GONE);
            if (mVoicListener != null) {
                mVoicListener.onOneModelStart();
            }
            onStartTime();
        }
    }

    /**
     * 按钮模式结束
     * 更换停止按钮 显示取消按钮
     * 计时 结束
     */
    private void onOneModelStop() {
        if (!isLongModel) {
            if (secondTime < 3) {
                ToastUtil.showShort(getContext(), "时间太短");
                return;
            }
            isOneModelStop = true;
            stopVoice.setImageResource(R.drawable.icon_record_send);
            mVoiceCancel.setVisibility(VISIBLE);
            if (mVoicListener != null) {
                mVoicListener.onOneModelStop();
            }
            onStopTime();
        }
    }


    /**
     * 按钮模式下 取消发送录音
     */
    private void onOneModelCancel() {
        if (mVoicListener != null) {
            mVoicListener.onVoicCancel();
        }
        stopVoice.setImageResource(R.drawable.icon_voice_stop);
        stopVoice.setVisibility(GONE);
        oneStartVoice.setVisibility(VISIBLE);
        mVoiceCancel.setVisibility(GONE);
        mModelArea.setVisibility(VISIBLE);
        mStartSecondTime.setText(String.valueOf(secondTime = 0));
        isOneModelStop = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

    }


    /**
     * 发送信息
     */
    private void onOneModelSend() {
        if (mVoicListener != null) {
            mVoicListener.onOneModelSend();
        }
        stopVoice.setImageResource(R.drawable.icon_voice_stop);
        stopVoice.setVisibility(GONE);
        oneStartVoice.setVisibility(VISIBLE);
        mVoiceCancel.setVisibility(GONE);
        mModelArea.setVisibility(VISIBLE);
        mStartSecondTime.setText(String.valueOf(secondTime = 0));
        isOneModelStop = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

    }

    private void onLongModelDown() {
        mVoiceRecordHint.setVisibility(GONE);
        onStartTime();
        if (mVoicListener != null) {
            mVoicListener.onLongModelTouchDown();
        }
    }


    /**
     * 长按 抬起
     *
     * @param flag 是否在按钮内抬起
     */
    private void onLongModeUp(boolean flag) {
        if (!flag) {
            isShowTimeShortToast();
        }
        hindAndStopTime();
        if (mVoicListener != null) {
            mVoicListener.onLongModelTouchUp(flag);
        }
        mVoiceRecordHint.setVisibility(VISIBLE);
    }

    private boolean isShowTimeShortToast() {
        if (secondTime < 3) {
            ToastUtil.showShort(getContext(), "时间太短");
            return true;
        }
        return false;
    }

    private void setOneModel() {
        if (isLongModel) {
            modelswitch();
        }
    }

    private void setLongModel() {
        if (!isLongModel) {
            modelswitch();
        }
    }


    /**
     * 模式切换
     */
    public void modelswitch() {
        if (mIsAnimActive) return;
        if (isLongModel) {
            mStartSecondTime.setText(String.valueOf(secondTime = 0));
            //转按钮模式
            ViewCompat.animate(mModelArea).translationXBy((float) (oneModel.getWidth() * 1.5)).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    mIsAnimActive = true;
                }

                @Override
                public void onAnimationEnd(View view) {
                    oneModel.setTextColor(getResources().getColor(R.color.tool_bar_bg));
                    longModel.setTextColor(getResources().getColor(R.color.grey1));
                    mTimeArea.setVisibility(VISIBLE);
                    mVoiceRecordHint.setVisibility(GONE);
//                    oneStartVoice.setImageResource(R.drawable.icon_voice_start);
                    oneStartVoice.setVisibility(VISIBLE);
                    longStartVoice.setVisibility(GONE);
                    mIsAnimActive = false;
                    isLongModel = false;

                }

                @Override
                public void onAnimationCancel(View view) {
                    mIsAnimActive = false;
                }
            }).start();
        } else {
            //转长按模式
            ViewCompat.animate(mModelArea).translationXBy((float) -(oneModel.getWidth() * 1.5)).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    mIsAnimActive = true;
                }

                @Override
                public void onAnimationEnd(View view) {
                    oneModel.setTextColor(getResources().getColor(R.color.grey1));
                    longModel.setTextColor(getResources().getColor(R.color.tool_bar_bg));
                    mTimeArea.setVisibility(GONE);
                    mVoiceRecordHint.setVisibility(VISIBLE);
//                    oneStartVoice.setImageResource(R.drawable.icon_voice_1);
                    longStartVoice.setVisibility(VISIBLE);
                    oneStartVoice.setVisibility(GONE);
                    mIsAnimActive = false;
                    isLongModel = true;
                }

                @Override
                public void onAnimationCancel(View view) {
                    mIsAnimActive = false;
                }
            }).start();
        }
    }


    private void initData() {
        longStartVoice.setOnTouchListener(new LongStartVoicTouchListener());
        oneModel.setOnClickListener(this);
        longModel.setOnClickListener(this);
        stopVoice.setOnClickListener(this);
        oneStartVoice.setOnClickListener(this);
        mVoiceCancel.setOnClickListener(this);
    }


    private int vCenterX, vCenterY, r;
    private int lastX, lastY;


    private class LongStartVoicTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Log.d(TAG, "v:" + v);
            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTimeOut = false;
                    if (isLongModel) {
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        r = v.getWidth() / 2;
                        vCenterX = x + r;
                        vCenterY = y + r;
                        //点击位置x坐标与圆心的x坐标的距离
                        int distanceX = Math.abs(vCenterX - lastX);
                        //点击位置y坐标与圆心的y坐标的距离
                        int distanceY = Math.abs(vCenterY - lastY);
                        //点击位置与圆心的直线距离
                        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                        if (distanceZ < r) {
                            onLongModelDown();
                        } else {
                            return false;
                        }
                    } /*else {
                        onOneModelStart();
                    }*/
                    break;
                case MotionEvent.ACTION_UP:
                    if (isLongModel && !isTimeOut) {
                        //点击位置x坐标与圆心的x坐标的距离
                        int distanceX = Math.abs(vCenterX - lastX);
                        //点击位置y坐标与圆心的y坐标的距离
                        int distanceY = Math.abs(vCenterY - lastY);
                        //点击位置与圆心的直线距离
                        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                        onLongModeUp(distanceZ > r);
                    }
                    isTimeOut = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //点击位置x坐标与圆心的x坐标的距离
                    int distanceX = Math.abs(vCenterX - lastX);
                    //点击位置y坐标与圆心的y坐标的距离
                    int distanceY = Math.abs(vCenterY - lastY);
                    //点击位置与圆心的直线距离
                    int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                    //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                    if (mVoicListener != null) {
                        mVoicListener.onLongModelTouchIsOutSize(distanceZ > r);
                    }

                    break;
            }
            return isLongModel;
        }
    }


    private class StartVoicTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    if (isLongModel) {
                        if (mVoicListener != null) {
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            r = v.getWidth() / 2;
                            vCenterX = x + r;
                            vCenterY = y + r;
                            //点击位置x坐标与圆心的x坐标的距离
                            int distanceX = Math.abs(vCenterX - lastX);
                            //点击位置y坐标与圆心的y坐标的距离
                            int distanceY = Math.abs(vCenterY - lastY);
                            //点击位置与圆心的直线距离
                            int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

                            //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                            if (distanceZ < r) {
                                onLongModelDown();
                            } else {
                                return false;
                            }
                        }
                    } else {
                        onOneModelStart();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isLongModel) {
                        //点击位置x坐标与圆心的x坐标的距离
                        int distanceX = Math.abs(vCenterX - lastX);
                        //点击位置y坐标与圆心的y坐标的距离
                        int distanceY = Math.abs(vCenterY - lastY);
                        //点击位置与圆心的直线距离
                        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                        if (mVoicListener != null) {
                            mVoicListener.onLongModelTouchUp(distanceZ > r);
                        }

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    //点击位置x坐标与圆心的x坐标的距离
                    int distanceX = Math.abs(vCenterX - lastX);
                    //点击位置y坐标与圆心的y坐标的距离
                    int distanceY = Math.abs(vCenterY - lastY);
                    //点击位置与圆心的直线距离
                    int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                    //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                    if (mVoicListener != null) {
                        mVoicListener.onLongModelTouchIsOutSize(distanceZ > r);
                    }

                    break;
            }
            return isLongModel;
        }
    }




    private OnVoicListener mVoicListener;

    public void setVoicListener(OnVoicListener voicListener) {
        mVoicListener = voicListener;
    }

    public interface OnVoicListener {
        void onOneModelStart();

        void onOneModelStop();

        void onOneModelSend();

        void onLongModelTouchDown();

        void onLongModelTouchIsOutSize(boolean flag);

        /**
         * @param flag 是否是在外部抬起。
         */
        void onLongModelTouchUp(boolean flag);


        void onVoicCancel();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mTimer != null)
        mTimer.cancel();
        mTimer = null;
        mHandler.removeMessages(msgKey1);
        mHandler = null;
    }
}
