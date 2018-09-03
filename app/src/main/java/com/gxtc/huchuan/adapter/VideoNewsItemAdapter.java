package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.utils.StatusBarUtils;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.VideoNewsBean;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.pop.PopShield;
import com.gxtc.huchuan.ui.deal.deal.hotdeallist.HotDealListActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * 来自 苏修伟 on 2018/5/8.
 */
public class VideoNewsItemAdapter extends BaseRecyclerAdapter<VideoNewsBean> implements View.OnClickListener {

    public VideoNewsItemAdapter(Context context, List<VideoNewsBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, VideoNewsBean videoNewsBean) {
        TextView title =  holder.getViewV2(R.id.tv_video_title);
        ImageView imageView = holder.getViewV2(R.id.iv_video_property);
        TextView  author = holder.getViewV2(R.id.tv_video_author);
        TextView createTime = holder.getViewV2(R.id.tv_video_time);
        ImageView shield  = holder.getViewV2(R.id.iv_shield);
        ImageView videoCover  = holder.getViewV2(R.id.iv_video_cover);
        title.setText(videoNewsBean.getTitle());
        ImageHelper.loadHeadIcon(getContext(), videoCover, videoNewsBean.getHeadPic());
        if(videoNewsBean.getSetTop() == 1){
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setVisibility(View.VISIBLE);
        }
        author.setText(videoNewsBean.getAuthor());

        createTime.setText(DateUtil.getDatePoor(System.currentTimeMillis(), videoNewsBean.getCreateTime()));
        shield.setOnClickListener(this);

    }

    private void shied(View v , NewsBean bean){
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int  mode = Gravity.BOTTOM;
        int height =WindowUtil.getScreenH(getContext());
        if(location[1] >= WindowUtil.getScreenH(getContext()) - v.getHeight() - WindowUtil.dip2px(getContext(),122)){
            mode = Gravity.TOP;
        }




        PopShield shieldDialog = new PopShield(getContext(), bean);
        shieldDialog.anchorView(v)
                .showAnim(new PopEnterAnim().duration(200))
                .dismissAnim(new PopExitAnim().duration(200))
                .cornerRadius(4)
                .setTextNotice("屏蔽此视频","屏蔽此人的所有视频")
                .gravity(mode)
                .bubbleColor(Color.parseColor("#ffffff"))
                .setBgAlpha(0.1f)
                .setOnClickListener(this);
        shieldDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.iv_shield:
                shied(v, null);
                break;

                //屏蔽单个
            case R.id.tv_shield_dynamic:
                break;

                //屏蔽所有
            case R.id.tv_shield_user:
                break;
        }
    }
}
