package com.gxtc.huchuan.bean.pay;

import java.io.Serializable;

/**
 * Created by Steven on 16/12/26.
 */

public class YouZanCoustomBean implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * address : 广州
     * city : 广州
     * id : 1
     * isdefault : 0
     * name : 张三
     * phone : 13676267856
     * province : 广东
     */

    private String access_token;
    private String cookie_key;
    private String cookie_value;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCookie_key() {
        return cookie_key;
    }

    public void setCookie_key(String cookie_key) {
        this.cookie_key = cookie_key;
    }

    public String getCookie_value() {
        return cookie_value;
    }

    public void setCookie_value(String cookie_value) {
        this.cookie_value = cookie_value;
    }

    @Override
    public String toString() {
        return "YouZanCoustomBean{" +
                "access_token='" + access_token + '\'' +
                ", cookie_key='" + cookie_key + '\'' +
                ", cookie_value='" + cookie_value + '\'' +
                '}';
    }
}
