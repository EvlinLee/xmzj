package com.gxtc.huchuan.bean.event;

import com.gxtc.huchuan.bean.UploadPPTFileBean;

import java.util.List;

/**
 * Created by sjr on 2017/4/11.
 */

public class EventPPTBean {
    public List<UploadPPTFileBean> datas;

    public EventPPTBean(List<UploadPPTFileBean> datas) {
        this.datas = datas;
    }
}
