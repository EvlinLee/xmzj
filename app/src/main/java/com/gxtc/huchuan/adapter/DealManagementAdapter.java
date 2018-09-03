package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventDealManagement;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/15 .
 */

public class DealManagementAdapter extends BaseRecyclerAdapter<PurchaseListBean>{
    public DealManagementAdapter(Context context, List<PurchaseListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseListBean purchaseListBean) {
        //是否选中
        final CheckBox cb = (CheckBox) holder.getView(R.id.cb_deal);
        cb.setTag(purchaseListBean);
        cb.setChecked(purchaseListBean.isCheck());

        if (purchaseListBean.isShow() == true) {
            cb.setVisibility(View.VISIBLE);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("tag", "onCheckedChanged: 00"+isChecked );
                    PurchaseListBean bean = (PurchaseListBean) buttonView.getTag();
                    if (isChecked){
                        bean.setCheck(true);
//                        ToastUtil.showShort(getContext(),bean.getId());
//                        getChatInfoId(bean);
                        EventBusUtil.post(new EventDealManagement(bean.getId(),bean.isCheck()));
                    }else {
                        bean.setCheck(false);
//                        ToastUtil.showShort(getContext(),bean.getId());
                        EventBusUtil.post(new EventDealManagement(bean.getId(),bean.isCheck()));
                    }
                }
            });

        } else {
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
        }

        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        tvTime.setText(DateUtil.stampToDate(String.valueOf(purchaseListBean.getCreateTime())));

        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        tvTitle.setText(purchaseListBean.getTradeInfoTitle());
    }
}
