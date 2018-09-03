package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.AutoLinkTextView;

import java.util.List;

import io.rong.imkit.emoticon.AndroidEmoji;

public class CircleDynamicDetialAdapter extends BaseRecyclerAdapter<CircleCommentBean>{
    private Activity mActivity;

    public CircleDynamicDetialAdapter( Activity activity, List<CircleCommentBean> datas, int  resid) {
        super(activity, datas, resid);
        this.mActivity = activity;
    }


    @Override
    public void bindData(final ViewHolder holder, final int position, final CircleCommentBean bean) {
        holder.setText(R.id.tv_circle_home_name,bean.getUserName());
        final AutoLinkTextView     tvContent = (AutoLinkTextView) holder.getView(R.id.tv_circle_home_three_content);
        if(TextUtils.isEmpty(bean.getTargetUserName())){
            tvContent.setTextColor(mActivity.getResources().getColor(R.color.text_color_666));
            SpannableString builder   = new SpannableString(AndroidEmoji.ensure(bean.getContent()));
            tvContent.setText(builder);
        }else {
            String hf = "回复";
            String target = bean.getTargetUserName() + ":";
            TextAppearanceSpan colorSpan = new TextAppearanceSpan(getContext(), R.style.dynamic_detailed_text_color);
            String content = hf + target + bean.getContent();
            SpannableString builder   = new SpannableString(AndroidEmoji.ensure(content));
            int start = hf.length();
            int end = hf.length() + target.length();
            builder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvContent.setText(builder);
        }

        ImageView img = holder.getImageView(R.id.iv_circle_home_img);
        holder.setText(R.id.tv_time,DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));
        ImageHelper.loadImage(context, img, bean.getUserPic());
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalInfoActivity.startActivity(getContext(), bean.getUserCode());
            }
        });
    }
}
