package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.event.EventLive;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.live.create.CreateLiveActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse.CreateSeriesCourseActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.LiveBgSettingActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify.SeriesClassifyActivity;
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.news.MineArticleActivity;
import com.gxtc.huchuan.ui.mine.news.applyauthor.ApplyAuthorActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:我的 > 课堂 > 课堂后台
 * Created by ALing on 2017/3/13 .
 */

public class DirectSeedingBackgroundActivity extends BaseTitleActivity {
    @BindView(R.id.iv_head)
    ImageView mIvHead;
    @BindView(R.id.tv_room_name)
    TextView mTvRoomName;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.btn_create_topic)
    Button btnCreateTopic;
    @BindView(R.id.btn_create_series_course)
    Button btnCreateSeriesCourse;
    @BindView(R.id.ll_button)
    LinearLayout llButton;

    private LiveRoomBean bean;
    private Subscription sub;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_seeding_bacground);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_personal_direct_seeding_title));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        EventBusUtil.register(this);
        getLiveRoomData();
        if ("0".equals(UserManager.getInstance().getIsAnchor())){
            showDialog();
        }else if ("2".equals(UserManager.getInstance().getIsAnchor())){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("温馨提示：");
            dialog.setMessage("您现在还不是讲师，您提交的申请在审核中！");
            dialog.setCancelable(false);
            dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DirectSeedingBackgroundActivity.this.finish();
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
    }
    private AlertDialog mDialog;

    private void showDialog() {
        mDialog = DialogUtil.createDialog(this, "提示", "您还不是主播不能创建课程，请申请讲师！", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        DirectSeedingBackgroundActivity.this.finish();
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        GotoUtil.goToActivity(DirectSeedingBackgroundActivity.this,ApplyAuthorActivity.class);
                        DirectSeedingBackgroundActivity.this.finish();
                        mDialog.dismiss();
                    }

                });
        mDialog.show();
    }

    @OnClick({R.id.iv_head,R.id.btn_create_topic, R.id.btn_create_series_course, R.id.tv_live_home, R.id.tv_live_setting,
            R.id.tv_class_classification,R.id.tv_use_tutorial})
    public void onClick(View view) {
        switch (view.getId()) {
            //新建课程
            case R.id.btn_create_topic:
                GotoUtil.goToActivity(this, CreateTopicActivity.class);
                break;
            //创建系列课
            case R.id.btn_create_series_course:
                GotoUtil.goToActivity(this, CreateSeriesCourseActivity.class);
                break;
            //课堂主页
            case R.id.tv_live_home:
                String chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
                LiveHostPageActivity.startActivity(this,"1", chatRoomId);
                break;
            //课堂设置
            case R.id.iv_head:
            case R.id.tv_live_setting:
                GotoUtil.goToActivity(this, LiveBgSettingActivity.class);
                break;
            //系列课分类
            case R.id.tv_class_classification:
                GotoUtil.goToActivity(this, SeriesClassifyActivity.class);
                break;
            case R.id.tv_use_tutorial:
                gotoWebView(Constant.ABOUTLINK+"3",getString(R.string.title_join_us));
                break;
        }
    }

    private void goToActivity(Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            if (!"0".equals(UserManager.getInstance().getChatRoomId())
                    || !"".equals(UserManager.getInstance().getChatRoomId()))
                GotoUtil.goToActivity(this, toClass);
            else
                GotoUtil.goToActivity(this, CreateLiveActivity.class);
        } else
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

    }

    private void gotoWebView(String url,String title) {
        Intent intent = new Intent(this, CommonWebViewActivity.class);
        intent.putExtra("web_url", url);
        intent.putExtra("web_title", title);
        startActivity(intent);
    }

    /**
     * 获取课堂信息
     */
    private void getLiveRoomData() {
        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            chatRoomId = UserManager.getInstance().getUser().getChatRoomId();
        }

        sub = LiveApi.getInstance().getLiveRoom(chatRoomId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<LiveRoomBean>>(new ApiCallBack<LiveRoomBean>() {

                    @Override
                    public void onSuccess(LiveRoomBean data) {
                        getBaseLoadingView().hideLoading();

                        bean = (LiveRoomBean) data;
                        mTvRoomName.setText(bean.getRoomname());
                        ImageHelper.loadImage(DirectSeedingBackgroundActivity.this, mIvHead, bean.getHeadpic(), R.drawable.person_icon_head_logo_62);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        getBaseLoadingView().hideLoading();
                        LoginErrorCodeUtil.showHaveTokenError(DirectSeedingBackgroundActivity.this, errorCode, message);
                    }
                }));

    }

    //修改课堂图标
    @Subscribe
    public void onEvent(EventLive bean) {
        String headpic = bean.getHeadpic();
        ImageHelper.loadHeadIcon(this, mIvHead, headpic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
