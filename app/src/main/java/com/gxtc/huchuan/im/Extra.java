package com.gxtc.huchuan.im;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gubr on 2017/2/22.
 */

public class Extra {

    private String senderType;      //发送者身份 1、嘉宾  2、观众   3、主持人
    private boolean isAsk;          //是否有提问
    private String msgId;           //消息的id
    private String isClass;         //是否属于课堂
    private long sentTime;          //发送时间
    private String infoType;        // 0.圈子 1：文章，2：课程，3：系列课，4：交易，5：商品，6：个人名片
    private String typeId;
    private String typeCover;       //分享的封面
    private String typeTitle;       //分享的标题


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public boolean getIsAsk() {
        return isAsk;
    }

    public void setIsAsk(boolean isAsk) {
        this.isAsk = isAsk;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getIsClass() {
        return isClass;
    }

    public void setIsClass(String isClass) {
        this.isClass = isClass;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeCover() {
        return typeCover;
    }

    public void setTypeCover(String typeCover) {
        this.typeCover = typeCover;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public Extra(String json) {
        if(json == null) return;
        if(TextUtils.isEmpty(json))
            json = "";
        try {
            JSONObject e = new JSONObject(json);

            if (e.has("senderType")) setSenderType(e.optString("senderType"));
            if (e.has("isAsk")) setIsAsk(Boolean.parseBoolean(e.optString("isAsk")));
            if (e.has("msgId")) setMsgId(e.optString("msgId"));
            if (e.has("sentTime")) setSentTime(e.optLong("sentTime",0L));
            if (e.has("isClass")) setIsClass(e.optString("isClass"));
            if (e.has("infoType")) setInfoType(e.optString("infoType"));
            if (e.has("typeId")) setTypeId(e.optString("typeId"));
            if (e.has("typeCover")) setTypeCover(e.optString("typeCover"));
            if (e.has("typeTitle")) setTypeTitle(e.optString("typeTitle"));


        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    public static Extra obtan(String senderType, boolean isAsk, String msgId,String sentTime,String isClass, String typeId, String infoType, String typeTitle, String typeCover) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderType", senderType);
            jsonObject.put("isAsk", isAsk);
            jsonObject.put("msgId", msgId);
            if(isClass != null)
            jsonObject.put("isClass", isClass);
            if (sentTime!=null)
            jsonObject.put("sentTime",sentTime );

            if (typeId!=null)
                jsonObject.put("typeId",typeId );

            if (infoType!=null)
                jsonObject.put("infoType",infoType );

            if (typeTitle!=null)
                jsonObject.put("typeTitle",typeTitle );

            if (typeCover!=null)
                jsonObject.put("typeCover",typeCover );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Extra(jsonObject.toString());
    }

    public static Extra obtan(String senderType, boolean isAsk, String msgId, String sentTime, String isClass) {
        return obtan(senderType, isAsk, msgId, sentTime, isClass, null, null, null, null);
    }

    public static Extra obtan(String senderType, boolean isAsk, String msgId,String isClass) {
        return obtan(senderType, isAsk, msgId,null,isClass, null, null, null, null);
    }

    public String encode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderType", senderType);
            jsonObject.put("isAsk", isAsk+"");
            jsonObject.put("msgId", msgId);
            jsonObject.put("sentTime", sentTime);
            jsonObject.put("isClass", isClass);

            if(!TextUtils.isEmpty(typeId)){
                jsonObject.put("typeId", typeId);
            }

            if(!TextUtils.isEmpty(infoType)){
                jsonObject.put("infoType", infoType);
            }

            if(!TextUtils.isEmpty(typeCover)){
                jsonObject.put("typeCover", typeCover);
            }

            if(!TextUtils.isEmpty(typeTitle)){
                jsonObject.put("typeTitle", typeTitle);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
