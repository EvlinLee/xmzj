package com.gxtc.huchuan.ui.circle.groupmember;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/6/9 .
 */

public class GroupRulePresenter implements GroupRuleContract.Presenter {

    private CircleSource         mData;
    private GroupRuleContract.View mView;

    private int id;
    private int start = 0;

    public GroupRulePresenter(GroupRuleContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mData = new CircleRepository();

    }


    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }

    @Override
    public void getGroupRule(int groupId) {
        mView.showLoad();
        mData.getGroupRule(groupId, new ApiCallBack<GroupRuleBean>() {
            @Override
            public void onSuccess(GroupRuleBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showGroupRule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void saveGroupRule(HashMap<String, Object> map) {
        mView.showLoad();
        mData.saveGroupRule(map, new ApiCallBack<GroupRuleBean>() {
            @Override
            public void onSuccess(GroupRuleBean data) {
                if(mView == null) return;
                mView.showLoadFinish();
                mView.showSaveGroupRule(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }
}
