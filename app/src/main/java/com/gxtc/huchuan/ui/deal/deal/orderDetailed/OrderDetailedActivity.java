package com.gxtc.huchuan.ui.deal.deal.orderDetailed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.ClipboardUtil;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.JumpPermissionManagement;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * 卖家订单详情
 */
public class OrderDetailedActivity extends BaseTitleActivity implements View.OnClickListener,OrderDetailedContract.View {

    @BindView(R.id.layout_order_detailed)    View rootView;

    @BindView(R.id.img_deal_icon1)      ImageView       imgIcon1;
    @BindView(R.id.img_deal_icon2)      ImageView       imgIcon2;
    @BindView(R.id.img_deal_icon3)      ImageView       imgIcon3;
    @BindView(R.id.img_deal_icon4)      ImageView       imgIcon4;
    @BindView(R.id.img_deal_line1)      ImageView       imgLine1;
    @BindView(R.id.img_deal_line2)      ImageView       imgLine2;
    @BindView(R.id.img_deal_line3)      ImageView       imgLine3;
    @BindView(R.id.tv_process1)         TextView        tvProcess1;
    @BindView(R.id.tv_process2)         TextView        tvProcess2;
    @BindView(R.id.tv_process3)         TextView        tvProcess3;
    @BindView(R.id.tv_process4)         TextView        tvProcess4;
    @BindView(R.id.tv_process5)         TextView        tvProcess5;

    //底下三个按钮
    @BindView(R.id.btn_finish)          Button           btn;
    @BindView(R.id.btn_cancel)          Button           btnCancel;
    @BindView(R.id.btn_deal_argeen)     Button          btnAgreen;
    @BindView(R.id.layout_agreen)       LinearLayout    layoutAgreen;
    @BindView(R.id.layout_bottom)       RelativeLayout  layoutBottom;
    @BindView(R.id.layout_adder)        View             layoutAdder;
    @BindView(R.id.layout_wuliu)        View             layoutWuliu;
    @BindView(R.id.tv_addr)              TextView         tvAdder;

    @BindView(R.id.tv_name)             TextView        tvName;
    @BindView(R.id.tv_goods_name)       TextView        tvGoodsName;
    @BindView(R.id.tv_money)            TextView        tvMoney;
    @BindView(R.id.tv_number)           TextView        tvNumber;
    @BindView(R.id.tv_time)             TextView        tvTime;
    @BindView(R.id.tv_danbao)           TextView        tvDanbao;
    @BindView(R.id.tv_discount)         TextView        tvZhekou;
    @BindView(R.id.tv_total_money)      TextView        tvTotal;
    @BindView(R.id.tv_comment)          TextView        tvMsg;

    @BindView(R.id.layout_cancel)       View            LayoutCancel;
    @BindView(R.id.tv_finish)           TextView        tvFinish;
    @BindView(R.id.tv_cancel)           TextView        tvCancel;

    @BindView(R.id.layout_phone)        LinearLayout    layoutPhone;
    @BindView(R.id.tv_phone)            TextView        tvPhone;
    @BindView(R.id.tv_goods)            TextView        tvGoods;
    @BindView(R.id.img_phone)           ImageView       imgPhone;
    @BindView(R.id.tv_endtime)          TextView       tvEndTime;

    private boolean isAgree = false;
    private double total = 0;
    private String id;
    private OrderDetailedBean bean;
    private ChatRoomBean      chatRoomBean;

    private MessageSource mMessageSource;
    private OrderHelperDialog helperDialog;
    //卖家同意(不同意)退款申请
    private String remark = "";
    private String isAgreeRefunds = "";
    private HashMap<String,String> map;
    private OrderDetailedContract.Presenter mPresenter;
    private AlertDialog phoneDialog;
    private AlertDialog dialog;
    private String msg = "";
    private List<TextView> textViews;
    private TextView btnRejectCancel;
    private TextView btnRejectOk;

