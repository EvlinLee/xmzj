package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe:
 * Created by ALing on 2017/5/18.
 */

public class PersonalDymicBean implements Serializable{

    /**
     * content : bhjj<br><br><br><br><br>gjj<br><br><br><br><br>ghn<br><br>jk<br><br><br><br><br><br>bjjj<br><br>hjj<br><br><br>gh<br><br><br><br>
     * date : 1495611442000
     * id : 44
     * isDZ : 0
     * replyContent :
     * title : 这么长的文本怎么显示
     * type : 0
     * url : http://app.xinmei6.com/html/circleNews.html?infoId=44&token=
     * userCode : 571904840
     * userName : 472823
     * userPic : http://xmzjvip.b0.upaiyun.com/xmzj/96541493803989807.jpeg
     * picUrl :
     */

    private String content;
    private String date;
    private String id;
    private String isDZ;
    private String replyContent;
    private String title;
    private String type;
    private String url;
    private String userCode;
    private String userName;
    private String userPic;
    private String picUrl;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

    public String getDate() { return date;}

    public void setDate(String date) { this.date = date;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public String getIsDZ() { return isDZ;}

    public void setIsDZ(String isDZ) { this.isDZ = isDZ;}

    public String getReplyContent() { return replyContent;}

    public void setReplyContent(String replyContent) { this.replyContent = replyContent;}

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public String getUserPic() { return userPic;}

    public void setUserPic(String userPic) { this.userPic = userPic;}
}
