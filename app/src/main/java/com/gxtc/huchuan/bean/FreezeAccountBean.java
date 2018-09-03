package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by sjr on 2017/3/22.
 */

public class FreezeAccountBean implements Serializable {
    /**
     * orderInfo : {"chatInfoId":"31","chatInfoTitle":"","createtime":"1491964913000","fee":"0.00","headPic":"","isPay":"1","isSett":"0","midFee":"0.00","name":"","orderId":"CI20170412104152913015","saleFee":"0.00","userCode":""}
     * orderType : chatSignup
     */

    private OrderInfoBean orderInfo;
    private String orderType;

    private static final long serialVersionUID = 1L;
    /**
     * invite : {"commission":"5.0","createTime":"1502677897000","facePic":"https://xmzjvip.b0.upaiyun.com/xmzj/35811498643069064.jpg","groupDesc":"call图拒绝了","headPic":"https://xmzjvip.b0.upaiyun.com/xmzj/38941502252345181.png","id":"237","isSett":"0","name":"nangua","title":"南瓜","type":"group","userCode":"275458197"}
     */

    private InviteBean invite;

    public OrderInfoBean getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfoBean orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public InviteBean getInvite() { return invite;}

    public void setInvite(InviteBean invite) { this.invite = invite;}


    public static class OrderInfoBean implements Serializable{

        private static final long serialVersionUID = -763628247875560522L;

        /**
         * chatInfoId : 31
         * chatInfoTitle :
         * createtime : 1491964913000
         * fee : 0.00
         * headPic :
         * isPay : 1
         * isSett : 0
         * midFee : 0.00
         * name :
         * orderId : CI20170412104152913015
         * saleFee : 0.00
         * userCode :
         */

        private String chatInfoId;
        private String chatInfoTitle;
        private String isPay;
        private String saleFee;
        private String userCode;

        /**
         * orderInfo : {"buyer":1,"createTime":1491533643000,"dbFee":0,"discount":0,"fbUserId":16,"fbUserName":"MzM","id":69,"isFinish":1,"message":"","orderId":"20170405170003393067","tradeAmt":0.1,"tradeInfoId":7,"tradeInfoPic":"http://xmzjvip.b0.upaiyun.com/xmzj/67181489562928458.png","tradeInfoTitle":"交易","unagremark":"","userCode":"678046738"}
         * orderType : tradeOrder
         */

        /**
         * commission : 5.0
         * createTime : 1502677897000
         * facePic : https://xmzjvip.b0.upaiyun.com/xmzj/35811498643069064.jpg
         * groupDesc : call图拒绝了
         * id : 237
         * title : 南瓜
         * type : group
         */

        private String commission;
        //@SerializedName("createTime") private String createTimeX;
        private                               String facePic;
        private                               String groupDesc;
        private                               String title;
        private                               String type;

        public String getChatInfoId() {
            return chatInfoId;
        }

        public void setChatInfoId(String chatInfoId) {
            this.chatInfoId = chatInfoId;
        }

        public String getChatInfoTitle() {
            return chatInfoTitle;
        }

        public void setChatInfoTitle(String chatInfoTitle) {
            this.chatInfoTitle = chatInfoTitle;
        }


        public String getIsPay() {
            return isPay;
        }

        public void setIsPay(String isPay) {
            this.isPay = isPay;
        }

        public String getSaleFee() {
            return saleFee;
        }

        public void setSaleFee(String saleFee) {
            this.saleFee = saleFee;
        }

        /**
         * chatSeriesId : 2
         * chatSeriesName :
         * createtime : 1492505877000
         * fee : 1.00
         * headPic :
         * isSett : 0
         * midFee : 0.00
         * name :
         * orderId : 1234123413
         * payFlag : 1
         * userCode :
         */

        private String chatSeriesId;
        private String chatSeriesName;
        private String createtime;
        private String fee;
        private String headPic;
        private String isSett;
        private String midFee;
        private String name;
        private String orderId;
        private String payFlag;
        private String isRefund;//0：正常，1：退款中，2：已退款
        private String endtime;
        private String groupId;
        private String groupName;
        private String payType;
        private String signuptime;
        private String groupCover;

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getSignuptime() {
            return signuptime;
        }

        public void setSignuptime(String signuptime) {
            this.signuptime = signuptime;
        }

        public String getGroupCover() {
            return groupCover;
        }

        public void setGroupCover(String groupCover) {
            this.groupCover = groupCover;
        }

        public String getIsRefund() {
            return isRefund;
        }

        public void setIsRefund(String isRefund) {
            this.isRefund = isRefund;
        }

        public String getChatSeriesId() {
            return chatSeriesId;
        }

        public void setChatSeriesId(String chatSeriesId) {
            this.chatSeriesId = chatSeriesId;
        }

