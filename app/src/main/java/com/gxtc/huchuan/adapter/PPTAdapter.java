package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.common.ViewPagerActivity;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Gubr on 2017/3/16.
 */

public class PPTAdapter extends PagerAdapter {

    //		private static final int[] sDrawables = { R.drawable.wallpaper, R.drawable
    // .wallpaper, R.drawable.wallpaper,
    //				R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper };
    List<Uri> datas;


    public PPTAdapter(Context context, List<Uri> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        Glide.with(container.getContext())
             .load(datas.get(position))
             .apply(new RequestOptions().dontAnimate().error(com.gxtc.commlibrary.R.drawable.list_error_img))
             .transition(withCrossFade(ImageHelper.TRANSTION_TIME))
             .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent         intent = new Intent(v.getContext(), ViewPagerActivity.class);
                ArrayList<Uri> uris   = new ArrayList<>(datas);
                intent.putExtra("photo", uris);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public void setUris(List<Uri> uris) {
        datas = uris;
    }

    @Override
    public int getItemPosition(Object object) {
//        if (datas != null && datas.size() == 0) return POSITION_NONE;
       /* else */return POSITION_NONE;
    }

}
