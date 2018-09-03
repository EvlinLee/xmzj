package com.gxtc.huchuan.ui.mine.editorarticle;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public interface ArticleResolveContract {

    int RESULTE = 101;
    int REQUEST = 100;

    interface View extends BaseUserView<ArticleResolveContract.Presenter>{
        void showHtmlContent(String url, String title, String des, String imgUrl,String content);
        void showArticleType(String [] item, List<ChannelBean.NormalBean> beans);
        void showHtmlError();
        void showSaveSuccess();
        void showUploadingSuccess(String id, String url);
        void showUploadingFailure(String info);
        void showUploadVideoSuccess(String id, String url);
        void showUploadVideoFailure(String info);
    }

    interface Presenter extends BasePresenter{
        //获取文章分类
        void getArticleType();

        //抓取网页内容
        void getHtmlContent(String url);

        //保存文章
        void saveArticle(HashMap<String,String> map);

        //上传图片
        void uploadingFile(String id, String path);

        void uploadingVideo(String id, String path);

    }

    interface Source extends BaseSource{
        void getArticleType(ApiCallBack<ChannelBean> callBack);
        void saveArticle(HashMap<String,String> map , ApiCallBack<Object> callBack);
    }
}
