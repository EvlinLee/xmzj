package com.gxtc.huchuan.ui.mine.news.applyauthor;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.ApplyAuthorRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.live.intro.LiveIntroSettingActivity;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

/**
 * Describe:
 * Created by ALing on 2017/4/1.
 */

public class ApplyAuthorPresenter implements ApplyAuthorContract.Presenter{
    private ApplyAuthorContract.View mView;
    private ApplyAuthorContract.Source mData;

    public ApplyAuthorPresenter(ApplyAuthorContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new ApplyAuthorRepository();
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
    public void applayAuthor(HashMap<String, String> map) {
        mView.showLoad();
        mData.applayAuthor(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showApplayResule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);
    }

    @Override
    public void compressImg(String s) {
        mView.showLoad();
        LogUtil.i("原图路径： " + s);

        //将图片进行压缩
        final File file = new File(s);
        Luban.get(MyApplication.getInstance().getApplicationContext()).load(file) .putGear
                (Luban.THIRD_GEAR).launch()
             .asObservable().subscribeOn(Schedulers.newThread()).
                     observeOn(AndroidSchedulers.mainThread()).subscribe(new ApiObserver<File>(new ApiCallBack<File>() {
            @Override
            public void onSuccess(File compressFile) {
                if(FileUtil.getSize(file) > maxLen_500k ){
                    if(mView == null) return;
                    mView.showCompressSuccess(compressFile);
                }else {
                    mView.showCompressSuccess(file);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                 mView.showLoadFinish();
                 mView.showCompressFailure();
            }
        }));

    }

    @Override
    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showUploadingSuccess(result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ErrorCodeUtil.handleErr(mView, errorCode, msg);
            }
        }, null, file);
    }
}
