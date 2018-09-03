package com.gxtc.huchuan.widget.guide;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.WindowUtil;

/**
 * Created by binIoter
 */
class Common {
    /**
     * 设置Component
     */
    static View componentToView(LayoutInflater inflater, Component c) {
        View view = c.getView(inflater);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        final MaskView.LayoutParams lp = new MaskView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.offsetX = c.getXOffset();
        lp.offsetY = c.getYOffset();
        lp.targetAnchor = c.getAnchor();
        lp.targetParentPosition = c.getFitPosition();
        if (params != null) {
            lp.width = params.width;
            lp.height = params.height;
        }
        view.setLayoutParams(lp);
        return view;
    }

    /**
     * Rect在屏幕上去掉状态栏高度的绝对位置
     */
    static Rect getViewAbsRect(View view, int parentX, int parentY) {
        int statusHeight = WindowUtil.getStatusHeight(view.getContext());
        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        Rect rect = new Rect();
        rect.set(loc[0], loc[1], loc[0] + view.getMeasuredWidth(), loc[1] + view.getMeasuredHeight());
        Boolean guideOne = SpUtil.getBoolean(view.getContext(), "guide_one");
        if (guideOne) {

            rect.offset(-parentX, -parentY + statusHeight);
            SpUtil.putBoolean(view.getContext(), "guide_one", false);
        } else
            rect.offset(-parentX, -parentY);

        return rect;

    }
}
