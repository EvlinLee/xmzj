package com.gxtc.huchuan.ui.circle.file;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.CircleFileRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.List;

/**
 * Created by Gubr on 2017/5/13.
 *
 */

public class CircleFilePresenter implements CircleFileContract.Presenter {
    private CircleFileContract.Source mData;
    private CircleFileContract.View   mView;
    private int start = 0;
    private int groudId;
    private boolean loadingflag = false;

    public CircleFilePresenter(CircleFileContract.View mView, int groudId) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.groudId = groudId;
        mData = new CircleFileRepository();
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
    public void getData(final boolean isRefresh) {
        if (isRefresh) {
            start = 0;
        } else {
            mView.showLoad();
        }
        String token = UserManager.getInstance().getToken();
        mData.getData(token,groudId, start, new ApiCallBack<List<CircleFileBean>>() {

            @Override
            public void onSuccess(List<CircleFileBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data == null || data.size() == 0) {
                    mView.showEmpty();
                    return;
                }

                if (isRefresh) {
                    mView.showRefreshFinish(data);
                } else {
                    mView.showData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void queryFile(String keyWord, Integer groupId, Integer folderId, final Integer start,
            Integer pageSize) {
        if (loadingflag) {
            return;
        }
        loadingflag = true;
        String token = UserManager.getInstance().getToken();
        mData.queryFile(token,keyWord, groupId, folderId, start, pageSize,
                new ApiCallBack<List<CircleFileBean>>() {
                    @Override
                    public void onSuccess(List<CircleFileBean> data) {
                        if(mView == null) return;
                        if (data.size() > 0) {
                            mView.showQueryFile(start, data);
                        } else {
                            mView.showNoMore();
                        }
                        loadingflag = false;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(mView == null) return;
                        mView.showError(message);
                        loadingflag = false;
                    }
                });
    }

    @Override
    public void getFolderFile(int folderid, final int start) {
        if (loadingflag) {
            return;
        }
        if(mView == null) return;
        mView.showLoad();
        loadingflag = true;
        String token = UserManager.getInstance().getToken();
        mData.getFolderFile(groudId, token,folderid, start, new ApiCallBack<List<CircleFileBean>>() {
            @Override
            public void onSuccess(List<CircleFileBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data.size() > 0) {
                    mView.showFolderFile(start, data);
                } else {
                    mView.showNoMore();
                }
                loadingflag = false;
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showError(message);
                loadingflag = false;
            }
        });
    }

    @Override
    public void refreshData(int folderId) {
        String token = UserManager.getInstance().getToken();
        mData.getFolderFile(groudId,token,folderId, 0, new ApiCallBack<List<CircleFileBean>>() {
            @Override
            public void onSuccess(List<CircleFileBean> data) {
                if(mView == null) return;
                mView.showLoadFinish();
                if (data.size() > 0) {
                    mView.showRefreshFinish(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showError(message);
            }
        });
    }

    @Override
    public void loadMrore() {
        start += 15;
        String token = UserManager.getInstance().getToken();
        mData.getData(token,groudId, start, new ApiCallBack<List<CircleFileBean>>() {

            @Override
            public void onSuccess(List<CircleFileBean> data) {
                if(mView == null) return;
                if (data == null || data.size() == 0) {
                    mView.showNoMore();
                    return;
                }
                mView.showLoadMore(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void deleteCircleFile(String token, final int groudId, final String fileId) {
        mData.deleteCircleFile(token, groudId, fileId, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null) return;
                mView.updateByDeleteCircleFile(groudId, fileId);
            }

            @Override
            public void onError(String errorCode, String message) {

            }
        });
    }

    @Override
    public void saveCircleFile(String token, Integer circleId, File file, String fileUrl,
            Integer type, Integer folderId) {

        mData.saveCircleFile(token, circleId, file, fileUrl, type, folderId, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null) return;
                mView.showSaveCirccleFileResult("保存成功");
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
