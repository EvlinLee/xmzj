package com.gxtc.huchuan.bean;

import com.gxtc.huchuan.bean.pay.YouZanCoustomBean;

import java.io.Serializable;

/**
 * Created by Steven on 16/12/26.
 */

public class YouZanBean implements Serializable{

    private static final long serialVersionUID = 1L;


    private String code;
    private String msg;
    private YouZanCoustomBean data;

    public YouZanCoustomBean getData() {
        return data;
    }

    public void setData(YouZanCoustomBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "YouZanBean{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
