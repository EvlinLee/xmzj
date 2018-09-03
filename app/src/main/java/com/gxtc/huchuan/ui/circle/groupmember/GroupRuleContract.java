package com.gxtc.huchuan.ui.circle.groupmember;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.huchuan.bean.GroupRuleBean;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/6/9 .
 */

interface GroupRuleContract {
    interface View extends BaseUserView<Presenter> {
        void showGroupRule(GroupRuleBean data);
        void showSaveGroupRule(GroupRuleBean data);
    }

    interface Presenter extends BasePresenter{
        void getGroupRule(int groupId);
        void saveGroupRule(HashMap<String,Object> map);
    }
}
