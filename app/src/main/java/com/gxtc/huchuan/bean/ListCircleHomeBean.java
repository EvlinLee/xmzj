package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/5/10.
 * 资讯列表bean()
 */

public class ListCircleHomeBean implements Serializable {

    private static final long serialVersionUID = -763618847875650322L;
    @SerializedName("circleHomeBeen")
    private List<CircleHomeBean> circleHomeBeen;
    @SerializedName("circleBean")
    private List<CircleBean> circleBean;
    @SerializedName("mineCircleBeen")
    private List<MineCircleBean> mineCircleBeen;

    public List<MineCircleBean> getMineCircleBeen() {
        return mineCircleBeen;
    }

    public void setMineCircleBeen(List<MineCircleBean> mineCircleBeen) {
        this.mineCircleBeen = mineCircleBeen;
    }

    public List<CircleBean> getCircleBean() {
        return circleBean;
    }

    public void setCircleBean(List<CircleBean> circleBean) {
        this.circleBean = circleBean;
    }

    public List<CircleHomeBean> getCircleHomeBeen() {
        return circleHomeBeen;
    }

    public void setCircleHomeBeen(List<CircleHomeBean> circleHomeBeen) {
        this.circleHomeBeen = circleHomeBeen;
    }
}
