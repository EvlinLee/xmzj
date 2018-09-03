package com.gxtc.huchuan.ui.circle.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicManagerAdapter;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.bean.event.EventCircleCommentBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.MineDynamicDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.dymic.DymicMineActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.Constant.ResponseCode.LOGINRESPONSE_CODE;

/**
 * Created by sjr on 2017/4/28.
 * 圈子动态
 */

public class CircleDynamicManagerActivity extends BaseTitleActivity implements
        CircleDynamicManagerContract.View, View.OnClickListener {

    @BindView(R.id.rl_circle_topic)        RecyclerView       mRecyclerView;
    @BindView(R.id.sw_circle_topic)        SwipeRefreshLayout swCircleTopic;
    @BindView(R.id.headBackButton)         ImageButton        headBackButton;
    @BindView(R.id.cb_editor)              CheckBox           cbEditor;
    @BindView(R.id.tv_circle_topic_delete) TextView           tvCircleTopicDelete;
//    @BindView(R.id.fl_circle_btn)
//    FloatingActionButton flCircleBtn;

    private AlertDialog mDialog;

    private CircleDynamicManagerContract.Presenter mPresenter;
    private CircleDynamicManagerAdapter            mAdapter;

    int circleId;
    int count = 0;

    private List<CircleHomeBean>                 mDatas;
    private List<Integer>                        topicIds;
    private List<BaseRecyclerAdapter.ViewHolder> holders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_topic_manager);

        EventBusUtil.register(this);
    }

    @Override
    public void initView() {

        Intent intent = getIntent();
        circleId = intent.getIntExtra("circle_id", -1);

        swCircleTopic.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
    }


    @Override
    public void initData() {

        mDatas = new ArrayList<>();
        topicIds = new ArrayList<>();
        holders = new ArrayList<>();

        initRecyCleView();
        new CircleDynamicManagerPresenter(this, circleId);
        mPresenter.getData(false);

    }

    @Override
    public void initListener() {
        headBackButton.setOnClickListener(this);
        tvCircleTopicDelete.setOnClickListener(this);

        swCircleTopic.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRecyclerView.reLoadFinish();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });


    }

    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    private int touchPosition;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                CircleDynamicManagerActivity.this.finish();
                break;
            case R.id.tv_circle_topic_delete:
                showDeleteDialog(false);
                break;
            case R.id.tv_dynamic_essence://设置精华
                setEssence(mAdapter.getList().get(touchPosition).getId(), touchPosition);
                break;
            case R.id.tv_dynamic_delete://删除
