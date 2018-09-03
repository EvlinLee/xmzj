package com.gxtc.huchuan.bean.pay;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 支付bean
 */
public class PayBean {


   public boolean isPaySucc;//是否支付成功 true 成功 false失败
   public String OrderNo;

    public PayBean(boolean isPaySucc) {
        this.isPaySucc = isPaySucc;
    }

    public PayBean(boolean isPaySucc,String OrderNo) {
        this.isPaySucc = isPaySucc;
        this.OrderNo = OrderNo;
    }
}
