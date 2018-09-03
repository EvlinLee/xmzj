package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.event.EventClickBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class IssueListAdapter extends BaseRecyclerAdapter<PurchaseListBean> {

    private SimpleDateFormat sdf ;

    public IssueListAdapter(Context context, List<PurchaseListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        sdf = new SimpleDateFormat("MM-dd",Locale.CHINA);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseListBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        ImageView ivStatus = (ImageView) holder.getView(R.id.iv_status);
        TextView tvRead = (TextView) holder.getView(R.id.tv_read);

        String name = bean.getTradeInfoTitle();
        tvTitle.setText(name);

        String time = DateUtil.showTimeAgo(bean.getCreateTime() + "");
        tvTime.setText(time);
        switch (bean.getIsFinish() ){
            case 0:
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.icon_status_recommend);
                break;
            case 1:
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.news_manage_icon_not_pass);
                break;
            default:
                ivStatus.setVisibility(View.GONE);
                break;
        }
        int read = bean.getRead();
        int comment = bean.getCommentNum();

        tvRead.setText("浏览" + read +" / " + "留言" + comment);
    }
}
