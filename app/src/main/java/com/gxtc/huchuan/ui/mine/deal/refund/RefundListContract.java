package com.gxtc.huchuan.ui.mine.deal.refund;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.PurchaseListBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/9.
 */

public interface RefundListContract {

    interface View extends BaseUserView<RefundListContract.Presenter>,BaseListUiView<PurchaseListBean> {
        void showData(List<PurchaseListBean> datas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getData();
    }

}
