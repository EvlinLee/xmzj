package com.gxtc.huchuan.ui.live.hotlist;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/3/30.
 */

public interface HotListContract {
    public interface View extends BaseUiView<Presenter> {
        void showData(List<UnifyClassBean> datas);

        void showLoMore(List<UnifyClassBean> datas);

        void loadFinish();
    }

    public interface Presenter extends BasePresenter {


        void getData(boolean isloadmroe, int start, String type);
    }

    public interface Source extends BaseSource {
        void getData(HashMap<String,String> map,ApiCallBack<List<UnifyClassBean>> callBack);
        void getHotData(HashMap<String,String> map,ApiCallBack<List<ClassHotBean>> callBack);
        void getboutiqueData(HashMap<String,String> map,ApiCallBack<List<ClassLike>> callBack);

    }
}
