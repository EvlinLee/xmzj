package com.gxtc.huchuan.ui.im.system

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import io.rong.common.ParcelUtils
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import org.json.JSONException
import org.json.JSONObject

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 * 圈子推送消息
 */
@MessageTag(value = "GroupMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class CircleMessage() : MessageContent() {

    val TAG : String = "CircleMessage";

    var groupId: String? = null
    var groupName: String? = null
    var groupPic: String? = null
    var content: String? = null
    var msgType: Int? = -1

    constructor(parcel: Parcel) : this() {
        groupId = ParcelUtils.readFromParcel(parcel)
        groupName = ParcelUtils.readFromParcel(parcel)
        groupPic = ParcelUtils.readFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, groupId)
        ParcelUtils.writeToParcel(parcel, groupName)
        ParcelUtils.writeToParcel(parcel, groupPic)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, msgType)
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
            val jsonObject : JSONObject = JSONObject(jsonStr)
            if(jsonObject.has("groupId")){
                groupId = jsonObject.optString("groupId")
            }

            if(jsonObject.has("groupName")){
                groupName = jsonObject.optString("groupName")
            }

            if(jsonObject.has("groupPic")){
                groupPic = jsonObject.optString("groupPic")
            }

            if(jsonObject.has("content")){
                content = jsonObject.optString("content")
            }

            if(jsonObject.has("msgType")){
                msgType = jsonObject.optInt("msgType")
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
        val CREATOR : Parcelable.Creator<CircleMessage> = object : Parcelable.Creator<CircleMessage> {
            override fun newArray(size: Int): Array<CircleMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): CircleMessage = CircleMessage(source)

        }

    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(groupId)){
                jsonObject.put("groupId",groupId)
            }

            if(!TextUtils.isEmpty(groupName)){
                jsonObject.put("groupName",groupName)
            }

            if(!TextUtils.isEmpty(groupPic)){
                jsonObject.put("groupPic",groupPic)
            }

            if(!TextUtils.isEmpty(content)){
                jsonObject.put("content",content)
            }

            if(msgType != -1){
                jsonObject.put("msgType",msgType)
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