package com.gxtc.huchuan.ui.circle.classroom.newcalssroomaudit;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SynchronousCircleAdater;
import com.gxtc.huchuan.adapter.SynchronousCircleAdater.OnAuditListener;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.classroom.classroomaudit.ClassRoomAuditContract;
import com.gxtc.huchuan.ui.circle.classroom.classroomaudit.ClassRoomAuditPresenter;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zzg on 2017/11/13.
 */

public class NewClassRoomAuditFragment extends BaseTitleFragment implements ClassRoomAuditContract.View {
    private static final String TAG = "ClassRoomAuditFragment";
    @BindView(R.id.recyclerView) RecyclerView                     mRecyclerView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout               mSwipelayout;
    private                      SynchronousCircleAdater          mLiveRoomAdapter;
    private                      ClassRoomAuditContract.Presenter mPresenter;
    private                      Integer                          mgroupId;
    private                      String                           mUserCode;
    private                      AlertDialog                      dialog;
    private final int LOGINREQUEST            = 1 << 4;
    private HashMap<String, String> map;
    private int start = 0;
    private boolean isRefresh = true;
    private int curPosition = -1;
    private int auditType ;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_purchase_list, container, false);
    }

    @Override
    public void initData() {
        map = new HashMap<>();
        new ClassRoomAuditPresenter(this);
        mgroupId = getArguments().getInt("circleid", 0);
        mUserCode = getArguments().getString("userCode");
        mSwipelayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2, R.color.refresh_color3, R.color.refresh_color4);
        initAdapter();
        getBaseLoadingView().showLoading();
        getData();
    }

    private void getData(){
        if (mgroupId != 0)
        map.put("start", start +"");
        map.put("token", UserManager.getInstance().getToken());
        map.put("relType", "2");
        map.put("isAudit", "0");
        map.put("groupId", "" + mgroupId);
        mPresenter.getAuditClassRoom(map);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                start = 0;
                mRecyclerView.reLoadFinish();
                getData();
            }
        });
    }

    public void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mLiveRoomAdapter = new SynchronousCircleAdater(getContext(), new ArrayList<ClassAndSeriseBean>(), R.layout.item_live_list,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mLiveRoomAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        switch (mLiveRoomAdapter.getList().get(position).getType()){
                            //课程
                            case "2":
                                ChatInfosBean bean = (ChatInfosBean) mLiveRoomAdapter.getData("2",mLiveRoomAdapter.getList().get(position).getData());
                                ChatItemClick(bean);
                                break;
                            //系列课
                            case "6":
                                ChatInfosBean bean1 = new ChatInfosBean();
                                SeriesPageBean bean2 = (SeriesPageBean) mLiveRoomAdapter.getData("6",mLiveRoomAdapter.getList().get(position).getData());
                                bean1.setChatSeries(bean2.getId());
                                bean1.setChatSeriesData(bean2);
                                ChatItemClick(bean1);
                                break;
                        }

                    }
                });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isRefresh = false;
                start = start + 15;
                getData();
            }
        });
        mLiveRoomAdapter.setOnAuditListener(new OnAuditListener() {
            @Override
            public void onAuditClick(ChatInfosBean bean,int position) {
                curPosition = position;
                showAuditCircleDialog(bean);
            }
        });
        mRecyclerView.setAdapter(mLiveRoomAdapter);
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
                GotoUtil.goToActivityForResult(NewClassRoomAuditFragment.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else if ("1".equals(bean.getIsForGrop())) {
            if (UserManager.getInstance().isLogin()) {
                if ("1".equals(bean.getJoinGroup())) {
                    LiveIntroActivity.startActivity(getContext(), bean.getId());
                }
            } else {
                GotoUtil.goToActivityForResult(NewClassRoomAuditFragment.this,
                        LoginAndRegisteActivity.class, LOGINREQUEST);
            }
        } else {
            LiveIntroActivity.startActivity(getContext(), bean.getId());
        }
    }


    private void showAuditCircleDialog(final ChatInfosBean bean) {
        dialog = DialogUtil.showInputDialog(getActivity(), false, "", "是否通过审核？", "不通过", "通过",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_dialog_cancel:
                                auditType = 0;
                                if (UserManager.getInstance().isLogin()) {
                                    if(bean.getChatSeries() != null && "0".equals(bean.getChatSeries())){    //课程
                                        mPresenter.auditSerise(UserManager.getInstance().getToken(),String.valueOf(bean.getId()),mgroupId,auditType, "2");
                                    }else {   //系列课
                                        mPresenter.auditSerise(UserManager.getInstance().getToken(),bean.getChatSeriesData().getId(),mgroupId,auditType, "6");
                                    }
                                } else {
                                    Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                break;
                            case R.id.tv_dialog_confirm:
                                auditType = 1;
                                if (UserManager.getInstance().isLogin()) {
                                    if(bean.getChatSeries() != null && "0".equals(bean.getChatSeries())){    //课程
                                        mPresenter.auditSerise(UserManager.getInstance().getToken(),String.valueOf(bean.getId()),mgroupId,auditType, "2");
                                    }else {   //系列课
                                        mPresenter.auditSerise(UserManager.getInstance().getToken(),bean.getChatSeriesData().getId(),mgroupId,auditType, "6");
                                    }
                                } else {
                                    Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                break;
                        }



                    }
                });
    }


    @Override
    public void showDatas(int start, List<ChatInfosBean> datas) {}

    @Override
    public void shwoLoadMoreDatas(int start, List<ChatInfosBean> datas) {}

    @Override
    public void auditSuccessful(String linkid) {
        mRecyclerView.removeData(mLiveRoomAdapter,curPosition);
        ((NewClassRoomAuditActivity) getActivity()).upDateAuditedClassRoom();
    }

    @Override
    public void auditSeriseSuccessful(Object object) {
        mRecyclerView.removeData(mLiveRoomAdapter,curPosition);
        ((NewClassRoomAuditActivity) getActivity()).upDateAuditedClassRoom();
    }

    @Override
    public void showAuditClassRoom(List<ClassAndSeriseBean> datas) {
        getBaseLoadingView().hideLoading();
        mSwipelayout.setRefreshing(false);
        if(datas != null && datas.size() > 0){
            if(isRefresh){
                mRecyclerView.notifyChangeData(datas,mLiveRoomAdapter);
            }else {
                mRecyclerView.changeData(datas,mLiveRoomAdapter);
            }
        }else {
            if(start == 0){
                getBaseEmptyView().showEmptyContent();
            }else {
                mRecyclerView.loadFinish();
            }
        }
    }

    @Override
    public void setPresenter(ClassRoomAuditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {}

    @Override
    public void showNetError() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null)
            mPresenter.destroy();
    }
}
