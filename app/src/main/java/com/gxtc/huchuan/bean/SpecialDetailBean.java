package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Created by laoshiren on 2018/5/24.
 * 专题详情
 */
public class SpecialDetailBean {


    /**
     * isFee : 0
     * isPay : 1
     * abstracts : 测试专题2
     * author : {"name":"Trevet_","pic":"https://xmzjvip.b0.upaiyun.com/xmzj/24631497857771857.gif","userCode":"Trevet"}
     * introduce : 专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍
     * subscribeSum :
     * recommend : 0
     * updateTime : 1527064775000
     * label : [{"name":"标签2","id":2},{"name":"标签3","id":3},{"name":"标签1","id":1}]
     * pic : http://xmzjvip.b0.upaiyun.com/admin/newsSpecial/2018/05/23/com.xinmei6.app_163852.jpg
     * top : 1
     * typeByListCss : 0
     * price : 11.1
     * name : 测试专题1_免费
     * id : 1
     */

    private int isFee;
    private String isPay;
    private String abstracts;
    private AuthorBean author;
    private String introduce;
    private String subscribeSum;
    private int recommend;
    private long updateTime;
    private String pic;
    private int top;
    private int typeByListCss;
    private double price;
    private String name;
    private int id;
    private List<LabelBean> label;

    public int getIsFee() {
        return isFee;
    }

    public void setIsFee(int isFee) {
        this.isFee = isFee;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public AuthorBean getAuthor() {
        return author;
    }

    public void setAuthor(AuthorBean author) {
        this.author = author;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getSubscribeSum() {
        return subscribeSum;
    }

    public void setSubscribeSum(String subscribeSum) {
        this.subscribeSum = subscribeSum;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getTypeByListCss() {
        return typeByListCss;
    }

    public void setTypeByListCss(int typeByListCss) {
        this.typeByListCss = typeByListCss;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<LabelBean> getLabel() {
        return label;
    }

    public void setLabel(List<LabelBean> label) {
        this.label = label;
    }

    public static class AuthorBean {
        /**
         * name : Trevet_
         * pic : https://xmzjvip.b0.upaiyun.com/xmzj/24631497857771857.gif
         * userCode : Trevet
         */

        private String name;
        private String pic;
        private String userCode;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
    }

    public static class LabelBean {
        /**
         * name : 标签2
         * id : 2
         */

        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
