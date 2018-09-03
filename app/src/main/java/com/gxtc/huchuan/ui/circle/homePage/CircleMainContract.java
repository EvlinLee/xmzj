package com.gxtc.huchuan.ui.circle.homePage;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/6.
 */

public interface CircleMainContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<CircleMainContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<CircleHomeBean> datas);

        void showGroupRule(GroupRuleBean data);

        void update2DeleteCircle(int circleId);

        void update2AddFavorite(int circlePosition, ThumbsupVosBean data);

        void update2DeleteFavort(int circlePosition, String favortId);

        void update2AddComment(int circlePosition, CircleCommentBean addItem);

        void update2DeleteComment(int circlePosition, int commentId);

        void showRefreshFinish(List<CircleHomeBean> datas);

        void showLoadMore(List<CircleHomeBean> datas);

        void showNoMore();

        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);

        void update2AddComment(int circlePosition, List<CircleCommentBean> data);

        void update2DeleteCircleItem(int groupInfoId, int commentPosition);
    }

    /**
     * presenter层接口
     */
    public interface Presenter extends BasePresenter {
        /**
         * @param isRefresh 是否刷新
         */
        void getData(boolean isRefresh);

        void getGroupRule(int groupId);

        void loadMrore();

        void addComment(String content, CommentConfig commentConfig);

        void addCommentReply(String comment, CommentConfig commentConfig);

        void support(String token, CommentConfig comomentConfig);

        void showEditTextBody(CommentConfig commentConfig);

        void getMoreComment(CommentConfig commentConfig);

        void removeComment(CommentConfig commentConfig);

        void removeCommentItem(CommentConfig commentConfig);
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(int goupid, int start,long loadTime, ApiCallBack<List<CircleHomeBean>> callBack);



        void addComment(String content, int id, int groupId, ApiCallBack<CircleCommentBean> callBack);

        void addCommentReply(HashMap<String, String> map, ApiCallBack<CircleCommentBean> callBack);

        void support(String token, int infoId, ApiCallBack<ThumbsupVosBean> callBack);



        void getMoreComment(int start, int groupInfoId,
                ApiCallBack<List<CircleCommentBean>> apiCallBack);

        void removeComment(String token, int groupInfoId, ApiCallBack<Void> apiCallBack);

        void removeCommentItem(String token, int commentid, ApiCallBack<Void> apiCallBack);
    }


//    interface View extends BaseUiView<CircleMainContract.Presenter>,BaseListUiView<CircleHomeBean>{
//        void showFolderData(List<CircleHomeBean> datas);
//        void showDZSuccess(int id);
//    }
//
//    interface Presenter extends BasePresenter,BaseListPresenter{
//        void getData(int id);
//        void dianZan(int id);
//    }

}
