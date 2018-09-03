package com.gxtc.huchuan.ui.live.intro.introresolve;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IntroResolveContract {

    int RESULTE = 101;
    int REQUEST = 100;

    interface View extends BaseUserView<Presenter>{



        void showSaveSuccess();
        void showUploadingSuccess(String id, String url);
        void showUploadingFailure(String info);
        void showUploadVideoSuccess(String id, String url);
        void showUploadVideoFailure(String info);
    }

    interface Presenter extends BasePresenter{







        //上传图片
        void uploadingFile(String id, String path);

        void uploadingVideo(String id, String path);

    }

}
