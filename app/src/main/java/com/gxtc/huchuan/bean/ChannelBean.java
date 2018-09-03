package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sjr on 2017/2/10.
 * 频道实体bean（默认）
 */

public class ChannelBean {

    @SerializedName("default")
    private List<DefaultBean> defaultX;
    private List<NormalBean> normal;

    public List<DefaultBean> getDefaultX() {
        return defaultX;
    }

    public void setDefaultX(List<DefaultBean> defaultX) {
        this.defaultX = defaultX;
    }

    public List<NormalBean> getNormal() {
        return normal;
    }

    public void setNormal(List<NormalBean> normal) {
        this.normal = normal;
    }

    public static class DefaultBean {
        /**
         * newstypeId : 22
         * newstypeName : 科技
         */

        private int newstypeId;
        private String newstypeName;

        public int getNewstypeId() {
            return newstypeId;
        }

        public void setNewstypeId(int newstypeId) {
            this.newstypeId = newstypeId;
        }

        public String getNewstypeName() {
            return newstypeName;
        }

        public void setNewstypeName(String newstypeName) {
            this.newstypeName = newstypeName;
        }
    }

    public static class NormalBean {
        /**
         * newstypeId : 24
         * newstypeName : 数码
         */

        private int newstypeId;
        private String newstypeName;

        public NormalBean() {
        }

        public NormalBean(int newstypeId, String newstypeName) {
            this.newstypeId = newstypeId;
            this.newstypeName = newstypeName;
        }

        public int getNewstypeId() {
            return newstypeId;
        }

        public void setNewstypeId(int newstypeId) {
            this.newstypeId = newstypeId;
        }

        public String getNewstypeName() {
            return newstypeName;
        }

        public void setNewstypeName(String newstypeName) {
            this.newstypeName = newstypeName;
        }
    }
}
