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
 * 推送直播  信息体  一个占位 没什么用。
 *
 */
@MessageTag(value = "XM:ReMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ReceivedMessage extends MessageContent {
    private static final String TAG = "ReceivedMessage";

    private String content;
    protected String extra;

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


    public ReceivedMessage(byte[] data){
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

    public ReceivedMessage(Parcel source) {
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




    public static final Creator<ReceivedMessage> CREATOR = new Creator<ReceivedMessage>() {
        @Override
        public ReceivedMessage createFromParcel(Parcel source) {
            return new ReceivedMessage(source);
        }

        @Override
        public ReceivedMessage[] newArray(int size) {
            return new ReceivedMessage[size];
        }
    };
}
