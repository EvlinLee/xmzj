package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class DistributionCountBean {

    /**
     * count : 1
     * DistributionCountBean :0
     */

    private int count;           //分销数量

    private String sum;

    private String incomeMoney;     //分销收益统计数

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(String incomeMoney) {
        this.incomeMoney = incomeMoney;
    }
}
