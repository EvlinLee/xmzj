package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createseriescourse;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/3/23 .
 */

public class CreateSeriesCourseContract {
    public interface View extends BaseUserView<CreateSeriesCourseContract.Presenter> {
        void createLiveSeriesResult(SeriesPageBean bean);
        void showChatSeriesTypeList(List<ChooseClassifyBean> bean);
        void showAddSeriesClassify(List<ChooseClassifyBean> bean);
        void showCompressSuccess(File file);
        void showCompressFailure();
        void showUploadingSuccess(String url);
        void showDelSeries(Object object);
    }

    public interface Presenter extends BasePresenter {
        void createLiveSeries(HashMap<String,String> map);
        void getChatSeriesTypeList(HashMap<String,String> map);
        void addSeriesClassify(HashMap<String,String> map);
        //压缩图片
        void compressImg(String s);

        //上传图片
        void uploadingFile(File file);

        void delSeries(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {

        void createLiveSeries(HashMap<String,String> map, ApiCallBack<SeriesPageBean> callBack);

        void getChatSeriesTypeList(HashMap<String,String> map, ApiCallBack<List<ChooseClassifyBean>> callBack);

        void addSeriesClassify(HashMap<String,String> map,ApiCallBack<List<ChooseClassifyBean>> callBack);

        void delSeries(HashMap<String,String> map,ApiCallBack<Object> callBack);
    }
}
