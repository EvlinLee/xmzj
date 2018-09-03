package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Steven on 17/4/5.
 */

public class PurchaseListBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * createTime : 1491382194000
     * id : 2
     * isFinish : 0
     * orderId : 20170405164953866482
     * tradeAmt : 0.1
     * tradeInfoPic : http://xmzjvip.b0.upaiyun.com/xmzj/67181489562928458.png
     * tradeInfoTitle : 交易测试
     */

    private long   createTime;
    private int    buyer;           //是否是买家。0：买家；1：卖家
    private int    id;
    private int    commentNum;      //留言数量
    private int    read;            //阅读数量
    private int    isFinish;
    private String orderId;
    private double tradeAmt;
    private String tradeInfoPic;
    private String tradeInfoTitle;

    //用户退款申请列表
    private int    type;              //0:交易退款，1，课堂退款，2，圈子退款，3，商城退款
    private int    audit;             //审核状态     0:未审核，1：审核通过，2：审核不通过
    private String   orderTime;         //下单时间戳
    private double refundMoney;       //退款金额
    private String auditRemark;       //审核备注说明
    private String remark;            //退款说明
    private boolean isCheck;//是否选中
    private boolean isShow;//是否显示cb
    private String tradeOrderId;//担保交易订单id
    private String orderPrice;
    private String isRefund;


    //卖家获取退款申请列表
    private String title;
    private String auditTime;
    private String fee;
    private String userCode;
    private String userName;
    private String userPic;

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(double refundMoney) {
        this.refundMoney = refundMoney;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(double tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getTradeInfoPic() {
        return tradeInfoPic;
    }

    public void setTradeInfoPic(String tradeInfoPic) {
        this.tradeInfoPic = tradeInfoPic;
    }

    public String getTradeInfoTitle() {
        return tradeInfoTitle;
    }

    public void setTradeInfoTitle(String tradeInfoTitle) {
        this.tradeInfoTitle = tradeInfoTitle;
    }

    public int getBuyer() {
        return buyer;
    }

    public void setBuyer(int buyer) {
        this.buyer = buyer;
    }
}
