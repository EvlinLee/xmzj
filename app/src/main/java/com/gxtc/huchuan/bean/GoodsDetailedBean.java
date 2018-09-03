package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

public class GoodsDetailedBean implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * audit : 1
     * audittime : 1490607259000
     * audituser : 39
     * audituserName : 测试
     * content : ["今年1月20日，北京市十四届人大第五次会议选举市委常委、市纪委书记张硕辅为北京市监察委员会主任，市人大常委会第三十四次会议任命了北京市监察委员会其他组成人员，标志着北京市监察委员会正式成立。北京市监察委员会将按照管理权限，对北京市所有行使公权力的公职人员依法实行监察，履行监督、调查、处置职责。

     1月21日上午，北京市监察委员会成立暨区级监察体制改革试点工作动员部署会召开。

     3月3日，海淀区纪委监察局网站曾发布消息，海淀区监察体制改革试点工作小组近日召开会议，研究并制定《海淀区深化监察体制改革试点工作实施方案》、《海淀区深化监察体制改革试点工作小组规则》等文件。","http://xmzjvip.b0.upaiyun.com/xmzj/24821490254184321.jpg"]
     * createTime : 1490606304000
     * dr : 0
     * id : 1
     * isTop : 0
     * isfinish : 0
     * num : 1000
     * price : 0.1
     * title : 交易测试
     * tradePrice : 0.1
     * tradeType : 0
     * tradeTypeSonId : 2
     * tradeTypeSonName : 域名交易
     * udefs : []
     * userId : 3
     */

    private int userId;
    private String userName;
    private String userPic;
    private int audit;                  //0:未审核，1审核通过，2审核不通过
    private long audittime;             //审核时间
    private int audituser;              //审核人ID
    private String audituserName;       //审核人名称
    private String content;             //内容
    private long createTime;
    private int dr;
    private int id;                     //交易信息id
    private int isTop;                  //是否置顶(0、否；1、是)
    private int isfinish;               //是否完成交易：0：未完成，1：交易中2：交易完成
    private int num;                    //数量
    private int readNum;                //阅读数量
    private int commentNum;             //评论数量
    private int pattern;                //模式。0、交易；1、论坛
    private String tradeOrderId;           //交易订单id 快速担保交易用到
    private int tradeType;              //是否出售  0：出售，1：求购(必选)
    private int tradeTypeSonId;         //交易分类ID
    private String price;               //价格
    private double tradePrice;          //实际交易价格
    private String title;               //标题
    private String tradeTypeSonName;    //交易小分类
    private String tradeTypeName;       //交易大分类
    private List<Udef> udefs;           //商品参数条件
    private String contacts;            //联系电话
    private String picUrl;              //封面图片
    private String isPost;              //是否需要物流。0：不需要；1：需要
    private String anonymous;           //是否匿名。0：不匿名；1：匿名
    private String userCode;            //发布人融云id 。
    private String targetUserCode;      //对方的userCode
    private String shareUrl;            //分享链接
    private int isCollect;              //是否收藏
    private int workOff;                //已售出多少
    private int buyWay;                 //0:买家付，1：卖家付，2：双方平摊
    private int buyer;                  //发起快速担保交易的人的身份，0：买家，1：卖家
    private int fastTradeState;         //0未同意， 1同意， 2，不同意
    private String videoText;             //视频
    private String videoPic;             //视频封面
    private String createSource;        //创建来源：0常规帖， 1资源交易主页创建的快速交易帖， 2个人主页创建的快速交易帖子

    public String getCreateSource() {
        return createSource;
    }

    public void setCreateSource(String createSource) {
        this.createSource = createSource;
    }

    public String getTargetUserCode() {
        return targetUserCode;
    }

    public void setTargetUserCode(String targetUserCode) {
        this.targetUserCode = targetUserCode;
    }

    public String getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(String tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public int getBuyer() {
        return buyer;
    }

    public void setBuyer(int buyer) {
        this.buyer = buyer;
    }

    public int getFastTradeState() {
        return fastTradeState;
    }

    public void setFastTradeState(int fastTradeState) {
        this.fastTradeState = fastTradeState;
    }

    private List<String> picUrlList;

    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public void setPicUrlList(List<String> picUrlList) {
        this.picUrlList = picUrlList;
    }

    public int getBuyWay() {
        return buyWay;
    }

    public void setBuyWay(int buyWay) {
        this.buyWay = buyWay;
    }

    public int getWorkOff() {
        return workOff;
    }

    public void setWorkOff(int workOff) {
        this.workOff = workOff;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
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

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public long getAudittime() {
        return audittime;
    }

    public void setAudittime(long audittime) {
        this.audittime = audittime;
    }

    public int getAudituser() {
        return audituser;
    }

    public void setAudituser(int audituser) {
        this.audituser = audituser;
    }

    public String getAudituserName() {
        return audituserName;
    }

    public void setAudituserName(String audituserName) {
        this.audituserName = audituserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public int getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(int isfinish) {
        this.isfinish = isfinish;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public int getTradeTypeSonId() {
        return tradeTypeSonId;
    }

    public void setTradeTypeSonId(int tradeTypeSonId) {
        this.tradeTypeSonId = tradeTypeSonId;
    }

    public String getTradeTypeSonName() {
        return tradeTypeSonName;
    }

    public void setTradeTypeSonName(String tradeTypeSonName) {
        this.tradeTypeSonName = tradeTypeSonName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public List<Udef> getUdefs() {
        return udefs;
    }

    public void setUdefs(List<Udef> udefs) {
        this.udefs = udefs;
    }

    public static class Udef implements Serializable{

        private static final long serialVersionUID = 1L;

        private String id ;
        private String tradeTypeId ;
        private String tradeField ;
        private String name ;
        private String value ;
        private String choice ;
        private String tradeInfoValue ;

        public Udef() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTradeTypeId() {
            return tradeTypeId;
        }

        public void setTradeTypeId(String tradeTypeId) {
            this.tradeTypeId = tradeTypeId;
        }

        public String getTradeField() {
            return tradeField;
        }

        public void setTradeField(String tradeField) {
            this.tradeField = tradeField;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }

        public String getTradeInfoValue() {
            return tradeInfoValue;
        }

        public void setTradeInfoValue(String tradeInfoValue) {
            this.tradeInfoValue = tradeInfoValue;
        }
    }

    public String getVideoText() {
        return videoText;
    }

    public void setVideoText(String videoText) {
        this.videoText = videoText;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }
}
