package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/6/7.
 */

public class FolderBean implements Serializable{

    /**
     * createTime : 1496836611000
     * fileNum : 0
     * folderName : test123456789012345
     * groupId : 93
     * id : 95
     * userId : 37
     */

    private long createTime;
    private int    fileNum;
    private String folderName;
    private int    groupId;
    private int    id;
    private int    userId;

    public long getCreateTime() { return createTime;}

    public void setCreateTime(long createTime) { this.createTime = createTime;}

    public int getFileNum() { return fileNum;}

    public void setFileNum(int fileNum) { this.fileNum = fileNum;}

    public String getFolderName() { return folderName;}

    public void setFolderName(String folderName) { this.folderName = folderName;}

    public int getGroupId() { return groupId;}

    public void setGroupId(int groupId) { this.groupId = groupId;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public int getUserId() { return userId;}

    public void setUserId(int userId) { this.userId = userId;}
}
