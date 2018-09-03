package com.gxtc.huchuan.ui.circle.file.filelist;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.List;

/**
 * Created by Gubr on 2017/6/10.
 */

public interface FileListContract {
    public interface AuditedView extends BaseUiView<Presenter> {
        void setAuditedDatas(Integer start, List<CircleFileBean> datas);
    }

    public interface AuditView extends BaseUiView<Presenter> {
        void setAuditDatas(Integer start, List<CircleFileBean> datas);

        void setAuditFileSuccessful(CircleFileBean bean,Integer audit);

    }


    public interface Presenter extends BasePresenter {
        public void getAuditedDatas(Integer start, Integer pageSize);

        public void getAuditDatas(Integer start, Integer pageSize);


        public Presenter getPresenter();

        void auditFile(CircleFileBean bean, int i);
    }

    public interface Source extends BaseSource {
        public void getFileDatas(String token,Integer groupId, Integer start, Integer pageSize, Integer audit,
                ApiCallBack<List<CircleFileBean>> callBack);

        public void auditFile(String token, String fileId, Integer audit, Integer groupId,
                ApiCallBack<Void> callBack);

        void saveCircleFile(String token, Integer circleId, File file, String fileUrl, Integer type,
                Integer folderId, ApiCallBack<Void> callBack);
    }
}
