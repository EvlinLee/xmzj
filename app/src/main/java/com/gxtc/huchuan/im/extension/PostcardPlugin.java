package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;

import java.util.Locale;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

import static com.gxtc.huchuan.Constant.SELECT_TYPE_CARD;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 分享名片
 */

public class PostcardPlugin implements IPluginModule {

    //设置插件 Plugin 图标
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.plugin_man_selector);
    }

    @Override
    public String obtainTitle(Context context) {
        return "名片";
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        String id = fragment.getActivity().getIntent().getData().getQueryParameter("targetId");
        Conversation.ConversationType type = Conversation.ConversationType
                .valueOf(fragment.getActivity()
                        .getIntent()
                        .getData()
                        .getLastPathSegment()
                        .toUpperCase(Locale.getDefault()));
        Intent intent = new Intent(fragment.getContext(), FocusActivity.class);
        intent.putExtra("focus_flag", "2");
        intent.putExtra("select_type_card", SELECT_TYPE_CARD);
        intent.putExtra(Constant.INTENT_DATA, id);
        intent.putExtra("type", type);
        fragment.getActivity().startActivityForResult(intent, ConversationActivity.REQUEST_SHARE_CARD);
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
