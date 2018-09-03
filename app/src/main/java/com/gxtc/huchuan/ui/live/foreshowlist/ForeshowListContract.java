package com.gxtc.huchuan.ui.live.foreshowlist;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 */

public interface ForeshowListContract {
    public interface View extends BaseUiView<Presenter> {
        void showData(List<ChatInfosBean> datas);

        void showLoMore(List<ChatInfosBean> datas);

        void loadFinish();
    }

    public interface Presenter extends BasePresenter {


        void getData(boolean isloadmroe, int start);
    }

    public interface Source extends BaseSource {
        void getData(HashMap<String, String> map, ApiCallBack<ArrayList<ChatInfosBean>> callBack);
    }
}
