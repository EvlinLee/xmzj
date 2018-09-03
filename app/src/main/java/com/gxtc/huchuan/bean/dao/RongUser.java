package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gubr on 2017/2/21.
 * 已经没有用了
 */
@Entity
public class RongUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String name;
    private String headPic;
    private String sex;

    @Generated(hash = 1083111154)
    public RongUser(Long id, String name, String headPic, String sex) {
        this.id = id;
        this.name = name;
        this.headPic = headPic;
        this.sex = sex;
    }

    @Generated(hash = 1368628815)
    public RongUser() {
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return this.headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
