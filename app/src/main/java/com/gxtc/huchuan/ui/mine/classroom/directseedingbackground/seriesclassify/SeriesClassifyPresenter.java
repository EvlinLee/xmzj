package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.data.SeriesClassifyRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/29 0029.
 */

public class SeriesClassifyPresenter implements SeriesClassifyContract.Presenter {
    private SeriesClassifyContract.Source mData;
    private SeriesClassifyContract.View mView;

    public SeriesClassifyPresenter(SeriesClassifyContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new SeriesClassifyRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;

    }

    @Override
    public void getChatSeriesTypeList(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.getChatSeriesTypeList(map, new ApiCallBack<List<ChooseClassifyBean>>() {
            @Override
            public void onSuccess(List<ChooseClassifyBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showChatSeriesTypeList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void addSeriesClassify(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.addSeriesClassify(map, new ApiCallBack<List<ChooseClassifyBean>>() {
            @Override
            public void onSuccess(List<ChooseClassifyBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showAddSeriesClassify(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void editClassifyName(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.editClassifyName(map, new ApiCallBack<List<ChooseClassifyBean>>() {
            @Override
            public void onSuccess(List<ChooseClassifyBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showEditClassifyName(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void delSeriseClassify(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.delSeriseClassify(map, new ApiCallBack<List<ChooseClassifyBean>>() {
            @Override
            public void onSuccess(List<ChooseClassifyBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showDelResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
