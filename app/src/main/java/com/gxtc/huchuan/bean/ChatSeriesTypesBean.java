package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/3/13.
 */
public class ChatSeriesTypesBean implements Serializable {

    private static final long serialVersionUID = -763618247875560552L;

    /**
     * id : 9
     * typeName : 开发
     */

    private String id;
    private String typeName;

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
}
