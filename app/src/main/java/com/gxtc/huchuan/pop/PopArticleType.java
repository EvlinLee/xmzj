package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Steven on 17/3/7.
 */

public class PopArticleType extends BasePopupWindow{

    @BindView(R.id.btn_ts_ok)                   TextView            btnOk;
    @BindView(R.id.picker_position)             NumberPickerView    pickerView;

    private int index;

    private NumberPickerView.OnValueChangeListener listener;

    public PopArticleType(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this,layoutView);
    }


    @Override
    public void initListener() {
        pickerView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                index = newVal;
            }
        });
    }

    @OnClick(R.id.btn_ts_ok)
    public void onClick(View v){
        closePop();
        if(listener != null){
            listener.onValueChange(null,0,index);
        }
    }


    public void setData(String [] itmes){
        pickerView.refreshByNewDisplayedValues(itmes);
    }

    public void setOnValueChangeListener(NumberPickerView.OnValueChangeListener listener) {
        this.listener = listener;
    }
}
