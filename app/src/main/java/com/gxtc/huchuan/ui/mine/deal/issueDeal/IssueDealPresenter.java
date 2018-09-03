package com.gxtc.huchuan.ui.mine.deal.issueDeal;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.AllTypeBaen;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

public class IssueDealPresenter implements IssueDealContract.Presenter {

    private IssueDealContract.View mView;
    private DealSource mData;

    public IssueDealPresenter(IssueDealContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void start() {

    }


    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        if (uploadSub != null) uploadSub.unsubscribe();
    }

    @Override
    public void getType() {
        if (mView == null) return;
        mView.showLoad();
        mData.getAllType(new ApiCallBack<List<AllTypeBaen>>() {
            @Override
            public void onSuccess(List<AllTypeBaen> data) {
                if (mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }
                mView.showType(data);
            }

            @Override
            public void onError(String errorCode, String message) {

                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }


    private Subscription uploadSub;

    @Override
    public void uploadingFile(final String id, String path) {
        LogUtil.i("原图路径： " + path);
        LogUtil.i("img id ： " + id);

        //将图片进行压缩
        final File srcFile = new File(path);
        uploadSub = Luban.get(MyApplication.getInstance()).load(srcFile)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .asObservable()
                .subscribeOn(Schedulers.io())
                .map(new Func1<File, File>() {
                    @Override
                    public File call(File compressFile) {
                        if (FileUtil.getSize(srcFile) > maxLen_500k) {
                            return compressFile;
                        } else {
                            return srcFile;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showUploadingFailure("上传图片失败");
                    }

                    @Override
                    public void onNext(File file) {
                        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                            @Override
                            public void onUploadSuccess(UploadResult result) {
                                if (mView == null) return;
                                mView.showUploadingSuccess(id, result.getUrl());
                            }

                            @Override
                            public void onUploadFailed(String errorCode, String msg) {
                                ErrorCodeUtil.handleErr(mView, errorCode, msg);
                            }
                        }, null, file);
                    }
                });
    }


    @Override
    public void uploadingVideo(final String id, String path) {
        File file = new File(path);
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_VIDEO, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if (mView == null) return;
                mView.showUploadVideoSuccess(id, result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                if (mView != null) {
                    mView.showUploadVideoFailure(msg);
                }
            }
        }, new UIProgressListener() {
            @Override
            public void onUIProgress(long currentBytes, long contentLength, boolean done) {
                LogUtil.i("bytesWrite :  " + currentBytes + "        contentLength : " + contentLength);
            }
        }, file);
    }

    @Override
    public void issueDeal(Map<String, String> map) {
        if (mView == null) return;
        mView.showLoad();
        mData.issueDeal(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if (mView == null) return;
                mView.showLoadFinish();
                mView.showIssueSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                if (mView == null) return;
                if (errorCode.equals("10256")) {
                    mView.showLoadFinish();
                    mView.showIssueSuccess();
                } else {
                    mView.showLoadFinish();
                    mView.showIssueFailure(message);
                }
            }
        });
    }

}
