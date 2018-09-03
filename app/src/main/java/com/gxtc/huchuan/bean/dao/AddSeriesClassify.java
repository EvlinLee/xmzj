package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Describe:
 * Created by ALing on 2017/3/20 .
 */
@Entity
public class AddSeriesClassify {
    @Id
    private Long id;
    private String classifyName;

    @Generated(hash = 1595906912)
    public AddSeriesClassify(Long id, String classifyName) {
        this.id = id;
        this.classifyName = classifyName;
    }
    @Generated(hash = 1218105329)
    public AddSeriesClassify() {
    }
    public AddSeriesClassify(String classifyName) {
        this.classifyName = classifyName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getClassifyName() {
        return this.classifyName;
    }
    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }
}