//                                mAdapter.getList().remove(position);
                showDeleteDialog(true);
                break;
        }
    }


    private void showDeleteDialog(final boolean isSingle) {
        if (isSingle) {
            count = 1;
        }
        mDialog = DialogUtil.createDialog(this, "提示", "确定要删除" + count + "条动态吗？", "取消", "确定",
                new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(View view) {
                        if (!isSingle) {
                            for (int i = 0; i < mAdapter.getList().size(); i++) {
                                CircleHomeBean bean = mAdapter.getList().get(i);
                                if (bean.isCheck()) {//选中的时候
                                    topicIds.add(bean.getId());
                                }
                            }
                            deleteCollect(UserManager.getInstance().getToken(),
                                    (ArrayList<Integer>) topicIds);
                        } else {
                            ArrayList<Integer> ids  = new ArrayList<>();
                            CircleHomeBean     bean = mAdapter.getList().get(touchPosition);
                            bean.setCheck(true);
                            ids.add(bean.getId());
                            deleteCollect(UserManager.getInstance().getToken(), ids);

                        }
                        mDialog.dismiss();
                    }

                });
        mDialog.show();
    }



    private void deleteCollect(String token, final ArrayList<Integer> topicIds) {
        if (topicIds.size() > 0){
            Subscription sub = AllApi.getInstance().deletedynimic(token,circleId, topicIds).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(mRecyclerView == null)   return;
                            for (int i = 0; i < mAdapter.getList().size(); i++) {
                                CircleHomeBean bean = mAdapter.getList().get(i);
                                if (bean.isCheck()) {//选中的时候
                                    mAdapter.getList().remove(bean);
                                    i--;
                                }
                            }
                            mRecyclerView.notifyChangeData();
//
                            tvCircleTopicDelete.setVisibility(View.GONE);
                            for (int i = 0; i < mDatas.size(); i++) {
                                mDatas.get(i).setShow(false);
                            }
                            topicIds.clear();
                            cbEditor.setChecked(false);
                            count = 0;
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(CircleDynamicManagerActivity.this, errorCode, message);
                        }
                    }));
            RxTaskHelper.getInstance().addTask(this,sub);
        } else ToastUtil.showShort(CircleDynamicManagerActivity.this, "请选择动态");

    }


    @Subscribe
    public void onEvent(EventCollectSelectBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (i == bean.getPosition() && bean.isSelected() == true) {
                holders.add(bean.holder);
                count++;
            } else if (i == bean.getPosition() && bean.isSelected() == false) {
                count--;
                if (count < 0) {
                    count = 0;
                }
            }
        }
        tvCircleTopicDelete.setText("删除(" + count + ")");

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(CircleDynamicManagerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swCircleTopic.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        findViewById(R.id.vs_circle_topic_manager).setVisibility(View.VISIBLE);
        cbEditor.setVisibility(View.GONE);
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    private MineDynamicDialog dynamicDialog;

    @Override
    public void showData(final List<CircleHomeBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter = new CircleDynamicManagerAdapter(this, datas,
                R.layout.item_circle_topic_manager_activity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnReItemOnLongClickListener(
                new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        dynamicDialog = new MineDynamicDialog(CircleDynamicManagerActivity.this);
                        touchPosition = position;

                        if (0 == mAdapter.getList().get(touchPosition).getIsGood()) {
                            dynamicDialog.setTvEssenceText("设置为精华");
                        } else {
                            dynamicDialog.setTvEssenceText("取消精华");
                        }
                        showEssenceorDel(v);
                    }
                });

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CircleHomeBean data   = datas.get(position);
                DynamicDetialActivity.startActivity(CircleDynamicManagerActivity.this,data.getId()+"");
            }
        });


        setEditor(datas);
    }


    private void showEssenceorDel(View v) {

        dynamicDialog.showAnim(null).dismissAnim(null).anchorView(v).dimEnabled(true).offset(100,
                -100).dimEnabled(false).gravity(Gravity.BOTTOM).setOnClickListener(this);

        dynamicDialog.show();
    }


    Subscription subEssence;

    /**
     * 设置精华
     *
     * @param id 动态id
     */
    private void setEssence(int id, final int position) {
        final CircleHomeBean bean = mAdapter.getList().get(position);
        subEssence = CircleApi.getInstance().setGood(circleId, UserManager.getInstance().getToken(),
                id).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        bean.setIsGood(0 == bean.getIsGood() ? 1 : 0);
                        mAdapter.getList().set(position, bean);

                        mRecyclerView.notifyChangeData();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleDynamicManagerActivity.this,
                                errorCode, message);
                    }
                }));
    }

    private void setEditor(final List<CircleHomeBean> datas) {
        cbEditor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//可以编辑状态
                    cbEditor.setText("取消");
                    for (int i = 0; i < datas.size(); i++) {
                        datas.get(i).setShow(true);
                    }
                    mRecyclerView.notifyChangeData();
                    tvCircleTopicDelete.setVisibility(View.VISIBLE);
                    tvCircleTopicDelete.setText("删除(0)");
                } else {
                    cbEditor.setText("编辑");
                    tvCircleTopicDelete.setVisibility(View.GONE);
                    for (int i = 0; i < datas.size(); i++) {
                        datas.get(i).setShow(false);
                        datas.get(i).setCheck(false);
                    }
                    count = 0;

                    mRecyclerView.notifyChangeData();
                }
            }
        });
    }


    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);

    }

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
        mDatas.addAll(datas);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.requestCode.REFRESH_CIRCLE_HOME && resultCode == LOGINRESPONSE_CODE) {
            initData();
        }
        if (requestCode == Constant.requestCode.CIRCLE_DZ && resultCode == Constant.ResponseCode.CIRCLE_RESULT_DZ) {
            int circleId = data.getIntExtra("circle_id", -1);
            int isDz     = data.getIntExtra("is_dz", -1);
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i).getId() == circleId) {
                    mDatas.get(i).setIsDZ(isDz);
                    if (0 == isDz) {

                        mDatas.get(i).setDianZan(mDatas.get(i).getDianZan() - 1);
                    } else if (1 == isDz) {
                        mDatas.get(i).setDianZan(mDatas.get(i).getDianZan() + 1);
                    }
                    mRecyclerView.notifyItemChanged(i);
                }
            }
        }
        if (requestCode == 0 && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            initData();
        }

    }

    @Subscribe
    public void onEvent(EventCircleCommentBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId() == bean.id) {
                mDatas.get(i).setLiuYan(bean.count);
                mRecyclerView.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }


}
