package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.im.event.PlayEvent;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Describe:我的 > 课堂  >  观看历史 适配器
 * Created by ALing on 2017/3/13 .
 */

public class ClassRoomHistoryAdapter extends BaseRecyclerAdapter<UnifyClassBean> {

    private static boolean mDestory;

    public ClassRoomHistoryAdapter(Context context, List<UnifyClassBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        mDestory = false;
    }

    @Override
    public void bindData(ViewHolder holder, int position, UnifyClassBean data) {
        UnifyClassBean.SubData bean = data.getData();
        if (holder.getDataTag() == null) {
            ClassRoomHistoryAdapter.PlayEventListnerBean playEventListnerBean = new ClassRoomHistoryAdapter.PlayEventListnerBean(
                    holder);
            holder.setDataTag(playEventListnerBean);
        }

        ClassRoomHistoryAdapter.PlayEventListnerBean playEventListnerBean = (ClassRoomHistoryAdapter.PlayEventListnerBean) holder.getDataTag();
        playEventListnerBean.setData(data);
        ImageHelper.loadRound(getContext(), holder.getImageView(R.id.dvHeader), bean.getFacePic(),4);

        holder.setText(R.id.tvTopicName, bean.getSubtitle());

        if (ConversationManager.getInstance().getChatInfosBean() != null)
            holder.getImageView(R.id.ivStatus).setImageResource(String.valueOf(bean.getId()).equals(
                    ConversationManager.getInstance().getChatInfosBean().getId()) && AudioPlayManager.getInstance().isAudioPlaying() ? R.drawable.ic_back_pause : R.drawable.ic_back_play);

        TextView tvName = holder.getViewV2(R.id.tv_name);
        tvName.setText(bean.getChatRoomName());

        TextView tvMoney = holder.getViewV2(R.id.tv_money);
        //免费
        if (bean.getFee() == 0 || bean.getIsfree() == 0) {
            tvMoney.setText("免费");
            tvMoney.setTextColor(holder.getItemView().getContext().getResources().getColor(
                    R.color.text_color_999));
        } else {
            String money = "￥" + StringUtil.formatMoney(2, bean.getFee());
            tvMoney.setText(money);
            tvMoney.setTextColor(holder.getItemView().getContext().getResources().getColor(
                    R.color.color_fb4717));
        }

        TextView message = holder.getViewV2(R.id.tvMessage);
        try {
            //1：预告，2：直播中，3：结束
            int status = bean.getStatus();
            if (status == 1) {
                long startTime = Long.valueOf(bean.getStarttime());

                message.setText(DateUtil.formatTime(startTime, "MM-dd HH:mm"));
                long l = startTime - System.currentTimeMillis();
                if (l > 0) {
                    String[] strings = DateUtil.countDownNotAddZero(l);
                    if (!strings[0].equals("0")) {
                        message.setText(strings[0] + "天");

                    } else if (!strings[1].equals("0")) {
                        message.setText(strings[1] + "小时");

                    } else if (!strings[2].equals("0")) {
                        message.setText(strings[2] + "分");

                    } else {
                        message.setText("进行中");
                    }
                }

            }

            if (status == 2) {
                message.setText("直播中");
            }

            if (status == 3) {
                message.setText("已开始");
            }
            message.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView tvStatic = holder.getViewV2(R.id.tv_status);
        if (data.getType() == 2) {
            tvStatic.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        } else {
            tvStatic.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }

    }


    private static class PlayEventListnerBean implements View.OnClickListener {

        private       UnifyClassBean           mBean;
        private       WeakReference<ViewHolder> holderReference;
        private       WeakReference<ImageView>  imgReference;

        PlayEventListnerBean(ViewHolder holder) {
            holderReference = new WeakReference<>(holder);
            ImageView mIvStatus = holderReference.get().getViewV2(R.id.ivStatus);
            mIvStatus.setOnClickListener(this);
            imgReference = new WeakReference<>(mIvStatus);

            EventBusUtil.register(ClassRoomHistoryAdapter.PlayEventListnerBean.this);
        }

        public void setData(UnifyClassBean bean) {
            mBean = bean;
            if(imgReference != null && imgReference.get() != null){
                ImageView mIvStatus = imgReference.get();
                if (mBean.getType() == 2) {
                    mIvStatus.setVisibility(View.GONE);
                    mIvStatus.setOnClickListener(null);
                } else {
                    mIvStatus.setVisibility(View.VISIBLE);
                    mIvStatus.setOnClickListener(this);
                }
            }
        }

        @Subscribe
        public void PlayEvent(PlayEvent bean) {
            if (mDestory)
                EventBusUtil.unregister(ClassRoomHistoryAdapter.PlayEventListnerBean.this);

            if(imgReference == null && imgReference.get() == null){
                return;
            }

            ImageView mIvStatus = imgReference.get();
            if (String.valueOf(mBean.getData().getId()).equals(bean.getChatInfosId())) {
                if (AudioPlayManager.getInstance().isAudioPlaying()) {
                    mIvStatus.setImageResource(R.drawable.ic_back_play);
                } else {
                    mIvStatus.setImageResource(R.drawable.ic_back_pause);
                }

            } else {
                mIvStatus.setImageResource(R.drawable.ic_back_play);
            }

            if (bean.getStaus() == PlayEvent.PAUSE_STATUS) {
                mIvStatus.setImageResource(R.drawable.ic_back_play);
            }
        }

        @Override
        public void onClick(View v) {
            ChatInfosBean infosBean = new ChatInfosBean();
            infosBean.setId(mBean.getData().getId() + "");
            infosBean.setSubtitle(mBean.getData().getSubtitle());
            infosBean.setFacePic(mBean.getData().getFacePic());

            if(imgReference == null && imgReference.get() == null){
                return;
            }

            ImageView mIvStatus = imgReference.get();

            //这里到时候去播放
            //播放 暂停
            EventBusUtil.post(new PlayEvent(infosBean, PlayEvent.CLICK_STATUS));

            ChatInfosBean chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
            if (chatInfosBean != null && infosBean.getId().equals(chatInfosBean.getId())) {
                if (AudioPlayManager.getInstance().isAudioPlaying()) {
                    AudioPlayManager.getInstance().pausePlay();
                    mIvStatus.setImageResource(R.drawable.ic_back_play);
                } else {
                    AudioPlayManager.getInstance().startPlay(infosBean);
                    mIvStatus.setImageResource(R.drawable.ic_back_pause);
                }
            } else {
                AudioPlayManager.getInstance().pausePlay();
                AudioPlayManager.getInstance().startPlay(infosBean);
                mIvStatus.setImageResource(R.drawable.ic_back_pause);
            }
        }
    }

    public void onDestory() {
        mDestory = true;
    }
}