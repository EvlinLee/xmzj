package com.gxtc.huchuan.ui.circle.homePage;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.event.EventCircleIntro;
import com.gxtc.huchuan.bean.event.EventImgBean;
import com.gxtc.huchuan.bean.event.EventJPushBean;
import com.gxtc.huchuan.bean.event.EventShareMessage;
import com.gxtc.huchuan.bean.event.TransCircleBeanEvent;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.CircleHomeShieldDialogV5;
import com.gxtc.huchuan.dialog.ComplaintDialog;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.dialog.GroupRuleDialog;
import com.gxtc.huchuan.dialog.ListDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.pop.KotlinPopCircleAction;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.circle.article.ArticleActivity;
import com.gxtc.huchuan.ui.circle.circleInfo.CircleInfoActivity;
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity;
import com.gxtc.huchuan.ui.circle.file.folder.FolderListActivity;
import com.gxtc.huchuan.ui.circle.home.EssenceDymicListActivity;
import com.gxtc.huchuan.ui.im.redPacket.IssueRedPacketActivity;
import com.gxtc.huchuan.ui.mall.MallHandleUtil;
import com.gxtc.huchuan.ui.mine.circle.NoticeListActivity;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.DynamicUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.KeyboardUtils;
import com.gxtc.huchuan.utils.ListScrollUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RelayHelper;
import com.gxtc.huchuan.utils.TextLineUtile;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.CircleMainHeadView;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.PraiseTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.Constant.ResponseCode.LOGINRESPONSE_CODE;
import static com.gxtc.huchuan.im.ui.ConversationActivity.REQUEST_SHARE_RELAY;

/**
 * 圈子主页 主页不是tab首页
 */
