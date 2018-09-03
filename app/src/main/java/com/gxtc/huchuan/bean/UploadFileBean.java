package com.gxtc.huchuan.bean;

/**
 * Created by Steven on 17/3/9.
 */

public class UploadFileBean {

    private String fileUrl ;

    public UploadFileBean() {
    }

    public UploadFileBean(String url) {
        this.fileUrl = url;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
