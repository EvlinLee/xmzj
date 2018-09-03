package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.ImageUtils;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;
import com.gxtc.huchuan.widget.AddPopWindow;
import com.gxtc.huchuan.widget.CommentListTextView;
import com.gxtc.huchuan.widget.PraiseTextView;
import com.gxtc.huchuan.widget.ScrollGridLayoutManager;
import com.gxtc.huchuan.widget.commentlistview.CommentListView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CircleDynamicAdapterV5 extends BaseMoreTypeRecyclerAdapter<CircleHomeBean> {
    private static final String TAG = "CircleDynamicAdapter";
    private Context mContext;
    private Activity mActivity;
    private List<CircleHomeBean> mDdatas;
    private CircleHomeImgAdapter mAdapter;
    private OnShareAndLikeItemListener listener;
    private OnCommentAndPraiseClickListener mCPListener;
    private RecyclerView mRecyclerView;

    Subscription sub;

    Boolean szflag = true;

    public CircleDynamicAdapterV5(Context mContext, Activity activity, List<CircleHomeBean> datas,
                                  int... resid) {
        super(mContext, datas, resid);
        this.mContext = mContext;
        this.mActivity = activity;
        this.mDdatas = datas;
    }

    public CircleDynamicAdapterV5(RecyclerView recyclerView, Context mContext, Activity activity, List<CircleHomeBean> datas,
                                  int... resid) {
        super(mContext, datas, resid);
        this.mContext = mContext;
        this.mActivity = activity;
        this.mDdatas = datas;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void bindData(final BaseMoreTypeRecyclerAdapter.ViewHolder holder, final int position,
                         final CircleHomeBean bean) {

        if (0 == bean.getType()) {//无图

        } else if (1 == bean.getType()) {//视频

//            TextView tvContent = (TextView) holder.getView(R.id.tv_circle_home_video_content);
//            if (!TextUtils.isEmpty(bean.getContent()))
//                tvContent.setText(bean.getContent());
//            else tvContent.setVisibility(View.GONE);

            final JZVideoPlayerStandard player = (JZVideoPlayerStandard) holder.getView(
                    R.id.play_circle_video_cover);
            player.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_LIST, "", bean.getVideoPic());
            Observable.just(bean.getVideoUrl()).map(
                    new Func1<String, Bitmap>() {
                        @Override
                        public Bitmap call(String s) {
                            return ImageUtils.createVideoThumbnail(s, WindowUtil.getScreenW(mContext), 360);

                        }
                    }
            ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            player.thumbImageView.setImageBitmap(bitmap);
                        }
                    });
            //没有封面
            //ImageHelper.loadImage(mContext, player.thumbImageView, bean.getCover());0
        } else {
            RecyclerView recyclerView = (RecyclerView) holder.getView(R.id.rv_circle_home_item);
            recyclerView.setTag(bean);

            recyclerView.setNestedScrollingEnabled(false);

            mAdapter = new CircleHomeImgAdapter(mContext, bean.getPicList(),
                    R.layout.item_circle_home_img2, R.layout.item_circle_home_img);



//            ScrollGridLayoutManager manager = (ScrollGridLayoutManager) recyclerView.getLayoutManager();
//            if (manager == null) {
//                manager = new ScrollGridLayoutManager(mContext, 1);
//                manager.setScrollEnabled(false);
//            }
//            if (bean.getPicList().size() == 1) {
//                manager.setSpanCount(1);
//            } else if (bean.getPicList().size() != 4) {
//                manager.setSpanCount(3);
//            } else {
//                manager.setSpanCount(2);
//            }
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

        }

        RelativeLayout rlRoot = (RelativeLayout) holder.getView(R.id.rl_root);
        rlRoot.setTag(bean);


        //内容
        final TextView tvContent = (TextView) holder.getView(R.id.tv_circle_home_three_content);
        if (!TextUtils.isEmpty(bean.getContent())) tvContent.setText(bean.getContent());
        else tvContent.setVisibility(View.GONE);

        final TextView tvzk = (TextView) holder.getView(R.id.tv_content_zk);

        LinearLayout llzk = (LinearLayout) holder.getView(R.id.ll_content_zk);

        if (bean.getContent().length() <= 200) {
            tvzk.setVisibility(View.GONE);
        } else if (bean.getContent().length() > 200) {
            tvContent.setLines(6);
            tvzk.setVisibility(View.VISIBLE);
            tvzk.setText("全文");
        }

        llzk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (szflag) {
                    szflag = false;
                    tvzk.setText("收起");
                    tvContent.setEllipsize(null);
                    tvContent.setSingleLine(szflag);
                } else {
                    szflag = true;
                    tvzk.setText("全文");
                    tvContent.setLines(6);
                    mRecyclerView.scrollToPosition(position + 1);
                }

            }
        });

        //用户名
        TextView tvNoName = (TextView) holder.getView(R.id.tv_circle_home_name);
        tvNoName.setText(bean.getUserName());
        //
