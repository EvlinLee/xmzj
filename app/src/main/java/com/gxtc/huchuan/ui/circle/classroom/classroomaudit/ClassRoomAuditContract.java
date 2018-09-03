package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/6/13.
 */

public interface ClassRoomAuditContract {
    public interface View extends BaseUiView<Presenter> {
        public void showDatas(int start, List<ChatInfosBean> datas);

        public void shwoLoadMoreDatas(int start, List<ChatInfosBean> datas);

        public void auditSuccessful(String linkid);

        public void auditSeriseSuccessful(Object object);

        public void showAuditClassRoom( List<ClassAndSeriseBean> datas);
    }

    public interface Presenter extends BasePresenter {
        public void getDatas(String userCode, int start, Integer pageSize, Integer groupId);

        public void auditClassRoom(String token, String linkId, Integer audit, Integer groupId);

        public void auditSerise(String token, String chatId, Integer groupId, Integer auditType,String chatType);

        public void getAuditClassRoom(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {
        public void getDatas(String userCode, int start, Integer pageSize, Integer groupId,
                ApiCallBack<List<ChatInfosBean>> apiCallBack);

        public void auditClassRoom(String linkId, String token, Integer audit, Integer type, Integer groupId,
                ApiCallBack<Void> callBack);

        public void auditSerise(String token, String chatId, Integer groupId, Integer auditType,String chatType,
                ApiCallBack<Void> callBack);
    }
}
