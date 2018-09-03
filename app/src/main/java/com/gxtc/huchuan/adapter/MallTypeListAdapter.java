package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MallDetailBean;

import java.util.List;

/**
 * Created by zzg on 17/11/1
 */

public class MallTypeListAdapter extends BaseRecyclerAdapter<MallDetailBean.DetailParamBean>{

    public  int chooseposition = -1;

    public void setChoosePosition(int chooseposition) {
        this.chooseposition = chooseposition;
    }

    public MallTypeListAdapter(Context context, List<MallDetailBean.DetailParamBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, MallDetailBean.DetailParamBean bean) {
        TextView typeName = (TextView) holder.getView(R.id.type_name);
        if(chooseposition == position ){
            typeName.setTextColor(context.getResources().getColor(R.color.white));
            typeName.setBackgroundResource(R.drawable.selected_orange_btn_no_line);
        }else {
            typeName.setTextColor(context.getResources().getColor(R.color.text_color_666));
            typeName.setBackgroundResource(R.drawable.shap_focus_bg);
        }
        typeName.setText(bean.getName());
    }
}
