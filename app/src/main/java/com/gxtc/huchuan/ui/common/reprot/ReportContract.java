package com.gxtc.huchuan.ui.common.reprot;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 *
 */

public class ReportContract {
    public interface View extends BaseUiView<ReportContract.Presenter> {
        void showReportResult(Object object);
    }

    //prenster接口
    public interface Presenter extends BasePresenter {


        void report(String content, String type, String id);
    }

    //model层接口
    public interface Source extends BaseSource {


        void report(String content, String type, String id, ApiCallBack<Void> callBack);
    }
}
