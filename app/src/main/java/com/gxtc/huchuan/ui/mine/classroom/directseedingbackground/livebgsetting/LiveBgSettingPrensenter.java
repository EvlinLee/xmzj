package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting;

import android.util.Log;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.LiveBgSettingRepository;
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
 * Created by ALing on 2017/3/21 .
 */

public class LiveBgSettingPrensenter implements LiveBgSettingContract.Presenter {

    private LiveBgSettingContract.View mView;
    private LiveBgSettingContract.Source mData;

    public LiveBgSettingPrensenter(LiveBgSettingContract.View mView){
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new LiveBgSettingRepository();
    }
    @Override
    public void getLiveManageData(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.getLiveManageData(map, new ApiCallBack<LiveRoomBean>() {
            @Override
            public void onSuccess(LiveRoomBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showLiveManageData(data);
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
    public void getPicList(HashMap<String, String> map) {
        mView.showLoad();
        mData.getPicList(new ApiCallBack<List<BgPicBean>>() {

            @Override
            public void onSuccess(List<BgPicBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showPicList(data);
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
    public void loadMrore() {

    }

    @Override
    public void compressImg(String s) {
        mView.showLoad();
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
                        mView.showCompressSuccess(file);
                    }

                    //  当压缩过去出现问题时调用
                    @Override
                    public void onError(Throwable e) {
                        if(mView == null) return;
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

    @Override
    public void saveChatRoomSetting(HashMap<String, String> map) {
        mView.showLoad();
        mData.saveChatRoomSetting(new ApiCallBack<LiveBgSettingBean>() {
            @Override
            public void onSuccess(LiveBgSettingBean data) {
                Log.d("LiveBgSettingPrensenter", "data:" + data);
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showChatRoomSetting(data);
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
    public void getManagerList(HashMap<String, String> map) {
        mView.showLoad();
        mData.getManagerList(new ApiCallBack<LiveManagerBean>() {
            @Override
            public void onSuccess(LiveManagerBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.getDatas().size() == 0){
                    mView.showNoMore();
                    return;
                }
                mView.showManagerList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        },map);
    }

    @Override
    public void loadMoreManagers(HashMap<String, String> map) {
        getManagerList(map);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
       mData.destroy();
        mView = null;
    }
}
