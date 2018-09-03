package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/3/20 .
 */

public class EventLive {
    private String id;
    private String headpic;
    public EventLive(String headpic){
        this.headpic = headpic;
    }

    public EventLive() {
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
