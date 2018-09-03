package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ClassRoomAuditAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Gubr on 2017/6/13.
 */

public class ClassRoomAuditFragment extends BaseTitleFragment implements
        ClassRoomAuditContract.View {
    private static final String TAG = "ClassRoomAuditFragment";
    @BindView(R.id.recyclerView) RecyclerView                     mRecyclerView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout               mSwipelayout;
    private                      ClassRoomAuditAdapter            mLiveRoomAdapter;
    private                      ClassRoomAuditContract.Presenter mPresenter;
    private                      Integer                          mgroupId;
    private                      String                           mUserCode;
    private                      AlertDialog                      dialog;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_purchase_list, container, false);
    }

    @Override
    public void initData() {
        new ClassRoomAuditPresenter(this);
        mgroupId = getArguments().getInt("circleid", 0);
        mUserCode = getArguments().getString("userCode");
        initAdapter();
        mPresenter.getDatas(mUserCode, 0, 15, mgroupId);
    }

    public void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mLiveRoomAdapter = new ClassRoomAuditAdapter(getContext(), new ArrayList<ChatInfosBean>(),
                R.layout.item_classroom_audit);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mLiveRoomAdapter.setOnReItemOnClickListener(
                new BaseRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        ChatInfosBean bean = mLiveRoomAdapter.getList().get(position);
//                        ChatItemClick(bean);
                    }
                });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getDatas(mUserCode, mLiveRoomAdapter.getItemCount(), 15, mgroupId);
            }
        });
        mLiveRoomAdapter.setOnAuditListener(new ClassRoomAuditAdapter.OnAuditListener() {
            @Override
            public void onAuditClick(ChatInfosBean bean) {
                showAuditCircleDialog(bean);
            }
        });
        mRecyclerView.setAdapter(mLiveRoomAdapter);

    }

    private void showAuditCircleDialog(final ChatInfosBean bean) {
        dialog = DialogUtil.showInputDialog(getActivity(), false, "", "是否通过审核？", "不通过", "通过",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_dialog_cancel:
//                                mPresenter.auditFile(bean, 0);
                                dialog.dismiss();
                                break;
                            case R.id.tv_dialog_confirm:
                                if (UserManager.getInstance().isLogin()) {
                                    mPresenter.auditClassRoom(UserManager.getInstance().getToken(),String.valueOf(bean.getId()), 1,mgroupId );
                                } else {
                                    Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
//                                mPresenter.auditFile(bean, 1);
                                break;
                        }



                    }
                });
    }


    @Override
    public void showDatas(int start, List<ChatInfosBean> datas) {
        if (mLiveRoomAdapter != null && datas.size() > 0) {
            mRecyclerView.notifyChangeData(datas, mLiveRoomAdapter);
        }else {
            getBaseEmptyView().showEmptyView();
        }
    }

    @Override
    public void shwoLoadMoreDatas(int start, List<ChatInfosBean> datas) {

    }

    @Override
    public void auditSuccessful(String linkid) {
        List<ChatInfosBean> list = mLiveRoomAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(linkid)) {
                list.remove(i);
                mRecyclerView.notifyChangeData();
                return;
            }
        }
        ((ClassRoomAuditActivity) getActivity()).upDateAuditedClassRoom();
    }

    @Override
    public void auditSeriseSuccessful(Object object) {}

    @Override
    public void showAuditClassRoom(List<ClassAndSeriseBean> datas) {}

    @Override
    public void setPresenter(ClassRoomAuditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter != null)
            mPresenter.destroy();
    }
}
