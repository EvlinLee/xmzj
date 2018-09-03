package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gxtc.commlibrary.base.AbBasePagerAdapter;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.LaunchActivity;

import java.util.List;


public class WelcomeAdapter extends AbBasePagerAdapter<Integer> {



	public WelcomeAdapter(Context context, List<View> views, List<Integer> datas) {
		super(context,views, datas);
	}
	
	@Override
	public void bindData(View view, Integer t,int position) {
		ImageView img = (ImageView) view;
		//欢迎页过大不要直接设置在imageview上，用Glide加载出来避免内存溢出
		Glide.with(MyApplication.getInstance()).load(t).apply(new RequestOptions().placeholder(t)).into(img);
	}
}
