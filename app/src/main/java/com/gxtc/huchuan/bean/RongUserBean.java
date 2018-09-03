package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/2/23.
 */

public class RongUserBean implements Serializable {
    String userid;
    String name;
    String token;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RongUserBean(String userid, String name, String token) {

        this.userid = userid;
        this.name = name;
        this.token = token;
    }
}
