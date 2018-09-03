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
 * 文章推送消息
 */
@MessageTag(value = "MallMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class MallMessage() : MessageContent() {

    val TAG : String = "MallMessage";

    var orderId: String? = null
    var orderNum: String? = ""
    var cover: String? = null
    var title: String? = null
    var msgType: Int? = -1

    constructor(parcel: Parcel) : this() {
        orderId = ParcelUtils.readFromParcel(parcel)
        orderNum = ParcelUtils.readFromParcel(parcel)
        cover = ParcelUtils.readFromParcel(parcel)
        title = ParcelUtils.readFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, orderId)
        ParcelUtils.writeToParcel(parcel, orderNum)
        ParcelUtils.writeToParcel(parcel, cover)
        ParcelUtils.writeToParcel(parcel, title)
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
            val jsonObject = JSONObject(jsonStr)
            if(jsonObject.has("orderId")){
                orderId = jsonObject.optString("orderId")
            }

            if(jsonObject.has("orderNum")){
                orderNum = jsonObject.optString("orderNum")
            }

            if(jsonObject.has("cover")){
                cover = jsonObject.optString("cover")
            }

            if(jsonObject.has("title")){
                title = jsonObject.optString("title")
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
        val CREATOR : Parcelable.Creator<MallMessage> = object : Parcelable.Creator<MallMessage> {
            override fun newArray(size: Int): Array<MallMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): MallMessage = MallMessage(source)
        }
    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(orderId)){
                jsonObject.put("orderId",orderId)
            }

            if(!TextUtils.isEmpty(orderNum)){
                jsonObject.put("orderNum",orderNum)
            }

            if(!TextUtils.isEmpty(cover)){
                jsonObject.put("cover",cover)
            }

            if(!TextUtils.isEmpty(title)){
                jsonObject.put("title",title)
            }

            if(msgType != -1 ){
                jsonObject.put("msgType", msgType)
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