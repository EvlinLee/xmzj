package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleSignBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/22.
 */

public interface CircleStatisticContract {

    interface View extends BaseUserView<CircleStatisticContract.Presenter>,BaseListUiView<CircleSignBean>{
        void showData(CircleBean chartData,List<CircleSignBean> usersDatas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData(int id);
        void getActiveData(int id, int start);
    }

}
