package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveRoomAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomContract;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomPresenter;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by Gubr on 2017/6/13.
 */

public class ClassRoomAuditedFragment extends BaseTitleFragment implements ClassroomContract.View {
    private static final String TAG = "ClassRoomAuditedFragmen";
    @BindView(R.id.recyclerView) RecyclerView       mRecyclerView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout mSwipelayout;
    Unbinder unbinder;
    private int             mCircleid;
    private String          mUserCode;
    private LiveRoomAdapter mAdapter;
    private int GOTO_CIRCLE_REQUESTCODE = 1 << 3;
    private int LOGINREQUEST            = 1 << 4;
    private boolean isRefresh = true;
    ClassroomContract.Presenter mPresenter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_purchase_list, container, false);

    }

    @Override
    public void initListener() {
        mSwipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipelayout.setRefreshing(false);
                isRefresh = true;
                getDatas();
            }
        });
    }

    @Override
    public void initData() {
        mCircleid = getArguments().getInt("circleid", 0);
        mUserCode = getArguments().getString("userCode");
        new ClassroomPresenter(this);
        mSwipelayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isRefresh = false;
                HashMap<String, String> map = new HashMap<>();
                map.put("userCode", mUserCode);
                if (mCircleid != 0) map.put("groupId", "" + mCircleid);

                mPresenter.loadmoreDatas(map);
            }
        });
        initAdater();
        getDatas();
    }


    public void getDatas() {
        HashMap<String, String> map = new HashMap<>();

        if (mCircleid != 0) map.put("groupId", "" + mCircleid);
        mPresenter.getDatas(map);
    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLiveRoom(List<ChatInfosBean> datas) {
        getBaseLoadingView().hideLoading();
        if(isRefresh){
            mRecyclerView.notifyChangeData(datas,mAdapter);
        }else {
            mRecyclerView.changeData(datas,mAdapter);
        }
    }

    @Override
    public void showSearChLiveRoom(List<SearchBean> datas) {}

    @Override
    public void showSearData(List<ClassAndSeriseBean> datas) {}

    private void initAdater(){
        mAdapter = new LiveRoomAdapter(getContext(), new ArrayList<ChatInfosBean>(), R.layout.item_live_room);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                ChatInfosBean bean = mAdapter.getList().get(position);
                ChatItemClick(bean);
            }
        });
    }
    @Override
    public void showlaodMoreLiveRoom(List<ChatInfosBean> datas) {
        if (mAdapter != null) mRecyclerView.changeData(datas, mAdapter);

    }

    @Override
    public void showUnauditGroup(List<ClassAndSeriseBean> datas) {

    }

    private void ChatItemClick(ChatInfosBean bean) {

        if (bean.isSingUp()) {
            LiveIntroActivity.startActivity(getContext(), bean.getId());
        } else if (!"0".equals(bean.getChatSeries())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.isSingUp())) {
                    LiveIntroActivity.startActivity(getContext(), bean.getId());
                } else {
                    //这里提示先购买系列课
                    //joinSeriesDialog(bean.getChatSeries());
                    SeriesActivity.startActivity(getActivity(),bean.getChatSeries(), true);
                }
            } else {
                GotoUtil.goToActivityForResult(ClassRoomAuditedFragment.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(getContext(), bean.getId());
                } else {
                    //这里提示加入圈子
                    joinGroupDialog(bean.getGroupId());
                }
            } else {
                GotoUtil.goToActivityForResult(ClassRoomAuditedFragment.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else {
            LiveIntroActivity.startActivity(getContext(), bean.getId());
        }
    }

    private void joinGroupDialog(final long groupId) {
        final NormalDialog dialog = new NormalDialog(getContext());
        dialog.content("要先加载圈子才能进入?")//
//              .showAnim(mBasIn)//
//              .dismissAnim(mBasOut)//
              .show();
        dialog.btnText("取消", "加入");
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
//                        T.showShort(mContext, "left");
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                Intent intent = new Intent(getContext(), CircleJoinActivity.class);
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

    }

    @Override
    public void showLoadFinish() {
        if (mRecyclerView != null) {
            mRecyclerView.loadFinish();
        }
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
}
