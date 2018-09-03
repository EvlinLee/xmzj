package com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/3/20 .
 */

public class CreateTopicContract {
    public interface View extends BaseUserView<CreateTopicContract.Presenter> {
        void createLiveResult(CreateLiveTopicBean bean);
    }

    public interface Presenter extends BasePresenter {
        void createLiveTopic(HashMap<String,String> map);
    }

    public interface Source extends BaseSource {
        void createLiveTopic(HashMap<String,String> map, ApiCallBack<CreateLiveTopicBean> callBack);
    }
}
