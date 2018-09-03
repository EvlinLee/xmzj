package com.gxtc.huchuan.ui.circle.home;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.widget.MsgView;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LightStatusBarUtils;
import com.gxtc.commlibrary.utils.RomUtils;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CirclePagerAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.event.EventNewDynamicMsg;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.RelayHelper;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.gxtc.huchuan.Constant.STATUE_REFRESH_DYNIMIC;
import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;


/**
 * 来至  苏修伟  on 2018/5/2
 * 圈子首页
 */
public class MainCircleHomeFragment extends BaseTitleFragment implements View.OnClickListener, ViewPager.OnPageChangeListener{



    @BindView(R.id.vp_maincricle)      ViewPager                  mViewPager;
    @BindView(R.id.circle_tab)          MPagerSlidingTabStrip      pssTab;
    @BindView(R.id.headRightButton)    TextView                   headRightButton;
    @BindView(R.id.rl_circle_head)     RelativeLayout             head;
    @BindView(R.id.msgView)             MsgView                    msg;

    private CircleHomeFragment                  circleHomeFragment;
    private CirclePublicFragment                circlePublicFragment;
    private CirclePagerAdapter                  mAdapter;


    private List<Fragment> fragments = new ArrayList<>();
    private String[] titledata = {"推荐","关注"};


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_main_circle_home, container, false);

//        EventBusUtil.register(this);
//        View    header = getLayoutInflater().inflate(R.layout.view_circle_head,getBaseHeadView().getParentView(),false);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header.getLayoutParams();
//        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.RIGHT_OF,R.id.headBackButton);
//        params.addRule(RelativeLayout.LEFT_OF,R.id.headRightLinearLayout);
//        getBaseHeadView().getParentView().addView(header);
//        getBaseHeadView().showHeadRightButton("发动态", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserManager.getInstance().isLogin()) {
//                    showSelectDialog();
//                } else {
//                    GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
//                }
//            }
//        });

        return view;
    }

    @Override
    public void initData() {
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) head.getLayoutParams();
//        layoutParams.topMargin = WindowUtil.getStatusHeight(getContext());
        switch (RomUtils.getLightStatausBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
            case RomUtils.AvailableRomType.FLYME:
            case RomUtils.AvailableRomType.ANDROID_NATIVE://6.0以上
                head.setPadding(0, WindowUtil.getStatusHeight(getContext()), 0 , 0);
                break;

            case RomUtils.AvailableRomType.NA://6.0以下

                break;
        }
//        head.setPadding(0, WindowUtil.getStatusHeight(getContext()), 0, 0);
        circleHomeFragment = new CircleHomeFragment();
        circlePublicFragment = new CirclePublicFragment();
        fragments.add(circleHomeFragment);
        fragments.add(circlePublicFragment);
        mAdapter = new CirclePagerAdapter(getChildFragmentManager(), fragments, titledata);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(titledata.length);
        pssTab.setViewPager(mViewPager);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) msg.getLayoutParams();
        layoutParams.setMargins(-pssTab.getTabPaddingLeftRight(), WindowUtil.dip2px(getContext(), 10), 0, 0 );
        layoutParams.width = WindowUtil.dip2px(getContext(), 8);
        layoutParams.height = WindowUtil.dip2px(getContext(), 8);
        msg.setLayoutParams(layoutParams);
    }


    @Override
    public void initListener() {
        EventBusUtil.register(this);
        headRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().isLogin()) {
                    showSelectDialog();
                } else {
                    GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
                }
            }
        });

        pssTab.setOnPageChangeListener(this);
        pssTab.setOnItemClicklistener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh((Integer) v.getTag());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_cicle:
                break;


        }
    }


    private ListDialog mListDialog;

    public void showSelectDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this.getContext(), new String[]{"文字", "图片", "视频"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(),
                            IssueDynamicActivity.class);
                    switch (position) {
                        case 0://文字
                            intent.putExtra("select_type", "3");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                        case 1://拍摄或相册
                            intent.putExtra("select_type", "2");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                        case 2://视频
                            intent.putExtra("select_type", "1");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                    }
                }
            });
        }
        mListDialog.show();
    }

    @Subscribe
    public void onEvent(EventNewDynamicMsg bean) {
        if(msg.getVisibility() != View.VISIBLE){
            msg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //圈子动态发送成功
        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            if(mViewPager.getCurrentItem() == 1) {
                circlePublicFragment.onActivityResult(333, 333, null);
                msg.setVisibility(View.INVISIBLE);
            }else{
                mViewPager.setCurrentItem(1);
            }
        }else if(requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK){
            //转发时做判断是在第一个界面还是第二个界面，否则会转发了两次
            if( mViewPager.getCurrentItem() == 0 ){
                circleHomeFragment.onActivityResult(requestCode, resultCode, data);
            }
            if( mViewPager.getCurrentItem() == 1 ){
                circlePublicFragment.onActivityResult(requestCode, resultCode, data);
            }
        }else{
            circleHomeFragment.onActivityResult(requestCode, resultCode, data);
            circlePublicFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //有新动态刷新关注的动态
        refresh(position);
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void refresh(int position){
        if(position == 1 && msg.getVisibility() == View.VISIBLE){
            circlePublicFragment.onActivityResult(333, 333, null);
            msg.setVisibility(View.INVISIBLE);
        }
    }
}



