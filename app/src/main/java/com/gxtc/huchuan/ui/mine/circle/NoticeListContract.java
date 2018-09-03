package com.gxtc.huchuan.ui.mine.circle;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.CircleBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/24.
 */

public interface NoticeListContract {

    interface View extends BaseUiView<NoticeListContract.Presenter>,BaseListUiView<CircleBean>{
        void showData(List<CircleBean> datas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData(int id);
    }
}
