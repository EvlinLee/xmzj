package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveListBean;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Gubr on 2017/2/27.
 */

public class TopicCommentAdapter extends BaseRecyclerAdapter<Message> {
    public TopicCommentAdapter(Context context, List<Message> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, Message o) {
        TextMessage textMessage = (TextMessage) o.getContent();
        UserInfo userInfo = textMessage.getUserInfo();
        if (userInfo == null) {
            return;
        }
        ImageHelper.loadHeadIcon(holder.getItemView().getContext(), holder.getImageView(R.id.iv_head), R.drawable.person_icon_head, userInfo.getPortraitUri().toString());
        holder.setText(R.id.tv_name, userInfo.getName())
                .setText(R.id.tv_time, DateUtil.formatTime(o.getSentTime(), "MM-dd HH:mm:ss"))
                .setText(R.id.tv_content, textMessage.getContent());
        TextView content = holder.getViewV2(R.id.tv_content);
        Extra extra = new Extra(textMessage.getExtra());
        holder.getView(R.id.iv_icon_question).setVisibility(extra.getIsAsk() ? View.VISIBLE : View.GONE);
    }


}
