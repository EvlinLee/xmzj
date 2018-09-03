package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */

public class MallBean implements Serializable {
    private String id;
    private String type;//类型0：商品；1：URL链接 2,分类列表
    private String cover;
    private String data;
    private String name;//名称
    private String price;//价格
    private String picUrl;//菜单图片
    private String ratio;
    private int picUrlX;//图片宽
    private int picUrlY;//图片高
    private ArrayList<PriceList> priceList;

    public ArrayList<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(ArrayList<PriceList> priceList) {
        this.priceList = priceList;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public int getPicUrlX() {
        return picUrlX;
    }

    public void setPicUrlX(int picUrlX) {
        this.picUrlX = picUrlX;
    }

    public int getPicUrlY() {
        return picUrlY;
    }

    public void setPicUrlY(int picUrlY) {
        this.picUrlY = picUrlY;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public class PriceList implements Serializable{

        private int id;
        private int storeId;
        private int sum;
        private String name;
        private String price;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStoreId() {
            return storeId;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

}
