package com.gxtc.huchuan.ui.mine.collect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.ImageUtils;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/6/5.
 */

class CircleVideoCollectItemView extends CircleCollectItemView<CollectionBean>  {
    public Activity mActivity;
    public CircleVideoCollectItemView(Activity collectActivity,RecyclerView recyclerView) {
        super(collectActivity,recyclerView);
        mActivity = collectActivity;

    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_collect_circle_video;
    }

    @Override
    public boolean isForViewType(CollectionBean item, int position) {
        if ("4".equals(item.getType())){
            CircleHomeBean itemdata =  item.getData();
            if (1 == itemdata.getType()) {
                return true;
            }
        }
        return false;
    }



    @Override
    protected void convertContent(final ViewHolder holder, CircleHomeBean bean, int position) {
        final JZVideoPlayerStandard player = (JZVideoPlayerStandard) holder.getView(
                R.id.play_circle_video_cover);
        TextView title =  holder.getView(R.id.tv_circle_home_three_content);
        title.setText(bean.getContent());
        TextView tvTime =  holder.getView(R.id.tv_news_collect_time);
        tvTime.setText(DateUtil.showTimeAgo(String.valueOf(bean.getCreateTime())));
        if (player != null) {
            player.setVisibility(View.INVISIBLE);
            player.setUp(bean.getVideoUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "", bean.getVideoPic());
            player.widthRatio = 0;
            player.heightRatio = 0;
            player.currentTimeTextView.setVisibility(View.INVISIBLE);
            player.totalTimeTextView.setVisibility(View.INVISIBLE);
            player.progressBar.setVisibility(View.INVISIBLE);
            player.thumbImageView.setBackgroundColor(mActivity.getResources().getColor(R.color.grey_efef));
        }

        if(TextUtils.isEmpty(bean.getVideoPic())){
            Observable.just(bean.getVideoUrl())
                      .subscribeOn(Schedulers.io())
                      .map(new Func1<String, Bitmap>() {
                          @Override
                          public Bitmap call(String s) {
                              return ImageUtils.createVideoThumbnail(s, WindowUtil.getScreenW(mActivity), 360);

                          }
                      })
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Action1<Bitmap>() {
                          @Override
                          public void call(Bitmap bitmap) {
                              if(bitmap == null) return;
                              setVideoSize(bitmap.getWidth(),bitmap.getHeight(),null,player);
                              player.thumbImageView.setImageBitmap(bitmap);
                          }
                      });
        }else{
            setVideoSize(0,0,bean.getVideoPic(),player);
            ImageHelper.loadImage(mActivity,player.thumbImageView,bean.getVideoPic());
        }
    }

    //设置视频的尺寸
    private void setVideoSize(int width, int height, String videoPic, JZVideoPlayerStandard videoView){
        float [] size = null;
        if(TextUtils.isEmpty(videoPic)){
            size = getVideoSize(width,height);
        }else{
            try {
                String sub = videoPic.split("\\?")[1];
                String [] var = sub.split("\\*");
                float tempWidth = Float.valueOf(var[0]);
                float tempheight = Float.valueOf(var[1]);
                size = getVideoSize(tempWidth,tempheight);

            }catch (Exception e){
                e.printStackTrace();
                LogUtil.i(e.getMessage());
            }
        }

        if(size != null && size.length >= 2 && videoView != null){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoView.getLayoutParams();
            params.width = (int) size[0];
            params.height = (int) size[1];
            videoView.setVisibility(View.VISIBLE);
        }
    }

    private float[] getVideoSize(float width, float height){
        float marginLeft = mActivity.getResources().getDimension(R.dimen.px130dp);
        float marginRight = mActivity.getResources().getDimension(R.dimen.margin_larger);
        float [] size = new float[2];
        if(width < height){
            float videoWidth = mActivity.getResources().getDimension(R.dimen.px240dp);
            //如果图片比设定最小宽度还小 就给最小宽度的值
            if(width < videoWidth){
                width = videoWidth;
            }

            float videoHeight = videoWidth * height / width;
            size[0] = videoWidth;
            size[1] = videoHeight;

        } else if(width == height) {
            float videoWidth = mActivity.getResources().getDimension(R.dimen.px240dp);
            size[0] = videoWidth;
            size[1] = videoWidth;

        } else {
            float videoWidth = (float) ((WindowUtil.getScreenW(mActivity) - marginLeft - marginRight) * 0.7);
            float videoHeight = videoWidth * height / width;
            if(height < videoHeight){
                height = videoHeight;
            }
            size[0] = videoWidth;
            size[1] = videoHeight;
        }

        return size;
    }
}
