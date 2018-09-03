package com.gxtc.huchuan.ui.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.huchuan.R;

/**
 * 来自 苏修伟 on 2018/5/8.
 * 视频集详情界面
 */
public class VideoDetailsFragment extends BaseLazyFragment{

    @Override
    protected void lazyLoad() {

    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_video_details, container,false);
        return view;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }
}
