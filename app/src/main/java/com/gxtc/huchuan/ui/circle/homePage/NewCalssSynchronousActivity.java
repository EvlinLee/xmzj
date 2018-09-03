package com.gxtc.huchuan.ui.circle.homePage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SynchronousCircleAdater;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomContract;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomPresenter;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.SearchView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zzg on 2017/11/22.
 */

public class NewCalssSynchronousActivity extends BaseTitleActivity implements ClassroomContract.View {
    private static final String TAG = "ClassRoomAuditedFragmen";

    @BindView(R.id.recyclerView) RecyclerView       mRecyclerView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout mSwipelayout;

    private int mCircleid;
    private int     start                   = 0;
    private int     GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    private int     LOGINREQUEST            = 1 << 4;
    private boolean isRefresh               = true;

    public  String                   mUserCode;
    private SynchronousCircleAdater  mAdapter;
    private List<ClassAndSeriseBean> tempData;
    ClassroomContract.Presenter mPresenter;
    HashMap<String, String> map = new HashMap<>();
    private  String searchKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
    }

    @Override
    public void initView() {
        EventBusUtil.register(this);
        mCircleid = getIntent().getIntExtra("circleid", -1);
        getBaseHeadView().showTitle("课堂");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.person_circle_manage_icon_add,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NewCalssSynchronousActivity.this, CreateTopicActivity.class);
                        intent .putExtra("groupIds",mCircleid);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void initListener() {
        mSwipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showReLoad();
                isRefresh = true;
                if(TextUtils.isEmpty(searchKey)){
                    start = 0;
                    getDatas();
                }else {
                    mPresenter.searchClass(searchKey,mCircleid,true);
                }
            }
        });
    }

    @Override
    public void initData() {
        tempData = new ArrayList<>();

        mUserCode = getIntent().getStringExtra("userCode");
        new ClassroomPresenter(this);
        mSwipelayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isRefresh = false;
                if(TextUtils.isEmpty(searchKey)){
                    start = start + 15;
                    getDatas();
                }else {
                    mPresenter.searchClass(searchKey,mCircleid,false);
                }
            }
        });

        SearchView searchView = new SearchView(this);
        mRecyclerView.addHeadView(searchView);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKey = query;
                isRefresh = true;
                if(TextUtils.isEmpty(searchKey)){
                    mRecyclerView.notifyChangeData(tempData,mAdapter);
                }else {
                    mPresenter.searchClass(searchKey,mCircleid,true);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey = newText;
                isRefresh = true;
                if(TextUtils.isEmpty(searchKey)){
                    mRecyclerView.notifyChangeData(tempData,mAdapter);
                }else {
                    mPresenter.searchClass(searchKey,mCircleid,true);
                }
                return false;
            }
        });

        initAdater();
        getBaseLoadingView().showLoading();
        getDatas();
    }


    public void getDatas() {
        if (mCircleid != 0)
        map.put("start", start + "");
        map.put("token", UserManager.getInstance().getToken());
        map.put("relType", "2");
        map.put("isAudit", "1");
        map.put("groupId", "" + mCircleid);
        mPresenter.getUnauditGroup(map);
    }

    @Override
    public void tokenOverdue() {}

    @Override
    public void showLiveRoom(List<ChatInfosBean> datas) {}

    @Override
    public void showSearChLiveRoom(List<SearchBean> datas) {}

    @Override
    public void showSearData(List<ClassAndSeriseBean> datas) {
        getBaseLoadingView().hideLoading();
        if (isRefresh) {
            mSwipelayout.setRefreshing(false);
            mRecyclerView.notifyChangeData(datas, mAdapter);
        } else {
            mRecyclerView.changeData(datas, mAdapter);
        }
    }

    private void initAdater() {
        mAdapter = new SynchronousCircleAdater(this, new ArrayList<ClassAndSeriseBean>(), R.layout.item_live_list, true);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (mAdapter.getList().get(position).getType()) {
                    //课程
                    case "2":
                        ChatInfosBean bean = (ChatInfosBean) mAdapter.getData("2",
                                mAdapter.getList().get(position).getData());
                        ChatItemClick(bean);
                        break;
                    //系列课
                    case "6":
                        ChatInfosBean bean1 = new ChatInfosBean();
                        SeriesPageBean bean2 = (SeriesPageBean) mAdapter.getData("6",
                                mAdapter.getList().get(position).getData());
                        bean1.setChatSeries(bean2.getId());
                        bean1.setChatSeriesData(bean2);
                        ChatItemClick(bean1);
                        break;
                }

            }
        });
    }

    @Override
    public void showlaodMoreLiveRoom(List<ChatInfosBean> datas) {}

    @Override
    public void showUnauditGroup(List<ClassAndSeriseBean> datas) {
        getBaseLoadingView().hideLoading();
        if(datas != null && datas.size() > 0){
            if (isRefresh) {
                mSwipelayout.setRefreshing(false);
                mRecyclerView.notifyChangeData(datas, mAdapter);
            } else {
                mRecyclerView.changeData(datas, mAdapter);
            }
        }else {
            if(start == 0){
                getBaseEmptyView().showEmptyContent();
            }else {
                mRecyclerView.loadFinish();
            }
        }
        tempData.addAll(datas);
    }


    private void ChatItemClick(ChatInfosBean bean) {

        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(this, bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(this, bean.getId());
                } else {
                    //这里提示先购买系列课
                    SeriesActivity.startActivity(this, bean.getChatSeries(), true);
                }
            } else {
                GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(this, bean.getId());
                } else {
                    //这里提示加入圈子
                    joinGroupDialog(bean.getGroupId());
                }
            } else {
                GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else {
            LiveIntroActivity.startActivity(this, bean.getId());
        }
    }

    private void joinGroupDialog(final long groupId) {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("要先加载圈子才能进入?")
              .show();
        dialog.btnText("取消", "加入");
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(NewCalssSynchronousActivity.this, CircleJoinActivity.class);
                intent.putExtra("byLiveId", (int) groupId);
                startActivityForResult(intent, GOTO_CIRCLE_REQUESTCODE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(ClassroomContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        if(!mSwipelayout.isRefreshing()){
            getBaseLoadingView().showLoading();
        }
    }
    @Subscribe
    public void onEvent(CreateLiveTopicBean bean) {
        getDatas();
    }
    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {
        mRecyclerView.reLoadFinish();
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyView(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatas();
            }
        });
    }

    public static void startActivity(Context context, String userCode, int circleid) {
        Intent intent = new Intent(context, NewCalssSynchronousActivity.class);
        intent.putExtra("circleid", circleid);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
