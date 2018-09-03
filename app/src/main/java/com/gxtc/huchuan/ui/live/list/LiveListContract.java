package com.gxtc.huchuan.ui.live.list;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.LiveListBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Gubr on 2017/2/27.
 */

public interface LiveListContract {

    interface View extends BaseUserView<Presenter> {

        void showLiveListDatas(List<LiveListBean> datas);

        void showLoadMore(List<LiveListBean> datas);




        void canLoadMore(boolean flag);

    }


    interface Presenter extends BasePresenter {
        void getLiveListDatas(int id);

        /**
         * 加载更多。
         */
        void getLoadMore(int id, int count);
    }


     interface Source extends BaseSource {
        /**
         * 获取房间
         * @param callBack
         * @param count
         * @param id
         */
        void getLiveListDatas(ApiCallBack<List<LiveListBean>> callBack,int count,int id);


         /**
          * 获取之前加载的历史
          * @param currentType
          */
         List<LiveListBean> getHistory(String currentType);

         /**
          * 保存历史
          * @param id
          * @param data
          */
         void saveHistory(int id, List<LiveListBean> data);
     }


}
