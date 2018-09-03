package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Steven on 17/3/29.
 */

public class DealData implements Serializable{

    private static final long serialVersionUID = 1L;

    private String tradeOrderNum;       //交易数
    private String tradeInfoNum;        //帖子数

    private List<DealListBean> infos;

    private List<DealTypeBean> types;

    public DealData() {
    }


    public String getTradeOrderNum() {
        return tradeOrderNum;
    }

    public void setTradeOrderNum(String tradeOrderNum) {
        this.tradeOrderNum = tradeOrderNum;
    }

    public String getTradeInfoNum() {
        return tradeInfoNum;
    }

    public void setTradeInfoNum(String tradeInfoNum) {
        this.tradeInfoNum = tradeInfoNum;
    }

    public List<DealListBean> getInfos() {
        return infos;
    }

    public void setInfos(List<DealListBean> infos) {
        this.infos = infos;
    }

    public List<DealTypeBean> getTypes() {
        return types;
    }

    public void setTypes(List<DealTypeBean> types) {
        this.types = types;
    }
}
