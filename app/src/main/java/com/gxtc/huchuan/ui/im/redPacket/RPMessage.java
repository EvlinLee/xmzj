package com.gxtc.huchuan.ui.im.redPacket;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/5.
 * 发送红包消息
 */
@MessageTag(value = "RongCloudIMRedBagMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class RPMessage extends MessageContent{

    private final String TAG = "RPMessage";

    private String redId;
    private String content;     //红包留言
    private String name;

    public RPMessage(){

    }

    public static RPMessage obtain(String id, String name, String content){
        RPMessage var = new RPMessage();
        var.setRedId(id);
        var.setName(name);
        var.setContent(content);
        return  var;
    }

    public static final Creator<RPMessage> CREATOR = new Creator<RPMessage>() {
        @Override
        public RPMessage createFromParcel(Parcel source) {
            return new RPMessage(source);
        }

        @Override
        public RPMessage[] newArray(int size) {
            return new RPMessage[size];
        }
    };

    public RPMessage(Parcel source){
        setRedId(ParcelUtils.readFromParcel(source));
        setContent(ParcelUtils.readFromParcel(source));
        setName(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,redId);
        ParcelUtils.writeToParcel(dest,content);
        ParcelUtils.writeToParcel(dest,name);
        ParcelUtils.writeToParcel(dest,getUserInfo());
    }

    public RPMessage(byte [] bytes){
        String jsonStr = null;
        try {
            jsonStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("content")) {
                setContent(jsonObject.optString("content"));
            }

            if (jsonObject.has("name")) {
                setName(jsonObject.optString("name"));
            }
            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }
            if (jsonObject.has("redId")) {
                setRedId(jsonObject.optString("redId"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {

            if (!TextUtils.isEmpty(getRedId())) {
                jsonObject.put("redId", getRedId());
            }

            if (!TextUtils.isEmpty(getContent())) {
                jsonObject.put("content", getContent());
            }
            if (!TextUtils.isEmpty(getName())) {
                jsonObject.put("name", getName());
            }
            if (getJSONUserInfo() != null) {
                jsonObject.putOpt("user", getJSONUserInfo());
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }

        try {
            return jsonObject.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getRedId() {
        return redId;
    }

    public void setRedId(String redId) {
        this.redId = redId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
