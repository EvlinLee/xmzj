package com.gxtc.huchuan.ui.mine.deal;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;

import java.util.List;


public interface MyIssueDetailedContract {

    interface View extends BaseUserView<MyIssueDetailedContract.Presenter> {
        void showSubmitSuccess();
        void showReplySuccess();
        void showDeletCommentSuccess(int id);
        void showDZSuccess(int id);
    }

    interface Presenter extends BasePresenter{
        void submitComments(String infoId, String content);
        void replyComments(String commentId, String content, String targetUserId);
        void deletComment(int id);
        void DZ(int id);
    }


}
