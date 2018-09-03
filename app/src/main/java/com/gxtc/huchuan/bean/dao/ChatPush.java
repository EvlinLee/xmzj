package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gubr on 2017/3/15.
 * 课程推送 次数 保存
 */
@Entity
public class ChatPush {
    @Id
    private Long id;


    /**
     * 最后 推送的时间  保存到
     */
    private String date;


    /**
     * 推送次数
     */
    private int count;


    @Generated(hash = 450434780)
    public ChatPush(Long id, String date, int count) {
        this.id = id;
        this.date = date;
        this.count = count;
    }


    @Generated(hash = 563492952)
    public ChatPush() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getDate() {
        return this.date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public int getCount() {
        return this.count;
    }


    public void setCount(int count) {
        this.count = count;
    }
}
