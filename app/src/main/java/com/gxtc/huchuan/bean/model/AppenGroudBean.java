package com.gxtc.huchuan.bean.model;

import com.google.gson.annotations.SerializedName;
import com.gxtc.huchuan.bean.pay.AccountSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gubr on 2017/5/4.
 */

public class AppenGroudBean {
    /**
     * userPercent : 0
     * accountSet : []
     */

 private ArrayList<Integer>          groupIds;

    public ArrayList<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<Integer> groupIds) {
        this.groupIds = groupIds;
    }
}
