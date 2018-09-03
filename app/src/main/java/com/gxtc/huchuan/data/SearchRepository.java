package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.dao.SearchHistory;
import com.gxtc.huchuan.bean.dao.SearchHistoryDao;
import com.gxtc.huchuan.helper.GreenDaoHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.search.SearchContract;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/3/6.
 */

public class SearchRepository extends BaseRepository implements SearchContract.Source {
    @Override
    public void getSearch(HashMap<String, String> map, ApiCallBack<List<SearchBean>> callBack) {
        addSub(AllApi.getInstance().searchList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<SearchBean>>>(callBack)));
    }

    //获取历史记录
    @Override
    public List<SearchHistory> getHistory() {
        SearchHistoryDao dao = GreenDaoHelper.getInstance().getSeeion().getSearchHistoryDao();
        return dao.loadAll();
    }

    //插入一条历史记录
    @Override
    public long saveHistory(SearchHistory bean) {
        SearchHistoryDao dao = GreenDaoHelper.getInstance().getSeeion().getSearchHistoryDao();
        QueryBuilder<SearchHistory> qb = dao.queryBuilder();
        List<SearchHistory> list = qb.where(SearchHistoryDao.Properties.SearchContent.eq(bean.getSearchContent())).list();
        long id = -1;
        if(list.size() == 0){
            id = dao.insert(bean);
        }
        return id ;
    }

    @Override
    public Void delHistory() {
        SearchHistoryDao dao = GreenDaoHelper.getInstance().getSeeion().getSearchHistoryDao();
        dao.deleteAll();
        return null;
    }


}
