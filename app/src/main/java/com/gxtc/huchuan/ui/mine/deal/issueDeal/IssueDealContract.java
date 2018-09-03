package com.gxtc.huchuan.ui.mine.deal.issueDeal;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.AllTypeBaen;

import java.util.List;
import java.util.Map;


public interface IssueDealContract {

    interface View extends BaseUserView<Presenter>{
        void showType(List<AllTypeBaen> datas);
        void showUploadingSuccess(String id, String url);
        void showUploadingFailure(String info);
        void showUploadVideoSuccess(String id, String url);
        void showUploadVideoFailure(String info);
        void showIssueSuccess();
        void showIssueFailure(String info);


    }

    interface Presenter extends BasePresenter{
        void getType();

        //上传图片
        void uploadingFile(String id, String path);

        void uploadingVideo(String id, String path);

        //发布交易
        void issueDeal(Map<String, String> map);

    }

}
