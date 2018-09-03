package com.gxtc.huchuan.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.bean.event.EventPayResultBean;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
/**
 * 微信支付回调页面
 */
public class WXPayEntryActivity extends BaseTitleActivity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this,"wxe7ecfb0750844217");
//        api = WXAPIFactory.createWXAPI(this,"wxdc89f6f6dcce9040");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.i("onReq:  " );
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.i("resp.errCode   :  " + resp.errCode);
        EventBusUtil.post(new EventPayResultBean(resp.errCode));
        finish();
    }

}