package com.gxtc.huchuan.ui.mine.circleinfodetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleHomeImgAdapter;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.helper.ShareHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.pop.KotlinPopCircleAction;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsCommentDialog;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.CircleRecyclerView;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.gxtc.huchuan.widget.ScrollGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2017/8/30.
 */

public class CircleDetailActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.recyclerView)      RecyclerView       listView;
    @BindView(R.id.swipelayout)       SwipeRefreshLayout swipeLayout;
    @BindView(R.id.ll_webview_bottom) LinearLayout       llWebviewBottom;
    @BindView(R.id.iv_like)           ImageView          ivLike;
    @BindView(R.id.tv_write_comment)  TextView           tvWriteComment;

    ImageView             headPic;
    TextView              userName;
    TextView              tvTime;
    TextView              tvContent;
    CircleDetailAdater    mAdater;
    PersonalDymicBean     mbean;
    CircleHomeBean        mCircleHomeBean;
    TextView              commentCount;
    TextView              zanCount;
    CircleRecyclerView    nRecylce;
    JZVideoPlayerStandard player;
    Context               mContext;
    NewsCommentDialog     mCOmmentDialog;

    private boolean isLike;//是否点赞
    int                     location;
    List<CircleCommentBean> Alldata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_detail_layout);
    }

    @Override
    public void initView() {
        super.initView();
        mContext = this;
        getBaseHeadView().showTitle("圈子详情").showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        tvWriteComment.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                getCirCleDynamicData(mbean.getId());
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        Alldata = new ArrayList<>();
        mbean = (PersonalDymicBean) getIntent().getSerializableExtra("data");
        if (mbean == null) return;
        getCirCleDynamicData(mbean.getId());
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        int[] res = new int[]{R.layout.text_item_layout};
        mAdater = new CircleDetailAdater(this, new ArrayList<CircleCommentBean>(), res);
        View headView = getLayoutInflater().inflate(R.layout.activity_circle_detail_header, null);
        initHeaderView(headView);
        listView.addHeadView(headView);
        listView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, 1,
                        Color.parseColor("#ECEDEE")));
        listView.setAdapter(mAdater);
        mAdater.setOnReItemOnLongClickListener(
                new BaseMoreTypeRecyclerAdapter.OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        location = position;
                        showEditDialog(v, mAdater.getDatas().get(position));
                    }
                });
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);
    }

    private void initHeaderView(View headView) {
        headPic = (ImageView) headView.findViewById(R.id.iv_circle_home_img);
        userName = (TextView) headView.findViewById(R.id.tv_circle_home_name);
        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvContent = (TextView) headView.findViewById(R.id.tv_content);
        commentCount = (TextView) headView.findViewById(R.id.comment_conter);
        zanCount = (TextView) headView.findViewById(R.id.zan_counter);
        nRecylce = (CircleRecyclerView) headView.findViewById(R.id.iv_mutil_content);
        player = (ExpandVideoPlayer) headView.findViewById(R.id.play_circle_video_cover);
    }

    KotlinPopCircleAction mPopComment;

    private void showEditDialog(View v, final CircleCommentBean mCircleHomeBean) {
        if (mPopComment == null)
            mPopComment = new KotlinPopCircleAction(MyApplication.getInstance());
        mPopComment.setTextLeft("回复评论");
        mPopComment.setTextRight("删除评论");
        mPopComment.setAtLocation(v, MyApplication.getInstance().getApplicationContext());
        mPopComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_circle_copy://回复他人
                        if (UserManager.getInstance().isLogin()) {//用户已经登录
                            mCOmmentDialog = new NewsCommentDialog(CircleDetailActivity.this);
                            mCOmmentDialog.setOnSendListener(
                                    new NewsCommentDialog.OnSendListener() {
                                        @Override
                                        public void sendComment(String content) {
                                            commentConnent(mCircleHomeBean.getUserCode(), content);
                                        }
                                    });
                            mCOmmentDialog.show();
                        } else {
                            gotoLogin();
                        }
                        break;
                    case R.id.tv_circle_collect://删除评论
                        deleteComment(location, mCircleHomeBean.getId());
                        break;
                }
            }
        });
    }

    //回复他人
    private void commentConnent(String userCode, final String content) {
        if (UserManager.getInstance().isLogin()) {
            Subscription sub = AllApi.getInstance().comment(UserManager.getInstance().getToken(),
                    mCircleHomeBean.getId(), content, userCode).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<CircleCommentBean>>(new ApiCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            CircleCommentBean bean = (CircleCommentBean) data;
                            Alldata = mAdater.getDatas();
                            Alldata.add(bean);
                            mAdater.notifyItemRangeChanged(mAdater.getDatas().size(),
                                    Alldata.size() - mAdater.getDatas().size() + 1);
                            listView.smoothScrollToPosition(mAdater.getDatas().size() - 1);
                            commentCount.setText("评论 (" + Alldata.size() + ")");
                            mCOmmentDialog.dismiss();
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(CircleDetailActivity.this,
                                    errorCode, message);
                        }
                    }));

            RxTaskHelper.getInstance().addTask(this, sub);
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    //删除评论
    private void deleteComment(final int position, final int id) {
        Subscription sub = CircleApi.getInstance().deleteComment(id,
                UserManager.getInstance().getToken()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        listView.removeData(mAdater, position);
                        commentCount.setText("评论 (" + mAdater.getDatas().size() + ")");
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(CircleDetailActivity.this, errorCode,
                                message);
                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    //获取圈子动态信息
    public void getCirCleDynamicData(String id) {
        getBaseLoadingView().showLoading();
        String token = UserManager.getInstance().getToken();
        Subscription sub = CircleApi.getInstance().getDynamicData(token, id).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<CircleHomeBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        getBaseLoadingView().hideLoading();
                        if (data == null) return;
                        mCircleHomeBean = (CircleHomeBean) data;
                        isShowBottomCount(String.valueOf(mCircleHomeBean.getIsDZ()));
                        setData();
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        getBaseLoadingView().hideLoading();
                        ToastUtil.showShort(mContext, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void setData() {
        setPersonInfo();//头部个人信息和底部评论
        setImage();//图片信息
        setVideo();//视屏信息
    }

    private void setPersonInfo() {
        if (TextUtils.isEmpty(mCircleHomeBean.getContent())) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(mCircleHomeBean.getContent());
        }
        ImageHelper.loadImage(mContext, headPic, mCircleHomeBean.getUserPic());
        userName.setText(mCircleHomeBean.getUserName());
        tvTime.setText(DateUtil.showTimeAgo(String.valueOf(mCircleHomeBean.getCreateTime())));
        if (mCircleHomeBean.getCommentVos() != null) {
            commentCount.setText("评论 (" + mCircleHomeBean.getCommentVos().size() + ")");
        } else {
            commentCount.setText("评论 (" + 0 + ")");
        }
        zanCount.setText(mCircleHomeBean.getDianZan() + "赞");
        listView.notifyChangeData(mCircleHomeBean.getCommentVos(), mAdater);
    }

    private void setVideo() {
        if (TextUtils.isEmpty(mCircleHomeBean.getVideoUrl())) {
            player.setVisibility(View.GONE);
        } else {
            player.setVisibility(View.VISIBLE);
            if (player != null) {
                player.setUp(mCircleHomeBean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_LIST, "", "");
            }
            Observable.just(mCircleHomeBean.getVideoUrl()).map(new Func1<String, Bitmap>() {
                @Override
                public Bitmap call(String s) {
                    return ImageUtils.createVideoThumbnail(s, WindowUtil.getScreenW(mContext), 360);

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            player.thumbImageView.setImageBitmap(bitmap);
                        }
                    });
        }

    }

    private void setImage() {
        if (mCircleHomeBean.getGroupPicVos() == null || mCircleHomeBean.getGroupPicVos().size() == 0) {
            nRecylce.setVisibility(View.GONE);
        } else {
            nRecylce.setVisibility(View.VISIBLE);
            CircleHomeImgAdapter mAdapter = new CircleHomeImgAdapter(mContext,
                    mCircleHomeBean.getGroupPicVos(), R.layout.item_circle_home_img2,
                    R.layout.item_circle_home_img);

            ScrollGridLayoutManager manager;
            if (mCircleHomeBean.getGroupPicVos().size() == 1) {
                manager = new ScrollGridLayoutManager(mContext, 1);

            } else if (mCircleHomeBean.getGroupPicVos().size() != 4) {
                manager = new ScrollGridLayoutManager(mContext, 3);

            } else {
                manager = new ScrollGridLayoutManager(mContext, 2);
            }

            manager.setScrollEnabled(false);
            nRecylce.setLayoutManager(manager);
            nRecylce.setAdapter(mAdapter);
            mAdapter.setOnReItemOnClickListener(
                    new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            goImgActivity(mCircleHomeBean, position);
                        }
                    });
        }
    }

    private void goImgActivity(CircleHomeBean bean, int position) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < bean.getGroupPicVos().size(); i++) {
            uris.add(Uri.parse(bean.getGroupPicVos().get(i).getPicUrl()));
        }
        CommonPhotoViewActivity.startActivity(this, uris, position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_write_comment://填写评论
                if (UserManager.getInstance().isLogin()) {//用户已经登录
                    mCOmmentDialog = new NewsCommentDialog(this);
                    mCOmmentDialog.setOnSendListener(new NewsCommentDialog.OnSendListener() {
                        @Override
                        public void sendComment(String content) {
                            comment(content);
                        }
                    });
                    mCOmmentDialog.show();
                } else {
                    gotoLogin();
                }

                break;
            case R.id.iv_like://是否点赞
                if (isLike) {//已经点赞用户肯定已经在登录状态了
                    thumbsup(UserManager.getInstance().getToken(), mCircleHomeBean.getId());
                } else {//没有点赞
                    if (UserManager.getInstance().isLogin()) {
                        thumbsup(UserManager.getInstance().getToken(), mCircleHomeBean.getId());
                    } else {
                        gotoLogin();
                    }
                }
                break;
        }
    }

    //评论动态
    private void comment(final String content) {
        if (UserManager.getInstance().isLogin()) {
            if (UserManager.getInstance().isLogin()) {
                Subscription sub = AllApi.getInstance().comment(
                        UserManager.getInstance().getToken(), mCircleHomeBean.getId(), content,
                        "").subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()).subscribe(
                        new ApiObserver<ApiResponseBean<CircleCommentBean>>(new ApiCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                CircleCommentBean bean = (CircleCommentBean) data;
                                Alldata = mAdater.getDatas();
                                Alldata.add(bean);
                                mAdater.notifyItemRangeChanged(mAdater.getDatas().size(),
                                        Alldata.size() - mAdater.getDatas().size() + 1);
                                listView.smoothScrollToPosition(mAdater.getDatas().size() - 1);
                                commentCount.setText("评论 (" + Alldata.size() + ")");
                                mCOmmentDialog.dismiss();

                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                if (mCOmmentDialog == null) return;
                                LoginErrorCodeUtil.showHaveTokenError(CircleDetailActivity.this,
                                        errorCode, message);
                                mCOmmentDialog.dismiss();
                            }
                        }));

                RxTaskHelper.getInstance().addTask(this, sub);
            }
        } else GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    /**
     * 点赞或取消点赞
     */

    private void thumbsup(String token, final int id) {
        Subscription sub = AllApi.getInstance().support(token, id).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<ThumbsupVosBean>>(
                        new ApiCallBack<ThumbsupVosBean>() {
                            @Override
                            public void onSuccess(ThumbsupVosBean data) {

                                if (isLike) {
                                    isLike = false;
                                    ivLike.setImageResource(R.drawable.news_icon_like_normal);
                                    mCircleHomeBean.setIsDZ(0);
                                    mCircleHomeBean.setDianZan(mCircleHomeBean.getDianZan() - 1);
                                    zanCount.setText(mCircleHomeBean.getDianZan() + "赞");

                                } else if (isLike == false) {
                                    isLike = true;
                                    ivLike.setImageResource(R.drawable.news_icon_like_selected);
                                    mCircleHomeBean.setIsDZ(1);
                                    mCircleHomeBean.setDianZan(mCircleHomeBean.getDianZan() + 1);
                                    zanCount.setText(mCircleHomeBean.getDianZan() + "赞");
                                }
                            }

                            @Override
                            public void onError(String errorCode, String message) {
                                LoginErrorCodeUtil.showHaveTokenError(CircleDetailActivity.this,
                                        errorCode, message);
                                getBaseLoadingView().hideLoading();
                            }
                        }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    /**
     * @param isThum
     */
    private void isShowBottomCount(String isThum) {

        //是否已经点赞,0未点1已点
        if ("0".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_normal);
            isLike = false;
        } else if ("1".equals(isThum)) {
            ivLike.setImageResource(R.drawable.news_icon_like_selected);
            isLike = true;
        }
    }

    private void gotoLogin() {

        Intent intent = new Intent(this, LoginAndRegisteActivity.class);
        startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //分享视频
        if(requestCode == ConversationActivity.REQUEST_SHARE_VIDEO && resultCode == RESULT_OK){
            EventSelectFriendBean bean = data.getParcelableExtra(Constant.INTENT_DATA);
            ShareHelper.INSTANCE.getBuilder().targetId(bean.targetId).type(bean.mType).liuyan(bean.liuyan).action(ConversationActivity.REQUEST_SHARE_VIDEO).toShare();
        }
    }
}
