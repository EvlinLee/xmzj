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
package com.gxtc.huchuan.ui.circle.dynamic;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.common.HackyViewPager;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * photoview
 */
public class CirclePhothViewActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.headTitle)            TextView       headTitle;
    @BindView(R.id.headBackButton)       ImageButton    headBackButton;
    @BindView(R.id.HeadRightImageButton) ImageButton    headRightImageButton;
    @BindView(R.id.view_pager)           HackyViewPager viewPager;


    private List<Uri>      mDatas;
    private ArrayList<Uri> mDeleteDatas;             //需要删除的数据

    private int                mPosition;
    private SamplePagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_photo_view);
    }


    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        mDeleteDatas = new ArrayList<>();

        mDatas = getIntent().getParcelableArrayListExtra("photo");
        mPosition = getIntent().getIntExtra("position", 0);
        mAdapter = new SamplePagerAdapter(mDatas);
        viewPager.setAdapter(mAdapter);
        headTitle.setText((mPosition + 1) + "/" + mDatas.size());
        viewPager.setCurrentItem(mPosition, false);

    }

    @Override
    public void initListener() {
        headBackButton.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                mPosition = position;
                headTitle.setText((mPosition + 1) + "/" + mDatas.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private AlertDialog dialog;

    @OnClick({R.id.headBackButton, R.id.HeadRightImageButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                /*if (mDeleteDatas != null && mDeleteDatas.size() > 0) {
                    EventBusUtil.post(new EventCircleIssueDeletePhotoBean(mDeleteDatas));
                }
                this.finish();*/
                goBack();
                break;

            case R.id.HeadRightImageButton:
                dialog = DialogUtil.showInputDialog(this, true, "提示", "要删除这张照片吗？",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDeleteDatas.add(mDatas.remove(mPosition));
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                                if (mDatas.size() == 0) {
                                    /*EventBusUtil.post(new EventCircleIssueDeletePhotoBean(mDeleteDatas));
                                    CirclePhothViewActivity.this.finish();*/
                                    goBack();
                                }
                            }
                        });
                break;
        }
    }


    private void goBack() {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_DATA, mDeleteDatas);
        setResult(RESULT_OK, intent);
        finish();
    }

    static class SamplePagerAdapter extends PagerAdapter {

        List<Uri> datas;

        public SamplePagerAdapter(List<Uri> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas != null ? datas.size() : 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageHelper.loadImage(container.getContext(), photoView,
                    String.valueOf(datas.get(position)));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
