package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DealTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;


    private int id;
    private String typeCode;
    private String typeName;
    private String isDefault;
    private String pic;

    /**
     * 这部分数据是自定义筛选条件的
     * choice : >
     * name : 流量
     * tradeField : udef2
     * udfType : 5
     * values : ["不限","100","500","1000","3000","5000"]
     */

    private String       choice;
    private String       name;
    private String       tradeField;
    private String       content;
    private int          udfType;
    private List<String> values;


    /**
     * 这部分数据是旧的
     */
    private int             currIndex;          //0 交易分类   1交易类型   2价格
    private List<TypesBean> prices;         //价格筛选
    private List<TypesBean> types;          //类型筛选
    private List<TypesBean> classifys;      //分类类型   0全部 1求购  2出售

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DealTypeBean() {

    }

    public int getCurrIndex() {
        return currIndex;
    }

    public void setCurrIndex(int currIndex) {
        this.currIndex = currIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<TypesBean> getPrices() {
        return prices;
    }

    public List<TypesBean> getClassifys() {
        if (classifys == null) {
            classifys = new ArrayList<>();
            classifys.add(new TypesBean("0", "全部"));
            classifys.add(new TypesBean("1", "求购"));
            classifys.add(new TypesBean("2", "出售"));
        }
        return classifys;
    }

    public void setClassifys(List<TypesBean> classifys) {
        this.classifys = classifys;
    }

    public void setPrices(List<TypesBean> prices) {
        this.prices = prices;
    }

    public List<TypesBean> getTypes() {
        return types;
    }

    public void setTypes(List<TypesBean> types) {
        this.types = types;
    }

    public String getChoice() { return choice;}

    public void setChoice(String choice) { this.choice = choice;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getTradeField() { return tradeField;}

    public void setTradeField(String tradeField) { this.tradeField = tradeField;}

    public int getUdfType() { return udfType;}

    public void setUdfType(int udfType) { this.udfType = udfType;}

    public List<String> getValues() { return values;}

    public void setValues(List<String> values) {
        this.values = values;
        List<TypesBean> temp = new ArrayList<>();
        for (String s : values) {
            TypesBean bean = new TypesBean();
            bean.setCode(getTradeField() + "," + getUdfType());
            bean.setTitle(s);
            bean.setChoice(getChoice());
            temp.add(bean);
        }
        setTypes(temp);
    }


    public static class TypesBean {
        /**
         * code : 0
         * title : 全部类型
         */

        private String  code;
        private String  title;
        private String  choice;        //条件符号   = < >
        private boolean isSelect;

        public TypesBean() {
        }

        public TypesBean(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
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
    }


}
