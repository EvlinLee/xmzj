package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjr on 2017/5/2.
 * 圈子首页
 * 2017/5/10
 * bean跟圈子动态一样，可以复用
 */

public class CircleHomeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * content : 发表测试帖子1
     * createTime : 1493351875000
     * dianZan : 0
     * id : 6
     * isDZ : 0
     * liuYan : 0
     * picList : [{"id":8,"picUrl":"http://xmzjvip.b0.upaiyun.com/xmzj/70711493272856803.png"},{"id":9,"picUrl":"http://xmzjvip.b0.upaiyun.com/xmzj/70711493272856803.png"}]
     * title : 测试帖子1
     * type : 3
     * url :
     * userCode : 279666917
     * userName : 279666917
     * userPic :
     * videoUrl :
     */

    //用来展开与收起展示标签
    public boolean tagFlag = false;

    private String content;
    private long createTime;
    private int dianZan;
    private int id;
    private int isDZ;           //当前用户是否已点赞。0：否、1：是
    private int isMy;//0、不是；1、是
    private int liuYan;
    private String title;
    private int type;
    private int isCollect;
    private String url;
    private String userCode;
    private String userName;
    private String groupId;
    private String groupName;
    private String userPic;
    private String videoPic;     //视频封面
    private String videoUrl;
    private int infoType;        //0:普通动态，1：新闻连接，2：课程，3：交易,4：圈子
    private String typeId;          //转发内容ID
    private String typeTitle;       //转发内容标题
    private String typeCover;       //转发内容封面
    private int isAttention;//是否关注 0、未关注；1、已关注
    private int isGood;//是否是精华。0、不是；1、是
    private int isTop;//是否置顶。0、否；1、是
    private int isAlsdinfo;//是否允许动态同步  0=允许，1=不允许
    private List<PicListBean> picList;
    private List<PicListBean> groupPicVos;
    private List<CircleCommentBean> commentVos;
    private ArrayList<ThumbsupVosBean> thumbsupVos;

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public List<PicListBean> getGroupPicVos() {
        return groupPicVos;
    }

    public void setGroupPicVos(List<PicListBean> groupPicVos) {
        this.groupPicVos = groupPicVos;
    }

    public int getIsAlsdinfo() {
        return isAlsdinfo;
    }

    public void setIsAlsdinfo(int isAlsdinfo) {
        this.isAlsdinfo = isAlsdinfo;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
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

    public int getInfoType() {
        return infoType;
    }

    public void setInfoType(int infoType) {
        this.infoType = infoType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTypeCover() {
        return typeCover;
    }

    public void setTypeCover(String typeCover) {
        this.typeCover = typeCover;
    }

    public ArrayList<ThumbsupVosBean> getThumbsupVos() {
        return thumbsupVos;
    }

    public int getIsGood() {
        return isGood;
    }

    public void setIsGood(int isGood) {
        this.isGood = isGood;
    }

    public void setThumbsupVos(ArrayList<ThumbsupVosBean> thumbsupVos) {
        this.thumbsupVos = thumbsupVos;
    }

    public List<CircleCommentBean> getCommentVos() {
        return commentVos;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public void setCommentVos(List<CircleCommentBean> commentVos) {
        this.commentVos = commentVos;
    }

    private boolean isCheck;//是否选中
    private boolean isShow;//是否显示cb

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
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

    public int getDianZan() {
        return dianZan;
    }

    public void setDianZan(int dianZan) {
        this.dianZan = dianZan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsDZ() {
        return isDZ;
    }

    public void setIsDZ(int isDZ) {
        this.isDZ = isDZ;
    }

    public int getLiuYan() {
        return liuYan;
    }

    public void setLiuYan(int liuYan) {
        this.liuYan = liuYan;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<PicListBean> getPicList() {
        return picList;
    }

    public void setPicList(List<PicListBean> picList) {
        this.picList = picList;
    }


    public static class PicListBean implements Serializable {

        private static final long serialVersionUID = 2L;
        /**
         * id : 8
         * picUrl : http://xmzjvip.b0.upaiyun.com/xmzj/70711493272856803.png
         */

        private int id;
        private String picUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleHomeBean that = (CircleHomeBean) o;

        return id == that.id;

    }

    @Override
    public String toString() {
        return "CircleHomeBean{" + "content='" + content + '\'' + ", createTime=" + createTime + ", dianZan=" + dianZan + ", id=" + id + ", isDZ=" + isDZ + ", isMy=" + isMy + ", liuYan=" + liuYan + ", title='" + title + '\'' + ", type=" + type + ", url='" + url + '\'' + ", userCode='" + userCode + '\'' + ", userName='" + userName + '\'' + ", userPic='" + userPic + '\'' + ", videoUrl='" + videoUrl + '\'' + ", picList=" + picList + ", commentVos=" + commentVos + ", thumbsupVos=" + thumbsupVos + ", isCheck=" + isCheck + ", isShow=" + isShow + '}';
    }
}