//        //头像
        ImageView ivNoName = (ImageView) holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadImage(mContext, ivNoName, bean.getUserPic());
        ivNoName.setTag(R.id.tag_first, bean);

        ivNoName.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                CircleHomeBean bean = (CircleHomeBean) v.getTag(R.id.tag_first);
                listener.goToCircleHome(bean);
            }
        });
        holder.getView(R.id.v_commentline).setVisibility(bean.getCommentVos().size()>0&&bean.getThumbsupVos().size() > 0 ? View.VISIBLE : View.GONE);

        commentDialog(holder, position, bean);
        setPraiset(holder, position, bean);
        CommentList(holder, position, bean);

        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_circle_home_time);
        if (!(0 == bean.getCreateTime()))
            tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));

        //
////        //评论数
//        TextView tvNoComment = (TextView) holder.getView(R.id.tv_circle_home_comment);
//        tvNoComment.setText(String.valueOf(bean.getLiuYan()));

        //点赞
//        dianZan(holder, bean);


//        //分享
//        RelativeLayout rlShare = (RelativeLayout) holder.getView(R.id.rl_circle_home_share);
//        rlShare.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//                listener.onShareOrDeleteOrComplaint(holder, position, bean);
//            }
//        });
    }

    private void commentDialog(final ViewHolder holder, final int position,
                               final CircleHomeBean bean) {
        final ImageView img = (ImageView) holder.getView(R.id.iv_comment);
//        if (bean.getUserCode().equals(UserManager.getInstance().getUserCode())) {
//            img.setVisibility(View.VISIBLE);
//        } else {
//
//
//        }
//        img.setVisibility(View.VISIBLE);
        boolean flag = false;
        for (ThumbsupVosBean thumbsupVosBean : bean.getThumbsupVos()) {
            if (thumbsupVosBean!=null&&UserManager.getInstance().getUserCode().equals(thumbsupVosBean.getUserCode())){
                flag = true;
                break;
            }
        }
        if (flag) {
            img.setTag("取消");
        } else {
            img.setTag("赞");
        }
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = new AddPopWindow(
                        (Activity) holder.getItemView().getContext(), img,
                        new AddPopWindow.ClickCallBack() {

                            @Override
                            public void clicked(int type) {
                                // 点击取消
                                if (type == 1) {
                                    if ("赞".equals(img.getTag())){

//                                        img.setTag("取消");
                                        CommentConfig commentConfig = new CommentConfig();
                                        commentConfig.groupInfoId = bean.getId();
                                        commentConfig.commentPosition = -1;
                                        commentConfig.circlePosition = position;
                                        commentConfig.commentType = CommentConfig.Type.PUBLIC;

                                        mCPListener.onCancelZan(commentConfig);
                                    } else {
                                        CommentConfig commentConfig = new CommentConfig();
                                        commentConfig.groupInfoId = bean.getId();
                                        commentConfig.commentPosition = -1;
                                        commentConfig.circlePosition = position;
                                        commentConfig.commentType = CommentConfig.Type.PUBLIC;
                                        mCPListener.onDianZan(commentConfig);
//                                        img.setTag("赞");
                                    }

                                } else {
                                    CommentConfig commentConfig = new CommentConfig();
                                    commentConfig.groupInfoId = bean.getId();
                                    commentConfig.commentPosition = -1;
                                    commentConfig.circlePosition = position;
                                    commentConfig.commentType = CommentConfig.Type.PUBLIC;
                                    mCPListener.onPopCommentClick(commentConfig);
//                            // 点击评论
//                            showCommentEditText(sID,
//                                    tv_commentmembers_temp,
//                                    commentArray, view_pop,
//                                    goodArray.size());
                                }
                            }

                        });
                addPopWindow.showPopupWindow(img);
            }
        });
    }

    private void dianZan(ViewHolder holder, final CircleHomeBean bean) {//        //点赞数
        final TextView tvLike = (TextView) holder.getView(R.id.tv_circle_home_like);
        tvLike.setText(String.valueOf(bean.getDianZan()));


        if (1 == bean.getIsDZ())

        {
            Drawable drawable = mActivity.getResources().getDrawable(
                    R.drawable.circle_home_icon_zan_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLike.setCompoundDrawables(drawable, null, null, null);
            tvLike.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));

        } else if (0 == bean.getIsDZ())

        {

            Drawable drawable = mActivity.getResources().getDrawable(
                    R.drawable.circle_home_icon_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLike.setCompoundDrawables(drawable, null, null, null);
            tvLike.setTextColor(mActivity.getResources().getColor(R.color.text_color_999));
        }

        //点赞
        RelativeLayout rlLike = (RelativeLayout) holder.getView(R.id.rl_circle_home_like);
        rlLike.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (1 == bean.getIsDZ()) {
                    like(bean, tvLike, 1);
                } else if (0 == bean.getIsDZ()) {
                    like(bean, tvLike, 0);
//                    listener.onLike(v, 0, position, bean);
                }
            }
        });
    }


    private void setPraiset(ViewHolder holder, final int groupPosition,
                            final CircleHomeBean bean) {
        List<ThumbsupVosBean> thumbsupVos = bean.getThumbsupVos();
        if (bean.getThumbsupVos().size() == 0 && bean.getCommentVos().size() == 0) {
            holder.getView(R.id.ll_foot).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.ll_foot).setVisibility(View.VISIBLE);
        }
        PraiseTextView mPraiseTextView = (PraiseTextView) holder.getView(
                R.id.praisetextview);
        List<PraiseTextView.PraiseInfo> mPraiseInfos = new ArrayList<>();
        for (ThumbsupVosBean thumbsupVo : thumbsupVos) {
            mPraiseInfos.add(new PraiseTextView.PraiseInfo().setId(thumbsupVo.getId()).setNickname(
                    thumbsupVo.getUserName()).setUserCode(thumbsupVo.getUserCode()));
        }
        if (mPraiseInfos.size()==0){
            mPraiseTextView.setVisibility(View.GONE);
        }else{
            mPraiseTextView.setVisibility(View.VISIBLE);
        }
        mPraiseTextView.setData(mPraiseInfos);
        mPraiseTextView.setNameTextColor(holder.getItemView().getResources().getColor(R.color.color_676980));
        mPraiseTextView.setIcon(R.drawable.circle_friend_comment_icon_zan_select);
        mPraiseTextView.setMiddleStr("，");
