package com.gxtc.huchuan.ui.deal.deal.orderDetailed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatRoomBean;
import com.gxtc.huchuan.bean.MessageBean;
import com.gxtc.huchuan.bean.OrderDetailedBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventDealBean;
import com.gxtc.huchuan.bean.pay.OrdersRequestBean;
import com.gxtc.huchuan.data.MessageRepository;
import com.gxtc.huchuan.data.MessageSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.OrderHelperDialog;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.DealApi;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.collectresolve.CollectResolveActivity;
import com.gxtc.huchuan.ui.mine.deal.refund.RefundActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.pay.PayActivity;
import com.gxtc.huchuan.ui.pay.PayConstant;
import com.gxtc.huchuan.utils.ClickUtil;
import com.gxtc.huchuan.utils.ClipboardUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 买家订单详情
 */
public class OrderDetailedBuyerActivity extends BaseTitleActivity implements View.OnClickListener, OrderDetailedBuyerContract.View {

    @BindView(R.id.layout_order_detailed)
    View rootView;

    @BindView(R.id.img_deal_icon1)
    ImageView imgIcon1;
    @BindView(R.id.img_deal_icon2)
    ImageView imgIcon2;
    @BindView(R.id.img_deal_icon3)
    ImageView imgIcon3;
    @BindView(R.id.img_deal_icon4)
    ImageView imgIcon4;
    @BindView(R.id.img_deal_line1)
    ImageView imgLine1;
    @BindView(R.id.img_deal_line2)
    ImageView imgLine2;
    @BindView(R.id.img_deal_line3)
    ImageView imgLine3;
    @BindView(R.id.tv_process1)
    TextView tvProcess1;
    @BindView(R.id.tv_process2)
    TextView tvProcess2;
    @BindView(R.id.tv_process3)
    TextView tvProcess3;
    @BindView(R.id.tv_process4)
    TextView tvProcess4;
    @BindView(R.id.tv_process5)
    TextView tvProcess5;
    @BindView(R.id.btn_article_article)
    Button btn;
    @BindView(R.id.btn_refund)
    Button btnRefund;

