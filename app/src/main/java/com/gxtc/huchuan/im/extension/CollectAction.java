package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.gxtc.huchuan.R;

import io.rong.imkit.actions.IClickActions;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/28.
 * 收藏更多消息
 */
public class CollectAction implements IClickActions{
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.msg_collection);
    }

    @Override
    public void onClick(Fragment fragment) {

    }
}
