package com.gxtc.huchuan.im.itemview;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.recyclerview.MultiItemTypeAdapter;
import com.gxtc.commlibrary.recyclerview.base.ViewHolder;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.im.Extra;
import com.gxtc.huchuan.im.MessageFactory;
import com.gxtc.huchuan.im.manager.MessageManager;
import com.gxtc.huchuan.pop.PopReward;
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.ArrayList;

import io.rong.imkit.utils.RongDateUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

/**
 * Created by Gubr on 2017/2/22.
 */

public class ImageMessageView extends AbsMessageView {
    private PopReward mPopReward;
    private MultiItemTypeAdapter<Message> mAdapter;

    public ImageMessageView(Activity activity, MultiItemTypeAdapter<Message> adapter) {
        super(activity);
        this.mAdapter = adapter;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_char_received_image;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return "RC:ImgMsg".equals(item.getObjectName());
    }

    @Override
    public void convert(final ViewHolder holder, final Message message, int position) {
        final ImageMessage content = (ImageMessage) message.getContent();
        Extra              extra   = new Extra(content.getExtra());


        final ImageView imageView = (ImageView) holder.getView(R.id.iv_item_char_received_image_content);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
                ArrayList<Uri> imageList = MessageManager.getInstance().getImageList();
                int i=0;
                for (; i < imageList.size(); i++) {
                    if (imageList.get(i) != null && imageList.get(i).equals(content.getRemoteUri())) {
                        break;
                    }
                }

                CommonPhotoViewActivity.startActivity(mActivity,imageList,i);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return l.onLongClick(v,message);
            }
        });
        UserInfo userInfo = null;
        if ((userInfo = content.getUserInfo()) != null) {
            holder.setTag(R.id.iv_zan, userInfo);
            holder.getView(R.id.iv_zan).setOnClickListener(l);
            holder.setText(R.id.tv_item_char_received_image_sender, content.getUserInfo().getName());
            ImageHelper.loadCircle(getActivity(),(ImageView) holder.getView(R.id.iv_item_char_received_image_head),userInfo.getPortraitUri().toString(),R.drawable.person_icon_head);

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
            holder.setText(R.id.tv_item_char_received_image_sendertype, sender);

        }
        if(content.getRemoteUri() == null) return;
        Uri uri = content.getRemoteUri();
        if(uri != null){
            ImageHelper.loadIntoUseFitWidthOrHeight(MyApplication.getInstance(), content.getRemoteUri().toString(), R.drawable.icon_placeholder, imageView);
        }

        TextView tvTime = holder.getView(R.id.tv_time);
        long sendTime = MessageFactory.getMessageTime(message);
        if(position == 0){
            if(RongDateUtils.isShowChatTime(System.currentTimeMillis(), sendTime, 5*60)){
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

}
