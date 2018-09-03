package com.gxtc.huchuan.ui.special;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseView;
import com.gxtc.huchuan.data.ArticleSpecialBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 来自 苏修伟 on 2018/5/12.
 */
public class ArticleSpecialContract {

    public interface Presenter extends BasePresenter{

        void getArticleSpeialList(boolean Refresh, Map<String, String> map);

    }

    public interface View extends BaseUiView<Presenter> {

        void showData(List<ArticleSpecialBean> datas);

    }
}
