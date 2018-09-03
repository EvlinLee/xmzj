package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.MineCircleBean;

import java.util.List;

/**
 * 圈子首页第三版头部适配器
 */
public class NewAfterNewCircleHomeHeadAdapter extends BaseRecyclerAdapter<MineCircleBean> {
    private Context mContext;

    public NewAfterNewCircleHomeHeadAdapter(Context context, List<MineCircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        int size = 0; //只显示20条
        if( super.getItemCount() > 20){
            size = 20;
        }else {
            size = super.getItemCount();
        }
        return size;
    }

    @Override
    public void bindData(ViewHolder holder, int position, MineCircleBean bean) {

        ImageView iv = (ImageView) holder.getView(R.id.iv_item_new_after_new_circle_head);
        ImageHelper.loadRound(mContext, iv, bean.getCover(),4);

        TextView tvName = (TextView) holder.getView(R.id.tv_new_after_new_circle_name);
        tvName.setText(bean.getGroupName());

        TextView tvTopic = (TextView) holder.getView(R.id.tv_new_after_new_issue_topic);
        tvTopic.setText("话题：" + bean.getInfoNum());
    }


}
