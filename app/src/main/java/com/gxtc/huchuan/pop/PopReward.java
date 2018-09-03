package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.gxtc.huchuan.ui.pay.PayActivity;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Gubr on 2017/3/17.
 */

public class PopReward extends BasePopupWindow {
    private static double[] defPrice = new double[]{2, 5, 10, 50, 100, 200};
    @BindView(R.id.sv_user_head)     ImageView mSvUserHead;
    @BindView(R.id.tv_user_name)     TextView  mTvUserName;
    @BindView(R.id.tv_reward_price0) TextView  mTvReward0;
    @BindView(R.id.tv_reward_price1) TextView  mTvReward1;
    @BindView(R.id.tv_reward_price2) TextView  mTvReward2;
    @BindView(R.id.tv_reward_price3) TextView  mTvReward3;
    @BindView(R.id.tv_reward_price4) TextView  mTvReward4;
    @BindView(R.id.tv_reward_price5) TextView  mTvReward5;
    @BindView(R.id.tv_other_price)   TextView  mTvOtherPrice;

    private View        view;
    private View        cancel;
    private View        okview;
    private EditText    mEditPrice;
    private AlertDialog mAlertDialog;
    private UserInfo    mUserCode;

    public void setUserCode(UserInfo userCode) {
        this.mUserCode = userCode;
        ImageHelper.loadImage(getActivity(), mSvUserHead, userCode.getPortraitUri().toString());
        mTvUserName.setText(userCode.getName());
    }

    public PopReward(Activity activity, int resId) {
        super(activity, resId);
    }

    public PopReward(Activity activity, int resId, boolean outSide) {
        super(activity, resId, outSide);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
    }

    @Override
    public void initListener() {

    }


    public void setPriceList(double[] rewardPrice) {
        if (rewardPrice == null || rewardPrice.length < 6) return;
        defPrice = rewardPrice;
        setPrice(defPrice);
    }


    private void setPrice(double[] prices) {
        mTvReward0.setText(String.format("%.0f", prices[0]));
        mTvReward1.setText(String.format("%.0f", prices[1]));
        mTvReward2.setText(String.format("%.0f", prices[2]));
        mTvReward3.setText(String.format("%.0f", prices[3]));
        mTvReward4.setText(String.format("%.0f", prices[4]));
        mTvReward5.setText(String.format("%.0f", prices[5]));
    }

    @OnClick({R.id.iv_close, R.id.ll_reward_0, R.id.ll_reward_1, R.id.ll_reward_2, R.id.ll_reward_3, R.id.ll_reward_4, R.id.ll_reward_5, R.id.tv_other_price})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                closePop();
                break;
            case R.id.ll_reward_0:
                gotoPlay(0);
                break;
            case R.id.ll_reward_1:
                gotoPlay(1);
                break;

            case R.id.ll_reward_2:
                gotoPlay(2);
                break;

            case R.id.ll_reward_3:
                gotoPlay(3);
                break;

            case R.id.ll_reward_4:
                gotoPlay(4);
                break;

            case R.id.ll_reward_5:
                gotoPlay(5);
                break;
            case R.id.tv_other_price:
                showOtherPriceDialog();
                break;
        }
    }

    private void gotoPlay(int position) {
        double i = defPrice[position];
        gotoPlayOther(i);
    }


    private void gotoPlayOther(double price) {
        if (l != null) {
            l.onPriceSelected(price);
        }
    }


    private void showOtherPriceDialog() {

        if (mAlertDialog == null) {

            view = View.inflate(getActivity(), R.layout.dialog_other_price, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                    R.style.MyDialogStyle2);
            mAlertDialog = builder.setView(view).create();


            cancel = view.findViewById(R.id.tv_cancel);
            mEditPrice = (EditText) view.findViewById(R.id.edit_price);
            mEditPrice.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                        int dstart, int dend) {
                   /* if (source.equals(".") && dest.toString().length() == 0) {
                        return "0.";
                    }*/
                    if (dest.toString().contains(".")) {
                        int index   = dest.toString().indexOf(".");
                        int mlength = dest.toString().substring(index).length();
                        if (mlength == 3) {
                            return "";
                        }
                    }
                    return null;
                }
            }});

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
            okview = view.findViewById(R.id.tv_asok);
            okview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(mEditPrice.getText().toString().trim())){
                        ToastUtil.showShort(getActivity(), "金额不能为空");
                        return;
                    }
                    Double price = Double.valueOf(mEditPrice.getText().toString());
                  /*  if (price < 5 || price > 20000) {
                        ToastUtil.showShort(getActivity(), "金额必需在5元-2000元之间");
                    } else {
                        gotoPlayOther(price);
                        mAlertDialog.dismiss();
                    }*/
                    gotoPlayOther(price);
                    mAlertDialog.dismiss();

                }
            });
        }
        mAlertDialog.show();


    }


    public interface PayPriceListener {
        public void onPriceSelected(double price);
    }


    private PayPriceListener l;

    public void setPayPriceListener(PayPriceListener l) {
        this.l = l;
    }
}
