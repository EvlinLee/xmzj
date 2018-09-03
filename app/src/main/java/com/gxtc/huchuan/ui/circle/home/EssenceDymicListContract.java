package com.gxtc.huchuan.ui.circle.home;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/8 .
 */

 public class EssenceDymicListContract {

    public interface View extends BaseUserView<Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showEssenceList(List<CircleHomeBean> datas);

        void update2DeleteCircle(int circleId);



        void update2AddFavorite(int circlePosition, ThumbsupVosBean data);

        void update2DeleteFavort(int circlePosition, String favortId);



        void update2AddComment(int circlePosition, CircleCommentBean addItem);

        void update2DeleteComment(int circlePosition, int commentId);

        /**
         * 刷新结束
         *
         * @param datas
         */
        void showRefreshFinish(List<CircleHomeBean> datas);

        /**
         * 加载更多
         *
         * @param datas
         */
        void showLoadMore(List<CircleHomeBean> datas);

        /**
         * 没有更多数据
         */
        void showNoMore();

        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);

        void update2AddComment(int circlePosition, List<CircleCommentBean> data);

        void update2DeleteCircleItem(int groupInfoId, int commentPosition);
    }

    public interface Presenter extends BasePresenter {
        /**
         * @param isRefresh 是否刷新
         */
        void getEssenceList(boolean isRefresh);

        void loadMrore();

        void addComment(String content, CommentConfig commentConfig,int groupId);


        void addCommentReply(String comment, CommentConfig commentConfig);

        void support(String token, CommentConfig comomentConfig);

        void showEditTextBody(CommentConfig commentConfig);

        void getMoreComment(CommentConfig commentConfig);

        void removeComment(CommentConfig commentConfig);

        void removeCommentItem(CommentConfig commentConfig);
    }

    public interface Source extends BaseSource {
        void getEssenceList(int groupId, int start, ApiCallBack<List<CircleHomeBean>> callBack);

        void addComment(String content, int groupInfoId,int groupId,
                ApiCallBack<CircleCommentBean> circleMainPresenter);

        void addCommentReply(HashMap<String, String> map,
                ApiCallBack<CircleCommentBean> circleMainPresenter);

        void support(String token, int groupInfoId, ApiCallBack<ThumbsupVosBean> apiCallBack);

        void getMoreComment(int commentCount, int groupInfoId,
                ApiCallBack<List<CircleCommentBean>> apiCallBack);

        void removeComment(String token, int groupInfoId, ApiCallBack<Void> callBack);

        void removeCommentItem(String token, int commentId, ApiCallBack<Void> callBack);
    }
}
