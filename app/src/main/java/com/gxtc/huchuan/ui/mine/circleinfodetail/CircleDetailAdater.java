package com.gxtc.huchuan.ui.mine.circleinfodetail;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.widget.RoundImageView;

import java.util.List;

import static com.gxtc.huchuan.R.id.textView;

/**
 * Created by Administrator on 2017/8/30.
 */

public class CircleDetailAdater extends BaseMoreTypeRecyclerAdapter<CircleCommentBean> {
    public CircleDetailAdater(Context mContext, List<CircleCommentBean> datas, int... resid) {
        super(mContext, datas, resid);
    }


    @Override
    public int getItemViewType(int position) {
         return 0;
    }
    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder , int position, CircleCommentBean circleHomeBean) {
        RoundImageView picHead = (RoundImageView) holder.getView(R.id.iv_circle_home_img);
        ImageHelper.loadImage(mContext,picHead,circleHomeBean.getUserPic());
        TextView userName = (TextView) holder.getView(R.id.tv_circle_home_name);
        userName.setText(circleHomeBean.getUserName());
        TextView content = (TextView) holder.getView(R.id.content);
        if(TextUtils.isEmpty(circleHomeBean.getTargetUserCode())){
            content.setText(circleHomeBean.getContent());
            content .setTextColor(mContext.getResources().getColor(R.color.text_color_333));
        }else {
            content.setText("回复"+circleHomeBean.getTargetUserName()+":"+circleHomeBean.getContent());
            setPartTextColor(content,circleHomeBean.getTargetUserName());
        }
        TextView time = (TextView) holder.getView(R.id.time);
        time.setText(DateUtil.showTimeAgo(String.valueOf(circleHomeBean.getCreateTime())));
    }
    void setPartTextColor(TextView textView,String target){
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_48baf3));
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        builder.setSpan(blueSpan, 2, target.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
}
