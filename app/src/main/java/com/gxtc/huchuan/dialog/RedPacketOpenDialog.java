package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.im.redPacket.RPOpenMessage;
import com.gxtc.huchuan.ui.im.redPacket.RedPacketDetailedActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;
import com.gxtc.huchuan.utils.AndroidDes3Util;
import com.gxtc.huchuan.utils.Rotate3dAnimation;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/6.
 * 开红包弹窗
 */

public class RedPacketOpenDialog extends Dialog {

    @BindView(R.id.iv_close)       ImageView imgClose;
    @BindView(R.id.iv_header)      ImageView imgHead;
    @BindView(R.id.iv_open_rp)     ImageView imgOpen;
    @BindView(R.id.tv_name)        TextView  tvName;
    @BindView(R.id.tv_tip)         TextView  tvContent;
    @BindView(R.id.tv_no_rp)       TextView  tvLabel;
    @BindView(R.id.tv_look_others) TextView  tvOrder;

    private boolean isPlaying = false;
    private boolean isNetworking = false;
    private boolean isOpen = false;

    private RedPacketBean                 bean;
    private CircleSource                  mData;
    private String                        targetId;
    private Conversation.ConversationType mConversationType;

    private MediaPlayer mMediaPlayer;

    public RedPacketOpenDialog(@NonNull Context context, RedPacketBean bean) {
        super(context, R.style.dialog_Translucent);

        this.bean = bean;
        targetId = bean.getTargetId();
        mConversationType = bean.getConversationType();
        mData = new CircleRepository();

        initView();
        initData();
        initListener();
    }


    private void initView() {
        View view = View.inflate(getContext(), R.layout.dialog_open_rp, null);
        ButterKnife.bind(this, view);
        setContentView(view);
    }


    private void initData() {
        String headPic = bean.getUserPic();
        ImageHelper.loadRound(getContext(), imgHead, headPic, 4);
        tvContent.setText(bean.getMessage());
        tvName.setText(bean.getUserName());
        //判断是否是随机红包
        if(bean.getAllotType() == 1){
            tvName.setCompoundDrawables(null,null,null,null);
        }

        fillData();
    }

