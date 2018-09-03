package com.gxtc.huchuan.utils;

import io.rong.imlib.RongIMClient;

/**
 * Created by zzg on 2017/9/1.
 */

public class RIMErrorCodeUtil {

    //暂时先这么多，后期如需要，在去融云官网添加相应的错误码
    public static String handleErrorCode(RongIMClient.ErrorCode errorCode){
        switch (errorCode.getValue()){
            case 405:
                return "你已被对方加入黑名单!";
            case 20604:
                return "发送消息频率过高， 1 秒钟最多只允许发送 5 条消息!";
            case 21406:
                return "不在该讨论组中!";
            case 22406:
                return "不在该群组中!";
            case 22408:
                return "你在群组中已被禁言!";
            case 23409:
                return "已被踢出并禁止加入聊天室!";
            case 23410:
                return "聊天室不存在!";
            case 30002:
                return "当前连接不可用";

        }
        return "UFO出现啦...";
    }
}
