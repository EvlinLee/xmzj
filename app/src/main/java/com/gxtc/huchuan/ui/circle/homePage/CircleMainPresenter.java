package com.gxtc.huchuan.ui.circle.homePage;

import android.util.Log;
import android.view.View;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.CircleMainRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/6.
 */

public class CircleMainPresenter implements CircleMainContract.Presenter {

    private CircleSource              mRuleData;
    private CircleMainContract.Source mData;
    private CircleMainContract.View   mView;
    private int start = 0;
    private int groupId;
    public  long loadTime;

    public CircleMainPresenter(int groupId, CircleMainContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.groupId = groupId;
        loadTime = System.currentTimeMillis();
        mData = new CircleMainRepository();
        mRuleData = new CircleRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mRuleData.destroy();
        mView = null;
    }


    @Override
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            loadTime = System.currentTimeMillis();
        } else {
            if(mView == null) return;
            mView.showLoad();
        }

        mData.getData(groupId, start,loadTime, new ApiCallBack<List<CircleHomeBean>>() {

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
    public void getGroupRule(int groupId) {
        mRuleData.getGroupRule(groupId, new ApiCallBack<GroupRuleBean>() {
            @Override
            public void onSuccess(GroupRuleBean data) {
                if(mView == null) return;
                mView.showGroupRule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        mData.getData(groupId, start,loadTime, new ApiCallBack<List<CircleHomeBean>>() {

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
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void addComment(String content, final CommentConfig commentConfig) {
        Log.d("CircleMainPresenter", commentConfig.toString());
        mData.addComment(content, commentConfig.groupInfoId, commentConfig.getGroupId(),
                new ApiCallBack<CircleCommentBean>() {
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
        if (commentConfig.groupId != null) {
            map.put("groupId", commentConfig.groupId.toString());
        }
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
                        public void onError(String errorCode, String message) {
                            if(mView == null)   return;
                            mView.showError(message);
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
                            if(mView == null)   return;
                            mView.update2DeleteComment(commentConfig.circlePosition, commentConfig.commentId);
                        }

                        @Override
                        public void onError(String errorCode, String message) {}
                    });
        }

    }
}
