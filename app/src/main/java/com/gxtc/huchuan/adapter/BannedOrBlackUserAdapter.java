package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.List;

/**
 * Created by sjr on 2017/3/21.
 * 禁言用户适配器
 */

public class BannedOrBlackUserAdapter extends BaseRecyclerAdapter<BannedOrBlackUserBean> {
    private Context mContext;
    private OnDelItemListener del;
    private Activity mActivity;
    private int mFlag;

    public BannedOrBlackUserAdapter(Activity activity, Context context,
                                    List<BannedOrBlackUserBean> list, int itemLayoutId, int flag) {
        super(context, list, itemLayoutId);
        this.mActivity = activity;
        this.mContext = context;
        this.mFlag = flag;
    }

    AlertDialog alertDialog;

    @Override
    public void bindData(final ViewHolder holder, final int position, BannedOrBlackUserBean bannedOrBlackUserBean) {
        //头像
        ImageView imageView = (ImageView) holder.getView(R.id.riv_banned_user);
        ImageHelper.loadImage(mContext, imageView, bannedOrBlackUserBean.getHeadPic());

        //姓名
        TextView tvName = (TextView) holder.getView(R.id.tv_banned_user_name);
        tvName.setText(bannedOrBlackUserBean.getName());

        //取消
        TextView tvDel = (TextView) holder.getView(R.id.tv_cancel_banned);
        tvDel.setTag(bannedOrBlackUserBean);

        //黑名单
        if (1 == mFlag) {
            tvDel.setText("取消黑名单");
            tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BannedOrBlackUserBean bean = (BannedOrBlackUserBean) v.getTag();

                    alertDialog = DialogUtil.showInputDialog(mActivity, false, "", "确定将" + bean.getName() + "取消黑名单?"
                            , new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    del.onDel(holder, position, bean);
                                    alertDialog.dismiss();
                                }
                            });

                }
            });

        //禁言
        } else if (2 == mFlag) {
            tvDel.setText("取消禁言");
            tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BannedOrBlackUserBean bean = (BannedOrBlackUserBean) v.getTag();

                    alertDialog = DialogUtil.showInputDialog(mActivity, false, "", "确定将" + bean.getName() + "取消禁言?"
                            , new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    del.onDel(holder, position, bean);
                                    alertDialog.dismiss();
                                }
                            });

                }
            });


        }

    }

    public void setOnDelItemListener(OnDelItemListener itemDel) {
        this.del = itemDel;
    }

    //删除条目监听
    public interface OnDelItemListener {
        void onDel(ViewHolder holder, int position, BannedOrBlackUserBean bean);
    }
}
