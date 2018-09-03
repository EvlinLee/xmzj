package com.gxtc.huchuan.ui.circle.file.folder;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/30.
 *
 */

public class FolderListSource extends BaseRepository implements FolderListContract.Source {


    @Override
    public void destroy() {

    }

    @Override
    public void getData(String token,int grouid, int start,Integer pageise, ApiCallBack<List<FolderBean>> callBack) {
        CircleApi.getInstance().listFolder(token,grouid, start, pageise).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<FolderBean>>>(callBack));

    }

    @Override
    public void createFolder(String token, int grouid, String folderName, String fileid,
            ApiCallBack<FolderBean> callBack) {
        CircleApi.getInstance().createFolder(token, grouid, folderName, fileid).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<FolderBean>>(callBack));

    }


    @Override
    public void listFileByFolder(int groupId,String token ,int folderId, int start, ApiCallBack<List<CircleFileBean>> callBack) {
        CircleApi.getInstance().listFileByFolder(groupId,token,folderId, start, null).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleFileBean>>>(callBack));

    }

    @Override
    public void deleteFolder(String token, int grouid, int folderId, ApiCallBack<Void> callBack) {
        CircleApi.getInstance().deleteFolder(token, grouid, folderId).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));

    }

    @Override
    public void saveFile(String token, int fileId, String fileName, Integer folderId,
            ApiCallBack<Void> callBack) {
        CircleApi.getInstance().update(token, fileId, fileName,folderId).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));

    }
}
