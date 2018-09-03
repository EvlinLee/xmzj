package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.im.event.PlayEvent;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Gubr on 2017/3/29.
 *
 */

public class ParicipationAdaspter extends BaseRecyclerAdapter<ChatInfosBean> {

    private static boolean mDestory;

    public ParicipationAdaspter(Context context, List<ChatInfosBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        mDestory = false;
    }

    @Override
    public void bindData(ViewHolder holder, int position, ChatInfosBean bean) {
        if (holder.getDataTag() == null) {
            PlayEventListnerBean playEventListnerBean = new PlayEventListnerBean(holder);
            holder.setDataTag(playEventListnerBean);
        }

        PlayEventListnerBean playEventListnerBean = (PlayEventListnerBean) holder.getDataTag();
        playEventListnerBean.setData(bean);
        ImageHelper.loadImage(getContext(), holder.getImageView(R.id.dvHeader), bean.getFacePic(), R.drawable.live_default_play_picture);

        holder.setText(R.id.tvTopicName, bean.getSubtitle());

        if (ConversationManager.getInstance().getChatInfosBean() != null)
            holder.getImageView(R.id.ivStatus)
                  .setImageResource(bean.getId().equals(ConversationManager.getInstance().getChatInfosBean().getId())
                  && AudioPlayManager.getInstance().isAudioPlaying() ? R.drawable.ic_back_pause : R.drawable.ic_back_play);

        TextView tvName = holder.getViewV2(R.id.tv_name);
        tvName.setText(bean.getName());

        TextView tvMoney = holder.getViewV2(R.id.tv_money);
        //免费
        if("0".equals(bean.getFee()) || "0".equals(bean.getIsfree())){
            tvMoney.setText("免费");
            tvMoney.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.text_color_999));
        }else{
            String money = "￥" + StringUtil.formatMoney(2,bean.getFee());
            tvMoney.setText(money);
            tvMoney.setTextColor(holder.getItemView().getContext().getResources().getColor(R.color.color_fb4717));
        }

        TextView message = holder.getViewV2(R.id.tvMessage);
        try {
            //1：预告，2：直播中，3：结束
            int status = Integer.valueOf(bean.getStatus());
            if(status == 1){
                long startTime = Long.valueOf(bean.getStarttime());

                message.setText(DateUtil.formatTime(startTime, "MM-dd HH:mm"));
                long l = startTime - System.currentTimeMillis();
                if (l > 0) {
                    String[] strings = DateUtil.countDownNotAddZero(l);
                    if (!strings[0].equals("0")) {
                        message.setText(strings[0] + "天");

                    } else if (!strings[1].equals("0")) {
                        message.setText(strings[1] + "小时");

                    } else if(!strings[2].equals("0")) {
                        message.setText(strings[2] + "分");

                    }else{
                        message.setText("进行中");
                    }
                }

            }

            if(status == 2){
                message.setText("直播中");
            }

            if(status == 3){
                message.setText("已开始");
            }
            message.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

        }catch (Exception e){
            e.printStackTrace();
        }

        TextView tvStatic = holder.getViewV2(R.id.tv_status);
        if(!"0".equals(bean.getChatSeries())){
            tvStatic.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        }else{
            tvStatic.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }

    }


    private static class PlayEventListnerBean implements View.OnClickListener {

        private       WeakReference<ImageView> imgReference;
        private       WeakReference<ViewHolder> holderReference;
        private       ChatInfosBean            mBean;

        PlayEventListnerBean(ViewHolder holder) {
            holderReference = new WeakReference<>(holder);
            ImageView mIvStatus = holderReference.get().getViewV2(R.id.ivStatus);
            mIvStatus.setOnClickListener(this);
            EventBusUtil.register(PlayEventListnerBean.this);
            imgReference = new WeakReference<>(mIvStatus);
        }

        public void setData(ChatInfosBean bean) {
            mBean = bean;
            if(imgReference != null && imgReference.get() != null){
                ImageView mIvStatus = imgReference.get();
                if(!"0".equals(bean.getChatSeries())){
                    mIvStatus.setVisibility(View.GONE);
                    mIvStatus.setOnClickListener(null);
                }else{
                    mIvStatus.setVisibility(View.VISIBLE);
                    mIvStatus.setOnClickListener(this);
                }
            }
        }

        @Subscribe
        public void PlayEvent(PlayEvent bean) {
            if (mDestory) EventBusUtil.unregister(PlayEventListnerBean.this);

            if(imgReference == null && imgReference.get() == null){
                return;
            }

            ImageView mIvStatus = imgReference.get();
            if (mBean.getId().equals(bean.getChatInfosId())) {
                if(AudioPlayManager.getInstance().isAudioPlaying()){
                    mIvStatus.setImageResource(R.drawable.ic_back_play);
                }else{
                    mIvStatus.setImageResource(R.drawable.ic_back_pause);
                }

            } else {
                mIvStatus.setImageResource(R.drawable.ic_back_play);
            }

            if(bean.getStaus() == PlayEvent.PAUSE_STATUS){
                mIvStatus.setImageResource(R.drawable.ic_back_play);
            }
        }

        @Override
        public void onClick(View v) {
            //这里到时候去播放
            //播放 暂停
            EventBusUtil.post(new PlayEvent(mBean, PlayEvent.CLICK_STATUS));

            if(imgReference == null && imgReference.get() == null){
                return;
            }

            ImageView mIvStatus = imgReference.get();

            ChatInfosBean chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
            if(chatInfosBean != null && mBean.getId().equals(chatInfosBean.getId())){
                if(AudioPlayManager.getInstance().isAudioPlaying()){
                    AudioPlayManager.getInstance().pausePlay();
                    mIvStatus.setImageResource(R.drawable.ic_back_play);
                }else{
                    AudioPlayManager.getInstance().startPlay(mBean);
                    mIvStatus.setImageResource(R.drawable.ic_back_pause);
                }
            }else{
                AudioPlayManager.getInstance().pausePlay();
                AudioPlayManager.getInstance().startPlay(mBean);
                mIvStatus.setImageResource(R.drawable.ic_back_pause);
            }


        }
    }

    public void onDestory() {
        mDestory = true;
    }

}
