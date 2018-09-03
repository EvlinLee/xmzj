package com.gxtc.huchuan.ui.mine.account;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 未结算金额
 */

public class FreezeAccountContract {

    /**
     * view层接口
     * 如果涉及到用户相关的 那么这个接口应该继承
     *
     * @see BaseUserView  区别在于多了个token过期的回调方法
     * <p>
     * 如果没涉及到用户相关的操作 那么这个接口直接继承
     * @see BaseUiView
     */
    public interface View extends BaseUserView<FreezeAccountContract.Presenter> {
        /**
         * 展示数据
         *
         * @param datas
         */
        void showData(List<FreezeAccountBean> datas);


        /**
         * 加载更多
         *
         * @param datas
         */
        void showLoadMore(List<FreezeAccountBean> datas);

        /**
         * 刷新数据
         * @param datas
         */
        void showRefreshFinish(List<FreezeAccountBean> datas);

        /**
         * 没有更多数据
         */
        void showNoMore();
    }

    /**
     * presenter层接口
     */
    public interface Presenter extends BasePresenter {

        void getData(boolean isRefresh);

        void loadMrore();
    }

    /**
     * model层接口 实现类还需要继承
     *
     * @see BaseRepository
     */
    public interface Source extends BaseSource {
        void getData(int start, ApiCallBack<List<FreezeAccountBean>> callBack);
    }
}
