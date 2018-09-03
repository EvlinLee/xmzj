package com.gxtc.huchuan.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.bean.MessageBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MessageApi;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mall.MallDetailedActivity;
import com.gxtc.huchuan.ui.message.ConfirmJoinGroupActivity;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.mine.scanCode.ScanCodeNotLinkerActivity;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIMessage;
import io.rong.imlib.model.Message;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/4.
 * 二维码处理类
 */

public class QrcodeHandler {

    private Context mContext;
    private CircleShareHandler mHandler;

    public QrcodeHandler(Context context) {
        mContext = context;
        mHandler = new CircleShareHandler(mContext);
    }

    //如果是扫一扫的话 就真的没有二维码了
    public boolean resolvingCode(String url,String userCode){
        if(TextUtils.isEmpty(url))  return false;

        if(url.contains("QRType")){
            Uri uri = Uri.parse(url);
            String qrType = uri.getQueryParameter("QRType");
            String id = uri.getQueryParameter("bzId");
            LogUtil.d("tag","----url--="+url);
            try {
                int type = Integer.valueOf(qrType);
                switch (type){
                    //个人资料
                    case 1:
                        PersonalInfoActivity.startActivity(mContext,id);
                        return true;

                    //圈子
                    case 2:
                        Message message = new Message();
                        message.setSenderUserId(userCode);
                        UIMessage uiMessage = UIMessage.obtain(message);
                        mHandler.getCircleInfo(id, 4, uiMessage);
                        return true;

                    //课堂
                    case 3:
                        LiveIntroActivity.startActivity(mContext,id,userCode);
                        return true;

                    //群聊
                    case 4:
                        getInfo(id);
                        return true;

                    //课堂主页
                    case 5:
                        LiveHostPageActivity.startActivity(mContext,"1",id);
                        return true;

                    //系列课
                    case 6:
                        SeriesActivity.startActivity(mContext, id, userCode);
                        return true;

                    //商品
                    case 7:
                        MallDetailedActivity.startActivity(mContext,id);
                        return true;

                    //交易
                    case 8:
                        GoodsDetailedActivity.startActivity(mContext,id);
                        return true;
                }

            }catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }else{
            LogUtil.d("tag","----url-1-="+url);
            //过滤不掉"http://weixin.qq.com"  公众号名片; 微信个人名片 https://u.wechat.com
            if(!url.startsWith("http://weixin.qq.com") || !url.startsWith("https://u.wechat.com")){
               CommonWebViewActivity.startActivity(mContext,url,"");
            }else {
                Intent intent = new Intent(mContext,ScanCodeNotLinkerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url",url);
                mContext.startActivity(intent);
            }
            return false;
        }
        return false;
    }
    //这个用正则表达式判断不了是否未链接，暂时先不用
    public  boolean isLinker(String str) {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\\\.\\\\-]+\\\\.([a-zA-Z]{2,4})(:\\\\d+)?(/[a-zA-Z0-9\\\\.\\\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\\\.\\\\-]+\\\\.([a-zA-Z]{2,4})(:\\\\d+)?(/[a-zA-Z0-9\\\\.\\\\-~!@#$%^&*+?:_/=<>]*)?)");

        java.util.regex.Matcher match = pattern.matcher(str);
        return match.matches() ;
    }

    public void getInfo(final String id){
        EventBusUtil.post(new EventLoadBean(true));
        Subscription sub1 =
            MessageApi.getInstance()
                .getGroupRole(UserManager.getInstance().getToken(),id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ApiObserver<ApiResponseBean<MessageBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        EventBusUtil.post(new EventLoadBean(false));

                        //先获取群聊的信息，在进入群聊
                        MessageBean bean = (MessageBean) data;
                        if(bean != null){
                            //未加入群聊，跳到加入群聊页面
                            if(bean.getIsJoin() == 0){
                                ConfirmJoinGroupActivity.startActivity(mContext,bean);
                            }else{
                                RongIM.getInstance().startGroupChat(mContext,bean.getChatId(),bean.getGroupName());
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        EventBusUtil.post(new EventLoadBean(false));
                        ToastUtil.showShort(mContext,message);
                    }
                }));
        RxTaskHelper.getInstance().addTask(this,sub1);
    }


    public void destroy(){
        mHandler.destroy();
        RxTaskHelper.getInstance().cancelTask(this);
    }
}
