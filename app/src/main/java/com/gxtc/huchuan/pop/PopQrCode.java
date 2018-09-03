package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Describe: 二维码弹出框
 * Created by ALing on 2017/3/23 .
 */

public class PopQrCode extends BasePopupWindow {
    @BindView(R.id.iv_pop_qrcode)
    ImageView mIvpopRrCode;

    private Activity activity;

    public PopQrCode(Activity activity, int resId) {
        super(activity, resId);
        this.activity =activity;
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);

    }

    @Override
    public void initListener() {

    }
    @Override
    public void showPopOnRootView(Activity activity) {
        super.showPopOnRootView(activity);

    }
    public void showQrCode(String imgUrl){
        ImageHelper.loadImage(activity,mIvpopRrCode,imgUrl);
    }
}
