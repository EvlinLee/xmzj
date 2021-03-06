package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sjr on 2017/3/30.
 * 视频页
 * 理论上来说视频页跟资讯页还有收藏页都是一样的布局是可以复用的，但是MVP架构复用之后反而
 * 代码别的很乱，所以单独写
 */

public class VideoNewsContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<VideoNewsContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<NewsBean> datas);

        /**
         * 刷新结束
         *
         * @param datas
         */
        void showRefreshFinish(List<NewsBean> datas);

        /**
         * 加载更多
         *
         * @param datas
         */
        void showLoadMore(List<NewsBean> datas);

        /**
         * 没有更多数据
         */
        void showNoMore();

        void showShieldSuccess(String articleId, String targetUserCode);

        void showShieldError(String articleId, String targetUserCode, String msg);
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

        void shieldType(String newsId, String targetUserCode, int type, String shieldType);
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<NewsBean>> callBack);
    }
}
