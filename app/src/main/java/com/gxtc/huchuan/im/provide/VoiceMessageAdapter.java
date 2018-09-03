package com.gxtc.huchuan.im.provide;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.VoiceMessage;
import io.rong.message.VoiceMessageHandler;

/**
 * Created by Gubr on 2017/4/9.
 * 融云voiceMessage的适配器
 * 因为  融云的voicemessage json不符合要求 所以需要写此类来适配
 */
@MessageTag(
        value = "RC:VcMsg",
        flag = 3,
        messageHandler = VoiceMessageHandler.class
)
public class VoiceMessageAdapter extends MessageContent {



    private Uri mUri;
    private int mDuration;
    private String mBase64;
    protected String extra;
    public static final Creator<VoiceMessage> CREATOR = new Creator<VoiceMessage>() {
        public VoiceMessage createFromParcel(Parcel source) {
            return new VoiceMessage(source);
        }

        public VoiceMessage[] newArray(int size) {
            return new VoiceMessage[size];
        }
    };

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public VoiceMessageAdapter(Parcel in) {
        this.setExtra(ParcelUtils.readFromParcel(in));
        this.mUri = (Uri)ParcelUtils.readFromParcel(in, Uri.class);
        this.mDuration = ParcelUtils.readIntFromParcel(in).intValue();
        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
    }


    public VoiceMessageAdapter(VoiceMessage message,String uri){

        setUri(uri==null?message.getUri():Uri.parse(uri));
        setDuration(message.getDuration());
        setExtra(message.getExtra());
        setMentionedInfo(message.getMentionedInfo());
        setUserInfo(message.getUserInfo());
        setBase64(message.getBase64());
    }


    public VoiceMessageAdapter(String data){
        try {
            JSONObject e = new JSONObject(data);
            if(e.has("duration")) {
                this.setDuration(e.optInt("duration"));
            }

            if(e.has("content")) {
                this.setBase64(e.optString("content"));
            }
            if (e.has("uri")){
                this.setUri(Uri.parse(e.optString("uri")));
            }

            if(e.has("extra")) {
                this.setExtra(e.optString("extra"));
            }

            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            Log.e("JSONException", var4.getMessage());
        }
    }

    public VoiceMessageAdapter(byte[] data) {
        this(new String(data));
//        String jsonStr = new String(data);
//
//        try {
//            JSONObject e = new JSONObject(jsonStr);
//            if(e.has("duration")) {
//                this.setDuration(e.optInt("duration"));
//            }
//
//            if(e.has("content")) {
//                this.setBase64(e.optString("content"));
//            }
//            if (e.has("uri")){
//                this.setUri(Uri.parse(e.optString("uri")));
//            }
//
//            if(e.has("extra")) {
//                this.setExtra(e.optString("extra"));
//            }
//
//            if(e.has("user")) {
//                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
//            }
//        } catch (JSONException var4) {
//            Log.e("JSONException", var4.getMessage());
//        }

    }

    private VoiceMessageAdapter(Uri uri, int duration) {
        this.mUri = uri;
        this.mDuration = duration;
    }

    public static VoiceMessageAdapter obtain(Uri uri, int duration) {
        return new VoiceMessageAdapter(uri, duration);
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public String getBase64() {
        return this.mBase64;
    }

    public void setBase64(String base64) {
        this.mBase64 = base64;
    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", this.mBase64);
            jsonObj.put("duration", this.mDuration);
            jsonObj.put("uri",this.getUri().toString());
            if(!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.extra);
            }

            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var3) {
            Log.e("JSONException", var3.getMessage());
        }

//        this.mBase64 = null;
        return jsonObj.toString().getBytes();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.extra);
        ParcelUtils.writeToParcel(dest, this.mUri);
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mDuration));
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
    }


    public VoiceMessage getVoicMessage(){
        VoiceMessage voiceMessage = new VoiceMessage(new byte[]{});
        voiceMessage.setExtra(getExtra());
        voiceMessage.setDuration(getDuration());
        voiceMessage.setUri(getUri());
        voiceMessage.setUserInfo(getUserInfo());
        voiceMessage.setBase64(getBase64());
        return voiceMessage;
    }
}
