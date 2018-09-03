package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.widget.DividerItemDecoration;
import com.gxtc.huchuan.widget.GridDivider;
import com.gxtc.huchuan.widget.RecyclerSpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzg on 17/10/27
 */

public class BaseMallAdapter extends BaseRecyclerAdapter<MallBean> {

    View.OnClickListener mAddshopCartListenner;

    public void setAddshopCartListenner(View.OnClickListener mAddshopCartListenner) {
        this.mAddshopCartListenner = mAddshopCartListenner;
    }

    public BaseMallAdapter(Context context, List<MallBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }


    @Override
    public void bindData(ViewHolder holder, final int position, MallBean bean) {
        if(bean == null)   return;
        ImageView                 iv          = (ImageView) holder.getView(R.id.image);
        TextView                  content     = (TextView) holder.getView(R.id.content);
        ImageView                 addShopCart = (ImageView) holder.getView(R.id.add_shop_cart);
        TextView                  price       = (TextView) holder.getView(R.id.price);
        LinearLayout.LayoutParams params      = (LinearLayout.LayoutParams) iv.getLayoutParams();
        try{
            if("1".equals(bean.getRatio())){
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                params.height = (WindowUtil.getScreenW(context) * bean.getPicUrlY()) / bean.getPicUrlX();
                params.width = WindowUtil.getScreenW(context);
                holder.getView(R.id.layout_content).setVisibility(View.GONE);
            }else {
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                params.height = (WindowUtil.getScreenW(context)/2);
                params.width = (WindowUtil.getScreenW(context)/2);
                holder.getView(R.id.layout_content).setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        addShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(position);
                if (mAddshopCartListenner != null) mAddshopCartListenner.onClick(v);
            }
        });
        content.setText(bean.getName());
        price.setText("￥ " + bean.getPrice());
        ImageHelper.loadImage(holder.getItemView().getContext(), iv, bean.getPicUrl(), R.drawable.news_home_place_holder_video);
    }
}
