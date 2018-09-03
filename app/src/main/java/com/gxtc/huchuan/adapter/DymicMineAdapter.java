package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

import io.rong.imkit.emoticon.AndroidEmoji;

/**
 * Describe:
 * Created by ALing on 2017/5/17 .
 */

public class DymicMineAdapter extends BaseRecyclerAdapter<PersonalDymicBean> {
    public DymicMineAdapter(Context context, List<PersonalDymicBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PersonalDymicBean bean) {

        ImageView ivCover = (ImageView) holder.getView(R.id.iv_head);
        ImageHelper.loadImage(getContext(), ivCover, bean.getUserPic());

        ImageView ivDymic = (ImageView) holder.getView(R.id.iv_dymic);
        TextView tvDymic = (TextView) holder.getView(R.id.tv_dyminc);
        String    picUrl  = bean.getPicUrl();
        if (TextUtils.isEmpty(picUrl)){
            //纯文字动态
            tvDymic.setVisibility(View.VISIBLE);
            ivDymic.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(bean.getContent())){
                SpannableString builder = new SpannableString(AndroidEmoji.ensure(bean.getContent()));
                tvDymic.setText(builder);
            }
        }else {
            //带图片动态
            tvDymic.setVisibility(View.GONE);
            ivDymic.setVisibility(View.VISIBLE);
            ImageHelper.loadImage(getContext(), ivDymic, bean.getPicUrl());
        }

        TextView tvUserName = (TextView) holder.getView(R.id.tv_username);
        tvUserName.setText(bean.getUserName());

        ImageView ivDZ = (ImageView) holder.getView(R.id.iv_zan);
        TextView tvComment = (TextView) holder.getView(R.id.tv_comment);

        String type = bean.getType();
        //  0  评论   1  点赞
        if ("0".equals(type)){
            tvComment.setVisibility(View.VISIBLE);
            SpannableString builder = new SpannableString(AndroidEmoji.ensure(bean.getReplyContent()));
            tvComment.setText(builder);
            ivDZ.setVisibility(View.GONE);
        }else if ("1".equals(type)){
            ivDZ.setVisibility(View.VISIBLE);
            ivDZ.setImageResource(R.drawable.circle_home_icon_zan_select);
            tvComment.setVisibility(View.GONE);
        }/*else {
            tvDelComment.setVisibility(View.VISIBLE);
            tvComment.setVisibility(View.GONE);
            ivDZ.setVisibility(View.GONE);
        }*/

        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        tvTime.setText(DateUtil.stampToDate(bean.getDate()));

        String isDZ = bean.getIsDZ();       //是否 是 点赞
    }
}
