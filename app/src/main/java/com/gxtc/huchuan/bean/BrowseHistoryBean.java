package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/5/18 .
 */

public class BrowseHistoryBean {

    /**
     * browseId : 1094      浏览类型对应的id
     * browseType : 1       浏览类型  1：新闻文章，2：课程，3：交易信息，4：圈子
     * createtime : 1494838833000   浏览时间
     * facePic : http://xmzjvip.b0.upaiyun.com/xmzj/70711493272856803.png       封面
     * id : 1       记录id
     * title : 用微信赚程序员的钱，大家怎么看这种共享模式？       标题
     */

    private String browseId;
    private String browseType;
    private String createtime;
    private String facePic;
    private String id;
    private String title;
    private boolean isCheck;//是否选中
    private boolean isShow;//是否显示cb
    private String isDel;//0=正常，1=已删除

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getBrowseId() {
        return browseId;
    }

    public void setBrowseId(String browseId) {
        this.browseId = browseId;
    }

    public String getBrowseType() {
        return browseType;
    }

    public void setBrowseType(String browseType) {
        this.browseType = browseType;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getFacePic() {
        return facePic;
    }

    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
