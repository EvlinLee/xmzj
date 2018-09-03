package com.gxtc.huchuan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gxtc.commlibrary.utils.ActivityUtils;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GsonUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.event.EventDealBean;
import com.gxtc.huchuan.bean.event.EventJPushBean;
import com.gxtc.huchuan.bean.event.EventJPushMessgeBean;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity;
import com.gxtc.huchuan.ui.mine.classroom.message.MyMessageActivity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MyJPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        /*type
        消息类型。
            0：担保交易，1：收费课程，2，流量交易，3，收费圈子到期，4，商家交易，
            5，作者审核，6，主播审核，7：圈子审核，8：打赏，9：充值，
            11：提现，12：退款,13, 账户流水,14, 用户关注 15，新闻审核，16，动态推送
            21，申请好友推送
        bizId 业务id*/
        //用户收到通知
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.i("extra  :  " + extra);        //extra  :  {"type":"0","bizId":"99"}

            try {
                int    type  = Integer.valueOf((String) GsonUtil.getJsonValue(extra, "type"));
                String bizId = (String) GsonUtil.getJsonValue(extra, "bizId");

                switch (type) {
                    //担保交易
                    case 0:
                        //EventBusUtil.post(new EventJPushBean(type, bizId));
                        EventBusUtil.post(new EventDealBean());
                        break;

                    //收费课程
                    case 1:
                        break;

                    //流量交易
                    case 2:
                        break;

                    //收费圈子到期
                    case 3:
                        break;

                    //商家交易
                    case 4:
                        break;

                    //作者审核
                    case 5:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type ,bizId ));
                        break;

                    //主播审核
                    case 6:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type ,bizId));
                        break;

                    //圈子审核
                    case 7:
                        break;

                    //打赏
                    case 8:
                        break;

                    //充值
                    case 9:
                        break;

                    //提现
                    case 11:
                        break;

                    //退款
                    case 12:
                        break;

                    //账户流水
                    case 13:
                        EventBusUtil.post(new EventJPushBean(type, bizId));
                        break;

                    //用户关注
                    case 14:

                        break;

                    //新闻审核
                    case 15:
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //用户点击打开了通知
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            List<Intent> intents = new ArrayList<>();

            if (!ActivityUtils.isActivityExists(context.getPackageName(), ".ui.MainActivity",
                    context)) {
                Intent intentMain = new Intent(context, MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intents.add(intentMain);
            }

            try {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                LogUtil.i("extra  :  " + extra);        //extra  :  {"saleType":"0","type":"13","bizId":"459"}

                int    type  = Integer.valueOf((String) GsonUtil.getJsonValue(extra, "type"));
                String bizId = (String) GsonUtil.getJsonValue(extra, "bizId");

                switch (type) {
                    //跳转到订单详情页面
                    case 0:
                        String saleType = (String) GsonUtil.getJsonValue(extra, "saleType");
                        Intent intent1;
                        //0是买家
                        if ("0".equals(saleType)) {
                            intent1 = new Intent(context, OrderDetailedBuyerActivity.class);

                        //1是卖家
                        } else {
                            intent1 = new Intent(context, OrderDetailedActivity.class);
                        }
                        intent1.putExtra(Constant.INTENT_DATA, bizId);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intents.add(intent1);
                        startActivity(context, intents);
                        break;

                    //收费圈子到期
                    case 3:
                        int circleId = Integer.valueOf(bizId);
                        Intent intent3 = new Intent(context, CircleJoinActivity.class);
                        intent3.putExtra("xuefei", true);
                        intent3.putExtra("byLiveId", circleId);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intents.add(intent3);
                        startActivity(context, intents);
                        break;

                    // 自定义打开的界面
                    default:
                        Intent i = new Intent(context, MyMessageActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intents.add(i);
                        startActivity(context, intents);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //自定义推送消息
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            try {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                int    type  = Integer.valueOf((String) GsonUtil.getJsonValue(extra, "type"));
                String bizId = (String) GsonUtil.getJsonValue(extra, "bizId");
                switch (type) {
                    //动态提醒
                    case 16:
                        String userPic = (String) GsonUtil.getJsonValue(extra, "userPic");
                        String userName = (String) GsonUtil.getJsonValue(extra, "userName");
                        String unReadNum = (String) GsonUtil.getJsonValue(extra, "unReadNum");
                        String userCode = (String) GsonUtil.getJsonValue(extra, "userCode");
                        EventBusUtil.postStickyEvent(new EventJPushMessgeBean(userPic, userName, unReadNum, userCode));
                        break;

                    //圈子提醒
                    case 17:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId));
                        break;

                    //文章提醒
                    case 18:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId));
                        break;

                    //课堂提醒
                    case 19:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId));
                        break;

                    //交易提醒
                    case 20:
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId));
                        break;

                    //申请好友
                    case 21:
                        int num = Integer.valueOf((String) GsonUtil.getJsonValue(extra,"num"));
                        String add_userPic = (String) GsonUtil.getJsonValue(extra, "userPic");
                        EventBusUtil.post(new EventJPushBean(type, bizId, add_userPic, num));
                        break;
                    //精华提醒  不需要这两个推送啦！！
                  /*  case 22:
                        int groupId = Integer.valueOf((String) GsonUtil.getJsonValue(extra,"groupId"));
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId,groupId));//301
                        break;
                    //文件提醒
                    case 23:
                        groupId = Integer.valueOf((String) GsonUtil.getJsonValue(extra,"groupId"));
                        EventBusUtil.postStickyEvent(new EventJPushBean(type, bizId,groupId));
                        break;*/
                }
            } catch (Exception e) {
                LogUtil.e("tag", e.getMessage());
            }

        }

    }

    private void startActivity(Context context, List<Intent> intents) {
        Intent intentArr[] = new Intent[intents.size()];
        for (int i = 0; i < intents.size(); i++) {
            intentArr[i] = intents.get(i);
        }
        context.startActivities(intentArr);
    }

}