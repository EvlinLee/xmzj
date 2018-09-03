package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify.SeriesClassifyContract;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/3/29 .
 */

public class SeriesClassifyRepository extends BaseRepository implements SeriesClassifyContract.Source {
    @Override
    public void destroy() {

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
    public void editClassifyName(HashMap<String, String> map, ApiCallBack<List<ChooseClassifyBean>> callBack) {
        addSub(MineApi.getInstance().saveChatSeriesType(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(callBack)));
    }

    @Override
    public void delSeriseClassify(HashMap<String, String> map, ApiCallBack<List<ChooseClassifyBean>> callBack) {
        addSub(MineApi.getInstance().deleteChatSeriesType(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ChooseClassifyBean>>>(callBack)));
    }
}