//        mPraiseTextView.setIconSize (new Rect (0,0,33,33));
        mPraiseTextView.setonPraiseListener(new PraiseTextView.onPraiseClickListener() {
            @Override
            public void onClick(final int position, final PraiseTextView.PraiseInfo mPraiseInfo) {
                PersonalInfoActivity.startActivity(mContext,mPraiseInfo.getUserCode());
//                PersonalHomePageActivity.startActivity(mContext, mPraiseInfo.getUserCode());
                //这里跳个人主页
//                mCPListener.onClick(groupPosition, position, mPraiseInfo);
            }

            @Override
            public void onOtherClick() {
                mCPListener.onPraiseOtherClick();
            }
        });
    }

    private void CommentList(final ViewHolder holder, final int groupPosition,
                             final CircleHomeBean bean) {

        if (UserManager.getInstance().isLogin()) {
            if (UserManager.getInstance().getUserCode().equals(bean.getUserCode())){
                TextView textView = (TextView) holder.getView(R.id.tv_circle_home_remove);
                textView.setVisibility(View.VISIBLE);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: 删除按钮被点击了");
                        CommentConfig commentConfig = new CommentConfig();
                        commentConfig.groupInfoId = bean.getId();
                        commentConfig.circlePosition = groupPosition;
                        commentConfig.commentType = CommentConfig.Type.REPLY;
                        mCPListener.onRemoveCommentClick(commentConfig);

                    }
                });
            } else {
                holder.getView(R.id.tv_circle_home_remove).setVisibility(View.GONE);
            }
        }

        CommentListView commentList = (CommentListView) holder.getView(R.id.commentList);
        final List<CircleCommentBean> commentVos1 = bean.getCommentVos();
        commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(int commentPosition) {
                CircleCommentBean commentItem = commentVos1.get(commentPosition);


                CommentConfig commentConfig = new CommentConfig();
                commentConfig.groupInfoId = bean.getId();
                commentConfig.commentPosition = commentPosition;
                commentConfig.circlePosition = groupPosition;
                commentConfig.commentId = commentItem.getId();
                commentConfig.commentType = CommentConfig.Type.REPLY;
                commentConfig.targetUserCode = commentItem.getUserCode();
                commentConfig.targetUserName = commentItem.getUserName();
                mCPListener.onCommentItemClick(commentConfig);

            }
        });
        TextView morelable = (TextView) holder.getView(R.id.tv_show_more_comment);

        if (commentVos1.size() < bean.getLiuYan()) {
            morelable.setVisibility(View.VISIBLE);
            morelable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: 更多被点击了");
                    CommentConfig commentConfig = new CommentConfig();
                    commentConfig.groupInfoId = bean.getId();
                    commentConfig.circlePosition = groupPosition;
                    commentConfig.commentType = CommentConfig.Type.REPLY;
                    commentConfig.commentCount = bean.getCommentVos().size();
                    mCPListener.onMoreCommentClick(commentConfig);
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
                mCPListener.onCommentItemLongClick(groupPosition, commentPosition, commentItem);
//                CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
//                dialog.show();
            }
        });
        commentList.setDatas(commentVos1);
        commentList.setVisibility(View.VISIBLE);
        commentList.setOnNameTextClickListener(new CommentListView.OnNameTextClickListener() {
            @Override
            public void onTextClick(String name, String id) {
                PersonalInfoActivity.startActivity(mContext,id);

            }
        });


    }

    private void goImgActivity(CircleHomeBean bean, int position) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < bean.getPicList().size(); i++) {
            uris.add(Uri.parse(bean.getPicList().get(i).getPicUrl()));
        }
        CommonPhotoViewActivity.startActivity(mActivity,uris,position);
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == mDdatas.get(position).getType()) {//无图
            return 0;
        } else if (1 == mDdatas.get(position).getType()) {//视频
            return 1;
        }
