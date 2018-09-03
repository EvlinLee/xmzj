package com.gxtc.huchuan.ui.mine.setting;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/4/7 .
 */

public class FeedBackContract {
    public interface View extends BaseUiView<FeedBackContract.Presenter> {
        void showFeedBackResult(Object object);
    }

    //prenster接口
    public interface Presenter extends BasePresenter {
        void feeBack(HashMap<String,String> map);
    }

    //model层接口
    public interface Source extends BaseSource {
        void feedBack(HashMap<String,String> map,ApiCallBack<Object> callBack);

    }
}
