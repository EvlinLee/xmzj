package com.gxtc.huchuan.bean.event;

public class EventJPushBean {

    public static int TRADE = 0;       //订单交易通知
    public static int LIVE = 1;       //收费课程
    public static int FLOW_TRADE = 2;       //流量交易
    public static int CIRCLE_EXPIRE = 3;       //收费圈子到期
    public static int SELLER_TRADE = 4;       //商家交易
    public static int AUTHOR_AUDIT = 5;       //作者审核
    public static int ANCHOR_AUDIT = 6;       //主播审核
    public static int CIRCLE_AUDIT = 7;       //圈子审核
    public static int DASHANG = 8;       //打赏
    public static int RECHARGE = 9;       //充值
    public static int TAKE_MONEY = 11;       //提现
    public static int REFUND = 12;       //退款
    public static int ACCOUNT_BILL = 13;       //账户流水
    public static int USER_FOLLOW = 14;       //用户关注
    public static int NEWS_AUDIT = 15;       //新闻审核
    public static int DYNAMIC_NOTICE = 16;       //动态提醒
    public static int CIRCLE_NOTICE = 17;       //圈子提醒
    public static int ARTICLE_NOTICE = 18;       //文章提醒
    public static int CLASS_NOTICE = 19;       //课堂提醒
    public static int TRADE_NOTICE = 20;       //交易提醒
    public static int APPLY_FRIENDS = 21;       //好友申请提醒
    public static int RE_APPLY_FRIENDS = 22;       //重新获取好友申请提醒数量

    public static int IM_MESSAGE = 101;         //融云的消息

    public int type ;
    public int num;
    public String bizId;
    public String userPic;

    public EventJPushBean(int type, String bizId) {
        this.type = type;
        this.bizId = bizId;
    }

    public EventJPushBean(int type, String bizId, int num) {
        this.type = type;
        this.num = num;
        this.bizId = bizId;
    }

    public EventJPushBean(int type, String bizId, String userPic, int num) {
        this.type = type;
        this.num = num;
        this.bizId = bizId;
        this.userPic = userPic;
    }
}
