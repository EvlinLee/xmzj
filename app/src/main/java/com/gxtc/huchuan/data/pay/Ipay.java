package com.gxtc.huchuan.data.pay;


import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by Steven on 16/12/4.
 */

public interface Ipay extends BaseSource{
    void getOrders(HashMap<String, String> map, ApiCallBack<OrdersResultBean> callBack);
    void getRePayOrder(HashMap<String, String> map, ApiCallBack<OrdersResultBean> callBack);
    void validateOrder(String out_trade_no, ApiCallBack<Void> callBack);
}
