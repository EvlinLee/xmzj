package com.gxtc.huchuan.ui.circle.home;

import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.CircleHomeRepository;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 圈子首页
 */

public class CircleHomePresenter implements CircleHomeContract.Presenter {
    private CircleHomeContract.Source mData;
    private CircleHomeContract.View   mView;
    private int start = 0;

    public CircleHomePresenter(CircleHomeContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CircleHomeRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }


    @Override
    public void getData(final boolean isRefresh) {
        if(mView == null) return;
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }

        mData.getData(start, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(start, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void addComment(String content, final CommentConfig commentConfig) {
        mData.addComment(content, commentConfig.groupInfoId, new ApiCallBack<CircleCommentBean>() {
            @Override
            public void onSuccess(CircleCommentBean data) {
                if(mView == null) return;
                if (data != null) {
                    mView.update2AddComment(commentConfig.circlePosition, data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });

    }

    @Override
    public void addCommentReply(String comment, final CommentConfig commentConfig) {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", UserManager.getInstance().getToken());
        map.put("groupInfoId", "" + commentConfig.groupInfoId);
        map.put("comment", comment);
        map.put("userCode", commentConfig.targetUserCode);
        mData.addCommentReply(map, new ApiCallBack<CircleCommentBean>() {
            @Override
            public void onSuccess(CircleCommentBean data) {
                if(mView == null) return;
                mView.update2AddComment(commentConfig.circlePosition, data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

    private int recommendStart = 0;
    @Override
    public void recommenddynamic(final boolean isRefresh, String type) {
        if(mView == null) return;
        if (isRefresh) {
            recommendStart = 0;
        } else {
            mView.showLoad();
        }
        String token = null;
        if(!TextUtils.isEmpty(UserManager.getInstance().getToken())){
            token = UserManager.getInstance().getToken();
        }

        mData.recommenddynamic(token, type , recommendStart, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void recommendloadMrore(String type) {
        recommendStart += 15;
        String token = null;
        if(!TextUtils.isEmpty(UserManager.getInstance().getToken())){
            token = UserManager.getInstance().getToken();
        }
        mData.recommenddynamic(token ,type, recommendStart, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }


    @Override
    public void support(String token, final CommentConfig comomentConfig) {
        mView.showLoad();
        mData.support(token, comomentConfig.groupInfoId, new ApiCallBack<ThumbsupVosBean>() {
            @Override
            public void onSuccess(ThumbsupVosBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data.getUserCode() == null) {
                    //删除点赞
                    mView.update2DeleteFavort(comomentConfig.circlePosition, null);
                } else {
                    //添加点赞
                    mView.update2AddFavorite(comomentConfig.circlePosition, data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showLoadFinish();
            }
        });
    }

    @Override
    public void showEditTextBody(CommentConfig commentConfig) {
        if (mView != null) {
            mView.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }

    @Override
    public void getMoreComment(final CommentConfig commentConfig) {
        mData.getMoreComment(commentConfig.commentCount, commentConfig.groupInfoId,
                new ApiCallBack<List<CircleCommentBean>>() {

                    @Override
                    public void onSuccess(List<CircleCommentBean> data) {
                        if(mView == null) return;
                        mView.update2AddComment(commentConfig.circlePosition, data);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView != null)
                            mView.showError(message);
                    }
                });

    }

    @Override
    public void removeComment(final CommentConfig commentConfig) {
        if (UserManager.getInstance().isLogin()) {

            mData.removeComment(UserManager.getInstance().getToken(), commentConfig.groupInfoId,
                    new ApiCallBack<Void>() {

                        @Override
                        public void onSuccess(Void data) {
                            if(mView == null) return;
                            mView.update2DeleteCircle(commentConfig.groupInfoId);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            if(mView == null) return;
                            ErrorCodeUtil.handleErr(mView, errorCode, message);
                        }
                    });
        }
    }

    @Override
    public void removeCommentItem(final CommentConfig commentConfig) {
        if (UserManager.getInstance().isLogin()) {

            mData.removeCommentItem(UserManager.getInstance().getToken(), commentConfig.commentId,
                    new ApiCallBack<Void>() {

                        @Override
                        public void onSuccess(Void data) {
                            if(mView == null) return;
                            mView.update2DeleteComment(commentConfig.circlePosition, commentConfig.commentId);
                        }

                        @Override
                        public void onError(String errorCode, String message) {
                            ErrorCodeUtil.handleErr(mView, errorCode, message);
                        }
                    });
        }
    }

    /**
     * 获取用户创建的圈子  0：全部数据、1：创建的、2：关注的 3：管理的
     */
    @Override
    public void getHeadData(String type){
        Subscription sub = AllApi.getInstance().listByUser(UserManager.getInstance().getToken(),
                type).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<MineCircleBean>>>(new ApiCallBack<List<MineCircleBean>>() {
                    @Override
                    public void onSuccess(List<MineCircleBean> data) {
                        if(data == null || data.size() == 0){
                            mView.headEmpty();
                            return;
                        }

                        mView.showHeadData(data);
                    }

                    @Override
                    public void onError(String errorCode, String message) {

                    }
                }));

        RxTaskHelper.getInstance().addTask(this, sub);
    }
}