package com.gxtc.huchuan.data.deal;

import android.util.Log;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.circle.file.CircleFileContract;

import java.io.File;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 圈子文件
 */

public class CircleFileRepository extends BaseRepository implements CircleFileContract.Source {


    @Override
    public void getData(String token ,int groudId, final int start, final ApiCallBack<List<CircleFileBean>> callBack) {
        Subscription sub =
                CircleApi.getInstance()
                         .getCircleFile(token,15, groudId, start,null)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<List<CircleFileBean>>>(callBack));
        addSub(sub);
    }

    @Override
    public void deleteCircleFile(String token, int groudId, final String fileId,
            final ApiCallBack<Void> callBack) {
        Log.d("CircleFileRepository", token + " " + groudId + " " + fileId);
        Subscription sub = CircleApi.getInstance().deleteCircleFile(token, groudId,
                fileId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));
        addSub(sub);
    }

    @Override
    public void getFolderFile(int groupId,String token ,int folderId, int start, ApiCallBack<List<CircleFileBean>> callBack) {
        CircleApi.getInstance().listFileByFolder(groupId,token,folderId, start, null).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleFileBean>>>(callBack));

    }

    @Override
    public void queryFile(String token ,String keyWord, Integer groupId, Integer folderId, Integer start,
            Integer pageSize, ApiCallBack<List<CircleFileBean>> callBack) {
        CircleApi.getInstance().fileQuery(token,keyWord, groupId, folderId, start, pageSize).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<CircleFileBean>>>(callBack));
    }

    @Override
    public void saveCircleFile(String token, Integer circleId, File file, String fileUrl,
            Integer type, Integer folderId, ApiCallBack<Void> callBack) {
        AllApi.getInstance().saveCircleFile(circleId, token, fileUrl, file.getName(), type,
                folderId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));
    }


}
