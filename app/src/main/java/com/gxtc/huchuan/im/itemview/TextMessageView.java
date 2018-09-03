package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.ui.live.conversation.LiveInsertChooseActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity;
import com.gxtc.huchuan.ui.special.SpecialDetailActivity;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;
import com.gxtc.huchuan.utils.TextViewLinkUtils;

import io.rong.imkit.utils.RongDateUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Gubr on 2017/2/21.
 */

public class TextMessageView extends AbsMessageView {

    private PopReward mPopReward;
    private MultiItemTypeAdapter<Message> mAdapter;

    public TextMessageView(Activity activity, MultiItemTypeAdapter<Message> adapter) {
        super(activity);
        this.mAdapter = adapter;
    }

    private static final String TAG = "TextMessageView";

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_char_received_message;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "RC:TxtMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(ViewHolder holder, final Message message, int position) {
        TextMessage content = (TextMessage) message.getContent();
        Extra extra = new Extra(content.getExtra());

        //为了兼容以前的版本 这里分享内容的消息体就先用着textMessage，所以用typeId判断是否是分享内容
        String typeId = extra.getTypeId();
        TextView tvContent = holder.getView(R.id.tv_item_char_received_message_content);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        if(TextUtils.isEmpty(typeId)){
            tvContent.setText(TextViewLinkUtils.getInstance().getUrlClickableSpan(tvContent, content.getContent()));
            tvContent.setVisibility(View.VISIBLE);
            holder.getView(R.id.layout_content).setVisibility(View.GONE);
        }else{
            tvContent.setVisibility(View.GONE);
            holder.getView(R.id.layout_content).setVisibility(View.VISIBLE);
            fillShareData(holder, extra, position);
        }

        holder.setOnLongClickListener(R.id.tv_item_char_received_message_content, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return l.onLongClick(v, message);
            }
        });

        holder.setOnLongClickListener(R.id.layout_content, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return l.onLongClick(v, message);
            }
        });

        UserInfo userInfo = null;
        if ((userInfo = content.getUserInfo()) != null) {
            holder.setTag(R.id.iv_zan, userInfo);

            holder.getView(R.id.iv_zan).setOnClickListener(l);
            holder.setText(R.id.tv_item_char_received_message_sender, userInfo.getName());
            ImageHelper.loadCircle(getActivity(),(ImageView) holder.getView(R.id.iv_item_char_received_message_head),userInfo.getPortraitUri().toString(),R.drawable.person_icon_head);

            String sender = null;
            switch (extra.getSenderType()) {
                case "1":
                    sender = "讲师";
                    break;
                case "2":
                    sender = "观众";
                    break;
                case "3":
                    sender = "主持人";
                    break;
            }
            holder.setText(R.id.tv_item_char_received_message_sendertype, sender);
        }

        TextView tvTime = holder.getView(R.id.tv_time);
        long sendTime = MessageFactory.getMessageTime(message);
        if(position == 0){
            if(RongDateUtils.isShowChatTime(System.currentTimeMillis(),sendTime,5*60)){
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(DateUtil.getFormatChatTime(sendTime));
            }else{
                tvTime.setVisibility(View.GONE);
            }

        }else{
            Message lastMsg = mAdapter.getDatas().get(position - 1);
            long lastTime = MessageFactory.getMessageTime(lastMsg);
            if(RongDateUtils.isShowChatTime(sendTime,lastTime,5*60)){
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(DateUtil.getFormatChatTime(sendTime));
            }else{
                tvTime.setVisibility(View.GONE);
            }
        }

    }


    private void fillShareData(ViewHolder holder, Extra extra, int position){
        String title = extra.getTypeTitle();
        String cover = extra.getTypeCover();
        String infoType = extra.getInfoType();

        View layoutContnet = holder.getView(R.id.layout_content);
        ImageView img = holder.getView(R.id.img_share);
        TextView  tvTitle = holder.getView(R.id.tv_share);
        TextView  tvType = holder.getView(R.id.tv_type);

        layoutContnet.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
        ImageHelper.loadImage(MyApplication.getInstance(), img, cover, R.drawable.live_list_place_holder_120);
        tvTitle.setText(title);

        String type = "";
        if(String.valueOf(LiveInsertChooseActivity.TYPE_CIRCLE).equals(infoType)){
            type = "圈子";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_ARTICLE).equals(infoType)){
            type = "文章";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_CLASS).equals(infoType)){
            type = "课堂";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_SERISE).equals(infoType)){
            type = "系列课";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_DEAL).equals(infoType)){
            type = "交易";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_MALL).equals(infoType)){
            type = "商品";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_VISITING_CARD).equals(infoType)){
            type = "个人名片";
        }

        if(String.valueOf(LiveInsertChooseActivity.TYPE_SPECIAL).equals(infoType)){
            type = "专题";
        }
        tvType.setText(type);

        layoutContnet.setTag(extra);
        layoutContnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Extra extra = (Extra) v.getTag();
                String infoType = extra.getInfoType();
                String typeId = extra.getTypeId();
                CircleShareHandler shareHandler = new CircleShareHandler(getActivity());
                switch (StringUtil.toInt(infoType)){
                    case LiveInsertChooseActivity.TYPE_CIRCLE:
                        shareHandler.shareHandle(getActivity(), typeId, StringUtil.toInt(CircleShareHandler.SHARE_CIRCLE), null);
                        break;

                    case LiveInsertChooseActivity.TYPE_ARTICLE:
                        shareHandler.shareHandle(getActivity(), typeId, StringUtil.toInt(CircleShareHandler.SHARE_NEW), null);
                        break;

                    case LiveInsertChooseActivity.TYPE_CLASS:
                        shareHandler.shareHandle(getActivity(), typeId, StringUtil.toInt(CircleShareHandler.SHARE_CLASS), null);
                        break;

                    case LiveInsertChooseActivity.TYPE_DEAL:
                        shareHandler.shareHandle(getActivity(), typeId, StringUtil.toInt(CircleShareHandler.SHARE_DEAL), null);
                        break;

                    case LiveInsertChooseActivity.TYPE_MALL:
                        shareHandler.shareHandle(getActivity(), typeId, StringUtil.toInt(CircleShareHandler.SHARE_MALL), null);
                        break;

                    case LiveInsertChooseActivity.TYPE_VISITING_CARD:
                        PersonalHomePageActivity.startActivity(getActivity(), typeId);
                        break;

                    case LiveInsertChooseActivity.TYPE_SPECIAL:
                        SpecialDetailActivity.gotoSpecialDetailActivity(getActivity(), typeId);
                        break;
                }

            }
        });
    }


}
