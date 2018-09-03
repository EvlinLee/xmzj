package com.gxtc.huchuan.ui.deal.leftMenu;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.huchuan.bean.WxResponse;

/**
 * Created by Steven on 17/3/24.
 */

public interface WxLoginContract {

    interface View extends BaseUiView<WxLoginContract.Presenter>{
        void loginSuccess(WxResponse response);
        void loginFailed(String msg);
        void showVerifcode(String userName);
    }

    interface Presenter extends BasePresenter{

        void login(String userName, String password,String ver);

    }

}
