package com.gxtc.huchuan.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bolex.pressscan.ScanTools;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.handler.QrcodeHandler;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.home.EssenceDymicListActivity;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.special.SpecialDetailActivity;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.TextViewLinkUtils;
import com.gxtc.huchuan.widget.AddPopWindow;
import com.gxtc.huchuan.widget.AutoLinkTextView;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.ExpandVideoPlayer;
import com.gxtc.huchuan.widget.PraiseTextView;
import com.gxtc.huchuan.widget.ScrollGridLayoutManager;
import com.gxtc.huchuan.widget.commentlistview.CommentListView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CircleDynamicAdapter extends BaseMoreTypeRecyclerAdapter<CircleHomeBean> implements View.OnClickListener,
        View.OnLongClickListener {
    private static final String TAG = "CircleDynamicAdapter";
    private boolean isPreviews;
    private Context mContext;
    private Activity mActivity;
    private List<CircleHomeBean> mDdatas;
    private CircleHomeImgAdapter mAdapter;
    private OnShareAndLikeItemListener listener;
    private OnCommentAndPraiseClickListener mCPListener;
    private RecyclerView mRecyclerView;
    private CompositeSubscription mSubscriptions;
    private int srceeWidth = 0;
    private int textLeftMargin = 0;
    private int textRightMargin = 0;

    private CircleShareHandler mShareUtil;
    Subscription sub;
    Subscription subAttention;

    public CircleDynamicAdapter(Context mContext, Activity activity, List<CircleHomeBean> datas, int... resid) {
        super(mContext, datas, resid);
        this.mContext = mContext;
        this.mActivity = activity;
        this.mDdatas = datas;
        srceeWidth = WindowUtil.getScreenWidth(mContext);
        textLeftMargin = (int) (getContext().getResources().getDimension(R.dimen.px90dp) + getContext().getResources().getDimension(R.dimen.px20dp));
        textRightMargin = (int) getContext().getResources().getDimension(R.dimen.px20dp);
    }

    public CircleDynamicAdapter(boolean isPreviews, RecyclerView recyclerView, Context mContext, Activity activity, List<CircleHomeBean> datas, int... resid) {
        super(mContext, datas, resid);
        this.isPreviews = isPreviews;
        this.mContext = mContext;
        this.mActivity = activity;
        this.mDdatas = datas;
        this.mRecyclerView = recyclerView;
        this.mSubscriptions = new CompositeSubscription();
        mShareUtil = new CircleShareHandler(mContext);
        srceeWidth = WindowUtil.getScreenWidth(mContext);
        textLeftMargin = (int) (getContext().getResources().getDimension(R.dimen.px90dp) + getContext().getResources().getDimension(R.dimen.px20dp));
        textRightMargin = (int) getContext().getResources().getDimension(R.dimen.px20dp);
    }

    @Override
    public int getItemViewType(int position) {
        switch (mDdatas.get(position).getType()) {
            //无图
            case 0:
                return 0;

            //视频
            case 1:
                return 1;

            //转发
            case 6:
                return 0;

            case 2:
            case 3:
            case 4:
            case 5:
                return 2;

            //服务器新增加的，应该提示暂不支持
            default:
                return 3;
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void bindData(final BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int position, final CircleHomeBean bean) {

        if (bean == null || bean.getType() == -1) return;
        switch (bean.getType()) {
            //无图
            case 0:
                AutoLinkTextView mAutoLinkTextView = (AutoLinkTextView) holder.getView(R.id.tv_circle_home_three_content);
                mAutoLinkTextView.setOnLongClickListener(this);
                mAutoLinkTextView.setTag(position);
                View layoutShare0 = holder.getView(R.id.layout_share);
                if (layoutShare0.getVisibility() == View.VISIBLE) {
                    layoutShare0.setVisibility(View.GONE);
                }
                break;

            //视频
            case 1:

                if (getItemViewType(position) == 3) break;

                final ExpandVideoPlayer player = (ExpandVideoPlayer) holder.getView(R.id.play_circle_video_cover);


                if (player != null) {
                    player.setVisibility(View.INVISIBLE);
                    player.setUp(bean.getVideoUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "", bean.getVideoPic());
                    player.widthRatio = 0;
                    player.heightRatio = 0;
                    player.currentTimeTextView.setVisibility(View.INVISIBLE);
                    player.totalTimeTextView.setVisibility(View.GONE);
                    player.progressBar.setVisibility(View.INVISIBLE);
                    player.batteryTimeLayout.setVisibility(View.GONE);
                    player.thumbImageView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_efef));
                }

                if (TextUtils.isEmpty(bean.getVideoPic())) {
                    Observable.just(bean.getVideoUrl())
                            .subscribeOn(Schedulers.io())
                            .map(new Func1<String, Bitmap>() {
                                @Override
                                public Bitmap call(String s) {
                                    return ImageUtils.createVideoThumbnail(s, WindowUtil.getScreenW(mContext), 360);

                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Bitmap>() {
                                @Override
                                public void call(Bitmap bitmap) {
                                    if (bitmap == null) return;
                                    setVideoSize(bitmap.getWidth(), bitmap.getHeight(), null, player);
                                    player.thumbImageView.setImageBitmap(bitmap);

                                }
                            });
                } else {

                    setVideoSize(0, 0, bean.getVideoPic(), player);
                    ImageHelper.loadImage(getContext(), player.thumbImageView, bean.getVideoPic());
                }
                break;

            //转发
            case 6:
                if (getItemViewType(position) == 3) break;
                View layoutTime = holder.getView(R.id.layout_time_share);
                LinearLayout.LayoutParams timeParam = (LinearLayout.LayoutParams) layoutTime.getLayoutParams();
                timeParam.topMargin = 0;

                View layoutShare = holder.getView(R.id.layout_share);
                layoutShare.setVisibility(View.VISIBLE);

                View linearlayoutShare = holder.getView(R.id.linear_lauout_share);
                FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams) linearlayoutShare.getLayoutParams();
                parms.rightMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.margin_middle);

                ImageView imgShare = (ImageView) holder.getView(R.id.img_share);
                TextView tvShare = (TextView) holder.getView(R.id.tv_share);
                ImageHelper.loadImage(getContext(), imgShare, bean.getTypeCover(), R.drawable.live_list_place_holder_120, R.drawable.live_list_place_holder_120);

                String title = bean.getTypeTitle();
                if (bean.getInfoType() == 5) {
                    title = bean.getUserName() + "邀请你加入" + title + "圈子";
                }
                tvShare.setText(title);
                layoutShare.setTag(bean);
                layoutShare.setOnClickListener(this);
                break;

            case 2:
            case 3:
            case 4:
            case 5:
                if (getItemViewType(position) == 3) break;
                android.support.v7.widget.RecyclerView recyclerView = (android.support.v7.widget.RecyclerView) holder.getView(R.id.rv_circle_home_item);
                recyclerView.setTag(bean);

                recyclerView.setNestedScrollingEnabled(false);

                mAdapter = new CircleHomeImgAdapter(mContext, bean.getPicList(),
                        R.layout.item_circle_home_img2, R.layout.item_circle_home_img);

                ScrollGridLayoutManager manager;
                if (bean.getPicList().size() == 1) {
                    manager = new ScrollGridLayoutManager(mContext, 1);

                } else if (bean.getPicList().size() != 4) {
                    manager = new ScrollGridLayoutManager(mContext, 3);

                } else {
                    manager = new ScrollGridLayoutManager(mContext, 2);
                }

                manager.setScrollEnabled(false);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnReItemOnClickListener(new OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        goImgActivity(bean, position);
                    }
                });
                mAdapter.setOnReItemOnLongClickListener(new OnReItemOnLongClickListener() {
                    @Override
                    public void onItemLongClick(final View v, int position) {
                        ScanTools.scanCode(v, new ScanTools.ScanCall() {

                            @Override
                            public void getCode(String s) {
                                if (!TextUtils.isEmpty(s)) {
                                    showDiaLog(v, s);
                                }
                            }
                        });
                    }
                });

                break;

            //服务器新新增加的，应该提示暂不支持
            default:
                View layoutTime1 = holder.getView(R.id.layout_time_share);
                LinearLayout.LayoutParams timeParam1 = (LinearLayout.LayoutParams) layoutTime1.getLayoutParams();
                timeParam1.topMargin = 0;

                View layoutShare1 = holder.getView(R.id.layout_share);
                layoutShare1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) layoutShare1.getLayoutParams();
                Resources res1 = getContext().getResources();
                layoutParams1.leftMargin = res1.getDimensionPixelSize(R.dimen.px130dp) - res1.getDimensionPixelSize(R.dimen.margin_middle);
                break;
        }

        RelativeLayout rlRoot = (RelativeLayout) holder.getView(R.id.rl_root);
        rlRoot.setTag(bean);


        final AutoLinkTextView tvContent = (AutoLinkTextView) holder.getView(R.id.tv_circle_home_three_content);
        tvContent.setOnLongClickListener(this);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setTag(position);
        SpannableStringBuilder ssb = null;
        if (bean.getContent() != null) {
            ssb = TextViewLinkUtils.getInstance().getUrlClickableSpan(tvContent, bean.getContent());
        }
        if (!TextUtils.isEmpty(bean.getContent()) && ssb != null) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(ssb);

        } else {
            tvContent.setVisibility(View.GONE);
        }

        final TextView tvzk = (TextView) holder.getView(R.id.tv_content_zk);

        LinearLayout llzk = (LinearLayout) holder.getView(R.id.ll_content_zk);
        llzk.setTag(position);

        int textLength = TextUtils.isEmpty(bean.getContent()) ? 0 : getTextViewLength(tvContent, bean.getContent());
        int availableTextWidth = (srceeWidth - (textLeftMargin + textRightMargin)) * 10;

        if (!TextUtils.isEmpty(bean.getContent()) && availableTextWidth >= textLength) {
            tvzk.setVisibility(View.GONE);
            tvContent.setMaxLines(Integer.MAX_VALUE);

            //} else if (!TextUtils.isEmpty(bean.getContent()) && bean.getContent().length() > 150) {
        } else if (!TextUtils.isEmpty(bean.getContent()) && availableTextWidth < textLength) {
            tvzk.setVisibility(View.VISIBLE);
            tvzk.setTag(bean.tagFlag ? "收起" : "全文");
            tvzk.setText(bean.tagFlag ? "收起" : "全文");
            tvContent.setMaxLines(10);

        } else {
            tvContent.setMaxLines(Integer.MAX_VALUE);
            tvzk.setVisibility(View.GONE);
        }

        if (availableTextWidth < textLength) {
            if (bean.tagFlag) {
                //tvContent.setFilters(new InputFilter[]{});
                tvContent.setMaxLines(Integer.MAX_VALUE);
                tvContent.setEllipsize(null);
            } else {
                tvContent.setMaxLines(10);
                tvContent.setEllipsize(TextUtils.TruncateAt.END);
                //tvContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
            }
        }


        llzk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPosition = (int) v.getTag();
                //收起
                if (bean.tagFlag) {
                    bean.tagFlag = false;
                    mRecyclerView.notifyItemChanged(currPosition);
                    if (currPosition != -1) {
                        mRecyclerView.scrollToPosition(currPosition);
                        LinearLayoutManager mLayoutManager =
                                (LinearLayoutManager) mRecyclerView.getLayoutManager();
                        mLayoutManager.scrollToPositionWithOffset(currPosition + 1, 0);
                    }

                    //全文
                } else {
                    bean.tagFlag = true;
                    mRecyclerView.notifyItemChanged(currPosition);
                }

            }
        });


        //用户名
        TextView tvNoName = (TextView) holder.getView(R.id.tv_circle_home_name);
        tvNoName.setText(bean.getUserName() + "");

        //精华
        ImageView ivEssence = (ImageView) holder.getView(R.id.iv_circle_home_essence);
        ivEssence.setVisibility(0 == bean.getIsGood() ? View.GONE : View.VISIBLE);

        //置顶
        ImageView ivTop = (ImageView) holder.getView(R.id.iv_top);
        ivTop.setVisibility(0 == bean.getIsTop() ? View.GONE : View.VISIBLE);

        ivEssence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EssenceDymicListActivity.class);
                intent.putExtra("groupId", getmId());
                mActivity.startActivity(intent);
            }
        });
        //头像
        ImageView ivNoName = (ImageView) holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadImage(mContext, ivNoName, bean.getUserPic(), R.drawable.circle_head_icon_120, R.drawable.circle_head_icon_120);
        ivNoName.setTag(R.id.tag_first, bean);

        ivNoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleHomeBean bean = (CircleHomeBean) v.getTag(R.id.tag_first);
                listener.goToCircleHome(bean);
            }
        });

        if (bean.getCommentVos() != null) {
            holder.getView(R.id.v_commentline).setVisibility(bean.getCommentVos().size() > 0 && bean.getThumbsupVos().size() > 0 ? View.VISIBLE : View.GONE);
        }

        commentDialog(holder, position, bean);
        setPraiset(holder, position, bean);
        CommentList(holder, position, bean);

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_circle_home_time);
        if (!(0 == bean.getCreateTime()))
            tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));

        //来自哪个圈子
        if (!TextUtils.isEmpty(bean.getGroupName())) {
            TextView tvFrom = (TextView) holder.getView(R.id.tv_circle_home_from);
            tvFrom.setText("来自圈子" + bean.getGroupName());
            tvFrom.setTag(bean);
            tvFrom.setOnClickListener(this);
            tvFrom.setVisibility(View.VISIBLE);
        } else {
            TextView tvFrom = (TextView) holder.getView(R.id.tv_circle_home_from);
            tvFrom.setTag(bean);
            tvFrom.setOnClickListener(this);
            tvFrom.setText("来自个人动态");
        }

        //分享
        TextView ivShare = (TextView) holder.getView(R.id.tex_share);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.shareItem(position, bean);
            }
        });

        //加关注
        final TextView tvAttention = (TextView) holder.getView(R.id.tv_attention);
        tvAttention.setTag(bean);
        ImageView ivShield = (ImageView) holder.getView(R.id.iv_shield);
        ivShield.setTag(bean);
        if (0 == bean.getIsMy() && 0 == bean.getIsAttention()) {
            tvAttention.setVisibility(View.VISIBLE);
            tvAttention.setText("+ 关注");
            tvAttention.setFocusable(true);
            tvAttention.setEnabled(true);
            tvAttention.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            ivShield.setVisibility(View.VISIBLE);
            ivShield.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CircleHomeBean bean2 = (CircleHomeBean) v.getTag();
                    listener.onShield(position, bean2, v);
                }
            });
            tvAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CircleHomeBean circleHomeBean = (CircleHomeBean) v.getTag();
                    AttentionUserDynamic(circleHomeBean.getUserCode(), tvAttention);
                }
            });
        } else if (0 == bean.getIsMy() && 1 == bean.getIsAttention()) {
            tvAttention.setVisibility(View.INVISIBLE);
            ivShield.setVisibility(View.VISIBLE);
            tvAttention.setText("已关注");
            ivShield.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CircleHomeBean bean2 = (CircleHomeBean) v.getTag();
                    listener.onShield(position, bean2, v);
                }
            });
            tvAttention.setTextColor(ContextCompat.getColor(mContext, R.color.color_666666));
            tvAttention.setFocusable(false);
            tvAttention.setEnabled(false);
        } else {
            tvAttention.setVisibility(View.GONE);
            ivShield.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击转发链接
            case R.id.layout_share:
                CircleHomeBean homeBean = (CircleHomeBean) v.getTag();
                UIMessage uiMessage = new UIMessage();
                Message msg = new Message();
                uiMessage.setMessage(msg);
                uiMessage.setSenderUserId(homeBean.getUserCode());

                //系列课主页
                if (homeBean.getInfoType() == 6) {
                    SeriesActivity.startActivity(mContext, homeBean.getTypeId());

                    //商品点击
                } else if (homeBean.getInfoType() == 7) {
                    MallDetailedActivity.startActivity(mContext, homeBean.getTypeId());

                } else if (homeBean.getInfoType() == 8) {
                    LiveHostPageActivity.startActivity(mContext, "1", homeBean.getTypeId());

                } else if(homeBean.getInfoType() == 9){
                    SpecialDetailActivity.gotoSpecialDetailActivity((Activity) mContext, homeBean.getTypeId());

                } else{
                    mShareUtil.shareHandle(mActivity, homeBean.getTypeId(), homeBean.getInfoType(), uiMessage);
                }
                break;

            //点击圈子来自哪里
            case R.id.tv_circle_home_from:
                CircleHomeBean homeBean1 = (CircleHomeBean) v.getTag();
                if (!TextUtils.isEmpty(homeBean1.getGroupName())) {
                    UIMessage uiMessage1 = new UIMessage();
                    Message msg1 = new Message();
                    uiMessage1.setMessage(msg1);
                    uiMessage1.setSenderUserId(homeBean1.getUserCode());
                    mShareUtil.getCircleInfo(homeBean1.getGroupId(), 0, uiMessage1);
                } else {
                    PersonalInfoActivity.startActivity(mContext, homeBean1.getUserCode());
                }
                break;
        }
    }

    private void showDiaLog(final View view, final String code) {
        final QrcodeHandler mQrcodeHandler = new QrcodeHandler(mContext);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.dialog_layout);
        // 设置宽高
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        // 设置弹出的动画效果
//        window.setWindowAnimations(R.style.AnimBottom);
        // 设置监听
        TextView ok = (TextView) window.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.isFastClick()) {
                    if (!TextUtils.isEmpty(code)) {
                        mQrcodeHandler.resolvingCode(code, "");
                        dialog.dismiss();
                    }
                }
            }
        });
    }
