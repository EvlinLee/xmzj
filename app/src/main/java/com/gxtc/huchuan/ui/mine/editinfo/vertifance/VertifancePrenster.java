package com.gxtc.huchuan.ui.mine.editinfo.vertifance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileStorage;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.bean.pay.AccountBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.VertifanceRepository;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.live.intro.LiveIntroSettingActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.forward.androids.utils.ImageUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

/**
 * Created by zxz on 2017/6/26
 */

public class VertifancePrenster implements VertifanceContract.Presenter{
    private VertifanceContract.View mView;
    private VertifanceContract.Source mData;

    public VertifancePrenster(VertifanceContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new VertifanceRepository();
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
    public void Vertifance(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.Vertifance(new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showVertifanceResule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);

    }

    @Override
    public void compressImg(String s) {
        if(mView == null) return;
        mView.showLoad();
        //将图片进行压缩
       final File file = new File(s);
        Subscription sub = Luban.get(MyApplication.getInstance().getApplicationContext())
                                .load(file)
                                .putGear(Luban.THIRD_GEAR)
                                .asObservable()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<File>() {
                                    @Override
                                    public void call(File compressFile) {
                                        if(FileUtil.getSize(file) > maxLen_500k ){
                                            mView.showCompressSuccess(compressFile);
                                        }else {
                                            mView.showCompressSuccess(file);
                                        }
                                    }
                                });
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    public void uploadingFile(File file) {
        LogUtil.i(file.getAbsolutePath());

        final File watermarkFile = watermarkFile(file);
        String     token         = UserManager.getInstance().getToken();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token",token)
                .addFormDataPart("file",watermarkFile.getName(),RequestBody.create(MediaType.parse("image*//**//*"),watermarkFile))
                .build();

        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showUploadingSuccess(result.getUrl());
                deletefile(watermarkFile);
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                ErrorCodeUtil.handleErr(mView,errorCode, msg);
            }
        }, null, file);
    }

    private void deletefile(File file){
        Subscription sub =
            Observable.just(file)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        FileUtil.deleteFile(file.getPath());
                    }
                });

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    //给照片打水印
    private File watermarkFile(File file) {
        Bitmap srcBitmap = ImageUtils.createBitmapFromPath(file.getPath(), MyApplication.getInstance());
        Bitmap watermarkBitmap = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.logo_watermark);

        //按比例缩放水印
        float scale = watermarkBitmap.getWidth() / watermarkBitmap.getHeight();
        int width = (int) (srcBitmap.getWidth() * 0.5);
        int height = (int) (width / scale);
        Bitmap scaleWatermark = null;
        scaleWatermark = com.gxtc.huchuan.utils.ImageUtils.zoomBitmap(watermarkBitmap, width, height);
        Bitmap targetBitmap = com.gxtc.huchuan.utils.ImageUtils.createWaterMaskCenter(srcBitmap, scaleWatermark);

        if(!srcBitmap.isRecycled()){
            srcBitmap.recycle();
        }
        if(!scaleWatermark.isRecycled()){
            scaleWatermark.recycle();
        }
        if(!watermarkBitmap.isRecycled()){
            watermarkBitmap.recycle();
        }
        mView.showWatermarkImage(targetBitmap);
        String filePath = FileStorage.getImgCacheFile().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpeg";
        com.gxtc.huchuan.utils.ImageUtils.saveImage(MyApplication.getInstance(), filePath, targetBitmap);
        return new File(filePath);
    }

    @Override
    public void showVertifanceCard(HashMap<String,String> map) {
        mView.showLoad();
        mData.VertifanceAccoun(new ApiCallBack<List<AccountBean>>() {
            @Override
            public void onSuccess(List<AccountBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showVertifanceCardSuccess(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);

    }

    @Override
    public void showVertifanceEpay(HashMap<String,String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.VertifanceAccoun(new ApiCallBack<List<AccountBean>>() {
            @Override
            public void onSuccess(List<AccountBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showVertifanceEpaySuccess(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);

    }
}
