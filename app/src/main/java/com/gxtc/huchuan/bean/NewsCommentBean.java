package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/5/9.
 * 资讯评论实体bean
 */

public class NewsCommentBean implements Serializable{

    private static final long serialVersionUID = -763618247835550322L;
    /**
     * comment : 啊啊啊
     * createtime : 1494313173032
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/22321494252724999.jpg
     * id : 1548
     * name : 老实任
     * replys : []
     */

    private String comment;
    private String createtime;
    private String headPic;
    private String id;
    private String name;
    private String userCode;
//    private List<?> replys;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<?> getReplys() {
//        return replys;
//    }
//
//    public void setReplys(List<?> replys) {
//        this.replys = replys;
//    }
}
