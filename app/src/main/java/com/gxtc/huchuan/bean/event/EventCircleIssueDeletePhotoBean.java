package com.gxtc.huchuan.bean.event;

import android.net.Uri;

import java.util.List;

/**
 * Created by sjr on 2017/5/20.
 * 发表动态时删除图片
 */

public class EventCircleIssueDeletePhotoBean {
    public static List<Uri> datas;

    public EventCircleIssueDeletePhotoBean(List<Uri> datas) {
        this.datas = datas;
    }
}
