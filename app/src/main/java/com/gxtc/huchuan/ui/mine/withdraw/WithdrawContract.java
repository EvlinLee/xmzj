package com.gxtc.huchuan.ui.mine.withdraw;

import com.gxtc.commlibrary.BasePresenter;
import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.data.BaseSource;
import com.gxtc.huchuan.bean.AccountSetInfoBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.pay.AccountSet;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gubr on 2017/5/4.
 */

public interface WithdrawContract {
    public interface View extends BaseUiView<Presenter> {


        void showAccount(AccountSet bean);

        void showPercent(String percent,List<AccountSet> list);

        void onSuccess(String message);

        void onError(String message);
    }

    public interface Presenter extends BasePresenter {

        /**
         * 获取相关信息
         */
        public void getAccountSetInfo();


        /**
         * 提交申请
         * @param map
         */
        public void submit(HashMap<String, String> map);
    }

    public interface Source extends BaseSource {
        /**
         * 获取相关信息
         */
        void getAccountSetInfo(ApiCallBack<AccountSetInfoBean> callBack, String token);

        void saveExpRecd(ApiCallBack<Void> callBack, HashMap<String, String> map);


        void upUser(ApiCallBack<User> callBack, String token);
    }
}
