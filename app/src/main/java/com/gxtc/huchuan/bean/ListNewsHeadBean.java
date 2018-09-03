package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/5/24.
 */

public class ListNewsHeadBean implements Serializable {

    private static final long serialVersionUID = -763618847875650321L;
    @SerializedName("advertise")
    private List<NewsAdsBean> advertise;

    public List<NewsAdsBean> getAdvertise() {
        return advertise;
    }

    public void setAdvertise(List<NewsAdsBean> advertise) {
        this.advertise = advertise;
    }
}
