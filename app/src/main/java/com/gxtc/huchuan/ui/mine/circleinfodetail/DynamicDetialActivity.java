package com.gxtc.huchuan.ui.mine.circleinfodetail;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.popup.BubblePopup;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleDynamicDetialAdapter;
import com.gxtc.huchuan.adapter.CircleHomeImgAdapter;
import com.gxtc.huchuan.adapter.ThumpVosDetailAdapter;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.DynamicCommentDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.provide.SilentMessage;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.ui.circle.home.CircleHomeContract;
import com.gxtc.huchuan.ui.circle.home.CircleHomePresenter;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.ListScrollUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.utils.RIMErrorCodeUtil;
import com.gxtc.huchuan.utils.RongIMTextUtil;
import com.gxtc.huchuan.utils.UMShareUtils;
import com.gxtc.huchuan.widget.AutoLinkTextView;
import com.gxtc.huchuan.widget.CircleRecyclerView;
import com.gxtc.huchuan.widget.MyLayoutManager;
import com.gxtc.huchuan.widget.ScrollGridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import im.collect.CollectMessage;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2018/1/17.改版后的动态详情
 */

public class DynamicDetialActivity extends BaseTitleActivity implements CircleHomeContract.View,View.OnClickListener {

    @BindView(R.id.recyclerView)     RecyclerView       mRecyclerView;
    @BindView(R.id.ll_bottom_layout) View               mBottomLayout;
    @BindView(R.id.iv_like)           ImageView          ivLike;
    @BindView(R.id.iv_collect)       ImageView          ivCollect;

