package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by sjr on 2017/2/16.
 * 资讯实体
 */

public class NewsBean implements Serializable {
    private static final long serialVersionUID = -763618247875550322L;


    /**
     *
     * audit: "0"
     * commentCount : 5
     * content :
     * cover : http://xmzjvip.b0.upaiyun.com/xmzj/35631489565917973.png
     * date : 03-07
     * desc :
     * digest : 你是我梦中的女孩，当然我也是你梦中的男孩
     * id : 67
     * isCollect : 0
     * isFollow : 0
     * isThumbsup : 0
     * keywords :
     * readCount : 0
     * redirectUrl : http://120.25.56.153/publish/news/viewNews.do?newsId=67&token=
     * showtype : 1
     * source : 新媒之家
     * sourceicon : http://xmzjvip.b0.upaiyun.com/xmzj/44001489545493826.png
     * thumbsupCount : 0
     * title :  一旦笃定，就是那个人了
     * typeId : 31
     * typeSign : 1
     */
    private String audit;       //审核标记 0：新提交，未审核；1：审核通过；2：审核不通过
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
    private String isVideo;//新闻类型。0和1  0：咨询，1：视频
    private String joinGroup;
    private String userCode;
    private String videoUrl;
    private String ignoreContent;       //富文本的原内容
    private boolean isCheck;//是否选中
    private boolean isShow;//是否显示cb

    private int isDeploy ;  //1草稿，0正式提交

    public String getIgnoreContent() {
        return ignoreContent;
    }

    public void setIgnoreContent(String ignoreContent) {
        this.ignoreContent = ignoreContent;
    }

    public int getIsDeploy() {
        return isDeploy;
    }

    public void setIsDeploy(int isDeploy) {
        this.isDeploy = isDeploy;
    }

    public String getJoinGroup() {
        return joinGroup;
    }

    public void setJoinGroup(String joinGroup) {
        this.joinGroup = joinGroup;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(String isVideo) {
        this.isVideo = isVideo;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsBean newsBean = (NewsBean) o;
        return id.equals(newsBean.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "audit='" + audit + '\'' +
                ", commentCount='" + commentCount + '\'' +
                ", content='" + content + '\'' +
                ", cover='" + cover + '\'' +
                ", date='" + date + '\'' +
                ", desc='" + desc + '\'' +
                ", digest='" + digest + '\'' +
                ", id='" + id + '\'' +
                ", isCollect='" + isCollect + '\'' +
                ", isFollow='" + isFollow + '\'' +
                ", isThumbsup='" + isThumbsup + '\'' +
                ", keywords='" + keywords + '\'' +
                ", readCount='" + readCount + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", showtype='" + showtype + '\'' +
                ", source='" + source + '\'' +
                ", sourceicon='" + sourceicon + '\'' +
                ", thumbsupCount='" + thumbsupCount + '\'' +
                ", title='" + title + '\'' +
                ", typeId='" + typeId + '\'' +
                ", typeSign='" + typeSign + '\'' +
                ", isVideo='" + isVideo + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", isCheck=" + isCheck +
                ", isShow=" + isShow +
                '}';
    }
}
