package com.gxtc.huchuan.ui.live.apply;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/4/5 .
 */

public class ApplyLectureContract {
    public interface View extends BaseUserView<ApplyLectureContract.Presenter> {
        void showApplayResule(Object object);
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showUploadingSuccess(String url);
    }

    public interface Presenter extends BasePresenter {
        void applayAuthor(HashMap<String,String> map);
        //压缩图片
        void compressImg(String s);

        //上传图片
        void uploadingFile(File file);
    }

    public interface Source extends BaseSource {

        void applayAuthor(ApiCallBack<Object> callBack, HashMap<String,String> map);

    }
}
