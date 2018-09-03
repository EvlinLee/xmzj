package com.gxtc.huchuan.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.event.EventHandleVideoBean;
import com.gxtc.huchuan.dialog.ListBottomDialog;
import com.gxtc.huchuan.ui.common.CommonVideoContract;
import com.gxtc.huchuan.ui.common.CommonVideoPresenter;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/8.
 * 自定义UI可以说是用处最多的，比如标题的显示与否，添加分享按钮，全屏显示电量等任何的DEMO UI不一样的修改都可以总结为自定义UI。
 * 和自定义相关的工作，最主要是先继承JCVideoPlayerStandard！！！
 * 修改xml

    在R.layout.jc_layout_standard的基础上，添加自己想要的控制，不需要的控件不能删除，如果删除代码findViewById找不见会报错，只能隐藏。

    取得新控件的引用

    复写getLayoutId函数，设置自己的xml布局，全屏和非全屏是一个xml布局，只是有的控件全屏显示，非全屏隐藏。

    复写init函数，findViewById找到自己添加的控件。

    操作控件

    根据自己的需要，复写进入状态的函数，代码中应该是不厌其烦的分别控制每个状态的控件，这样做思路清晰，不会出现遗漏。

    onStateNormal
    onStatePreparing
    onStatePreparingChangingUrl
    onStatePlaying
    onStatePause
    onStatePlaybackBufferingStart
    onStateError
    onStateAutoComplete
 */

public class ExpandVideoPlayer extends JZVideoPlayerStandard implements CommonVideoContract.View{

    @BindView(R.id.iv_share) ImageButton imgShare;

    private ListBottomDialog mBottomDialog;

    private CommonVideoContract.Presenter videoPresenter;

    public ExpandVideoPlayer(Context context) {
        super(context);
    }

    public ExpandVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_expand_video;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        ButterKnife.bind(this, this);
    }


    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if(objects.length >= 2){
            new CommonVideoPresenter(this, url, (String) objects[1]);
        }
        if(batteryTimeLayout != null){
            batteryTimeLayout.setVisibility(GONE);
        }
    }

    /**
     * 设置播放数据源
     * @param dataSourceObjects
     * @param defaultUrlMapIndex
     * @param screen
     * @param objects  一个可以传递任何对象的数组， 数组的第一个参数是要显示的视频标题，可以传空字符串
     *                 第二个参数是视频的封面 一定要传，不然视频全屏的时候 不能分享跟收藏
     */
    @Override
    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSourceObjects, defaultUrlMapIndex, screen, objects);
        if(dataSourceObjects != null && dataSourceObjects.length > 0 && dataSourceObjects[0] instanceof LinkedHashMap){
            LinkedHashMap map = (LinkedHashMap) dataSourceObjects[0];
            new CommonVideoPresenter(this, (String) map.get(URL_KEY_DEFAULT), (String) objects[1]);
        }
        if(batteryTimeLayout != null){
            batteryTimeLayout.setVisibility(GONE);
        }
    }

    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                imgShare.setVisibility(INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                imgShare.setVisibility(VISIBLE);
                break;
        }
    }

    @OnClick({R.id.iv_share})
    public void onClick(View v){
        switch (v.getId()){
            //分享
            case R.id.iv_share:
                showBottomDialog();
                return;

            case cn.jzvd.R.id.fullscreen:
                break;
        }
        super.onClick(v);
    }


    private void showBottomDialog() {
        if(mBottomDialog == null){
            String [] items = { "收藏", "发送给朋友", "保存视频"};
            mBottomDialog = new ListBottomDialog();
            mBottomDialog.setDatas(items);
            mBottomDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(videoPresenter != null){
                        switch (position){
                            case 0:
                                videoPresenter.collectVideo((Activity) getContext());
                                break;

                            case 1:
                                videoPresenter.shareVideoToFriends((Activity) getContext());
                                break;

                            case 2:
                                videoPresenter.saveVideo(getContext());
                                break;
                        }
                    }
                    mBottomDialog.dismiss();
                }
            });
        }
        if(getContext() instanceof FragmentActivity){
            FragmentActivity activity = (FragmentActivity) getContext();
            mBottomDialog.show(activity.getSupportFragmentManager(), ListBottomDialog.class.getSimpleName());
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtil.unregister(this);
    }


    @Override
    public void setVideoPrsenter(CommonVideoContract.Presenter prsenter) {
        this.videoPresenter = prsenter;
    }
}
