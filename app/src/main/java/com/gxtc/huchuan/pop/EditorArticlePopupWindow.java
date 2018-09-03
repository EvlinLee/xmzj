package com.gxtc.huchuan.pop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * Created by sjr on 2017/2/27.
 */

public class EditorArticlePopupWindow extends LinearLayout {

    Context mContext;
    private OnClickCallback mCallback;

    public EditorArticlePopupWindow(Context context) {
        this(context, null);
    }

    public EditorArticlePopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorArticlePopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.shap_editor_article);
    }


    /**
     * 设置标题内容
     *
     * @param context
     * @param integers 图片
     * @param hasDraw  是否带右侧向下箭�?
     */
    public void initViews(Context context, List<Integer> integers, boolean hasDraw) {
        this.mContext = context;
        setLayoutContent(mContext, integers, hasDraw);
    }

    /**
     * 设置条目点击监听
     */
    public void setClickListener(OnClickCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 设置内容
     *
     * @param context
     * @param integers
     * @param hasDraw
     */
    private void setLayoutContent(Context context, final List<Integer> integers, boolean hasDraw) {
        removeAllViews();
        if (integers != null && integers.size() > 0) {
            // 不带箭头
            if (!hasDraw) {
                for (int i = 0; i < integers.size(); i++) {
                    final int index = i;
                    final ImageView imageView = new ImageView(context);
                    imageView.setImageResource(integers.get(i));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowUtil.dip2px(mContext, 30),
                            WindowUtil.dip2px(mContext, 30), 1.0f);

                    params.setMargins(WindowUtil.dip2px(mContext, 8), 0, WindowUtil.dip2px(mContext, 8), 0);
                    params.gravity = Gravity.CENTER;
                    imageView.setLayoutParams(params);
                    addView(imageView);
                    if (i < integers.size() - 1) {
                        LayoutParams layoutParams = new LayoutParams(1, 48);
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        View view = new View(context);
                        view.setLayoutParams(layoutParams);
                        view.setBackgroundResource(R.color.white);
                        addView(view);
                    }
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onItemClick(EditorArticlePopupWindow.this, integers.size(), index);

                            }
                        }
                    });
                }
            }
        } else {
            throw new RuntimeException("不能没有图片");
        }
    }

    /**
     * 点击事件接口
     */
    public interface OnClickCallback {
        /**
         * 点击子视图时调用
         *
         * @param parentView 当前操作的View视图
         * @param size       当前视图中子视图数量
         * @param index      当前点击子视图索�?
         */
        void onItemClick(LinearLayout parentView, int size, int index);
    }

}