    @BindView(R.id.layout_cancel)
    View LayoutCancel;
    @BindView(R.id.btn_cancel)
    TextView btnCancel;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_danbao)
    TextView tvDanbao;
    @BindView(R.id.tv_discount)
    TextView tvZhekou;
    @BindView(R.id.tv_total_money)
    TextView tvTotal;
    @BindView(R.id.tv_comment)
    TextView tvMsg;

    @BindView(R.id.layout_daojishi)
    RelativeLayout layoutDaojishi;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_endtime)
    TextView tvEndTime;
    @BindView(R.id.tv_hint_content)
    TextView tvHintContent;
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @BindView(R.id.layout_adder)
    View layoutAdder;
    @BindView(R.id.layout_wuliu)
    View layoutWuliu;
    @BindView(R.id.tv_addr)
    TextView tvAdder;
    @BindView(R.id.btn_cancel_refund)
    TextView tvCancelRefund;

    @BindView(R.id.layout_phone)
    LinearLayout layoutPhone;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_goods)
    TextView tvGoods;
    @BindView(R.id.img_phone)
    ImageView imgPhone;

    private boolean isEndTime = false;
    private String id;
    private OrderDetailedBean bean;
    private ChatRoomBean chatRoomBean;

    private OrderHelperDialog helperDialog;

    private MessageSource mMessageSource;
    private OrderDetailedBuyerContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;
    private AlertDialog phoneDialog;
    private AlertDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detailed_buyer);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_order_detailed));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_help, this);

        ImageButton imageView = new ImageButton(this);
        imageView.setImageResource(R.drawable.navigation_icon_kefu);
        imageView.setId(R.id.img);
        imageView.setOnClickListener(this);
        imageView.setBackgroundColor(getResources().getColor(R.color.transparent));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -1);
        imageView.setLayoutParams(params);
        getBaseHeadView().getHeadRightLayout().addView(imageView, 0);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (bean == null) return false;
                ClipboardUtil.copyText(bean.getOrderId());
                return false;
            }
        });
    }

    @OnClick({R.id.btn_cancel,
            R.id.tv_contact,
            R.id.btn_refund,
            R.id.tv_goods,
            R.id.look_more,
            R.id.btn_cancel_refund,
            R.id.img_phone})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            //按钮点击
            case R.id.btn_article_article:
                //卖家同意交易，去支付
                if (bean.getIsFinish() == 1) {
                    gotoPay();
                }

                //卖家已交付
                if (bean.getIsFinish() == 3) {
                    confirmDeal();
                }
                break;

            //取消订单
            case R.id.btn_cancel:
                cancelOrder();
                break;

            //联系卖家
            case R.id.tv_contact:
                gotoChatRoom();
                break;

            //电话联系
            case R.id.img_phone:
                callPhone();
                break;

            //交易主页
            case R.id.tv_goods:
                if (bean != null)
                    GoodsDetailedActivity.startActivity(this, bean.getTradeInfoId() + "");
                break;

            //帮助
            case R.id.HeadRightImageButton:
                if (helperDialog == null) {
                    helperDialog = new OrderHelperDialog(this);
                    helperDialog.anchorView(getBaseHeadView()
                            .getHeadRightImageButton())
                            .location(10, -5)
                            .showAnim(new PopEnterAnim().duration(200))
                            .dismissAnim(new PopExitAnim().duration(200))
                            .gravity(Gravity.BOTTOM)
                            .dimEnabled(true)
                            .cornerRadius(4)
                            .bubbleColor(Color.parseColor("#ffffff"))
                            .setOnClickListener(this);
                }
                helperDialog.show();
                break;

            //跳转到个人主页
            case R.id.tv_home:
                gotoHomePage();
                break;

            //跳转到帮助页面
            case R.id.tv_helper:
                CommonWebViewActivity.startActivity(this, "https://apps.xinmei6.com/publish/sysInfo.html?type=2", "帮助");
                break;

            //申请退款
            case R.id.btn_refund:
                if (!ClickUtil.isFastClick()) {
                    gotoRefund();
                }
                break;

            //取消退款
            case R.id.btn_cancel_refund:
                showDialogNotice();
                break;

            //客服
            case R.id.img:
                //0：全局客服1：商城客服 2：交易客服 3：app客服  rand  0：列表 1：随机
                MallCustomersActivity.Companion.goToCustomerServicesActivity(this, MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_DEAL(), MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_LIST());
                break;
            //查看更多
            case R.id.look_more:
                CommonWebViewActivity.startActivity(this, "https://apps.xinmei6.com/publish/tradeRule.html", "");
                break;
        }
    }


    //电话联系
    private void callPhone() {
        String[] pers = new String[]{Manifest.permission.CALL_PHONE};
        performRequestPermissions(getString(R.string.txt_call_phone_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        phoneDialog = DialogUtil.showInputDialog(OrderDetailedBuyerActivity.this, false, "", "是否呼叫: " + bean.getPhone() + " ?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent mIntent = new Intent(Intent.ACTION_CALL);
                                    mIntent.setData(Uri.parse("tel:" + bean.getPhone()));
                                    startActivity(mIntent);
                                    phoneDialog.dismiss();
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(OrderDetailedBuyerActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v.getId() == R.id.tv_dialog_confirm) {
                                            JumpPermissionManagement.GoToSetting(OrderDetailedBuyerActivity.this);
                                        }
                                        mAlertDialog.dismiss();
                                    }
                                });

                    }
                });
    }

    private void showDialogNotice() {
        mAlertDialog = DialogUtil.showDeportDialog(OrderDetailedBuyerActivity.this, false, null, "您已成功申请退款，确认撤销退款申请？",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.tv_dialog_confirm) {
                            cancelRefund();
                        }
                        mAlertDialog.dismiss();
                    }
                });
    }

    //申请退款
    private void gotoRefund() {
        //0:未支付，1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，10：卖家不同意交易
        int flag = bean.getIsFinish();
//        if(flag == 2){
        GotoUtil.goToActivity(this, RefundActivity.class, 0, bean);
//        }
    }

    private void cancelRefund() {

        Map<String, String> map = new HashMap<>();
        String token = UserManager.getInstance().getToken();
        map.put("token", token);
        map.put("orderId", bean.getOrderId());
        map.put("type", "0");
        map.put("role", "1");
        map.put("cancel", "1");
        Subscription sub = DealApi.getInstance().refund(map).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (tvName == null) return;
                        ToastUtil.showShort(OrderDetailedBuyerActivity.this, "取消退款成功");
                        mPresenter.getData(id);
                        EventBusUtil.post(new EventClickBean("", true));
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        ToastUtil.showShort(OrderDetailedBuyerActivity.this, message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }

    private void gotoHomePage() {
        if (bean != null) {
            PersonalInfoActivity.startActivity(this, bean.getUserCode());
        }
    }

    private void gotoPay() {
        double money = bean.getTradeAmt();
        tvMoney.setText("¥" + money);

        double danbao = bean.getDbFee();
        tvDanbao.setText("¥" + danbao);

//        double zhekou = bean.getDiscount();
        double zhekou = 0;//折扣已被隐藏，暂时不用，这里直接置为0
        tvZhekou.setText("-¥" + zhekou);

        BigDecimal moneyB = new BigDecimal(money);
        BigDecimal danbaoB = new BigDecimal(danbao);
        BigDecimal zhekouB = new BigDecimal(zhekou);
        //计算总价
        double total = moneyB.add(danbaoB).subtract(zhekouB).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;

        String extra = "{\"orderId\":\"" + bean.getOrderId() + "\"}";

        OrdersRequestBean requestBean = new OrdersRequestBean();
        requestBean.setToken(UserManager.getInstance().getToken());
        requestBean.setTransType("TO");
        requestBean.setTotalPrice(total + "");
        requestBean.setExtra(extra);
        requestBean.setGoodsName(bean.getTradeInfoTitle());

        GotoUtil.goToActivity(this, PayActivity.class, Constant.INTENT_PAY_RESULT, requestBean);
    }

    private void gotoChatRoom() {
        final User user = UserManager.getInstance().getUser();
        if (user == null) {
            GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
            return;
        }
        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mPresenter.getChatRoom(token, bean.getOrderId());
    }


    //确认完成
    private void confirmDeal() {
        confirmDialog = DialogUtil.showInputDialog(this, false, "", "确认收货后交易担保结束，交易款将直接转到卖家账户，是否确认收货？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = UserManager.getInstance().getToken();
                HashMap<String, String> map = new HashMap<>();
                map.put("token", token);
                map.put("id", bean.getId() + "");
                map.put("status", "4");
                mPresenter.confirmDeal(map);
                confirmDialog.dismiss();
            }
        });
    }

    @Override
    public void initData() {
        mMessageSource = new MessageRepository();
        id = getIntent().getStringExtra(Constant.INTENT_DATA);
        new OrderDetailedBuyerPresenter(this);
        mPresenter.getData(id);
    }

    @Override
    public void tokenOverdue() {//服务器莫名其妙有时会返回这个错误码
//        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(OrderDetailedBean bean) {
        this.bean = bean;
        String name = bean.getFbUserName();
        tvName.setText(name);

        String number = bean.getOrderId();
        tvNumber.setText(number);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String time = sdf.format(new Date(bean.getCreateTime()));
        tvTime.setText(time);

        String message = bean.getMessage();
        tvMsg.setText(message);

        String goodsName = bean.getTradeInfoTitle();
        tvGoodsName.setText(goodsName);

        double money = bean.getTradeAmt();
        tvMoney.setText("¥" + money);

        double danbao = bean.getDbFee();
        tvDanbao.setText("¥" + danbao);

//        double zhekou = bean.getDiscount();
        double zhekou = 0;//折扣已被隐藏，暂时不用，这里直接置为0
        tvZhekou.setText("-¥" + zhekou);

        BigDecimal moneyB = new BigDecimal(money);
        BigDecimal danbaoB = new BigDecimal(danbao);
        BigDecimal zhekouB = new BigDecimal(zhekou);
        //计算总价
        double total = moneyB.add(danbaoB).subtract(zhekouB).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tvTotal.setText("¥" + total);

        if (!TextUtils.isEmpty(bean.getAddr())) {
            layoutAdder.setVisibility(View.VISIBLE);
            layoutWuliu.setVisibility(View.VISIBLE);
            tvAdder.setText(bean.getAddr());
        } else {
            layoutAdder.setVisibility(View.GONE);
            layoutWuliu.setVisibility(View.GONE);
        }
        int flag = bean.getIsFinish();

        if (flag == 2 || flag == 3) {
            btnRefund.setVisibility(View.VISIBLE);
        } else {
            btnRefund.setVisibility(View.GONE);
        }

        layoutPhone.setVisibility(View.VISIBLE);
        imgPhone.setVisibility(View.VISIBLE);
        tvGoods.setVisibility("0".equals(bean.getIsAppointTr()) ? View.VISIBLE : View.GONE);
        tvPhone.setText(String.valueOf(bean.getPhone()));
        switch (bean.getIsRefund()) {
            //正常
            case 0:
                setFinishStatus(bean);
                break;
            //申请退款中
            case 1:
                LayoutCancel.setVisibility(View.VISIBLE);
                tvCancelRefund.setVisibility(View.VISIBLE);
                tvHint.setText("交易关闭");
                tvHintContent.setText("申请退款中");
                btnRefund.setVisibility(View.GONE);
                tvEndTime.setVisibility(View.INVISIBLE);
                btn.setText("交易已关闭");
                btn.setOnClickListener(null);
//                btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//                btn.setTextColor(getResources().getColor(R.color.text_color_999));
                btnCancel.setVisibility(View.INVISIBLE);
                tvProcess5.setVisibility(View.GONE);
                break;
            //退款完成
            case 2:
                LayoutCancel.setVisibility(View.VISIBLE);
                tvHint.setText("交易关闭");
                tvHintContent.setText("已退款");
                tvCancelRefund.setVisibility(View.GONE);
                btnRefund.setVisibility(View.GONE);
                tvEndTime.setVisibility(View.INVISIBLE);
                imgIcon1.setImageResource(R.drawable.deal_pay_select);
                imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
                imgIcon2.setImageResource(R.drawable.deal_pay);
                imgLine2.setImageResource(R.drawable.deal_icon_quotes);
                btn.setText("交易已关闭");
                btn.setOnClickListener(null);
//                btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//                btn.setTextColor(getResources().getColor(R.color.text_color_999));
                btnCancel.setVisibility(View.INVISIBLE);
                tvProcess5.setVisibility(View.VISIBLE);
                tvProcess5.setText("退款已到账，请至我的钱包查看退款流水.");
                break;
            //退款被拒绝
            case 3:
                LayoutCancel.setVisibility(View.VISIBLE);
                tvHint.setText("交易已冻结");
                tvHintContent.setText("退款被拒");
                imgIcon1.setImageResource(R.drawable.deal_pay_select);
                imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
                tvCancelRefund.setVisibility(View.GONE);
                btnRefund.setVisibility(View.GONE);
                tvEndTime.setVisibility(View.INVISIBLE);
                btn.setText("交易已冻结");
                btn.setOnClickListener(null);
//                btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//                btn.setTextColor(getResources().getColor(R.color.text_color_999));
                btnCancel.setVisibility(View.INVISIBLE);
                tvProcess5.setVisibility(View.VISIBLE);
                tvProcess5.setText("你的退款卖家不同意，请与卖家继续沟通，或者联系平台客服介入");
                break;

        }

    }

    private void setFinishStatus(OrderDetailedBean bean) {
        String btnText = "";
        //0:未支付，1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，5:卖家逾期不发货交易关闭，10：卖家不同意交易
        if (bean.getIsFinish() == 10) {
            LayoutCancel.setVisibility(View.GONE);
            btnRefund.setVisibility(View.GONE);
            tvCancelRefund.setVisibility(View.GONE);
            tvHint.setText("交易关闭");
            tvHintContent.setText(bean.getUnagremark());
            btnText = "交易已关闭";
            btn.setOnClickListener(null);
//            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            btnCancel.setVisibility(View.INVISIBLE);
            tvProcess1.setText("卖家不同意");
            tvProcess5.setVisibility(View.GONE);
        }


        //0:未支付
        if (bean.getIsFinish() == 0) {
            btnText = "等待卖家同意";
            btnRefund.setVisibility(View.GONE);
            tvCancelRefund.setVisibility(View.GONE);
            LayoutCancel.setVisibility(View.VISIBLE);      //买家支付之前都可以取消订单
            btn.setOnClickListener(null);
//            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            tvProcess5.setVisibility(View.VISIBLE);
            tvProcess5.setText("你已下单成功，等待卖家同意交易");
            mPresenter.computeEndTime(bean.getEndTime());
            tvHint.setText("等待卖家同意");
            tvHintContent.setText("12小时内卖家未同意，系统将自动取消订单");
        }

        //1：卖家同意交易
        if (bean.getIsFinish() == 1) {
            btnText = "去支付";
            btn.setOnClickListener(this);
//            btn.setBackgroundResource(R.drawable.select_whit_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.colorAccent));
            btnRefund.setVisibility(View.GONE);
            tvCancelRefund.setVisibility(View.GONE);
            LayoutCancel.setVisibility(View.VISIBLE);      //此时可以取消订单
            mPresenter.computeEndTime(bean.getEndTime());
            tvHint.setText("等待支付");
            tvProcess5.setVisibility(View.VISIBLE);
            tvProcess5.setText("卖家已经同意交易，请及时支付");
            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            tvProcess1.setSelected(true);
            tvHintContent.setText("30分钟内未支付，系统将自动取消订单");
        }

        //2：买家已支付
        if (bean.getIsFinish() == 2) {
            LayoutCancel.setVisibility(View.VISIBLE);
            btnText = "等待卖家交付";
            btn.setOnClickListener(null);
            btnRefund.setVisibility(View.VISIBLE);//买家已支付，“确认收货”之前均可以退款
            tvCancelRefund.setVisibility(View.GONE);
//            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            tvProcess5.setVisibility(View.GONE);
            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon2.setImageResource(R.drawable.deal_pay_select);
            tvProcess1.setSelected(true);
            tvProcess2.setSelected(true);
            mPresenter.computeEndTime(bean.getEndTime());
            tvHint.setText("等待卖家交付");
            tvHintContent.setText("七天卖家未交付，系统将自动取消订单，钱将会退回账户");
            btnCancel.setVisibility(View.INVISIBLE);
        }

        //3：卖家已交付
        if (bean.getIsFinish() == 3) {
            btnText = "确认收货";//新需求“确认收货”之前均可以退款
            btn.setOnClickListener(this);
            btnRefund.setVisibility(View.VISIBLE);
            LayoutCancel.setVisibility(View.VISIBLE);
            tvCancelRefund.setVisibility(View.GONE);
//            btn.setBackgroundResource(R.drawable.select_whit_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.colorAccent));

            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon2.setImageResource(R.drawable.deal_pay_select);
            imgLine2.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon3.setImageResource(R.drawable.deal_maijia_select);
            tvProcess5.setVisibility(View.VISIBLE);
            tvProcess5.setText("你下单的产品卖家已发货，收到商品后请及时确认收货");
            tvProcess1.setSelected(true);
            tvProcess2.setSelected(true);
            tvProcess3.setSelected(true);
            mPresenter.computeEndTime(bean.getEndTime());
            tvHint.setText("等待收货");
            tvHintContent.setText("七天未确认收货，系统将自动确认");
            btnCancel.setVisibility(View.INVISIBLE);
        }

        //4：交易完成
        if (bean.getIsFinish() == 4) {
            btnText = "交易完成";
            btn.setOnClickListener(null);
            btnRefund.setVisibility(View.GONE);
            tvCancelRefund.setVisibility(View.GONE);
//            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.text_color_999));

            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon2.setImageResource(R.drawable.deal_pay_select);
            imgLine2.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon3.setImageResource(R.drawable.deal_maijia_select);
            imgLine3.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon4.setImageResource(R.drawable.deal_icon_finish_select);
            tvProcess1.setSelected(true);
            tvProcess2.setSelected(true);
            tvProcess3.setSelected(true);
            tvProcess4.setSelected(true);
            tvProcess5.setVisibility(View.GONE);
            LayoutCancel.setVisibility(View.VISIBLE);
            layoutDaojishi.setVisibility(View.INVISIBLE);
            tvFinish.setVisibility(View.VISIBLE);
            tvProcess5.setVisibility(View.GONE);
        }
        btn.setText(btnText);
        //超过七天，交易关闭 ，自动退款
        if (bean.getIsFinish() == 5) {
            LayoutCancel.setVisibility(View.VISIBLE);
            btn.setOnClickListener(null);
            btnRefund.setVisibility(View.GONE);
            tvCancelRefund.setVisibility(View.GONE);
//            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
//            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            tvProcess5.setVisibility(View.GONE);
            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            tvProcess1.setSelected(true);
            tvHint.setText("交易关闭");
            tvEndTime.setVisibility(View.GONE);
            tvHintContent.setText(bean.getUnagremark());
            btnCancel.setVisibility(View.INVISIBLE);
            btn.setText("交易已关闭");
        }
    }

    @Override
    public void showCancelSuccress() {
        ToastUtil.showShort(this, "取消订单成功");
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA, id);
        setResult(Constant.ResponseCode.ORDER_CANCEL, intent);
        EventBusUtil.post(new EventClickBean("", true));
        finish();
    }

    @Override
    public void showCancelFailed(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    public void showEndTime(boolean isEnd, String time) {
        if (isEnd) {
            isEndTime = isEnd;
            tvHint.setText("支付超时");
            tvEndTime.setVisibility(View.INVISIBLE);
            btn.setText("支付超时");
            btn.setBackgroundColor(getResources().getColor(R.color.gray));

        } else {
            tvEndTime.setText("剩余时间: " + time);
        }
    }

    @Override
    public void showConfirmSuccess() {
        mPresenter.getData(id);
        EventBusUtil.post(new EventClickBean("", true));
    }

    @Override
    public void showConfirmFailed(String message) {
    }

    @Override
    public void showChatRoom(ChatRoomBean bean) {
        this.chatRoomBean = bean;

        //有聊天室就进入聊天室
        if (chatRoomBean != null && chatRoomBean.getChatId() != null) {
            getBaseLoadingView().hideLoading();
            String title = this.bean.getTradeInfoTitle();
            RongIM.getInstance().startGroupChat(this, chatRoomBean.getChatId(), title);

            //没有聊天室创建聊天室
        } else {
            final String title = this.bean.getTradeInfoTitle();

            Map<String, String> map = new HashMap<>();
            map.put("token", UserManager.getInstance().getToken());
            map.put("groupName", title);
            map.put("type", "1");//群聊类型：type：0（默认）普通群聊，type：1带客服群聊
            map.put("userCode", UserManager.getInstance().getUserCode() + "," + this.bean.getUserCode());
            if (!TextUtils.isEmpty(this.bean.getTradeInfoPic()))
                map.put("groupPic", this.bean.getTradeInfoPic());

            mMessageSource.createGroup(map, new ApiCallBack<MessageBean>() {
                @Override
                public void onSuccess(MessageBean data) {
                    getBaseLoadingView().hideLoading();
                    //向服务器上传讨论组id
                    HashMap<String, String> map = new HashMap<>();
                    String token = UserManager.getInstance().getToken();
                    map.put("token", token);
                    map.put("orderId", OrderDetailedBuyerActivity.this.bean.getOrderId());
                    map.put("chatId", data.getGroupChatId());
                    mPresenter.saveChatRoom(map);

                    RongIM.getInstance().startGroupChat(OrderDetailedBuyerActivity.this, data.getGroupChatId(), title);
                }

                @Override
                public void onError(String errorCode, String message) {
                    ToastUtil.showShort(OrderDetailedBuyerActivity.this, "创建讨论组失败 : " + message);
                }
            });
        }
    }

    @Override
    public void showGetChatRoomFailed(String info) {
        LoginErrorCodeUtil.showHaveTokenError(this, info, "");
    }

    @Override
    public void showSaveChatSuccess() {
    }

    @Override
    public void showSaveChatFailed(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void setPresenter(OrderDetailedBuyerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
    }

    @Override
    public void showReLoad() {
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(id);
            }
        });
    }

    @Subscribe(priority = 1, threadMode = ThreadMode.MAIN)
    public void onEvent(EventDealBean bean) {
        Subscription sub = Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mPresenter.getData(id);
                    }
                });

        RxTaskHelper.getInstance().addTask(this, sub);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PayConstant.PAY_RESULT) {
            mPresenter.getData(id);
            EventBusUtil.post(new EventClickBean("", true));
        }

        if (resultCode == RefundActivity.RESULT) {
            LayoutCancel.setVisibility(View.VISIBLE);
            tvHint.setText("交易关闭");
            tvHintContent.setText("申请退款中");
            tvEndTime.setVisibility(View.INVISIBLE);
            btn.setText("交易已关闭");
            btn.setOnClickListener(null);
            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            btnCancel.setVisibility(View.INVISIBLE);
            mPresenter.getData(id);
        }

    }

    private AlertDialog dialog;

    //取消订单
    private void cancelOrder() {
        dialog = DialogUtil.showInputDialog(this, false, "", "确认取消订单？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseLoadingView().showLoading(true);
                mPresenter.cancelOrder(id);
                dialog.dismiss();
            }
        });
    }

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, OrderDetailedBuyerActivity.class);
        intent.putExtra(Constant.INTENT_DATA, id);
        context.startActivity(intent);
    }
}
