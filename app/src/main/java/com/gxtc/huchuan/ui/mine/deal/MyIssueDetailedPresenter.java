package com.gxtc.huchuan.ui.mine.deal;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/15.
 */

public class MyIssueDetailedPresenter implements MyIssueDetailedContract.Presenter {

    private DealSource mData;
    private MyIssueDetailedContract.View mView;

    public MyIssueDetailedPresenter(MyIssueDetailedContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void submitComments(String infoId, String content) {
        String token = UserManager.getInstance().getToken();
        mData.submitComment(token, infoId, content, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showSubmitSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void replyComments(String commentId, String content, String targetUserId) {
        String token = UserManager.getInstance().getToken();
        mData.replyComment(token, commentId, content,targetUserId ,new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showReplySuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void deletComment(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.deleteComment(token, id, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showDeletCommentSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void DZ(final int id) {
        String token = UserManager.getInstance().getToken();
        mData.DzComment(token, id, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showDZSuccess(id);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        mData.destroy();
    }
}
