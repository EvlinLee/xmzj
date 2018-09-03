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
 * <p>
 * 拉黑 信息 体
 */
@MessageTag(value = "XM:BLMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class BlacklistMessage extends MessageContent {

    private final String TAG = "BlacklistMessage";

    private   String userCode;// 0代表全部用户
    protected String extra;

    public BlacklistMessage() {

    }

    public static BlacklistMessage obtain(String userCode) {
        BlacklistMessage model = new BlacklistMessage();
        if (userCode==null){
            model.setUserCode("0");
        }else{
            model.setUserCode(userCode);
        }
        return model;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", getUserCode());

            if (!TextUtils.isEmpty(getExtra())) {
                jsonObject.put("extra", getExtra());
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


    public BlacklistMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("userCode")) {
                setUserCode(jsonObject.optString("userCode"));
            }
            if (jsonObject.has("extra")) {
                setExtra(jsonObject.optString("extra"));
            }
            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

    }


    public String getUserCode() {
        return userCode;
    }


    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, getExtra());
        ParcelUtils.writeToParcel(dest, userCode);
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }

    public BlacklistMessage(Parcel source) {
        setExtra(ParcelUtils.readFromParcel(source));
        setUserCode(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }


    public static final Creator<BlacklistMessage> CREATOR = new Creator<BlacklistMessage>() {
        @Override
        public BlacklistMessage createFromParcel(Parcel source) {
            return new BlacklistMessage(source);
        }

        @Override
        public BlacklistMessage[] newArray(int size) {
            return new BlacklistMessage[size];
        }
    };
}
