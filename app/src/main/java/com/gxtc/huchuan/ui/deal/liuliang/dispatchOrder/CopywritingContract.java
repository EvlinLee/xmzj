package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.CopywritingBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.List;

/**
 * Created by Steven on 17/3/2.
 */

public interface CopywritingContract {

    interface View extends BaseUserView<CopywritingContract.Presenter>{
        void showData(List<CopywritingBean> datas);
    }

    interface Presenter extends BasePresenter{
        void getData();
    }

    interface Source extends BaseSource{
        void getData(ApiCallBack<List<CopywritingBean>> callBack);
    }

}
