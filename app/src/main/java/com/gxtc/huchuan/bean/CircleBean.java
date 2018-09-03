package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

public class CircleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    //发现圈子
    private String groupName;       //圈子名称
    private String groupCode;       //圈子编号
    private String groupChatName;   //群聊名称
    private String groupChatId;     //群聊ID
    private String content;         //圈子描述
    private String cover;           //圈子封面
    private String backgCover;      //圈子背景图片
    private String userCode;        //用户编码
    private String userName;        //用户名
    private String userPic;         //用户头像
    private int    infoNum;            //帖子数量
    private int    attention;          //关注数量
    private int    isFee;              //是否收费。0：免费、1：收费
    private double fee;             //费用
    private int    isJoin;             //是否已加入。0：未加入。1：已加入
    private String joinUrl;         //圈子介绍详情链接
    private int    privateChat;        //允许私聊。0、允许；1、不允许
    private int    pent;               //分成
    private int    dispark;            //开放成员。0、开放；1、不开放

    //圈子主页(不是首页)
    private int    groupChatNum;    //群聊的数量
    private String notice;          //圈子公告
    private String shareUrl;        //圈子分享链接
    private String qrUrl;           //二维码链接
    private String chatId;          //圈子讨论组ID
    private String owner;           //圈主名字
    private String isShow;          //圈子是否通过审核 0：未通过；1：通过
    private String roleType;          //角色。0:普通用户，1：管理员，2：圈主
    private String isShutup;          //0:未禁言；1：已禁用
    private String onlyReal;          //是否开启仅实名用户可加入。0：未通过；1：通过


    private int    isMy;               //是否是圈主。0:否；1：是
    private double brokerage;       //分享佣金。
    private int    eNum;               //文章数量
    private int    cNum;               //课堂数量
    private int    fNum;               //文件数量

    private int count;            //群人数


    //圈子资料
    private String       name;
    private long         createTime;      //创建时间
    private long         userEndtime;      //剩余到期时间
    private int          adminNum;         //管理员数量
    private int          regularNum;       //普通成员数量
    private int          memberType;        //成员类型  0 普通成员  1 管理员  2 圈主
    private int          isReceive;        //0、接收动态；1、不接收动态
    private String       isAudit;           //成员是否需要审核 0、不需要；1需要
    private String       isRefund;           //0、不可退款；1、可退款
    private List<Member> adminMembers;      //管理员集合
    private List<Member> groupMembers;      //普通成员集合


    //圈子数据统计列表
    private int    groupId;                //圈子id
    private int    totalInfo;              //帖子总数
    private int    totalJoin;              //成员总数
    private double totalEarnings;          //圈子总收益
    private double earnings;               //圈子当天收益
    private double weekEarnings;           //圈子本周收益
    private double monthEarnings;          //圈子本月收益
    private int    charge;                 //圈子当天付费用户数量
    private int    free;                   //圈子当天免费用户数量
    private int    commNum;                //活跃用户评论量
    private int    checkNum;               //圈子查看数
    private int    preViewNum;             //圈子预览数
    private String    saleFee;                //每日佣金
    private String    dayActiveUser;          //每日活跃用户数
    private String    dayGroupHomeBrowse;      //圈子主页每日查看量
    private String    someDaysGroupHomeBrowse; //圈子主页30天查看量
    private String    dayGroupIntroBrowse;     // 圈子介绍页日访问
    private String    someDaysGroupIntroBrowse;// 圈子介绍页月访问
    private String    lastSomeDaysJoinMember;  // 最近三十天加入的人数
    private String    lastSomeDaysGroupInfo;   // 最近三十天动态数
    private String    lastSomeDaysActiveMember;// 最近三十天活跃成员
    private int    sevenRealIncomeCount;// 最近7天圈子收入的笔数
    private int    thirtyRealIncomeCount  ;// 最近30天圈子收入的笔数



    private String refundRemark;

    private int    memberCount;                //成员人数
    private int    unReadInfo;                 //未读圈子动态数量

    private String  typeCode;               //分类编码
    private String  typeName;               //分类名称
    private boolean isSelect;
    private String  isAlsdinfo;             //是否允许动态同步  0=允许，1=不允许

    private int jumpPage = -1;              //跳转的页面

    private int isCollect = -1;
    private String ownerPic;

    private long shutupStartTime;
    private long shutupEndTime;
    private String isShutupTiming ;
    private String isUnRelinfo ;//全体禁发动态 1设置 0正常
    private long unRelinfoStartTime;
    private long unRelinfoEndTime;
    private String isUnRelinfoTiming;  //定时全体禁发动态1 设置  0正常

    private int unReadChatNum;//课程
    private int unReadFileNum;//文件
    private int unReadInfoNum;//精华
    private int unReadNewsNum;//文章

    private int canJoin;//1 设置  0正常
    private int createGroupChat;//1 没创建   0 已经创建

    public int getSevenRealIncomeCount() {
        return sevenRealIncomeCount;
    }

    public void setSevenRealIncomeCount(int sevenRealIncomeCount) {
        this.sevenRealIncomeCount = sevenRealIncomeCount;
    }

    public int getThirtyRealIncomeCount() {
        return thirtyRealIncomeCount;
    }

    public void setThirtyRealIncomeCount(int thirtyRealIncomeCount) {
        this.thirtyRealIncomeCount = thirtyRealIncomeCount;
    }

    public String getSaleFee() {
        return saleFee;
    }

    public void setSaleFee(String saleFee) {
        this.saleFee = saleFee;
    }

    public String getDayActiveUser() {
        return dayActiveUser;
    }

    public void setDayActiveUser(String dayActiveUser) {
        this.dayActiveUser = dayActiveUser;
    }

    public String getDayGroupHomeBrowse() {
        return dayGroupHomeBrowse;
    }

    public void setDayGroupHomeBrowse(String dayGroupHomeBrowse) {
        this.dayGroupHomeBrowse = dayGroupHomeBrowse;
    }

    public String getSomeDaysGroupHomeBrowse() {
        return someDaysGroupHomeBrowse;
    }

    public void setSomeDaysGroupHomeBrowse(String someDaysGroupHomeBrowse) {
        this.someDaysGroupHomeBrowse = someDaysGroupHomeBrowse;
    }

    public String getDayGroupIntroBrowse() {
        return dayGroupIntroBrowse;
    }

    public void setDayGroupIntroBrowse(String dayGroupIntroBrowse) {
        this.dayGroupIntroBrowse = dayGroupIntroBrowse;
    }

    public String getSomeDaysGroupIntroBrowse() {
        return someDaysGroupIntroBrowse;
    }

    public void setSomeDaysGroupIntroBrowse(String someDaysGroupIntroBrowse) {
        this.someDaysGroupIntroBrowse = someDaysGroupIntroBrowse;
    }

    public String getLastSomeDaysJoinMember() {
        return lastSomeDaysJoinMember;
    }

    public void setLastSomeDaysJoinMember(String lastSomeDaysJoinMember) {
        this.lastSomeDaysJoinMember = lastSomeDaysJoinMember;
    }

    public String getLastSomeDaysGroupInfo() {
        return lastSomeDaysGroupInfo;
    }

    public void setLastSomeDaysGroupInfo(String lastSomeDaysGroupInfo) {
        this.lastSomeDaysGroupInfo = lastSomeDaysGroupInfo;
    }

    public String getLastSomeDaysActiveMember() {
        return lastSomeDaysActiveMember;
    }

    public void setLastSomeDaysActiveMember(String lastSomeDaysActiveMember) {
        this.lastSomeDaysActiveMember = lastSomeDaysActiveMember;
    }

    public int getCreateGroupChat() {
        return createGroupChat;
    }

    public void setCreateGroupChat(int createGroupChat) {
        this.createGroupChat = createGroupChat;
    }

    public int getCanJoin() {
        return canJoin;
    }

    public void setCanJoin(int canJoin) {
        this.canJoin = canJoin;
    }

    public int getUnReadChatNum() {
        return unReadChatNum;
    }

    public void setUnReadChatNum(int unReadChatNum) {
        this.unReadChatNum = unReadChatNum;
    }

    public int getUnReadFileNum() {
        return unReadFileNum;
    }

    public void setUnReadFileNum(int unReadFileNum) {
        this.unReadFileNum = unReadFileNum;
    }

    public int getUnReadInfoNum() {
        return unReadInfoNum;
    }

    public void setUnReadInfoNum(int unReadInfoNum) {
        this.unReadInfoNum = unReadInfoNum;
    }

    public int getUnReadNewsNum() {
        return unReadNewsNum;
    }

    public void setUnReadNewsNum(int unReadNewsNum) {
        this.unReadNewsNum = unReadNewsNum;
    }

    public String getIsUnRelinfo() {
        return isUnRelinfo;
    }

    public void setIsUnRelinfo(String isUnRelinfo) {
        this.isUnRelinfo = isUnRelinfo;
    }

    public long getUnRelinfoStartTime() {
        return unRelinfoStartTime;
    }

    public void setUnRelinfoStartTime(long unRelinfoStartTime) {
        this.unRelinfoStartTime = unRelinfoStartTime;
    }

    public long getUnRelinfoEndTime() {
        return unRelinfoEndTime;
    }

    public void setUnRelinfoEndTime(long unRelinfoEndTime) {
        this.unRelinfoEndTime = unRelinfoEndTime;
    }

    public String getIsUnRelinfoTiming() {
        return isUnRelinfoTiming;
    }

    public void setIsUnRelinfoTiming(String isUnRelinfoTiming) {
        this.isUnRelinfoTiming = isUnRelinfoTiming;
    }

    public long getShutupStartTime() {
        return shutupStartTime;
    }

    public void setShutupStartTime(long shutupStartTime) {
        this.shutupStartTime = shutupStartTime;
    }

    public long getShutupEndTime() {
        return shutupEndTime;
    }

    public void setShutupEndTime(long shutupEndTime) {
        this.shutupEndTime = shutupEndTime;
    }

    public String getIsShutupTiming() {
        return isShutupTiming;
    }

    public void setIsShutupTiming(String isShutupTiming) {
        this.isShutupTiming = isShutupTiming;
    }

    public int getUnReadInfo() {
        return unReadInfo;
    }

    public void setUnReadInfo(int unReadInfo) {
        this.unReadInfo = unReadInfo;
    }

    public int getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
    }

    public int getPreViewNum() {
        return preViewNum;
    }

    public void setPreViewNum(int preViewNum) {
        this.preViewNum = preViewNum;
    }

    public double getWeekEarnings() {
        return weekEarnings;
    }

    public void setWeekEarnings(double weekEarnings) {
        this.weekEarnings = weekEarnings;
    }

    public double getMonthEarnings() {
        return monthEarnings;
    }

    public void setMonthEarnings(double monthEarnings) {
        this.monthEarnings = monthEarnings;
    }

    public String getOwnerPic() {
        return ownerPic;
    }

    public void setOwnerPic(String ownerPic) {
        this.ownerPic = ownerPic;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public long getUserEndtime() {
        return userEndtime;
    }

    public void setUserEndtime(long userEndtime) {
        this.userEndtime = userEndtime;
    }

    public String getOnlyReal() {
        return onlyReal;
    }

    public void setOnlyReal(String onlyReal) {
        this.onlyReal = onlyReal;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getJumpPage() {
        return jumpPage;
    }

    public void setJumpPage(int jumpPage) {
        this.jumpPage = jumpPage;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getIsAlsdinfo() {
        return isAlsdinfo;
    }

    public void setIsAlsdinfo(String isAlsdinfo) {
        this.isAlsdinfo = isAlsdinfo;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }

    private List<CircleDataVosBean> dataVos;        //日统计

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

    public String getIsShutup() {
        return isShutup;
    }

    public void setIsShutup(String isShutup) {
        this.isShutup = isShutup;
    }

    //圈子公告列表
    private String title;
    private String context;

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public int getGroupChatNum() {
        return groupChatNum;
    }

    public void setGroupChatNum(int groupChatNum) {
        this.groupChatNum = groupChatNum;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBackgCover() {
        return backgCover;
    }

    public void setBackgCover(String backgCover) {
        this.backgCover = backgCover;
    }

    public int getIsReceive() {
        return isReceive;
    }

    public void setIsReceive(int isReceive) {
        this.isReceive = isReceive;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getcNum() {
        return cNum;
    }

    public void setcNum(int cNum) {
        this.cNum = cNum;
    }

    public int getfNum() {
        return fNum;
    }

    public void setfNum(int fNum) {
        this.fNum = fNum;
    }

    public int geteNum() {
        return eNum;
    }

    public void seteNum(int eNum) {
        this.eNum = eNum;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCommNum() {
        return commNum;
    }

    public void setCommNum(int commNum) {
        this.commNum = commNum;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getTotalInfo() {
        return totalInfo;
    }

    public void setTotalInfo(int totalInfo) {
        this.totalInfo = totalInfo;
    }

    public int getTotalJoin() {
        return totalJoin;
    }

    public void setTotalJoin(int totalJoin) {
        this.totalJoin = totalJoin;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public List<CircleDataVosBean> getDataVos() {
        return dataVos;
    }

    public void setDataVos(List<CircleDataVosBean> dataVos) {
        this.dataVos = dataVos;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public double getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(double brokerage) {
        this.brokerage = brokerage;
    }

    public String getChatId() {
        return chatId;
    }

    public int getPent() {
        return pent;
    }

    public void setPent(int pent) {
        this.pent = pent;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(int privateChat) {
        this.privateChat = privateChat;
    }

    public int getDispark() {
        return dispark;
    }

    public void setDispark(int dispark) {
        this.dispark = dispark;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getAdminNum() {
        return adminNum;
    }

    public void setAdminNum(int adminNum) {
        this.adminNum = adminNum;
    }

    public int getRegularNum() {
        return regularNum;
    }

    public void setRegularNum(int regularNum) {
        this.regularNum = regularNum;
    }

    public List<Member> getAdminMembers() {
        return adminMembers;
    }

    public void setAdminMembers(List<Member> adminMembers) {
        this.adminMembers = adminMembers;
    }

    public List<Member> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Member> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getIsFee() {
        return isFee;
    }

    public void setIsFee(int isFee) {
        this.isFee = isFee;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(int isJoin) {
        this.isJoin = isJoin;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getInfoNum() {
        return infoNum;
    }

    public void setInfoNum(int infoNum) {
        this.infoNum = infoNum;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public static class Member implements Serializable {
        private int    type;       //用户类型。0:普通用户，1：管理员，2：圈主
        private String userCode;
        private String userName;
        private String userPic;
        private String content;         //用户简介

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
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


    @Override
    public String toString() {
        return "CircleBean{" + "id=" + id + ", groupName='" + groupName + '\'' + ", content='" + content + '\'' + ", cover='" + cover + '\'' + ", userCode='" + userCode + '\'' + ", userName='" + userName + '\'' + ", userPic='" + userPic + '\'' + ", infoNum=" + infoNum + ", attention=" + attention + ", isFee=" + isFee + ", fee=" + fee + ", isJoin=" + isJoin + ", joinUrl='" + joinUrl + '\'' + ", notice='" + notice + '\'' + ", shareUrl='" + shareUrl + '\'' + ", isMy=" + isMy + ", name='" + name + '\'' + ", createTime=" + createTime + ", adminNum=" + adminNum + ", regularNum=" + regularNum + ", memberType=" + memberType + ", adminMembers=" + adminMembers + ", groupMembers=" + groupMembers + '}';
    }
}
