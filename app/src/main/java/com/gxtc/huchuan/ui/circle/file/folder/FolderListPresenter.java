package com.gxtc.huchuan.ui.circle.file.folder;

import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 *
 */

public class FolderListPresenter implements FolderListContract.Presenter {
    private static final String TAG = "FolderListPresenter";

    private FolderListContract.View mView;
    private FolderListSource        source;


    private boolean flag = false;
    private int mStart;

    public FolderListPresenter(FolderListContract.View view) {
        mView = view;
        mView.setPresenter(this);
        source = new FolderListSource();
    }

    @Override
    public void getData(boolean isloadmroe, int grouid, final int start, Integer pagesize) {
        if (flag) {
            return;
        }
        if (start == 0) mView.showLoad();
        mStart = start;
        flag = true;
        String token = UserManager.getInstance().getToken();
        source.getData(token,grouid, start, pagesize, new ApiCallBack<List<FolderBean>>() {
            @Override
            public void onSuccess(List<FolderBean> data) {
                if(mView == null) return;

                flag = false;
                if (mStart == 0) {
                    mView.showFolderData(data);
                    mView.showLoadFinish();
                } else {
                    if (data.size() == 0) {
                        mView.loadFinish();
                    } else {
                        mView.showLoMore(data);
                    }

                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                flag = false;
                if (mStart == 0) {
                    mView.showLoadFinish();
                }
            }
        });
    }

    @Override
    public void createFolder(String token, final int grouid, final String folderName,
            final String fileid) {
        source.createFolder(token, grouid, folderName, fileid, new ApiCallBack<FolderBean>() {
            @Override
            public void onSuccess(FolderBean data) {
                if(mView == null) return;
                if (fileid != null) {
                    mView.createFolder(grouid, folderName, fileid);
                } else {
                    mView.createFolder(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        });
    }

    @Override
    public void listFileByFolder(int folderId, int start) {

    }

    @Override
    public void deleteFolder(String token, final int grouid, final int folderId) {
        source.deleteFolder(token, grouid, folderId, new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null) return;
                mView.deleteFolder(grouid, folderId);
            }

            @Override
            public void onError(String errorCode, String message) {

            }
        });
    }

    public void saveFile(String token, final int fileId, final String fileName, final Integer folderId) {
        source.saveFile(token, fileId, fileName,folderId , new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null) return;
                mView.saveFolder(fileId, fileName,folderId);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                mView.showError(message);
            }
        } );
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        source.destroy();
        mView = null;
    }
}
