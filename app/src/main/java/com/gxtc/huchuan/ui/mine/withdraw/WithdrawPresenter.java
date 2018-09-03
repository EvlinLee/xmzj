package com.gxtc.huchuan.ui.mine.withdraw;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.bean.AccountSetInfoBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.WithdrawRepository;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.HashMap;

/**
 * Created by Gubr on 2017/5/4.
 */

public class WithdrawPresenter implements WithdrawContract.Presenter {
    private final WithdrawRepository    source;
    private       WithdrawContract.View mView;


    public WithdrawPresenter(WithdrawContract.View view) {
        mView = view;
        mView.setPresenter(this);
        source = new WithdrawRepository();
    }

    @Override
    public void getAccountSetInfo() {
        if (UserManager.getInstance().isLogin()) {
            if(mView == null)return;
            mView.showLoad();
            source.getAccountSetInfo(new ApiCallBack<AccountSetInfoBean>() {
                @Override
                public void onSuccess(AccountSetInfoBean data) {
                    if(mView == null)return;
                    if (data != null) {
                        if (data.getAccountSet() != null && data.getAccountSet().size() > 0) {
                            mView.showAccount(data.getAccountSet().get(data.getAccountSet().size() - 1));
                        }

                        String userPercent = data.getUserPercent();
                        Double aDouble     = Double.valueOf(userPercent);
                        mView.showPercent(aDouble + "",data.getAccountSet());
                    }

                    mView.showLoadFinish();
                }

                @Override
                public void onError(String errorCode, String message) {
                    if(mView == null)return;
                    mView.showLoadFinish();
                    ErrorCodeUtil.handleErr(mView, errorCode, message);
                }
            }, UserManager.getInstance().getToken());
        }

    }

    @Override
    public void submit(HashMap<String, String> map) {
        mView.showLoad();
        source.saveExpRecd(new ApiCallBack<Void>() {
            @Override
            public void onSuccess(Void data) {
                if(mView == null)return;
                mView.showLoadFinish();
                mView.onSuccess("");
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)return;
                mView.showLoadFinish();
                mView.onError(message);
            }
        }, map);
    }



    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
        source.destroy();
    }
}
