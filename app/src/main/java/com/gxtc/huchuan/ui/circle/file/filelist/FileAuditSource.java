package com.gxtc.huchuan.ui.circle.file.filelist;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.CircleApi;

import java.io.File;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/6/10.
 */

public class FileAuditSource extends BaseRepository implements FileListContract.Source {


    @Override
    public void getFileDatas(String token,Integer groupId, Integer start, Integer pageSize, Integer audit,
            ApiCallBack<List<CircleFileBean>> callBack) {
        Subscription sub =
                CircleApi.getInstance()
                         .getCircleFile(token,pageSize, groupId, start, audit)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<List<CircleFileBean>>>(callBack));
        addSub(sub);

    }

    @Override
    public void auditFile(String token, String fileId, Integer audit, Integer groupId, ApiCallBack<Void> callBack) {
        Subscription subscribe = CircleApi.getInstance().auditFile(token, fileId,
                audit,groupId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));
        addSub(subscribe);
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
