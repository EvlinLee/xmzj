package com.gxtc.huchuan.ui.mine.classroom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.live.apply.ApplyLecturerActivity;
import com.gxtc.huchuan.ui.live.create.CreateLiveActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.DirectSeedingBackgroundActivity;
import com.gxtc.huchuan.ui.mine.classroom.history.MyHistoryActivity;
import com.gxtc.huchuan.ui.mine.classroom.profitList.profit.ProfitActivity;
import com.gxtc.huchuan.ui.mine.classroom.purchaserecord.PurchaseRecordActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的  课堂
 * Created by ALing on 2017/3/9.
 */

public class MyClassRoomFragment extends BaseTitleFragment implements View.OnClickListener {
    private static final String TAG = MyClassRoomFragment.class.getSimpleName();
    @BindView(R.id.iv_apply_anchor)
    ImageView mIvAnchor;
    @BindView(R.id.tv_direct_seeding_home)
    TextView mTvDirectSeedingHome;
    @BindView(R.id.tv_direct_seeding_background)
    TextView mTvDirectSeedingBackground;
    @BindView(R.id.ll_home_background)          //申请成为主播成功后显示
            LinearLayout mLlHomeBackground;
    @BindView(R.id.iv_create_liveroom)
    ImageView mIvCreateLiveroom;

    private User user;
    private int status;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_my_classroom, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
//        getUserMessage();
        EventBusUtil.register(this);
        if (UserManager.getInstance().isLogin()){
            user = UserManager.getInstance().getUser();
            String isAnchor = user.getIsAnchor();
            Log.d(TAG, "isAnchor: "+isAnchor);
            if ("0".equals(isAnchor)){
                mIvAnchor.setVisibility(View.VISIBLE);
                mIvCreateLiveroom.setVisibility(View.GONE);
                mLlHomeBackground.setVisibility(View.GONE);
            }else {
                if ("0".equals(user.getChatRoomId())){
                    mIvCreateLiveroom.setVisibility(View.VISIBLE);
                }
                mIvAnchor.setVisibility(View.GONE);
                mLlHomeBackground.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void initListener() {
        super.initListener();

    }

    @OnClick({R.id.iv_apply_anchor, R.id.iv_create_liveroom,
            R.id.tv_direct_seeding_home, R.id.tv_direct_seeding_background,
            R.id.tv_purchase_record, R.id.tv_class_profit,R.id.tv_data_statistics,
            R.id.tv_reported_course,R.id.tv_course_distribution,

    })
    public void onClick(View v) {
        switch (v.getId()) {
            //申请成为主播
            case R.id.iv_apply_anchor:
                if (status == EventLoginBean.TOKEN_OVERDUCE){       //token 过期
                    GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
                }else {         //未登录
                    goToActivity(ApplyLecturerActivity.class);
                }
                break;
            //一键创建课堂
            case R.id.iv_create_liveroom:
                GotoUtil.goToActivity(getActivity(), CreateLiveActivity.class);
                break;
           /* case R.id.tv_purchase_record:
                goToActivity(PurchaseRecordActivity.class);
                break;
            //我的消息
            case R.id.tv_my_message:
                GotoUtil.goToActivity(getActivity(), MyMessageActivity.class);
                break;
            //历史记录
           /* case R.id.tv_history_record:
                GotoUtil.goToActivity(getActivity(), MyHistoryActivity.class);
                break;*/
            //个人直播主页
            case R.id.tv_direct_seeding_home:
                if (UserManager.getInstance().isLogin()){

                    String chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
                    LiveHostPageActivity.startActivity(getActivity(),"1",chatRoomId);
                }
                break;
            //直播后台
            case R.id.tv_direct_seeding_background:
                goToActivity(DirectSeedingBackgroundActivity.class);
                break;
            //购买记录
            case R.id.tv_purchase_record:
                goToActivity(PurchaseRecordActivity.class);
                break;
            //课程收益
            case R.id.tv_class_profit:
                goToProfit();
                break;
            //数据统计
            case R.id.tv_data_statistics:

                break;
            //已报课程
            case R.id.tv_reported_course:
                goToActivity(MyHistoryActivity.class);
                break;
            //课程分销
            case R.id.tv_course_distribution:
//                goToDistribution();
                break;

        }
    }

    private void goToProfit() {
        if (UserManager.getInstance().isLogin()){
            ProfitActivity.startActivity(getActivity(), "1");
        }else {
            GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
        }
    }

//    private void goToDistribution() {
//        if (UserManager.getInstance().isLogin()){
//            DistributionActivity.startActivity(getActivity(), "1");
//        }else {
//            GotoUtil.goToActivity(getActivity(),LoginAndRegisteActivity.class);
//        }
//    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()){
            GotoUtil.goToActivity(getActivity(), toClass);
        }else {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    }

    //申请成为主播成功
    @Subscribe
    public void onEvent(EventEditInfoBean bean) {
        User user = UserManager.getInstance().getUser();
        if (bean.status == EventEditInfoBean.ISANCHOR) {    //申请主播
            Log.d("tag", "onEvent: " + bean.status);
            mIvAnchor.setVisibility(View.GONE);
            mIvCreateLiveroom.setVisibility(View.VISIBLE);
            mLlHomeBackground.setVisibility(View.GONE);
        }else if (bean.status == EventEditInfoBean.CREATELIVE){     //一键创建课堂
            mIvAnchor.setVisibility(View.GONE);
            mIvCreateLiveroom.setVisibility(View.GONE);
            mLlHomeBackground.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || status == EventLoginBean.THIRDLOGIN) {
            if (UserManager.getInstance().isLogin()) {
                user = UserManager.getInstance().getUser();
               if ("1".equals(user.getIsAnchor())){
                   mIvAnchor.setVisibility(View.GONE);
                   mIvCreateLiveroom.setVisibility(View.GONE);
                   mLlHomeBackground.setVisibility(View.VISIBLE);
               }else {
                   mIvAnchor.setVisibility(View.VISIBLE);
                   mIvCreateLiveroom.setVisibility(View.GONE);
                   mLlHomeBackground.setVisibility(View.GONE);
               }
            }
        } else if (bean.status == EventLoginBean.EXIT || bean.status == EventLoginBean.TOKEN_OVERDUCE) {      //退出登录
            status = bean.status;
            mIvAnchor.setVisibility(View.VISIBLE);
            mIvCreateLiveroom.setVisibility(View.GONE);
            mLlHomeBackground.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
