package com.gxtc.huchuan.ui.circle.home;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.utils.StatusBarUtils;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.commlibrary.widget.MyRadioGroup;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.adapter.CircleItemAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ListCircleHomeBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.event.EventCircleCommentBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventImgBean;
import com.gxtc.huchuan.bean.event.EventJPushMessgeBean;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleHomeShieldDialogV5;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.pop.KotlinPopCircleAction;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleListActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mall.MallHandleUtil;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.MyDynamicActivity;
import com.gxtc.huchuan.ui.mine.dymic.DymicMineActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DynamicUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.ListScrollUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.RelayHelper;
import com.gxtc.huchuan.utils.ShieldCircleDynamicHandler;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.PraiseTextView;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.gxtc.huchuan.Constant.STATUE_REFRESH_DYNIMIC;
import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;


/**
 * tab 圈子  推荐
 * 如果需要滑动自动播放  参考这里
 * https://github.com/lipangit/JiaoZiVideoPlayer/issues/381
 * ui改版于  10-28
 */
public class CircleHomeFragment extends BaseLazyFragment implements View.OnClickListener,
        CircleHomeContract.View {

    private final int REQUEST_JOIN = 210;
    private final int REQUEST_REFRESH_RECOME = 211;
    public final static int COMMENT_MAX_LENGHT = 400; //评论做多字数

    @BindView(R.id.rv_circle_home)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_circle_layout)
    SwipeRefreshLayout swCircleLayout;

    private CommentConfig commentConfig;

    private CircleHomeContract.Presenter mPresenter;
    private CircleDynamicAdapter mAdapter;
    private Activity mActivity;

    //屏蔽
    private int shieldPosition;
    private CircleHomeBean mBean;

    //复制或收藏
    private int copyPosition;
    private int collectId;

    private UMShareUtils shareUtils;
    private static final int CIRCLE_WEB_REQUEST = 1 << 4;

    private View mheadView;
    private AlertDialog mAlertDialog;
    private CircleHomeShieldDialogV5 shieldDialog;
    private LinearLayout messageNoteLayout;
    private ImageView messageNotePic;
    private TextView messageUnReadcount;
    private RelativeLayout mNoCircleData;
    private LinearLayout   mHaveCircleData;

    private MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<CircleHomeFragment> mWeakReference;
        CircleHomeFragment mCircleHomeFragment;

        public MyHandler(CircleHomeFragment mCircleHomeFragment) {
            mWeakReference = new WeakReference<>(mCircleHomeFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakReference.get() == null) return;
            mCircleHomeFragment = mWeakReference.get();
            switch (msg.what) {
                case STATUE_REFRESH_DYNIMIC:
                    mCircleHomeFragment.mPresenter.recommenddynamic(true, "1");
                    mCircleHomeFragment.mRecyclerView.reLoadFinish();
                    mCircleHomeFragment.mRecyclerView.scrollToPosition(0);
                    break;
            }
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_circle_home, container, false);
        EventBusUtil.register(this);
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        return view;
    }

    @Override
    public void initData() {
//        Drawable d = getResources().getDrawable(R.drawable.issue_dongtai);
//        d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());

//        getBaseHeadView().showTitle(getString(R.string.title_circle_home));
//        getBaseHeadView().getHeadRightButton().setCompoundDrawables(d, null, null, null);
//        getBaseHeadView().getHeadTitle().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ClickUtil.isFastClick()){
//                    if(mAdapter != null && mRecyclerView != null){
//                        mRecyclerView.smoothScrollToPosition(0);
//                    }
//                }
//            }
//        });
//        getBaseHeadView().showHeadRightButton("发动态", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserManager.getInstance().isLogin()) {
//                    showSelectDialog();
//                } else {
//                    GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);
//                }
//            }
//        });
        mActivity = this.getActivity();
        swCircleLayout.setColorSchemeResources(Constant.REFRESH_COLOR);


        initRecyclerView(new ArrayList<CircleHomeBean>());

    }


    private void readCache() {
        //读取主页的缓存数据
        Observable.just("recommend_home_bean")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ListCircleHomeBean>() {
                    @Override
                    public ListCircleHomeBean call(String key) {
                        ListCircleHomeBean bean = (ListCircleHomeBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ListCircleHomeBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove("recommend_home_bean");
//                          mPresenter.getData(false);
                    }

                    @Override
                    public void onNext(ListCircleHomeBean bean) {
                        if (bean != null && bean.getCircleHomeBeen() != null) {
                            if (bean.getCircleHomeBeen().size() > 0)
                                showData(bean.getCircleHomeBeen());
                        }
//                          mPresenter.getData(false);
                    }
                });
    }


    @Override
    public void initListener() {
        swCircleLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.recommenddynamic(true,"1");
                mRecyclerView.reLoadFinish();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.recommendloadMrore("1");
            }
        });

        mRecyclerView.addOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                switch (newState) {
                    //滑动停止，开始加载
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //用户手指慢慢滑也恢复加载
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if (mAdapter != null) {
                            Glide.with(mAdapter.getContext()).resumeRequests();
                        }
                        break;

                    //滑动
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        if (mAdapter != null) {
                            Glide.with(mAdapter.getContext()).pauseRequests();
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
                        ListScrollUtil.INSTANCE.scrollList(mRecyclerView, size[1]);
                    }
                }
            }
        });
    }

    private void initRecyclerView(final List<CircleHomeBean> datas) {
        if (mAdapter == null) {
            mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
            int[] res = new int[]{
                    R.layout.item_circle_home_noimgv5,
                    R.layout.item_circle_home_videov5,
                    R.layout.item_circle_home_threeimgv5,
                    R.layout.item_circle_home_no_support};
            mAdapter = new CircleDynamicAdapter(false, mRecyclerView, getContext(), getActivity(), datas, res);

            mRecyclerView.addHeadView(headView());
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        //分享或举报或删除
        setAdapterListener();
    }

    private MyRadioGroup mIndicator;
    private ViewPager mViewPager;

    private View headView() {
        if (mheadView == null)
            mheadView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_cicle_headview_new_after_new, swCircleLayout, false);
        messageNoteLayout =  mheadView.findViewById(R.id.message_note_layout);
        messageNotePic =  mheadView.findViewById(R.id.message_note_pic);
        messageUnReadcount =  mheadView.findViewById(R.id.message_note_unReadcount);
        mIndicator =  mheadView.findViewById(R.id.layoutIndicator);
        mViewPager =  mheadView.findViewById(R.id.viewpager);
        mNoCircleData = mheadView.findViewById(R.id.rl_nodata);
        mHaveCircleData = mheadView.findViewById(R.id.ll_have_data);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewPager.getLayoutParams();
//        params.height = (int) (WindowUtil.getScreenW(getContext()) * 0.3);

        initDynamicUnread();

        messageNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                EventBusUtil.postStickyEvent(new EventJPushMessgeBean(null, null, "0", null));
                String userCode = UserManager.getInstance().getUserCode();
                ACache.get(getContext()).removeByAsync(userCode + EventJPushMessgeBean.class.getSimpleName());
                GotoUtil.goToActivity(getActivity(), DymicMineActivity.class);
            }
        });
        initHeadRecyclerView();

        LinearLayout createCircle =  mheadView.findViewById(R.id.ll_my_cicle);        //我的圈子
        LinearLayout findCircle = mheadView.findViewById(R.id.ll_cicle_find);        //发现圈子
        LinearLayout mineIssue = mheadView.findViewById(R.id.ll_cicle_mine_issue);   //我的动态

        createCircle.setOnClickListener(this);
        findCircle.setOnClickListener(this);
        mineIssue.setOnClickListener(this);

        return mheadView;
    }

    Subscription sub;

    /**
     * 头部发现圈子改成我的圈子
     */
    private void initHeadRecyclerView() {
        Observable.just("circle_home_head_bean")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ListCircleHomeBean>() {
                    @Override
                    public ListCircleHomeBean call(String key) {
                        ListCircleHomeBean bean = (ListCircleHomeBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ListCircleHomeBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove("circle_home_head_bean");
                    }

                    @Override
                    public void onNext(ListCircleHomeBean datas) {
                        if (datas != null && datas.getMineCircleBeen().size() != 0) {
                            showHeadRecyclerView(datas.getMineCircleBeen());
                        }
                        String token = null;
                        if (UserManager.getInstance().isLogin()) {
                            token = UserManager.getInstance().getToken();
                        }
                        sub = AllApi.getInstance()
                                .getListRecommend(token, 0)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack<List<MineCircleBean>>() {
                                    @Override
                                    public void onSuccess(List<MineCircleBean> datas) {
                                        if (datas != null && datas.size() != 0) {
                                            showHeadRecyclerView(datas);
                                        }
                                    }

                                    @Override
                                    public void onError(String errorCode, String message) {
                                        if (ErrorCodeUtil.TOKEN_OVERDUE_10001 == Integer.parseInt(errorCode)) {
                                            mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                                        ReLoginUtil.ReloginTodo(getActivity());
                                                    }
                                                    mAlertDialog.dismiss();
                                                }
                                            });
                                        } else {
                                            ToastUtil.showShort(getContext(), message);
                                            mNoCircleData .setVisibility(View.VISIBLE);
                                            mHaveCircleData.setVisibility(View.GONE);
                                            mViewPager.setVisibility(View.GONE);
                                            mIndicator.setVisibility(View.GONE);
                                        }
                                    }
                                }));
                    }
                });
    }


    private void initDynamicUnread() {
        if (messageNoteLayout != null) {
            if (UserManager.getInstance().isLogin()) {
                Observable.just(UserManager.getInstance().getUserCode())
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<String, EventJPushMessgeBean>() {
                            @Override
                            public EventJPushMessgeBean call(String s) {
                                return (EventJPushMessgeBean) ACache.get(getContext()).getAsObject(s + EventJPushMessgeBean.class.getSimpleName());
                            }
                        })
                        .filter(new Func1<EventJPushMessgeBean, Boolean>() {
                            @Override
                            public Boolean call(EventJPushMessgeBean event) {
                                return event != null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<EventJPushMessgeBean>() {
                            @Override
                            public void call(EventJPushMessgeBean event) {
                                if (event != null) {
                                    messageNoteLayout.setVisibility(View.VISIBLE);
                                    messageUnReadcount.setText(event.unReadNum + "条新动态");
                                    ImageHelper.loadImage(getContext(), messageNotePic, event.userPic);
                                    messageMode = null;
                                }
                            }
                        });
            } else {
                messageNoteLayout.setVisibility(View.GONE);
            }
        }
    }

    private CircleItemAdapter recommendAdapter;

    private void showHeadRecyclerView(final List<MineCircleBean> datas) {
        if (datas == null || datas.size() == 0) {
            LogUtil.i("交易tab数据为空");
            return;
        }

        //初始化tab数据
        List<List<MineCircleBean>> list = new ArrayList<>();
        List<View> views = new ArrayList<>();
        List<MineCircleBean> arr = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (i % 8 == 0) {
                arr = new ArrayList<>();
                arr.add(datas.get(i));

            } else {
                arr.add(datas.get(i));
                if (arr.size() == 8 || i == datas.size() - 1) {
                    list.add(arr);
                    views.add(View.inflate(getContext(), R.layout.page_head_tab_circle, null));
                }
            }
        }
        if (list.size() == 0) {
            mNoCircleData .setVisibility(View.VISIBLE);
            mHaveCircleData.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mIndicator.setVisibility(View.GONE);
            return;
        } else if (list.size() == 1) {
            mIndicator.setEnabled(false);
        }


        //recommendAdapter = new CircleItemAdapter(CircleHomeFragment.this.getContext(), sortRecommendData(datas), R.layout.deal_item_layout);
        recommendAdapter = new CircleItemAdapter(CircleHomeFragment.this.getContext(), views, list);
        mIndicator.setCount(recommendAdapter.getCount(), mViewPager);
        mNoCircleData .setVisibility(View.GONE);
        mHaveCircleData.setVisibility(View.VISIBLE);
        mIndicator.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.setAdapter(recommendAdapter);

        recommendAdapter.setOnItemClickListener(new CircleItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MineCircleBean bean, int position) {
                if (UserManager.getInstance().isLogin(getActivity())) {
                    if (bean.getIsJoin() == 0) {
                        Intent intent = new Intent(getActivity(), CircleJoinActivity.class);
                        intent.putExtra("byLiveId", bean.getId());
                        startActivityForResult(intent, REQUEST_JOIN);
                    } else {
                        CircleBean temp = new CircleBean();

                        temp.setGroupName(bean.getGroupName());
                        temp.setCover(bean.getCover());
                        temp.setId(bean.getId());
                        temp.setContent(bean.getContent());
                        temp.setInfoNum(bean.getInfoNum());
                        temp.setAttention(bean.getAttention());
                        temp.setIsMy(bean.getIsMy());
                        Intent intent = new Intent(CircleHomeFragment.this.getContext(), CircleMainActivity.class);
                        intent.putExtra("data", temp);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private AlertDialog mSureDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_cicle:
                gotoMyCircle();
                break;

            //发现圈子
            case R.id.ll_cicle_find:
                GotoUtil.goToActivity(mActivity, CircleListActivity.class);
                break;

            //我的动态
            case R.id.ll_cicle_mine_issue:
                if (UserManager.getInstance().isLogin()) {
                    GotoUtil.goToActivity(getActivity(), MyDynamicActivity.class);
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity.class,
                            Constant.requestCode.NEWS_AUTHOR);
                }
                break;

            //屏蔽此条动态
            case R.id.tv_shield_dynamic:
                if (UserManager.getInstance().isLogin()) shieldDynamic(shieldPosition, mBean, "1");
                else GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);

                break;

            //屏蔽此人动态
            case R.id.tv_shield_user:
                if (UserManager.getInstance().isLogin()) {

                    mSureDialog = DialogUtil.createDialog2(mActivity, "确定屏蔽此人所有动态？", "取消", "确定",
                            new DialogUtil.DialogClickListener() {
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

                } else GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);
                break;

            case R.id.tv_shield_circle_dynamic:
                int type = 1; //1屏蔽圈子的动态 0 解除;
                if (UserManager.getInstance().isLogin()) {
                    ShieldCircleDynamicHandler.getInstant().receiveDynamic(UserManager.getInstance().getToken(),
                            Integer.parseInt(mBean.getGroupId()), type, new ApiCallBack<Object>() {

                                @Override
                                public void onSuccess(Object data) {
                                    for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                                        CircleHomeBean bean = mAdapter.getDatas().get(i);
                                        if (mBean.getGroupId().equals(bean.getGroupId())) {
                                            mAdapter.getDatas().remove(bean);
                                            i--;
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mRecyclerView.notifyChangeData();

                                    if (mAdapter.getDatas().size() < 15) {
                                        /**当页面的数据少于15条时重新从接口拿，，，*/
                                        mPresenter.recommenddynamic(true, "1");
                                    }
                                }

                                @Override
                                public void onError(String errorCode, String message) {
                                    ToastUtil.showShort(MyApplication.getInstance(), message);
                                }
                            });
                    ShieldCircleDynamicHandler.getInstant().addTask(this);
                } else {
                    GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
                }
                break;


            //复制
            case R.id.tv_circle_copy:
                ClipboardManager cmb = (ClipboardManager) CircleHomeFragment.this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(mAdapter.getDatas().get(copyPosition).getContent().trim());
                ToastUtil.showShort(CircleHomeFragment.this.getContext(), "已复制");
                break;

            //收藏
            case R.id.tv_circle_collect:
                if (UserManager.getInstance().isLogin())
                    collect(UserManager.getInstance().getToken(), String.valueOf(collectId));
                else GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);

                break;
            //转发
            case R.id.tv_reply:
                if (UserManager.getInstance().isLogin())
                    ConversationListActivity.startActivity(getActivity(), REQUEST_SHARE_RELAY, Constant.SELECT_TYPE_RELAY);
                else
                    GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);

                break;

            //悬浮按钮
            /*case R.id.fl_circle_btn:
                if (UserManager.getInstance().isLogin()) {
                    showSelectDialog();
                } else {
                    GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);
                }
                break;*/
        }
    }

    private void gotoMyCircle() {
        if (UserManager.getInstance().isLogin(getActivity())) {
            Intent intent = new Intent(this.getActivity(), MineCircleActivity.class);
            intent.putExtra("flag", "1");
            startActivityForResult(intent, REQUEST_REFRESH_RECOME);
        }
    }

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
                        if (getContext() == null) return;
                        ToastUtil.showShort(CircleHomeFragment.this.getContext(), "收藏成功");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getActivity() == null) return;
                        LoginErrorCodeUtil.showHaveTokenError(CircleHomeFragment.this.getActivity(), errorCode, message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    /**
     * 屏蔽此条动态
     */
    private void shieldDynamic(final int position, final CircleHomeBean bean, final String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        if ("1".equals(type)) map.put("targetId", String.valueOf(bean.getId()));
        else if ("0".equals(type)) map.put("targetId", String.valueOf(bean.getUserCode()));
        map.put("type", type);

        Subscription sub = AllApi.getInstance().shield(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (mRecyclerView == null) return;
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
                        if (getActivity() == null) return;
                        LoginErrorCodeUtil.showHaveTokenError(CircleHomeFragment.this.getActivity(), errorCode, message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private ListDialog mListDialog;

    public void showSelectDialog() {
        if (mListDialog == null) {
            mListDialog = new ListDialog(this.getContext(), new String[]{"文字", "图片", "视频"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CircleHomeFragment.this.getContext(),
                            IssueDynamicActivity.class);
                    switch (position) {
                        case 0://文字
                            intent.putExtra("select_type", "3");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                        case 1://拍摄或相册
                            intent.putExtra("select_type", "2");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                        case 2://视频
                            intent.putExtra("select_type", "1");
                            intent.putExtra("type", "1");
                            startActivityForResult(intent, Constant.requestCode.CIRCLE_DT_REFRESH);
                            break;
                    }
                }
            });
        }
        mListDialog.show();
    }


    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void setPresenter(CircleHomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swCircleLayout.setRefreshing(false);
        showContentView();
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
//        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        //有数据加载更多的时候出现错误
        if (mAdapter != null && mAdapter.getDatas().size() > 0) {
            ToastUtil.showShort(this.getContext(), info);
            getBaseLoadingView().hideLoading();

            //没有数据的时候出现错误
        } else {
            Observable.just("recommend_home_bean")
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<String, ListCircleHomeBean>() {
                        @Override
                        public ListCircleHomeBean call(String key) {
                            ListCircleHomeBean bean = (ListCircleHomeBean) ACache.get(
                                    MyApplication.getInstance()).getAsObject(key);
                            return bean;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ListCircleHomeBean>() {
                        @Override
                        public void call(ListCircleHomeBean bean) {
                            if (bean != null && bean.getCircleHomeBeen() != null) {
                                if (bean.getCircleHomeBeen().size() > 0)
                                    showData(bean.getCircleHomeBeen());
                            } else {
                                getBaseEmptyView().showNetWorkViewReload(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                initData();
                                                //记得隐藏
                                                getBaseEmptyView().hideEmptyView();
                                            }
                                        });
                            }
                        }
                    });
        }

        if (mCommentDialog != null) {
            mCommentDialog.reset();
        }
    }

    @Override
    public void showNetError() {

        Observable.just("recommend_home_bean")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ListCircleHomeBean>() {
                    @Override
                    public ListCircleHomeBean call(String key) {
                        ListCircleHomeBean bean = (ListCircleHomeBean) ACache.get(
                                MyApplication.getInstance()).getAsObject(key);
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ListCircleHomeBean>() {
                    @Override
                    public void call(ListCircleHomeBean bean) {
                        if (bean != null && bean.getCircleHomeBeen() != null) {
                            if (bean.getCircleHomeBeen().size() > 0)
                                showData(bean.getCircleHomeBeen());

                        } else {
                            getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    initData();
                                    //记得隐藏
                                    getBaseEmptyView().hideEmptyView();
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void showData(List<CircleHomeBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
        final ListCircleHomeBean bean = new ListCircleHomeBean();
        bean.setCircleHomeBeen(mAdapter.getDatas());
        Observable.just(bean)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<ListCircleHomeBean>() {
                    @Override
                    public void call(ListCircleHomeBean bean) {
                        ACache.get(MyApplication.getInstance()).put("recommend_home_bean", bean);
                    }
                });

        initDynamicUnread();
    }

    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {
        headView();
        mRecyclerView.notifyChangeData(datas, mAdapter);
    }

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CIRCLE_WEB_REQUEST && resultCode == getActivity().RESULT_OK) {
            CircleHomeBean bean = (CircleHomeBean) data.getSerializableExtra("newData");

            if (bean != null) {
                for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                    if (mAdapter.getDatas().get(i).getId() == bean.getId()) {
                        mAdapter.getDatas().remove(i);
                        mAdapter.getDatas().add(i, bean);
                        mRecyclerView.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }

        if (requestCode == Constant.requestCode.CIRCLE_DZ && resultCode == Constant.ResponseCode.CIRCLE_RESULT_DZ) {
            int circleId = data.getIntExtra("circle_id", -1);
            int isDz = data.getIntExtra("is_dz", -1);
            for (int i = 0; i < mAdapter.getDatas().size(); i++) {
                if (mAdapter.getDatas().get(i).getId() == circleId) {
                    mAdapter.getDatas().get(i).setIsDZ(isDz);
                    if (0 == isDz) {
                        mAdapter.getDatas().get(i).setDianZan(mAdapter.getDatas().get(i).getDianZan() - 1);
                    } else if (1 == isDz) {
                        mAdapter.getDatas().get(i).setDianZan(mAdapter.getDatas().get(i).getDianZan() + 1);
                    }
                    mRecyclerView.notifyItemChanged(i);
                }
            }
        }

        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            handler.sendEmptyMessageDelayed(STATUE_REFRESH_DYNIMIC, 1000);
        }

        if (resultCode == Constant.ResponseCode.NORMAL_FLAG) {
            initHeadRecyclerView();
            int id = data.getIntExtra(Constant.INTENT_DATA, 0);
            CircleBean bean = new CircleBean();
            bean.setId(id);
            GotoUtil.goToActivity(this.getActivity(), CircleMainActivity.class, 0, bean);
        }

        //转发消息
        if (requestCode == REQUEST_SHARE_RELAY && resultCode == getActivity().RESULT_OK) {
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            RelayHelper.INSTANCE.relayMessage(mAdapter.getDatas().get(copyPosition).getContent(), (String) bean.mObject, bean.mType, bean.Liuyan);
        }

        //刷新推荐的圈子
        if (requestCode == REQUEST_REFRESH_RECOME && resultCode == RESULT_OK) {
            initHeadRecyclerView();
        }
    }

    @Subscribe
    public void onEvent(EventScorllTopBean bean) {
        if (bean.position == 2 && mAdapter != null && mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    /**
     * 加入圈子，更新头部列表
     *
     * @param refresh
     */
    @Subscribe(sticky = true)
    public void onEvent(Boolean refresh) {
        if (refresh) {
            initHeadRecyclerView();
        }
    }

    //退出圈子消息
    @Subscribe(sticky = true)
    public void onEvent(EventCircleIntro bean) {
        initHeadRecyclerView();
    }


    EventJPushMessgeBean messageMode;

    @Subscribe(sticky = true)
    public void onEvent(EventJPushMessgeBean bean) {
        if (UserManager.getInstance().isLogin()) {
            if (!TextUtils.isEmpty(bean.unReadNum) && !bean.unReadNum.equals("0")) {
                messageNoteLayout.setVisibility(View.GONE);
                messageMode = bean;
                if (messageNoteLayout != null) {
                    if (UserManager.getInstance().isLogin()) {
                        messageNoteLayout.setVisibility(View.VISIBLE);
                        messageUnReadcount.setText(bean.unReadNum + "条新动态");
                        ImageHelper.loadImage(getContext(), messageNotePic, bean.userPic);
                        messageMode = null;
                    } else {
                        messageNoteLayout.setVisibility(View.GONE);
                    }
                }
                String userCode = UserManager.getInstance().getUserCode();
                ACache.get(getContext()).putByAsync(userCode + EventJPushMessgeBean.class.getSimpleName(), bean);

            } else {
                messageNoteLayout.setVisibility(View.GONE);
                String userCode = UserManager.getInstance().getUserCode();
                ACache.get(getContext()).removeByAsync(userCode + EventJPushMessgeBean.class.getSimpleName());
            }
        }
    }

    @Subscribe
    public void onEvent(EventCircleCommentBean bean) {
        for (int i = 0; i < mAdapter.getDatas().size(); i++) {
            if (mAdapter.getDatas().get(i).getId() == bean.id) {
                mAdapter.getDatas().get(i).setLiuYan(bean.count);
                mRecyclerView.notifyItemChanged(i);
            }
        }
    }

    @Subscribe
    public void onEvent(EventLoginBean bean) {
        if (bean.status == EventLoginBean.LOGIN || bean.status == EventLoginBean.REGISTE || bean.status == EventLoginBean.THIRDLOGIN) {
            mRecyclerView.reLoadFinish();
            getBaseEmptyView().hideEmptyView();
        } else if (bean.status == EventLoginBean.EXIT || bean.status == EventLoginBean.TOKEN_OVERDUCE) {      //退出登录
            mRecyclerView.reLoadFinish();
        }
    }


    private void setAdapterListener() {

        mAdapter.setOnReItemOnLongClickListener(
                new BaseMoreTypeRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        copyPosition = position;
                        collectId = mAdapter.getDatas().get(position).getId();
                        boolean isShowReply = false;
                        if (TextUtils.isEmpty(mAdapter.getDatas().get(position).getContent())) {
                            isShowReply = false;
                        } else {
                            isShowReply = true;
                        }
                        KotlinPopCircleAction mPopComment = new KotlinPopCircleAction(MyApplication.getInstance());
                        mPopComment.setisShowReply(isShowReply);
                        mPopComment.setOnClickListener(CircleHomeFragment.this);
                        mPopComment.setAtLocation(v, MyApplication.getInstance().getApplicationContext());
                    }
                });

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
                        Log.d("CircleMainActivity", "mInfo:onCommentOtherClick:");
                        //如果按到的是显示全部  去服务器请求更多

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
                                                if (mPresenter != null)
                                                    mPresenter.removeCommentItem(comConfig);
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
                                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                                View v = manager.findViewByPosition(i + mRecyclerView.getHeadCount());
                                if (v != null) {
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
                            if (!commentConfig.targetUserCode.equals(UserManager.getInstance().getUserCode())) {

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
                            Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDianZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

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
                                    mPresenter.removeComment(commentConfig);
                                }
                            }
                        });
                    }
                });
        mAdapter.setOnShareAndLikeItemListener(
                new CircleDynamicAdapter.OnShareAndLikeItemListener() {

                    @Override
                    public void goToCircleHome(CircleHomeBean bean) {
                        PersonalInfoActivity.startActivity(CircleHomeFragment.this.getContext(), bean.getUserCode());
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
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        int x = location[0] + view.getWidth() / 2 - WindowUtil.dip2px(getContext(), 5);
                        int y = location[1] - StatusBarUtils.getHeight(getContext())
                                + view.getHeight() + WindowUtil.dip2px(getContext(), 15);
                        shieldDialog = new CircleHomeShieldDialogV5(CircleHomeFragment.this.getContext());
                        if (!TextUtils.isEmpty(bean.getGroupName())) {
                            shieldDialog.setShieldCircleDynamic(true);
                        } else {
                            shieldDialog.setShieldCircleDynamic(false);
                        }
                        shieldDialog.anchorView(view)
                                .showAnim(new PopEnterAnim().duration(200))
                                .dismissAnim(new PopExitAnim().duration(200))
                                .cornerRadius(4)
                                .gravity(Gravity.BOTTOM)
                                .bubbleColor(Color.parseColor("#ffffff"))
                                .setBgAlpha(0.1f)
                                .location(x, y)
                                .setOnClickListener(CircleHomeFragment.this);

                        shieldDialog.show();
                    }

                    @Override
                    public void shareItem(int position, CircleHomeBean bean) {
                        shareNews(bean);
                    }
                });
    }

    private void shareNews(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(CircleHomeFragment.this.getActivity());

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
                                    ClipboardManager cmb = (ClipboardManager) CircleHomeFragment.this.getActivity().getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(bean.getUrl().trim());
                                    ToastUtil.showShort(CircleHomeFragment.this.getActivity(),
                                            "复制成功");
                                } else if (flag == 1) {//收藏
                                    if (UserManager.getInstance().isLogin())
                                        collect(UserManager.getInstance().getToken(),
                                                String.valueOf(bean.getId()));
                                    else GotoUtil.goToActivity(mActivity,
                                            LoginAndRegisteActivity.class);
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {
                                        ReportActivity.jumptoReportActivity(getContext(), String.valueOf(bean.getId()), "5");
                                    } else {
                                        gotoLogin();
                                    }
                                }
                            }
                        });
