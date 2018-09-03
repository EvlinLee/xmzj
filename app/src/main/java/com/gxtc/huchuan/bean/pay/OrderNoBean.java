package com.gxtc.huchuan.bean.pay;


/**
 * 订单bean，方便用微信支付时记录OrderNo
 */
public class OrderNoBean {
   public String OrderNo;

   public OrderNoBean(String orderNo) {
        OrderNo = orderNo;
    }
}
