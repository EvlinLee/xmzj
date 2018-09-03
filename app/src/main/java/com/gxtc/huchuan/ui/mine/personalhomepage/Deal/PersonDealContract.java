package com.gxtc.huchuan.ui.mine.personalhomepage.Deal;

import com.gxtc.commlibrary.BaseListPresenter;
import com.gxtc.commlibrary.BaseListUiView;
import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.deal.DealContract;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/3.
 */

public interface PersonDealContract {


    interface View extends BaseUiView<PersonDealContract.Presenter>,BaseListUiView<DealListBean>{
        void showData(List<DealListBean> datas);
    }

    interface Presenter extends BasePresenter,BaseListPresenter{
        void getUserDealList(String token, String userCode);
    }


}
