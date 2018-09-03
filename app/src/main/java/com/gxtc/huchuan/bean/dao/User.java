package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Steven on 16/12/11.
 */
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;

    private String token;
    private String imToken;         //融云
    private String age;
    private String email;
    private String createTime;      //创建时间
    private String education;
    private String headPic;         //头像url
    private String interest;
    private String isAnchor;        //是否是主播
    private String isAuthor;        //是否是作者     0 不是作者  1是作者   2是正在审核中
    private String isBlog;          //是否是圈主
    private String isSaler;         //是否是卖家
    private String isRealAudit;     //是否实名 0、否；1、是;2、实名审核中 3,审核不通过
    private String name;
    private String phone;
    private String sex;
    private String address;     //地址
    private String area;        //区域
    private String city;        //城市
    private String province;    //省
    private String followCount;     //关注
    private String fsCount;         //粉丝数
    private String balance;         //余额
    private String frozenBalance;   //冻结资金
    private String usableBalance;  //可用余额
    private String deposit;         //待提现金额
    private String selfMediaName;  //自媒体名称  bfa1167339cb0d749fa1736af786c4fc
    private String introduction;    //简介
    private String chatRoomId;      //课堂ID，如果是0或空表示没有创建课堂
    private String userCode;
    private String uniqueKey;       //第三方登录的唯一标识
    private String memberLevel;     //会员等级
    private String selfMediaLevel;  //自媒体等级
    private String memberLevelPicUrl;   //会员等级图标
    private String selfMediaLevelPicUrl;    //自媒体等级图标
    private String bakPic;      //背景图片
    private String isFollow;    //是否关注，0：未关注 1：已关注
    private String shareUrl;    //个人主页分享链接
    private String userDynamicCount;    //用户动态数量
    private String browseCount; //访问数量
    private int hasPayPwd; //是否有支付密码。       0：否；1：有


    private String friendChat; //仅好友可聊天。0、否；1、是
    private String friendComment; //仅好友可评论。0、否；1、是





    @Generated(hash = 586692638)
    public User() {
    }




    @Generated(hash = 641154651)
    public User(Long id, String token, String imToken, String age, String email,
            String createTime, String education, String headPic, String interest,
            String isAnchor, String isAuthor, String isBlog, String isSaler,
            String isRealAudit, String name, String phone, String sex,
            String address, String area, String city, String province,
            String followCount, String fsCount, String balance,
            String frozenBalance, String usableBalance, String deposit,
            String selfMediaName, String introduction, String chatRoomId,
            String userCode, String uniqueKey, String memberLevel,
            String selfMediaLevel, String memberLevelPicUrl,
            String selfMediaLevelPicUrl, String bakPic, String isFollow,
            String shareUrl, String userDynamicCount, String browseCount,
            int hasPayPwd, String friendChat, String friendComment) {
        this.id = id;
        this.token = token;
        this.imToken = imToken;
        this.age = age;
        this.email = email;
        this.createTime = createTime;
        this.education = education;
        this.headPic = headPic;
        this.interest = interest;
        this.isAnchor = isAnchor;
        this.isAuthor = isAuthor;
        this.isBlog = isBlog;
        this.isSaler = isSaler;
        this.isRealAudit = isRealAudit;
        this.name = name;
        this.phone = phone;
        this.sex = sex;
        this.address = address;
        this.area = area;
        this.city = city;
        this.province = province;
        this.followCount = followCount;
        this.fsCount = fsCount;
        this.balance = balance;
        this.frozenBalance = frozenBalance;
        this.usableBalance = usableBalance;
        this.deposit = deposit;
        this.selfMediaName = selfMediaName;
        this.introduction = introduction;
        this.chatRoomId = chatRoomId;
        this.userCode = userCode;
        this.uniqueKey = uniqueKey;
        this.memberLevel = memberLevel;
        this.selfMediaLevel = selfMediaLevel;
        this.memberLevelPicUrl = memberLevelPicUrl;
        this.selfMediaLevelPicUrl = selfMediaLevelPicUrl;
        this.bakPic = bakPic;
        this.isFollow = isFollow;
        this.shareUrl = shareUrl;
        this.userDynamicCount = userDynamicCount;
        this.browseCount = browseCount;
        this.hasPayPwd = hasPayPwd;
        this.friendChat = friendChat;
        this.friendComment = friendComment;
    }




    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImToken() {
        return this.imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBakPic() {
        return this.bakPic;
    }

    public void setBakPic(String bakPic) {
        this.bakPic = bakPic;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEducation() {
        return this.education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getHeadPic() {
        return this.headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getInterest() {
        return this.interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getIsAnchor() {
        return this.isAnchor;
    }

    public void setIsAnchor(String isAnchor) {
        this.isAnchor = isAnchor;
    }

    public String getIsAuthor() {
        return this.isAuthor;
    }

    public void setIsAuthor(String isAuthor) {
        this.isAuthor = isAuthor;
    }

    public String getIsBlog() {
        return this.isBlog;
    }

    public void setIsBlog(String isBlog) {
        this.isBlog = isBlog;
    }

    public String getIsSaler() {
        return this.isSaler;
    }

    public void setIsSaler(String isSaler) {
        this.isSaler = isSaler;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemberLevel() {
        return this.memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getMemberLevelPicUrl() {
        return this.memberLevelPicUrl;
    }

    public void setMemberLevelPicUrl(String memberLevelPicUrl) {
        this.memberLevelPicUrl = memberLevelPicUrl;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getFollowCount() {
        return this.followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }

    public String getFsCount() {
        return this.fsCount;
    }

    public void setFsCount(String fsCount) {
        this.fsCount = fsCount;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFrozenBalance() {
        return this.frozenBalance;
    }

    public void setFrozenBalance(String frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public String getUsableBalance() {
        return this.usableBalance;
    }

    public void setUsableBalance(String usableBalance) {
        this.usableBalance = usableBalance;
    }

    public String getSelfMediaLevel() {
        return this.selfMediaLevel;
    }

    public void setSelfMediaLevel(String selfMediaLevel) {
        this.selfMediaLevel = selfMediaLevel;
    }

    public String getSelfMediaLevelPicUrl() {
        return this.selfMediaLevelPicUrl;
    }

    public void setSelfMediaLevelPicUrl(String selfMediaLevelPicUrl) {
        this.selfMediaLevelPicUrl = selfMediaLevelPicUrl;
    }

    public String getSelfMediaName() {
        return this.selfMediaName;
    }

    public void setSelfMediaName(String selfMediaName) {
        this.selfMediaName = selfMediaName;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getChatRoomId() {
        return this.chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getIsFollow() {
        return this.isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public String getShareUrl() {
        return this.shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getUserDynamicCount() {
        return this.userDynamicCount;
    }

    public void setUserDynamicCount(String userDynamicCount) {
        this.userDynamicCount = userDynamicCount;
    }

    public String getBrowseCount() {
        return this.browseCount;
    }

    public void setBrowseCount(String browseCount) {
        this.browseCount = browseCount;
    }

    public int getHasPayPwd() {
        return this.hasPayPwd;
    }

    public void setHasPayPwd(int hasPayPwd) {
        this.hasPayPwd = hasPayPwd;
    }

    public String getDeposit() {
        return this.deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getIsRealAudit() {
        return this.isRealAudit;
    }

    public void setIsRealAudit(String isRealAudit) {
        this.isRealAudit = isRealAudit;
    }




    public String getFriendChat() {
        return this.friendChat;
    }




    public void setFriendChat(String friendChat) {
        this.friendChat = friendChat;
    }




    public String getFriendComment() {
        return this.friendComment;
    }




    public void setFriendComment(String friendComment) {
        this.friendComment = friendComment;
    }

}
