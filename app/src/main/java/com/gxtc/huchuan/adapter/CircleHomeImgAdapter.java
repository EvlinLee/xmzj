package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * Created by 宋家任 on 2017/5/15.
 * 圈子动态里面的图片适配器
 */

public class CircleHomeImgAdapter extends BaseMoreTypeRecyclerAdapter<CircleHomeBean.PicListBean> {

    private List<CircleHomeBean.PicListBean> mDatas;
    private Context                          mContext;
    private int width;

    public CircleHomeImgAdapter(Context mContext, List<CircleHomeBean.PicListBean> datas, int... resid) {
        super(mContext, datas, resid);
        this.mDatas = datas;
        this.mContext = mContext;

        int paddingLeft = (int) mContext.getResources().getDimension(R.dimen.px130dp);
        int paddingRight = (int) mContext.getResources().getDimension(R.dimen.px100dp);
        int divide = WindowUtil.dip2px(mContext, 5) * 2;
        width = (WindowUtil.getScreenWidth(mContext) - paddingLeft - paddingRight - divide) / 3;
    }

    @Override
    public void bindData(BaseMoreTypeRecyclerAdapter.ViewHolder holder, int position, CircleHomeBean.PicListBean picListBean) {
        if (mDatas.size() == 1) {
            ImageView imageView = (ImageView) holder.getView(R.id.iv_item_circle_home2);

            String url = picListBean.getPicUrl();
            if (!TextUtils.isEmpty(url) && url.contains("*")) {

                int lastIndex = url.lastIndexOf("?");
                if(lastIndex != -1){
                    String wh = url.substring(lastIndex + 1,url.length());
                    String whArray [] = wh.split("\\*");

                    if(whArray.length >= 2){
                        double width = StringUtil.toDouble(whArray[0]);
                        double height = StringUtil.toDouble(whArray[1]);

                        if(width == 0 && height == 0){
                            ImageHelper.loadIntoUseFitWidthOrHeight(mContext, picListBean.getPicUrl(), 0, imageView);
                            return;
                        }

                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int maxWidth = imageView.getMaxWidth();
                        int maxHeight = imageView.getMaxHeight();

                        if(width > height){
                            int vw = maxWidth - imageView.getPaddingLeft() - imageView.getPaddingRight();
                            float scale = (float) vw / (float) width;
                            if (width > maxWidth) {
                                int   vh    = (int) Math.round(height * scale);
                                params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                                params.width = maxWidth;

                            }else{
                                params.width = (int) (scale * width);
                                params.height = (int) (scale * height);
                            }

                        }else{
                            int   vh    = maxHeight - imageView.getPaddingBottom() - imageView.getPaddingTop();
                            float scale = (float) vh / (float) height;
                            if (height > maxHeight) {
                                int   vw    = (int) Math.round(width * scale);
                                params.height = maxHeight;
                                params.width = vw + imageView.getPaddingLeft() + imageView.getPaddingRight();

                            }else{
                                params.width = (int) (scale * width);
                                params.height = (int) (scale * height);
                            }
                        }
                    }
                    ImageHelper.loadImage(mContext,imageView,picListBean.getPicUrl());
                    return ;
                }

            }
            ImageHelper.loadIntoUseFitWidthOrHeight(mContext, picListBean.getPicUrl(), 0, imageView);

        } else {
            ImageView                   imageView = (ImageView) holder.getView(R.id.iv_item_circle_home);
            RelativeLayout.LayoutParams params    = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.width = width;
            params.height = width;
            ImageHelper.loadImage(mContext, imageView, picListBean.getPicUrl());
        }
    }


    public int getItemViewType(int position) {
        if (1 == mDatas.size()) {//单图
            return 0;
        } else {//多图
            return 1;
        }
    }

}
