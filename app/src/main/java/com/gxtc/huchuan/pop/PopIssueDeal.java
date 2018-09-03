package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AllTypeBaen;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class PopIssueDeal extends BasePopupWindow {

    @BindView(R.id.picker_type)
    NumberPickerView typePicker;
    @BindView(R.id.picker_subType)
    NumberPickerView subTypePicker;
    @BindView(R.id.btn_ts_ok)
    TextView btnOk;

    private int typeIndex = 0;
    private int subTypeIndex = 0;

    private List<AllTypeBaen> typeBean;

    private OnPopClickListener listener;

    public PopIssueDeal(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);

    }

    @Override
    public void initListener() {
        typePicker.setWrapSelectorWheel(false);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPopClick(typeIndex, subTypeIndex);
                }
                closePop();
            }
        });

        typePicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                changeSubType(newVal);
            }
        });

        subTypePicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                subTypeIndex = newVal;
            }
        });
    }

    private void changeSubType(int newVal) {
        String subType[] = new String[typeBean.get(newVal).getSon().size()];
        for (int i = 0; i < typeBean.get(newVal).getSon().size(); i++) {
            subType[i] = typeBean.get(newVal).getSon().get(i).getTitle();
        }
        if (subType.length == 0) {
            subType = new String[1];
            subType[0] = "暂无分类";
        }
        typeIndex = newVal;
        subTypeIndex = 0;
        subTypePicker.refreshByNewDisplayedValues(subType);
    }

    public void setDatas(List<AllTypeBaen> typeBean) {
        if (typeBean.size() == 0) return;
        this.typeBean = typeBean;

        String type[] = new String[typeBean.size()];
        for (int i = 0; i < typeBean.size(); i++) {
            type[i] = typeBean.get(i).getTitle();
        }
        String subType[] = new String[typeBean.get(0).getSon().size()];
        for (int i = 0; i < typeBean.get(0).getSon().size(); i++) {
            subType[i] = typeBean.get(0).getSon().get(i).getTitle();
        }
        if (subType.length == 0) {
            subType = new String[]{"暂无分类"};
        }

        typePicker.refreshByNewDisplayedValues(type);
        subTypePicker.refreshByNewDisplayedValues(subType);

    }

    public void setTypeValue(int typeIndex) {
        this.typeIndex = typeIndex;
        typePicker.setValue(typeIndex);
        changeSubType(typeIndex);
    }

    public void setSubTypeValue(int typeIndex) {
        this.subTypeIndex = typeIndex;
        subTypePicker.setValue(typeIndex);
    }


    public interface OnPopClickListener {
        void onPopClick(int typeIndex, int subTypeIndex);
    }

    public void setOnPopClickListener(OnPopClickListener listener) {
        this.listener = listener;
    }
}
