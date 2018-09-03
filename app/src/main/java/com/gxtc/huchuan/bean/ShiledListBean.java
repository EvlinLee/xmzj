package com.gxtc.huchuan.bean;

/**
 * Created by 宋家任 on 2017/6/7.
 */

public class ShiledListBean {

    /**
     * createTime : 1496672622000
     * id : 6
     * targetCode : 948399748
     * targetName : 夜猫
     * targetPic : http://xmzjvip.b0.upaiyun.com/xmzj/1490343577953
     * userId : 16
     */

    private long createTime;
    private int id;
    private String targetCode;
    private String targetName;
    private String targetPic;
    private int userId;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetPic() {
        return targetPic;
    }

    public void setTargetPic(String targetPic) {
        this.targetPic = targetPic;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