//

    /**
     * 关注用户动态
     */

    private void AttentionUserDynamic(final String userCode, final TextView textView) {
        if (UserManager.getInstance().isLogin()) {
            subAttention = AllApi.getInstance().setUserFollow(UserManager.getInstance().getToken(), "3", userCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onSuccess(Object data) {
                            textView.setVisibility(View.INVISIBLE);
                            textView.setText("已关注");
                            textView.setTextColor(ContextCompat.getColor(mContext, R.color.color_666666));
                            textView.setFocusable(false);
                            textView.setEnabled(false);
//
                            listener.notifyRecyclerView(userCode);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ToastUtil.showShort(mContext, message);
                        }
                    }));
            mSubscriptions.add(subAttention);
        } else {
            Intent intent = new Intent(mContext, LoginAndRegisteActivity.class);
            mActivity.startActivityForResult(intent, Constant.requestCode.REFRESH_CIRCLE_HOME);
        }
    }

    private void commentDialog(final ViewHolder holder, final int position, final CircleHomeBean bean) {
        final ImageView img = (ImageView) holder.getView(R.id.iv_comment);
        img.setTag(bean.getIsDZ());
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().isLogin()) {

                    AddPopWindow addPopWindow = new AddPopWindow(
                            (Activity) holder.getItemView().getContext(), img,
                            new AddPopWindow.ClickCallBack() {

                                @Override
                                public void clicked(int type) {
                                    // 点赞
                                    if (type == 1) {
                                        //已点赞
                                        if (1 == (int) img.getTag()) {
                                            CommentConfig commentConfig = new CommentConfig();
                                            commentConfig.groupInfoId = bean.getId();
                                            commentConfig.commentPosition = -1;
                                            commentConfig.circlePosition = position;
                                            commentConfig.commentType = CommentConfig.Type.PUBLIC;

                                            if (isPreviews) {
                                                ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                                            } else {
                                                mCPListener.onCancelZan(commentConfig);
                                            }
                                            img.setTag(0);

                                            //未点赞
                                        } else {
                                            CommentConfig commentConfig = new CommentConfig();
                                            commentConfig.groupInfoId = bean.getId();
                                            commentConfig.commentPosition = -1;
                                            commentConfig.circlePosition = position;
                                            commentConfig.commentType = CommentConfig.Type.PUBLIC;
                                            if (isPreviews) {
                                                ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                                            } else {
                                                mCPListener.onDianZan(commentConfig);
                                            }
                                            img.setTag(1);

                                        }

                                        //评论
                                    } else {
                                        CommentConfig commentConfig = new CommentConfig();
                                        commentConfig.groupInfoId = bean.getId();
                                        commentConfig.commentPosition = -1;
                                        commentConfig.circlePosition = position;
                                        commentConfig.commentType = CommentConfig.Type.PUBLIC;
                                        if (isPreviews) {
                                            ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                                        } else {
                                            mCPListener.onPopCommentClick(commentConfig);
                                        }

                                    }
                                }

                            });
                    addPopWindow.showPopupWindow(img);
                } else {
                    GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);
                }

            }
        });

    }


    private void setPraiset(BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int groupPosition,
                            final CircleHomeBean bean) {
        final ArrayList<ThumbsupVosBean> thumbsupVos = bean.getThumbsupVos();
        if (thumbsupVos == null) {
            holder.getView(R.id.ll_foot).setVisibility(View.GONE);
            return;
        }
        if (bean.getThumbsupVos().size() == 0 && bean.getCommentVos().size() == 0) {
            holder.getView(R.id.ll_foot).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.ll_foot).setVisibility(View.VISIBLE);
        }
        PraiseTextView mPraiseTextView = (PraiseTextView) holder.getView(
                R.id.praisetextview);
        final List<PraiseTextView.PraiseInfo> mPraiseInfos = new ArrayList<>();
        final int count = 10;//仅显示10个人点的赞
        if (thumbsupVos.size() > count) {
            for (int j = 0; j < count + 1; j++) {
                if (j < count) {
                    mPraiseInfos.add(new PraiseTextView.PraiseInfo().setId(thumbsupVos.get(j).getId()).setNickname(thumbsupVos.get(j).getUserName()).setUserCode(thumbsupVos.get(j).getUserCode()));
                } else {
                    mPraiseInfos.add(new PraiseTextView.PraiseInfo().setNickname("等" + bean.getDianZan() + "人点赞"));
                }
            }
        } else {
            for (ThumbsupVosBean thumbsupVo : thumbsupVos) {
                mPraiseInfos.add(new PraiseTextView.PraiseInfo().setId(thumbsupVo.getId()).setNickname(thumbsupVo.getUserName()).setUserCode(thumbsupVo.getUserCode()));
            }
        }

        if (mPraiseInfos.size() == 0) {
            mPraiseTextView.setVisibility(View.GONE);
        } else {
            mPraiseTextView.setVisibility(View.VISIBLE);
        }

        mPraiseTextView.setData(mPraiseInfos);
        mPraiseTextView.setNameTextColor(holder.getItemView().getResources().getColor(R.color.color_676980));
        mPraiseTextView.setIcon(R.drawable.circle_friend_comment_icon_zan_select);
        mPraiseTextView.setMiddleStr("，");
        mPraiseTextView.setonPraiseListener(new PraiseTextView.onPraiseClickListener() {
            @Override
            public void onClick(final int position, final PraiseTextView.PraiseInfo mPraiseInfo) {
                if (position > count - 1) {
                    DynamicDetialActivity.startActivity(mContext, bean.getId() + "");
                } else {
                    PersonalInfoActivity.startActivity(mContext, mPraiseInfo.getUserCode());
                }

            }

            @Override
            public void onOtherClick() {
                if (isPreviews) {
                    ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                } else {
                    mCPListener.onPraiseOtherClick();
                }

            }
        });
    }

    private void CommentList(final BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int groupPosition, final CircleHomeBean bean) {
        if (UserManager.getInstance().isLogin()) {
            if (!TextUtils.isEmpty(bean.getUserCode()) && bean.getUserCode().equals(UserManager.getInstance().getUserCode())) {
                TextView textView = (TextView) holder.getView(R.id.tv_circle_home_remove);
                textView.setVisibility(View.VISIBLE);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentConfig commentConfig = new CommentConfig();
                        commentConfig.groupInfoId = bean.getId();
                        commentConfig.circlePosition = groupPosition;
                        commentConfig.commentType = CommentConfig.Type.REPLY;
                        if (isPreviews) {
                            ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                        } else {
                            if (mCPListener != null) {
                                mCPListener.onRemoveCommentClick(commentConfig);
                            }
                        }


                    }
                });
            } else {
                holder.getView(R.id.tv_circle_home_remove).setVisibility(View.GONE);
            }
        }

        final CommentListView commentList = (CommentListView) holder.getView(R.id.commentList);
        final List<CircleCommentBean> commentVos1 = bean.getCommentVos();
        if (commentVos1 == null) {
            holder.getView(R.id.ll_foot).setVisibility(View.GONE);
            return;
        }
        commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(int commentPosition) {
                CircleCommentBean commentItem = commentVos1.get(commentPosition);
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.item = commentList.getChildAt(commentPosition);
                commentConfig.groupInfoId = bean.getId();
                commentConfig.commentPosition = commentPosition;
                commentConfig.circlePosition = groupPosition;
                commentConfig.commentId = commentItem.getId();
                commentConfig.commentType = CommentConfig.Type.REPLY;
                commentConfig.targetUserCode = commentItem.getUserCode();
                commentConfig.targetUserName = commentItem.getUserName();
                if (isPreviews) {
                    ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                } else {
                    mCPListener.onCommentItemClick(commentConfig);
                }
            }
        });
        TextView morelable = (TextView) holder.getView(R.id.tv_show_more_comment);

        // 原来的判断commentVos1.size() < bean.getLiuYan()  后台默认一次返回15条评论
        if (commentVos1.size() >= 15) {
            morelable.setVisibility(View.VISIBLE);
            morelable.setText(" 共有" + bean.getLiuYan() + "条评论,点击查看更多");
            morelable.setTextColor(mContext.getResources().getColor(R.color.color_676980));
            morelable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentConfig commentConfig = new CommentConfig();
                    commentConfig.groupInfoId = bean.getId();
                    commentConfig.circlePosition = groupPosition;
                    commentConfig.commentType = CommentConfig.Type.REPLY;
                    commentConfig.commentCount = bean.getCommentVos().size();
                    if (isPreviews) {
                        ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                    } else {
                        mCPListener.onMoreCommentClick(commentConfig);
                    }

                }
            });
        } else {
            morelable.setVisibility(View.GONE);
        }
        commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int commentPosition) {
                //长按进行复制或者删除
                CircleCommentBean commentItem = commentVos1.get(commentPosition);
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.item = commentList.getChildAt(commentPosition);
                commentConfig.groupInfoId = bean.getId();
                commentConfig.commentPosition = commentPosition;
                commentConfig.circlePosition = groupPosition;
                commentConfig.commentId = commentItem.getId();
                commentConfig.commentType = CommentConfig.Type.REPLY;
                commentConfig.targetUserCode = commentItem.getUserCode();
                commentConfig.targetUserName = commentItem.getUserName();
                if (isPreviews) {
                    ToastUtil.showShort(getContext(), "加入圈子后才能操作");
                } else {
                    mCPListener.onCommentItemLongClick(groupPosition, commentPosition, commentItem, commentConfig, commentList.getChildAt(commentPosition));
                }

            }
        });
        commentList.setDatas(commentVos1);
        commentList.setVisibility(View.VISIBLE);
        commentList.setOnNameTextClickListener(new CommentListView.OnNameTextClickListener() {
            @Override
            public void onTextClick(String name, String id) {
                PersonalInfoActivity.startActivity(mContext, id);
            }
        });

    }


    //设置视频的尺寸
    private void setVideoSize(int width, int height, String videoPic, ExpandVideoPlayer videoView) {
        float[] size = null;
        if (TextUtils.isEmpty(videoPic)) {
            size = getVideoSize(width, height);
        } else {
            try {
                String sub = videoPic.split("\\?")[1];
                String[] var = sub.split("\\*");
                float tempWidth = StringUtil.toFloat(var[0]);
                float tempheight = StringUtil.toFloat(var[1]);
                size = getVideoSize(tempWidth, tempheight);

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.i(e.getMessage());
            }
        }

        if (size != null && size.length >= 2 && videoView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoView.getLayoutParams();
            params.width = (int) size[0];
            params.height = (int) size[1];
            videoView.setVisibility(View.VISIBLE);
        }
    }

    private float[] getVideoSize(float width, float height) {
        float marginLeft = getContext().getResources().getDimension(R.dimen.px130dp);
        float marginRight = getContext().getResources().getDimension(R.dimen.margin_larger);
        float[] size = new float[2];
        if (width < height) {
            float videoWidth = getContext().getResources().getDimension(R.dimen.px240dp);
            //如果图片比设定最小宽度还小 就给最小宽度的值
            if (width < videoWidth) {
                width = videoWidth;
            }

            float videoHeight = videoWidth * height / width;
            size[0] = videoWidth;
            size[1] = videoHeight;

        } else if (width == height) {
            float videoWidth = getContext().getResources().getDimension(R.dimen.px240dp);
            size[0] = videoWidth;
            size[1] = videoWidth;

        } else {
            float videoWidth = (float) ((WindowUtil.getScreenW(getContext()) - marginLeft - marginRight) * 0.7);
            float videoHeight = videoWidth * height / width;
            if (height < videoHeight) {
                height = videoHeight;
            }
            size[0] = videoWidth;
            size[1] = videoHeight;
        }

        return size;
    }

    @Override
    public void onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mSubscriptions != null) mSubscriptions.unsubscribe();
        if (mShareUtil != null) mShareUtil.destroy();
    }


    private int mId;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    private void goImgActivity(CircleHomeBean bean, int position) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < bean.getPicList().size(); i++) {
            uris.add(Uri.parse(bean.getPicList().get(i).getPicUrl()));
        }
        CommonPhotoViewActivity.startActivity(mActivity, uris, position);
    }

    public void setOnShareAndLikeItemListener(OnShareAndLikeItemListener itemShare) {
        this.listener = itemShare;
    }

    public void setOnCommentAndPraiseClickListener(OnCommentAndPraiseClickListener CPListener) {
        this.mCPListener = CPListener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (getLongListener() != null) {
            getLongListener().onItemLongClick(v, (Integer) v.getTag());
        }
        return false;
    }


    /**
     * 计算出该TextView中文字的长度(像素)
     *
     * @param textView
     * @param text
     * @return
     */
    public static int getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return (int) paint.measureText(text);
    }

    AlertDialog dialog;

    private void commentDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = DialogUtil.showInputDialog(mActivity, false, "", "确认取消该圈子同步？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserManager.getInstance().isLogin()) {


                        } else {
                            Toast.makeText(getContext(), "登录后才能操作", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }


    //分享回调和点赞
    public interface OnShareAndLikeItemListener {

        void goToCircleHome(CircleHomeBean bean);

        void notifyRecyclerView(String userCode);

        void onShield(int position, CircleHomeBean bean, View view);

        void shareItem(int position, CircleHomeBean bean);
    }


    public interface OnCommentAndPraiseClickListener {
        /**
         * 点击人名的回调
         *
         * @param position 第几条评论  从0开始
         * @param mInfo    评论相关信息
         */
        public void onNickNameClick(int groupPosition, int position,
                                    CommentListTextView.CommentInfo mInfo);

        /**
         * 点击被评论人名的回调
         *
         * @param position 第几条评论  从0开始
         * @param mInfo    评论相关信息
         */
        public void onToNickNameClick(int groupPosition, int position,
                                      CommentListTextView.CommentInfo mInfo);

        /**
         * 点击评论文本回调，主要针对对谁回复操作
         *
         * @param position
         * @param mInfo
         */
        public void onCommentItemClick(int groupPosition, int position, CircleCommentBean mInfo);

        /**
         * 点击非文字部分
         */
        public void onCommentOtherClick();


        public void onClick(int groupPosition, int position, PraiseTextView.PraiseInfo mPraiseInfo);

        public void onPraiseOtherClick();

        void onCommentItemLongClick(int groupPosition, int commentPosition,
                                    CircleCommentBean commentItem, CommentConfig config, View view);

        void onPopCommentClick(CommentConfig commentConfig);

        void onCommentItemClick(CommentConfig commentConfig);

        void onCancelZan(CommentConfig commentConfig);

        void onDianZan(CommentConfig commentConfig);


        void onMoreCommentClick(CommentConfig commentConfig);

        void onRemoveCommentClick(CommentConfig commentConfig);
    }
}
