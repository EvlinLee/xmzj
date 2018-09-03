package com.gxtc.huchuan.ui.circle.home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ManagetMentCircleAdapter;
import com.gxtc.huchuan.adapter.MineCircleAdapter;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventCreateCirlceBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.VertifanceFlowDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.findCircle.CircleListActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mine.circle.CircleManagerActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.CheaekUtil;
import com.gxtc.huchuan.widget.DefaultItemTouchHelpCallback;
import com.gxtc.huchuan.widget.DefaultItemTouchHelper;
import com.gxtc.huchuan.widget.RecyclerSpace;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/4/26.
 * 我的圈子界面
 */

public class MineCircleActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.rv_mine_circle_mine)
    RecyclerView recyclerViewMine;
    @BindView(R.id.rv_mine_circle_jion)
    RecyclerView recyclerViewJoin;
    @BindView(R.id.tv_mine_circle_join)
    TextView tvMineCircleJoin;
    @BindView(R.id.tv_mine_cicle_create)
    TextView tvMineCicleCreate;

    private int status = -1;
    private String flag;

    private DefaultItemTouchHelper mItemTouchHelper;
    private MineCircleAdapter mAdapter;
    private ManagetMentCircleAdapter managetMentCircleAdapterAdapter;
    private VertifanceFlowDialog mVertifanceFlowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_circle);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_mine_circle));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MineCircleActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");

        recyclerViewMine.setNestedScrollingEnabled(false);
        recyclerViewJoin.setNestedScrollingEnabled(false);
    }

    @Override
    public void initData() {
        getBaseLoadingView().showLoading();
        hideContentView();
        if (!TextUtils.isEmpty(flag) && "1".equals(flag)) {
            getMineCircle("1");
            getMineCircle("2");
        } else {
            getMineCircle("1");
            getMineCircle("3");
        }
    }

    @Override
    public void onClick(View v) {
        getBaseHeadView().getHeadRightButton().setVisibility(View.GONE);
        tvMineCircleJoin.setText("加入的圈子(长按可以拖拽排序)");
        if (mItemTouchHelper != null) {
            mItemTouchHelper.setDragEnable(false);
            mItemTouchHelper.setSwipeEnable(false);
            updataSort();
        }
    }


    /**
     * 获取用户创建的圈子  0：全部数据、1：创建的、2：关注的 3：管理的
     */
    private void getMineCircle(final String type) {
        if (UserManager.getInstance().isLogin()) {

            Subscription sub = AllApi.getInstance().listByUser(UserManager.getInstance().getToken(),
                    type).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            showContentView();
                            if (tvMineCicleCreate == null) return;
                            List<MineCircleBean> datas = (List<MineCircleBean>) data;

                            initRecyclerView(datas, type);
                            getBaseLoadingView().hideLoading();
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if (getBaseLoadingView() == null) return;
                            if (String.valueOf(400).equals(errorCode)) {
                                getBaseEmptyView().showNetWorkViewReload(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                            }
                                        });
                            }

                            getBaseLoadingView().hideLoading();
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this, sub);
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);

    }

    private void initRecyclerView(final List<MineCircleBean> datas, final String type) {
        // 0：全部数据、1：创建的、2：关注的 3：管理的
        if ("1".equals(type)) {
            if (datas != null && datas.size() > 0) {
                tvMineCicleCreate.setVisibility(View.VISIBLE);
                mAdapter = new MineCircleAdapter(MineCircleActivity.this, new ArrayList<MineCircleBean>(), R.layout.item_mine_circle, type);
                recyclerViewMine.setLayoutManager(new GridLayoutManager(this, 3));
                recyclerViewMine.setAdapter(mAdapter);
                recyclerViewMine.addItemDecoration(new RecyclerSpace(10, getResources().getColor(R.color.grey_F4F4F4)));
                mAdapter.notifyChangeData(datas);
            } else {
                tvMineCicleCreate.setVisibility(View.GONE);
                recyclerViewMine.setVisibility(View.GONE);
            }

        } else if ("2".equals(type)) {

            MineCircleBean bean = new MineCircleBean();
            datas.add(bean);
            managetMentCircleAdapterAdapter = new ManagetMentCircleAdapter(MineCircleActivity.this, new ArrayList<MineCircleBean>(), R.layout.item_mine_circle, type);
            recyclerViewJoin.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerViewJoin.addItemDecoration(new RecyclerSpace(10, getResources().getColor(R.color.grey_F4F4F4)));
            recyclerViewJoin.setAdapter(managetMentCircleAdapterAdapter);
            managetMentCircleAdapterAdapter.notifyChangeData(datas);
            mItemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
            mItemTouchHelper.attachToRecyclerView(recyclerViewJoin);
            tvMineCircleJoin.setText("加入的圈子(长按可以拖拽排序)");
            if (datas.size() < 0) {
                tvMineCircleJoin.setVisibility(View.GONE);
            }


        } else if ("3".equals(type)) {

            managetMentCircleAdapterAdapter = new ManagetMentCircleAdapter(MineCircleActivity.this, new ArrayList<MineCircleBean>(), R.layout.item_mine_circle, type);
            if (datas != null && datas.size() > 0) {
                tvMineCircleJoin.setText("管理的圈子");
                //在这里要过滤掉我创建的圈子
                final List<MineCircleBean> dataList = new ArrayList<>();
                Observable.from(datas)
                        .filter(new Func1<MineCircleBean, Boolean>() {
                            @Override
                            public Boolean call(MineCircleBean mineCircleBean) {
                                return mineCircleBean.getIsMy() == 0;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MineCircleBean>() {
                            @Override
                            public void onCompleted() {
                                recyclerViewJoin.setLayoutManager(new GridLayoutManager(MineCircleActivity.this, 3));
                                recyclerViewJoin.addItemDecoration(new RecyclerSpace(10, getResources().getColor(R.color.grey_F4F4F4)));
                                recyclerViewJoin.setAdapter(managetMentCircleAdapterAdapter);
                                managetMentCircleAdapterAdapter.notifyChangeData(dataList);
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showShort(MineCircleActivity.this, e.getMessage());
                            }

                            @Override
                            public void onNext(MineCircleBean mineCircleBean) {
                                dataList.add(mineCircleBean);
                            }
                        });
                recyclerViewJoin.setVisibility(View.VISIBLE);
            } else {
                tvMineCircleJoin.setVisibility(View.GONE);
                recyclerViewJoin.setVisibility(View.GONE);
            }
        }

        // 0：全部数据、1：创建的、2：关注的 3：管理的
        if (mAdapter != null) {
            mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    if (mAdapter.getList().get(position).getIsShow() == 1) {
                        /*MineCircleBean mineCircleBean = mAdapter.getList().get(position);

                        CircleBean bean = new CircleBean();

                        bean.setGroupName(mineCircleBean.getGroupName());
                        bean.setCover(mineCircleBean.getCover());
                        bean.setId(mineCircleBean.getId());
                        bean.setContent(mineCircleBean.getContent());
                        bean.setInfoNum(mineCircleBean.getInfoNum());
                        bean.setAttention(mineCircleBean.getAttention());
                        bean.setIsMy(mineCircleBean.getIsMy());*/

                        Intent intent = new Intent(MineCircleActivity.this, CircleManagerActivity.class);
                        intent.putExtra("circleId", mAdapter.getList().get(position).getId());
                        intent.putExtra("isMy", mAdapter.getList().get(position).getIsMy());
                        startActivity(intent);

                    } else {
                        showVertifanceDialog(mAdapter.getList().get(position));
                    }
                }
            });
        }

        if (managetMentCircleAdapterAdapter != null) {
            managetMentCircleAdapterAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    //这里的position用框架封装的会有问题
                    position = recyclerViewJoin.getChildAdapterPosition(v);
                    if ("3".equals(type) || position != managetMentCircleAdapterAdapter.getList().size() - 1) {   //type = 3 不用处理带“+”最后一项
                        if (!TextUtils.isEmpty(flag) && "1".equals(flag)) {  //跳圈子主页
                            // 0：没有申请过，1：审核通过，2：审核中，3：审核失败
                            if (managetMentCircleAdapterAdapter.getList().get(position).getIsShow() == 1) {
                                MineCircleBean mineCircleBean = managetMentCircleAdapterAdapter.getList().get(position);

                                CircleBean bean = new CircleBean();

                                bean.setGroupName(mineCircleBean.getGroupName());
                                bean.setCover(mineCircleBean.getCover());
                                bean.setId(mineCircleBean.getId());
                                bean.setContent(mineCircleBean.getContent());
                                bean.setInfoNum(mineCircleBean.getInfoNum());
                                bean.setAttention(mineCircleBean.getAttention());
                                bean.setIsMy(mineCircleBean.getIsMy());

                                GotoUtil.goToActivity(MineCircleActivity.this, CircleMainActivity.class,
                                        0, bean);
                            } else {
                                showVertifanceDialog(managetMentCircleAdapterAdapter.getList().get(position));
                            }

                        } else {//跳圈子管理页
                            //0：没有申请过，1：审核通过，2：审核中，3：审核失败
                            if (managetMentCircleAdapterAdapter.getList().get(position).getIsShow() == 1) {
                                Intent intent = new Intent(MineCircleActivity.this, CircleManagerActivity.class);
                                intent.putExtra("circleId", managetMentCircleAdapterAdapter.getList().get(position).getId());
                                intent.putExtra("isMy", managetMentCircleAdapterAdapter.getList().get(position).getIsMy());
                                startActivity(intent);
                            } else {
                                showVertifanceDialog(managetMentCircleAdapterAdapter.getList().get(position));
                            }
                        }
                    } else {
                        if ("2".equals(type)) {
                            GotoUtil.goToActivity(MineCircleActivity.this, CircleListActivity.class);
                        }
                    }

                }
            });
        }


        //只有我加入的圈子可以拖动排序
        if ("2".equals(type) && managetMentCircleAdapterAdapter != null) {
            managetMentCircleAdapterAdapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
                @Override
                public void onItemLongClick(View v, int position) {
                    getBaseHeadView().showHeadRightButton("完成", MineCircleActivity.this);
                    if (mItemTouchHelper != null) {
                        mItemTouchHelper.setSwipeEnable(true);
                        mItemTouchHelper.setDragEnable(true);
                    }
                }
            });
        }
    }

    private void showVertifanceDialog(final MineCircleBean mineCircleBean) {
        if (mVertifanceFlowDialog == null) {
            mVertifanceFlowDialog = new VertifanceFlowDialog(this);
        }
        mVertifanceFlowDialog.show();
        mVertifanceFlowDialog.setDownButtonVisiblet(true);
        mVertifanceFlowDialog.setDownButtonText("进入圈子");
        mVertifanceFlowDialog.setOnCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.complete://重新申请
                        switch (mineCircleBean.getIsShow()) {
                            case 2:
                                jumpToEdit(mineCircleBean);
                                break;
                            case 3:
                                jumpToEdit(mineCircleBean);
                                break;
                        }

                        break;
                    case R.id.tx_enter://进入圈子
                        Intent intent = new Intent(MineCircleActivity.this,
                                CircleMainActivity.class);
                        intent.putExtra("groupId", mineCircleBean.getId());
                        startActivity(intent);
                        break;
                }

                mVertifanceFlowDialog.dismiss();
            }
        });

        switch (mineCircleBean.getIsShow()) {
            case 0:
                mVertifanceFlowDialog.setFlowStatus("审核中...");
                mVertifanceFlowDialog.setUpButtonText("完成");
                mVertifanceFlowDialog.setFlowStatusPic(R.drawable.applicationprocess_icon_shjg_1);
                break;
            case 2:
                mVertifanceFlowDialog.setUpButtonText("重新申请");
                checkCicle(mineCircleBean);
            case 3:
                mVertifanceFlowDialog.setUpButtonText("重新申请");
                checkCicle(mineCircleBean);
                break;
        }
    }

    private void jumpToEdit(MineCircleBean mineCircleBean) {
        Class targeClass = null;
        switch (mineCircleBean.getIsFee()) {
            case 0:
                targeClass = CreateFreeCircleActivity.class;
                break;
            case 1:
                targeClass = CreateNoFreeCircleActivity.class;
                break;
        }
        Intent intent = new Intent(MineCircleActivity.this, targeClass);
        intent.putExtra("data", mineCircleBean);
        startActivity(intent);
    }

    public void checkCicle(final MineCircleBean mineCircleBean) {
        CheaekUtil.getInstance().getInfo(UserManager.getInstance().getToken(),
                mineCircleBean.getId() + "", Constant.STATUE_LINKTYPE_CIRCLE,
                new ApiCallBack<CheckBean>() {

                    @Override
                    public void onSuccess(CheckBean data) {
                        status = mineCircleBean.getIsShow();
                        if (data != null) {
                            switch (status) {
                                case 2:
                                    mVertifanceFlowDialog.setFlowStatus(data.getContent());
                                    break;
                                case 3:
                                    mVertifanceFlowDialog.setFlowStatus(data.getContent());//审核失败
                                    break;
                            }
                            mVertifanceFlowDialog.setFlowStatusPic(
                                    R.drawable.applicationprocess_icon_shjg_2);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MineCircleActivity.this, message);
                    }
                }).addTask(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
        if (status != -1) {
            CheaekUtil.getInstance().cancelTask(this);
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(EventCircleIntro bean) {
        if (bean.status == EventCircleIntro.EXIT || bean.status == EventCircleIntro.DELETE) {
            initData();
        }
    }

    @Subscribe
    public void onEvent(EventCreateCirlceBean bean) {
        if (!TextUtils.isEmpty(flag) && "1".equals(flag)) {
            getMineCircle("1");
            getMineCircle("2");
        } else {
            getMineCircle("1");
            getMineCircle("3");
//            tvMineCircleJoin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.requestCode.NEWS_LIKEANDCOLLECT && resultCode == Constant.ResponseCode.LOGINRESPONSE_CODE) {
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {

        }


        @Override
        public boolean onMove(int srcPosition, int targetPosition) {

            LogUtil.i("srcPosition   :  " + srcPosition + "    targetPosition   :  " + targetPosition);
            List<MineCircleBean> list = managetMentCircleAdapterAdapter.getList();
            if (list != null) {
                tvMineCircleJoin.setText("加入的圈子(点击完成按钮保存)");
                MineCircleBean temp = list.remove(srcPosition);
                list.add(targetPosition, temp);
                managetMentCircleAdapterAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }

        @Override
        public boolean onSelected(int adapterPosition) {
            View childAt = recyclerViewJoin.getChildAt(adapterPosition);
            ObjectAnimator anim = ObjectAnimator.ofFloat(childAt, "scaleY", 1f, 1.2f, 1f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(childAt, "scaleX", 1f, 1.2f, 1f);
            anim.setDuration(500);
            anim.start();
            anim2.setDuration(500);
            anim2.start();

            return false;
        }
    };


    //上传排序之后的圈子id数组
    private void updataSort() {
        List<MineCircleBean> list = managetMentCircleAdapterAdapter.getList();
        List<String> idList = new ArrayList<>();
        String[] ids = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getId() + "";
            idList.add(list.get(i).getId() + "");
        }
        String token = UserManager.getInstance().getToken();
        Subscription sub =
                CircleApi.getInstance()
                        .sortCircleList(token, idList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                //成功之后给上个页面刷新
                                setResult(RESULT_OK);
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(MineCircleActivity.this, message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


}