    private AlertDialog cancelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detailed);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        map = new HashMap<>();
        getBaseHeadView().showTitle(getString(R.string.title_order_detailed));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightImageButton(R.drawable.navigation_icon_help,this);
        ImageButton imageView = new ImageButton(this);
        imageView.setImageResource(R.drawable.navigation_icon_kefu);
        imageView.setId(R.id.img);
        imageView.setOnClickListener(this);
        imageView.setBackgroundColor(getResources().getColor(R.color.transparent));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2,-1);
        imageView.setLayoutParams(params);
        getBaseHeadView().getHeadRightLayout().addView(imageView,0);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(bean == null) return false;
                ClipboardUtil.copyText( bean.getOrderId());
                return false;
            }
        });
    }

    @OnClick({R.id.btn_cancel,
            R.id.btn_deal_argeen,
            R.id.tv_contact,
            R.id.look_more,
            R.id.tv_goods,
            R.id.img_phone})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            //确认交付接口
            case R.id.btn_finish:
                confirmDeal();
                break;

            //显示不同意交易dialog
            case R.id.btn_cancel:
                if(bean.getIsRefund() == 1){   //卖家不同意申请退款
                    showDialog();
                }else {
                    isAgree = false;
                    showCancelDialog();
                }
                break;

            // 显示同意交易dialog
            case R.id.btn_deal_argeen:
                if(bean.getIsRefund() == 1){  //卖家同意申请退款
                    isAgreeRefunds = "1";
                    agreAplayRefund();
                }else {
                    isAgree = true;
                    showAgreeDialog();
                }
                break;

            //同意交易
            case R.id.tv_agree:
                submitAgreeDeal();
                break;

            case R.id.tv_reject_cancel:
                cancelDialog.dismiss();
                break;

            //拒绝交易
            case R.id.tv_reject_ok:
                submitAgreeDeal();
                break;

            //联系买家
            case R.id.tv_contact:
                gotoChatRoom();
                break;

            //电话联系
            case R.id.img_phone:
                callPhone();
                break;

            //交易主页
            case R.id.tv_goods:
                if (bean != null) GoodsDetailedActivity.startActivity(this,bean.getTradeInfoId()+"");
                break;

            //帮助
            case R.id.HeadRightImageButton:
                if(helperDialog == null){
                    helperDialog = new OrderHelperDialog(this);
                    helperDialog.anchorView(getBaseHeadView().getHeadRightImageButton())
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
                CommonWebViewActivity.startActivity(this,"https://apps.xinmei6.com/publish/sysInfo.html?type=2","帮助");
                break;

            //客服
            case R.id.img:
                MallCustomersActivity.Companion.goToCustomerServicesActivity(this,MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_DEAL(),MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_LIST());
                break;
            //查看更多
            case R.id.look_more:
                CommonWebViewActivity.startActivity(this,"https://apps.xinmei6.com/publish/tradeRule.html","");
                break;
        }
    }

    private void gotoHomePage(){
        if(bean != null){
            PersonalInfoActivity.startActivity(this, bean.getUserCode());
        }
    }
    //电话联系
    private void callPhone() {
        String[] pers = new String[]{Manifest.permission.CALL_PHONE};
        performRequestPermissions(getString(R.string.txt_call_phone_permission), pers, 2200,
                new PermissionsResultListener() {
                    @Override
                    public void onPermissionGranted() {
                        phoneDialog = DialogUtil.showInputDialog(OrderDetailedActivity.this, false, "", "是否呼叫: " + bean.getPhone() + " ?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try{
                                    Intent mIntent = new Intent(Intent.ACTION_CALL);
                                    mIntent.setData(Uri.parse("tel:" + bean.getPhone()));
                                    startActivity(mIntent);
                                    phoneDialog.dismiss();
                                }catch (SecurityException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        dialog = DialogUtil.showDeportDialog(OrderDetailedActivity.this, false, null, getString(R.string.pre_storage_notice_msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(v.getId() == R.id.tv_dialog_confirm){
                                            JumpPermissionManagement.GoToSetting(OrderDetailedActivity.this);
                                        }
                                        dialog.dismiss();
                                    }
                                });

                    }
                });
    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_classify_input, null);
        final EditText etContent = (EditText) view.findViewById(R.id.et_input);
        dialog = DialogUtil.showRefundDialog(this, true, "请填写原因", "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAgreeRefunds = "2";
                remark = etContent.getText().toString();
                agreAplayRefund();
            }
        },view);
    }


    private  void agreAplayRefund(){
        map.put("token",UserManager.getInstance().getToken());
        map.put("type","0");//退款类型。0，交易，1，课堂，2，圈子
        map.put("orderId",bean.getOrderId());
        map.put("audit",isAgreeRefunds);//1：同意；2：拒绝
        if(!TextUtils.isEmpty(remark))
        map.put("remark",remark);
        Subscription mSub = DealApi.getInstance()
                .sellerAgr(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        switch (isAgreeRefunds){
                            case "1":
                                tvCancel.setText("同意退款成功");
                                ToastUtil.showShort(OrderDetailedActivity.this,"同意退款成功");
                                btnAgreen.setClickable(false);
                                break;
                            case "2":
                                tvCancel.setText("不同意退款");
                                ToastUtil.showShort(OrderDetailedActivity.this,"不同意退款成功");
                                break;
                        }
                        EventBusUtil.post(new EventDealBean());
                        if(dialog != null)
                        dialog .dismiss();
                        tvCancel.setVisibility(View.VISIBLE);
                        LayoutCancel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if(dialog != null)
                         dialog .dismiss();
                        ToastUtil.showShort(OrderDetailedActivity.this,message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,mSub);
    }

    //确认交付
    private void confirmDeal() {
        String token = UserManager.getInstance().getToken();
        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("id",bean.getId() + "");
        map.put("status","3");
        mPresenter.confirmDeal(map);
    }

    //同意交易
    private void submitAgreeDeal() {
        String token = UserManager.getInstance().getToken();
        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("id",bean.getId() + "");
        //同意
        if(isAgree){
            map.put("type","0");
            String money = editAgree.getText().toString();
            if(TextUtils.isEmpty(money)){
                ToastUtil.showShort(this,"商品价格不能为空");
                return;
            }
            map.put("param",money);
            agreeDialog.dismiss();

        //不同意交易
        }else{
            map.put("type","1");
            map.put("param",msg);
            cancelDialog.dismiss();
        }
        mPresenter.submitAgreeOrReject(map);

    }

    //显示不同意弹窗
    private void showCancelDialog() {
        if(cancelDialog == null){
            View view = View.inflate(this,R.layout.dialog_reject_deal,null);
            btnRejectCancel = (TextView) view.findViewById(R.id.tv_reject_cancel);
            btnRejectOk = (TextView) view.findViewById(R.id.tv_reject_ok);
            btnRejectCancel.setOnClickListener(this);
            btnRejectOk.setOnClickListener(this);

            textViews = new ArrayList<>();

            for(int i = 1;i <= 4 ; i++){
                int id = getResources().getIdentifier("tv_reject_"+i,"id",getPackageName());
                TextView tv = (TextView) view.findViewById(id);
                String text = tv.getText().toString();
                tv.setTag(text.substring(0,text.lastIndexOf("、")));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msg = (String) v.getTag();
                        for(TextView tv : textViews){
                            tv.setSelected(false);
                        }
                        v.setSelected(true);
                    }
                });
                textViews.add(tv);
            }
            cancelDialog = DialogUtil.createDialog(this,view);
        }
        cancelDialog.show();
    }


    private TextView btnAgree;
    private EditText editAgree;
    private AlertDialog agreeDialog;
    //显示同意弹窗
    private void showAgreeDialog() {
        if(agreeDialog == null){
            View view = View.inflate(this,R.layout.dialog_order_edit_money,null);
            btnAgree = (TextView) view.findViewById(R.id.tv_agree);
            editAgree = (EditText) view.findViewById(R.id.edit_content);
            agreeDialog = DialogUtil.createDialog(this,view);
            btnAgree.setOnClickListener(this);
        }
        editAgree.setText(bean.getTradeAmt() + "");
        agreeDialog.show();
    }

    private void gotoChatRoom(){
        final User user = UserManager.getInstance().getUser();
        if(user == null){
            GotoUtil.goToActivity(this,LoginAndRegisteActivity.class);
            return;
        }
        getBaseLoadingView().showLoading(true);
        String token = UserManager.getInstance().getToken();
        mPresenter.getChatRoom(token, bean.getOrderId());
    }

    @Override
    public void initData() {
        mMessageSource = new MessageRepository();
        id = getIntent().getStringExtra(Constant.INTENT_DATA);
        new OrderDetailedPresenter(this);
        mPresenter.getData(id);
    }
    @Override
    public void tokenOverdue() {//服务器莫名其妙有时会返回这个错误码
//        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(OrderDetailedBean bean) {
        layoutAgreen.setVisibility(View.INVISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
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
        double zhekou = 0;
        tvZhekou.setText("-¥" + zhekou);

        BigDecimal moneyB = new BigDecimal(money);
        BigDecimal danbaoB = new BigDecimal(danbao);
        BigDecimal zhekouB = new BigDecimal(zhekou);

        layoutPhone.setVisibility(View.VISIBLE);
        imgPhone.setVisibility(View.VISIBLE);
        tvPhone.setText(String.valueOf(bean.getPhone()));
        tvGoods.setVisibility("0".equals(bean.getIsAppointTr()) ? View.VISIBLE : View.GONE );

        //计算总价 = 商品价格 - 担保费用 + 会员折扣
        total = moneyB.subtract(danbaoB).add(zhekouB).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        tvTotal.setText("¥" + total);

        if(!TextUtils.isEmpty(bean.getAddr())){
            layoutAdder.setVisibility(View.VISIBLE);
            layoutWuliu.setVisibility(View.VISIBLE);
            tvAdder.setText(bean.getAddr());
        }else {
            layoutAdder.setVisibility(View.GONE);
            layoutWuliu.setVisibility(View.GONE);
        }
        //0:卖家未同意， 1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，5：超过七天，交易关闭 ，自动退款,10：卖家不同意交易
        btnAgreen.setText("同意交易");
        btnCancel.setText("不同意交易");
        switch (bean.getIsRefund()){
            case 0:
                setFinishStatus(bean);
               break;
            //退款中
            case 1:
                LayoutCancel.setVisibility(View.VISIBLE);
                layoutAgreen.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                btnAgreen.setText("同意退款");
                btnCancel.setText("不同意退款");
                tvCancel.setText("申请退款中, 退款金额: "+bean.getMoney()+"    退款原因:"+bean.getRemark());
                tvProcess5.setVisibility(View.VISIBLE);
                tvProcess5.setText("你的交易买方申请退款，请及时处理");
               break;
            //已退款
            case 2:
                LayoutCancel.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.VISIBLE);
                tvCancel.setText("已退款，交易关闭");
                btn.setText("交易已关闭");
                imgIcon1.setImageResource(R.drawable.deal_pay_select);
                imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
                imgIcon2.setImageResource(R.drawable.deal_pay);
                imgLine2.setImageResource(R.drawable.deal_icon_quotes);
                btn.setOnClickListener(null);
                tvProcess5.setVisibility(View.GONE);
                btn.setBackgroundColor(getResources().getColor(R.color.gray));
                btn.setTextColor(getResources().getColor(R.color.white));
               break;
            //拒绝退款
            case 3:
                LayoutCancel.setVisibility(View.GONE);
                imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
                imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
                imgIcon2.setImageResource(R.drawable.deal_pay_select);
                imgLine2.setImageResource(R.drawable.deal_icon_quotes_select);
                btn.setText("交易已冻结");
                btn.setOnClickListener(null);
                btn.setBackgroundResource(R.drawable.select_gray_round_btn);
                btn.setTextColor(getResources().getColor(R.color.text_color_999));
                btnCancel.setVisibility(View.INVISIBLE);
                tvProcess5.setVisibility(View.VISIBLE);
                tvProcess5.setText("你已拒绝买家退款，订单已冻结，请与买家继续沟通，或者联系平台客服介入");
               break;
        }
    }

    private void setFinishStatus(OrderDetailedBean bean) {
        String btnText = "" ;
        if(bean.getIsFinish() == 10){
            LayoutCancel.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText("交易已拒绝");
            layoutBottom.setVisibility(View.GONE);
            tvProcess5.setVisibility(View.GONE);
            tvProcess1.setText("卖家不同意");
        }

        if(bean.getIsFinish() == 0){
            layoutAgreen.setVisibility(View.VISIBLE);
            tvProcess5.setVisibility(View.VISIBLE);
            tvProcess5.setText("你的商品有买家购买，请同意或拒绝交易");
            btn.setVisibility(View.GONE);
            tvProcess5.setVisibility(View.VISIBLE);
        }

        if(bean.getIsFinish() == 1){
            btnText = "等待买家支付";
            btn.setBackgroundColor(getResources().getColor(R.color.gray));
            tvProcess5.setVisibility(View.GONE);
            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
        }

        //2：买家已支付
        if(bean.getIsFinish() == 2){
            btnText = "确认交付";
            btn.setOnClickListener(this);
            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon2.setImageResource(R.drawable.deal_pay_select);
            tvProcess1.setSelected(true);
            tvProcess2.setSelected(true);
            tvProcess5.setVisibility(View.VISIBLE);
            tvProcess5.setText("你的商品买方已支付完成，请及时发货");
        }

        //3：卖家已交付
        if(bean.getIsFinish() == 3){
            btnText = "等待买家确认";
            LayoutCancel.setVisibility(View.VISIBLE);
            tvFinish.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvEndTime.setVisibility(View.VISIBLE);
            tvFinish.setText("等待买家确认");
            tvCancel.setText("七天未确认收货，系统将自动确认");
            mPresenter.computeEndTime(bean.getEndTime());
            btn.setBackgroundColor(getResources().getColor(R.color.gray));

            imgIcon1.setImageResource(R.drawable.deal_confirm_icon_select);
            imgLine1.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon2.setImageResource(R.drawable.deal_pay_select);
            imgLine2.setImageResource(R.drawable.deal_icon_quotes_select);
            imgIcon3.setImageResource(R.drawable.deal_maijia_select);
            tvProcess1.setSelected(true);
            tvProcess2.setSelected(true);
            tvProcess3.setSelected(true);
            tvProcess5.setVisibility(View.GONE);
        }

        //4：交易完成
        if(bean.getIsFinish() == 4){
            btnText = "交易完成";
            btn.setBackgroundColor(getResources().getColor(R.color.gray));

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
            tvFinish.setText("交易完成");
            tvFinish.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.GONE);
            tvEndTime.setVisibility(View.GONE);
        }
        btn.setText(btnText);
        //5:超过七天，交易关闭 ，自动退款
        if(bean.getIsFinish() == 5){
            LayoutCancel.setVisibility(View.VISIBLE);
            btn.setOnClickListener(null);
            btn.setBackgroundResource(R.drawable.select_gray_round_btn);
            btn.setTextColor(getResources().getColor(R.color.text_color_999));
            tvProcess5.setVisibility(View.GONE);
            tvFinish.setVisibility(View.VISIBLE);
            tvFinish.setText("交易关闭");
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(TextUtils.isEmpty(bean.getUnagremark())?"买家支付超时，订单失效":bean.getUnagremark());
            layoutAgreen.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            tvEndTime.setVisibility(View.GONE);
            btn.setText("交易已关闭");
        }

    }

    @Override
    public void showAgreeOrRejectSuccess() {
        mPresenter.getData(id);
        EventBusUtil.post(new EventClickBean("",true));
    }

    @Override
    public void showAgreeOrRejectFailed(String message) {
        ToastUtil.showShort(this,message);
    }

    @Override
    public void showConfirmSuccess() {
        mPresenter.getData(id);
        EventBusUtil.post(new EventClickBean("",true));
    }

    @Override
    public void showConfirmFailed(String message) {
        ToastUtil.showShort(this,message);
    }

    @Override
    public void showChatRoom(ChatRoomBean bean) {
        this.chatRoomBean = bean;

        //有聊天室就进入聊天室
        if(chatRoomBean != null && chatRoomBean.getChatId() != null){
            getBaseLoadingView().hideLoading();
            String       title = this.bean.getTradeInfoTitle();
            RongIM.getInstance().startGroupChat(this,chatRoomBean.getChatId(),title);


        //没有聊天室创建聊天室
        }else {
            final String title = this.bean.getTradeInfoTitle();

            Map<String,String> map = new HashMap<>();
            map.put("token",UserManager.getInstance().getToken());
            map.put("groupName",title);
            map.put("type","1");//群聊类型：type：0（默认）普通群聊，type：1带客服群聊
            map.put("userCode",UserManager.getInstance().getUserCode() + "," + this.bean.getUserCode());
            if(!TextUtils.isEmpty(this.bean.getTradeInfoPic()))
                map.put("groupPic",this.bean.getTradeInfoPic());

            mMessageSource.createGroup(map, new ApiCallBack<MessageBean>() {
                @Override
                public void onSuccess(MessageBean data) {
                    getBaseLoadingView().hideLoading();
                    //向服务器上传讨论组id
                    HashMap<String,String> map = new HashMap<>();
                    String token = UserManager.getInstance().getToken();
                    map.put("token",token);
                    map.put("orderId",OrderDetailedActivity.this.bean.getOrderId());
                    map.put("chatId",data.getGroupChatId());
                    mPresenter.saveChatRoom(map);

                    RongIM.getInstance().startGroupChat(OrderDetailedActivity.this,data.getGroupChatId(),title);
                }

                @Override
                public void onError(String errorCode, String message) {
                    ToastUtil.showShort(OrderDetailedActivity.this,"创建讨论组失败 : " + message);
                }
            });
        }
    }

    @Override
    public void showGetChatRoomFailed(String info) {
        LoginErrorCodeUtil.showHaveTokenError(this,info,"");
    }

    @Override
    public void showSaveChatSuccess() {}

    @Override
    public void showSaveChatFailed(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showEndTime(boolean isEnd, String time) {
        if(!isEnd){
            tvEndTime.setText("剩余时间: " + time);
        }else {
            tvFinish.setText("交易完成");
            tvFinish.setVisibility(View.VISIBLE);
            tvEndTime.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(OrderDetailedContract.Presenter presenter) {
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
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

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
    public void onEvent(EventDealBean bean){
        LogUtil.i("EventDealBean  ==========");
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
        mMessageSource.destroy();
        EventBusUtil.unregister(this);
        RxTaskHelper.getInstance().cancelTask(this);
    }

    public static void startActivity(Context context, String id){
        Intent intent = new Intent(context,OrderDetailedActivity.class);
        intent.putExtra(Constant.INTENT_DATA,id);
        context.startActivity(intent);
    }

}
