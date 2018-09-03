package com.gxtc.huchuan.ui.deal.deal.goodsDetailed;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.GoodsCommentBean;
import com.gxtc.huchuan.bean.GoodsDetailedBean;

import java.util.List;


public interface GoodsDetailedContract {

    interface View extends BaseUserView<Presenter> {
        void showData(GoodsDetailedBean bean);
        void showComments(List<GoodsCommentBean> beans);
        void showLoadMore(List<GoodsCommentBean> beans);
        void showNoLoadMore();
        void showNoComments();
        void showSubmitSuccess();
        void showReplySuccess();
        void showCollectSuccess();
        void showDeletCommentSuccess(int id);
        void showDZSuccess(int id);
    }

    interface Presenter extends BasePresenter{
        void getData(String infoId);
        void getComments(String infoId);
        void submitComments(String infoId, String content);
        void replyComments(String commentId, String content,String targetUserId);
        void loadMore(String id);
        void collect(int id);
        void deletComment(int id);
        void DZ(int id);
    }


}
