package com.gxtc.huchuan.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by sjr on 2017/5/15.
 */

public class IssueDynamicBean implements Serializable {
    private static final long serialVersionUID = -763618847875650332L;


    private File file;
    private int imgWide;
    private int imgHeight;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImgWide() {
        return imgWide;
    }

    public void setImgWide(int imgWide) {
        this.imgWide = imgWide;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
