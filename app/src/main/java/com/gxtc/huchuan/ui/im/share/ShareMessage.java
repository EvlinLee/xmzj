package com.gxtc.huchuan.ui.im.share;

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
 * 分享消息
 */
@MessageTag(value = "RongCloudIMShareMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ShareMessage extends MessageContent{

    private final String TAG = "ShareMessage";

    private String typeId;
    private String typeTitle;     //内容标题
    private String typeCover;     //内容封面
    private String infoType;      //0:普通动态，1：新闻连接，2：课程，3：交易, 4：圈子 5:邀请管理员
                                  //            6:免费邀请好友加入圈子  7: 系列课, 8 主持人， 9 管理员, 10 免费进课堂  11 免费进系列课 12商品详情 13 直播间

    public ShareMessage(){

    }

    public static ShareMessage obtain(String typeId, String typeTitle, String typeCover,String infoType){
        ShareMessage var = new ShareMessage();
        var.setTypeId(typeId);
        var.setTypeTitle(typeTitle);
        var.setTypeCover(typeCover);
        var.setInfoType(infoType);
        return  var;
    }

    public static final Creator<ShareMessage> CREATOR = new Creator<ShareMessage>() {
        @Override
        public ShareMessage createFromParcel(Parcel source) {
            return new ShareMessage(source);
        }

        @Override
        public ShareMessage[] newArray(int size) {
            return new ShareMessage[size];
        }
    };

    public ShareMessage(Parcel source){
        setTypeId(ParcelUtils.readFromParcel(source));
        setTypeCover(ParcelUtils.readFromParcel(source));
        setTypeTitle(ParcelUtils.readFromParcel(source));
        setInfoType(ParcelUtils.readFromParcel(source));
        setUserInfo(ParcelUtils.readFromParcel(source, UserInfo.class));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,typeId);
        ParcelUtils.writeToParcel(dest,typeCover);
        ParcelUtils.writeToParcel(dest,typeTitle);
        ParcelUtils.writeToParcel(dest,infoType);
        ParcelUtils.writeToParcel(dest,getUserInfo());
    }

    public ShareMessage(byte [] bytes){
        String jsonStr = null;
        try {
            jsonStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("infoType")) {
                setInfoType(jsonObject.optString("infoType"));
            }
            if (jsonObject.has("typeId")) {
                setTypeId(jsonObject.optString("typeId"));
            }
            if (jsonObject.has("typeTitle")) {
                setTypeTitle(jsonObject.optString("typeTitle"));
            }
            if (jsonObject.has("typeCover")) {
                setTypeCover(jsonObject.optString("typeCover"));
            }
            if (jsonObject.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObject.getJSONObject("user")));
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject = new JSONObject();
        try {

            if (!TextUtils.isEmpty(getInfoType())) {
                jsonObject.put("infoType", getInfoType());
            }

            if (!TextUtils.isEmpty(getTypeId())) {
                jsonObject.put("typeId", getTypeId());
            }

            if (!TextUtils.isEmpty(getTypeTitle())) {
                jsonObject.put("typeTitle", getTypeTitle());
            }

            if (!TextUtils.isEmpty(getTypeCover())) {
                jsonObject.put("typeCover", getTypeCover());
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTypeCover() {
        return typeCover;
    }

    public void setTypeCover(String typeCover) {
        this.typeCover = typeCover;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }
}
