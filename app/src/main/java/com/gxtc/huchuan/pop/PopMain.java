package com.gxtc.huchuan.pop;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MainPagerAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.im.event.PlayEvent;
import com.gxtc.huchuan.im.manager.AudioPlayManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;
import com.gxtc.huchuan.ui.live.participation.ParticipationActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.BlurringView;
import com.gxtc.huchuan.widget.RotateImageButton;
import com.gxtc.huchuan.widget.WrapContentHeightViewPager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class PopMain extends BasePopupWindow {

    @BindView(R.id.btn_main_close) View                       btnClose;
    @BindView(R.id.vp_main)        WrapContentHeightViewPager viewPager;
    @BindView(R.id.blurringView)   BlurringView               blurringView;

    //跟播放有关的控件
    @BindView(R.id.img_head)        RotateImageButton imgHead;
    @BindView(R.id.img_list)        ImageView         imgList;
    @BindView(R.id.img_next)        ImageView         imgNext;
    @BindView(R.id.iv_play_bar_btn) ImageView         imgPlay;
    @BindView(R.id.tv_title)        TextView          tvTitle;

    //日期有关
    @BindView(R.id.tv_weather_week)  TextView tvWeek;
    @BindView(R.id.tv_weather_month) TextView tvMonth;
    @BindView(R.id.tv_weather_day)   TextView tvDay;


    private int index        = 0;
    private int visibleIndex = 0;

    private DealFragment mDealFragment;

    private String tabs[]  = new String[]{"课程", "商城", "交易", "视频", "分销", "钱包"};
    private String tabs2[] = new String[]{"点评", "好友圈", "音乐", "签到", "商品", "红包"};

    private Integer imgs[] = new Integer[]{
            R.drawable.use_icon_kecheng, R.drawable.use_icon_shangcheng,
            R.drawable.use_icon_jiaoyi, R.drawable.use_icon_shiping,
            R.drawable.use_icon_fenxiao, R.drawable.use_icon_qianbao};

    private Integer imgs2[] = new Integer[]{
            R.drawable.use_icon_collect, R.drawable.use_icon_friend,
            R.drawable.use_icon_wallet, R.drawable.use_icon_news,
            R.drawable.use_icon_order, R.drawable.use_icon_more};


    private Subscription    sub;
    private List<View>      views;
    private List<Animation> anims;
    private ObjectAnimator  inAnim;
    private ObjectAnimator  outAnim;

    private MainPagerAdapter adapter;
    private ChatInfosBean currentPlayChatInfosBean = null;

    public PopMain(Activity activity, int resId) {
        super(activity, resId);

        List<List<String>> tabs     = new ArrayList<>();
        List<String>       listTab  = Arrays.asList(this.tabs);
        List<String>       listTab2 = Arrays.asList(this.tabs2);
        tabs.add(listTab);
        //tabs.add(listTab2);

        List<List<Integer>> imgs     = new ArrayList<>();
        List<Integer>       listImg  = Arrays.asList(this.imgs);
        List<Integer>       listImg2 = Arrays.asList(this.imgs2);
        imgs.add(listImg);
        //imgs.add(listImg2);

        views = new ArrayList<>();
        views.add(View.inflate(getActivity(), R.layout.model_main_more, null));
        //views.add(View.inflate(getActivity(), R.layout.model_main_more, null));
        adapter = new MainPagerAdapter(getActivity(), views, tabs, imgs);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);

        inAnim = ObjectAnimator.ofFloat(layoutView, "alpha", 0, 1).setDuration(200);
        outAnim = ObjectAnimator.ofFloat(layoutView, "alpha", 1, 0).setDuration(200);

    }

    @Override
    public void initListener() {
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation == outAnim) {
                    PopMain.super.closePop();
                }
                if (animation == inAnim) {
                    startButtonAnim();
                }
            }
        };
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                cancel();
            }
        });

        outAnim.addListener(animatorListener);
        inAnim.addListener(animatorListener);

    }

    @OnClick({R.id.btn_main_close,
        R.id.iv_play_bar_btn,
        R.id.img_next,
        R.id.img_list})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_close:
                outAnim.start();
                break;

            //播放
            case R.id.iv_play_bar_btn:
                if(UserManager.getInstance().isLogin(getActivity())){
                    if(AudioPlayManager.getInstance().isAudioPlaying()){
                        stopPlay();
                    }else{
                        startPlay();
                    }
                }
                break;

            //播放下一条
            case R.id.img_next:
                if(UserManager.getInstance().isLogin(getActivity())){
                    if (AudioPlayManager.getInstance().isAudioPlaying()) {
                        AudioPlayManager.getInstance().playNext();
                    }
                }
                break;

            //参与的课程
            case R.id.img_list:
                if(UserManager.getInstance().isLogin(getActivity())){
                    Intent intent = new Intent(getActivity(), ParticipationActivity.class);
                    getActivity().startActivity(intent);
                }
                break;

        }
    }




    /**
     * 设置要模糊的视图
     */
    public void setBlurView(final View v) {
        blurringView.setBlurredView(v);
        blurringView.invalidate();
    }

    @Override
    public void showPopOnRootView(Activity activity) {
        super.showPopOnRootView(activity);
        initData();
        inAnim.start();
    }


    @Override
    public void closePop() {
        cancel();
        outAnim.start();
    }

    //接收播放事件
    @Subscribe
    public void showCurrentPlayTopic(PlayEvent data) {
        currentPlayChatInfosBean = data.getChatInfosBean();
        ImageHelper.loadCircle(getActivity(),imgHead,currentPlayChatInfosBean.getFacePic());
        tvTitle.setText(currentPlayChatInfosBean.getSubtitle());

        switch (data.getStaus()) {
            case PlayEvent.TOGGLE_STATUS:
                //mPlaybarview.setStuas(PlayBarView.TOGGLE_STATUS);
                break;

            case PlayEvent.START_STATUS:
                imgPlay.setImageResource(R.drawable.use_icon_bofang);
                imgHead.setRotate(true);
                break;

            case PlayEvent.CLICK_STATUS:
            case PlayEvent.PAUSE_STATUS:
                imgPlay.setImageResource(R.drawable.use_icon_zanting);
                imgHead.setRotate(false);
                break;
        }
    }

    private void initData(){
        ChatInfosBean chatInfosBean = ConversationManager.getInstance().getChatInfosBean();
        if (chatInfosBean != null) {
            currentPlayChatInfosBean = chatInfosBean;
            ImageHelper.loadCircle(getActivity(),imgHead,currentPlayChatInfosBean.getFacePic());
            tvTitle.setText(currentPlayChatInfosBean.getSubtitle());

            if (AudioPlayManager.getInstance().isAudioPlaying()) {
                startPlay();
            }
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String days = day < 10 ? "0" + day : String.valueOf(day);
        tvDay.setText(days);

        String date = month < 10 ? "0" + month + "/" + year : month + "/" + year;
        tvMonth.setText(date);

        String weeks = "星期" + DateUtil.getCurrWeekday();
        tvWeek.setText(weeks);
    }


    /**
     * 启动按钮的上滑动画
     */
    private void startButtonAnim() {
        anims = new ArrayList<>();
        final RelativeLayout layout = (RelativeLayout) views.get(0).findViewById(R.id.rl_main_anim);

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.getChildAt(visibleIndex++).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        };


        for (int i = 0; i < layout.getChildCount(); i++) {
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.translation_y_in);
            anim.setInterpolator(new OvershootInterpolator(1.0f));
            anim.setAnimationListener(animationListener);
            anims.add(anim);
        }

        sub = Observable.interval(100, TimeUnit.MILLISECONDS).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (index < anims.size()) {
                    layout.getChildAt(index).startAnimation(anims.get(index));
                } else {
                    sub.unsubscribe();
                }
                index++;
            }
        });
    }

    private void cancel() {
        if (sub != null) {
            sub.unsubscribe();
        }
        if (anims != null) {
            anims.clear();
            index = 0;
            visibleIndex = 0;
            ViewGroup viewGroup = (ViewGroup) views.get(0);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void startPlay() {
        if(currentPlayChatInfosBean != null){
            imgPlay.setImageResource(R.drawable.use_icon_bofang);
            imgHead.setRotate(true);

            if(!AudioPlayManager.getInstance().isAudioPlaying()){
                AudioPlayManager.getInstance().startPlay(currentPlayChatInfosBean);
            }
        }
    }

    private void stopPlay() {
        if(currentPlayChatInfosBean != null){
            imgPlay.setImageResource(R.drawable.use_icon_zanting);
            imgHead.setRotate(false);

            if(AudioPlayManager.getInstance().isAudioPlaying()){
                AudioPlayManager.getInstance().pausePlay();
            }
        }
    }

    private boolean gotoActivity(Class clazz){
        if(UserManager.getInstance().isLogin(getActivity())){
            Intent intent = new Intent(getActivity(),clazz);
            getActivity().startActivity(intent);
            return true;
        }

        return false;
    }

}
