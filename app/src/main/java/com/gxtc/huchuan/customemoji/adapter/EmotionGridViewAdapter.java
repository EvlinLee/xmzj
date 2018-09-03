package com.gxtc.huchuan.customemoji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.gxtc.huchuan.R;
import com.gxtc.huchuan.customemoji.utils.EmotionUtils;

import java.util.List;


public class EmotionGridViewAdapter extends BaseAdapter {

	private Context context;
	private List<Integer> emotionNames;
	private int itemWidth;
    private int emotion_map_type;
	
	public EmotionGridViewAdapter(Context context, List<Integer> emotionNames, int itemWidth,int emotion_map_type) {
		this.context = context;
		this.emotionNames = emotionNames;
		this.itemWidth = itemWidth;
		this.emotion_map_type=emotion_map_type;
	}
	
	@Override
	public int getCount() {
		// +1 最后一个为删除按钮
		return emotionNames.size() + 1;
	}

	@Override
	public Integer getItem(int position) {
		return emotionNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv_emotion = new ImageView(context);
		// 设置内边距
		iv_emotion.setPadding(itemWidth/8, itemWidth/8, itemWidth/8, itemWidth/8);
		LayoutParams params = new LayoutParams(itemWidth, itemWidth);
		iv_emotion.setLayoutParams(params);
		
		//判断是否为最后一个item
		if(position == getCount() - 1) {
			iv_emotion.setImageResource(R.drawable.compose_emotion_delete);
		} else {
			Integer emotionName = emotionNames.get(position);
			iv_emotion.setImageResource(EmotionUtils.getImgByName(emotion_map_type,emotionName));
		}
		
		return iv_emotion;
	}

}
