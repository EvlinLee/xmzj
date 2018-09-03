package com.gxtc.huchuan.ui.im.postcard;

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
 * zzg
 */
@MessageTag(value = "RongCloudIMPersonCardMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class PTMessage extends MessageContent{

    private final String TAG = "PTMessage";

    private String userCode;
    private String name;     //名称
    private String headPic;     //头像

    public PTMessage(){

    }

    public static PTMessage obtain(String userCode, String name, String headPic){
        PTMessage var = new PTMessage();
        var.setUserCode(userCode);
        var.setName(name);
        var.setHeadPic(headPic);
        return  var;
    }

    public static final Creator<PTMessage> CREATOR = new Creator<PTMessage>() {
        @Override
        public PTMessage createFromParcel(Parcel source) {
            return new PTMessage(source);
        }

        @Override
        public PTMessage[] newArray(int size) {
            return new PTMessage[size];
        }
    };

    public PTMessage(Parcel source){
        setUserCode(ParcelUtils.readFromParcel(source));
        setName(ParcelUtils.readFromParcel(source));
        setHeadPic(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,userCode);
        ParcelUtils.writeToParcel(dest,name);
        ParcelUtils.writeToParcel(dest,headPic);
        ParcelUtils.writeToParcel(dest,getUserInfo());
    }

    public PTMessage(byte [] bytes){
        String jsonStr = null;
        try {
            jsonStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("userCode")) {
                setUserCode(jsonObject.optString("userCode"));
            }
            if (jsonObject.has("name")) {
                setName(jsonObject.optString("name"));
            }
            if (jsonObject.has("headPic")) {
                setHeadPic(jsonObject.optString("headPic"));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {

            if (!TextUtils.isEmpty(getUserCode())) {
                jsonObject.put("userCode", getUserCode());
            }

            if (!TextUtils.isEmpty(getName())) {
                jsonObject.put("name", getName());
            }

            if (!TextUtils.isEmpty(getHeadPic())) {
                jsonObject.put("headPic", getHeadPic());
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }
}
