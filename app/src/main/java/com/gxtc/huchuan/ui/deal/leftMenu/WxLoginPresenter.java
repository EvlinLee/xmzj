package com.gxtc.huchuan.ui.deal.leftMenu;

import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.WxResponse;
import com.gxtc.huchuan.data.WxRepository;
import com.gxtc.huchuan.data.WxSource;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.utils.MD5Util;

import java.util.HashMap;

/**
 * Created by Steven on 17/3/24.
 */

public class WxLoginPresenter implements WxLoginContract.Presenter {

    private WxLoginContract.View    mView;
    private WxSource                mData;

    public WxLoginPresenter(WxLoginContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new WxRepository();
    }

    @Override
    public void login(final String userName, String password, String ver) {

        password = MD5Util.MD5Encode(password,"");
        HashMap<String,String> map = new HashMap<>();

        map.put("username",userName);
        map.put("pwd",password);
        map.put("imgcode",ver);
        map.put("f","json");

        mView.showLoad();
        mData.login(map, new ApiCallBack<WxResponse>() {
            @Override
            public void onSuccess(WxResponse data) {
                if(mView == null) return;

                mView.showLoadFinish();

                int code = data.getResp().getRet();
                LogUtil.i("code : " + code);
                String msg = "";
                switch (code){
                    case -1:
                        msg = "系统错误，请稍候再试。";
                        break;

                    case 0:
                        mView.loginSuccess(data);
                        break;

                    case 200002:
                        msg = "服务器拒绝访问。";
                        break;

                    case 200007:
                        msg = "您目前处于访问受限状态。";
                        break;

                    case 200008:
                        msg = "请输入验证码。";
                        mView.showVerifcode(userName);
                        break;

                    case 200021:
                        msg = "不存在该帐户。";
                        break;

                    case 200023:
                        msg = "您输入的帐号或者密码不正确，请重新输入。";
                        break;

                    case 200025:
                        msg = "无法登录海外帐号。";
                        break;

                    case 200027:
                        msg = "您输入的验证码不正确,请重新输入。";
                        mView.showVerifcode(userName);
                        break;

                    case 200121:
                        msg = "该帐号属于微信开放平台。";
                        break;

                }

                mView.loginFailed(msg);

            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });

    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
    }
}
