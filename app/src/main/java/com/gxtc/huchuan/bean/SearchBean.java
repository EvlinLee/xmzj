package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.commlibrary.utils.GsonUtil;

import java.util.List;

/**
 * Created by Gubr on 2017/3/31.
 */
public class SearchBean {

    @SerializedName("type")
    private String type;

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    @SerializedName("searchKey")
    private String searchKey;

    @SerializedName("datas")
    private List<?> datas;


    private List datasbean;


    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public <T extends Object> List<T> getDatas() {
        if (datasbean == null) {
            Class claz = null;
            switch (getType()) {
                case "1"://新闻类型
                    claz = NewsBean.class;
                    break;
                case "2"://课程类型
                    claz= ChatInfosBean.class;
                    break;
                case "3"://课堂类型
                    claz=LiveRoomBean.class;
                    break;
                case "4"://担保交易
                    claz = DealListBean.class;
                    break;
                case "5"://
                    claz = MenberSearchBean.class;
                    break;
                case "6":
                    claz=CircleBean.class;
                    break;
                case "7":
                    claz=CircleHomeBean.class;
                    break;
            }

            String s = GsonUtil.objectToJson(datas);
            System.out.println(s);
            datasbean = GsonUtil.fromJsonArray(s, claz);
        }
        return (List<T>) datasbean;
    }


    public void setDatas(List<?> datas) { this.datas = datas;}





}
