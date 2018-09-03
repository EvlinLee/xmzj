package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
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
import java.util.ArrayList;
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

public class IssueArticlePresenter implements IssueArticleContract.Presenter {

    private IssueArticleContract.View mView;
    private CircleSource              mData;

    public IssueArticlePresenter(IssueArticleContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CircleRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        if(uploadSub != null) uploadSub.unsubscribe();
    }

    @Override
    public void getArticleType() {
        mView.showLoad();
        mData.getArticleType(new ApiCallBack<ChannelBean>() {

            @Override
            public void onSuccess(ChannelBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                data.getDefaultX().remove(0);
                String[]                     items = new String[data.getDefaultX().size() + data.getNormal().size()];
                List<ChannelBean.NormalBean> beans = new ArrayList<ChannelBean.NormalBean>();

                int k = 0;
                for (int i = 0; i < data.getDefaultX().size(); i++) {
                    ChannelBean.DefaultBean bean = data.getDefaultX().get(i);
                    items[i] = bean.getNewstypeName();
                    beans.add(new ChannelBean.NormalBean(bean.getNewstypeId(),
                            bean.getNewstypeName()));
                    k++;
                }
                for (int i = 0; i < data.getNormal().size(); i++) {
                    ChannelBean.NormalBean bean = data.getNormal().get(i);
                    items[k] = bean.getNewstypeName();
                    beans.add(new ChannelBean.NormalBean(bean.getNewstypeId(),
                            bean.getNewstypeName()));
                    k++;
                }
                mView.showArticleType(items, beans);
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
        final File file = new File(path);
        uploadSub = Luban.get(MyApplication.getInstance()).load(file)                     //传人要压缩的图片
                         .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                         .asObservable()
                         .subscribeOn(Schedulers.io())
                         .map(new Func1<File, File>() {
                             @Override
                             public File call(File compressFile) {
                                 if(FileUtil.getSize(file) > maxLen_500k ){
                                     return compressFile;
                                 }else {
                                     return file;
                                 }
                             }
                         })
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new Subscriber<File>() {
                             @Override
                             public void onCompleted() {}

                             @Override
                             public void onError(Throwable e) {
                                 mView.showUploadingFailure("上传图片失败");
                             }

                             @Override
                             public void onNext(File file) {
                                 LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
                                     @Override
                                     public void onUploadSuccess(UploadResult result) {
                                         if(mView == null) return;
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
                if(mView == null) return;
                mView.showUploadVideoSuccess(id, result.getUrl());
            }

            @Override
            public void onUploadFailed(String errorCode, String msg) {
                if(mView != null){
                    mView.showUploadVideoFailure(msg);
                }
            }
        },new UIProgressListener() {
            @Override
            public void onUIProgress(long currentBytes, long contentLength, boolean done) {
                LogUtil.i("bytesWrite :  " + currentBytes + "        contentLength : " + contentLength);
            }
        }, file);
    }

    @Override
    public void issueArticle(HashMap<String, String> map) {
        if(mView == null) return;
        mView.showLoad();
        mData.issueArticle(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showIssueSuccess();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }


}
