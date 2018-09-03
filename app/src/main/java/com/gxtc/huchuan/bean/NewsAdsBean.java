package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/2/15.
 * 轮播图实体bean
 */

public class NewsAdsBean implements Serializable {

    private static final long serialVersionUID = -763328247878760554L;
    /**
     * advContentId : 9
     * contentType : 0
     * cover : http://xmzjvip.b0.upaiyun.com/xmzj/11271489720036596.jpg
     * id : 20
     * title : 资讯广告er
     * type : 02
     * url : www.baidu.com
     * newsRespVo : {"audit":"1","commentCount":"0","content":"","cover":"http://xmzjvip.b0.upaiyun.com/xmzj/54521491121093248.png!upyun300","date":"1491121639000","desc":"","digest":"变现新招数","id":"745","isCollect":"0","isFollow":"0","isThumbsup":"0","isVideo":"0","keywords":"","readCount":"4","redirectUrl":"http://120.25.56.153/publish/news/viewNews.do?newsId=745&token=","showtype":"1","source":"潘子","sourceicon":"http://xmzjvip.b0.upaiyun.com/xmzj/1490343236166","thumbsupCount":"0","title":"网易云音乐能引流还能变现？","typeId":"39","typeSign":"0","userCode":"333945500","videoUrl":""}
     */

    private int advContentId;
    private int contentType;
    private String cover;
    private int id;
    private String title;
    private String type;
    private String url;
    private String chatType;
    private NewsRespVoBean newsRespVo;
    private DealListBean tradeInfoVo;
    private ChatInfosBean chatInfoRespVo;
    private List<NewsAdsBean> datas;


    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public List<NewsAdsBean> getDatas() {
        return datas;
    }

    public void setDatas(List<NewsAdsBean> datas) {
        this.datas = datas;
    }

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static class NewsRespVoBean implements Serializable{

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

}
