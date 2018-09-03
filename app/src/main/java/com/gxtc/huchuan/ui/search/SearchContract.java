package com.gxtc.huchuan.ui.search;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.dao.SearchHistory;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Steven on 17/3/6.
 */

public interface SearchContract {

    interface View extends BaseUiView<SearchContract.Presenter>{
        void showSearchResult(List<SearchBean> bean);

        void showHistory(List<SearchHistory> beans);

    }

    interface Presenter extends BasePresenter{
        void getSearch(HashMap<String,String> map);
        void getHistory();
        void saveHistory(SearchHistory bean);
        void delHistory();
    }

    interface Source extends BaseSource{
        //热门搜索
//        void getHot(HashMap<String,String> map,ApiCallBack<SearchBean> callBack);

        void getSearch(HashMap<String,String> map,ApiCallBack<List<SearchBean>> callBack);
        List<SearchHistory> getHistory();
        long saveHistory(SearchHistory bean);
        Void delHistory();

    }

}
