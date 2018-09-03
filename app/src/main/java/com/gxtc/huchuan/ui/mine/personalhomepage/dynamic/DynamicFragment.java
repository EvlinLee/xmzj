package com.gxtc.huchuan.ui.mine.personalhomepage.dynamic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.base.EmptyView;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.event.EventCircleCommentBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleHomeCollectDialogV5;
import com.gxtc.huchuan.dialog.ComplaintDialog;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.pop.KotlinPopCircleAction;
import com.gxtc.huchuan.ui.circle.home.CircleHomeContract;
import com.gxtc.huchuan.ui.circle.home.CircleHomePresenter;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mall.MallHandleUtil;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.MyDynamicActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalHomePageMoreContract;
import com.gxtc.huchuan.ui.mine.personalhomepage.more.PersonalHomePageMorePresenter;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DynamicUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.ListScrollUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RelayHelper;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.PraiseTextView;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;

/**
 * Describe: 个人主页 > 动态
 * Created by ALing on 2017/4/19.
 */

@SuppressLint("ValidFragment")
public class DynamicFragment extends BaseTitleFragment {
    private static final String TAG = DynamicFragment.class.getSimpleName();

    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.base_empty_area)
    View emptyView;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;

    private EmptyView mEmptyView;

    private PersonalHomePageMoreContract.Presenter mPresenter;
    private CircleHomeContract.Presenter mCirclePresenter;    //这里不想改太多代码 直接复用presenter
    private String token;
    private String userCode;
    private CircleDynamicAdapter mAdapter;
    private UMShareUtils shareUtils;
    private Subscription sub;

    private ComplaintDialog complaintDialog;//举报对话框
    private AlertDialog.Builder builder;
    private AlertDialog sureDialog;
    private CircleHomeCollectDialogV5 collectDialog;
    private DynamicCommentDialog mCommentDialog;

    private int copyPosition;
    private KotlinPopCircleAction mPopComment;
    private CompositeSubscription mSubscriptions;
    private AlertDialog mAlertDialog;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_personal_homepage, container, false);
        return view;
    }

    @Override
    public void initListener() {

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                if (!TextUtils.isEmpty(userCode)) {
                    mPresenter.getHomePageGroupInfoList(userCode, true);
                } else {
                    mPresenter.getHomePageGroupInfoList(UserManager.getInstance().getUserCode(),
                            true);
                }
                mRcList.reLoadFinish();
            }
        });

        mRcList.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                switch (newState) {
                    //滑动停止，开始加载
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //用户手指慢慢滑也恢复加载
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if (mAdapter != null) {
                            Glide.with(MyApplication.getInstance()).resumeRequests();
                        }
                        break;

                    //滑动
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        if (mAdapter != null) {
                            Glide.with(MyApplication.getInstance()).pauseRequests();
                        }
                        break;
                }
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
            }
        });


        KeyboardUtils.registerSoftInputChangedListener(getActivity(), new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChangeState(int state) {
                if (mCommentDialog != null && mCommentDialog.isVisible()) {
                    if (state == KeyboardUtils.KEYBOARD_OPEN) {
                        int[] size = new int[2];
                        mCommentDialog.getEdit().getLocationOnScreen(size);
                        ListScrollUtil.INSTANCE.scrollList(mRcList, size[1]);
                    }
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        Glide.with(MyApplication.getInstance()).pauseAllRequests();
    }

    @Override
    public void initData() {
        super.initData();
        EventBusUtil.register(this);
        mEmptyView = new EmptyView(emptyView);
        mSubscriptions = new CompositeSubscription();
        new PersonalHomePageMorePresenter(mHomeView);
        new CircleHomePresenter(mCircleView);

        initRecyclerView();
        //获取数据
        getHomePageData();
    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        int[] res = new int[]{R.layout.item_circle_home_noimgv5, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv5, R.layout.item_circle_home_no_support};
        WeakReference<Activity> weakReference = new WeakReference<Activity>(getActivity());
        if (weakReference.get() == null) return;
        mAdapter = new CircleDynamicAdapter(false, mRcList, getContext(), weakReference.get(), new ArrayList<CircleHomeBean>(), res);

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(userCode)) {
                    mPresenter.loadMrore("dymic", userCode);
                } else {
                    mPresenter.loadMrore("dymic", UserManager.getInstance().getUserCode());
                }
            }
        });


        mAdapter.setOnReItemOnLongClickListener(
                new BaseMoreTypeRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        int collectId = mAdapter.getDatas().get(position).getId();
                        copyPosition = position;
                        showCollectPop(v, collectId);
                    }
                });


        mAdapter.setOnCommentAndPraiseClickListener(
                new CircleDynamicAdapter.OnCommentAndPraiseClickListener() {
                    @Override
                    public void onNickNameClick(int groupPosition, int position, CommentListTextView.CommentInfo mInfo) {
                        //如果是自己的 弹出Dialog 显示  复制 删除
                        if (UserManager.getInstance().isLogin()) {
                            if (mInfo.getNickUserCode().equals(
                                    UserManager.getInstance().getUserCode())) {
                                showCopyOrRemoveDialg(groupPosition, position, mInfo);
                            }
                        }
                        //如果是别的  显示 评论输入框
                        Log.d("CircleMainActivity", "onNickNameClickmInfo:" + mInfo);
                    }

                    @Override
                    public void onToNickNameClick(int groupPosition, int position,
                                                  CommentListTextView.CommentInfo mInfo) {
                        Log.d("CircleMainActivity", "onToNickNameClickmInfo:" + mInfo);
                    }

                    @Override
                    public void onCommentItemClick(int groupPosition, int position,
                                                   CircleCommentBean mInfo) {
                    }

                    @Override
                    public void onCommentOtherClick() {
                        //如果按到的是显示全部  去服务器请求更多
                        Log.d("CircleMainActivity", "mInfo:onCommentOtherClick:");
                    }

                    @Override
                    public void onClick(int groupPosition, int position,
                                        PraiseTextView.PraiseInfo mPraiseInfo) {
                        Log.d("CircleMainActivity", "mPraiseInfo:" + mPraiseInfo);
                        //用户名被点击  显示更多。
                    }

                    @Override
                    public void onPraiseOtherClick() {
                        Log.d("CircleMainActivity", "mPraiseInfo:onPraiseOtherClick");
                    }

                    @Override
                    public void onCommentItemLongClick(int groupPosition, int commentPosition,
                                                       final CircleCommentBean commentItem, final CommentConfig comConfig, View v) {
                        List<MallBean> list = new ArrayList<>();
                        MallBean mall = new MallBean();
                        mall.setName("复制");
                        list.add(mall);
                        if (commentItem.getUserCode().equals(UserManager.getInstance().getUserCode())) {
                            MallBean mall1 = new MallBean();
                            mall1.setName("删除");
                            list.add(mall1);
                        }
                        final int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        int mode;
                        if (location[1] >= 600) {
                            mode = Gravity.TOP;
                        } else {
                            mode = Gravity.BOTTOM;
                        }
                        MallHandleUtil.showPop(getActivity(), v, list, mode,
                                new BaseRecyclerAdapter.OnItemClickLisntener() {
                                    @Override
                                    public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
                                        switch (position) {
                                            case 0:
                                                ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                                cmb.setText(commentItem.getContent().trim());
                                                ToastUtil.showShort(getContext(), "已复制");
                                                break;
                                            case 1:
                                                if (mCirclePresenter != null)
                                                    mCirclePresenter.removeCommentItem(comConfig);
                                                break;
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onPopCommentClick(CommentConfig commentConfig) {
                        int id = commentConfig.groupInfoId;
                        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                            if (id == mAdapter.getDatas().get(i).getId()) {
                                LinearLayoutManager manager = (LinearLayoutManager) mRcList.getLayoutManager();
                                View v = manager.findViewByPosition(i + mRcList.getHeadCount());
                                if (v != null) {
                                    commentConfig.item = v;
                                }
                                break;
                            }
                        }
                        mCirclePresenter.showEditTextBody(commentConfig);
                    }

                    @Override
                    public void onCommentItemClick(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            if (!commentConfig.targetUserCode.equals(
                                    UserManager.getInstance().getUserCode())) {
                                mCirclePresenter.showEditTextBody(commentConfig);

                            }
//                            else {
//                                showRemoreCommentItemDialog(commentConfig);
//
//                            }
                        }
                    }

                    @Override
                    public void onCancelZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mCirclePresenter.support(UserManager.getInstance().getToken(),
                                    commentConfig);

                        } else {
                            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDianZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mCirclePresenter.support(UserManager.getInstance().getToken(),
                                    commentConfig);

                        } else {
                            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onMoreCommentClick(CommentConfig commentConfig) {
                        DynamicDetialActivity.startActivity(getContext(), commentConfig.groupInfoId + "");
                    }

                    @Override
                    public void onRemoveCommentClick(final CommentConfig commentConfig) {
                        DynamicUtil.showDoalogConfiromRemove(getActivity(), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mPresenter != null) {
                                    mCirclePresenter.removeComment(commentConfig);
                                }
                            }
                        });
                    }
                });

        mAdapter.setOnShareAndLikeItemListener(
                new CircleDynamicAdapter.OnShareAndLikeItemListener() {

                    @Override
                    public void goToCircleHome(CircleHomeBean bean) {
                        PersonalInfoActivity.startActivity(getContext(), bean.getUserCode());
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
                        mRcList.notifyChangeData();
                    }

                    @Override
                    public void onShield(int position, CircleHomeBean bean, View view) {
                    }

                    @Override
                    public void shareItem(int position, CircleHomeBean bean) {
                        shareNews(bean);
                    }
                });


        mRcList.setAdapter(mAdapter);
    }

    private void showCollectPop(View v, final int collectId) {
        mPopComment = new KotlinPopCircleAction(MyApplication.getInstance());
        if (TextUtils.isEmpty(mAdapter.getDatas().get(copyPosition).getContent())) {
            mPopComment.setisShowReply(false);
        } else {
            mPopComment.setisShowReply(true);
        }
        mPopComment.setAtLocation(v, MyApplication.getInstance().getApplicationContext());
        mPopComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_circle_copy://复制
                        ClipboardManager cmb = (ClipboardManager) DynamicFragment.this.getContext().getSystemService(
                                Context.CLIPBOARD_SERVICE);
                        cmb.setText(
                                mAdapter.getDatas().get(copyPosition).getContent().trim());
                        ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), "已复制");
                        break;
                    case R.id.tv_reply://转发
                        if (UserManager.getInstance().isLogin())
                            ConversationListActivity.startActivity(getActivity(), REQUEST_SHARE_RELAY, Constant.SELECT_TYPE_RELAY);
                        else GotoUtil.goToActivity(getActivity(),
                                LoginAndRegisteActivity.class);
                        break;
                    case R.id.tv_circle_collect://收藏
                        if (UserManager.getInstance().isLogin())
                            collect(UserManager.getInstance().getToken(),
                                    String.valueOf(collectId));
                        else GotoUtil.goToActivity(getActivity(),
                                LoginAndRegisteActivity.class);
                        break;
                }
            }
        });
    }


    //userCode 不为空，则获取用户个人主页列表，为空则获取自己个人主页列表
    private void getHomePageData() {
        if (!TextUtils.isEmpty(userCode)) {
            mPresenter.getHomePageGroupInfoList(userCode, false);
        } else {
            mPresenter.getHomePageGroupInfoList(UserManager.getInstance().getUserCode(), false);
        }
    }


    private boolean flag = false;

    private void deleteItem(final CircleHomeBean bean, final int position) {
        if (flag) {
            return;
        }
        flag = true;
        sub = AllApi.getInstance().deleteDT(UserManager.getInstance().getToken(),
                bean.getId()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        mRcList.removeData(mAdapter, position);
                        flag = false;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        flag = false;
                        LoginErrorCodeUtil.showHaveTokenError(DynamicFragment.this.getActivity(),
                                errorCode, message);
                    }
                }));
    }

    /**
     * 分享
     *
     * @param bean
     */
    private void share(final CircleHomeBean bean, final int position, final boolean isMine) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(DynamicFragment.this.getActivity());
                        if (bean.getPicList().size() > 0) {
                            shareUtils.shareDynamic(bean.getPicList().get(0).getPicUrl(),
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl(), isMine);

                        } else {
                            if (!"".equals(bean.getUserPic())) {
                                shareUtils.shareDynamic(bean.getUserPic(), "这条动态很有意思，快来围观吧",
                                        bean.getContent(), bean.getUrl(), isMine);
                            } else {
                                shareUtils.shareDynamic(R.mipmap.person_icon_head_share,
                                        "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl(), isMine);
                            }

                        }
                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 1) {
                                    if (UserManager.getInstance().isLogin()) {
                                        complaintDialog = new ComplaintDialog(
                                                DynamicFragment.this.getActivity(),
                                                DynamicFragment.this.getContext(),
                                                R.style.BottomDialog, String.valueOf(bean.getId()),
                                                "举报动态问题", "5");
                                        complaintDialog.getWindow().setGravity(Gravity.BOTTOM);
                                        complaintDialog.show();
                                    } else GotoUtil.goToActivity(getActivity(),
                                            LoginAndRegisteActivity.class);

                                }
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });
    }

    @Subscribe
    public void onEvent(EventCircleCommentBean bean) {
        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
            if (mAdapter.getDatas().get(i).getId() == bean.id) {
                mAdapter.getDatas().get(i).setLiuYan(bean.count);
                mRcList.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    @Override
    public void onDestroy() {
        mAdapter = null;
        mPresenter.destroy();
        mCirclePresenter.destroy();
        EventBusUtil.unregister(this);
        mSubscriptions.unsubscribe();
        mAlertDialog = null;
        ImageHelper.onDestroy(MyApplication.getInstance());
//        mRcList.removeOnScrollListener(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.requestCode.CIRCLE_DZ && resultCode == Constant.ResponseCode.CIRCLE_RESULT_DZ) {
            int circleId = data.getIntExtra("circle_id", -1);
            int isDz = data.getIntExtra("is_dz", -1);
            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                if (mAdapter.getDatas().get(i).getId() == circleId) {
                    mAdapter.getDatas().get(i).setIsDZ(isDz);
                    if (0 == isDz) {

                        mAdapter.getDatas().get(i).setDianZan(
                                mAdapter.getDatas().get(i).getDianZan() - 1);
                    } else if (1 == isDz) {
                        mAdapter.getDatas().get(i).setDianZan(
                                mAdapter.getDatas().get(i).getDianZan() + 1);
                    }
                    mRcList.notifyItemChanged(i);
                }
            }
        }
        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            getHomePageData();
            mRcList.reLoadFinish();

        }
        //转发消息
        if (requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK) {
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            RelayHelper.INSTANCE.relayMessage(mAdapter.getDatas().get(copyPosition).getContent(), (String) bean.mObject, bean.mType, bean.Liuyan);
        }

    }

    /**
     * ================================================ 个人主页的View ================================================
     */

    private PersonalHomePageMoreContract.View mHomeView = new PersonalHomePageMoreContract.View() {

        @Override
        public void showLoad() {
            getBaseLoadingView().showLoading();
        }

        @Override
        public void showLoadFinish() {
            if (mSwipeLayout != null) mSwipeLayout.setRefreshing(false);
            getBaseLoadingView().hideLoading();
        }

        @Override
        public void showEmpty() {
            mScrollView.setVisibility(View.VISIBLE);
            mEmptyView.showEmptyContent();
        }

        @Override
        public void showReLoad() {
            getHomePageData();
        }

        @Override
        public void showError(String info) {
            ToastUtil.showShort(getContext(), info);
            if (mCommentDialog != null) {
                mCommentDialog.reset();
            }

        }

        @Override
        public void showNetError() {
            mScrollView.setVisibility(View.VISIBLE);
            mEmptyView.showNetWorkView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showReLoad();
                }
            });
        }

        @Override
        public void setPresenter(PersonalHomePageMoreContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void showHomePageGroupInfoList(List<CircleHomeBean> list) {
            mSwipeLayout.setRefreshing(false);
            mEmptyView.hideEmptyView();
            mRcList.notifyChangeData(list, mAdapter);
        }

        @Override
        public void showDZSuccess(int id) {
            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                CircleHomeBean bean = mAdapter.getDatas().get(i);
                if (bean.getId() == id) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRcList.getLayoutManager();
                    View v = layoutManager.findViewByPosition(i);
                    View v1 = v.findViewById(R.id.rl_circle_home_like);
                    TextView tv = (TextView) v1.findViewById(
                            R.id.tv_circle_home_like);

                    //0是没有点赞
                    if (bean.getIsDZ() == 0) {
                        Drawable drawable = getContext().getResources().getDrawable(
                                R.drawable.circle_home_icon_zan_select);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());

                        tv.setCompoundDrawables(drawable, null, null, null);
                        tv.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                        mAdapter.getDatas().get(i).setDianZan(bean.getDianZan() + 1);
                        mAdapter.getDatas().get(i).setIsDZ(1);

                        //1是已经点赞
                    } else {
                        Drawable drawable = getContext().getResources().getDrawable(
                                R.drawable.circle_home_icon_zan);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());

                        tv.setCompoundDrawables(drawable, null, null, null);
                        tv.setTextColor(
                                getContext().getResources().getColor(R.color.text_color_999));
                        mAdapter.getDatas().get(i).setDianZan(bean.getDianZan() - 1);
                        mAdapter.getDatas().get(i).setIsDZ(0);
                    }
                    tv.setText(String.valueOf(mAdapter.getDatas().get(i).getDianZan()));
                }
            }
        }

        @Override
        public void showSelfNewsList(List<NewsBean> list) {
        }

        @Override
        public void showUserNewsList(List<NewsBean> list) {
        }

        @Override
        public void showSelfChatInfoList(List<HomePageChatInfo> list) {
        }

        @Override
        public void showUserChatInfoList(List<HomePageChatInfo> list) {
        }

        @Override
        public void showSelfDealList(List<DealListBean> list) {
        }

        @Override
        public void showUserDealList(List<DealListBean> list) {
        }

        @Override
        public void showLoadMoreNewsList(List<NewsBean> list) {
        }

        @Override
        public void showLoadMoreChatInfoList(List<HomePageChatInfo> list) {
        }

        @Override
        public void showLoadMoreHomePageGroupInfoList(List<CircleHomeBean> list) {
            if (mAdapter != null) mRcList.changeData(list, mAdapter);
        }

        @Override
        public void showLoadMoreDealList(List<DealListBean> list) {
        }

        @Override
        public void showNoMore() {
            mRcList.loadFinish();
        }

        @Override
        public void tokenOverdue() {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    };


    /**
     * ================================================ 我的动态相关 ================================================
     */

    private CommentConfig mCommentConfig;

    private CircleHomeContract.View mCircleView = new CircleHomeContract.View() {

        @Override
        public void showData(List<CircleHomeBean> datas) {
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

        @Override
        public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
            mCommentConfig = commentConfig;
            //用户已经登录
            if (UserManager.getInstance().isLogin()) {
                ListScrollUtil.INSTANCE.onPressView(commentConfig.item);
                mCommentDialog = new DynamicCommentDialog();
                mCommentDialog.setSendListener(new DynamicCommentDialog.OnSendListener() {
                    @Override
                    public void sendComment(String content) {
                        if (!UserManager.getInstance().isLogin()) {
                            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mCirclePresenter != null) {
                            if (TextUtils.isEmpty(content)) {
                                Toast.makeText(getContext(), "评论内容不能为空...",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            switch (mCommentConfig.commentType) {
                                case PUBLIC:
                                    mCirclePresenter.addComment(content, mCommentConfig);
                                    break;
                                case REPLY:
                                    mCirclePresenter.addCommentReply(content, mCommentConfig);
                                    break;
                            }
                        }
                    }
                });

                if (!TextUtils.isEmpty(commentConfig.targetUserName)) {
                    mCommentDialog.setText("回复 " + commentConfig.targetUserName + "：");
                }
                mCommentDialog.show(getChildFragmentManager(), DynamicCommentDialog.class.getSimpleName());
            } else {
                Intent intent = new Intent(getContext(), LoginAndRegisteActivity.class);
                startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
            }

        }

        @Override
        public void update2DeleteCircle(int circleId) {
            ToastUtil.showShort(getContext(), "删除成功");
            List<CircleHomeBean> circleItems = mAdapter.getDatas();
            for (int i = 0; i < circleItems.size(); i++) {
                if (circleId == circleItems.get(i).getId()) {
                    circleItems.remove(i);
                    mRcList.notifyChangeData();
                    return;
                }
            }
            ToastUtil.showShort(MyApplication.getInstance(), "删除成功");
        }

        @Override
        public void update2AddFavorite(int circlePosition, ThumbsupVosBean data) {
            if (mAdapter != null) {
                CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
                data.setUserCode(UserManager.getInstance().getUserCode());
                data.setUserName(UserManager.getInstance().getUserName());
                item.getThumbsupVos().add(data);
                item.setIsDZ(1);

                mRcList.notifyItemChanged(circlePosition);
                //circleAdapter.notifyItemChanged(circlePosition+1);
            }
        }

        @Override
        public void update2DeleteFavort(int circlePosition, String favortId) {
            Log.d(TAG, "update2DeleteFavort: " + circlePosition);
            if (!UserManager.getInstance().isLogin()) {
                return;
            }
            String userCode = UserManager.getInstance().getUserCode();
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(
                    circlePosition);
            List<ThumbsupVosBean> items = item.getThumbsupVos();
            for (int i = 0; i < items.size(); i++) {
                if (userCode.equals(items.get(i).getUserCode())) {
                    Log.d(TAG, userCode + " 相同  " + items.get(i).getUserCode());
                    items.remove(i);
                    item.setIsDZ(0);
//                mAdapter.notifyDataSetChanged();
                    mRcList.notifyItemChanged(circlePosition);
                    //circleAdapter.notifyItemChanged(circlePosition+1);
                    return;
                } else {
                    Log.d(TAG, userCode + " 不同  " + items.get(i).getUserCode());
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
                mRcList.notifyItemChanged(circlePosition);
            }

            //清空评论文本
            mCommentDialog.clearContent();
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
                mRcList.notifyItemChanged(circlePosition);
            }
        }

        @Override
        public void update2DeleteCircleItem(int groupInfoId, int commentPosition) {
        }

        @Override
        public void update2DeleteComment(int circlePosition, int commentId) {
            CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(
                    circlePosition);
            List<CircleCommentBean> items = item.getCommentVos();
            for (int i = 0; i < items.size(); i++) {
                if (commentId == (items.get(i).getId())) {
                    Log.d(TAG, "update2DeleteComment: " + commentId + " " + items.get(i).getId());
                    item.setLiuYan(item.getLiuYan() - 1);
                    items.remove(i);
//                mAdapter.notifyDataSetChanged();
                    mRcList.notifyItemChanged(circlePosition);
                    //circleAdapter.notifyItemChanged(circlePosition+1);
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
            mCirclePresenter = presenter;
        }

        @Override
        public void tokenOverdue() {
            GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
        }
    };


    //收藏
    private void collect(String token, String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "4");
        map.put("bizId", id);

        Subscription sub = AllApi.getInstance().saveCollection(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(getContext(), "收藏成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(getActivity(), errorCode, message);
                    }
                }));

        mSubscriptions.add(sub);

    }

    //分享
    private void shareNews(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(getActivity());

                        if (bean.getPicList().size() > 0) {
                            shareUtils.shareCircleIssueDynamic(bean.getPicList().get(0).getPicUrl(),
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        } else {
                            shareUtils.shareCircleIssueDynamic(R.mipmap.person_icon_head_share,
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        }

                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 0) {//复制链接
                                    ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(bean.getUrl().trim());
                                    ToastUtil.showShort(getContext(), "复制成功");
                                } else if (flag == 1) {//收藏
                                    if (UserManager.getInstance().isLogin())
                                        collect(UserManager.getInstance().getToken(),
                                                String.valueOf(bean.getId()));
                                    else GotoUtil.goToActivity(getActivity(),
                                            LoginAndRegisteActivity.class);
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {

                                        complaintDialog = new ComplaintDialog(getActivity(),
                                                getContext(), R.style.BottomDialog,
                                                String.valueOf(bean.getId()), "举报动态问题", "5");
                                        complaintDialog.getWindow().setGravity(Gravity.BOTTOM);
                                        complaintDialog.show();

                                    } else {
                                        Intent intent = new Intent(getActivity(),
                                                LoginAndRegisteActivity.class);
                                        startActivityForResult(intent,
                                                Constant.requestCode.NEWS_LIKEANDCOLLECT);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(getActivity());
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });

    }

    private void showRemoreCommentItemDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[]{"删除"};
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext(), contents,
                null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mCirclePresenter.removeCommentItem(commentConfig);
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

    private void showCopyOrRemoveDialg(int groupPosition, int position, CommentListTextView.CommentInfo mInfo) {
        final NormalListDialog dialog = new NormalListDialog(getContext(), mMenuItems);
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


    private void showRemoreCommentDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[]{"删除"};
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext(), contents,
                null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mCirclePresenter.removeComment(commentConfig);
                }
                actionSheetDialog.dismiss();
            }
        });
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        if (bundle != null) {
            userCode = (String) bundle.get(Constant.INTENT_DATA);
        }
    }
}
