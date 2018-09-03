package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gxtc.huchuan.R;

import io.rong.imkit.DeleteClickActions;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/28.
 * 删除更多消息
 */
public class DeleteAtion extends DeleteClickActions {

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.msg_delete);
    }
}
