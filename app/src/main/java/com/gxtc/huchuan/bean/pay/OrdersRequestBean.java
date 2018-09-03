package com.gxtc.huchuan.bean.pay;

import java.io.Serializable;

/**
 * Created by Steven on 16/12/5.
 * 订单请求
 */
public class OrdersRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String goodsName;   //商品名字
    private String token;
    private String transType;      //"DS", "打赏" "LL","流量交易" "CI","直播间课程报名"  "CS","直播间系列课购买"  "TO","担保交易订单"  "UR","用户充值"  "GJ","加入圈子" ZS 专题
    private String payType;     //支付类型。WX, ALIPAY   "WX"："微信"，  "ALIPAY"："支付宝"
    private String totalPrice;  //订单价格。单位分  价格要 * 100
    private String extra;
    private String payPassword;
    private String orderNo;//订单号为重新支付准备的

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 自定义参数，json格式字符串。
     * 1，当transType==DS时：
     * {“contId”:”12”, “type”:”wz”,” userCode”:”12”}
     * 2，当transType==LL时：
     * <p>
     * 3，当transType==CI时：
     * {“chatInfoId”:”12”}//直播课程ID
     * 4，当transType==CS时：
     * {“chatSeriesId”:”12”}//直播系列课ID
     * 5，当transType==TO时：
     * {“orderId”:”TO20170414164927443808”}
     * 6，当transType==UR时：可以为空
     * 7，当transType==GJ时：
     * {“groupId”:”12”}//圈子ID
     *
     * @return
     */


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
