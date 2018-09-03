package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DragAdapter;
import com.gxtc.huchuan.adapter.OtherAdapter;
import com.gxtc.huchuan.bean.dao.NewChannelItem;
import com.gxtc.huchuan.data.ChannelManager;
import com.gxtc.huchuan.widget.DragGrid;
import com.gxtc.huchuan.widget.OtherGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sjr on 2017/2/9.
 * 频道选择页
 * http://blog.csdn.net/vipzjyno1/article/details/25005851
 */

public class ChannelActivity extends BaseTitleActivity implements AdapterView.OnItemClickListener {

    public static String TAG = "ChannelActivity";
    /**
     * 用户栏目的GRIDVIEW
     */
    @BindView(R.id.userGridView)
    DragGrid userGridView;
    /**
     * 其它栏目的GRIDVIEW
     */
    @BindView(R.id.otherGridView)
    OtherGridView otherGridView;

    /**
     * 用户栏目对应的适配器，可以拖动
     */
    DragAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    OtherAdapter otherAdapter;
    /**
     * 其它栏目列表
     */
    ArrayList<NewChannelItem> otherChannelList = new ArrayList<>();
    /**
     * 用户栏目列表
     */
    ArrayList<NewChannelItem> userChannelList = new ArrayList<>();
    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

    }

    @Override
    public void initData() {
        userChannelList = (ArrayList<NewChannelItem>) ChannelManager.getInstance().getUserChannel();
        otherChannelList = (ArrayList<NewChannelItem>) ChannelManager.getInstance().getOtherChannel();
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
        getBaseHeadView().showTitle(getResources().getString(R.string.title_channels_select));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                saveChannel();
//                List<NewChannelItem> userChannel = ChannelManager.getInstance().getUserChannel();
//                for (int i = 0; i < userChannel.size(); i++) {
//                    LogUtil.printD("s:" + userChannel.get(i).getName());
//                }
//                if (userAdapter.isListChanged()) {
//                    Intent intent = new Intent(ChannelActivity.this, NewsFragment.class);
//                    setResult(NewsFragment.CHANNELRESULT, intent);
//                    finish();
//                } else {
//                    finish();
//                }
            }
        });
        getBaseHeadView().showHeadRightButton("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userAdapter.isListChanged()) {
                    for (int i = 0; i < userAdapter.getChannnelLst().size(); i++) {
                        LogUtil.printD("条目：" + userAdapter.getChannnelLst().get(i).getName());
                    }
                    ChannelManager.getInstance().deleteAllChannel();
                    ChannelManager.getInstance().saveUserChannel(userAdapter.getChannnelLst());
                    List<NewChannelItem> userChannel = ChannelManager.getInstance().getUserChannel();
                    for (int i = 0; i < userChannel.size(); i++) {
                        LogUtil.printD("后条目：" + userChannel.get(i).getName());
                    }
                    ChannelManager.getInstance().saveOtherChannel(otherAdapter.getChannnelLst());
//                    Intent intent = new Intent(ChannelActivity.this, NewsFragment.class);
//                    setResult(NewsFragment.CHANNELRESULT, intent);
                    finish();
                } else {
                    finish();
                }
            }
        });
    }


    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0 && position != 1) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final NewChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        otherAdapter.setVisible(false);
                        //添加到最后一个
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final NewChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final NewChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    private void saveChannel() {
        ChannelManager.getInstance().deleteAllChannel();
        ChannelManager.getInstance().saveUserChannel(userAdapter.getChannnelLst());
        ChannelManager.getInstance().saveOtherChannel(otherAdapter.getChannnelLst());
    }

//    @Override
//    public void onBackPressed() {

//        saveChannel();
//        if (userAdapter.isListChanged()) {
//            Intent intent = new Intent(ChannelActivity.this, NewsFragment.class);
//            setResult(NewsFragment.CHANNELRESULT, intent);
//            finish();
//            Log.d(TAG, "数据发生改变");
//        } else {
//            super.onBackPressed();
//        }

//    }
}
