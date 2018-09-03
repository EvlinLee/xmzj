package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomContract;
import com.gxtc.huchuan.ui.circle.classroom.ClassroomRepository;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/6/13.
 */

public class ClassRoomAuditPresenter implements ClassRoomAuditContract.Presenter {

    ClassRoomAuditContract.View   mView;
    ClassRoomAuditContract.Source mSource;
    ClassroomContract.Source data;
    public ClassRoomAuditPresenter(ClassRoomAuditContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mSource = new ClassRoomAuditSource();
        data = new ClassroomRepository();
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mSource.destroy();
        mView = null;
    }

    private boolean loadingFlag = false;

    @Override
    public void getDatas(String userCode, final int start, Integer pageSize, Integer groupId) {
        if (loadingFlag) {
            return;
        }
        loadingFlag = true;
        mSource.getDatas(userCode, start, pageSize, groupId,
                new ApiCallBack<List<ChatInfosBean>>() {
                    @Override
                    public void onSuccess(List<ChatInfosBean> data) {
                        if(mView == null)   return;
                        if (start == 0) {
                            mView.showDatas(0, data);
                        } else {
                            if (start > 0 && data.size() > 0) {

                                mView.shwoLoadMoreDatas(start, data);
                            } else {
                                mView.showLoadFinish();
                            }
                        }

                        loadingFlag = false;
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        loadingFlag = false;
                    }
                });
    }

    @Override
    public void auditClassRoom(String token, final String linkId, Integer audit, Integer groupId) {
        mSource.auditClassRoom(linkId, token, audit, 1,groupId , new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null)   return;
                mView.auditSuccessful(linkId);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void auditSerise(String token, String chatId, Integer groupId, Integer auditType,
                            String chatType) {
        mSource.auditSerise(token, chatId, groupId, auditType,chatType , new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null)   return;
                mView.auditSeriseSuccessful(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

    @Override
    public void getAuditClassRoom(HashMap<String, String> map) {
        data.getUnauditGroup(map, new ApiCallBack<List<ClassAndSeriseBean>>() {
            @Override
            public void onSuccess(List<ClassAndSeriseBean> data) {
                if(mView == null)   return;
                mView.showAuditClassRoom(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }
}
