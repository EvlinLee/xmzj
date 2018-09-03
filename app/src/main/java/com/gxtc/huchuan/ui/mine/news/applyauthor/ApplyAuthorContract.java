package com.gxtc.huchuan.ui.mine.news.applyauthor;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ApplyInfoBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Describe:申请作者
 * Created by ALing on 2017/4/1 .
 */

public class ApplyAuthorContract {
    public interface View extends BaseUserView<ApplyAuthorContract.Presenter> {
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
