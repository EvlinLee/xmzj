package com.gxtc.huchuan.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.im.extension.MyRongExtensionModule;
import com.gxtc.huchuan.im.provide.MyGroupInfoProvider;
import com.gxtc.huchuan.im.provide.MyUserInfoProvider;
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity;
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;
import com.gxtc.huchuan.utils.JPushUtil;
import com.gxtc.huchuan.utils.RIMSoundHandler;

import java.util.List;

import io.rong.callkit.RongCallKit;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.push.RongPushClient;

/**
 * Created by Steven on 16/12/30.
 * 融云帮助类
 */

public class RongImHelper {

    //客服id
    public static final String ID_KEFU_DEBUG = "KEFU149455996053212";
    public static final String ID_KEFU = "KEFU149455997594652";


    /**
     * 初始化
     */
    public static void init(Context context){
        RongIM.init(context);
    }

    /**
     * 连接融云
     * @param app
     * @param token
     * @param callback
     */
    public static void connect(Application app, String token , RongIMClient.ConnectCallback callback){
        if (app.getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(app))
                && RongIM.getInstance().getCurrentConnectionStatus() == RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {
            RongIM.connect(token, callback);
        }
    }


    public static void initDefaultConfig(){
        RongIM.setUserInfoProvider(new MyUserInfoProvider(), true);                     //设置用户信息提供者
        RongIM.setGroupInfoProvider(new MyGroupInfoProvider(), true);                   //设置用户信息提供者
        initExtension();                                                                //自定义输入区域拓展功能，加个红包功能

        //初始化融云消息状态
        SettingActivity.sound = SpUtil.getBoolean(MyApplication.getInstance(), SettingActivity.SP_SOUND(), true);
        RIMSoundHandler.INSTANCE.setRongIMSounds(MyApplication.getInstance(),SettingActivity.sound); // 设置融云会话提示的声音是否打开
        boolean notifi = SpUtil.getBoolean(MyApplication.getInstance(), MessageSettingActivity.SP_NEW_MESSAGE(), true);
        JPushUtil.setSoundAndVibrate(MyApplication.getInstance(), notifi, notifi);

        User user = UserManager.getInstance().getUser();
        if(user != null){
            RongIM.getInstance().setCurrentUserInfo(RongImHelper.createUserInfo(user.getUserCode(), user.getName(), user.getHeadPic()));
        }
        RongIM.getInstance().enableNewComingMessageIcon(true);  //显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);     //显示未读消息数目
        RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_240P);
    }


    /**
     * 创建一个userInfo对象
     * @param userId 用户id
     * @param name 用户名字
     * @param avatar 用户头像
     */
    public static UserInfo createUserInfo(String userId, String name, String avatar){
        return new UserInfo(userId, name, Uri.parse(avatar));
    }



    public static void startCustomerServices(Activity activity,String title){
        //首先需要构造使用客服者的用户信息
        /*CSCustomServiceInfo.Builder csBuilder = new CSCustomServiceInfo.Builder();
        CSCustomServiceInfo         csInfo    = csBuilder.nickName("在线客服").build();
        RongIM.getInstance().startCustomerServiceChat(activity, ID_KEFU, title,csInfo);*/

        //暂时使用聊天客服
        //RongIM.getInstance().startPrivateChat(activity,"00006 ","小袁");
        PersonalInfoActivity.startActivity(activity,"00006");
    }

    public static void startCustomerServices(Activity activity,String title,String userCode){

        //暂时使用聊天客服
        RongIM.getInstance().startPrivateChat(activity,userCode,title);
    }


    private static void initExtension(){
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new MyRongExtensionModule());
            }
        }
    }

}

