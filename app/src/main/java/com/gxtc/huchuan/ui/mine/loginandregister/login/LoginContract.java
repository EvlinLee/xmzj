package com.gxtc.huchuan.ui.mine.loginandregister.login;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by ALing on 2017/2/8.
 */

public class LoginContract {
    public interface View extends BaseUiView<LoginContract.Presenter> {
        void showLogin(User bean);
        void thirdLoginResult(User bean);
        void thirdLoginBindPhone();
        void bindPhoneResult();
    }

    //prenster接口
    public interface Presenter extends BasePresenter {
        void getLogin(String count,String psw);

        void getThirdLogin(HashMap<String,String> map);

    }

    //model层接口
    public interface Source extends BaseSource {
        void getLogin(ApiCallBack<User> callBack, String count,String psw);

        void getThirdLogin(ApiCallBack<User> callBack, HashMap<String,String> map);

    }
}
