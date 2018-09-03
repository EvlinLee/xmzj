package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ShiledListBean;

import java.util.List;

/**
 * Created by 宋家任 on 2017/6/7.
 * 屏蔽列表
 */

public class ShieldListAdapter extends BaseRecyclerAdapter<ShiledListBean> {
    private Context mContext;
    private OnDelItemListener del;
    private boolean isShowRemoveIcon = false;

    public ShieldListAdapter(Context context, List<ShiledListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final ShiledListBean bean) {

        ImageView imageView = (ImageView) holder.getView(R.id.iv_shield_item);
        ImageHelper.loadImage(mContext, imageView, bean.getTargetPic());

        TextView tvName = (TextView) holder.getView(R.id.tv_shield_name);
        if (!"".equals(bean.getTargetName())) {
            tvName.setText(bean.getTargetName());
        }

        ImageView ivDelete = (ImageView) holder.getView(R.id.iv_shield_relieve);
        ivDelete.setTag(bean);
        ivDelete.setVisibility(isShowRemoveIcon ? View.VISIBLE : View.GONE);
        if (isShowRemoveIcon) {
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    del.onDel(holder, position, bean);
                }
            });
        }


    }

    public void setOnDelItemListener(OnDelItemListener itemDel) {
        this.del = itemDel;
    }

    //删除条目监听
    public interface OnDelItemListener {
        void onDel(ViewHolder holder, int position, ShiledListBean bean);
    }


    public void setShowRemoveIcon(boolean showRemoveIcon) {
        isShowRemoveIcon = showRemoveIcon;
    }


    public boolean isShowRemoveIcon() {
        return isShowRemoveIcon;
    }
}
