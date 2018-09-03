package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.utils.DateUtil;

import io.rong.imkit.utils.RongDateUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Gubr on 2017/2/21.
 */

public class NoneMessageView extends AbsMessageView {

    private PopReward mPopReward;

    public NoneMessageView(Activity activity) {
        super(activity);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_chat_room_no_support;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return false;
    }

    @Override
    public void convert(ViewHolder holder, final Message message, int position) {

    }

}
