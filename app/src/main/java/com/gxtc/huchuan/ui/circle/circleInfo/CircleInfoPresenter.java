package com.gxtc.huchuan.ui.circle.circleInfo;

import android.util.Log;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.FileUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.UploadResult;
import com.gxtc.huchuan.data.CircleInfoRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.LoadHelper;
import com.gxtc.huchuan.http.service.MineApi;

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

import static com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity.maxLen_500k;

/**
 * Describe:
 * Created by ALing on 2017/5/8 .
 */

public class CircleInfoPresenter implements CircleInfoContract.Presenter {
    private CircleInfoContract.Source mData;
    private CircleInfoContract.View mView;

    private int start = 0;
    private String onlyLook = "";

    public CircleInfoPresenter(CircleInfoContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new CircleInfoRepository();
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
    public void getMemberList(int groupId, int type, final boolean isRefresh,String onlyLook) {
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }
        this.onlyLook = onlyLook;
        mData.getMemberList(groupId, type, start, onlyLook,new ApiCallBack<List<CircleMemberBean>>() {

            @Override
            public void onSuccess(List<CircleMemberBean> data) {
                if (mView == null) return;

                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showMemberList(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void loadMore(int groupId, int type) {
        start += 15;

        mData.getMemberList(groupId, type, start,onlyLook, new ApiCallBack<List<CircleMemberBean>>() {

            @Override
            public void onSuccess(List<CircleMemberBean> data) {
                if (mView == null) return;

                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void compressImg(String s) {
        mView.showLoad();
        LogUtil.i("原图路径： " + s);
        //将图片进行压缩
        final File file = new File(s);
        Subscription sub = Luban.get(MyApplication.getInstance()).load(file).putGear(Luban.THIRD_GEAR).asObservable().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<File>(new ApiCallBack<File>() {
                    @Override
                    public void onSuccess(File compressFile) {
                        if (FileUtil.getSize(file) > maxLen_500k) {
                            mView.showCompressSuccess(compressFile);
                        } else {
                            mView.showCompressSuccess(file);
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
                        mView.showLoadFinish();
                        mView.showCompressFailure();
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    @Override
    public void uploadingFile(File file) {
        LoadHelper.uploadFile(LoadHelper.UP_TYPE_IMAGE, new LoadHelper.UploadCallback() {
            @Override
            public void onUploadSuccess(UploadResult result) {
                if (mView == null) return;
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
    public void getCircleInfo(String token, int groupId) {
        mView.showLoad();
        mData.getCircleInfo(token, groupId, new ApiCallBack<CircleBean>() {
            @Override
            public void onSuccess(CircleBean data) {
                if (mView == null) return;
                mView.showLoadFinish();
                mView.showCircleInfo(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void editCircleInfo(HashMap<String, Object> map) {
        mView.showLoad();
        mData.editCircleInfo(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if (mView == null) return;
                mView.showLoadFinish();
                mView.showEditCircle(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void removeMember(String token, final CircleMemberBean circleMemberBean) {
        mData.removeMember(token, circleMemberBean.getGroupId(), circleMemberBean.getUserCode(),
                new ApiCallBack<Void>() {

                    @Override
                    public void onSuccess(Void data) {
                        if (mView == null) return;
                        mView.removeMember(circleMemberBean);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
                        mView.showError(message);
                    }
                });
    }

    @Override
    public void transCircle(String token, final CircleMemberBean circleMemberBean) {
        mData.transCircle(token, circleMemberBean.getGroupId(), circleMemberBean.getUserCode(),
                new ApiCallBack<Void>() {

                    @Override
                    public void onSuccess(Void data) {
                        if (mView == null) return;
                        mView.transCircle(circleMemberBean);

                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
                        mView.showError(message);
                    }
                });

    }


    @Override
    public void changeMemberTpye(String token, final CircleMemberBean circleMemberBean, int type) {
        mData.changeMemberTpye(token, circleMemberBean.getGroupId(), circleMemberBean.getUserCode(),
                type, new ApiCallBack<Void>() {

                    @Override
                    public void onSuccess(Void data) {
                        if (mView == null) return;
                        mView.showChangeMemberTpye(circleMemberBean);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (mView == null) return;
                        mView.showError(message);
                    }
                });
    }
}