public class CircleMainActivity extends BaseTitleActivity implements CircleMainContract.View,
        View.OnClickListener {
    private static final String TAG = "CircleMainActivity";
    private static final String NOTICE_BEST = "notice_best";
    private static final String NOTICE_FILE = "notice_file";
    private static final int CIRCLE_WEB_REQUEST = 1 << 3;

    @BindView(R.id.img_header_bg)     ImageView                   imgHeaderBg;
    @BindView(R.id.img_back)          ImageView                   imgBack;
    @BindView(R.id.img_back_white)    ImageView                   imgBackWhite;
    @BindView(R.id.img_setting)       ImageView                   imgSetting;
    @BindView(R.id.img_setting_white) ImageView                   imgSettingWhite;
    @BindView(R.id.img_icon)          ImageView                   imgIcon;
    @BindView(R.id.tv_title)          TextView                    tvTitle;
    @BindView(R.id.tv_name)           TextView                    tvName;
    @BindView(R.id.tv_member)         TextView                    tvMember;
    @BindView(R.id.tv_count)          TextView                    tvCount;
    @BindView(R.id.toolbar)           Toolbar                     toolbar;
    @BindView(R.id.layout_head)       LinearLayout                layoutHead;
    @BindView(R.id.toolbar_layout)    CollapsingToolbarLayout     toolbarLayout;
    @BindView(R.id.fab_issue)         ImageView                   fabIssue;
    @BindView(R.id.app_bar)           AppBarLayout                appBarLayout;
    @BindView(R.id.recyclerView)      RecyclerView                mRecyclerView;
    @BindView(R.id.swipelayout)       SwipeRefreshLayout          swipeLayout;
    @BindView(R.id.tv_classroom)      TextView                    tvClassroom;
    @BindView(R.id.tv_article)        TextView                    tvArticle;
    @BindView(R.id.tv_file)           TextView                    tvFile;
    @BindView(R.id.tv_quanzhu)        TextView                    tvQuanzhu;
    @BindView(R.id.tv_id)             TextView                    tvCircleId;
    @BindView(R.id.btn_chat)          TextView                    btnChat;
    @BindView(R.id.line)              View                        line;
    @BindView(R.id.line_lin3)         View                        line3;
    @BindView(R.id.line_lin4)         View                        line4;
    @BindView(R.id.line_lin5)         View                        line5;

    private float headerBgHeight;


    private CircleBean bean;

    private CircleMainHeadView mHeadView;
    private KotlinPopCircleAction mPopComment;
    //屏蔽
    private int            shieldPosition;
    private CircleHomeBean mBean;

    //复制或收藏
    private int copyPosition;
    private int collectId;

    private int     id;
    private boolean isPreviews;

    private CircleDynamicAdapter         mAdapter;
    private CircleMainContract.Presenter mPresenter;

    private List<CircleHomeBean> mDatas = new ArrayList<>();

    private CommentConfig commentConfig;
    private AlertDialog mAlertDialog;
    private DynamicCommentDialog mCommentDialog;

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_main);
        EventBusUtil.register(this);
    }

    //JZVideoPlayer内部的AudioManager会对Activity持有一个强引用，而AudioManager的生命周期比较长，导致这个Activity始终无法被回收
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name)){
                    return getApplicationContext().getSystemService(name);
                }
                return super.getSystemService(name);
            }
        });
    }


    @Override
    public void initView() {
        setActionBarTopPadding(toolbar, false);
        super.setActionBarTopPadding(layoutHead, false);

        headerBgHeight = getResources().getDimension(R.dimen.px500dp) - getResources().getDimension(R.dimen.head_view_hight);

        swipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (isPreviews) {
                    ToastUtil.showShort(MyApplication.getInstance(), "加入圈子可以查看更多动态哦！");
                } else {
                    mPresenter.loadMrore();
                }
            }
        });
    }


    @Override
    public void initData() {
        if (getIntent().getIntExtra("groupId", 0) != 0) {
            id = getIntent().getIntExtra("groupId", 0);
        } else {
            bean = (CircleBean) getIntent().getSerializableExtra(Constant.INTENT_DATA);
            if(bean == null) return;
            id = bean.getId();


        }
        isPreviews = getIntent().getBooleanExtra("pre", false);
        if (isPreviews) {
            fabIssue.setVisibility(View.GONE);
        }

        new CircleMainPresenter(id, this);
        readCache();
        initAdater();
    }


    //这里是读取
    private void readCache() {
        Observable.just(CircleMainActivity.class.getSimpleName() + id + "head")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, CircleBean>() {
                    @Override
                    public CircleBean call(String key) {
                        return (CircleBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CircleBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove(CircleMainActivity.class.getSimpleName() + id + "head");
                        getCircleInfo();
                    }

                    @Override
                    public void onNext(CircleBean circleBean) {
                        if(circleBean != null){
                            bean = circleBean;
                            showHeadInfo();
                        }
                        readListCache();
                        getCircleInfo();
                    }
                });
    }

    private void readListCache() {
        Observable.just(CircleMainActivity.class.getSimpleName() + id + "list")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<CircleHomeBean>>() {
                    @Override
                    public ArrayList<CircleHomeBean> call(String key) {
                        return (ArrayList<CircleHomeBean>) ACache.get(MyApplication.getInstance()).getAsObject(key);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<CircleHomeBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(CircleMainActivity.this).remove(CircleMainActivity.class.getSimpleName() + id + "list");
                        mPresenter.getData(false);
                        boolean isShowRule = getIntent().getBooleanExtra("rule",false);
                        if(isShowRule){
                            mPresenter.getGroupRule(id);
                        }
                    }

                    @Override
                    public void onNext(ArrayList<CircleHomeBean> circleBeen) {
                        if(circleBeen != null){
                            showData(circleBeen);
                        }

                        //mPresenter.getData(false);
                        boolean isShowRule = getIntent().getBooleanExtra("rule",false);
                        if(isShowRule) mPresenter.getGroupRule(id);
                    }
                });
    }


    @Override
    public void initListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percen = Math.abs(verticalOffset / headerBgHeight);
                tvTitle.setAlpha(percen);
                imgBack.setAlpha(percen);
                imgSetting.setAlpha(percen);
                imgBackWhite.setAlpha(1f - percen);
                imgSettingWhite.setAlpha(1f - percen);
            }
        });
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRecyclerView.reLoadFinish();
            }
        });

        mRecyclerView.addOnScrollListener(new android.support.v7.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                switch (newState){
                    //滑动停止，开始加载
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //用户手指慢慢滑也恢复加载
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if(mAdapter != null && mAdapter.getContext() != null) {
                            Glide.with(mAdapter.getContext()).resumeRequests();
                        }
                        break;

                    //滑动
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        if(mAdapter != null && mAdapter.getContext() != null){
                            Glide.with(mAdapter.getContext()).pauseRequests();
                        }
                        break;
                }
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {}

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


    @OnClick({R.id.tv_quanzhu,
            R.id.tv_classroom,
            R.id.tv_file,
            R.id.tv_article,
            R.id.tv_count,
            R.id.img_back,
            R.id.img_back_white,
            R.id.img_setting,
            R.id.fab_issue,
            R.id.img_setting_white,
            R.id.btn_chat})
    public void onClick(View v) {
        if (v.getId() == R.id.img_back_white || v.getId() == R.id.img_back) {
            finish();
            return;
        }
        if (isPreviews) {
            ToastUtil.showShort(MyApplication.getInstance(), "加入圈子可以查看更多内容哦！");
            return;
        }
        if (ClickUtil.isFastClick()) return;

        switch (v.getId()) {
            case R.id.img_setting_white:
            case R.id.img_setting:
                gotoSetting();
                break;

            case R.id.tv_quanzhu:
                break;

            //发布
            case R.id.fab_issue:
                if(bean != null){
                    if("1".equals(bean.getIsAlsdinfo())){
                        ToastUtil.showShort(this,"您已被圈主禁发动态！");
                    }else {
                        showSelectDialog();
                    }
                }
                break;

            //群聊
            case R.id.btn_chat:
                if(bean != null && bean.getCreateGroupChat() == 0){
                    gotoChat();
                }
                break;

            case R.id.tv_classroom:
                onClickClassRoom();
                break;

            case R.id.tv_file:
                onClickFile();
                break;

            case R.id.tv_article:
                onClickArticle();
                break;

            case R.id.tv_count:
                break;
        }
    }


    private ListDialog mListDialog;

    public void showSelectDialog() {
        if (mListDialog == null && bean != null) {
            mListDialog = new ListDialog(this, new String[]{"文字", "图片", "视频"});
            mListDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CircleMainActivity.this, IssueDynamicActivity.class);
                    switch (position) {
                        //发布文字
                        case 0:
                            intent.putExtra("select_type", "3");
                            intent.putExtra("type", "0");
                            intent.putExtra("data_name", bean.getGroupName());
                            intent.putExtra(Constant.INTENT_DATA, bean.getId());
                            startActivityForResult(intent, 0);
                            break;

                        //拍摄或相册
                        case 1:
                            intent.putExtra("select_type", "2");
                            intent.putExtra("type", "0");
                            intent.putExtra("data_name", bean.getGroupName());
                            intent.putExtra(Constant.INTENT_DATA, bean.getId());
                            startActivityForResult(intent, 0);
                            break;

                        //录像或本地视频
                        case 2:
                            intent.putExtra("select_type", "1");
                            intent.putExtra("type", "0");
                            intent.putExtra("data_name", bean.getGroupName());
                            intent.putExtra(Constant.INTENT_DATA, bean.getId());
                            startActivityForResult(intent, 0);
                            break;
                    }
                }
            });
        }
        mListDialog.show();
    }


    private void gotoChat() {
        if(bean == null)    return;

        if(TextUtils.isEmpty(bean.getChatId())){
            reJoinChat();//群聊id为空则调接口重新拿，若还为空，则提示“正在加入群聊… 请等待10秒！”
            return;
        }

        if (bean.getGroupChatNum() == 1 || bean.getMemberType() == 0) {
            Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon()
                         .appendPath("conversation")
                         .appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                         .appendQueryParameter("targetId", bean.getChatId())
                         .appendQueryParameter("title", bean.getGroupChatName()).build();
            startActivity(new Intent("android.intent.action.VIEW", uri));
        }else{
            ChatGroupActivity.startActivity(this,bean.getId() + "");
        }
    }

    private void reJoinChat() {
        Subscription sub = CircleApi.getInstance()
                         .getCircleMainInfo(UserManager.getInstance().getToken(), id)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                             @Override
                             public void onSuccess(Object data) {
                                 bean = (CircleBean) data;
                                 if(TextUtils.isEmpty(bean.getChatId())){
                                     ToastUtil.showShort(MyApplication.getInstance(),"正在加入群聊… 请等待10秒");
                                 }else {
                                     if (bean.getGroupChatNum() == 1 || bean.getMemberType() == 0) {
                                         Uri uri = Uri.parse("rong://" + CircleMainActivity.this.getApplicationInfo().packageName).buildUpon()
                                                      .appendPath("conversation")
                                                      .appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                                                      .appendQueryParameter("targetId", bean.getChatId())
                                                      .appendQueryParameter("title", bean.getGroupChatName()).build();
                                         startActivity(new Intent("android.intent.action.VIEW", uri));
                                     }else{
                                         ChatGroupActivity.startActivity(CircleMainActivity.this,bean.getId() + "");
                                     }
                                 }
                             }

                             @Override
                             public void onError(String errorCode, String message) {
                               ToastUtil.showShort(MyApplication.getInstance(),message);
                             }
                         }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }


    private void gotoSetting() {
        if(bean != null){
            GotoUtil.goToActivity(this, CircleInfoActivity.class, 0, bean);
        }
    }


    /**
     * 获取圈子信息
     */
    private void getCircleInfo() {
        showLoad();
        String token = UserManager.getInstance().getToken();
        Subscription sub =
                CircleApi.getInstance()
                         .getCircleMainInfo(token, id)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<CircleBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                bean = (CircleBean) data;
                                if(bean == null) return;

                                showLoadFinish();
                                showNoticeStatus();
                                showHeadInfo();
                                mPresenter.getData(false);
                                boolean isShowRule = getIntent().getBooleanExtra("rule",false);
                                if(isShowRule) mPresenter.getGroupRule(bean.getId());

                                mHeadView.ShowCommission(bean.getBrokerage());

                                Observable.just(CircleMainActivity.class.getSimpleName() + id + "head")
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Action1<String>() {
                                            @Override
                                            public void call(String key) {
                                                ACache.get(MyApplication.getInstance()).put(key, bean);


                                            }
                                        });
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                showLoadFinish();
                                LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode, message);
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    //精华 文章，课程，文件提醒
    public void showNoticeStatus() {
        if(bean != null && bean.getUnReadInfoNum() > 0){
            mHeadView.dotBest.setVisibility(View.VISIBLE);
        }else {
            mHeadView.dotBest.setVisibility(View.GONE);
        }

        if(bean != null && bean.getUnReadNewsNum() > 0){
            mHeadView.dotChatInfo.setVisibility(View.VISIBLE);
        }else {
            mHeadView.dotChatInfo.setVisibility(View.GONE);
        }

        if(bean != null && bean.getUnReadChatNum() > 0){
            mHeadView.dotClass.setVisibility(View.VISIBLE);
        }else {
            mHeadView.dotClass.setVisibility(View.GONE);
        }

        if(bean != null && bean.getUnReadFileNum() > 0){
            mHeadView.dotFile.setVisibility(View.VISIBLE);
        }else {
            mHeadView.dotFile.setVisibility(View.GONE);
        }


    }

    /**
     * 列表头部按钮监听
     */
    private void headViewListener() {

        mHeadView.setOnItemCLickLisetner(new CircleMainHeadView.OnItemClickListener() {

            //发表
            @Override
            public void onFaBiao() {
                if(bean != null && bean.getUnReadInfoNum() > 0){
                    mHeadView.dotBest.setVisibility(View.INVISIBLE);
                }
                Intent intent = new Intent(CircleMainActivity.this, EssenceDymicListActivity.class);
                intent.putExtra("groupId", id);
                startActivity(intent);
            }

            @Override
            public void onArticle() {//文章
                if(bean != null && bean.getUnReadNewsNum() > 0){
                    mHeadView.dotChatInfo.setVisibility(View.INVISIBLE);
                }
                onClickArticle();
            }

            @Override
            public void onClassRoom() {//课堂
                if(bean != null && bean.getUnReadChatNum() > 0){
                    mHeadView.dotClass.setVisibility(View.INVISIBLE);
                }
                onClickClassRoom();
            }

            @Override
            public void onFile() {//文件
                if(bean != null && bean.getUnReadFileNum() > 0){
                    mHeadView.dotFile.setVisibility(View.INVISIBLE);
                }
                onClickFile();
            }

            //公告详情
            @Override
            public void onNotice() {
                if(bean != null && (bean.getMemberType() == 1 || bean.getMemberType() == 2)){
                    NoticeListActivity.startActivity(CircleMainActivity.this,id,true);
                }else{
                    NoticeListActivity.startActivity(CircleMainActivity.this,id,false);
                }
            }

            //邀请
            @Override
            public void onYaoQing() {
                if (bean == null) return;
                //管理员或是圈主
                Intent intent = new Intent();
                if(bean.getMemberType() == 2){
                    intent.setClass(CircleMainActivity.this, InviteActivity.class);
                    intent.putExtra("CircleBean",bean);
                }else {
                    String url    = bean.getShareUrl() + "0";
                    intent.setClass(CircleMainActivity.this, CircleInviteActivity.class);
                    intent.putExtra("id", bean.getId() + "");
                    intent.putExtra("share_img_url", bean.getCover());
                    intent.putExtra("share_title", bean.getGroupName());
                    intent.putExtra("share_url", url);
                    intent.putExtra("memberType", 0);
                    intent.putExtra("money", bean.getBrokerage());
                }
                startActivity(intent);
            }

        });
    }

    /**
     *
     * @param isDelete
     * 删除通过后刷新界面
     */
    @Subscribe(sticky = true)
    public void onEvent(Boolean isDelete){
        if(isDelete){
            getCircleInfo();
        }
    }

    private void onClickArticle() {
        if (bean == null) return;
        ArticleActivity.startActivity(CircleMainActivity.this, bean.getUserCode(), bean.getId());
    }

    private void onClickClassRoom() {
        if (bean == null) return;
        NewCalssSynchronousActivity.startActivity(CircleMainActivity.this, bean.getUserCode(), bean.getId());
    }

    private void onClickFile() {
        if (bean == null) return;
        Intent intent = new Intent(CircleMainActivity.this, FolderListActivity.class);
        intent.putExtra("data", bean.getId());
        intent.putExtra("bean", bean);
        intent.putExtra("usercode", bean.getUserCode());
        intent.putExtra("memberType", bean.getMemberType());
        intent.putExtra("type", "0");
        startActivityForResult(intent, 0);
    }


    private void showHeadInfo() {
        if(tvTitle == null) return;
        line.setVisibility(View.VISIBLE);
        line3.setVisibility(View.VISIBLE);
        line4.setVisibility(View.VISIBLE);
        line5.setVisibility(View.VISIBLE);

        tvTitle.setText(this.bean.getGroupName());
        tvName.setText(this.bean.getGroupName());
        String quanzhu = "圈主 " + this.bean.getOwner();
        String member  = "成员 ";

        if (Integer.valueOf(this.bean.getAttention()) > 100000) {
            member = member + "10万+";
        } else {
            member = member + this.bean.getAttention();
        }
        String circleId  = "ID:" + this.bean.getGroupCode();
        String count     = "话题 " + this.bean.getInfoNum();
        String article   = "文章" + this.bean.geteNum();
        String file      = "文件" + this.bean.getfNum();
        String classRoom = "课堂" + this.bean.getcNum();

        if(bean.getCreateGroupChat() == 0){
            Drawable drawable = getResources().getDrawable(R.drawable.person_icon_group_blue);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnChat.setCompoundDrawables(drawable,null,null,null);
            btnChat.setText("群聊");
        }

        if(bean.getCreateGroupChat() == 1){
            btnChat.setCompoundDrawables(null,null,null,null);
            btnChat.setText("未开通群聊");
        }

        tvCircleId.setText(circleId);
        tvQuanzhu.setText(quanzhu);
        tvFile.setText(file);
        tvArticle.setText(article);
        tvMember.setText(member);
        tvCount.setText(count);
        tvClassroom.setText(classRoom);
        ImageHelper.loadImage(this, imgIcon, this.bean.getCover());
        if(TextUtils.isEmpty(this.bean.getBackgCover())){
            ImageHelper.loadImage(this, imgHeaderBg, this.bean.getCover());
        }else{
            ImageHelper.loadImage(this, imgHeaderBg, this.bean.getBackgCover());
        }
        mHeadView.setNotice(this.bean.getNotice());
    }

    @Override
    public void setPresenter(CircleMainContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showData(List<CircleHomeBean> datas) {
        if (datas == null ) return;
        mRecyclerView.notifyChangeData(datas, mAdapter);

        Observable.just(CircleMainActivity.class.getSimpleName() + id + "list").subscribeOn(Schedulers.io()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {

                        ArrayList<CircleHomeBean> temps = new ArrayList<>();
                        temps.addAll(mAdapter.getDatas());
                        ACache.get(MyApplication.getInstance()).put(s, temps);
                    }
                });
    }


   public void initAdater(){
       if (mAdapter == null) {
           int [] res = new int[]{R.layout.item_circle_home_noimgv5, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv5};
           mAdapter = new CircleDynamicAdapter(isPreviews, mRecyclerView, this, this, new ArrayList<CircleHomeBean>(), res);
           mAdapter.setmId(id);
           mHeadView = new CircleMainHeadView(CircleMainActivity.this, mRecyclerView, id, "", isPreviews);
           mRecyclerView.addHeadView(mHeadView);
           mRecyclerView.setAdapter(mAdapter);
           headViewListener();

       }
       setAdapterListener();

   }

    /**
     * 只有第一次进圈子会显示进群发红包弹窗 然后点击确认跳转到群里面 并且发红包
     */
    @Override
    public void showGroupRule(GroupRuleBean data) {
        if(data == null) return;

        if(bean == null || TextUtils.isEmpty( bean.getChatId())) return;
        if(data.getRoletext() != null && TextUtils.isEmpty(data.getRoletext().trim()) && "0".equals(data.getIsRedbag())) return;

        final GroupRuleBean tempData = data;
        GroupRuleDialog mOpenDialog = new GroupRuleDialog(this, data);
        mOpenDialog.show();
        mOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(tempData.getIsRedbag())){
                    Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName)
                                 .buildUpon()
                                 .appendPath("conversation")
                                 .appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                                 .appendQueryParameter("targetId", bean.getChatId())
                                 .appendQueryParameter("title", bean.getGroupChatName())
                                 .appendQueryParameter("isMy", bean.getIsMy() + "")
                                 .build();
                    Intent  intent1 = new Intent("android.intent.action.VIEW", uri);

                    Intent intent2 = new Intent(CircleMainActivity.this, IssueRedPacketActivity.class);
                    intent2.putExtra(Constant.INTENT_DATA, bean.getChatId());
                    intent2.putExtra("type", Conversation.ConversationType.GROUP);
                    Intent intents [] = new Intent[]{intent1,intent2};
                    startActivities(intents);
                }
            }
        });
        if("1".equals(data.getIsRedbag())){
            mOpenDialog.getConfirmBtn().setText("进群发红包");
        }
    }

    private CircleHomeShieldDialogV5  shieldDialog;

    private void setAdapterListener() {
        mAdapter.setOnReItemOnLongClickListener(
                new BaseMoreTypeRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        copyPosition = position;
                        collectId = mAdapter.getDatas().get(position).getId();
                        showEditDialog(v,mAdapter.getDatas().get(position));
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

                    }

                    @Override
                    public void onToNickNameClick(int groupPosition, int position,
                            CommentListTextView.CommentInfo mInfo) {
                    }

                    @Override
                    public void onCommentItemClick(int groupPosition, int position,
                            CircleCommentBean mInfo) {

                    }

                    //如果按到的是显示全部  去服务器请求更多
                    @Override
                    public void onCommentOtherClick() {

                    }

                    //用户名被点击  显示更多。
                    @Override
                    public void onClick(int groupPosition, int position,
                            PraiseTextView.PraiseInfo mPraiseInfo) {}

                    @Override
                    public void onPraiseOtherClick() {}

                    @Override
                    public void onCommentItemLongClick(int groupPosition, int commentPosition,
                                                       final CircleCommentBean commentItem, final CommentConfig config, View v) {
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
                        MallHandleUtil.showPop(CircleMainActivity.this, v, list,mode,
                                new BaseRecyclerAdapter.OnItemClickLisntener() {
                            @Override
                            public void onItemClick(android.support.v7.widget.RecyclerView parentView, View v, int position) {
                                switch (position){
                                    case 0 :
                                        ClipboardManager cmb = (ClipboardManager) CircleMainActivity.
                                                this.getSystemService(Context.CLIPBOARD_SERVICE);
                                        cmb.setText(commentItem.getContent().trim());
                                        ToastUtil.showShort(CircleMainActivity.this, "已复制");
                                        break;
                                    case 1 :
                                        if (mPresenter != null) mPresenter.removeCommentItem(config);

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
                        commentConfig.groupId = bean.getId();
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
                            Toast.makeText(CircleMainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDianZan(CommentConfig commentConfig) {
                        if (UserManager.getInstance().isLogin()) {
                            mPresenter.support(UserManager.getInstance().getToken(), commentConfig);

                        } else {
                            Toast.makeText(CircleMainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onMoreCommentClick(CommentConfig commentConfig) {
                        DynamicDetialActivity.startActivity(CircleMainActivity.this,commentConfig.groupInfoId + "");
                    }

                    @Override
                    public void onRemoveCommentClick(final CommentConfig commentConfig) {
                        commentConfig.groupId = bean.getId();
                        DynamicUtil.showDoalogConfiromRemove(CircleMainActivity.this, new View.OnClickListener() {
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
                        PersonalInfoActivity.startActivity(CircleMainActivity.this, bean.getUserCode());

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

    private void showEditDialog(View v, CircleHomeBean mCircleHomeBean){
        int isMy = bean != null ? bean.getIsMy() : 0;                  //0、不是；1、是
        int typeMember = bean != null ? bean.getMemberType() : 0;      //0:普通用户，1：管理员，2：圈主
        int tempStatus = 0;

        final CircleHomeBean tempBean = mCircleHomeBean;

        if (isMy == 1 || typeMember == 1 || typeMember == 2) {
            tempStatus = 1;//通过tempStatus状态值判断
            mPopComment = new KotlinPopCircleAction(MyApplication.getInstance(), tempStatus,mCircleHomeBean.getIsTop(),mCircleHomeBean.getIsAlsdinfo(),mCircleHomeBean.getIsGood());
        } else {
            mPopComment = new KotlinPopCircleAction(MyApplication.getInstance());
        }

        if(TextUtils.isEmpty(mCircleHomeBean.getContent())){
            mPopComment.setisShowReply(false);
        }else {
            mPopComment.setisShowReply(true);
        }
        mPopComment.setAtLocation(v,MyApplication.getInstance().getApplicationContext());
        mPopComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //复制
                    case R.id.tv_circle_copy:
                        ClipboardManager cmb = (ClipboardManager) CircleMainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(mAdapter.getDatas().get(copyPosition).getContent().trim());
                        ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(), "已复制");
                        break;

                    //收藏
                    case R.id.tv_circle_collect:
                        if (UserManager.getInstance().isLogin())
                            collect(UserManager.getInstance().getToken(), String.valueOf(collectId));
                        else
                            GotoUtil.goToActivity(CircleMainActivity.this, LoginAndRegisteActivity.class);
                        break;

                    //转发
                    case R.id.tv_reply:
                        if (UserManager.getInstance().isLogin())
                            ConversationListActivity.startActivity(CircleMainActivity.this,REQUEST_SHARE_RELAY ,Constant.SELECT_TYPE_RELAY);
                        else
                            GotoUtil.goToActivity(CircleMainActivity.this, LoginAndRegisteActivity.class);
                        break;
                    //精华
                    case R.id.set_best:
                        setGood();
                        break;

                    //删除动态
                    case R.id.delete:
                        ArrayList<Integer> topicIds=new ArrayList<Integer>();
                        topicIds.add(tempBean.getId());
                        deleteCollect(UserManager.getInstance().getToken(),topicIds, bean.getId());
                        break;

                    //动态置顶
                    case R.id.toTop:
                        setToTop(tempBean);
                        break;

                    //禁发动态
                    case R.id.forbident_sent_dynamic:
                        forbidentDynamic(tempBean);
                        break;
                }
            }
        });
    }

    private void forbidentDynamic(final CircleHomeBean mCircleHomeBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", id + "");
        map.put("userCode", mCircleHomeBean .getUserCode());
        map.put("token", UserManager.getInstance().getToken());

        Subscription sub = AllApi.getInstance().setGroupUserDynamicStart(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data == null) return;
                        JSONObject jb = null;
                        try {
                            jb = new JSONObject(GsonUtil.objectToJson(data));
                            String statue = String.valueOf(jb.optInt("dynamicStart"));
                            switch (statue){
                                case "0":
                                    mCircleHomeBean .setIsAlsdinfo(0);
                                    ToastUtil.showShort(CircleMainActivity.this,"解禁成功");
                                    break;
                                case "1":
                                    mCircleHomeBean .setIsAlsdinfo(1);
                                    ToastUtil.showShort(CircleMainActivity.this,"禁发动态成功");
                                    break;
                            }
                            mRecyclerView.notifyItemChanged(copyPosition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void setToTop(CircleHomeBean mCircleHomeBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", id + "");
        map.put("infoId", collectId + "");
        map.put("token", UserManager.getInstance().getToken());

        final CircleHomeBean tempBean = mCircleHomeBean;

        Subscription  subTop = AllApi.getInstance().setTop(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(tempBean.getIsTop() == 0){
                            ToastUtil.showShort(MyApplication.getInstance(), "置顶成功");
                        }else if(tempBean.getIsTop() == 1) {
                            ToastUtil.showShort(MyApplication.getInstance(), "取消成功");
                        }
                        mPresenter.getData(true);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subTop);
    }

    private void deleteCollect(String token, ArrayList<Integer> topicIds, int circleId) {
        if (topicIds.size() > 0){
            Subscription sub = AllApi.getInstance().deletedynimic(token, circleId, topicIds).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if(mRecyclerView == null)   return;
                            mRecyclerView.removeData(mAdapter,copyPosition);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode, message);
                        }
                    }));
            RxTaskHelper.getInstance().addTask(this,sub);
        }
    }

    private void setGood() {
        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", id + "");
        map.put("groupInfoId", collectId + "");
        map.put("token", UserManager.getInstance().getToken());

        Subscription subBest = AllApi.getInstance().setGood(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if(0 ==  mAdapter.getDatas().get(copyPosition).getIsGood()){
                            ToastUtil.showShort(MyApplication.getInstance(), "设置成功");
                            mAdapter.getDatas().get(copyPosition).setIsGood(1);
                        }else if(1 ==  mAdapter.getDatas().get(copyPosition).getIsGood()){
                            ToastUtil.showShort(MyApplication.getInstance(), "取消成功");
                            mAdapter.getDatas().get(copyPosition).setIsGood(0);
                        }
                        mRecyclerView.notifyItemChanged(copyPosition);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subBest);
    }

    private AlertDialog mSureDialog;

    private void showShieldDialog(View view) {
        shieldDialog = new CircleHomeShieldDialogV5(CircleMainActivity.this);
        shieldDialog.showAnim(new PopEnterAnim().duration(200))
                .dismissAnim(new PopExitAnim().duration(200))
                .cornerRadius(4).bubbleColor(Color.parseColor("#ffffff")). setBgAlpha(0.1f).anchorView(view).location(10,
                -50).gravity(Gravity.BOTTOM).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_shield_dynamic://屏蔽此条动态

                                if (UserManager.getInstance().isLogin())
                                    shieldDynamic(shieldPosition, mBean, "1");
                                else GotoUtil.goToActivity(CircleMainActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                            case R.id.tv_shield_user://屏蔽此人动态
                                if (UserManager.getInstance().isLogin()) {

                                    mSureDialog = DialogUtil.createDialog2(CircleMainActivity.this,
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

                                } else GotoUtil.goToActivity(CircleMainActivity.this,
                                        LoginAndRegisteActivity.class);

                                break;
                        }
                    }
                });

        shieldDialog.show();
    }


    Subscription subShield;

    private void shieldDynamic(int position, CircleHomeBean bean, String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("type", type);
        if ("1".equals(type)) {
            map.put("targetId", String.valueOf(bean.getId()));
        } else if ("0".equals(type)){
            map.put("targetId", String.valueOf(bean.getUserCode()));
        }

        final String tempType = type;
        final int tempPosition = position;
        final CircleHomeBean tempBean = bean;

        subShield = AllApi.getInstance().shield(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if ("1".equals(tempType)) {
                            mRecyclerView.removeData(mAdapter, tempPosition);

                        } else if ("0".equals(tempType)) {
                            String userCode = tempBean.getUserCode();

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
                        LoginErrorCodeUtil.showHaveTokenError(CircleMainActivity.this, errorCode,
                                message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,subShield);
    }


    ComplaintDialog complaintDialog;//举报对话框
    private UMShareUtils shareUtils;

    private void shareIssueNamic(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        shareUtils = new UMShareUtils(CircleMainActivity.this);

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
                                    ClipboardManager cmb = (ClipboardManager) CircleMainActivity.this.getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                                    cmb.setText(bean.getUrl().trim());
                                    ToastUtil.showShort(CircleMainActivity.this, "复制成功");
                                } else if (flag == 1) {//收藏
                                    if (UserManager.getInstance().isLogin())
                                        collect(UserManager.getInstance().getToken(),
                                                String.valueOf(bean.getId()));
                                    else GotoUtil.goToActivity(CircleMainActivity.this,
                                            LoginAndRegisteActivity.class);
                                } else if (flag == 2) {//投诉
                                    if (UserManager.getInstance().isLogin()) {
                                        ReportActivity.jumptoReportActivity(CircleMainActivity.this,String.valueOf(bean.getId()),"5");
                                    } else {
                                        gotoLogin();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(CircleMainActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(CircleMainActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });
                    }
                });
    }


    private void collect(String token, String id) {

        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "4");
        map.put("bizId", id);

        Subscription subCollect =
                AllApi.getInstance()
                      .saveCollection(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                ToastUtil.showShort(MyApplication.getInstance(), "收藏成功");
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                ToastUtil.showShort(MyApplication.getInstance(), message);
                            }
                        }));

        RxTaskHelper.getInstance().addTask(this,subCollect);
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
    }

    private void showRemoreCommentDialog(final CommentConfig commentConfig) {
        final String[] contents = new String[mMenuRemoveCommentItems.size()];
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this, mMenuRemoveCommentItems.toArray(contents), null);
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
        final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this,
                mMenuRemoveCommentItems.toArray(contents), null);
        actionSheetDialog.isTitleShow(false).cancelText("取消").show();
        actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPresenter != null) mPresenter.removeCommentItem(commentConfig);
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
//                T.showShort(this, mMenuItems.get(position).mOperName);
                dialog.dismiss();
            }
        });
    }


    @Override
    public void update2DeleteCircle(int circleId) {
        ToastUtil.showShort(this, "删除成功");
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
            } else {

            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CircleCommentBean addItem) {
        if(mCommentDialog != null){
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
        CircleHomeBean          item  = (CircleHomeBean) mAdapter.getDatas().get(circlePosition);
        List<CircleCommentBean> items = item.getCommentVos();
        for (int i = 0; i < items.size(); i++) {
            if (commentId == (items.get(i).getId())) {
                item.setLiuYan(item.getLiuYan() - 1);
                items.remove(i);
                mRecyclerView.notifyItemChanged(circlePosition);
                return;
            }
        }
    }

    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {
        mRecyclerView.notifyChangeData(datas, mAdapter);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {
        if (datas.size() > 0) {
            if (mRecyclerView != null && mAdapter != null) {

                mRecyclerView.changeData(datas, mAdapter);
            }
        } else {
            mRecyclerView.loadFinish();
        }

    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }


    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }


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
                            Toast.makeText(CircleMainActivity.this, "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        content = content.trim();
                        switch (CircleMainActivity.this.commentConfig.commentType) {
                            case PUBLIC:
                                mPresenter.addComment(content, CircleMainActivity.this.commentConfig);
                                break;
                            case REPLY:
                                mPresenter.addCommentReply(content, CircleMainActivity.this.commentConfig);
                                break;
                        }
                    }
                }
            });

            if(!TextUtils.isEmpty(commentConfig.targetUserName)){
                mCommentDialog.setText("回复 " + commentConfig.targetUserName + "：");
            }
            mCommentDialog.show(getSupportFragmentManager(), DynamicCommentDialog.class.getSimpleName());
        } else {
            gotoLogin();
        }
    }


    @Override
    public void showEmpty() {
        mAdapter = new CircleDynamicAdapter(isPreviews, mRecyclerView, this, this,
                new ArrayList<CircleHomeBean>(),
                new int[]{R.layout.item_circle_home_noimgv2, R.layout.item_circle_home_videov5, R.layout.item_circle_home_threeimgv2});

        String notice = bean == null ? "" : bean.getNotice();
        mHeadView = new CircleMainHeadView(CircleMainActivity.this, mRecyclerView, id, notice, isPreviews);
        if(mRecyclerView.getAdapter() == null) mRecyclerView.addHeadView(mHeadView);
        mRecyclerView.setAdapter(mAdapter);
        headViewListener();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
            }
        });
        setAdapterListener();

    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);

        if(mCommentDialog != null){
            mCommentDialog.reset();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    @Override
    protected void setActionBarTopPadding(View v, boolean change) {
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int stataHeight = getStatusBarHeight();
                v.getLayoutParams().height = v.getLayoutParams().height + stataHeight;
                v.setPadding(v.getPaddingLeft(), stataHeight, v.getPaddingRight(),
                        v.getPaddingBottom());

            }
        }
    }

    @Subscribe
    public void onEvent(EventCircleIntro bean) {
        //退出圈子消息 并删除圈子的聊天记录和会话列表里的相应圈子
        if (bean.status == EventCircleIntro.EXIT || bean.status == EventCircleIntro.DELETE) {
            if(this.bean.getChatId() != null){
                removeConversationRecord();
            }else {
                finish();
            }
        }

        //创建圈子群聊的回调
        if(bean.status == EventCircleIntro.CREATE ){
            getCircleInfo();
        }
    }


   public void removeConversationRecord(){
       RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, bean.getChatId(), new RongIMClient.ResultCallback<Boolean>() {
           @Override
           public void onSuccess(Boolean aBoolean) {
               if(aBoolean){
                   //清楚会话列表
                   RongIM.getInstance().getRongIMClient().removeConversation(
                           Conversation.ConversationType.GROUP,
                           bean.getChatId(), new RongIMClient.ResultCallback<Boolean>() {
                               @Override
                               public void onSuccess(Boolean aBoolean) {
                                   if(aBoolean)   CircleMainActivity.this.finish();
                               }

                               @Override
                               public void onError(RongIMClient.ErrorCode errorCode) {
                                   ToastUtil.showShort(CircleMainActivity.this, RIMErrorCodeUtil.handleErrorCode(errorCode));
                               }
                           });
               }
           }

           @Override
           public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(CircleMainActivity.this, RIMErrorCodeUtil.handleErrorCode(errorCode));
           }
       });
   }

    @Subscribe
    public void onEvent(TransCircleBeanEvent eventBean) {
        bean.setIsMy(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CIRCLE_WEB_REQUEST && resultCode == RESULT_OK) {
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
        if (requestCode == 0 && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            mPresenter.getData(true);
            mRecyclerView.reLoadFinish();
            mRecyclerView.scrollToPosition(0);
        }
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
        if (requestCode == Constant.requestCode.CIRCLE_DT_REFRESH && resultCode == Constant.ResponseCode.CIRCLE_ISSUE) {
            mPresenter.getData(true);
            mRecyclerView.reLoadFinish();
            mRecyclerView.scrollToPosition(0);
        }
        //转发消息
        if(requestCode == REQUEST_SHARE_RELAY && resultCode == RESULT_OK){
            EventShareMessage bean = (EventShareMessage) data.getSerializableExtra(Constant.INTENT_DATA);
            RelayHelper.INSTANCE.relayMessage(mAdapter.getDatas().get(copyPosition).getContent(), (String) bean.mObject,bean.mType,bean.Liuyan);
        }
    }

    //修改圈子封面图
    @Subscribe(sticky = true)
    public void onEvent(EventImgBean bean) {
        getCircleInfo();
    }



    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        ImageHelper.onDestroy(MyApplication.getInstance());
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
        EventBusUtil.unregister(this);
        TextLineUtile.clearTextLineCache();
        mAlertDialog = null;
        mAdapter = null;
        if(mPresenter != null)
            mPresenter.destroy();
        if(mHeadView != null)
            mHeadView.removeRunable();     //把MarqueeView里的Runable干掉，不然造成内存泄漏
    }


    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }


    public static void startActivity(Context context, int groupId){
        Intent intent = new Intent(context, CircleMainActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }


    public static void startActivity(Context context, CircleBean bean){
        Intent intent = new Intent(context, CircleMainActivity.class);
        intent.putExtra(Constant.INTENT_DATA, bean);
        context.startActivity(intent);
    }

}