    private CircleDynamicDetialAdapter   mAdapter;
    private CircleHomeContract.Presenter mPresenter;
    private CommentConfig        commentConfig;
    private DynamicCommentDialog mCommentDialog;
    private String                                 id;
    private CircleHomeBean                         mCircleHomeBean;
    private ImageView                              headPic;
    private TextView                               userName;
    private AutoLinkTextView                       mAutoLinkTextView;
    private ImageView                              imgShare;
    private TextView                               tvShare;
    private android.support.v7.widget.RecyclerView mCircleRecyclerView;
    private TextView                               time;
    private View                                   layoutShare;
    private ThumpVosDetailAdapter                  mThumpVosDetailAdapter;
    private View                                   dzLayout;
    private CircleRecyclerView                     imageListView;
    private CircleHomeImgAdapter                   mCircleHomeImgAdapter;
    private View                                   view;
    private TextView tvAttention;
    private AlertDialog mAlertDialog;
    private BubblePopup mBubblePopup;
    private View linearlayoutShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_detail_layout);
    }

    /**   http://blog.csdn.net/acerhphp/article/details/62889468
     *   JZVideoPlayer 内部的AudioManager会对Activity持有一个强引用，而AudioManager的生命周期比较长，导致这个Activity始终无法被回收
     */
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
        getBaseHeadView().showTitle("动态详情");
        hideContentView();
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_share, this);
        mRecyclerView.setFocusableInTouchMode(false);//让RecyclerView失去焦点，否则当添加的头部的高度大于手机屏幕时，刚进入界面，RecyclerView会自动往上移动一段距离的
        mRecyclerView.requestFocus();
        id = getIntent().getStringExtra("id");
        if (TextUtils.isEmpty(id)) return;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
    }

    @Override
    public void initData() {
        super.initData();
        getBaseLoadingView().showLoading();
        new CircleHomePresenter(this);
        view = View.inflate(this,R.layout.my_dynamic_detail_header,null);
        dzLayout = view.findViewById(R.id.recycler_view_container);
        tvAttention = view.findViewById(R.id.tv_attention);
        headPic =(ImageView)  view.findViewById(R.id.iv_circle_home_img);
        userName =(TextView)  view.findViewById(R.id.tv_circle_home_name);
        time =(TextView)  view.findViewById(R.id.tv_time);
        mAutoLinkTextView =(AutoLinkTextView)  view.findViewById(R.id.tv_circle_home_three_content);
        mCircleRecyclerView =(android.support.v7.widget.RecyclerView)  view.findViewById(R.id.rv_circle_home_item);
        mRecyclerView.addHeadView(view);
        getMyDynamicData(id);
        initAdater();
    }

    private void getMyDynamicData(final String id) {
        String token = UserManager.getInstance().getToken();
        Subscription sub = CircleApi.getInstance().getDynamicData(token, id).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<CircleHomeBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        showContentView();
                        getBaseLoadingView().hideLoading();
                        if (data != null) {
                            mCircleHomeBean = (CircleHomeBean) data;
                            setHeaderData();
                            setdata();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(DynamicDetialActivity.this, errorCode, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void setHeaderData() {
        ImageHelper.loadImage(this,headPic,mCircleHomeBean.getUserPic());
        userName.setText(mCircleHomeBean.getUserName());
        setAttention();

        if (!(0 == mCircleHomeBean.getCreateTime()))
            time.setText(DateUtil.showTimeAgo(String.valueOf(mCircleHomeBean.getCreateTime())));

        if(!TextUtils.isEmpty(mCircleHomeBean.getContent())){
            mAutoLinkTextView.setVisibility(View.VISIBLE);
            SpannableString builder = new SpannableString(AndroidEmoji.ensure(mCircleHomeBean.getContent()));
            mAutoLinkTextView.setText(builder);
        }else {
            mAutoLinkTextView.setVisibility(View.GONE);
        }

        switch (mCircleHomeBean.getType()){
            //视频
            case 1:
                ViewStub vediostub =  view.findViewById(R.id.vedio_layout_viewstub);
                vediostub.inflate();
                final JZVideoPlayerStandard player = view.findViewById(R.id.play_circle_video_cover);
                if (player != null) {
                    player.setVisibility(View.INVISIBLE);
                    player.setUp(mCircleHomeBean.getVideoUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "", mCircleHomeBean.getVideoPic());
                    player.widthRatio = 0;
                    player.heightRatio = 0;
                    player.currentTimeTextView.setVisibility(View.INVISIBLE);
                    player.totalTimeTextView.setVisibility(View.INVISIBLE);
                    player.progressBar.setVisibility(View.INVISIBLE);
                    player.thumbImageView.setBackgroundColor(getResources().getColor(R.color.grey_efef));
                }

                if(TextUtils.isEmpty(mCircleHomeBean.getVideoPic())){
                    Observable.just(mCircleHomeBean.getVideoUrl())
                              .subscribeOn(Schedulers.io())
                              .map(new Func1<String, Bitmap>() {
                                  @Override
                                  public Bitmap call(String s) {
                                      return ImageUtils.createVideoThumbnail(s, WindowUtil.getScreenW(getApplicationContext()), 360);

                                  }
                              })
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribe(new Action1<Bitmap>() {
                                  @Override
                                  public void call(Bitmap bitmap) {
                                      if(bitmap == null) return;
                                      setVideoSize(bitmap.getWidth(),bitmap.getHeight(),null,player);
                                      player.thumbImageView.setImageBitmap(bitmap);

                                  }
                              });
                }else{
                    setVideoSize(0,0,mCircleHomeBean.getVideoPic(),player);
                    ImageHelper.loadImage(this,player.thumbImageView,mCircleHomeBean.getVideoPic());
                }
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                ViewStub imagestub =  view.findViewById(R.id.dynamic_image_layout_viewstub);
                imagestub.inflate();
                imageListView = view.findViewById(R.id.rv_image_list);
                imageListView.setNestedScrollingEnabled(false);

                mCircleHomeImgAdapter = new CircleHomeImgAdapter(this, mCircleHomeBean.getPicList(),
                        R.layout.item_circle_home_img2, R.layout.item_circle_home_img);

                ScrollGridLayoutManager manager;
                if (mCircleHomeBean.getPicList().size() == 1) {
                    manager = new ScrollGridLayoutManager(this, 1);

                } else if (mCircleHomeBean.getPicList().size() != 4) {
                    manager = new ScrollGridLayoutManager(this, 3);

                } else {
                    manager = new ScrollGridLayoutManager(this, 2);
                }

                manager.setScrollEnabled(false);
                imageListView.setLayoutManager(manager);
                imageListView.setAdapter(mCircleHomeImgAdapter);
                mCircleHomeImgAdapter.setOnReItemOnClickListener(new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        goImgActivity(mCircleHomeBean, position);
                    }
                });
                break;
            case 6:
                ViewStub relaystub =  view.findViewById(R.id.model_relay_viewstub);
                relaystub.inflate();
                imgShare =(ImageView)  view.findViewById(R.id.img_share);
                tvShare =(TextView)  view.findViewById(R.id.tv_share);
                layoutShare = view.findViewById(R.id.layout_share);
                linearlayoutShare = view.findViewById(R.id.linear_lauout_share);
                FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams) linearlayoutShare.getLayoutParams();
                parms.leftMargin = getResources().getDimensionPixelSize(R.dimen.margin_middle);
                parms.rightMargin = getResources().getDimensionPixelSize(R.dimen.margin_middle);
                layoutShare.setVisibility(View.VISIBLE);
                layoutShare.setOnClickListener(this);
                String title = mCircleHomeBean.getTypeTitle();
                if(mCircleHomeBean.getInfoType() == 5){
                    title = mCircleHomeBean.getUserName() + "邀请你加入" + title + "圈子";
                }
                tvShare.setText(title);
                ImageHelper.loadImage(this,imgShare,mCircleHomeBean.getTypeCover(),R.drawable.live_list_place_holder_120 ,R.drawable.live_list_place_holder_120);
                break;
        }

        if(mCircleHomeBean.getIsCollect() == 0){
            ivCollect.setImageResource(R.drawable.news_icon_collect_normal);
        } else {
            ivCollect.setImageResource(R.drawable.news_icon_collect_selected);
        }

        if(mCircleHomeBean.getIsDZ() == 0){
            ivLike.setImageResource(R.drawable.news_icon_like_normal);
        } else {
            ivLike.setImageResource(R.drawable.news_icon_like_selected);
        }

        if(mCircleHomeBean.getThumbsupVos() != null && mCircleHomeBean.getThumbsupVos().size() > 0){
            dzLayout.setVisibility(View.VISIBLE);
        }else {
            dzLayout.setVisibility(View.GONE);
        }

        MyLayoutManager layout = new MyLayoutManager();
        //必须，防止recyclerview高度为wrap时测量item高度0
        layout.setAutoMeasureEnabled(true);
        mCircleRecyclerView.setLayoutManager(layout);
        mThumpVosDetailAdapter = new ThumpVosDetailAdapter(this,mCircleHomeBean.getThumbsupVos(),R.layout.thump_image_item);
        mCircleRecyclerView.setAdapter(mThumpVosDetailAdapter);
        mAdapter.setOnReItemOnLongClickListener(new BaseRecyclerAdapter.OnReItemOnLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                showManagerPop(v,position);
            }
        });
    }

    private void setdata() {
        mRecyclerView.notifyChangeData(mCircleHomeBean.getCommentVos(), mAdapter);
    }

    private void showManagerPop(View view, int position) {
        if (mBubblePopup != null && mBubblePopup.isShowing()) {
            mBubblePopup.dismiss();
        }
        final CircleCommentBean bean = mAdapter.getList().get(position);
        View inflate = View.inflate(this, R.layout.popup_bubble_text, null);
        TextView tvCopy = inflate.findViewById(R.id.tv_copy);
        inflate.findViewById(R.id.tv_bubble).setVisibility(View.GONE);
        inflate.findViewById(R.id.line).setVisibility(View.GONE);
        tvCopy.setVisibility(TextUtils.isEmpty(bean.getContent()) ? View.GONE : View.VISIBLE);

        tvCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBubblePopup != null && mBubblePopup.isShowing()) {
                        mBubblePopup.dismiss();
                    }
                    ClipboardManager cmb  = (ClipboardManager) getSystemService(
                            Context.CLIPBOARD_SERVICE);
                    cmb.setText(bean.getContent());
                    ToastUtil.showShort(DynamicDetialActivity.this, "已复制");
                }
            });

        int gravity = view.getTop() > 200 ? Gravity.TOP : Gravity.BOTTOM;
        mBubblePopup = new BubblePopup(this, inflate);
        mBubblePopup.anchorView(view).showAnim(null).dismissAnim(null).gravity(gravity).show();
    }


    private void goImgActivity(CircleHomeBean bean, int position) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < bean.getPicList().size(); i++) {
            uris.add(Uri.parse(bean.getPicList().get(i).getPicUrl()));
        }
        CommonPhotoViewActivity.startActivity(this,uris,position);
    }


    //设置视频的尺寸
    private void setVideoSize(int width, int height, String videoPic, JZVideoPlayerStandard videoView){
        float [] size = null;
        if(TextUtils.isEmpty(videoPic)){
            size = getVideoSize(width,height);
        }else{
            try {
                String sub = videoPic.split("\\?")[1];
                String [] var = sub.split("\\*");
                float tempWidth = Float.valueOf(var[0]);
                float tempheight = Float.valueOf(var[1]);
                size = getVideoSize(tempWidth,tempheight);

            }catch (Exception e){
                e.printStackTrace();
                LogUtil.i(e.getMessage());
            }
        }

        if(size != null && size.length >= 2 && videoView != null){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoView.getLayoutParams();
            params.width = (int) size[0];
            params.height = (int) size[1];
            videoView.setVisibility(View.VISIBLE);
        }
    }

    private float[] getVideoSize(float width, float height){
        float marginLeft = getResources().getDimension(R.dimen.px130dp);
        float marginRight = getResources().getDimension(R.dimen.margin_larger);
        float [] size = new float[2];
        if(width < height){
            float videoWidth = getResources().getDimension(R.dimen.px240dp);
            //如果图片比设定最小宽度还小 就给最小宽度的值
            if(width < videoWidth){
                width = videoWidth;
            }

            float videoHeight = videoWidth * height / width;
            size[0] = videoWidth;
            size[1] = videoHeight;

        } else if(width == height) {
            float videoWidth = getResources().getDimension(R.dimen.px240dp);
            size[0] = videoWidth;
            size[1] = videoWidth;

        } else {
            float videoWidth = (float) ((WindowUtil.getScreenW(this) - marginLeft - marginRight) * 0.7);
            float videoHeight = videoWidth * height / width;
            if(height < videoHeight){
                height = videoHeight;
            }
            size[0] = videoWidth;
            size[1] = videoHeight;
        }

        return size;
    }

    @OnClick({R.id.tv_write_comment,R.id.iv_like, R.id.iv_collect})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_write_comment:
                if (UserManager.getInstance().isLogin()) {
                    //评论详情
                    CommentConfig commentConfig = new CommentConfig();
                    commentConfig.groupInfoId = mCircleHomeBean.getId();
                    commentConfig.commentPosition = -1;
                    commentConfig.circlePosition = mCircleHomeBean.getCommentVos().size() - 1;
                    commentConfig.commentType = CommentConfig.Type.PUBLIC;
                    mPresenter.showEditTextBody(commentConfig);
                } else {
                    gotoLogin();
                }

                break;
            case R.id.iv_like:
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.groupInfoId = mCircleHomeBean.getId();
                commentConfig.commentPosition = -1;
                commentConfig.circlePosition = -1;
                commentConfig.commentType = CommentConfig.Type.PUBLIC;
                if (1 == mCircleHomeBean.getIsDZ()) {
                    mPresenter.support(UserManager.getInstance().getToken(), commentConfig);
                } else {
                    mPresenter.support(UserManager.getInstance().getToken(), commentConfig);
                }
                break;
            case R.id.iv_collect:
                collect(UserManager.getInstance().getToken(),mCircleHomeBean.getId()+"");
                break;
            case R.id.layout_share:
                CircleHomeBean homeBean = mCircleHomeBean;
                UIMessage uiMessage = new UIMessage();
                Message msg = new Message();
                uiMessage.setMessage(msg);
                uiMessage.setSenderUserId(homeBean.getUserCode());

                //系列课主页
                if(homeBean.getInfoType() == 6){
                    SeriesActivity.startActivity(this,homeBean.getTypeId());

                    //商品点击
                }else if(homeBean.getInfoType() == 7){
                    MallDetailedActivity.startActivity(this, homeBean.getTypeId());

                }else {
                    CircleShareHandler mShareUtil = new CircleShareHandler(this);
                    mShareUtil.shareHandle(this,homeBean.getTypeId(),homeBean.getInfoType(),uiMessage);
                }
                break;
            case R.id.HeadRightImageButton:
                shareNews(mCircleHomeBean);
                break;

        }

    }

    private void setAttention() {
        if (0 == mCircleHomeBean.getIsMy() && 0 == mCircleHomeBean.getIsAttention()) {
            tvAttention.setVisibility(View.VISIBLE);
            tvAttention.setText("+ 关注");
            tvAttention.setFocusable(true);
            tvAttention.setEnabled(true);
            tvAttention.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            tvAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttentionUserDynamic(mCircleHomeBean.getUserCode(), tvAttention);
                }
            });
        } else if (0 == mCircleHomeBean.getIsMy() && 1 == mCircleHomeBean.getIsAttention()) {
            tvAttention.setVisibility(View.INVISIBLE);
        } else {
            tvAttention.setVisibility(View.GONE);
        }
    }

    /**
     * 关注用户动态
     */

    private void AttentionUserDynamic(final String userCode, final TextView textView) {
        if (UserManager.getInstance().isLogin()) {
           Subscription subAttention = AllApi.getInstance().setUserFollow(UserManager.getInstance().getToken(), "3", userCode)
                                 .subscribeOn(Schedulers.io())
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                     @Override
                                     public void onSuccess(Object data) {
                                         textView.setVisibility(View.INVISIBLE);
                                         mCircleHomeBean.setIsAttention(1);
                                     }


                                     @Override
                                     public void onError(String errorCode, String message) {
                                         ToastUtil.showShort(DynamicDetialActivity.this, message);
                                     }
                                 }));
           RxTaskHelper.getInstance().addTask(this,subAttention);
        } else {
            Intent intent = new Intent(DynamicDetialActivity.this, LoginAndRegisteActivity.class);
            startActivityForResult(intent, Constant.requestCode.REFRESH_CIRCLE_HOME);
        }
    }

    private void shareNews(final CircleHomeBean bean) {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200,
                new BaseTitleActivity.PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        UMShareUtils shareUtils = new UMShareUtils(DynamicDetialActivity.this);

                        if (bean.getPicList().size() > 0) {
                            shareUtils.shareCollect(bean.getPicList().get(0).getPicUrl(),
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        } else {
                            shareUtils.shareCollect(R.mipmap.person_icon_head_share,
                                    "这条动态很有意思，快来围观吧", bean.getContent(), bean.getUrl());
                        }

                        shareUtils.setOnItemClickListener(new UMShareUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(int flag) {
                                if (flag == 0) {
                                    ConversationListActivity.startActivity(DynamicDetialActivity.this, ConversationActivity.REQUEST_SHARE_CONTENT,Constant.SELECT_TYPE_SHARE);
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(DynamicDetialActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(DynamicDetialActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });

    }

    public void initAdater() {
        mAdapter = new CircleDynamicDetialAdapter(this, new ArrayList<CircleCommentBean>(), R.layout.activity_dynamic_layout_comment_item);
        mRecyclerView.setAdapter(mAdapter);
        setAdapterListener();
    }

    private void setAdapterListener() {
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CircleCommentBean commentItem = mCircleHomeBean.getCommentVos().get(position);
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.item = v;
                commentConfig.groupInfoId = mCircleHomeBean.getId();
                commentConfig.commentPosition = position;
                commentConfig.circlePosition = position;
                commentConfig.commentId = commentItem.getId();
                commentConfig.commentType = CommentConfig.Type.REPLY;
                commentConfig.targetUserCode = commentItem.getUserCode();
                commentConfig.targetUserName = commentItem.getUserName();
                if (UserManager.getInstance().isLogin()) {
                    if (!TextUtils.isEmpty(mCircleHomeBean.getCommentVos().get(position).getUserCode()) &&
                            mCircleHomeBean.getCommentVos().get(position).getUserCode().equals(UserManager.getInstance().getUserCode())) {
                        showRemoreCommentItemDialog(commentConfig);
                    }else {
                        mPresenter.showEditTextBody(commentConfig);
                    }
                }else {
                    gotoLogin();
                }
            }
        });
    }


    private void gotoLogin() {
        Intent intent = new Intent(this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
    }

    private ArrayList<String> mMenuRemoveCommentItems = new ArrayList<>();{
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

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();{
        mMenuItems.add(new DialogMenuItem("收藏", 0));
        mMenuItems.add(new DialogMenuItem("下载", 0));
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
                        if (1 == mCircleHomeBean.getIsCollect()) {
                            mCircleHomeBean.setIsCollect(0);
                            ivCollect.setImageResource(R.drawable.news_icon_collect_normal);
                        } else {
                            mCircleHomeBean.setIsCollect(1);
                            ivCollect.setImageResource(R.drawable.news_icon_collect_selected);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(MyApplication.getInstance(),message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void showData(List<CircleHomeBean> datas) {}


    @Override
    public void update2DeleteCircle(int circleId) {ToastUtil.showShort(this, "删除成功");}

    @Override
    public void update2AddFavorite(int circlePosition, ThumbsupVosBean data) {
        if(TextUtils.isEmpty(data.getHeadPic())){
            data.setHeadPic(UserManager.getInstance().getHeadPic());
        }
        if(mThumpVosDetailAdapter.getList().size() == 0){
            dzLayout.setVisibility(View.VISIBLE);
        }
        mCircleHomeBean.setIsDZ(1);
        ivLike.setImageResource(R.drawable.news_icon_like_selected);
        mCircleHomeBean.getThumbsupVos().add(data);
        mThumpVosDetailAdapter.notifyItemInserted(mCircleHomeBean.getThumbsupVos().size() - 1);
    }

    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {
        ivLike.setImageResource(R.drawable.news_icon_like_normal);
        if (!UserManager.getInstance().isLogin()) {
            return;
        }
        String                userCode = UserManager.getInstance().getUserCode();
        List<ThumbsupVosBean> items    = mCircleHomeBean.getThumbsupVos();
        for (int i = 0; i < items.size(); i++) {
            if (userCode.equals(items.get(i).getUserCode())) {
                items.remove(i);
                mCircleHomeBean.setIsDZ(0);
                mThumpVosDetailAdapter.notifyItemRemoved(i);
                if(mThumpVosDetailAdapter.getList().size() == 0){
                    dzLayout.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public void update2AddComment(int circlePosition, CircleCommentBean addItem) {
        if (mCommentDialog != null && mCommentDialog.isAdded()) {
            mCommentDialog.dismiss();
        }
        //清空评论文本
        mCommentDialog.clearContent();
        if (addItem != null) {
            mCircleHomeBean.getCommentVos().add(addItem);
            mRecyclerView.notifyChangeData(mCircleHomeBean.getCommentVos(),mAdapter);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void update2DeleteComment(int circlePosition, int commentId) {
        mRecyclerView.removeData(mAdapter,circlePosition);
    }

    @Override
    public void showHeadData(List<MineCircleBean> data) {

    }

    @Override
    public void headEmpty() {

    }

    @Override
    public void showRefreshFinish(List<CircleHomeBean> datas) {}

    @Override
    public void showLoadMore(List<CircleHomeBean> datas) {}

    @Override
    public void showNoMore() {}

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
                            Toast.makeText(DynamicDetialActivity.this, "评论内容不能为空...",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        switch (DynamicDetialActivity.this.commentConfig.commentType) {
                            case PUBLIC:
                                mPresenter.addComment(content,
                                        DynamicDetialActivity.this.commentConfig);
                                break;
                            case REPLY:
                                mPresenter.addCommentReply(content,
                                        DynamicDetialActivity.this.commentConfig);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == RESULT_OK){
            EventSelectFriendBean         bean     = data.getParcelableExtra(Constant.INTENT_DATA);
            String                        targetId = bean.targetId;
            Conversation.ConversationType type     = bean.mType;
            shareCollect(targetId,type,bean.liuyan);
        }

        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }


    //分享收藏
    public  void shareCollect(final String targetId, final Conversation.ConversationType type, final String liuyan){
        String id = "";
        String t = "4";
        String cover = "";
        String content = "";
        String url = "";
        cover = mCircleHomeBean.getUserPic();
        content = mCircleHomeBean.getContent();
        id = mCircleHomeBean.getId() + "";

        if(TextUtils.isEmpty(content)){
            content = "这是一条动态";
        }
        CollectMessage msg     = CollectMessage.obtain(id, t, cover, content, url);
        Message        message = ImMessageUtils.obtain(targetId,type,msg);
        RongIM.getInstance().sendMessage(message, "动态", "动态", new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {}

            @Override
            public void onSuccess(Message message) {
                ToastUtil.showShort(MyApplication.getInstance(),"分享成功");
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.INSTANCE.relayMessage(liuyan,targetId,type);
                }
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                ToastUtil.showShort(MyApplication.getInstance(),RIMErrorCodeUtil.handleErrorCode(errorCode));
            }
        });
    }

    /**
     * 课堂相关
     */

    public static void silentMessage(String isSilent,String targetUserCode, String targetId, IRongCallback.ISendMessageCallback callback){
        String userCode = UserManager.getInstance().getUserCode();
        String name = UserManager.getInstance().getUserName();
        String head = UserManager.getInstance().getHeadPic();

        SilentMessage silentMessage = SilentMessage.obtain(isSilent,targetUserCode);
        UserInfo      userInfo      = RongImHelper.createUserInfo(userCode,name,head);
        Extra         extra         = Extra.obtan("4", false, targetId + System.currentTimeMillis(), "0");
        extra.setSentTime(System.currentTimeMillis());
        silentMessage.setUserInfo(userInfo);
        silentMessage.setExtra(extra.encode());

        Message message = Message.obtain(targetId, Conversation.ConversationType.CHATROOM, silentMessage);
        message.setExtra(extra.encode());

        RongIM.getInstance().sendMessage(message,null,null,callback);
    }

    @Override
    public void update2AddComment(int circlePosition, List<CircleCommentBean> data) {}

    @Override
    public void update2DeleteCircleItem(int groupInfoId, int commentPosition) {}

    @Override
    public void tokenOverdue() {}

    @Override
    public void showLoad() {}

    @Override
    public void showLoadFinish() {}

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {}

    @Override
    public void showNetError() {}

    @Override
    public void setPresenter(CircleHomeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onBackPressed() {
        if (!JZVideoPlayer.backPress()) {
            super.onBackPressed();
        }
        mBottomLayout.setVisibility(View.GONE);
    }

    public static void startActivity(Context context, String id){
        Intent intent = new Intent(context,DynamicDetialActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }


}
