package com.gxtc.huchuan.ui.deal.liuliang.takeOrder;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.pop.PopPosition;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * 接单设置
 */
public class TakeSettingActivity extends BaseTitleActivity implements View.OnClickListener, TimePickerView.OnTimeSelectListener {


    @BindView(R.id.tv_ts_position)          TextView        tvPosition;
    @BindView(R.id.tv_ts_time)              TextView        tvTime;
    @BindView(R.id.btn_ts)                  TextView        btnTs;
    @BindView(R.id.btn_ts_position)         RelativeLayout  btnPosition;
    @BindView(R.id.btn_ts_time)             RelativeLayout  btnTime;

    private PopPosition popPosition;
    private TimePickerView timePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_setting);

    }


    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_take_setting));
        getBaseHeadView().showBackButton(this);
    }

    @OnClick({R.id.btn_ts_position,R.id.btn_ts_time,R.id.btn_ts})
    @Override
    public void onClick(View v){
        switch (v.getId()){
            //选择推送位置
            case R.id.btn_ts_position:
                showPositionPop();
                break;

            //选择推送时间
            case R.id.btn_ts_time:
                showTimePop();
                break;

            //生成订单
            case R.id.btn_ts:

                break;

            case R.id.headBackButton:
                finish();
                break;
        }
    }

    //选择广告位置
    private void showPositionPop(){
        if(popPosition == null){
            popPosition = new PopPosition(this,R.layout.pop_ts_position);
            final String items [] = {"第一条图文","第一条图文","第一条图文","第一条图文","第一条图文"};
            popPosition.setData(items);
            popPosition.setOnValueChangeListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    tvPosition.setText(items[newVal]);
                }
            });
        }
        popPosition.showPopOnRootView(this);
    }

    //选择日期
    private void showTimePop(){
        if (timePickerView == null) {
            TimePickerView.Builder builder = new TimePickerView.Builder(this,this)
                    .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                    .setDate(new Date())
                    .setOutSideCancelable(true);

            timePickerView = new TimePickerView(builder);
        }
        timePickerView.show();
    }


    //生成订单
    private void generateOrder(){

    }


    @Override
    public void onTimeSelect(Date date, View v) {

    }
}
