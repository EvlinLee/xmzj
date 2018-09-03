package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.seriesclassify;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/29 .
 */

public class SeriesClassifyContract {
    public interface View extends BaseUserView<SeriesClassifyContract.Presenter> {
        void showChatSeriesTypeList(List<ChooseClassifyBean> bean);
        void showAddSeriesClassify(List<ChooseClassifyBean> bean);
        void showEditClassifyName(List<ChooseClassifyBean> bean);
        void showDelResult(List<ChooseClassifyBean> bean);
    }

    public interface Presenter extends BasePresenter {
        void getChatSeriesTypeList(HashMap<String,String> map);
        void addSeriesClassify(HashMap<String,String> map);
        void editClassifyName(HashMap<String,String> map);
        void delSeriseClassify(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {

        void getChatSeriesTypeList(HashMap<String,String> map, ApiCallBack<List<ChooseClassifyBean>> callBack);

        void addSeriesClassify(HashMap<String,String> map,ApiCallBack<List<ChooseClassifyBean>> callBack);

        //修改系列课名称
        void editClassifyName(HashMap<String,String> map ,ApiCallBack<List<ChooseClassifyBean>> callBack);

        //删除系列课

        void delSeriseClassify(HashMap<String,String> map, ApiCallBack<List<ChooseClassifyBean>> callBack);
    }
}
