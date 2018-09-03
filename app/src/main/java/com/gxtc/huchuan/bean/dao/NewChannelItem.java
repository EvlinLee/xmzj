package com.gxtc.huchuan.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by sjr on 2017/2/10.
 */
@Entity
public class NewChannelItem implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6465237897027410019L;
    @Id(autoincrement = true)
    public Long listId;

    /**
     * 栏目对应ID
     */
    public Integer id;
    /**
     * 栏目对应NAME
     */
    public String name;
    /**
     * 栏目在整体中的排序顺序  rank
     */
    public Integer orderId;
    /**
     * 栏目是否选中
     */
    public Integer selected;

    @Generated(hash = 390923673)
    public NewChannelItem(Long listId, Integer id, String name, Integer orderId,
                          Integer selected) {
        this.listId = listId;
        this.id = id;
        this.name = name;
        this.orderId = orderId;
        this.selected = selected;
    }

    /**
     *
     * @param id 栏目对应ID
     * @param name 栏目对应NAME
     * @param orderId 栏目在整体中的排序顺序
     * @param selected 栏目是否选中
     */
    public NewChannelItem(Integer id, String name, Integer orderId,
                          Integer selected) {

        this.id = id;
        this.name = name;
        this.orderId = orderId;
        this.selected = selected;
    }

    @Generated(hash = 241022574)
    public NewChannelItem() {
    }

    public Long getListId() {
        return this.listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getSelected() {
        return this.selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }


}

