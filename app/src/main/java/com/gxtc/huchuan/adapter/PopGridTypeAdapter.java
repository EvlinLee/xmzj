package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.event.EventClickBean;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/26.
 */

public class PopGridTypeAdapter extends AbsBaseAdapter<DealTypeBean.TypesBean> {

    public PopGridTypeAdapter(Context context, List<DealTypeBean.TypesBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, DealTypeBean.TypesBean bean, int position) {
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        String choice = bean.getChoice();

        if(!"不限".equals(bean.getTitle()) && !TextUtils.isEmpty(choice)){
            tvName.setText(choice + " " + bean.getTitle());

        }else{
            tvName.setText(bean.getTitle());
        }

        tvName.setTag(bean.getCode());
        tvName.setSelected(bean.isSelect());
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = (String) v.getTag();
                EventBusUtil.post(new EventClickBean(code,v));
            }
        });

    }
}