    private void initListener() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mMediaPlayer != null){
                    if(!mMediaPlayer.isPlaying()){
                        mMediaPlayer.release();
                        mMediaPlayer.setOnPreparedListener(null);
                        mMediaPlayer = null;
                    }else{
                        Observable.timer(1, TimeUnit.SECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        if(mMediaPlayer != null){
                                            LogUtil.i("call gc() ：   mMediaPlayer");
                                            mMediaPlayer.release();
                                            mMediaPlayer.setOnPreparedListener(null);
                                            mMediaPlayer = null;
                                            System.gc();
                                        }
                                    }
                                });
                    }
                }
                mData.destroy();
            }
        });
    }

    @OnClick({R.id.iv_close, R.id.iv_open_rp,R.id.tv_look_others})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;

            //打开红包
            case R.id.iv_open_rp:
                imgOpen.setImageResource(R.drawable.open_rp_select);
                openRedPacket();
                initAnima();
                break;

            //查看红包详情
            case R.id.tv_look_others:
                Intent intent = new Intent(getContext(), RedPacketDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA, bean);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
                dismiss();
                break;
        }
    }

    //领取红包
    private void openRedPacket() {
        isNetworking = true;
        String token = UserManager.getInstance().getToken();
        String ciphertext = "";

        try {
            //解密后获取红包Id 再用自己的token 加密
            String des3 = AndroidDes3Util.decode(bean.getId());
            LogUtil.i("des3  : " + des3);

            JSONObject encodeJson = new JSONObject();
            encodeJson.put("redBagId",des3);
            encodeJson.put("token",token);
            ciphertext = AndroidDes3Util.encode(encodeJson.toString());

            LogUtil.i("ciphertext  : " + ciphertext);

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getContext(),"红包Id为空");
        }


        mData.receiveRedPacket(ciphertext, new ApiCallBack<RedPacketBean>() {
            @Override
            public void onSuccess(RedPacketBean data) {
                isNetworking = false;
                if (data != null) {
                    bean = data;
                    if(!isPlaying && !isOpen){
                        handleData();
                    }
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                //红包已经领完了
                if ("10172".equals(errorCode)) {
                    bean.setIsFinish(1);
                    fillData();
                } else {
                    ToastUtil.showShort(getContext(), message);
                }

            }
        });
    }

    //准备领红包的提示音
    private void prepareRedPacketMusic(){
        try {
            mMediaPlayer = MediaPlayer.create(getContext(),R.raw.cash_received);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //设置了会话里的提示声音才播放红包的声音
                    if( SettingActivity.sound){
                        mMediaPlayer.start();
                    }
                }
            });
        }catch (IllegalStateException e){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    //播放红包来了的声音
    private void playerRedPacketMusic(){
        try {
            mMediaPlayer.stop();
            mMediaPlayer.prepareAsync();
        }catch (Exception e){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //修改钱包余额
    private void changeMoney() {
        //更新本地钱数
        String token = UserManager.getInstance().getToken();
        MineApi.getInstance()
               .getUserInfo(token)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ApiObserver<ApiResponseBean<User>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        User user = (User) data;
                        UserManager.getInstance().updateUserMoney(user);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LogUtil.i("redPacketIssued getUserInfo  : " + message);
                    }
                }));
    }

    //发送已经领取红包的消息
    private void sendReceiveRedPacketMsg() {
        String        id           = bean.getId();
        String        sendNickId   = bean.getUserCode();
        String        sendNickName = bean.getUserName();
        String        isGetDone    = bean.getIsFinish() + "";
        RPOpenMessage openMessage  = RPOpenMessage.obtain(id, isGetDone, sendNickId, sendNickName);
        String        userCode     = UserManager.getInstance().getUserCode();
        String        name         = UserManager.getInstance().getUser().getName();
        String        url          = UserManager.getInstance().getUser().getHeadPic();
        UserInfo      userInfo     = new UserInfo(userCode,name, Uri.parse(url));
        openMessage.setUserInfo(userInfo);
        String userIds [] = {sendNickId,userCode};
        RongIM.getInstance().sendDirectionalMessage(mConversationType,targetId,openMessage,userIds,"[红包消息]","[红包消息]",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        dismiss();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        dismiss();
                    }
                });

    }

    private void handleData(){
        if(bean == null) return;
        isOpen = true;

        //下手太慢，红包被抢完了
        if (bean.getIsFinish() == 1 && bean.getIsSnatch() == 0) {
            fillData();

            //领取成功 跳转到红包详情页面
        } else {
            Intent intent = new Intent(getContext(), RedPacketDetailedActivity.class);
            intent.putExtra(Constant.INTENT_DATA, bean);
            getContext().startActivity(intent);

            if(mMediaPlayer == null){
                prepareRedPacketMusic();
            }else{
                playerRedPacketMusic();
            }

            //发送已经领取红包的消息
            sendReceiveRedPacketMsg();

            //更新钱包余额
            changeMoney();
        }
    }

    private void fillData() {
        //红包没领取完毕
        if (bean.getIsFinish() == 0) {
            tvLabel.setVisibility(View.VISIBLE);

            //还没有领红包
            if (bean.getIsSnatch() == 0) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(bean.getMessage());
                imgOpen.setVisibility(View.VISIBLE);
                tvOrder.setVisibility(View.GONE);
            } else {
                Intent intent = new Intent(getContext(), RedPacketDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA, bean);
                getContext().startActivity(intent);
            }

        //红包已经领完了
        } else {
            imgOpen.clearAnimation();
            imgOpen.setVisibility(View.INVISIBLE);
            tvLabel.setVisibility(View.GONE);
            tvOrder.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("手慢了，红包派完了");
        }
    }


    private void initAnima() {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_rp);
        float centerX = bitmap.getWidth() / 2.0f;
        float centerY = bitmap.getHeight() / 2.0f;

        //括号内参数分别为（上下文，开始角度，结束角度，x轴中心点，y轴中心点，深度，是否扭曲）
        final Rotate3dAnimation rotation = new Rotate3dAnimation(getContext(), 0.f, 180.f, centerX, centerY, 1.0f, true);
        rotation.setRepeatMode(Animation.RESTART);
        rotation.setRepeatCount(1);                             //转两圈
        rotation.setDuration(300);                              //设置动画时长
        rotation.setFillAfter(true);                            //保持旋转后效果
        rotation.setInterpolator(new LinearInterpolator());     //设置插值器

        rotation.setAnimationListener(new Animation.AnimationListener() {   //设置监听器

            @Override
            public void onAnimationStart(Animation animation) {
                isPlaying = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPlaying = false;
                if(!isNetworking && !isOpen){
                    handleData();
                }
            }
        });
        imgOpen.startAnimation(rotation);                            //开始动画
    }

}
