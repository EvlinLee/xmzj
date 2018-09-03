package com.gxtc.huchuan.im.provide;

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

/**
 * Created by Gubr on 2017/3/10.
 *
 * 删除信息
 *
 */
@MessageTag(value = "XM:RmMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class RemoveMessage extends MessageContent {
    private static final String TAG = "ReceivedMessage";

    private String content;//要删除的信息的msgId;
    protected String extra;

    public RemoveMessage() {
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", getContent());


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


    public RemoveMessage(byte[] data){
        String jsonStr=null;
        try{
            jsonStr= new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("content")){
                setContent(jsonObject.optString("content"));
            }


            if (jsonObject.has("extra")){
                setExtra(jsonObject.optString("extra"));
            }
            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }
            if (jsonObject.has("mentionedInfo")) {
                setMentionedInfo(parseJsonToMentionInfo(jsonObject.getJSONObject("mentionedInfo")));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: "+e.getMessage()   );
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
        ParcelUtils.writeToParcel(dest, getUserInfo());
        ParcelUtils.writeToParcel(dest, getMentionedInfo());
    }

    public RemoveMessage(Parcel source) {
        setExtra(ParcelUtils.readFromParcel(source));
        setContent(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
        setMentionedInfo(ParcelUtils.readFromParcel(source, MentionedInfo.class));
    }

    public String getContent() {
        return content;
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




    public static final Creator<RemoveMessage> CREATOR = new Creator<RemoveMessage>() {
        @Override
        public RemoveMessage createFromParcel(Parcel source) {
            return new RemoveMessage(source);
        }

        @Override
        public RemoveMessage[] newArray(int size) {
            return new RemoveMessage[size];
        }
    };

    public static RemoveMessage obtain() {
        return new RemoveMessage();
    }
}
