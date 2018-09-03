package com.gxtc.huchuan.ui.im.system

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import im.collect.CollectMessage
import io.rong.common.ParcelUtils
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import org.json.JSONException
import org.json.JSONObject

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 * 系统推送消息
 */
@MessageTag(value = "SystemMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class SystemMessage() : MessageContent() {

    val TAG : String = "SystemMessage";

    var id : String ? = null
    var msgType : Int ? = -1
    var content : String ? = null

    constructor(parcel: Parcel) : this() {
        id = ParcelUtils.readFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, id)
        ParcelUtils.writeToParcel(parcel, msgType)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, userInfo)
    }

    constructor(bytes : ByteArray):this(){
        var jsonStr : String? = null
        try {
            //jsonStr = bytes.toString()
            jsonStr = String(bytes, charset("UTF-8"))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(jsonStr)
            if(jsonObject.has("id")){
                id = jsonObject.optString("id")
            }

            if(jsonObject.has("msgType")){
                msgType = jsonObject.optInt("msgType")
            }

            if(jsonObject.has("content")){
                content = jsonObject.optString("content")
            }

            if(jsonObject.has("user")){
                userInfo = parseJsonToUserInfo(jsonObject.getJSONObject("user"))
            }

        }catch (e : JSONException){
            e.printStackTrace()
        }
    }


    //声明静态方法
    companion object {
        @JvmField
        val CREATOR : Parcelable.Creator<SystemMessage> = object : Parcelable.Creator<SystemMessage> {
            override fun newArray(size: Int): Array<SystemMessage?> = arrayOfNulls<SystemMessage>(size)

            override fun createFromParcel(source: Parcel): SystemMessage = SystemMessage(source)

        }

    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{

            if(!TextUtils.isEmpty(id)){
                jsonObject.put("id",id)
            }

            if(msgType != -1){
                jsonObject.put("msgType",msgType)
            }

            if(!TextUtils.isEmpty(content)){
                jsonObject.put("content",content)
            }

            if(jsonUserInfo != null){
                jsonObject.put("user",jsonUserInfo)
            }

        } catch (e : Exception){
            Log.e(TAG, "JSONException " + e.message)
        }

        try {
            return jsonObject.toString().toByteArray(charset("UTF-8"))
        }catch (e : Exception){
            Log.e(TAG, "JSONException " + e.message)
        }

        return byteArrayOf()
    }

    override fun describeContents(): Int = 0

}