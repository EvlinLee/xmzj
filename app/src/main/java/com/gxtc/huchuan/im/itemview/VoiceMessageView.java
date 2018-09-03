package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.IAudioPlayListener;
import com.gxtc.huchuan.im.manager.MessageManager;
import com.gxtc.huchuan.im.manager.VoiceTimeBean;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.utils.DateUtil;

import java.lang.ref.WeakReference;

import io.rong.imkit.utils.RongDateUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.VoiceMessage;

/**
 * Created by Gubr on 2017/2/22.
 */

public class VoiceMessageView extends AbsMessageView {

    private PopReward mPopReward;
    private MultiItemTypeAdapter<Message> mAdapter;

    public VoiceMessageView(Activity activity, MultiItemTypeAdapter<Message> adapter) {
        super(activity);
        mAdapter = adapter;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_char_received_voice;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "RC:VcMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(ViewHolder holder, final Message message, int position) {
        if (holder.getDataTag() == null) {
            MyAudioPlayListener myAudioPlayListener = new MyAudioPlayListener(holder);
            holder.setDataTag(myAudioPlayListener);
            AudioPlayManager.getInstance().addPlayListener(myAudioPlayListener);
        }
        MyAudioPlayListener myaudioplayList = (MyAudioPlayListener) holder.getDataTag();
        myaudioplayList.setposition(position).setMessage(message);


        final VoiceMessage mContent = (VoiceMessage) message.getContent();
        VoiceTimeBean voiceTimeBean = MessageManager.getInstance().getVoiceTimeBean(
                mContent.getUri());

        holder.setTag(R.id.iv_voice, mContent);
        Extra    extra    = new Extra(mContent.getExtra());
        UserInfo userInfo = null;
        if ((userInfo = mContent.getUserInfo()) != null) {
            holder.setText(R.id.tv_item_char_received_voice_sender, mContent.getUserInfo().getName());
            holder.setTag(R.id.iv_zan, userInfo);
            holder.getView(R.id.iv_zan).setOnClickListener(l);
            ImageHelper.loadCircle(getActivity(),(ImageView) holder.getView(R.id.iv_item_char_received_voice_head),userInfo.getPortraitUri().toString(),R.drawable.person_icon_head);
            String sender = null;
            switch (extra.getSenderType()) {
                case "1":
                    sender = "讲师";
                    break;
                case "2":
                    sender = "观众";
                    break;
                case "3":
                    sender = "主持人";
                    break;
            }
            holder.setText(R.id.tv_item_char_received_voice_sendertype, sender);
            holder.setOnLongClickListener(R.id.layout_voice_area, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return l.onLongClick(v, message);
                }
            });
        }


        holder.setText(R.id.tv_item_char_received_voice_duration,
                getTimeForMat(mContent.getDuration()));
        final SeekBar seekBar = holder.getView(R.id.sb_item_char_received_voice_seekbar);
        seekBar.setTag(mContent);
        seekBar.setEnabled(voiceTimeBean.isPlaying());
        seekBar.setMax(voiceTimeBean.getDuration());
        seekBar.setProgress(voiceTimeBean.getCurrentposition());


        holder.getView(R.id.iv_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceMessage voiceMessage = (VoiceMessage) v.getTag();

                Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
                if (playingUri != null && playingUri.equals(voiceMessage.getUri())) {
                    AudioPlayManager.getInstance().pausePlay();
                } else {
                    AudioPlayManager.getInstance().startPlay(MyApplication.getInstance(), voiceMessage.getUri());
                }
            }
        });



        TextView tvTime = holder.getView(R.id.tv_time);
        long sendTime = MessageFactory.getMessageTime(message);
        if(position == 0){
            if(RongDateUtils.isShowChatTime(System.currentTimeMillis(),sendTime,5*60)){
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(DateUtil.getFormatChatTime(sendTime));
            }else{
                tvTime.setVisibility(View.GONE);
            }

        }else{
            Message lastMsg = mAdapter.getDatas().get(position - 1);
            long lastTime = MessageFactory.getMessageTime(lastMsg);
            if(RongDateUtils.isShowChatTime(sendTime,lastTime,5*60)){
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(DateUtil.getFormatChatTime(sendTime));
            }else{
                tvTime.setVisibility(View.GONE);
            }
        }



    }


    private String getTimeForMat(int duration) {
        int           i             = duration / 60;
        int           i1            = duration % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (i > 0) stringBuilder.append("" + i + "\"");
        stringBuilder.append("" + i1 + "\'");
        return stringBuilder.toString();
    }


    static public class MyAudioPlayListener implements IAudioPlayListener {

        private SeekBar   mSeekBar;
        private ImageView mVoice;


        private boolean    cureentPlayStatu;
        private boolean    isSeeking;
        private int        mProgress;
        private int        mPosition;
        private ViewHolder mHolder;
        private Message    mMessage;

        public MyAudioPlayListener(ViewHolder holder) {
            mSeekBar = holder.getView(R.id.sb_item_char_received_voice_seekbar);
            mVoice = holder.getView(R.id.iv_voice);
            cureentPlayStatu = false;
            mVoice.setImageResource(R.drawable.icon_voice_play);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mProgress = progress;
                        AudioPlayManager.getInstance().seekTo(mProgress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSeeking = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSeeking = false;
                }
            });

        }

        @Override
        public void onStart(Uri uri, int duration) {
            if (checkUri(uri)) {
                mSeekBar.setEnabled(true);
                mSeekBar.setMax(duration);
                mSeekBar.setProgress(0);
            }
        }

        @Override
        public void onStop(Uri uri) {
            if (checkUri(uri)) {
                mSeekBar.setProgress(0);
                mSeekBar.setMax(0);
                mSeekBar.setEnabled(false);
            }

        }

        @Override
        public void onComplete(Uri uri) {
            if (checkUri(uri)) {
                if (isSeeking) {
                    return;
                }
                mSeekBar.setProgress(0);
                mSeekBar.setMax(0);
//                mSeekBar.setProgress(mSeekBar.getMax());
                mSeekBar.setEnabled(false);
            }

        }


        /**
         * @param voiceTimeBean
         */
        @Override
        public void PlayEvent(VoiceTimeBean voiceTimeBean) {
            if (voiceTimeBean == null) {
                return;
            }
            setVoiceIcon(false);
            if (checkUri(voiceTimeBean.getUri()) && !isSeeking) {
                mSeekBar.setEnabled(voiceTimeBean.isPlaying());
                setVoiceIcon(voiceTimeBean.isPlaying());
                mSeekBar.setMax(voiceTimeBean.getDuration());
                mSeekBar.setProgress(voiceTimeBean.getCurrentposition());
            }
        }


        private void setVoiceIcon(boolean flag) {
            if (cureentPlayStatu != flag) {
                cureentPlayStatu = flag;
                mVoice.setImageResource(
                        cureentPlayStatu ? R.drawable.icon_voice_pause : R.drawable.icon_voice_play);
            }
        }

        @Override
        public void onProgress(Uri uri, int currentPosition, int duration) {
            Log.d("VoiceMessageView", "currentPosition:" + currentPosition);
            Log.d("VoiceMessageView", "duration:" + duration);
            if (checkUri(uri)) {
                if (isSeeking) {
                    return;
                }
                if (!mSeekBar.isEnabled()) mSeekBar.setEnabled(true);
                if (mSeekBar.getMax() != duration) mSeekBar.setMax(duration);
                mSeekBar.setProgress(currentPosition);
            }
        }

        private boolean checkUri(Uri uri) {
            if (mSeekBar == null) return false;
            return uri.equals(((VoiceMessage) mSeekBar.getTag()).getUri());
        }


        public MyAudioPlayListener setposition(int position) {
            mPosition = position;
            return this;
        }

        public MyAudioPlayListener setMessage(Message message) {
            mMessage = message;
            return this;
        }


    }
}
