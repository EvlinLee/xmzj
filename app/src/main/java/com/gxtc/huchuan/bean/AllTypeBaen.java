package com.gxtc.huchuan.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 17/4/6.
 */

public class AllTypeBaen {


    /**
     * code : 5
     * son : [{"code":"3","title":"行业门户"},{"code":"4","title":"音乐影视"},{"code":"5","title":"游戏小说"},{"code":"6","title":"女性时尚"},{"code":"7","title":"QQ/娱乐"},{"code":"8","title":"商城购物"}]
     * title : 网站交易
     * udef : [{"code":"udef2","title":"流量"},{"code":"udef3","title":"PR值"},{"code":"udef4","title":"价格"},{"code":"udef1","title":"网站域名"},{"title":"测试"}]
     */
    private int pattern;        //是否是交易模式  0  交易模式  1是论坛模式
    private int code;
    private String title;
    private List<SonBean> son;
    private List<UdefBean> udef;

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SonBean> getSon() {
        if(son == null){
            return new ArrayList<>();
        }
        return son;
    }

    public void setSon(List<SonBean> son) {
        this.son = son;
    }

    public List<UdefBean> getUdef() {
        return udef;
    }

    public void setUdef(List<UdefBean> udef) {
        this.udef = udef;
    }

    public static class SonBean {
        /**
         * code : 3
         * title : 行业门户
         */

        private String code;
        private String title;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class UdefBean {
        /**
         * code : udef2
         * title : 流量
         */

        private int udfType;
        private String code;
        private String title;
        private String content;
        private List<String> values;
        private List<Entity> entity;
        private boolean isSelect;

        public List<Entity> getEntity() {
            return entity;
        }

        public void setEntity(List<String> values) {
            if(values == null) return;
            List<Entity> entities = new ArrayList<>();
            for(String s : values){
                Entity entity = new Entity();
                entity.setCode(getCode());
                entity.setTitle(s);
                entity.setType(getUdfType());
                entities.add(entity);
            }
            this.entity = entities;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public int getUdfType() {
            return udfType;
        }

        public void setUdfType(int udfType) {
            this.udfType = udfType;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public static class Entity{
            private String code;
            private String title;
            private int type;
            private boolean isSelect;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }
        }
    }
}
