package com.gxtc.huchuan.ui.circle.dynamic;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.CircleHomeBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/6.
 */

public interface DynamicContract {

    interface View extends BaseUiView<DynamicContract.Presenter>,BaseListUiView<CircleHomeBean>{
        void showData(List<CircleHomeBean> datas);
        void showDZSuccess(int id);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData(int id);
        void dianZan(int id);
    }

}
