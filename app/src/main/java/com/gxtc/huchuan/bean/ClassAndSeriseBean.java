package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.GsonUtil;

import java.util.List;

/**
 * Created by zzg on 2017/11/20.
 */

public class ClassAndSeriseBean<T> {
    private String type;

    public T data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
