package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.im.manager.ConversationManager;
import com.gxtc.huchuan.im.provide.RedPacketMessage;
import com.gxtc.huchuan.ui.pay.PayActivity;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Gubr on 2017/3/17.
 */

public class PopRewardShow extends BasePopupWindow {

    @BindView(R.id.sv_user_head) ImageView mSvUserHead;
    @BindView(R.id.tv_user_name) TextView  mTvUserName;
    @BindView(R.id.tv_price)     TextView  mTvPrice;

    private RedPacketMessage message;

    public void setMessage(RedPacketMessage message) {
        this.message = message;
        ImageHelper.loadImage(getActivity(), mSvUserHead,
                message.getUserInfo().getPortraitUri().toString());
        mTvUserName.setText(message.getContent());
        mTvPrice.setText(message.getPrice() + "元");
    }

    public PopRewardShow(Activity activity, int resId) {
        super(activity, resId);
    }

    public PopRewardShow(Activity activity, int resId, boolean outSide) {
        super(activity, resId, outSide);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }

    @Override
    public void initListener() {

    }


    @OnClick({R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                closePop();
                break;
        }
    }


}
