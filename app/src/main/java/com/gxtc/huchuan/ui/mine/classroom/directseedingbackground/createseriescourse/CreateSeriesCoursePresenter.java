package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.CreateSeriesCourseRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.live.intro.LiveIntroSettingActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Describe:
 * Created by ALing on 2017/3/23 .
 */

public class CreateSeriesCoursePresenter implements CreateSeriesCourseContract.Presenter{
    private CreateSeriesCourseContract.Source mData;
    private CreateSeriesCourseContract.View mView;

    public CreateSeriesCoursePresenter(CreateSeriesCourseContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CreateSeriesCourseRepository();
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
    public void createLiveSeries(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.createLiveSeries(map, new ApiCallBack<SeriesPageBean>() {
            @Override
            public void onSuccess(SeriesPageBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.createLiveSeriesResult(data);
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
    public void compressImg(String s) {
        if(mView == null) return;
        LogUtil.i("原图路径： " + s);

        //将图片进行压缩
        File file = new File(s);
        Luban.get(MyApplication.getInstance())
                .load(file)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() {

                    @Override
                    public void onStart() {}

                    @Override
                    public void onSuccess(File file) {
                        if(mView == null) return;
                        mView.showCompressSuccess(file);
                    }

                    //  当压缩过去出现问题时调用
                    @Override
                    public void onError(Throwable e) {
                        if(mView == null) return;
                        mView.showCompressFailure();
                    }
                }).launch();
    }


    @Override
    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(mView == null) return;
                mView.showUploadingSuccess(result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ErrorCodeUtil.handleErr(mView, errorCode, msg);
            }
        }, null, file);
    }


    @Override
    public void delSeries(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.delSeries(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showDelSeries(data);
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
