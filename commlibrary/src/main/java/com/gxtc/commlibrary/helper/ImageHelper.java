package com.gxtc.commlibrary.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gxtc.commlibrary.R;
import com.gxtc.commlibrary.other.GlideCircularTransform;
import com.gxtc.commlibrary.other.GlideRoundTransform;
import com.gxtc.commlibrary.utils.WindowUtil;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 图片加载框架
 */
public class ImageHelper {

    public static int TRANSTION_TIME = 300;

    /**
     * 加载列表型图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     */
    public static void loadImage(Context context, ImageView imageView, String url) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(R.drawable.live_foreshow_img_temp);
        Glide.with(context).load(url).transition(withCrossFade(TRANSTION_TIME)).apply(options).into(imageView);
    }

    /**
     * 加载列表型图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     * @param res       加载错误或者占位图
     */
    public static void loadImage(Context context, ImageView imageView, String url, int res) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(res);
        Glide.with(context).load(url).transition(withCrossFade(TRANSTION_TIME)).apply(options).into(imageView);
    }


    /**
     * 加载列表型图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     * @param res       加载错误或者占位图
     */
    public static void loadImage(Context context, ImageView imageView, String url, int placeholder, int res) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(res).placeholder(placeholder);
        Glide.with(context).load(url).apply(options).into(imageView);
    }


    /**
     * 加载头像图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     */
    public static void loadHeadIcon(Context context, ImageView imageView, String url) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar);
        Glide.with(context).load(url).apply(options).into(imageView);
    }

    /**
     * 加载头像图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     * @param res       加载错误或者占位图
     */
    public static void loadHeadIcon(Context context, ImageView imageView, int res, String url) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(res);
        Glide.with(context).load(url).apply(options).into(imageView);
    }


    /**
     * 加载圆形图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     */
    public static void loadCircle(Context context, ImageView imageView, String url) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(R.color.grey_e5e5).transforms(new GlideCircularTransform(context));
        Glide.with(context).load(url).transition(withCrossFade(TRANSTION_TIME)).apply(options).into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param url       地址
     * @param errRes    错误图片
     */
    public static void loadCircle(Context context, ImageView imageView, String url, int errRes) {
        if (context == null) return;
        RequestOptions options = new RequestOptions().error(errRes).transforms(new GlideCircularTransform(context));
        Glide.with(context).load(url).apply(options).transition(withCrossFade(TRANSTION_TIME)).into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param img
     * @param url
     * @param radius  圆角大小
     */
    public static void loadRound(Context context, ImageView img, String url, int radius) {
        if (context == null) return;
        RequestOptions options = new RequestOptions()
                .error(R.drawable.list_error_img)
                .transforms(new CenterCrop(), new GlideRoundTransform(context, radius));
        Glide.with(context)
                .load(url)
                //.transition(withCrossFade(TRANSTION_TIME))
                .apply(options)
                .into(img);
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param img
     * @param res
     * @param radius  圆角大小
     */
    public static void loadRound(Context context, ImageView img, int res, int radius) {
        if (context == null) return;
        RequestOptions options = new RequestOptions()
                .error(R.drawable.list_error_img)
                .transforms(new CenterCrop(), new GlideRoundTransform(context, radius));
        Glide.with(context)
                .load(res)
                .transition(withCrossFade(TRANSTION_TIME))
                .apply(options)
                .into(img);
    }


    /**
     * 让ImageView  的maxWidth maxHeight 有效
     *
     * @param context
     * @param imageUrl
     * @param errorImageId
     * @param imageView
     */
    public static void loadIntoUseFitWidthOrHeight(Context context, final String imageUrl, int errorImageId,
                                                   final ImageView imageView) {
        if (context == null) return;
        RequestOptions options = new RequestOptions()
                .override(WindowUtil.dip2px(context, 100), WindowUtil.dip2px(context, 100))
                .error(errorImageId)
                .placeholder(errorImageId)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(new DrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        if (imageView == null) {
                            return;
                        }

                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }

                        int maxWidth = imageView.getMaxWidth();
                        int maxHeight = imageView.getMaxHeight();

                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        if (resource.getIntrinsicWidth() > resource.getIntrinsicHeight()) {

                            int vw = maxWidth - imageView.getPaddingLeft() - imageView.getPaddingRight();
                            float scale = (float) vw / (float) resource.getIntrinsicWidth();
                            if (resource.getIntrinsicWidth() > maxWidth) {
                                int vh = Math.round(resource.getIntrinsicHeight() * scale);
                                params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                                params.width = maxWidth;
                                imageView.setLayoutParams(params);

                            } else {
                                params.width = (int) (resource.getIntrinsicWidth() * scale);
                                params.height = (int) (resource.getIntrinsicHeight() * scale);
                            }

                        } else {
                            int vh = maxHeight - imageView.getPaddingBottom() - imageView.getPaddingTop();
                            float scale = (float) vh / (float) resource.getIntrinsicHeight();
                            if (resource.getIntrinsicHeight() > maxHeight) {
                                int vw = Math.round(resource.getIntrinsicWidth() * scale);
                                params.height = maxHeight;
                                params.width = vw + imageView.getPaddingLeft() + imageView.getPaddingRight();
                                imageView.setLayoutParams(params);
                            } else {
                                params.width = (int) (resource.getIntrinsicWidth() * scale);
                                params.height = (int) (resource.getIntrinsicHeight() * scale);
                            }
                        }
                    }
                });
    }

    //这里在Activity onDestroy()里调用，传入当前Application 不然会报错
    public static void onDestroy(Context context) {
        if (context == null) return;
        Glide.get(context).clearMemory();
    }


}
