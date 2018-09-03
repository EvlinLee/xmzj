package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/3/27 .
 */

public class EventChooseClassifyBean {
    private String typeId; //系列课分类ID
    private String typeName;

    public EventChooseClassifyBean(String typeId,String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
