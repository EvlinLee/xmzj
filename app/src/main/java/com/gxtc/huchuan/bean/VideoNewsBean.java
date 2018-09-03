package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * 来自 苏修伟 on 2018/5/8.
 * 首页视频集
 */
public class VideoNewsBean implements Serializable {

    private String title;
    private int id;
    private int setTop = 1;
    private String author;
    private long createTime;
    private String headPic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSetTop() {
        return setTop;
    }

    public void setSetTop(int setTop) {
        this.setTop = setTop;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
}
