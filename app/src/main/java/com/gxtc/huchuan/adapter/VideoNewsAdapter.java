package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.utils.StatusBarUtils;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.pop.PopEnterAnim;
import com.gxtc.huchuan.pop.PopExitAnim;
import com.gxtc.huchuan.pop.PopShield;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by sjr on 2017/2/17.
 * 视频列表播放器
 */

public class VideoNewsAdapter extends BaseRecyclerAdapter<NewsBean>  implements View.OnClickListener{
    private Context mContext;
    private OnShieldListener mShieldListener;


    public VideoNewsAdapter(Context context, List<NewsBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }

    @Override
    public void bindData(ViewHolder holder, int position, final NewsBean bean) {
        if(bean != null && bean.getVideoUrl() != null && bean.getDate() != null) {
            //视频标题
            TextView tvVideoTitle = (TextView) holder.getView(R.id.tv_news_video_title);
            tvVideoTitle.setText(bean.getTitle());

            JZVideoPlayerStandard player = (JZVideoPlayerStandard) holder.getView(R.id.play_news_video_cover);
            if(!TextUtils.isEmpty(bean.getVideoUrl()) && !TextUtils.isEmpty(bean.getCover()))
               player.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_LIST, "", bean.getCover());
            ImageHelper.loadImage(mContext, player.thumbImageView, bean.getCover());

            holder.getView(R.id.iv_shield).setOnClickListener(this);
            holder.getView(R.id.iv_shield).setTag(bean);
            //作者
            TextView tvAuthor = (TextView) holder.getView(R.id.tv_news_video_author);
            tvAuthor.setText(bean.getSource());
            //时间
            TextView tvTime = (TextView) holder.getView(R.id.tv_news_video_time);
            tvTime.setText(DateUtil.showTimeAgo(bean.getDate()));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击屏蔽按钮
            case R.id.iv_shield:
                NewsBean bean = (NewsBean) v.getTag();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int x = 0;
                int y = 0;
                    x = location[0] + v.getWidth() / 2 + WindowUtil.dip2px(getContext(), 2);
                    y = location[1] - StatusBarUtils.getHeight(getContext())
                            + v.getHeight() + WindowUtil.dip2px(getContext(), 15);
                PopShield shieldDialog = new PopShield(getContext(),bean);
                shieldDialog.anchorView(v)
                            .showAnim(new PopEnterAnim().duration(200))
                            .dismissAnim(new PopExitAnim().duration(200))
                            .cornerRadius(4)
                            .gravity(Gravity.BOTTOM)
                            .bubbleColor(Color.parseColor("#ffffff"))
                            .setBgAlpha(0.1f)
                            .location(x, y).setTextNotice("屏蔽此视频","屏蔽此人文章和视频")
                            .setOnClickListener(this);
                shieldDialog.show();
                break;

            //屏蔽此视频:
            case R.id.tv_shield_dynamic:
                if(mShieldListener != null){
                    NewsBean temp = (NewsBean) v.getTag();
                    mShieldListener.onShieldVideo(temp.getId());
                }
                break;


            //屏蔽此人文章和视频:
            case R.id.tv_shield_user:
                if(mShieldListener != null){
                    NewsBean temp = (NewsBean) v.getTag();
                    mShieldListener.onShieldUser(temp.getUserCode());
                }
                break;
        }

    }

    public interface OnShieldListener{
        void onShieldVideo(String id);
        void onShieldUser(String userCode);
    }

    public void setOnShieldListener(OnShieldListener shieldListener) {
        mShieldListener = shieldListener;
    }

}
