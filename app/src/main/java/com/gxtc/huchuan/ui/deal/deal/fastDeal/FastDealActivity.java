package com.gxtc.huchuan.ui.deal.deal.fastDeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.bean.FastDealBean;
import com.gxtc.huchuan.bean.event.EventUpdataAddrBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.pop.PopPosition;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity;
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity;
import com.gxtc.huchuan.ui.mine.address.AddAddressActivity;
import com.gxtc.huchuan.ui.mine.address.SelectAddressActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.AndroidBug5497Workaround;
import com.gxtc.huchuan.widget.NumberAddandSubView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * 快速交易页面
 */
public class FastDealActivity extends BaseTitleActivity implements View.OnClickListener,FastDealContract.View{


    //收获地址相关
    @BindView(R.id.switch_address)          SwitchCompat switchBtn;
    @BindView(R.id.tv_name)                 TextView tvName;
    @BindView(R.id.tv_phone)                TextView tvPhone;
    @BindView(R.id.tv_is_default)           TextView tvDefault;
    @BindView(R.id.tv_address)              TextView tvAddress;
    @BindView(R.id.tv_danbao)               TextView tvDanbao;
    @BindView(R.id.tv_zhekou)               TextView tvZhekou;
    @BindView(R.id.tv_total_money)          TextView tvTotal;
    @BindView(R.id.tv_total_sub_money)      TextView tvSubTotal;
    @BindView(R.id.tv_select_address)       TextView btnAddress;
    @BindView(R.id.btn_ok)                  TextView btnOk;
    @BindView(R.id.edit_message)            EditText editMsg;
    @BindView(R.id.rl_address_btn)          View selectAddress;

    @BindView(R.id.tv_money)          TextView            tvMoney;
    @BindView(R.id.tv_sub_money)      TextView            tvSubMoney;
    @BindView(R.id.tv_goods_name)     TextView            tvGoodsName;
    @BindView(R.id.tv_goods_sub_name) TextView            tvGoodsSubName;
    @BindView(R.id.tv_number)          TextView            tvNumber;
    @BindView(R.id.deal_cover)        ImageView           DealCover;
    @BindView(R.id.amount_view)       NumberAddandSubView mNumber;

    @BindView(R.id.root_view)               View rootView;

    private boolean isDefult = false;
    private boolean isDanbao = true;       //是否是买家出担保费用 ,0 买家担保  1卖家担保

    private int id;
    private double dbFree = 0  ;    // 担保费用
    private double discount = 0  ;  // 担保费用
    private double total = 0  ;     // 商品实际价格
    private double money = 0  ;     // 商品价格
    private String isPost = "0" ;            //是否需要物流  0 不需要   1需要
    private String picUrl;

    private AddressBean currAddress;
    private List<AddressBean> addBeans;
    private FastDealBean mbean;

    private FastDealContract.Presenter mPresenter;

