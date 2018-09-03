package com.gxtc.huchuan.ui.mine.loginandregister.changepsw;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by ALing on 2017/2/14.
 *
 */

public class ChangePswContract {

    public interface View extends BaseUiView<ChangePswContract.Presenter> {
        void changePwsResult(Object object);
        void showValidationCode(Object object);
        void showCountdown();
    }

    public interface Presenter extends BasePresenter {
        void changePws(HashMap<String, String> map);
        void getValidationCode(String phone, String type);
    }

    public interface Source extends BaseSource {
        void changePws(ApiCallBack<Object> callBack, HashMap<String, String> map);

        //获取验证码
        void getValidationCode(ApiCallBack<Object> callBack, String phone, String type);

        //验证验证码
        void verifyCode(String phone, String code, ApiCallBack<Object> callBack);

        //更改手机号
        void changePhone(String token, String phone, String password, ApiCallBack<Object> callBack);

        //校验密码
        void verifyPassword(String token, String password,ApiCallBack<Object> callBack);

        //修改密码
        void changePassword(String token,String oldPwd, String newPwd,ApiCallBack<Object> callBack);

        //绑定微信号
        void bindWx(String token,String name, String code,ApiCallBack<Object> callBack);
    }
}
