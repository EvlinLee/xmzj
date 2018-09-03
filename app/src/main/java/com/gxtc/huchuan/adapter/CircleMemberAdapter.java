package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.CircleImageView;

import java.util.List;

/**
 * Created by Steven on 17/4/26.
 * 成员列表
 */

public class CircleMemberAdapter extends BaseRecyclerAdapter<CircleMemberBean> {

    private int flag;

    public CircleMemberAdapter(Context context, List<CircleMemberBean> list, int itemLayoutId, int flag) {
        super(context, list, itemLayoutId);
        this.flag = flag;
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleMemberBean bean) {

        CircleImageView ivCover = (CircleImageView) holder.getView(R.id.img_icon);
        ImageHelper.loadImage(getContext(), ivCover, bean.getUserPic());

        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        tvName.setText(bean.getUserName());

        TextView tvContent = (TextView) holder.getView(R.id.tv_content);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getJoinTime())));

//        if (TextUtils.isEmpty(bean.getContent())) {
//            tvContent.setVisibility(View.GONE);
//        } else {
//            tvContent.setVisibility(View.VISIBLE);
//            tvContent.setText(bean.getContent());
//        }

//        ImageView ivMemberFlag = (ImageView) holder.getView(R.id.iv_member_flag);
        TextView tvType = (TextView) holder.getView(R.id.tv_type);
        //管理圈子成员
        if (flag == 2) {
            showType(bean, tvType);
            if (bean.getMemberType() == 2) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText("一圈之主");
            } else if (TextUtils.isEmpty(bean.getPayType())) {
                tvContent.setVisibility(View.GONE);
            } else {
                tvContent.setVisibility(View.VISIBLE);
                if ("0".equals(bean.getPayType())) {//免费加入
                    tvContent.setText("免费加入");
                } else if ("1".equals(bean.getPayType())) {//付费加入
                    tvContent.setText("付费加入(" + bean.getFee() + "元)");
                } else {//后台添加
                    tvContent.setText("后台添加");
                }
            }

            TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
//       isNotalk  被禁言 1已被禁言，0未被禁言 isAlsdinfo 禁动态 1被禁动态 0没有被禁动态
            if (0==bean.getIsNotalk()&&"0".equals(bean.getIsAlsdinfo())){//既没有禁言也没有禁动态
                tvStatus.setVisibility(View.GONE);
            }else if (1==bean.getIsNotalk()&&"1".equals(bean.getIsAlsdinfo())){
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText("禁言、禁动态中");
            }else if (0==bean.getIsNotalk()&&"1".equals(bean.getIsAlsdinfo())){
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText("禁动态中");
            }else if (1==bean.getIsNotalk()&&"0".equals(bean.getIsAlsdinfo())){
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText("禁言中");
            }

        } else {//普通成员
            tvType.setVisibility(View.GONE);
            tvContent.setText(bean.getContent());
        }

    }

    //    private void showMember(CircleMemberBean bean, ImageView ivMemberFlag) {
//        int memberType = bean.getMemberType();
//        if (memberType == 2){
//            ivMemberFlag.setVisibility(View.VISIBLE);
//            ivMemberFlag.setImageResource(R.drawable.circle_manage_icon_qz);
//        }else if (memberType == 1){
//            ivMemberFlag.setVisibility(View.VISIBLE);
//            ivMemberFlag.setImageResource(R.drawable.circle_manage_icon_gly);
//        }else {
//            ivMemberFlag.setVisibility(View.GONE);
//        }
//    }
    private void showType(CircleMemberBean bean, TextView textView) {
        int memberType = bean.getMemberType();
        if (memberType == 2) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("圈主");
        } else if (memberType == 1) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("管理员");
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
