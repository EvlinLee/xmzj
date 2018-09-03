package com.gxtc.huchuan.ui.im.redPacket;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventSendRPBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.RedPacketPayDialog;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.recharge.RechargeActivity;
import com.gxtc.huchuan.utils.ImMessageUtils;
import com.gxtc.huchuan.utils.StringUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 发送红包界面
 */
public class IssueRedPacketActivity extends BaseTitleActivity implements View.OnClickListener,
        RedPacketPayDialog.OnRedPacketListener {

    private final int MODEL_PIN    = 0X01;         //拼手气
    private final int MODEL_NORMAL = 0X02;         //普通红包

    private final int REQUEST_SETTING_PWD = 0X03;

    private final int MAX_RP_COUNT = 100;           //最大红包个数

    @BindView(R.id.layout_actionbar) View        actionbar;
    @BindView(R.id.tv_title)         TextView    tvTitle;
    @BindView(R.id.img_back)         ImageButton imgBack;

    @BindView(R.id.tv_pin)         TextView tvPin;
    @BindView(R.id.tv_switch_pin)  TextView tvSwitchPin;
    @BindView(R.id.tv_money)       TextView tvMoney;
    @BindView(R.id.tv_max_hint)    TextView tvHint;
    @BindView(R.id.btn_switch_pin) TextView btnSiwtchPin;
    @BindView(R.id.edit_money)     EditText editMoney;
    @BindView(R.id.edit_count)     EditText editCount;
    @BindView(R.id.edit_message)   EditText editMsg;
    @BindView(R.id.btn_issue)      Button   btnIssue;

    @BindView(R.id.layout_switch_model) View     layoutSwitch;
    @BindView(R.id.layout_count)        View     layoutCount;
    @BindView(R.id.tv_count)            TextView tvCount;

    private int    model      = MODEL_PIN;
    private double maxMoney   = 0;          //最大金额数
    private double money      = 0;
    private double totalMoney = 0;          //总金额
    private int    rpCount    = 1;          //红包个数
    private int type;                       //红包类型  0：个人，1：群组
    private boolean countFlag = false;

    private String allotType = "0";         //分配类型。0、随机；1、平均
    private String targetId;
    private String message;

    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_red_packet);
    }

    @Override
    public void initView() {
        tvTitle.setText("发红包");
        imgBack.setOnClickListener(this);
        setActionBarTopPadding(actionbar, true);

        targetId = getIntent().getStringExtra(Constant.INTENT_DATA);
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra(
                "type");
        type = mConversationType == Conversation.ConversationType.GROUP ? 1 : 0;
        maxMoney = 20000;

        //单聊
        if (type == 0) {
            switchModel();
            layoutSwitch.setVisibility(View.GONE);
            layoutCount.setVisibility(View.GONE);
            tvCount.setVisibility(View.GONE);
            rpCount = 1;
            maxMoney = 500;
            tvHint.setText("单次支付总额不可超过" + maxMoney +"元");
        }
    }


    @Override
    public void initListener() {
        editMoney.addTextChangedListener(moneyWatcher);
        editCount.addTextChangedListener(countWatcher);
    }

    @OnClick({R.id.btn_switch_pin})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                WindowUtil.closeInputMethod(this);
                setResult(RESULT_OK);
                finish();
                break;

            //切换红包模式
            case R.id.btn_switch_pin:
                switchModel();
                break;

            //发送红包
            case R.id.btn_issue:
                showPayDialog();
                break;
        }
    }

    //发红包成功
    @Override
    public void redPacketIssued(RedPacketBean bean) {
        //更新本地钱数
        String       token = UserManager.getInstance().getToken();
        Subscription sub = MineApi.getInstance().getUserInfo(token).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<User>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        User user = (User) data;
                        UserManager.getInstance().updateUserMoney(user);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LogUtil.i("redPacketIssued getUserInfo  : " + message);
                    }
                }));

        //发送红包消息
        String    name      = UserManager.getInstance().getUserName();
        RPMessage rpMessage = RPMessage.obtain(bean.getId() + "", name, message);
        Message   sendMsg   = ImMessageUtils.obtain(targetId, mConversationType, rpMessage);
        sendRongIm(sendMsg);
        EventBusUtil.post(new EventSendRPBean());
        RxTaskHelper.getInstance().addTask(this,sub);
    }

    //发红包失败
    @Override
    public void redPacketIssuedFailed(String msg) {
        ToastUtil.showShort(this, msg);
    }

    @Override
    public void gotoRecharge() {
        Intent intent   = new Intent(this,RechargeActivity.class);
        startActivityForResult(intent, Constant.INTENT_PAY_RESULT);
    }


    private class EditTextWatcher implements TextWatcher {

        private EditText mEditText;

        public EditTextWatcher(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(mEditText, s);
        }

        public void afterTextChanged(EditText edit, Editable s) {}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTING_PWD && resultCode == RESULT_OK) {
            showPayDialog();
        }

        if(requestCode == Constant.INTENT_PAY_RESULT && resultCode == RESULT_OK && mDialog != null){
            mDialog.refreshMoney();
        }
    }

    @Override
    protected void onDestroy() {
        editMoney.removeTextChangedListener(moneyWatcher);
        editCount.removeTextChangedListener(countWatcher);
        mDialog = null;
        RxTaskHelper.getInstance().cancelTask(this);
        super.onDestroy();
    }

    private void showErrorHint(String msg){
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(msg);
    }

    //验证参数
    private boolean verifyParam(){
        if(rpCount == 0){
            showErrorHint("红包个数不能为0");
            showAllowPay(false);
            return false;
        }

        if(rpCount > MAX_RP_COUNT || totalMoney > maxMoney){
            showAllowPay(false);
            //超过最大金额数
            if(totalMoney > maxMoney){
                showErrorHint("单次支付总额不可超过" + maxMoney +"元");
            }
            //超过最大红包个数
            if(rpCount > MAX_RP_COUNT){
                showErrorHint("最多只能发" + MAX_RP_COUNT + "个红包");
            }
            return false;
        }

        if(countFlag && totalMoney / rpCount > 500){
            showErrorHint("单个红包不可超过500元");
            return false;
        }

        showAllowPay(true);
        return true;
    }

    //是否允许支付
    private void showAllowPay(boolean isAllow){
        if(isAllow){
            btnIssue.setOnClickListener(IssueRedPacketActivity.this);
            btnIssue.setEnabled(true);
            tvHint.setVisibility(View.INVISIBLE);
        }else{
            btnIssue.setOnClickListener(null);
            btnIssue.setEnabled(false);
            tvHint.setVisibility(View.VISIBLE);
        }
    }


    //显示支付弹窗
    private RedPacketPayDialog mDialog;

    private void showPayDialog() {
        User user = UserManager.getInstance().getUser();
        //没有支付密码，去设置
        if (user != null && user.getHasPayPwd() == 0) {
            GotoUtil.goToActivityForResult(this, PayPwdSettingActivity.class, REQUEST_SETTING_PWD);

            //有支付密码就显示支付弹窗
        } else {
            message = editMsg.getText().toString();
            if (TextUtils.isEmpty(message)) {
                message = "恭喜发财，大吉大利";
            }

            if(money == 0){
                ToastUtil.showShort(this, "红包金额不能为0");
                return;
            }

            String token = UserManager.getInstance().getToken();
            String rewardAmt = new BigDecimal(totalMoney).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue() + "";           //红包金额
            String num       = rpCount + "";                                //红包数量
            String type      = this.type + "";                              //红包类型 0:个人  1:群组
            String allotType = this.allotType;                              //分配类型。0、随机；1、平均
            String linkId    = targetId;
            String payType   = "3";
            String message   = this.message;

            HashMap<String, String> param = new HashMap<>();
            param.put("token", token);
            param.put("rewardAmt", rewardAmt);
            param.put("num", num);
            param.put("type", type);
            param.put("allotType", allotType);
            param.put("linkId", linkId);
            param.put("payType", payType);
            param.put("message", message);
            mDialog = new RedPacketPayDialog(this, param,RedPacketPayDialog.PAY_TYPE_RP);
            mDialog.setOnRedPacketListener(this);
            mDialog.show();
        }

    }

    private void switchModel() {
        //普通红包
        if (model == MODEL_PIN) {
            model = MODEL_NORMAL;
            allotType = "1";
            tvPin.setText("单个金额");
            tvPin.setCompoundDrawables(null, null, null, null);
            tvSwitchPin.setText("群里每人收到固定金额，");
            btnSiwtchPin.setText("改为拼手气红包");

        //随机红包
        } else {
            model = MODEL_PIN;
            allotType = "0";
            tvPin.setText("总金额");
            Drawable d = getResources().getDrawable(R.drawable.ic_pin);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            tvPin.setCompoundDrawables(d, null, null, null);
            tvSwitchPin.setText("每人抽到的金额随机，");
            btnSiwtchPin.setText("改为普通红包");
        }
        changeMoney();

    }

    private EditTextWatcher moneyWatcher = new EditTextWatcher(editMoney) {
        @Override
        public void afterTextChanged(EditText edit, Editable s) {
            String temp = s.toString();
            if (TextUtils.isEmpty(temp)) {
                temp = "0";
            }
            money = StringUtil.toDouble(temp);
            if (model == MODEL_PIN) {
                totalMoney = money;
            } else {
                totalMoney = money * rpCount;
            }

            verifyParam();      //验证参数
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            String moneys = "¥" + nf.format(new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            tvMoney.setText(moneys);
        }
    };

    private TextWatcher countWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            countFlag = true;

            if (TextUtils.isEmpty(s)) {
                rpCount = 1;
            } else {
                rpCount = Integer.valueOf(s.toString());
            }

            if (model == MODEL_NORMAL) {
                totalMoney = money * rpCount;
            }

            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            String moneys = "¥" + nf.format(new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            tvMoney.setText(moneys);

            verifyParam();
        }
    };


    private void changeMoney() {
        editMoney.removeTextChangedListener(moneyWatcher);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        if (model == MODEL_NORMAL) {
            double count = rpCount;
            money = totalMoney / count;
            if(!TextUtils.isEmpty(editMoney.getText().toString())){
                editMoney.setText(nf.format(money));
            }

        } else {
            if(!TextUtils.isEmpty(editMoney.getText().toString())){
                editMoney.setText(nf.format(totalMoney));
            }
        }
        editMoney.addTextChangedListener(moneyWatcher);

        String moneys = "¥" + nf.format(new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        tvMoney.setText(moneys);
    }

    //发送融云消息
    private void sendRongIm(Message msg) {
        RongIM.getInstance().sendMessage(msg, "你有一个红包消息！", "你有一个红包消息！",
                new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        finish();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        ToastUtil.showShort(IssueRedPacketActivity.this, "发送失败，请重新登录");
                    }
                });
    }


}
