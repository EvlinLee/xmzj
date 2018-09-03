package com.gxtc.huchuan.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;
import com.gxtc.huchuan.ui.live.participation.ParticipationActivity;


/**
 * Created by Gubr on 2017/3/30.
 */

public class PlayBarView extends FrameLayout implements View.OnClickListener {

    private ChatInfosBean bean;

    public static final int START_STATUS  = 1;
    public static final int PAUSE_STATUS  = 2;
    public static final int TOGGLE_STATUS = 4;

    private int mStaus = PAUSE_STATUS;

    private ImageView         bg;
    private RotateImageButton head;
    private ImageView         btn;
    private ImageView         imgLisd;
    private ImageView         imgNext;
    private TextView          tvTitle;
    private TextView          tvName;

    public PlayBarView(@NonNull Context context) {
        this(context, null);
    }

    public PlayBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayBarView(@NonNull Context context, @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_play_bar, this);
        bg = (ImageView) findViewById(R.id.iv_play_bar_bg);
        head = (RotateImageButton) findViewById(R.id.iv_play_bar_head);
        btn = (ImageView) findViewById(R.id.iv_play_bar_btn);
        imgNext = (ImageView) findViewById(R.id.img_next);
        imgLisd = (ImageView) findViewById(R.id.img_list);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvName = (TextView) findViewById(R.id.tv_name);

        setOnClickListener(this);
        btn.setOnClickListener(this);
        imgLisd.setOnClickListener(this);
        imgNext.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击进入课堂
            case R.id.playbarview:
                if (bean != null) LiveConversationActivity.startActivity(getContext(), bean);
                break;

            //播放按钮
            case R.id.iv_play_bar_btn:
                if (bean != null) {
                    if (AudioPlayManager.getInstance().isAudioPlaying()) {
                        AudioPlayManager.getInstance().pausePlay();
                    } else {
                        AudioPlayManager.getInstance().startPlay(bean);
                    }
                }
                break;

            //我参与的历史
            case R.id.img_list:
                Intent intent = new Intent(getContext(), ParticipationActivity.class);
                getContext().startActivity(intent);
                break;

            //下一条语音
            case R.id.img_next:
                if (AudioPlayManager.getInstance().isAudioPlaying()) {
                    AudioPlayManager.getInstance().playNext();
                }
                break;
        }
    }

    public void setData(ChatInfosBean bean) {
        if (bean == null) return;

        this.bean = bean;
        tvTitle.setText(bean.getSubtitle());
        tvName.setText(bean.getMainSpeaker());
        ImageHelper.loadCircle(getContext(), head, this.bean.getFacePic());
    }


    public void setStuas(int staus) {
        switch (staus) {
            case START_STATUS:
                btn.setImageResource(R.drawable.use_icon_bofang);
                head.setRotate(true);
                break;

            case PAUSE_STATUS:
                btn.setImageResource(R.drawable.use_icon_zanting);
                head.setRotate(false);
                break;
        }
    }
}
