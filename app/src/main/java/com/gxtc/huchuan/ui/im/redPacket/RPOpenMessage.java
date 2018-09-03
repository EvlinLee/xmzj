package com.gxtc.huchuan.ui.im.redPacket;


import android.os.Parcel;
import android.text.TextUtils;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse.MsgAnalysePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * xxx领取红包消息
 */
@MessageTag(value = "RongCloudIMRedStausMessage", flag = MessageTag.ISPERSISTED)
public class RPOpenMessage extends MessageContent{

    private String redId;
    private String isGetDone;
    private String sendNickName;
    private String sendNickId;

    public static final Creator<RPOpenMessage> CREATOR = new Creator<RPOpenMessage>() {
        @Override
        public RPOpenMessage createFromParcel(Parcel source) {
            return new RPOpenMessage(source);
        }

        @Override
        public RPOpenMessage[] newArray(int size) {
            return new RPOpenMessage[size];
        }
    };


    public static RPOpenMessage obtain(String redId, String isGetDone, String sendNickId, String sendNickName){
        RPOpenMessage msg = new RPOpenMessage();
        msg.setRedId(redId);
        msg.setIsGetDone(isGetDone);
        msg.setSendNickId(sendNickId);
        msg.setSendNickName(sendNickName);
        return msg;
    }

    public RPOpenMessage() {
    }

    public RPOpenMessage(Parcel source){
        this.setRedId(ParcelUtils.readFromParcel(source));
        this.setIsGetDone(ParcelUtils.readFromParcel(source));
        this.setSendNickName(ParcelUtils.readFromParcel(source));
        this.setSendNickId(ParcelUtils.readFromParcel(source));
        this.setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
    }

    public RPOpenMessage(byte [] bytes){
        String jsonStr = null;
        try {
            jsonStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }
            if (jsonObject.has("redId")) {
                setRedId(jsonObject.optString("redId"));
            }

            if (jsonObject.has("isGetDone")) {
                setIsGetDone(jsonObject.optString("isGetDone"));
            }
            if (jsonObject.has("sendNickName")) {
                setSendNickName(jsonObject.optString("sendNickName"));
            }

            if (jsonObject.has("sendNickId")) {
                setSendNickId(jsonObject.optString("sendNickId"));
            }

        } catch (JSONException e) {
            LogUtil.i("JSONException: " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();

        try {
            if(!TextUtils.isEmpty(this.getIsGetDone())) {
                jsonObject.put("isGetDone", isGetDone);
            }

            if(!TextUtils.isEmpty(this.getRedId())) {
                jsonObject.put("redId", redId);
            }

            if(!TextUtils.isEmpty(this.getSendNickName())) {
                jsonObject.put("sendNickName", sendNickName);
            }

            if(!TextUtils.isEmpty(this.getSendNickId())) {
                jsonObject.put("sendNickId", sendNickId);
            }

            if(this.getJSONUserInfo() != null) {
                jsonObject.putOpt("user",getJSONUserInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return jsonObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.redId);
        ParcelUtils.writeToParcel(dest, this.isGetDone);
        ParcelUtils.writeToParcel(dest, this.sendNickName);
        ParcelUtils.writeToParcel(dest, this.sendNickId);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
    }

    public String getRedId() {
        return redId;
    }

    public void setRedId(String redId) {
        this.redId = redId;
    }


    public String getIsGetDone() {
        return isGetDone;
    }

    public void setIsGetDone(String isGetDone) {
        this.isGetDone = isGetDone;
    }

    public String getSendNickName() {
        return sendNickName;
    }

    public void setSendNickName(String sendNickName) {
        this.sendNickName = sendNickName;
    }

    public String getSendNickId() {
        return sendNickId;
    }

    public void setSendNickId(String sendNickId) {
        this.sendNickId = sendNickId;
    }
}
