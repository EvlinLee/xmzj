package com.gxtc.huchuan.ui.circle.dynamic;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;

import java.io.File;
import java.util.HashMap;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/8.
 */

public interface IssueDynamicContract {

    interface View extends BaseUserView<IssueDynamicContract.Presenter>{
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showUploadingSuccess(String url);
        void showUploadingFailure(String info);
        void showIssueSuccess();
        void showIssueFailure(String info);
    }

    interface Presenter extends BasePresenter{
        //压缩图片
        void compressImg(String s);

        //上传图片
        void uploadingFile(File file);

        //发布动态
        void issueDynamic(HashMap<String,String> map);
    }

}
