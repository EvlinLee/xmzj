package com.gxtc.huchuan.ui.live.apply;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.ApplyLectureRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;

import java.io.File;
import java.util.HashMap;

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
 * Created by ALing on 2017/4/5 .
 */

public class ApplyLecturePresenter implements ApplyLectureContract.Presenter{
    private ApplyLectureContract.View mView;
    private ApplyLectureContract.Source mData;

    public ApplyLecturePresenter(ApplyLectureContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ApplyLectureRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void applayAuthor(HashMap<String, String> map) {
        mView.showLoad();
        mData.applayAuthor(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showApplayResule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);
    }

    @Override
    public void compressImg(String s) {
        mView.showLoad();

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
                        if(mView == null)   return;
                        mView.showCompressSuccess(file);
                    }

                    //  当压缩过去出现问题时调用
                    @Override
                    public void onError(Throwable e) {
                        if(mView == null)   return;
                        mView.showLoadFinish();
                        mView.showCompressFailure();
                    }
                }).launch();
    }

    @Override
    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showUploadingSuccess(result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ErrorCodeUtil.handleErr(mView,errorCode, msg);
            }
        }, null, file);
    }
}
