package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CircleInfoBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/5/10 .
 */

public class EditCircleInfoContract {
    interface View extends BaseUserView<EditCircleInfoContract.Presenter> {
        void showGroupDesc(CircleInfoBean bean);
        void showSaveGroupDesc(Object o);
        void showUploadingSuccess(String id, String url);
        void showUploadingFailure(String info);
        void showUploadVideoSuccess(String id, String url);
        void showUploadVideoFailure(String info);
    }

    interface Presenter extends BasePresenter {
        void getGroupDesc(int groupId);
        void saveGroupDesc(HashMap<String,Object> map);
        void uploadingFile(String id, String path);
        void uploadingVideo(String id, String path);

    }

    public interface Source extends BaseSource {
        void getGroupDesc(int groupId ,ApiCallBack<CircleInfoBean> callBack);
        void saveGroupDesc(HashMap<String,Object> map, ApiCallBack<Object> callBack);
    }

}
