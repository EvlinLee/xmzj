package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by 宋家任 on 2017/4/23.
 * 需求更改资讯新的实体bean，旧的特么的太多地方用了，，，不改了
 * 注意两个坑。。不是String而是jsonArray。。。
 */

public class NewNewsBean implements Serializable {
    private static final long serialVersionUID = -763618247875650322L;

    @Override
    public String toString() {
        return "NewNewsBean{" + "data=" + data + ", type='" + type + '\'' + '}';
    }

    /**
     * data : {"audit":"1","commentCount":"0","content":"","cover":"http://xmzjvip.b0.upaiyun.com/xmzj/99641492591502821.png!upyun300","date":"1492679832000","desc":"写不一样的文章，做不一样的自己","digest":"看标题都懂，不用看摘要了","id":"828","isCollect":"0","isFollow":"0","isThumbsup":"0","isVideo":"0","keywords":"","readCount":"49","redirectUrl":"http://120.25.56.153/publish/news/viewNews.do?newsId=828&token=","showtype":"1","source":"四歌","sourceicon":"http://xmzjvip.b0.upaiyun.com/xmzj/63971492076259958.jpg","thumbsupCount":"0","title":"让你的群质量迅速提升3倍的社群维护技巧","typeId":"32","typeSign":"0","userCode":"762301550","videoUrl":""}
     * type : news
     */

