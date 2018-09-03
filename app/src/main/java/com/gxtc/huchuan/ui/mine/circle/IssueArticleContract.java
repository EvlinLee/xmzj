package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.ChannelBean;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IssueArticleContract {

    interface View extends BaseUserView<IssueArticleContract.Presenter> {
        void showArticleType(String [] item, List<ChannelBean.NormalBean> beans);
        void showUploadingSuccess(String id, String url);
        void showUploadingFailure(String info);
        void showUploadVideoSuccess(String id, String url);
        void showUploadVideoFailure(String info);
        void showIssueSuccess();
        void showIssueFailure(String info);
    }

    interface Presenter extends BasePresenter{
        //获取文章分类
        void getArticleType();

        //上传图片
        void uploadingFile(String id, String path);

        //上传视频
        void uploadingVideo(String id, String path);

        //发布文章
        void issueArticle(HashMap<String,String> map);

    }
}
