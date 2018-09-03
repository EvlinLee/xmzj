package com.gxtc.huchuan.bean;


public class FastDealBean {

    /**
     * dbfee : 150
     * discount : 0
     * totle : 650
     * tradeAmt : 500
     * tradeInfoId : 1
     * tradeInfoTitle : 【此姑娘太疯狂】公司+个人订阅号斤35万粉，月流量主收入2.7万
     */

    private double dbfee;
    private double discount;
    private double totle;
    private double tradeAmt;
    private int tradeInfoId;
    private String tradeInfoTitle;
    private String picUrl;
    private int num;  //商品剩余数量

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getDbfee() {
        return dbfee;
    }

    public void setDbfee(double dbfee) {
        this.dbfee = dbfee;
    }
    public String getPicUrl(){
        return picUrl;
    }
    public void setPicUrl(String picUrl){
        this.picUrl = picUrl;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotle() {
        return totle;
    }

    public void setTotle(double totle) {
        this.totle = totle;
    }

    public double getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(double tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public int getTradeInfoId() {
        return tradeInfoId;
    }

    public void setTradeInfoId(int tradeInfoId) {
        this.tradeInfoId = tradeInfoId;
    }

    public String getTradeInfoTitle() {
        return tradeInfoTitle;
    }

    public void setTradeInfoTitle(String tradeInfoTitle) {
        this.tradeInfoTitle = tradeInfoTitle;
    }
}
