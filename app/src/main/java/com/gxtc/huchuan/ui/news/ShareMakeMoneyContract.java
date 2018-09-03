package com.gxtc.huchuan.ui.news;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 分享赚钱
 */

public class ShareMakeMoneyContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<ShareMakeMoneyContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<ShareMakeMoneyBean> datas);

        void showMoneyList(List<PreProfitBean> datas);

        /**
         * 刷新结束
         *
         * @param datas
         */
        void showRefreshFinish(List<ShareMakeMoneyBean> datas);

        void showMoneyListRefreshFinish(List<PreProfitBean> datas);

        /**
         * 加载更多
         *
         * @param datas
         */
        void showLoadMore(List<ShareMakeMoneyBean> datas);

        void showLoadMoreMoneyList(List<PreProfitBean> datas);
        /**
         * 没有更多数据
         */
        void showNoMore();
    }

    /**
     * presenter层接口
     */
    public interface Presenter extends BasePresenter {
        /**
         * @param isRefresh 是否刷新
         */
        void getData(boolean isRefresh,String token, String type,String orderByType,String searchParm);

        void getMMoneyList(boolean isRefresh);

        void loadMrore(String token, String type,String orderByType,String searchParm);

        void loadMoreMoneyList();
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(String token, String type,String orderByType,int start,String searchParm, ApiCallBack<List<ShareMakeMoneyBean>> callBack);

//        获取分销赚钱邀请列表接口
        void getMMoneyList(int start,ApiCallBack<List<PreProfitBean>> callBack);
    }
}
