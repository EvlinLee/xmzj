package com.gxtc.huchuan.ui.circle.home;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CommentConfig;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 圈子首页
 */

public class CircleHomeContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<CircleHomeContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<CircleHomeBean> datas);

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

        void update2DeleteCircle(int circleId);

        void update2AddFavorite(int circlePosition, ThumbsupVosBean data);

        void update2DeleteFavort(int circlePosition, String favortId);

        void update2AddComment(int circlePosition, CircleCommentBean addItem);

        void update2AddComment(int circlePosition, List<CircleCommentBean> data);

        void update2DeleteCircleItem(int groupInfoId, int commentPosition);

        void update2DeleteComment(int circlePosition, int commentId);

        void showHeadData(List<MineCircleBean> data);

        void headEmpty();
    }

    /**
     * presenter层接口
     */
    public interface Presenter extends BasePresenter {
        /**
         * @param isRefresh 是否刷新
         */
        void getData(boolean isRefresh);

        void loadMrore();

        void showEditTextBody(CommentConfig commentConfig);

        void support(String token, CommentConfig commentConfig);

        void getMoreComment(CommentConfig commentConfig);

        void removeComment(CommentConfig commentConfig);

        void removeCommentItem(CommentConfig commentConfig);

        void addComment(String content, CommentConfig commentConfig);

        void addCommentReply(String content, CommentConfig commentConfig);

        void recommenddynamic(boolean isRefresh, String type);

        void recommendloadMrore(String type);

        void getHeadData(String type);
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<CircleHomeBean>> callBack);

        void recommenddynamic(String token ,String type, int start, ApiCallBack<List<CircleHomeBean>> callBack);

        void addComment(String content, int groupInfoId, ApiCallBack<CircleCommentBean> circleMainPresenter);

        void addCommentReply(HashMap<String, String> map,
                ApiCallBack<CircleCommentBean> circleMainPresenter);

        void support(String token, int groupInfoId, ApiCallBack<ThumbsupVosBean> apiCallBack);

        void getMoreComment(int commentCount, int groupInfoId,
                ApiCallBack<List<CircleCommentBean>> apiCallBack);

        void removeComment(String token, int groupInfoId, ApiCallBack<Void> callBack);

        void removeCommentItem(String token, int commentId, ApiCallBack<Void> callBack);
    }
}
