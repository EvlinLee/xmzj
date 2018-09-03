package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/6/13.
 */

public class ClassRoomAuditSource extends BaseRepository implements ClassRoomAuditContract.Source {



    @Override
    public void getDatas(String userCode, int start, Integer pageSize, Integer groupId,
            ApiCallBack<List<ChatInfosBean>> apiCallBack) {
        Subscription sub = CircleApi.getInstance().getUnauditChatInfoList(userCode, start,
                pageSize, groupId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(apiCallBack));
        addSub(sub);

    }

    @Override
    public void auditClassRoom(String linkId, String token, Integer audit, Integer type, Integer groupId,
            ApiCallBack<Void> callBack) {
        Subscription sub = CircleApi.getInstance().auditNC(token, linkId,
                audit, type,groupId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));
        addSub(sub);
    }

    @Override
    public void auditSerise(String token, String chatId, Integer groupId, Integer auditType,
                            String chatType, ApiCallBack<Void> callBack) {
        Subscription sub = CircleApi.getInstance().auditChatOfGroup(token, chatId,
                auditType, chatType,groupId).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack));
        addSub(sub);
    }
}
