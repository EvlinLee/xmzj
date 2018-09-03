package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AddressBean;
import com.gxtc.huchuan.ui.mine.address.UpdataAddressActivity;

import java.util.List;

public class SelectAddressAdapter extends BaseRecyclerAdapter<AddressBean> {


    public SelectAddressAdapter(Context context, List<AddressBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(final ViewHolder holder, int position, AddressBean bean) {
        ImageView imgSelect = (ImageView) holder.getView(R.id.iv_select);
        ImageView imgEdit   = (ImageView) holder.getView(R.id.img_address_edit);
        TextView tvName     = (TextView) holder.getView(R.id.tv_name);
        TextView tvPhone    = (TextView) holder.getView(R.id.tv_phone);
        TextView tvAddr     = (TextView) holder.getView(R.id.tv_address);
        TextView tvDefult   = (TextView) holder.getView(R.id.tv_address_defult);

        String name = bean.getName();
        String phone = bean.getPhone();
        String defult = bean.getIsdefault();
        String address = bean.getProvince()+bean.getCity();
        String area = bean.getArea();
        if (!TextUtils.isEmpty(area)) {
            address += area;
        }
        address += bean.getAddress();

        tvAddr.setText(address);
        tvName.setText(name);
        tvPhone.setText(phone);

        if("1".equals(defult)){
            tvDefult.setVisibility(View.VISIBLE);
        }else{
            tvDefult.setVisibility(View.INVISIBLE);
        }

        if(bean.isSelect()){
            imgSelect.setVisibility(View.VISIBLE);
        }else{
            imgSelect.setVisibility(View.INVISIBLE);
        }

        imgEdit.setTag(bean);
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressBean bean = (AddressBean) v.getTag();
                Intent intent = new Intent(getContext(), UpdataAddressActivity.class);
                intent.putExtra(Constant.INTENT_DATA,bean);
                getContext().startActivity(intent);
            }
        });

    }






}
