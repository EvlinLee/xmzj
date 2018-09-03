package com.gxtc.huchuan.ui.mine.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gxtc.commlibrary.BaseUserView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.ui.deal.deal.fastDeal.FastDealContract;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.dialog.ProvinceCityAreaDialog;
import com.gxtc.huchuan.utils.RegexUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ALing on 2016/12/26 0026.
 * 添加收获地址
 */
public class AddAddressActivity extends BaseTitleActivity implements View.OnClickListener,BaseUserView, ProvinceCityAreaDialog.OnCitySelectListener {

    @BindView(R.id.et_name)             EditText        etName;
    @BindView(R.id.et_phone)            EditText        etPhone;
    @BindView(R.id.et_address)          EditText        etAddress;
    @BindView(R.id.tv_area)             TextView        tvArea;
    @BindView(R.id.tv_set_default)      TextView        tvSetDefault;
    @BindView(R.id.sw_address_default)  Switch          swDefault;
    @BindView(R.id.ll_msg)              LinearLayout    llMsg;
    @BindView(R.id.rl_add_address)      RelativeLayout  rl;


    private String province;
    private String city;
    private String area;
    private String type = "1";      //保存类型 1：新增，2：修改
    private String defult = "0";
    private boolean isDefult = false;

    private DealSource mData;

    private ProvinceCityAreaDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_address);
    }

    @Override
    public void initListener() {
        getBaseHeadView().showTitle(getString(R.string.title_add_address));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("保存", this);
    }
    @OnClick({R.id.tv_area,
            R.id.sw_address_default,
            R.id.rl_add_address})
    public void onClick(View view){

        WindowUtil.closeInputMethod(this);
        switch (view.getId()){
            case R.id.headBackButton:
                finish();
                break;

            //保存
            case R.id.headRightButton:
                saveAddress();
                break;

            //选择城市
            case R.id.tv_area:
                showChooseProvinceCityArea();
                break;

            //是否是默认地址
            case R.id.sw_address_default:
                isDefult = swDefault.isChecked();
                defult = isDefult ? "1":"0";
                break;
        }
    }


    //保存地址
    private void saveAddress() {
        String token = UserManager.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            GotoUtil.goToActivity(this,LoginAndRegisteActivity.class);
            return ;
        }


        String addr = etAddress.getText().toString();
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.showShort(this,"收货人姓名不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort(this,"联系电话不能为空");
            return;
        }
        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtil.showShort(this, getString(R.string.incorrect_phone_format));
            return;
        }

        if (TextUtils.isEmpty(addr)) {
            ToastUtil.showShort(this,"详细地址不能为空");
            return;
        }

        if (addr != null && addr.length() < 5) {
            ToastUtil.showShort(this,"详细地址不能少于5个字");
            return;
        }

        if (TextUtils.isEmpty(province)) {
            ToastUtil.showShort(this,"请选择所在省份");
            return;
        }

        if (TextUtils.isEmpty(city)) {
            ToastUtil.showShort(this,"请选择所在城市");
            return;
        }


        final AddressBean bean = new AddressBean(token,name,phone,province,city,addr,defult,type,area);
        HashMap<String,String > map = new HashMap<String,String>();
        map.put("token",token);
        map.put("name",name);
        map.put("phone",phone);
        map.put("isDefalut",defult);
        map.put("province",province);
        map.put("city",city);
        map.put("area",area);
        map.put("address",addr);

        showLoad();
        mData.addAddress(map, new ApiCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                showLoadFinish();
                ToastUtil.showShort(AddAddressActivity.this,"添加地址成功");
                Intent intent = new Intent();
                intent.putExtra(Constant.INTENT_DATA,bean);
                setResult(Constant.ResponseCode.ADD_ADDRESS,intent);
                finish();
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(AddAddressActivity.this,errorCode,message);
            }
        });


    }

    @Override
    public void initData() {
        isDefult = getIntent().getBooleanExtra(FastDealContract.INTENT_ISDEFULT,false);
        defult = isDefult ? "1":"0";
        mData = new DealRepository();
    }

    private void showChooseProvinceCityArea() {
        if(dialog == null){
            dialog = new ProvinceCityAreaDialog(this);
            dialog.setOnCitySelectListener(this);
        }
        dialog.show();

    }


    @Override
    public void showNetError() {
        ToastUtil.showShort(this,getString(R.string.empty_net_error));
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this,info);
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading(true);
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showReLoad() {}

    @Override
    public void setPresenter(Object presenter) {}


    @Override
    public void onCitySelect(String province, String city, String area) {
        this.province = province;
        this.city = city;
        this.area = TextUtils.isEmpty(area) ? "": area;
        tvArea.setText(province + " " + city + " " + this.area);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this,LoginAndRegisteActivity.class);
    }
}
