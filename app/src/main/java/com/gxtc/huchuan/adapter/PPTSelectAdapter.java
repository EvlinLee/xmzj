package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.UploadPPTFileBean;

import java.util.List;

/**
 * Created by sjr on 2017/4/10.
 * 上传ppt列表
 * isCheck字段0是可编辑显示删除按钮和可以排序1是不可编辑不显示删除按钮不可排序
 */

public class PPTSelectAdapter extends BaseRecyclerAdapter<UploadPPTFileBean> {

    private Context mContext;
    private OnDelItemListener del;
    private boolean isShowRemoveIcon=false;



    public PPTSelectAdapter(Context context, List<UploadPPTFileBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final UploadPPTFileBean uploadPPTFileBean) {
        //图片
        ImageView imageView = (ImageView) holder.getView(R.id.iv_item_ppt);
        ImageHelper.loadImage(mContext, imageView, uploadPPTFileBean.getPicUrl());

        //删除按钮
        ImageView ivDelete = (ImageView) holder.getView(R.id.iv_item_ppt_delete);
        ivDelete.setTag(uploadPPTFileBean);

        ivDelete.setVisibility(isShowRemoveIcon?View.VISIBLE:View.GONE);
        if (isShowRemoveIcon){
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    del.onDel(holder, position, uploadPPTFileBean);
                }
            });
        }


//        if ("0".equals(uploadPPTFileBean.getIsClick())) {
//            ivDelete.setVisibility(View.VISIBLE);
//            ivDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    del.onDel(holder, position, uploadPPTFileBean);
//                }
//            });
//        } else if ("1".equals(uploadPPTFileBean.getIsClick())) {
//
//            ivDelete.setVisibility(View.GONE);
//        }
    }

    public void setOnDelItemListener(OnDelItemListener itemDel) {
        this.del = itemDel;
    }

    //删除条目监听
    public interface OnDelItemListener {
        void onDel(ViewHolder holder, int position, UploadPPTFileBean bean);
    }


    public void setShowRemoveIcon(boolean showRemoveIcon) {
        isShowRemoveIcon = showRemoveIcon;
    }


    public boolean isShowRemoveIcon() {
        return isShowRemoveIcon;
    }
}
