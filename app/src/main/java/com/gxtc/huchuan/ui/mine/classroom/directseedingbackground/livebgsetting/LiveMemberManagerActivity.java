package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveMemberManagerAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatInviteUrlBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Describe:直播间嘉宾管理
 */

public class LiveMemberManagerActivity extends BaseTitleActivity implements View.OnClickListener, LiveMemberManagerAdapter.ManageResult {

    private RelativeLayout mRlInviteManager;
    private RelativeLayout mRlInviteTeacher;
    private RelativeLayout mRlInviteStudent;

    public static int REFRESH = 666;

    @BindView(R.id.rc_list)
    RecyclerView mRcList;

    private HashMap<String, String> map;
    private int start = 0;
    private String mjoinType;  //0:普通用户， 1：管理员，  2：讲师。  3：免费邀请学员  4：免费邀请系列课
    private String Type;       //0:邀请讲师、管理员、成员   1：显示成员列表
    private View headView;
    private View view;
    private ImageView mIvHead;
    private TextView mTvName;
    private TextView mTvSubName;
    private List<ChatJoinBean.MemberBean> list;
    private LiveMemberManagerAdapter adapter;
    private ChatInfosBean bean;
    private SeriesPageBean seriesBean;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private boolean isShowFriend = true;
    private int isManager = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_member_manager);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle("参课成员");
        getBaseHeadView().showBackButton(this);
        headView = View.inflate(this, R.layout.item_live_manager, null);
        mIvHead = ((ImageView) headView.findViewById(R.id.iv_head));
        mTvName = ((TextView) headView.findViewById(R.id.tv_name));
        mTvSubName = ((TextView) headView.findViewById(R.id.tv_sub_name));
        isShowFriend = getIntent().getBooleanExtra("showfriend", true);
        isManager = getIntent().getIntExtra("ismanager", -1);
        bean = (ChatInfosBean) getIntent().getSerializableExtra("bean");
        Type = getIntent().getStringExtra("type");
        seriesBean = (SeriesPageBean) getIntent().getSerializableExtra("seriesBean");


        initRecyclerView();

    }

    private void initRecyclerView() {

        if (bean.getRoleType().equals(ChatInfosBean.ROLE_CREATER) || bean.getRoleType().equals(ChatInfosBean.ROLE_MANAGER)) {
            view = View.inflate(this, R.layout.activity_member_header, null);
            mRcList.addHeadView(view);
            mRlInviteManager = (RelativeLayout) view.findViewById(R.id.rl_invite_manager);
            mRlInviteTeacher = (RelativeLayout) view.findViewById(R.id.rl_invite_teacher);
            mRlInviteStudent = (RelativeLayout) view.findViewById(R.id.rl_invite_student);
            view.findViewById(R.id.rl_invite_manager).setOnClickListener(this);
            view.findViewById(R.id.tv_inivite_teacher).setOnClickListener(this);
            view.findViewById(R.id.rl_invite_student).setOnClickListener(this);
            mRlInviteManager.setVisibility(bean.isSelff() ? View.VISIBLE : View.GONE);
            if (isManager == 1) {  //显示管理员
                getBaseHeadView().showTitle("课程管理成员");
                mRlInviteManager.setVisibility(bean.isSelff() ? View.VISIBLE : View.GONE);
                mRlInviteTeacher.setVisibility(View.VISIBLE);
                mRlInviteStudent.setVisibility(View.GONE);

            } else if (isManager == 2) {   //显示参课成员
                mRlInviteManager.setVisibility(View.GONE);
                mRlInviteTeacher.setVisibility(View.GONE);
                mRlInviteStudent.setVisibility(View.VISIBLE);
            }
        }


        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initListener() {
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                start += 15;
                getData();
            }
        });
    }

    public void getData() {
        map = getMap();
//        map.put("searchKey","");
        map.put("loadTime", System.currentTimeMillis() + "");
//        if(Type.equals("0")){
//            adapter = new LiveMemberManagerAdapter(LiveMemberManagerActivity.this, new ArrayList< ChatJoinBean.MemberBean>(), R.layout.item_live_member_manager, 0);
//            mRcList.setAdapter(adapter);
//            return;
//        }
        getManagerList(map, new ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>() {


            @Override
            public void onSuccess(ArrayList<ChatJoinBean.MemberBean> data) {
                if (mRcList == null || data == null) return;

                if (data.size() == 0) {
                    mRcList.loadFinish();
                }
                list = new ArrayList<>();
                int size = data.size();
                list.addAll(data);
                if (adapter == null) {

                    adapter = new LiveMemberManagerAdapter(LiveMemberManagerActivity.this, list, R.layout.item_live_member_manager, size, map, mRcList);
                    adapter.setManageResult(LiveMemberManagerActivity.this);
                    mRcList.setAdapter(adapter);
                    adapter.setOnItemClickLisntener(new BaseRecyclerAdapter.OnItemClickLisntener() {
                        @Override
                        public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {

                            if (!adapter.getList().get(position).getUserCode().equals(UserManager.getInstance().getUserCode())) {
                                PersonalInfoActivity.startActivity(LiveMemberManagerActivity.this, adapter.getList().get(position).getUserCode());
                            }
                        }
                    });
                } else {
                    mRcList.changeData(list, adapter);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(LiveMemberManagerActivity.this, message);
            }
        });
    }

    @Override
    public void initData() {
        map = new HashMap<>();
        if (isManager != 1 && isManager != 2) {
            getBaseHeadView().showTitle("邀请新成员");
            adapter = new LiveMemberManagerAdapter(LiveMemberManagerActivity.this, new ArrayList<ChatJoinBean.MemberBean>(), R.layout.item_live_member_manager, 0, map, mRcList);
            mRcList.setAdapter(adapter);

            return;
        }
        getData();

    }

    private HashMap<String, String> getMap() {
        if (!isShowFriend) {
            list = new ArrayList<>();
        }
        map = new HashMap<>();
        map.put("joinType", bean.getRoleType());
        if (seriesBean != null) {
            map.put("type", "2");
            map.put("chatId", seriesBean.getId());

        } else {
            map.put("type", "1");
            map.put("chatId", bean.getId());
        }
        if (isManager == 1) {     //管理员

            map.put("userType", "2");

        } else if (isManager == 2) {

            map.put("userType", "3");

        }
        map.put("start", "0");
        map.put("token", UserManager.getInstance().getToken());
        map.put("start", start + "");
        map.put("pageSize", "15");
        return map;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            //邀请管理员
            case R.id.rl_invite_manager:
                mjoinType = "1";
                InvitedGuestsActivity.startActivity(this, bean.getId(), mjoinType, bean.getSubtitle(), bean.getFacePic());
                break;

            //邀请讲师
            case R.id.tv_inivite_teacher:
                mjoinType = "2";
                InvitedGuestsActivity.startActivity(this, bean.getId(), mjoinType, bean.getSubtitle(), bean.getFacePic());
                break;

            //免费邀请学员
            case R.id.rl_invite_student:
                if (seriesBean == null) {
                    mjoinType = "3";
                    InvitedGuestsActivity.startActivity(this, bean.getId(), mjoinType, bean.getSubtitle(), bean.getFacePic());
                } else {

                    mjoinType = "4";
                    InvitedGuestsActivity.startActivity(this, seriesBean.getId(), mjoinType,
                            seriesBean.getSeriesname(), seriesBean.getHeadpic());

                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        mCompositeSubscription.unsubscribe();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    private void getManagerList(HashMap<String, String> map, ApiCallBack callBack) {
        mCompositeSubscription.add(
                LiveApi.getInstance().getlistJoinMember(map).subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>>(callBack)));
    }

    @Override
    public void result() {
        setResult(REFRESH);
    }
}
