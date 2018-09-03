package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.huchuan.bean.NewNewsBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/5/10.
 * 资讯列表bean()
 */

public class ListNewNewsBean implements Serializable {

    private static final long serialVersionUID = -763618847875650322L;
    @SerializedName("newNewsBeen")
    private List<NewNewsBean> newNewsBeen;

    public List<NewNewsBean> getNewNewsBeen() {
        return newNewsBeen;
    }

    public void setNewNewsBeen(List<NewNewsBean> newNewsBeen) {
        this.newNewsBeen = newNewsBeen;
    }
}
