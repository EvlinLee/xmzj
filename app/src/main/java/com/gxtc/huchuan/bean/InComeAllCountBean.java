package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/21 .
 */

public class InComeAllCountBean {

    /**
     * chatMoney : 0.01
     * groupMoney : 10.0
     * rewardMoney : 0
     * totalMoney : 10.01
     * tradeMoney : 0
     * userStreams : [{"balance":"0.01","createTime":"1494387773000","orderId":"CI20170503193150161943","streamMoney":"0.01","title":"收费课程流水","type":"1"},{"balance":"10.00","createTime":"1494347400000","orderId":"123456","streamMoney":"10.00","title":"收费圈子流水","type":"3"}]
     */

    private String chatMoney;
    private String                groupMoney;
    private String                rewardMoney;
    private String                totalMoney;
    private String                tradeMoney;
    private String                distributionMoney;
    private List<UserStreamsBean> userStreams;

    public String getDistributionMoney() {
        return distributionMoney;
    }

    public void setDistributionMoney(String distributionMoney) {
        this.distributionMoney = distributionMoney;
    }

    public String getChatMoney() { return chatMoney;}

    public void setChatMoney(String chatMoney) { this.chatMoney = chatMoney;}

    public String getGroupMoney() { return groupMoney;}

    public void setGroupMoney(String groupMoney) { this.groupMoney = groupMoney;}

    public String getRewardMoney() { return rewardMoney;}

    public void setRewardMoney(String rewardMoney) { this.rewardMoney = rewardMoney;}

    public String getTotalMoney() { return totalMoney;}

    public void setTotalMoney(String totalMoney) { this.totalMoney = totalMoney;}

    public String getTradeMoney() { return tradeMoney;}

    public void setTradeMoney(String tradeMoney) { this.tradeMoney = tradeMoney;}

    public List<UserStreamsBean> getUserStreams() { return userStreams;}

    public void setUserStreams(List<UserStreamsBean> userStreams) { this.userStreams = userStreams;}

    public static class UserStreamsBean {
        /**
         * balance : 0.01
         * createTime : 1494387773000
         * orderId : CI20170503193150161943
         * streamMoney : 0.01
         * title : 收费课程流水
         * type : 1
         */

        private String balance;
        private String createTime;
        private String orderId;
        private String streamMoney;
        private String title;
        private String type;

        public String getBalance() { return balance;}

        public void setBalance(String balance) { this.balance = balance;}

        public String getCreateTime() { return createTime;}

        public void setCreateTime(String createTime) { this.createTime = createTime;}

        public String getOrderId() { return orderId;}

        public void setOrderId(String orderId) { this.orderId = orderId;}

        public String getStreamMoney() { return streamMoney;}

        public void setStreamMoney(String streamMoney) { this.streamMoney = streamMoney;}

        public String getTitle() { return title;}

        public void setTitle(String title) { this.title = title;}

        public String getType() { return type;}

        public void setType(String type) { this.type = type;}
    }
}
