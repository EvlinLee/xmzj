package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse.CreateSeriesCourseContract;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/3/23 .
 */

public class CreateSeriesCourseRepository extends BaseRepository implements CreateSeriesCourseContract.Source {

    @Override
    public void createLiveSeries(HashMap<String, String> map, ApiCallBack<SeriesPageBean> callBack) {
        addSub(MineApi.getInstance().saveChatSeries(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<SeriesPageBean>>(callBack)));
    }

    @Override
    public void getChatSeriesTypeList(HashMap<String, String> map, ApiCallBack<List<ChooseClassifyBean>> callBack) {
        addSub(MineApi.getInstance().getChatSeriesTypeList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(callBack)));
    }

    @Override
    public void addSeriesClassify(HashMap<String, String> map, ApiCallBack<List<ChooseClassifyBean>> callBack) {
        addSub(MineApi.getInstance().saveChatSeriesType(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(callBack)));
    }

    @Override
    public void delSeries(HashMap<String, String> map, ApiCallBack<Object> callBack) {
        addSub(MineApi.getInstance().deleteSeries(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(callBack)));
    }
}
