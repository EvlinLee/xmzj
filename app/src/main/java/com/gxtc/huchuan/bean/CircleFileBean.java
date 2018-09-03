package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/5/13.
 */
@Entity
public class CircleFileBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * createTime : 1494237539000
     * fileName : 文件测试1
     * fileSize : 15036345
     * fileType : 5
     * fileUrl : http://xmzjvip.b0.upaiyun.com/xmzj/16941490338104974.mp4
     * id : 1
     * userCode : 821705443
     * userName : 太极
     */
    @Id(autoincrement = false) @SerializedName("id") public Long id;

    @SerializedName("fileType") private   int    fileType;
    @SerializedName("fileSize") private   long   fileSize;
    @SerializedName("createTime") private long   createTime;
    @SerializedName("fileName") private   String fileName;
    @SerializedName("fileUrl") private    String fileUrl;
    @SerializedName("userCode") private   String userCode;
    @SerializedName("userName") private   String userName;
    @SerializedName("folderId") private   Long   folderId;

    private long loadId;

    @Generated(hash = 149080460)
    public CircleFileBean(Long id, int fileType, long fileSize, long createTime, String fileName, String fileUrl, String userCode, String userName, Long folderId, long loadId) {
        this.id = id;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createTime = createTime;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.userCode = userCode;
        this.userName = userName;
        this.folderId = folderId;
        this.loadId = loadId;
    }

    @Generated(hash = 257274173)
    public CircleFileBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileType() {
        return this.fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getLoadId() {
        return this.loadId;
    }

    public void setLoadId(long loadId) {
        this.loadId = loadId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    @Override
    public String toString() {
        return "CircleFileBean{" + "id=" + id + ", createTime=" + createTime + ", fileName='" + fileName + '\'' + ", fileSize=" + fileSize + ", fileType=" + fileType + ", fileUrl='" + fileUrl + '\'' + ", userCode='" + userCode + '\'' + ", userName='" + userName + '\'' + ", folderId=" + folderId + ", loadId=" + loadId + '}';
    }
}
