package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.HashMap;

import butterknife.OnClick;

/**
 * Describe:新建课程下一步
 * Created by ALing on 2017/3/16 .
 */

public class CreateTopicNextByCircleActivity extends BaseTitleActivity implements
        View.OnClickListener, CreateTopicContract.View {
    private static final String TAG = " b ";

    private boolean isCheck;
    private String  subtitle;    //课程主题
    private String  starttime;   //开始时间
    private String  chatway;     //课堂形式
    private String  chatTypeSonId;      //课堂子类型ID
    private String  token;
    private String chattype = "0";    //课堂类型
    private String id;          //课堂间ID
    private String chatSeries;  //系列课程id

    private String facePic;             //课堂封面
    private String mainSpeaker;         //主讲人名字
    private String speakerIntroduce;    //主讲人介绍
    private String desc;                //课程简介

    private CreateTopicContract.Presenter mPresenter;
    private String                        mGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic_next_bycircle);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_create_topic));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
        new CreateTopicPresenter(this);
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
            id = UserManager.getInstance().getUser().getChatRoomId();
        } else {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
        }

        subtitle = getIntent().getStringExtra("subtitle");
        starttime = getIntent().getStringExtra("starttime");
        chatway = getIntent().getStringExtra("chatway");
        chatTypeSonId = getIntent().getStringExtra("chatTypeSonId");
        chatSeries = getIntent().getStringExtra("chatSeries");

        facePic = getIntent().getStringExtra("facePic");
        mainSpeaker = getIntent().getStringExtra("mainSpeaker");
        speakerIntroduce = getIntent().getStringExtra("speakerIntroduce");
        desc = getIntent().getStringExtra("desc");

        mGroupId = getIntent().getStringExtra("groupIds");

    }

    @OnClick({R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //完成
            case R.id.btn_finish:
                createFinish();
                break;

        }
    }

    private void createFinish() {
        WindowUtil.closeInputMethod(this);
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("chatRoom", id);
        map.put("subtitle", subtitle);
        map.put("starttime", starttime);
        map.put("chatway", chatway);
        map.put("chattype", chattype);
        map.put("facePic", facePic);                    //课程封面
        map.put("mainSpeaker", mainSpeaker);            //主讲人名字
        map.put("speakerIntroduce", speakerIntroduce);  //主讲人介绍
        map.put("desc", desc);                          //课程简介

        if (chatSeries != null) map.put("chatSeries", chatSeries);

        if (!TextUtils.isEmpty(mGroupId)) {
            map.put("groupIds", "" + mGroupId);
        }
        mPresenter.createLiveTopic(map);
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void createLiveResult(CreateLiveTopicBean bean) {
        ToastUtil.showShort(this, "创建课程成功");     //进入课程主页
        EventBusUtil.post(new CreateLiveTopicBean());
        finish();
        LiveIntroActivity.startActivity(this, bean.getId());//这里 要传的是 课程ID  不是课堂间id
    }

    @Override
    public void setPresenter(CreateTopicContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {
        //        mPresenter.createLiveTopic(map);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
