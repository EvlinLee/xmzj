package com.gxtc.huchuan.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.BounceEnter.BounceBottomEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.animation.ZoomEnter.ZoomInEnter;
import com.flyco.animation.ZoomExit.ZoomInExit;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.CommonAdapter;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.pop.PopBubble;
import com.gxtc.huchuan.pop.PopManage;
import com.gxtc.huchuan.ui.mine.circle.ReportActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.DialogUtil;

import java.util.HashMap;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

import static com.gxtc.huchuan.bean.ChatInfosBean.ROLE_MANAGER;


/**
 * Created by Gubr on 2017/3/19.
 * 聊天界面  讨论页面  适配器
 */

public class DiscussAdapter extends BaseRecyclerAdapter<Message> {
    private ChatInfosBean mBean;
    private boolean isManage = false;
    private String  userCode = UserManager.getInstance().getUserCode();

    public DiscussAdapter(Context context, List<Message> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    public DiscussAdapter(Activity activity, List<Message> messages, int item_topic_comment,
            ChatInfosBean bean) {
        super(activity, messages, item_topic_comment);
        mBean = bean;
        isManage = !"2".equals(mBean.getRoleType());
    }

    @Override
    public void bindData(ViewHolder holder, final int position, final Message message) {
        TextMessage content = (TextMessage) message.getContent();
        Extra       extra   = new Extra(content.getExtra());
        UserInfo userInfo = null;

        if ((userInfo = content.getUserInfo()) != null) {
            if(UserManager.getInstance().getUserCode().equals( userInfo.getUserId() )){
                holder.getView(R.id.tv_report).setVisibility(View.GONE);
            }else {
                holder.getView(R.id.tv_report).setVisibility(View.VISIBLE);
            }
            String time = DateUtil.formatTime(message.getSentTime(), "yyyy-MM-dd HH:mm");

            holder.setText(R.id.tv_name, userInfo.getName())
                  .setText(R.id.tv_time,time)
                  .setText(R.id.tv_content, content.getContent())
                  .getView(R.id.iv_icon_question)
                  .setVisibility(extra.getIsAsk() ? View.VISIBLE : View.GONE);
            holder.getView(R.id.tv_manage).setVisibility(
                    userInfo.getUserId().equals(UserManager.getInstance().getUserCode()) || (mBean.getRoleType().equals(ROLE_MANAGER) ) ? View.GONE : View.VISIBLE);
            ImageHelper.loadHeadIcon(context, holder.getImageView(R.id.iv_head), R.drawable.person_icon_head_120, userInfo.getPortraitUri().toString());
            holder.setOnClick(R.id.tv_report, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReportActivity.jumptoReportActivity(getContext(),message.getMessageId()+"","3");
                }
            });
            holder.getView(R.id.tv_manage).setVisibility((isManage || userCode.equals(message.getContent().getUserInfo().getUserId())) ? View.VISIBLE : View.GONE);
            holder.setOnClick(R.id.tv_manage, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l != null) {
                        l.onClick(v,message,position);
                    }
                }
            });
        }
    }


    public interface ManageListener {
    public   void   onClick(View v,Message message,int Position);
    }


    private ManageListener l;

    public void setPopManageListener(ManageListener l) {
        this.l = l;
    }
}
