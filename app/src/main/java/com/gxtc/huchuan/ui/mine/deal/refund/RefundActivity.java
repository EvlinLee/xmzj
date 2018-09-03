package com.gxtc.huchuan.ui.mine.deal.refund;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventRefundBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.pop.PopRefund;
import com.gxtc.huchuan.ui.pay.PayActivity;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 申请退款页面
 */

public class RefundActivity extends BaseTitleActivity implements View.OnClickListener {

    public static final int RESULT = 0x09;

    @BindView(R.id.tv_msg)   EditText editMsg;
    @BindView(R.id.tv_name)  TextView tvName;
    @BindView(R.id.tv_phone) TextView tvPhone;
    @BindView(R.id.note) TextView note;
    @BindView(R.id.tv_mouney) EditText tvMouney;
    @BindView(R.id.btn)      Button   btn;
    private HashMap<String,String> map;
    private String orderId ;
    private OrderDetailedBean bean ;

    private String[] contents = {"订单不能按预计时间送达", "信息有误", "商品买错了", "协商一致退款", "与事实不符", "支付方式有误/无法支付", "不想买了"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("申请退款");
        getBaseHeadView().showBackButton(this);
        map = new HashMap<>();
        bean = (OrderDetailedBean)getIntent().getSerializableExtra(Constant.INTENT_DATA);
        orderId = bean.getOrderId();
    }

    @OnClick({R.id.btn, R.id.tv_msg})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //申请
            case R.id.btn:
                submit();
                break;


            case R.id.tv_msg:
                showPop();
                break;
        }
    }

    private PopRefund mPop;
    private void showPop() {
        if (mPop == null) {
            mPop = new PopRefund(this, R.layout.pop_complaint);
            List<String> list = new ArrayList<>();
            Collections.addAll(list,contents);
            mPop.setData("退款原因",list);
        }
        mPop.showPopOnRootView(this);
    }

    @Override
    public void initData() {
        super.initData();
        note.setText("最多可以退款金额："+totalPay()+"元");
    }

    private void submit(){
        String msg = editMsg.getText().toString();
        String mouney = tvMouney.getText().toString();
        Double total = totalPay();
        if (TextUtils.isEmpty(mouney)) {
            ToastUtil.showShort(this,"请输入退款金额");
            return;
        }
        if (Double.parseDouble(mouney) > total) {
            ToastUtil.showShort(this,"输入的金额不能大于交易的金额");
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.showShort(this,"请选择退款原因");
            return;
        }

        String token = UserManager.getInstance().getToken();
        map.put("token",token);
        map.put("orderId",orderId);
        map.put("type", "0");
        map.put("reason",msg);
        map.put("money",mouney);
        map.put("role","1");
        Subscription sub = DealApi.getInstance()
                                  .refund(map)
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                                      @Override
                                      public void onSuccess(Object data) {
                                          if(tvName == null)    return;
                                          ToastUtil.showShort(RefundActivity.this,"申请成功");
                                          EventBusUtil.post(new EventClickBean("",true));
                                          setResult(RESULT);
                                          finish();
                                      }

                                      @Override
                                      public void onError(String errorCode, String message) {
                                          ToastUtil.showShort(RefundActivity.this,message);
                                      }
                                  }));
        RxTaskHelper.getInstance().addTask(this,sub);
    }
    private double totalPay(){
        double money = bean.getTradeAmt();

        double danbao = bean.getDbFee();

        double zhekou = bean.getDiscount();
        double total = 0.0;
        BigDecimal moneyB = new BigDecimal(money);
        BigDecimal danbaoB = new BigDecimal(danbao);
        BigDecimal zhekouB = new BigDecimal(zhekou);
        //计算总价
        /*if("0".equals(bean.getBuyWay())){          //0：买家承担担保费；1：卖家承担担保费
             total = moneyB.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        }else {
             total = moneyB.subtract(danbaoB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        }*/
        total = moneyB.add(danbaoB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return total;
    }

    @Subscribe
    public void onEvent(EventRefundBean bean){
        editMsg.setText(bean.msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
