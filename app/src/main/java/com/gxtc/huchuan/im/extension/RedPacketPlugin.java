package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.im.redPacket.IssueRedPacketActivity;

import java.util.Locale;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 红包自定义插件
 */

public class RedPacketPlugin implements IPluginModule {

    //设置插件 Plugin 图标
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.plugin_red_packets_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "红包";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        String targetId = rongExtension.getTargetId();
        if(!TextUtils.isEmpty(targetId) && targetId.equals(UserManager.getInstance().getUserCode())){
            ToastUtil.showShort(rongExtension.getContext(), "不能给自己发送红包");
            return;
        }

        String id = fragment.getActivity().getIntent().getData().getQueryParameter("targetId");
        Conversation.ConversationType type = Conversation.ConversationType
                .valueOf(fragment.getActivity()
                                 .getIntent()
                                 .getData()
                                 .getLastPathSegment()
                                 .toUpperCase(Locale.getDefault()));
        Intent intent = new Intent(fragment.getContext(), IssueRedPacketActivity.class);
        intent.putExtra(Constant.INTENT_DATA, id);
        intent.putExtra("type",type);
        fragment.startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
