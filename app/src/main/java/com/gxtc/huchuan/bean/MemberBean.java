package com.gxtc.huchuan.bean;

/**
 * Created by Gubr on 2017/3/17.
 */

public class MemberBean {
    private String name;
    private String type;

    public MemberBean(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
