package com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Describe:
 * Created by ALing on 2017/6/1 .
 */

public interface PersonalInfoContract {
    interface View extends BaseUserView<PersonalInfoContract.Presenter> {
        void showUserInformation(PersonInfoBean bean);

        void showsaveLinkRemark(Object o);

        void showFollowSuccess();

        void showApplySuccess();
    }

    interface Presenter extends BasePresenter {
        void getUserInformation(HashMap<String,String> map);

        void saveLinkRemark(HashMap<String,String> map);

        void folowUser(String userCode);

        void applyFriends(String userCode,String message);

    }

    interface Source extends BaseSource {
        void getUserInformation(ApiCallBack<PersonInfoBean> callBack,HashMap<String,String> map);

        void setUserFocus(ApiCallBack<Object> callBack, String token, String followType, String bizId);

        void applyFriends(ApiCallBack<Object> callBack, String token, String followType, String bizId,String message);

        void saveLinkRemark(ApiCallBack<PersonInfoBean> callBack,HashMap<String,String> map);

    }
}
