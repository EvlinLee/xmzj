package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/3/15 .
 */

public class ChooseClassifyBean {

    /**
     * id : 18
     * typeName : a
     */

    private String id;
    private String typeName;
    private boolean isSelect;//是否选中
    private boolean isShow;//是否显示rb

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
