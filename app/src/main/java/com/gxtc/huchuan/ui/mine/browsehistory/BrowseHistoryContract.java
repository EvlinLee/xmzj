package com.gxtc.huchuan.ui.mine.browsehistory;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class BrowseHistoryContract {
    public interface View extends BaseUserView<Presenter> {
        void showData(List<BrowseHistoryBean> datas);
        void showRefreshFinish(List<BrowseHistoryBean> datas);
        void showDelResult(List<Object> datas);
        void showLoadMore(List<BrowseHistoryBean> datas);
        void showNoMore();
    }

    public interface Presenter extends BasePresenter {
        void getData(boolean isRefresh);
        void deleteBrowseRecord(String token, ArrayList<String> list);
        void loadMore();
    }

    public interface Source extends BaseSource {
//        浏览类型。1：新闻文章，2：课程，3：交易信息，4：圈子      不传显示全部类型
        void getData(String token,String start, ApiCallBack<List<BrowseHistoryBean>>callBack);

        //删除浏览记录
        void deleteBrowseRecord(String token, ArrayList<String> list, ApiCallBack<List<Object>> callBack);
    }
}
