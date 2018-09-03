package com.gxtc.huchuan.ui.mine.circleinfodetail;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleHomeShieldDialogV5;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.home.CircleHomeContract;
import com.gxtc.huchuan.ui.circle.home.CircleHomePresenter;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.mall.MallHandleUtil;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.dymic.DymicMineActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DynamicUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.ListScrollUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.TextLineUtile;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.PraiseTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2017/9/5.
 */

public class MyDynamicActivity extends BaseTitleActivity implements CircleHomeContract.View {

    @BindView(R.id.recyclerView) RecyclerView       mRecyclerView;
    @BindView(R.id.swipelayout)  SwipeRefreshLayout swipeLayout;

    View                 serchLayout;
    CircleDynamicAdapter mAdapter;
    HashMap<String, String> map = new HashMap<>();
    CircleHomeContract.Presenter mPresenter;

    int start = 0;
    private int                      shieldPosition;
    private CircleHomeShieldDialogV5 shieldDialog;
    private AlertDialog            mSureDialog;
    private CircleHomeBean           mBean;
    private AlertDialog              mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dynamic_layout);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("我的动态");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightButton("互动", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoUtil.goToActivity(MyDynamicActivity.this, DymicMineActivity.class);
            }
        });
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        View headerView = getLayoutInflater().inflate(R.layout.search_head_view, null);
        serchLayout = headerView.findViewById(R.id.et_input_search);
        mRecyclerView.addHeadView(headerView);
    }

    @Override
    public void initData() {
        super.initData();
        getBaseLoadingView().showLoading();
        new CircleHomePresenter(this);
        getMyDynamicData(false);
        initAdater();
    }

    private void getMyDynamicData(final boolean isLoadMore) {
        String token = UserManager.getInstance().getToken();
        map.put("userCode", UserManager.getInstance().getUserCode());
        map.put("start", start + "");
        map.put("pageSize", 15 + "");
        map.put("token", token);
        Subscription sub = CircleApi.getInstance().listByUserCode(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleHomeBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        List<CircleHomeBean> list = (List<CircleHomeBean>) data;
                        if (list != null && list.size() > 0.) {
                            start = start + 15;
                            if (!isLoadMore) {
                                mRecyclerView.notifyChangeData(list, mAdapter);
                            } else {
                                mRecyclerView.changeData(list, mAdapter);
                            }
                        } else {
                            mRecyclerView.loadFinish();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(MyDynamicActivity.this, errorCode,
                                message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void initListener() {
        serchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewSearchActivity.jumpToSearch(MyDynamicActivity.this, "7");
            }
        });
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                mRecyclerView.reLoadFinish();
                start = 0;
                getMyDynamicData(false);
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getMyDynamicData(true);
            }
        });

        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChangeState(int state) {
                if(mCommentDialog != null && mCommentDialog.isVisible()){
                    if(state == KeyboardUtils.KEYBOARD_OPEN){
                        int [] size = new int[2];
                        mCommentDialog.getEdit().getLocationOnScreen(size);
                        ListScrollUtil.INSTANCE.scrollList(mRecyclerView, size[1]);
                    }
                }
            }
        });
    }

    public void initAdater() {
        int[] res = new int[]{R.layout.item_circle_home_noimgv5, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv5};
        mAdapter = new CircleDynamicAdapter(false, mRecyclerView, this, this,
                new ArrayList<CircleHomeBean>(), res);
        mRecyclerView.setAdapter(mAdapter);
        setAdapterListener();
    }

    private void setAdapterListener() {
        mAdapter.setOnCommentAndPraiseClickListener(
                new CircleDynamicAdapter.OnCommentAndPraiseClickListener() {
                    @Override
                    public void onNickNameClick(int groupPosition, int position,
                                                CommentListTextView.CommentInfo mInfo) {
                        //如果是自己的 弹出Dialog 显示  复制 删除
                        if (UserManager.getInstance().isLogin()) {

                            if (mInfo.getNickUserCode().equals(
                                    UserManager.getInstance().getUserCode())) {
                                showCopyOrRemoveDialg(groupPosition, position, mInfo);
                            }
                        }
                    }

                    @Override
                    public void onToNickNameClick(int groupPosition, int position,
                                                  CommentListTextView.CommentInfo mInfo) {

                    }

                    @Override
                    public void onCommentItemClick(int groupPosition, int position,
                                                   CircleCommentBean mInfo) {

                    }

                    @Override
                    public void onCommentOtherClick() {

                    }

                    @Override
                    public void onClick(int groupPosition, int position,
                                        PraiseTextView.PraiseInfo mPraiseInfo) {}

                    @Override
                    public void onPraiseOtherClick() {}

                    @Override
                    public void onCommentItemLongClick(int groupPosition, int commentPosition,
                                                       final CircleCommentBean commentItem, final CommentConfig comConfig, View v) {
                        List<MallBean> list = new ArrayList<>();
                        MallBean mall = new MallBean();
                        mall.setName("复制");
                        list.add(mall);
                        if(commentItem.getUserCode().equals(UserManager.getInstance().getUserCode())) {
                            MallBean mall1 = new MallBean();
                            mall1.setName("删除");
                            list.add(mall1);
                        }
                        final int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        int mode;
                        if(location[1] >= 600){
                            mode = Gravity.TOP;
                        }else{
                            mode = Gravity.BOTTOM;
                        }
                        MallHandleUtil.showPop(MyDynamicActivity.this, v, list, mode,
                                new BaseRecyclerAdapter.OnItemClickLisntener() {
                                    @Override
                                    public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
                                        switch (position){
                                            case 0 :
                                                ClipboardManager cmb = (ClipboardManager) MyDynamicActivity.
                                                        this.getSystemService(Context.CLIPBOARD_SERVICE);
                                                cmb.setText(commentItem.getContent().trim());
                                                ToastUtil.showShort(MyDynamicActivity.this, "已复制");
                                                break;
                                            case 1 :
                                                if (mPresenter != null) mPresenter.removeCommentItem(comConfig);

                                                break;
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onPopCommentClick(CommentConfig commentConfig) {
                        int id = commentConfig.groupInfoId;
                        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                            if(id == mAdapter.getDatas().get(i).getId()){
                                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                                View v = manager.findViewByPosition(i + mRecyclerView.getHeadCount());
                                if(v != null){
                                    commentConfig.item = v;
                                }
                                break;
                            }
                        }
                        mPresenter.showEditTextBody(commentConfig);
                    }

                    @Override
                    public void onCommentItemClick(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            if (!commentConfig.targetUserCode.equals(
                                    UserManager.getInstance().getUserCode())) {
                                mPresenter.showEditTextBody(commentConfig);
                            }
//                            else {
//                                showRemoreCommentItemDialog(commentConfig);
//                            }
                        }
                    }

                    @Override
                    public void onCancelZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

                        } else {
                            Toast.makeText(MyDynamicActivity.this, "请先登录",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDianZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

                        } else {
                            Toast.makeText(MyDynamicActivity.this, "请先登录",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onMoreCommentClick(CommentConfig commentConfig) {
                        DynamicDetialActivity.startActivity(MyDynamicActivity.this,
                                commentConfig.groupInfoId + "");
                    }

                    @Override
                    public void onRemoveCommentClick(CommentConfig commentConfig) {
                        showRemoreCommentDialog(commentConfig);
                    }
                });
        mAdapter.setOnShareAndLikeItemListener(
                new CircleDynamicAdapter.OnShareAndLikeItemListener() {
                    @Override
                    public void goToCircleHome(CircleHomeBean bean) {
                        PersonalInfoActivity.startActivity(MyDynamicActivity.this,
                                bean.getUserCode());
                    }

                    @Override
                    public void notifyRecyclerView(String userCode) {
                        for (CircleHomeBean bean : mAdapter.getDatas()) {
                            if (!TextUtils.isEmpty(bean.getUserCode())) {
                                if (bean.getUserCode().equals(userCode)) {
                                    bean.setIsAttention(1);
                                }
                            }
                        }
                        mRecyclerView.notifyChangeData();
                    }

                    @Override
                    public void onShield(int position, CircleHomeBean bean, View view) {
                        shieldPosition = position;
                        mBean = bean;
                        showShieldDialog(view);

                    }

                    @Override
                    public void shareItem(int position, CircleHomeBean bean) {
                        shareIssueNamic(bean);
                    }
                });
    }

    private UMShareUtils shareUtils;

    private void shareIssueNamic(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(MyDynamicActivity.this);

                        if (!TextUtils.isEmpty(bean.getUserPic())) {
                            shareUtils.shareCircleIssueDynamic(bean.getUserPic(), "这条动态很有意思，快来围观吧",
                                    bean.getContent(), bean.getUrl());
                        } else {
                            shareUtils.shareCircleIssueDynamic(R.mipmap.person_icon_head_share,
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        }

                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 0) {//复制链接
                                    ClipboardManager cmb = (ClipboardManager) MyDynamicActivity.this.getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(bean.getUrl().trim());
                                    ToastUtil.showShort(MyDynamicActivity.this, "复制成功");
                                } else if (flag == 1) {//收藏
                                    if (UserManager.getInstance().isLogin())
                                        collect(UserManager.getInstance().getToken(),
                                                String.valueOf(bean.getId()));
                                    else GotoUtil.goToActivity(MyDynamicActivity.this,
                                            LoginAndRegisteActivity.class);
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {
                                        ReportActivity.jumptoReportActivity(MyDynamicActivity.this,
                                                String.valueOf(bean.getId()), "5");
                                    } else {
                                        gotoLogin();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(MyDynamicActivity.this, false,
                                null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(
                                                    MyDynamicActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
    }

    private void collect(String token, String id) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "4");
        map.put("bizId", id);

        Subscription subCollect = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(MyApplication.getInstance(), "收藏成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(), message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, subCollect);
    }

    private void showShieldDialog(View view) {
        shieldDialog = new CircleHomeShieldDialogV5(MyDynamicActivity.this);
        shieldDialog.showAnim(null).dismissAnim(null).anchorView(view).dimEnabled(true).location(10,
                -50).dimEnabled(false).gravity(Gravity.BOTTOM).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_shield_dynamic://屏蔽此条动态

                                if (UserManager.getInstance().isLogin())
                                    shieldDynamic(shieldPosition, mBean, "1");
                                else GotoUtil.goToActivity(MyDynamicActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                            case R.id.tv_shield_user://屏蔽此人动态
                                if (UserManager.getInstance().isLogin()) {

                                    mSureDialog = DialogUtil.createDialog2(MyDynamicActivity.this,
                                            "确定屏蔽此人所有动态？", "取消", "确定", new DialogUtil.DialogClickListener() {
                                                @Override
                                                public void clickLeftButton(View view) {
                                                    mSureDialog.dismiss();
                                                }

                                                @Override
                                                public void clickRightButton(View view) {
                                                    mSureDialog.dismiss();
                                                    shieldDynamic(shieldPosition, mBean, "0");
                                                }
                                            });
                                    mSureDialog.show();

                                } else GotoUtil.goToActivity(MyDynamicActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                        }
                    }
                });

        shieldDialog.show();
    }

    Subscription subShield;

    private void shieldDynamic(final int position, final CircleHomeBean bean, final String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if ("1".equals(type)) map.put("targetId", String.valueOf(bean.getId()));
        else if ("0".equals(type)) map.put("targetId", String.valueOf(bean.getUserCode()));
        map.put("type", type);

        subShield = AllApi.getInstance().shield(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if ("1".equals(type)) {
                            mRecyclerView.removeData(mAdapter, position);
                        } else if ("0".equals(type)) {
                            String userCode = bean.getUserCode();
                            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                                CircleHomeBean bean = mAdapter.getDatas().get(i);
                                if (userCode.equals(bean.getUserCode())) {
                                    mAdapter.getDatas().remove(bean);
                                    i--;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.notifyChangeData();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(MyDynamicActivity.this, errorCode,
                                message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, subShield);
    }

    private void showRemoreCommentDialog(final CommentConfig commentConfig) {
        DynamicUtil.showDoalogConfiromRemove(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter != null) {
                    mPresenter.removeComment(commentConfig);
                }
            }
        });
    }

    private ArrayList<String> mMenuRemoveCommentItems = new ArrayList<>();

    {
        mMenuRemoveCommentItems.add("删除");

    }

    private void showRemoreCommentItemDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mPresenter.removeCommentItem(commentConfig);
                }
                actionSheetDialog.dismiss();
            }
        });
    }

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    {
        mMenuItems.add(new DialogMenuItem("收藏", 0));
        mMenuItems.add(new DialogMenuItem("下载", 0));
    }

    private void showCopyOrRemoveDialg(int groupPosition, int position,
                                       CommentListTextView.CommentInfo mInfo) {
        final NormalListDialog dialog = new NormalListDialog(this, mMenuItems);
        dialog.title("请选择")//
              .isTitleShow(false)//
              .itemPressColor(Color.parseColor("#85D3EF"))//
              .itemTextColor(Color.parseColor("#303030"))//
              .itemTextSize(15)//
              .cornerRadius(2)//
              .widthScale(0.75f)//
              .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mAlertDialog = null;
        TextLineUtile.clearTextLineCache();
        JZMediaManager.instance().releaseMediaPlayer();     //释放内存泄露的资源
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void showData(List<CircleHomeBean> datas) {

    }


    @Override
    public void update2DeleteCircle(int circleId) {

        List<CircleHomeBean> circleItems = mAdapter.getDatas();
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId == circleItems.get(i).getId()) {
                circleItems.remove(i);
                mRecyclerView.notifyChangeData();

                return;
            }
        }
    }

    @Override
    public void update2AddFavorite(int circlePosition, ThumbsupVosBean data) {
        if (mAdapter != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            data.setUserCode(UserManager.getInstance().getUserCode());
            data.setUserName(UserManager.getInstance().getUserName());
            item.getThumbsupVos().add(data);
            item.setIsDZ(1);

            mRecyclerView.notifyItemChanged(circlePosition);
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
    }

    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        String                userCode = UserManager.getInstance().getUserCode();
        CircleHomeBean        item     = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<ThumbsupVosBean> items    = item.getThumbsupVos();
        for (int i = 0; i < items.size(); i++) {
            if (userCode.equals(items.get(i).getUserCode())) {
                items.remove(i);
                item.setIsDZ(0);
                mRecyclerView.notifyItemChanged(circlePosition);
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CircleCommentBean addItem) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.dismiss();
        }
        if (addItem != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            item.getCommentVos().add(addItem);
//            mAdapter.notifyDataSetChanged();
            mRecyclerView.notifyItemChanged(circlePosition);
        }
        //清空评论文本
        mCommentDialog.clearContent();
    }

    @Override
    public void update2DeleteComment(int circlePosition, int commentId) {
        CircleHomeBean          item  = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<CircleCommentBean> items = item.getCommentVos();
        for (int i = 0; i < items.size(); i++) {
            if (commentId == (items.get(i).getId())) {
                Log.d("tag", "update2DeleteComment: " + commentId + " " + items.get(i).getId());
                item.setLiuYan(item.getLiuYan() - 1);
                items.remove(i);
                mRecyclerView.notifyItemChanged(circlePosition);
                return;
            }
        }
    }

    @Override
    public void showHeadData(List<MineCircleBean> data) {

    }

    @Override
    public void headEmpty() {

    }

    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {

    }

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {

    }

    @Override
    public void showNoMore() {

    }

    private CommentConfig        commentConfig;
    private DynamicCommentDialog mCommentDialog;

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;

        //用户已经登录
        if (UserManager.getInstance().isLogin()) {
            ListScrollUtil.INSTANCE.onPressView(commentConfig.item);
            mCommentDialog = new DynamicCommentDialog();
            mCommentDialog.setSendListener(new DynamicCommentDialog.OnSendListener() {
                @Override
                public void sendComment(String content) {
                    if (mPresenter != null) {
                        if (TextUtils.isEmpty(content)) {
                            Toast.makeText(MyDynamicActivity.this, "评论内容不能为空...",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        switch (MyDynamicActivity.this.commentConfig.commentType) {
                            case PUBLIC:
                                mPresenter.addComment(content,
                                        MyDynamicActivity.this.commentConfig);
                                break;
                            case REPLY:
                                mPresenter.addCommentReply(content,
                                        MyDynamicActivity.this.commentConfig);
                                break;
                        }
                    }
                }
            });

            if (!TextUtils.isEmpty(commentConfig.targetUserName)) {
                mCommentDialog.setText("回复 " + commentConfig.targetUserName + "：");
            }
            mCommentDialog.show(getSupportFragmentManager(), DynamicCommentDialog.class.getSimpleName());
        } else {
            gotoLogin();
        }

    }

    @Override
    public void update2AddComment(int circlePosition, List<CircleCommentBean> data) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.dismiss();
        }
        if (data != null) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
            item.getCommentVos().addAll(data);
//            mAdapter.notifyDataSetChanged();
            mRecyclerView.notifyItemChanged(circlePosition);
        }


    }

    @Override
    public void update2DeleteCircleItem(int groupInfoId, int commentPosition) {

    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {

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
    public void setPresenter(CircleHomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }
}
