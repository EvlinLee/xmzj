package com.gxtc.huchuan.ui.mine.loginandregister.register;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ALing on 2017/2/13.
 */

public class RegisterContract {
    //view层接口
    public interface View extends BaseUiView<RegisterContract.Presenter> {
        void showRegisterSuccess(User bean);
        void  showValidationCode(Object object);
        void  showCountdown();
        void EditInfoSuccess(Object object);
    }

    //prenster接口
    public interface Presenter extends BasePresenter {
        void getRegister(HashMap<String,String> map);
        void getValidationCode(String phone,String type);
        //编辑用户资料
        void getEditInfo(Map<String,String> map);

    }

    //model层接口
    public interface Source extends BaseSource {
        //通过手机号注册
        void getRegister(ApiCallBack<User> callBack, HashMap<String,String> map);

        //获取验证码
        void getValidationCode(ApiCallBack<Object> callBack,String phone,String type);
    }
}
