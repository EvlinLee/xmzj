package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by sjr on 2017/2/17.
 * 动态管理
 */

public class CircleDynamicManagerAdapter extends BaseRecyclerAdapter<CircleHomeBean> {
    private Context mContext;


    public CircleDynamicManagerAdapter(Context context, List<CircleHomeBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;

    }

    @Override
    public void bindData(final ViewHolder holder, final int position, final CircleHomeBean bean) {
        //作者
        TextView tvAuctor = (TextView) holder.getView(R.id.tv_circle_topic_title);
        tvAuctor.setText(bean.getUserName());

        if (0 == bean.getType()) {//无图无视频
            /*Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle_compile_icon_font);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvAuctor.setCompoundDrawables(null, null, drawable, null);*/

        } else if (1 == bean.getType()) {//视屏
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle_compile_icon_video);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvAuctor.setCompoundDrawables(null, null, drawable, null);
        } else {//图片
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.circle_compile_icon_picture);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvAuctor.setCompoundDrawables(null, null, drawable, null);
        }

        TextView tvContent = (TextView) holder.getView(R.id.tv_circle_topic_content);
        tvContent.setText(bean.getContent());

//
//        //时间
        TextView tvTime = (TextView) holder.getView(R.id.tv_circle_topic_time);
        tvTime.setText(DateUtil.stampToDate(String.valueOf(bean.getCreateTime())));
//
//        //封面
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_circle_topic_cover);
        ImageHelper.loadImage(mContext, ivCover, bean.getUserPic());


        //是否是精华
        ImageView ivEssence = (ImageView) holder.getView(R.id.iv_topic_manager_essence);
        if (0 == bean.getIsGood()) {//0不是精华
            ivEssence.setVisibility(View.GONE);
        } else if (1 == bean.getIsGood()) {//1是精华
            ivEssence.setVisibility(View.VISIBLE);
        }

//
//        //是否选中
        CheckBox cb = (CheckBox) holder.getView(R.id.cb_circle_topic);
        cb.setTag(bean);
        cb.setChecked(bean.isCheck());
        if (bean.isShow() == true) {
            cb.setVisibility(View.VISIBLE);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isCheck()) {
                        CircleHomeBean bean = (CircleHomeBean) v.getTag();
                        EventBusUtil.post(new EventCollectSelectBean(false, position, holder));
                        bean.setCheck(false);
                    } else {
                        CircleHomeBean bean = (CircleHomeBean) v.getTag();
                        EventBusUtil.post(new EventCollectSelectBean(true, position, holder));
                        bean.setCheck(true);
                    }
                }
            });
        } else {
            cb.setVisibility(View.GONE);
            cb.setChecked(false);
        }

    }

}
