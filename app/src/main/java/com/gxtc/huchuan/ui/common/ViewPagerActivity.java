/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.gxtc.huchuan.ui.common;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class ViewPagerActivity extends AppCompatActivity {

    List<Uri> datas;
    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        datas=getIntent().getParcelableArrayListExtra("photo");

        if (datas != null) {
            Log.d("ViewPagerActivity", datas.toString());
        }
        mPosition = getIntent().getIntExtra("position", 0);
        ViewPager viewPager = (HackyViewPager) findViewById(R.id.view_pager);

        viewPager.setAdapter(new SamplePagerAdapter(datas));
        viewPager.setCurrentItem(mPosition,false);
    }

    static class SamplePagerAdapter extends PagerAdapter {

        //		private static final int[] sDrawables = { R.drawable.wallpaper, R.drawable
        // .wallpaper, R.drawable.wallpaper,
        //				R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper };
        List<Uri> datas;

        public SamplePagerAdapter(List<Uri> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas != null ? datas.size() : 0;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(container.getContext())
                 .load(datas.get(position))
                 .apply(new RequestOptions().dontAnimate().error(com.gxtc.commlibrary.R.drawable.list_error_img))
                 .transition(withCrossFade(ImageHelper.TRANSTION_TIME))
                 .into(photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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

    }
}
