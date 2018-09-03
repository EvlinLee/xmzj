package com.gxtc.huchuan.bean.event;

/**
 * Created by sjr on 2017/3/3.
 * 点赞时刷新列表单个数据的单个字段
 */

public class EventThumbsupBean {
    public String newsId;//新闻id
    public String isThumbsup;//是否点赞

    public EventThumbsupBean(String newsId,  String isThumbsup) {
        this.newsId = newsId;
        this.isThumbsup = isThumbsup;
    }
}
