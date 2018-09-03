package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ApplyForBean;

import java.util.List;

public class ApplyForMemberListAdapter extends BaseRecyclerAdapter<ApplyForBean> {

    private Context mContext;
    private OnAuditListener listener;


    public ApplyForMemberListAdapter(Context context, List<ApplyForBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;

    }

    @Override
    public void bindData(ViewHolder holder, final int position, final ApplyForBean bean) {

        //头像
        ImageView imageView = (ImageView) holder.getView(R.id.iv_apply_for_img);
        ImageHelper.loadImage(mContext, imageView, bean.getUserPic());

        //姓名
        TextView tvName = (TextView) holder.getView(R.id.tv_apply_for_name);
        tvName.setText(bean.getUserName());


        TextView tvContent = (TextView) holder.getView(R.id.tv_apply_for_content);
        if (!"".equals(bean.getContent()))
            tvContent.setText(bean.getContent());
        else
            tvContent.setText("该用户没有申请留言");


        final TextView tvStatus = (TextView) holder.getView(R.id.tv_apply_statu);
        tvStatus.setTag(bean);

        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyForBean bean1 = (ApplyForBean) tvStatus.getTag();
                listener.onAudit(bean1, position);
            }
        });


    }


    public void setOnAuditListener(OnAuditListener onAuditListener) {
        this.listener = onAuditListener;
    }


    //    审核监听
    public interface OnAuditListener {
        void onAudit(ApplyForBean bean, int position);
    }
}