//        else if (2 == mDdatas.get(position).getType()) {//单图
//            return 2;
//        } else if (3 == mDdatas.get(position).getType()) {//双图
//            return 3;
//        } else if (4 == mDdatas.get(position).getType() || 5 == mDdatas.get(position).getType()) {//三图及以上
//            return 4;
//        }
        else {
            return 2;
        }
        // throw new IllegalArgumentException("服务器返回更多的类型，注意跟服务器协商修改代码！！");
    }

    public void setOnShareAndLikeItemListener(OnShareAndLikeItemListener itemShare) {
        this.listener = itemShare;
    }

    public void setOnCommentAndPraiseClickListener(OnCommentAndPraiseClickListener CPListener) {
        this.mCPListener = CPListener;
    }

    private void like(final CircleHomeBean bean, final TextView textView, final int isLike) {
        if (UserManager.getInstance().isLogin()) {

            sub = AllApi.getInstance().support(UserManager.getInstance().getToken(),
                    bean.getId()).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    new ApiObserver<ApiResponseBean<ThumbsupVosBean>>(new ApiCallBack<ThumbsupVosBean>() {
                        @Override
                        public void onSuccess(ThumbsupVosBean data) {
                            if (1 == isLike) {
                                Drawable drawable = mActivity.getResources().getDrawable(
                                        R.drawable.circle_home_icon_zan);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                        drawable.getMinimumHeight());
                                textView.setCompoundDrawables(drawable, null, null, null);
                                textView.setTextColor(
                                        mActivity.getResources().getColor(R.color.text_color_999));
                                bean.setIsDZ(0);
                                textView.setText(bean.getDianZan() - 1);
                            } else {
                                Drawable drawable = mActivity.getResources().getDrawable(
                                        R.drawable.circle_home_icon_zan_select);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                        drawable.getMinimumHeight());
                                textView.setCompoundDrawables(drawable, null, null, null);
                                textView.setTextColor(
                                        mActivity.getResources().getColor(R.color.colorAccent));
                                bean.setIsDZ(1);
                                textView.setText(bean.getDianZan() + 1);

                            }
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            LoginErrorCodeUtil.showHaveTokenError(mActivity, errorCode, message);
                        }
                    }));
        } else {
            Intent intent = new Intent(mContext, LoginAndRegisteActivity.class);
            mActivity.startActivityForResult(intent, Constant.requestCode.REFRESH_CIRCLE_HOME);
        }
    }

    //分享回调和点赞
    public interface OnShareAndLikeItemListener {
        void onShareOrDeleteOrComplaint(ViewHolder holder, int position,
                                        CircleHomeBean bean);

//        void onLike(View view, int isLike, int position, CircleHomeBean bean);

        void goToCircleHome(CircleHomeBean bean);
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
                                    CircleCommentBean commentItem);

        void onPopCommentClick(CommentConfig commentConfig);

        void onCommentItemClick(CommentConfig commentConfig);

        void onCancelZan(CommentConfig commentConfig);

        void onDianZan(CommentConfig commentConfig);


        void onMoreCommentClick(CommentConfig commentConfig);

        void onRemoveCommentClick(CommentConfig commentConfig);
    }
}
