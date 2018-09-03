package com.gxtc.huchuan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决srcollview 嵌套listview冲突的问题
 */
public class ListViewByScroll extends ListView {

	public ListViewByScroll(Context context) {
		super(context);
	}

	public ListViewByScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewByScroll(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	  	int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}