    private DataBean data;
    private String   type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBean implements Serializable {
        @Override
        public String toString() {
            return "DataBean{" + "audit='" + audit + '\'' + ", commentCount='" + commentCount + '\'' + ", content='" + content + '\'' + ", cover='" + cover + '\'' + ", date='" + date + '\'' + ", desc='" + desc + '\'' + ", digest='" + digest + '\'' + ", id='" + id + '\'' + ", isCollect='" + isCollect + '\'' + ", isFollow='" + isFollow + '\'' + ", isThumbsup='" + isThumbsup + '\'' + ", isVideo='" + isVideo + '\'' + ", keywords='" + keywords + '\'' + ", readCount='" + readCount + '\'' + ", redirectUrl='" + redirectUrl + '\'' + ", showtype='" + showtype + '\'' + ", source='" + source + '\'' + ", sourceicon='" + sourceicon + '\'' + ", thumbsupCount='" + thumbsupCount + '\'' + ", title='" + title + '\'' + ", typeId='" + typeId + '\'' + ", typeSign='" + typeSign + '\'' + ", userCode='" + userCode + '\'' + ", videoUrl='" + videoUrl + '\'' + ", anonymous='" + anonymous + '\'' + ", createTime='" + createTime + '\'' + ", isTop='" + isTop + '\'' + ", isfinish='" + isfinish + '\'' + ", liuYan='" + liuYan + '\'' + ", read='" + read + '\'' + ", tradeType='" + tradeType + '\'' + ", tradeTypeSonId='" + tradeTypeSonId + '\'' + ", tradeTypeSonName='" + tradeTypeSonName + '\'' + ", userId='" + userId + '\'' + ", userName='" + userName + '\'' + ", userPic='" + userPic + '\'' + ", chatInfoCount='" + chatInfoCount + '\'' + ", chatRoom='" + chatRoom + '\'' + ", chatSeries='" + chatSeries + '\'' + ", chatRoomName='" + chatRoomName + '\'' + ", chatSeriesCount='" + chatSeriesCount + '\'' + ", chatTypeSonId='" + chatTypeSonId + '\'' + ", chattype='" + chattype + '\'' + ", chatway='" + chatway + '\'' + ", endtime='" + endtime + '\'' + ", facePic='" + facePic + '\'' + ", fee='" + fee + '\'' + ", followCount='" + followCount + '\'' + ", freetime='" + freetime + '\'' + ", isBanned='" + isBanned + '\'' + ", isSelf='" + isSelf + '\'' + ", isSignup='" + isSignup + '\'' + ", isfree='" + isfree + '\'' + ", joinCount='" + joinCount + '\'' + ", mainSpeaker='" + mainSpeaker + '\'' + ", password='" + password + '\'' + ", chatRoomHeadPic='" + chatRoomHeadPic + '\'' + ", pent='" + pent + '\'' + ", shareUrl='" + shareUrl + '\'' + ", showShare='" + showShare + '\'' + ", showSignup='" + showSignup + '\'' + ", showinfo='" + showinfo + '\'' + ", speakerIntroduce='" + speakerIntroduce + '\'' + ", starttime='" + starttime + '\'' + ", status='" + status + '\'' + ", subtitle='" + subtitle + '\'' + ", advContentId=" + advContentId + ", contentType=" + contentType + ", type='" + type + '\'' + ", url='" + url + '\'' + ", newsRespVo=" + newsRespVo + '}';
        }

        private static final long serialVersionUID = -763618667875650322L;
        /**
         * audit : 1
         * commentCount : 0
         * content :
         * cover : http://xmzjvip.b0.upaiyun.com/xmzj/99641492591502821.png!upyun300
         * date : 1492679832000
         * desc : 写不一样的文章，做不一样的自己
         * digest : 看标题都懂，不用看摘要了
         * id : 828
         * isCollect : 0
         * isFollow : 0
         * isThumbsup : 0
         * isVideo : 0
         * keywords :
         * readCount : 49
         * redirectUrl : http://120.25.56.153/publish/news/viewNews.do?newsId=828&token=
         * showtype : 1
         * source : 四歌
         * sourceicon : http://xmzjvip.b0.upaiyun.com/xmzj/63971492076259958.jpg
         * thumbsupCount : 0
         * title : 让你的群质量迅速提升3倍的社群维护技巧
         * typeId : 32
         * typeSign : 0
         * userCode : 762301550
         * videoUrl :
         */

        //专题
        private String pic;
        private String abstracts;
        private int isFee;
        private String price;
        private int subscribeSum;
        private Long updateTime;
        private String introduce;
//        private String isSubscribe;

        //news
        private String audit;
        private String commentCount;
        private String content;
        private String cover;
        private String date;
        private String desc;
        private String digest;
        private String id;
        private String isCollect;
        private String isFollow;
        private String isThumbsup;
        private String isVideo;
        private String keywords;
        private String readCount;
        private String redirectUrl;
        private String showtype;
        private String source;
        private String sourceicon;
        private String thumbsupCount;
        private String title;
        private String typeId;
        private String typeSign;
        private String userCode;
        private String videoUrl;

        public void setNewsData(NewsBean bean) {
            audit = bean.getAudit();
            commentCount = bean.getCommentCount();
            content = bean.getContent();
            cover = bean.getCover();
            date = bean.getDate();
            desc = bean.getDesc();
            digest = bean.getDigest();
            id = bean.getId();
            isCollect = bean.getIsCollect();
            isFollow = bean.getIsFollow();
            isThumbsup = bean.getIsThumbsup();
            isVideo = bean.getIsVideo();
            keywords = bean.getKeywords();
            readCount = bean.getReadCount();
            redirectUrl = bean.getRedirectUrl();
            showtype = bean.getShowtype();
            source = bean.getSource();
            sourceicon = bean.getSourceicon();
            thumbsupCount = bean.getThumbsupCount();
            title = bean.getTitle();
            typeId = bean.getTypeId();
            typeSign = bean.getTypeSign();
            userCode = bean.getUserCode();
            videoUrl = bean.getVideoUrl();
        }

        //tradeInfo
        private String anonymous;
        private String createTime;
        private String isTop;
        private String isfinish;
        private String liuYan;
        private String read;
        private String tradeType;
        private String tradeTypeSonId;
        private String tradeTypeSonName;
        private String userId;
        private String userName;
        private String userPic;

        //chatInfo
        private String         chatInfoCount;
        private String         chatRoom;
        private String         chatSeries;
        private String         chatRoomName;
        private SeriesPageBean chatSeriesData;
        private String         name;

//        public String getIsSubscribe() {
//            return isSubscribe;
//        }
//
//        public void setIsSubscribe(String isSubscribe) {
//            this.isSubscribe = isSubscribe;
//        }
//        public String getIntroduce() {
//            return introduce;
//        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getAbstracts() {
            return abstracts;
        }

        public void setAbstracts(String abstracts) {
            this.abstracts = abstracts;
        }

        public int getIsFee() {
            return isFee;
        }

