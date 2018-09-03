package com.gxtc.huchuan.ui.pay;

import android.app.Activity;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.pay.OrderRePayBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.bean.pay.OrdersResultBean;
import com.gxtc.huchuan.http.ApiCallBack;


/**
 * Created by Steven on 16/12/4.
 */

public class PayConstant {

    public static final String PAY_TYPE_WX = "WX";
    public static final String PAY_TYPE_ALIPAY = "ALIPAY";
    public static final String PAY_TYPE_BALANCE = "BALANCE";

    public static final int PAY_RESULT = 168;

    public static final int ERR_CODE_PAY_FINISH = 0;          //支付完成
    public static final int ERR_CODE_PAY_FAILURE = -1;        //支付失败
    public static final int ERR_CODE_PAY_CANCEL = -2;         //取消支付

    public interface View extends BaseUserView<Presenter> {
        void showPaySuccecc(int code,String orderId);
        void showPayCallBackFinish();
        void showPayFailure();
        void showPayCallBackCancel();

    }

    public interface Presenter extends BasePresenter {
        void getOrders(Activity activity, String payType, OrdersRequestBean requestBean);
        void getRePayOrder(Activity activity, String payType, OrderRePayBean rePayBean);
        void aliPay(Activity activity, OrdersResultBean resultBean);
        void wxPay(Activity activity, OrdersResultBean resultBean);
        //验证订单是否成功
        void isPaySuccessful(ApiCallBack<Void> beanApiCallBack);
        //计算余额
        void calculationBalance(String total);
    }
}
