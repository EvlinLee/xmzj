package com.gxtc.huchuan.ui.circle.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.bean.event.EventCreateCirlceBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.dialog.CircleRuleDialog;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.pop.PopPosition;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by sjr on 2017/4/26.
 * 设置创建圈子的属性
 */

public class CreateCirclePropertyActivity extends BaseTitleActivity implements
        View.OnClickListener {

    @BindView(R.id.choose_group)       RadioGroup  chooseGroup;
    @BindView(R.id.choose_creat_group) RadioGroup  chooseCreatGroup;
    @BindView(R.id.choose_classify)   TextView    classify;
    @BindView(R.id.img_free)           ImageView   imgFree;
    @BindView(R.id.img_pay)            ImageView   imgPay;
    @BindView(R.id.tv_free)            TextView    tvFree;
    @BindView(R.id.tv_pay)             TextView    tvPay;

    public  int groupType = -1;      //圈子类型
    public  int isPubluc  = -1;      //0、公开，1、私密
    private int payType   = -1;       //圈子付费类型

    public PopPosition      popType;
    public CircleRepository mCircleRepository;
    public String[]         groupTypeData;
    public List<CircleBean> groupTypeList;
    private int createGroupChat = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle_property);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_create_circle_property));
        getBaseHeadView().showBackButton(this);
        getBaseHeadView().showHeadRightButton("下一步", this);

        popType = new PopPosition(this, R.layout.pop_ts_position);
        popType.setTitle("选择分类");
        chooseGroup.clearCheck();//初始状态都不选中
        chooseCreatGroup.clearCheck();

        CircleRuleDialog dialog = new CircleRuleDialog();
        dialog.show(getSupportFragmentManager(), "CircleRuleDialog");
    }

    @Override
    public void initListener() {
        chooseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //公开
                    case R.id.rb_public:
                        isPubluc = 0;
                        break;

                    //私密
                    case R.id.rb_private:
                        isPubluc = 1;
                        break;
                }
            }
        });

        popType.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                if (groupTypeData != null && groupTypeData.length > 0) {
                    classify.setText(popType.getData()[newVal]);
                    groupType = groupTypeList.get(newVal).getId();
                }
            }
        });

        chooseCreatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {

                switch (checkedId) {
                    //创建群聊
                    case R.id.rb_creat:
                        createGroupChat  = 0;
                        break;

                    //不创建群聊
                    case R.id.rb_not_creat:
                        createGroupChat  = 1;
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        mCircleRepository = new CircleRepository();
        getGrouopType();
    }

    private void getGrouopType() {
        mCircleRepository.getListType(new ApiCallBack<List<CircleBean>>() {
            @Override
            public void onSuccess(List<CircleBean> data) {
                if (data == null || data.size() == 0) {
                    popType.setData(
                            new String[]{"全部", "初级小白", "网赚项目", "公众号运营", "项目投资", "淘宝电商", "微商"});
                } else {
                    groupTypeList = data;
                    groupTypeData = new String[data.size()];
                    getData(data);
                }
            }

            @Override
            public void onError(String errorCode, String message) {
               ToastUtil.showShort(MyApplication.getInstance(),message);
            }
        });
    }

    private void getData(List<CircleBean> data) {
        for (int j = 0; j < data.size(); j++) {
            groupTypeData[j] = data.get(j).getTypeName();
        }
        popType.setData(groupTypeData);
    }

    @OnClick({R.id.img_free, R.id.choose_classify, R.id.img_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;

            case R.id.headRightButton:
                next();
                break;

            //免费圈子
            case R.id.img_free:
                selectFreeCircle();
                break;

            //付费圈子
            case R.id.img_pay:
                selectPayCircle();
                break;

            //选择分类
            case R.id.choose_classify:
                popType.showPopOnRootView(this);
                break;
        }
    }

    private void next() {
        if(payType == -1){
            ToastUtil.showShort(this,"请选择圈子付费类型");
            return;
        }

        if (groupType == -1) {
            ToastUtil.showShort(this, "请选择圈子类型");
            return;
        }

        if (isPubluc == -1) {
            ToastUtil.showShort(this, "请选择圈子是否公开");
            return;
        }

        if (createGroupChat == -1) {
            ToastUtil.showShort(this, "请选择圈子是否开启群聊");
            return;
        }

        //创建免费圈子
        if(payType == 0){
            gotoActivity(CreateFreeCircleActivity.class);

        //创建付费圈子
        }else{
            gotoActivity(CreateNoFreeCircleActivity.class);
        }

    }

    private void selectPayCircle() {
        imgPay.setImageResource(R.drawable.circle_property_icon_pay_select);
        tvPay.setTextColor(getResources().getColor(R.color.colorAccent));
        imgFree.setImageResource(R.drawable.circle_property_icon_free);
        tvFree.setTextColor(getResources().getColor(R.color.text_color_666));
        payType = 1;
    }

    private void selectFreeCircle() {
        imgPay.setImageResource(R.drawable.circle_property_icon_pay);
        tvPay.setTextColor(getResources().getColor(R.color.text_color_666));
        imgFree.setImageResource(R.drawable.circle_property_icon_free_select);
        tvFree.setTextColor(getResources().getColor(R.color.colorAccent));
        payType = 0;
    }

    public void gotoActivity(Class target) {
        Intent intent = new Intent(this, target);
        intent.putExtra("isPubluc", isPubluc);
        intent.putExtra("groupType", groupType);
        intent.putExtra("createGroupChat", createGroupChat);
        startActivity(intent);
    }

    //创建圈子成功结束这个界面
    @Subscribe
    public void onEvent(EventCreateCirlceBean bean){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