        public void setIsFee(int isFee) {
            this.isFee = isFee;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getSubscribeSum() {
            return subscribeSum;
        }

        public void setSubscribeSum(int subscribeSum) {
            this.subscribeSum = subscribeSum;
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            this.updateTime = updateTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SeriesPageBean getChatSeriesData() {
            return chatSeriesData;
        }

        public void setChatSeriesData(SeriesPageBean chatSeriesData) {
            this.chatSeriesData = chatSeriesData;
        }

        public String getChatRoomName() {
            return chatRoomName;
        }

        public void setChatRoomName(String chatRoomName) {
            this.chatRoomName = chatRoomName;
        }

        private String chatSeriesCount;
        private String chatTypeSonId;
        private String chattype;
        private String chatway;
        private String endtime;
        private String facePic;
        private String fee;
        private String followCount;
        private String freetime;
        private String isBanned;
        private String isSelf;
        private String isSignup;
        private String isfree;
        private String joinCount;
        private String mainSpeaker;
        private String password;

        public String getChatRoomHeadPic() {
            return chatRoomHeadPic;
        }

        public void setChatRoomHeadPic(String chatRoomHeadPic) {
            this.chatRoomHeadPic = chatRoomHeadPic;
        }

        private String chatRoomHeadPic;
        private String pent;
        private String shareUrl;
        //        private String shareUsers;
        private String showShare;
        private String showSignup;
        private String showinfo;
        //        private String signupUsers;
        private String speakerIntroduce;
        private String starttime;
        private String status;
        private String subtitle;


        //ad
        private int            advContentId;
        private int            contentType;
        private String         type;
        private String         url;
        private NewsRespVoBean newsRespVo;
        private DealListBean   tradeInfoVo;
        private ChatInfosBean  chatInfoRespVo;

        public DealListBean getTradeInfoVo() {
            return tradeInfoVo;
        }

        public void setTradeInfoVo(DealListBean tradeInfoVo) {
            this.tradeInfoVo = tradeInfoVo;
        }

        public ChatInfosBean getChatInfoRespVo() {
            return chatInfoRespVo;
        }

        public void setChatInfoRespVo(ChatInfosBean chatInfoRespVo) {
            this.chatInfoRespVo = chatInfoRespVo;
        }

        public int getAdvContentId() {
            return advContentId;
        }

        public void setAdvContentId(int advContentId) {
            this.advContentId = advContentId;
        }

        public int getContentType() {
            return contentType;
        }

        public void setContentType(int contentType) {
            this.contentType = contentType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public NewsRespVoBean getNewsRespVo() {
            return newsRespVo;
        }

        public void setNewsRespVo(NewsRespVoBean newsRespVo) {
            this.newsRespVo = newsRespVo;
        }

        public static class NewsRespVoBean implements Serializable {

            private static final long serialVersionUID = 1L;

            @Override
            public String toString() {
                return "NewsRespVoBean{" + "audit='" + audit + '\'' + ", commentCount='" + commentCount + '\'' + ", content='" + content + '\'' + ", cover='" + cover + '\'' + ", date='" + date + '\'' + ", desc='" + desc + '\'' + ", digest='" + digest + '\'' + ", id='" + id + '\'' + ", isCollect='" + isCollect + '\'' + ", isFollow='" + isFollow + '\'' + ", isThumbsup='" + isThumbsup + '\'' + ", isVideo='" + isVideo + '\'' + ", keywords='" + keywords + '\'' + ", readCount='" + readCount + '\'' + ", redirectUrl='" + redirectUrl + '\'' + ", showtype='" + showtype + '\'' + ", source='" + source + '\'' + ", sourceicon='" + sourceicon + '\'' + ", thumbsupCount='" + thumbsupCount + '\'' + ", title='" + title + '\'' + ", typeId='" + typeId + '\'' + ", typeSign='" + typeSign + '\'' + ", userCode='" + userCode + '\'' + ", videoUrl='" + videoUrl + '\'' + '}';
            }

            /**
             * audit : 1
             * commentCount : 0
             * content :
             * cover : http://xmzjvip.b0.upaiyun.com/xmzj/54521491121093248.png!upyun300
             * date : 1491121639000
             * desc :
             * digest : 变现新招数
             * id : 745
             * isCollect : 0
             * isFollow : 0
             * isThumbsup : 0
             * isVideo : 0
             * keywords :
             * readCount : 4
             * redirectUrl : http://120.25.56.153/publish/news/viewNews.do?newsId=745&token=
             * showtype : 1
             * source : 潘子
             * sourceicon : http://xmzjvip.b0.upaiyun.com/xmzj/1490343236166
             * thumbsupCount : 0
             * title : 网易云音乐能引流还能变现？
             * typeId : 39
             * typeSign : 0
             * userCode : 333945500
             * videoUrl :
             */

            private String audit;
            private String commentCount;
            private String content;
            private String cover;
            private String date;
            private String desc;
            private String digest;
            private String id;
            private String isCollect;
            private String isFollow;
            private String isThumbsup;
            private String isVideo;
            private String keywords;
            private String readCount;
            private String redirectUrl;
            private String showtype;
            private String source;
            private String sourceicon;
            private String thumbsupCount;
            private String title;
            private String typeId;
            private String typeSign;
            private String userCode;
            private String videoUrl;


            public String getAudit() {
                return audit;
            }

            public void setAudit(String audit) {
                this.audit = audit;
            }

            public String getCommentCount() {
                return commentCount;
            }

            public void setCommentCount(String commentCount) {
                this.commentCount = commentCount;
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

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getDigest() {
                return digest;
            }

            public void setDigest(String digest) {
                this.digest = digest;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIsCollect() {
                return isCollect;
            }

            public void setIsCollect(String isCollect) {
                this.isCollect = isCollect;
            }

            public String getIsFollow() {
                return isFollow;
            }

            public void setIsFollow(String isFollow) {
                this.isFollow = isFollow;
            }

            public String getIsThumbsup() {
                return isThumbsup;
            }

            public void setIsThumbsup(String isThumbsup) {
                this.isThumbsup = isThumbsup;
            }

            public String getIsVideo() {
                return isVideo;
            }

            public void setIsVideo(String isVideo) {
                this.isVideo = isVideo;
            }

            public String getKeywords() {
                return keywords;
            }

            public void setKeywords(String keywords) {
                this.keywords = keywords;
            }

            public String getReadCount() {
                return readCount;
            }

            public void setReadCount(String readCount) {
                this.readCount = readCount;
            }

            public String getRedirectUrl() {
                return redirectUrl;
            }

            public void setRedirectUrl(String redirectUrl) {
                this.redirectUrl = redirectUrl;
            }

            public String getShowtype() {
                return showtype;
            }

            public void setShowtype(String showtype) {
                this.showtype = showtype;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getSourceicon() {
                return sourceicon;
            }

            public void setSourceicon(String sourceicon) {
                this.sourceicon = sourceicon;
            }

            public String getThumbsupCount() {
                return thumbsupCount;
            }

            public void setThumbsupCount(String thumbsupCount) {
                this.thumbsupCount = thumbsupCount;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTypeId() {
                return typeId;
            }

            public void setTypeId(String typeId) {
                this.typeId = typeId;
            }

            public String getTypeSign() {
                return typeSign;
            }

            public void setTypeSign(String typeSign) {
                this.typeSign = typeSign;
            }

            public String getUserCode() {
                return userCode;
            }

            public void setUserCode(String userCode) {
                this.userCode = userCode;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }
        }


        public String getAnonymous() {
            return anonymous;
        }

        public void setAnonymous(String anonymous) {
            this.anonymous = anonymous;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getIsTop() {
            return isTop;
        }

        public void setIsTop(String isTop) {
            this.isTop = isTop;
        }

        public String getIsfinish() {
            return isfinish;
        }

        public void setIsfinish(String isfinish) {
            this.isfinish = isfinish;
        }

        public String getLiuYan() {
            return liuYan;
        }

        public void setLiuYan(String liuYan) {
            this.liuYan = liuYan;
        }

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        public String getTradeTypeSonId() {
            return tradeTypeSonId;
        }

        public void setTradeTypeSonId(String tradeTypeSonId) {
            this.tradeTypeSonId = tradeTypeSonId;
        }

        public String getTradeTypeSonName() {
            return tradeTypeSonName;
        }

        public void setTradeTypeSonName(String tradeTypeSonName) {
            this.tradeTypeSonName = tradeTypeSonName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getChatInfoCount() {
            return chatInfoCount;
        }

        public void setChatInfoCount(String chatInfoCount) {
            this.chatInfoCount = chatInfoCount;
        }

        public String getChatRoom() {
            return chatRoom;
        }

        public void setChatRoom(String chatRoom) {
            this.chatRoom = chatRoom;
        }

        public String getChatSeries() {
            return chatSeries;
        }

        public void setChatSeries(String chatSeries) {
            this.chatSeries = chatSeries;
        }

        public String getChatSeriesCount() {
            return chatSeriesCount;
        }

        public void setChatSeriesCount(String chatSeriesCount) {
            this.chatSeriesCount = chatSeriesCount;
        }

        public String getChatTypeSonId() {
            return chatTypeSonId;
        }

        public void setChatTypeSonId(String chatTypeSonId) {
            this.chatTypeSonId = chatTypeSonId;
        }

        public String getChattype() {
            return chattype;
        }

        public void setChattype(String chattype) {
            this.chattype = chattype;
        }

        public String getChatway() {
            return chatway;
        }

        public void setChatway(String chatway) {
            this.chatway = chatway;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getFacePic() {
            return facePic;
        }

        public void setFacePic(String facePic) {
            this.facePic = facePic;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getFollowCount() {
            return followCount;
        }

        public void setFollowCount(String followCount) {
            this.followCount = followCount;
        }

        public String getFreetime() {
            return freetime;
        }

        public void setFreetime(String freetime) {
            this.freetime = freetime;
        }

        public String getIsBanned() {
            return isBanned;
        }

        public void setIsBanned(String isBanned) {
            this.isBanned = isBanned;
        }

        public String getIsSelf() {
            return isSelf;
        }

        public void setIsSelf(String isSelf) {
            this.isSelf = isSelf;
        }

        public String getIsSignup() {
            return isSignup;
        }

        public void setIsSignup(String isSignup) {
            this.isSignup = isSignup;
        }

        public String getIsfree() {
            return isfree;
        }

        public void setIsfree(String isfree) {
            this.isfree = isfree;
        }

        public String getJoinCount() {
            return joinCount;
        }

        public void setJoinCount(String joinCount) {
            this.joinCount = joinCount;
        }

        public String getMainSpeaker() {
            return mainSpeaker;
        }

        public void setMainSpeaker(String mainSpeaker) {
            this.mainSpeaker = mainSpeaker;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPent() {
            return pent;
        }

        public void setPent(String pent) {
            this.pent = pent;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

//        public String getShareUsers() {
//            return shareUsers;
//        }
//
//        public void setShareUsers(String shareUsers) {
//            this.shareUsers = shareUsers;
//        }

        public String getShowShare() {
            return showShare;
        }

        public void setShowShare(String showShare) {
            this.showShare = showShare;
        }

        public String getShowSignup() {
            return showSignup;
        }

        public void setShowSignup(String showSignup) {
            this.showSignup = showSignup;
        }

        public String getShowinfo() {
            return showinfo;
        }

        public void setShowinfo(String showinfo) {
            this.showinfo = showinfo;
        }

//        public String getSignupUsers() {
//            return signupUsers;
//        }
//
//        public void setSignupUsers(String signupUsers) {
//            this.signupUsers = signupUsers;
//        }

        public String getSpeakerIntroduce() {
            return speakerIntroduce;
        }

        public void setSpeakerIntroduce(String speakerIntroduce) {
            this.speakerIntroduce = speakerIntroduce;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getAudit() {
            return audit;
        }

        public void setAudit(String audit) {
            this.audit = audit;
        }

        public String getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(String commentCount) {
            this.commentCount = commentCount;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIsCollect() {
            return isCollect;
        }

        public void setIsCollect(String isCollect) {
            this.isCollect = isCollect;
        }

        public String getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(String isFollow) {
            this.isFollow = isFollow;
        }

        public String getIsThumbsup() {
            return isThumbsup;
        }

        public void setIsThumbsup(String isThumbsup) {
            this.isThumbsup = isThumbsup;
        }

        public String getIsVideo() {
            return isVideo;
        }

        public void setIsVideo(String isVideo) {
            this.isVideo = isVideo;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getReadCount() {
            return readCount;
        }

        public void setReadCount(String readCount) {
            this.readCount = readCount;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getShowtype() {
            return showtype;
        }

        public void setShowtype(String showtype) {
            this.showtype = showtype;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSourceicon() {
            return sourceicon;
        }

        public void setSourceicon(String sourceicon) {
            this.sourceicon = sourceicon;
        }

        public String getThumbsupCount() {
            return thumbsupCount;
        }

        public void setThumbsupCount(String thumbsupCount) {
            this.thumbsupCount = thumbsupCount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getTypeSign() {
            return typeSign;
        }

        public void setTypeSign(String typeSign) {
            this.typeSign = typeSign;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }
}





