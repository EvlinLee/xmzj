package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 2018/4/8.
 * 保存 需要邀请才能进入课堂的课程，解锁后是否已经进入过。
 * 苏修伟
 */

@Entity
public class FirstCurriculum {

    @Id
    private Long id;

    private String seriesOrtopId;

    @Generated(hash = 698455820)
    public FirstCurriculum(Long id, String seriesOrtopId) {
        this.id = id;
        this.seriesOrtopId = seriesOrtopId;
    }

    @Generated(hash = 1698248759)
    public FirstCurriculum() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeriesOrtopId() {
        return this.seriesOrtopId;
    }

    public void setSeriesOrtopId(String seriesOrtopId) {
        this.seriesOrtopId = seriesOrtopId;
    }

}
