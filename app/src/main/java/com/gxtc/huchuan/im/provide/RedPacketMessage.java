package com.gxtc.huchuan.im.provide;

/**
 * Created by Gubr on 2017/4/13.
 */

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Gubr on 2017/3/10.
 * <p>
 * 红包信息。
 */
@MessageTag(value = "XM:RpMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class RedPacketMessage extends MessageContent {
    private static final String TAG = "RPMessage";

    private   String content;
    private   String price;
    protected String extra;

    public RedPacketMessage() {

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", getContent());
            jsonObject.put("price", getPrice());


            if (!TextUtils.isEmpty(getExtra())) {
                jsonObject.put("extra", getExtra());
            }
            if (getJSONUserInfo() != null) {
                jsonObject.putOpt("user", getJSONUserInfo());
            }
            if (getJsonMentionInfo() != null) {
                jsonObject.putOpt("mentionedInfo", getJsonMentionInfo());

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


    public static RedPacketMessage obtain(String sender, String receiver, String price) {
        RedPacketMessage model   = new RedPacketMessage();
        String           content = sender + "打赏了" + receiver;
        model.setContent(content);
        model.price = price;
        return model;
    }


    public RedPacketMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("content")) {
                setContent(jsonObject.optString("content"));
            }
            if (jsonObject.has("price")) {
                setPrice(jsonObject.optString("price"));
            }


            if (jsonObject.has("extra")) {
                setExtra(jsonObject.optString("extra"));
            }
            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }
            if (jsonObject.has("mentionedInfo")) {
                setMentionedInfo(parseJsonToMentionInfo(jsonObject.getJSONObject("mentionedInfo")));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, getExtra());
        ParcelUtils.writeToParcel(dest, content);
        ParcelUtils.writeToParcel(dest, price);
        ParcelUtils.writeToParcel(dest, getUserInfo());
        ParcelUtils.writeToParcel(dest, getMentionedInfo());
    }

    public RedPacketMessage(Parcel source) {
        setExtra(ParcelUtils.readFromParcel(source));
        setContent(ParcelUtils.readFromParcel(source));
        setPrice(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
        setMentionedInfo(ParcelUtils.readFromParcel(source, MentionedInfo.class));
    }

    public String getContent() {
        return content;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }


    public static final Creator<RedPacketMessage> CREATOR = new Creator<RedPacketMessage>() {
        @Override
        public RedPacketMessage createFromParcel(Parcel source) {
            return new RedPacketMessage(source);
        }

        @Override
        public RedPacketMessage[] newArray(int size) {
            return new RedPacketMessage[size];
        }
    };
}
