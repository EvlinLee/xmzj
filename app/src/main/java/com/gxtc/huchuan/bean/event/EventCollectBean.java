package com.gxtc.huchuan.bean.event;

/**
 * Created by sjr on 2017/3/3.
 * 收藏时刷新列表单个数据的单个字段
 */

public class EventCollectBean {
    public String newsId;//新闻id
    public String isCollect;//是否收藏

    public EventCollectBean(String newsId, String isCollect) {
        this.newsId = newsId;
        this.isCollect = isCollect;
    }
}
