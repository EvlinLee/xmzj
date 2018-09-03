package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/5 .
 */

public class PhotoBean {
    private List<PhotoBean> photoList;
    String photoUrl;

    public PhotoBean() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<PhotoBean> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoBean> photoList) {
        this.photoList = photoList;
    }
}

