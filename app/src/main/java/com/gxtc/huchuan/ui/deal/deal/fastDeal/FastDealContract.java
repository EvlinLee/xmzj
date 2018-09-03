package com.gxtc.huchuan.ui.deal.deal.fastDeal;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.FastDealBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Steven on 17/4/10.
 */

public interface FastDealContract {

    String INTENT_ISDEFULT = "isDefult";

    interface View extends BaseUserView<FastDealContract.Presenter>{
        void showAddress(List<AddressBean> beans);
        void showOrderData(FastDealBean bean);
        void showSubmitSuccess(Integer id);
        void showSubmitFailed(String info);
    }

    interface Presenter extends BasePresenter{
        void getAddressList(String token);
        void getOrderData(String token ,int id);
        void submitOrder(HashMap<String,String> map);
        double computeTotal(double money,double dbFree,double discount,boolean isAdd);
    }

}
