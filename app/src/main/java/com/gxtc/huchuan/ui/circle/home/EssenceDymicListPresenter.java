package com.gxtc.huchuan.ui.circle.home;

import android.util.Log;
import android.view.View;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.EssenceDymicListRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/8 .
 */

public class EssenceDymicListPresenter implements EssenceDymicListContract.Presenter {
    private EssenceDymicListContract.Source mData;
    private EssenceDymicListContract.View   mView;
    private int start = 0;
    private int groupId;

    public EssenceDymicListPresenter(int groupId,EssenceDymicListContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.groupId = groupId;
        mData = new EssenceDymicListRepository();
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
    public void getEssenceList(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            if (UserManager.getInstance().isLogin()) {

            }
        } else {
            mView.showLoad();
        }

        mData.getEssenceList(groupId, start, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showEssenceList(data);
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
        mData.getEssenceList(groupId, start, new ApiCallBack<List<CircleHomeBean>>() {

            @Override
            public void onSuccess(List<CircleHomeBean> data) {
                if(mView == null)   return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                mView.showError(message);
            }
        });
    }
    
    @Override
    public void addComment(String content, final CommentConfig commentConfig,int groupId) {
        mData.addComment(content, commentConfig.groupInfoId,groupId, new ApiCallBack<CircleCommentBean>() {
            @Override
            public void onSuccess(CircleCommentBean data) {
                if(mView == null)   return;
                if (data != null) {
                    mView.update2AddComment(commentConfig.circlePosition, data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {

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
                if(mView == null)   return;
                mView.update2AddComment(commentConfig.circlePosition, data);
            }

            @Override
            public void onError(String errorCode, String message) {
            }
        });
    }

    @Override
    public void support(String token, final CommentConfig comomentConfig) {
        mData.support(token, comomentConfig.groupInfoId, new ApiCallBack<ThumbsupVosBean>() {
            @Override
            public void onSuccess(ThumbsupVosBean data) {
                if(mView == null)   return;
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
                        if(mView == null)   return;
                        mView.update2AddComment(commentConfig.circlePosition, data);
                    }

                    @Override
                    public void onError(String errorCode, String message) {

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
                            if(mView == null)   return;
                            mView.update2DeleteCircle(commentConfig.groupInfoId);
                        }

                        @Override
                        public void onError(String errorCode, String message) {}
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
                            if(mView == null)   return;
                            mView.update2DeleteComment(commentConfig.circlePosition, commentConfig.commentId);
                        }

                        @Override
                        public void onError(String errorCode, String message) {}
                    });
        }
    }

}