        public String getChatSeriesName() {
            return chatSeriesName;
        }

        public void setChatSeriesName(String chatSeriesName) {
            this.chatSeriesName = chatSeriesName;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public String getIsSett() {
            return isSett;
        }

        public void setIsSett(String isSett) {
            this.isSett = isSett;
        }

        public String getMidFee() {
            return midFee;
        }

        public void setMidFee(String midFee) {
            this.midFee = midFee;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPayFlag() {
            return payFlag;
        }

        public void setPayFlag(String payFlag) {
            this.payFlag = payFlag;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        /**
         * buyer : 1
         * createTime : 1491533643000
         * dbFee : 0
         * discount : 0
         * fbUserId : 16
         * fbUserName : MzM
         * id : 69
         * isFinish : 1
         * message :
         * orderId : 20170405170003393067
         * tradeAmt : 0.1
         * tradeInfoId : 7
         * tradeInfoPic : http://xmzjvip.b0.upaiyun.com/xmzj/67181489562928458.png
         * tradeInfoTitle : 交易
         * unagremark :
         * userCode : 678046738
         */

        private int buyer;
        private long createTime;
        private String dbFee;
        private int discount;
        private int fbUserId;
        private String fbUserName;
        private int id;
        private int isFinish;
        private String message;
        private double tradeAmt;
        private int tradeInfoId;
        private String tradeInfoPic;
        private String tradeInfoTitle;
        private String unagremark;

        public int getBuyer() {
            return buyer;
        }

        public void setBuyer(int buyer) {
            this.buyer = buyer;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getDbFee() {
            return dbFee;
        }

        public void setDbFee(String dbFee) {
            this.dbFee = dbFee;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public int getFbUserId() {
            return fbUserId;
        }

        public void setFbUserId(int fbUserId) {
            this.fbUserId = fbUserId;
        }

        public String getFbUserName() {
            return fbUserName;
        }

        public void setFbUserName(String fbUserName) {
            this.fbUserName = fbUserName;
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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public double getTradeAmt() {
            return tradeAmt;
        }

        public void setTradeAmt(double tradeAmt) {
            this.tradeAmt = tradeAmt;
        }

        public int getTradeInfoId() {
            return tradeInfoId;
        }

        public void setTradeInfoId(int tradeInfoId) {
            this.tradeInfoId = tradeInfoId;
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

        public String getUnagremark() {
            return unagremark;
        }

        public void setUnagremark(String unagremark) {
            this.unagremark = unagremark;
        }

        public String getCommission() { return commission;}

        public void setCommission(String commission) { this.commission = commission;}

        public String getFacePic() { return facePic;}

        public void setFacePic(String facePic) { this.facePic = facePic;}

        public String getGroupDesc() { return groupDesc;}

        public void setGroupDesc(String groupDesc) { this.groupDesc = groupDesc;}

        public String getTitle() { return title;}

        public void setTitle(String title) { this.title = title;}

        public String getType() { return type;}

        public void setType(String type) { this.type = type;}
    }

    public static class InviteBean implements Serializable{

        private static final long serialVersionUID = -1L;

        /**
         * commission : 5.0
         * createTime : 1502677897000
         * facePic : https://xmzjvip.b0.upaiyun.com/xmzj/35811498643069064.jpg
         * groupDesc : call图拒绝了
         * headPic : https://xmzjvip.b0.upaiyun.com/xmzj/38941502252345181.png
         * id : 237
         * isSett : 0
         * name : nangua
         * title : 南瓜
         * type : group
         * userCode : 275458197
         */

        private String commission;
        private String createTime;
        private String facePic;
        private String groupDesc;
        private String headPic;
        private String id;
        private String isSett;
        private String name;
        private String title;
        private String type;
        private String userCode;

        public String getCommission() { return commission;}

        public void setCommission(String commission) { this.commission = commission;}

        public String getCreateTime() { return createTime;}

        public void setCreateTime(String createTime) { this.createTime = createTime;}

        public String getFacePic() { return facePic;}

        public void setFacePic(String facePic) { this.facePic = facePic;}

        public String getGroupDesc() { return groupDesc;}

        public void setGroupDesc(String groupDesc) { this.groupDesc = groupDesc;}

        public String getHeadPic() { return headPic;}

        public void setHeadPic(String headPic) { this.headPic = headPic;}

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public String getIsSett() { return isSett;}

        public void setIsSett(String isSett) { this.isSett = isSett;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getTitle() { return title;}

        public void setTitle(String title) { this.title = title;}

        public String getType() { return type;}

        public void setType(String type) { this.type = type;}

        public String getUserCode() { return userCode;}

        public void setUserCode(String userCode) { this.userCode = userCode;}
    }
}
