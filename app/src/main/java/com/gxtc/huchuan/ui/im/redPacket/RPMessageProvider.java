package com.gxtc.huchuan.ui.im.redPacket;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.CircleRepository;
import com.gxtc.huchuan.data.CircleSource;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.RedPacketOpenDialog;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.concurrent.TimeUnit;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 红包消息提供者
 */
@ProviderTag(messageContent = RPMessage.class, showReadState = true)
public class RPMessageProvider extends IContainerItemProvider.MessageProvider<RPMessage> {

    private Context mContext;
    private boolean isLoading = false;

    @Override
    public void bindView(View view, int i, RPMessage rpMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();
        //发送方
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.bri_bg.setBackgroundResource(R.drawable._bg_from_hongbao);
            holder.tv_bri_target.setText("查看红包");

        //接收方
        } else {
            holder.bri_bg.setBackgroundResource(R.drawable._bg_to_hongbao);
            holder.tv_bri_target.setText("领取红包");
        }
        holder.tv_bri_mess.setText(rpMessage.getContent());
    }

    @Override
    public Spannable getContentSummary(RPMessage rpMessage) {
        return new SpannableString("[红包消息]");
    }


    //在这里做网络请求 会造成内存泄露
    @Override
    public void onItemClick(View view, int i, RPMessage rpMessage, UIMessage uiMessage) {}

    @Override
    public void onItemLongClick(View view, int i, RPMessage rpMessage, UIMessage uiMessage) {}

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        mContext = context;
        View       view   = LayoutInflater.from(context).inflate(R.layout.item_rp_message, null);
        ViewHolder holder = new ViewHolder();
        holder.layout = (RelativeLayout) view.findViewById(R.id.layout);
        holder.tv_bri_mess = (TextView) view.findViewById(R.id.tv_bri_mess);
        holder.tv_bri_target = (TextView) view.findViewById(R.id.tv_bri_target);
        holder.tv_bri_name = (TextView) view.findViewById(R.id.tv_bri_name);
        holder.bri_bg = (RelativeLayout) view.findViewById(R.id.bri_bg);
        view.setTag(holder);
        return view;
    }

    class ViewHolder {
        RelativeLayout layout;
        RelativeLayout bri_bg;
        TextView       tv_bri_mess;
        TextView       tv_bri_target;
        TextView       tv_bri_name;
    }

    /*private CircleSource mData;
    private long pressTime;

    //获取红包信息
    private void getData(final Context context, RPMessage rpMessage, final Message message) {
        if(mData == null) mData = new CircleRepository();
        if(rpMessage == null) return;

        //请求红包信息 小于0.3秒就不要显示加载页面了
        isLoading = true;
        pressTime = System.currentTimeMillis();
        final Subscription loadSub =
                Observable.timer(300, TimeUnit.MILLISECONDS)
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    if(isLoading){
                                        EventBusUtil.post(new EventLoadBean(true));
                                    }
                                }
                            });

        String token = UserManager.getInstance().getToken();
        mData.getRedPacketInfo(token, rpMessage.getRedId(), new ApiCallBack<RedPacketBean>() {
            @Override
            public void onSuccess(RedPacketBean data) {
                isLoading = false;
                //请求红包信息 小于0.3秒就不要显示加载页面了
                long time = System.currentTimeMillis();
                long cha = time - pressTime;
                if(cha < 300){
                    if(loadSub != null) loadSub.unsubscribe();
                }
                EventBusUtil.post(new EventLoadBean(false));

                if(data != null && context != null){
                    data.setTargetId(message.getTargetId());
                    data.setConversationType(message.getConversationType());

                    //如果是私聊 并且是自己点击红包
                    String userCoder = UserManager.getInstance().getUserCode();
                    if(message.getConversationType() == Conversation.ConversationType.PRIVATE && data.getUserCode().equals(userCoder)){
                        Intent intent = new Intent(context, RedPacketDetailedActivity.class);
                        intent.putExtra(Constant.INTENT_DATA,data);
                        context.startActivity(intent);
                        return;
                    }

                    //没领取这个红包，打开弹窗
                    if(data.getIsSnatch() == 0){
                        showOpenDialog(context,data);

                    //领取了红包，打开红包详情页面
                    }else{
                        Intent intent = new Intent(context, RedPacketDetailedActivity.class);
                        intent.putExtra(Constant.INTENT_DATA,data);
                        context.startActivity(intent);
                    }

                }
            }

            @Override
            public void onError(String errorCode, String message) {
                if(loadSub != null) loadSub.unsubscribe();
                isLoading = false;
                EventBusUtil.post(new EventLoadBean(false));
                ToastUtil.showShort(context,message);
            }
        });
    }

    private void showOpenDialog(Context context, RedPacketBean data){
        RedPacketOpenDialog mOpenDialog = new RedPacketOpenDialog(context,data);
        mOpenDialog.show();
    }*/
}
