package com.gxtc.huchuan.im.extension;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.im.ui.ConversationListActivity;
import com.gxtc.huchuan.im.ui.CopyConversationFragment;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.MyActionSheetDialog;

import java.util.List;

import io.rong.imkit.actions.IClickActions;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Message;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/24.
 * 更多模式的转发操作
 */
public class MoreAction implements IClickActions {

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.navigation_icon_share);
    }

    private MyActionSheetDialog actionDialog;

    @Override
    public void onClick(final Fragment curFragment) {
        final String [] titls = {"逐条转发", "合并转发"};
        if(curFragment != null && curFragment.getContext() != null){
            actionDialog = DialogUtil.showActionDialog(curFragment.getContext(), titls);
            actionDialog.show();
            actionDialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(titls[position].equals("逐条转发")){
                        stepRelay(curFragment);
                    }

                    if(titls[position].equals("合并转发")){
                        mergeRelay(curFragment);
                    }

                    actionDialog.dismiss();
                }
            });
        }
    }

    private void stepRelay(Fragment curFragment){
        CopyConversationFragment fragment = (CopyConversationFragment) curFragment;
        List<Message>        messages = fragment.getCheckedMessages();
        if(messages == null || messages.size() == 0){
            ToastUtil.showShort(curFragment.getContext(), curFragment.getString(R.string.message_relay_empty));
            return;
        }

        if(fragment.getActivity() instanceof ConversationActivity){
            ConversationActivity activity = (ConversationActivity) fragment.getActivity();
            activity.setSelectMessage(messages);
            ConversationListActivity.startActivity(activity, ConversationActivity.REQUEST_SHARE_MORE_RELAY, Constant.SELECT_TYPE_RELAY);
        }
    }

    private void mergeRelay(Fragment curFragment){
        CopyConversationFragment fragment = (CopyConversationFragment)curFragment;
        List<Message>        messages = fragment.getCheckedMessages();
        if(messages == null || messages.size() == 0){
            ToastUtil.showShort(curFragment.getContext(), curFragment.getString(R.string.message_relay_empty));
            return;
        }

        if(fragment.getActivity() instanceof ConversationActivity){
            ConversationActivity activity = (ConversationActivity) fragment.getActivity();
            activity.setSelectMessage(messages);
            ConversationListActivity.startActivity(activity, ConversationActivity.REQUEST_SHARE_MORE_MERGE_RELAY, Constant.SELECT_TYPE_RELAY);
        }
    }
}