//                new UMShareUtils(NewsWebViewActivity.this).shareSpecific(mBean.getCover(), mBean.getTitle()
//                        , mBean.getDigest(), mBean.getRedirectUrl(), "0");
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

    private void showRemoreCommentDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext(),
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) {
                    mPresenter.removeComment(commentConfig);
                }
                actionSheetDialog.dismiss();
            }
        });
    }


    private void showRemoreCommentItemDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext(),
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


    private ArrayList<String> mMenuRemoveCommentItems = new ArrayList<>();

    {
        mMenuRemoveCommentItems.add("删除");
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
                            Toast.makeText(getContext(), "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        content = content.trim();
                        switch (CircleHomeFragment.this.commentConfig.commentType) {
                            case PUBLIC:
                                mPresenter.addComment(content, CircleHomeFragment.this.commentConfig);
                                break;
                            case REPLY:
                                mPresenter.addCommentReply(content, CircleHomeFragment.this.commentConfig);
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
            gotoLogin();
        }

    }

    private void gotoLogin() {
        Intent intent = new Intent(CircleHomeFragment.this.getContext(),
                LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);

    }

    @Override
    public void update2DeleteCircle(int circleId) {
        ToastUtil.showShort(getContext(), "删除成功");
        List<CircleHomeBean> circleItems = mAdapter.getDatas();
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId == circleItems.get(i).getId()) {
                circleItems.remove(i);
                mRecyclerView.notifyChangeData();
                ToastUtil.showShort(MyApplication.getInstance(),"删除成功");
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
        String userCode = UserManager.getInstance().getUserCode();
        CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<ThumbsupVosBean> items = item.getThumbsupVos();
        for (int i = 0; i < items.size(); i++) {
            if (userCode.equals(items.get(i).getUserCode())) {
                items.remove(i);
                item.setIsDZ(0);
//                mAdapter.notifyDataSetChanged();
                mRecyclerView.notifyItemChanged(circlePosition);
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CircleCommentBean addItem) {
        if (mCommentDialog != null) {
            if (mCommentDialog.isAdded()) {
                mCommentDialog.dismiss();
            }
            if (addItem != null) {
                CircleHomeBean item = mAdapter.getDatas().get(circlePosition);
                item.getCommentVos().add(addItem);
                mRecyclerView.notifyItemChanged(circlePosition);
            }
            //清空评论文本
            mCommentDialog.clearContent();
        }
    }

    @Override
    public void update2AddComment(int circlePosition, List<CircleCommentBean> data) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.dismiss();
        }
        if (data != null) {
            CircleHomeBean item = mAdapter.getDatas().get(circlePosition);
            item.getCommentVos().addAll(data);
            mRecyclerView.notifyItemChanged(circlePosition);
        }
    }

    @Override
    public void update2DeleteCircleItem(int groupInfoId, int commentPosition) {

    }

    @Override
    public void update2DeleteComment(int circlePosition, int commentId) {
        CircleHomeBean item = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<CircleCommentBean> items = item.getCommentVos();
        for (int i = 0; i < items.size(); i++) {
            if (commentId == (items.get(i).getId())) {
                item.setLiuYan(item.getLiuYan() - 1);
                items.remove(i);
//                mAdapter.notifyDataSetChanged();
                mRecyclerView.notifyItemChanged(circlePosition);
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
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    //修改圈子封面图
    @Subscribe(sticky = true)
    public void onEvent(EventImgBean bean) {
        mPresenter.recommenddynamic(false, "1");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mPresenter.destroy();
        handler.removeMessages(STATUE_REFRESH_DYNIMIC);
        ShieldCircleDynamicHandler.getInstant().cancelTask(this);
        handler = null;
        mAlertDialog = null;
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
    }

    @Override
    protected void lazyLoad() {
        new CircleHomePresenter(this);
        mPresenter.recommenddynamic(false, "1");
        readCache();
    }

}



