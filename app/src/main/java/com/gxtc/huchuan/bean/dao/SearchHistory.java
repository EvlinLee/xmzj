package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 搜索历史纪录
 * Created by Administrator on 2017/4/7.
 */
@Entity
public class SearchHistory {
    @Id(autoincrement = true)
    private Long id;

    private String searchContent;   //搜索内容

    @Generated(hash = 495444001)
    public SearchHistory(Long id, String searchContent) {
        this.id = id;
        this.searchContent = searchContent;
    }

    @Generated(hash = 1905904755)
    public SearchHistory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return this.searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }
}
