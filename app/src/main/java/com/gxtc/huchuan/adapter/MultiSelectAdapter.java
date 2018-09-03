package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AllTypeBaen;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/1.
 */

public class MultiSelectAdapter extends AbsBaseAdapter<AllTypeBaen.UdefBean.Entity> {

    public MultiSelectAdapter(Context context, List<AllTypeBaen.UdefBean.Entity> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, AllTypeBaen.UdefBean.Entity bean, int position) {
        TextView    tvName = (TextView) holder.getView(R.id.tv_name);
        RadioButton rb     = (RadioButton) holder.getView(R.id.radio_btn);

        tvName.setText(bean.getTitle());
        rb.setChecked(bean.isSelect());
        rb.setTag(bean);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllTypeBaen.UdefBean.Entity entity = (AllTypeBaen.UdefBean.Entity) v.getTag();
                int type = entity.getType();

                //如果是单选
                if(type == 5){
                    for (AllTypeBaen.UdefBean.Entity entity1 : getDatas()){
                        if(entity1.getCode().equals(entity)) continue;
                        entity1.setSelect(false);
                    }
                }
                if(entity.isSelect()){
                    entity.setSelect(false);
                }else {
                    entity.setSelect(true);
                }
                notifyDataSetChanged();
            }
        });
    }


}
