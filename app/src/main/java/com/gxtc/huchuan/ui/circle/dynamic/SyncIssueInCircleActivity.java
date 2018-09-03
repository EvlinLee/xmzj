package com.gxtc.huchuan.ui.circle.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SyncIssueInCircleAdapter;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/5/18.
 * 同步圈子
 */

public class SyncIssueInCircleActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.rv_sync)  RecyclerView mRecyclerView;
    @BindView(R.id.tv_label) TextView     tvLabel;

    private List<MineCircleBean>      mDatas;
    private ArrayList<MineCircleBean> mSelectDatas;

    private int type;
    private String linkId;

    private SyncIssueInCircleAdapter mAdapter;
    private int                      defauleId;//默认圈子

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_issue_in_circle);
    }


    @Override
    public void initView() {
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showTitle(getString(R.string.title_sync));
        getBaseHeadView().showHeadRightButton("确定", this);

        mDatas = new ArrayList<>();
        mSelectDatas = new ArrayList<>();

        Intent intent = getIntent();
        defauleId = intent.getIntExtra("default", -1);
        boolean isClass = intent.getBooleanExtra("isClass", false);
        type = intent.getIntExtra("type", -1);
        linkId = intent.getStringExtra("linkId");

        if(isClass){
            tvLabel.setText("最多同步到" + SyncIssueInCircleAdapter.MAX_SELECT + "个圈子, 圈内成员免费听课");
        }else{
            tvLabel.setText("最多同步到" + SyncIssueInCircleAdapter.MAX_SELECT + "个圈子");
        }

        mAdapter = new SyncIssueInCircleAdapter(this, new ArrayList<MineCircleBean>(), R.layout.item_sync_issue, defauleId);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        getBaseLoadingView().showLoading();
        Subscription sub =
            AllApi.getInstance()
                  .listByUser(UserManager.getInstance().getToken(), "0")
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        mDatas = (List<MineCircleBean>) data;
                        if (mDatas.size() == 0) {
                            getBaseEmptyView().showEmptyContent("您暂时没有加入任何圈子");
                            return;
                        }
                        initRecyclerView(mDatas);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(getBaseLoadingView() == null) return;
                        getBaseLoadingView().hideLoading();
                        getBaseEmptyView().showEmptyView(message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private void initRecyclerView(final List<MineCircleBean> datas) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        Intent intent = getIntent();
        ArrayList<Integer> ids = (ArrayList<Integer>) intent.getSerializableExtra(Constant.INTENT_DATA);
        //默认选中当前圈子
        if (defauleId != -1) {
            for (MineCircleBean bean : datas) {
                if (defauleId == bean.getId()) {
                    bean.setFirst(true);
                    mSelectDatas.add(bean);
                    break;
                }
            }
        }
        for (MineCircleBean bean : datas) {
            if (defauleId != bean.getId()) {
                bean.setCheck(false);
            }
        }

        //如果是重新选择同步圈子，将已选的先勾上
        if (ids != null && ids.size() > 0) {
            for (Integer integer : ids) {
                for (MineCircleBean bean : datas) {
                    if (bean.getId() == integer) {
                        bean.setCheck(true);

                        if(ids.get(0).equals(integer)){
                            bean.setFirst(true);
                        }
                    }
                }
            }
        }
        mAdapter.notifyChangeData(datas);
        mAdapter.checkCount();
        if(ids != null && ids.size() > 0){
            mAdapter.setIds(ids,type,linkId);
        }
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.headBackButton:
                SyncIssueInCircleActivity.this.finish();
                break;

            case R.id.headRightButton:
                MineCircleBean firstBean = null;
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    MineCircleBean bean = mAdapter.getList().get(i);

                    if(bean.isFirst()){
                        firstBean = bean;
                    }

                    //选中的时候
                    if(bean.isCheck() && !bean.isFirst()) {
                        mSelectDatas.add(bean);
                    }
                }
                if(firstBean != null){
                    mSelectDatas.add(0,firstBean);
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("select_data", mSelectDatas);
                setResult(Constant.ResponseCode.ISSUE_TONG_BU, intent);
                SyncIssueInCircleActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
