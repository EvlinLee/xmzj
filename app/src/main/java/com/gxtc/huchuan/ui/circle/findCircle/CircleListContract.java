package com.gxtc.huchuan.ui.circle.findCircle;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.CircleBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/5.
 */

public interface CircleListContract {

    interface View extends BaseUiView<CircleListContract.Presenter>,BaseListUiView<CircleBean>{
        void showData(List<CircleBean> datas);
        void showListType(List<CircleBean> datas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData(String k,String isfree,String orderType,Integer typeId);
        void getListType();
    }

}