    private PopPosition pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_deal);
        EventBusUtil.register(this);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_fast_deal));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("帮助",this);

        pop = new PopPosition(this,R.layout.pop_ts_position);
        pop.setData(new String[]{"买方承担","卖方承担"});
        pop.setTitle("担保费用");
    }


    @Override
    public void initListener() {
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectAddress.setVisibility(View.VISIBLE);
                    isPost = "1";
                }else{
                    selectAddress.setVisibility(View.GONE);
                    isPost = "0";
                }
            }
        });
        pop.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                isDanbao = newVal == 0 ;

                if(isDanbao){
                    total = mPresenter.computeTotal(money,dbFree,discount,isDanbao);
                    tvTotal.setText("¥" + total);
                    tvSubTotal.setText("¥" + total);
                    tvDanbao.setText("¥" + dbFree);
                }else{
                    total = mPresenter.computeTotal(money,0,0,isDanbao);
                    tvTotal.setText("¥" + total);
                    tvSubTotal.setText("¥" + total);
                    tvDanbao.setText("¥0.00");
                    tvZhekou.setText("¥0.00");
                }
            }
        });
    }

    @OnClick({R.id.rl_address_btn,R.id.tv_danbao,R.id.btn_ok,R.id.coustomers})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.headBackButton:
                finish();
                break;

            //去选择收获地址
            case R.id.rl_address_btn:
                gotoAddress();
                break;

            //担保费用
            case R.id.tv_danbao:
                pop.showPopOnRootView(this);
                break;

            //提交订单
            case R.id.btn_ok:
                submit();
                break;

            //帮助
            case R.id.headRightButton:
                CommonWebViewActivity.startActivity(this,Constant.ABOUTLINK + "2","帮助");
                break;
            //客服
            case R.id.coustomers:
                MallCustomersActivity.Companion.goToCustomerServicesActivity(this,MallCustomersActivity.Companion.getCUSTOMERS_TYPE_OF_DEAL(),MallCustomersActivity.Companion.getCUSTOMERS_STATUS_SHOW_LIST());
                break;

        }
    }

    @Override
    public void initData() {
        id = getIntent().getIntExtra(Constant.INTENT_DATA,0);
        new FastDealPresenter(this);
        String token = UserManager.getInstance().getToken();
        mPresenter.getAddressList(token);
        mPresenter.getOrderData(UserManager.getInstance().getToken(),id);
    }



    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showAddress(List<AddressBean> beans) {
        tvName.setVisibility(View.VISIBLE);
        tvPhone.setVisibility(View.VISIBLE);
        tvAddress.setVisibility(View.VISIBLE);
        btnAddress.setVisibility(View.INVISIBLE);

        addBeans = beans;

        boolean flag = false;
        for (int i = 0; i < beans.size(); i++) {
            AddressBean bean = beans.get(i);

            String isDefult = bean.getIsdefault();
            //是默认地址
            if ("1".equals(isDefult)) {
                flag = true;
                currAddress = bean;
                beans.get(i).setSelect(true);
                showBean(bean);
                break;
            }

        }

        //假如没有默认地址   取第一个
        if (!flag) {
            beans.get(0).setSelect(true);
            currAddress = beans.get(0);
            showBean(currAddress);
        }

    }

    @Override
    public void showOrderData(final FastDealBean bean) {
        rootView.setVisibility(View.VISIBLE);
        tvGoodsName.setText(bean.getTradeInfoTitle());
       // tvGoodsSubName.setText(bean.getTradeInfoTitle());
         mNumber.setMax(bean.getNum());//设置可选最大商品数量
        dbFree = bean.getDbfee();
        discount = bean.getDiscount();
        money = bean.getTradeAmt();
        tvNumber.setText("数量:"+bean.getNum());
        final DecimalFormat df = new DecimalFormat("#0.00");
        if("0.00".equals(df.format(money))){
            tvMoney.setText("0.00");
        }else {
            tvMoney.setText("价格¥：" + df.format(money));
        }
        tvSubMoney.setText("¥" +  df.format(money));
        tvDanbao.setText("¥" + df.format(dbFree));
        tvZhekou.setText("-¥" + df.format(discount));
        ImageHelper.loadImage(this, DealCover, bean.getPicUrl());

        total = mPresenter.computeTotal(money,dbFree,discount,isDanbao);
        tvTotal.setText("¥" + df.format(total));
        tvSubTotal.setText("¥" + df.format(total));
        mNumber.setOnNumChangeListener(new NumberAddandSubView.OnNumChangeListener() {
            @Override
            public void onNumChange(View view, int num) {
                money = bean.getTradeAmt() * num;
                if(isDanbao){
                    dbFree = bean.getDbfee() * num;
                    discount = bean.getDiscount() * num;
                    total = mPresenter.computeTotal(money,dbFree,discount,isDanbao);
                    tvTotal.setText("¥" + df.format(total));
                    tvSubTotal.setText("¥" + df.format(total));
                    tvSubMoney.setText("¥" +  df.format(money));
                    tvZhekou.setText("-¥" + df.format(discount));
                    tvDanbao.setText("¥" + df.format(dbFree));
                }else {
                    //卖家负担保费，不要担保费和折扣
                    total = mPresenter.computeTotal(money,0,0,isDanbao);
                    tvSubMoney.setText("¥" +  df.format(money));
                    tvTotal.setText("¥" + total );
                    tvSubTotal.setText("¥" + total);
                }
                buyNum = num;
            }
        });

    }

    @Override
    public void showSubmitSuccess(Integer id) {
        GotoUtil.goToActivity(this, OrderDetailedBuyerActivity.class,0,String.valueOf(id));
        finish();
    }

    @Override
    public void showSubmitFailed(String info) {
        ToastUtil.showShort(this,info);
    }


    @Override
    public void setPresenter(FastDealContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        isDefult = true;
        tvName.setVisibility(View.INVISIBLE);
        tvPhone.setVisibility(View.INVISIBLE);
        tvDefault.setVisibility(View.INVISIBLE);
        tvAddress.setVisibility(View.INVISIBLE);
        btnAddress.setVisibility(View.VISIBLE);

        if(addBeans != null){
            addBeans.clear();
        }
        currAddress = null;
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //添加新的收货地址
            case Constant.ResponseCode.ADD_ADDRESS:
                getAddressList();
                break;

            //选择地址
            case Constant.ResponseCode.SELECT_ADDRESS:
                if(data != null){
                    addBeans = (List<AddressBean>) data.getSerializableExtra(Constant.INTENT_DATA);
                    for(AddressBean bean : addBeans){
                        if(bean.isSelect()){
                            showBean(bean);
                            break;
                        }
                    }
                }
                break;

            case Constant.ResponseCode.LOGINRESPONSE_CODE:
                getAddressList();
                break;

        }

    }

    /**
     * 修改地址回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenUpdataAddress(EventUpdataAddrBean event) {
        AddressBean tag = event.bean;
        if (tag.getId().equals(currAddress.getId()) || TextUtils.isEmpty(tag.getId())) {
            currAddress = tag;
            displayAddress(currAddress);
        }
    }

    /**
     * 提交订单
     */
    private int buyNum = 1;
    private void submit(){
        String token = UserManager.getInstance().getToken();
        String tradeInfoId = id + "";
        String tradeType = "0";

        String isPost = this.isPost;
        String buyWay = isDanbao ? "0" : "1";
        String msg = editMsg.getText().toString();

        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("tradeInfoId",tradeInfoId);
        map.put("tradeType",tradeType);
        map.put("buyWay",buyWay);
        map.put("isPost",isPost);
        map.put("tradeSource","0");//创建来源：0常规帖， 1资源交易主页创建的快速交易帖， 2个人主页创建的快速交易帖子
        map.put("buyNum",buyNum + "");

        if(isPost.equals("1") && currAddress == null){
            ToastUtil.showShort(this,"您还没选择地址");
            return;
        }

        if(isPost.equals("1") && currAddress != null){
            String takeAddrId = currAddress.getId();
            map.put("takeAddrId",takeAddrId);
        }

        if(!TextUtils.isEmpty(msg)){
            if(msg.length() > 140){
                ToastUtil.showShort(this,"留言不能超过140个字");
                return;
            }else {
                map.put("message",msg);
            }

        }
        mPresenter.submitOrder(map);

    }


    private void showBean(AddressBean bean){
        String name = bean.getName();
        String phone = bean.getPhone();
        String address = bean.getProvince() + bean.getCity();
        String area = bean.getArea();
        if (!TextUtils.isEmpty(area)) {
            address += area;
        }
        address += bean.getAddress();

        //是默认地址
        String isDefult = bean.getIsdefault();
        if ("1".equals(isDefult)) {
            tvDefault.setVisibility(View.VISIBLE);
        }else{
            tvDefault.setVisibility(View.INVISIBLE);
        }
        tvName.setText(name);
        tvPhone.setText(phone);
        tvAddress.setText(address);
    }

    private void gotoAddress(){
        //没有收货地址
        if(addBeans == null ){
            Intent addIntent = new Intent(this,AddAddressActivity.class);
            addIntent.putExtra(FastDealContract.INTENT_ISDEFULT,isDefult);
            startActivityForResult(addIntent,101);

            //选择收货地址
        }else{
            Intent selectIntent = new Intent(this,SelectAddressActivity.class);
            ArrayList<AddressBean> temp = new ArrayList<>();
            temp.addAll(addBeans);
            selectIntent.putExtra(Constant.INTENT_DATA,temp);
            startActivityForResult(selectIntent,101);

        }
    }

    private void getAddressList() {
        if (UserManager.getInstance().isLogin(this)) {
            String token = UserManager.getInstance().getToken();
            mPresenter.getAddressList(token);
        }
    }

    private void displayAddress(AddressBean bean) {
        String name = currAddress.getName();
        String phone = currAddress.getPhone();
        String address = currAddress.getProvince() + currAddress.getCity();
        String area = currAddress.getArea();
        if (!TextUtils.isEmpty(area)) {
            address += area;
        }
        address += currAddress.getAddress();

        tvName.setText(name);
        tvPhone.setText(phone);
        tvAddress.setText(address);

        if ("1".equals(currAddress.getIsdefault())) {
            tvDefault.setVisibility(View.VISIBLE);
        } else {
            tvDefault.setVisibility(View.INVISIBLE);
        }
    }

}

