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
 * 课堂推送消息
 */
@MessageTag(value = "ChatMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class ClassMessage() : MessageContent() {

    val TAG : String = "ClassMessage"

    var content: String? = ""         //文字
    var userCode: String? = ""
    var userName: String? = ""
    var userPic: String? = ""
    var bzId: String? = ""              //业务id
    var title: String? = ""             //业务标题
    var pic: String? = ""               //业务封面
    var orderId: String? = ""           //订单id
    var orderTime: Long? = 0         //下单时间
    var msgType: Int? = -1           //通知类型

    constructor(parcel: Parcel) : this() {
        content = ParcelUtils.readFromParcel(parcel)
        userCode = ParcelUtils.readFromParcel(parcel)
        userName = ParcelUtils.readFromParcel(parcel)
        userPic = ParcelUtils.readFromParcel(parcel)
        bzId = ParcelUtils.readFromParcel(parcel)
        title = ParcelUtils.readFromParcel(parcel)
        pic = ParcelUtils.readFromParcel(parcel)
        orderId = ParcelUtils.readFromParcel(parcel)
        orderTime = ParcelUtils.readLongFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel,content)
        ParcelUtils.writeToParcel(parcel,userCode)
        ParcelUtils.writeToParcel(parcel,userName)
        ParcelUtils.writeToParcel(parcel,userPic)
        ParcelUtils.writeToParcel(parcel,bzId)
        ParcelUtils.writeToParcel(parcel,title)
        ParcelUtils.writeToParcel(parcel,pic)
        ParcelUtils.writeToParcel(parcel,orderId)
        ParcelUtils.writeToParcel(parcel,orderTime)
        ParcelUtils.writeToParcel(parcel,msgType)
        ParcelUtils.writeToParcel(parcel,userInfo)
    }

    constructor(bytes : ByteArray):this(){
        var jsonStr : String? = null;
        try {
            //jsonStr = bytes.toString()
            jsonStr = String(bytes, charset("UTF-8"))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(jsonStr)
            if(jsonObject.has("content")){
                content = jsonObject.optString("content")
            }

            if(jsonObject.has("userCode")){
                userCode = jsonObject.optString("userCode")
            }

            if(jsonObject.has("userName")){
                userName = jsonObject.optString("userName")
            }

            if(jsonObject.has("userPic")){
                userPic = jsonObject.optString("userPic")
            }

            if(jsonObject.has("bzId")){
                bzId = jsonObject.optString("bzId")
            }

            if(jsonObject.has("title")){
                title = jsonObject.optString("title")
            }

            if(jsonObject.has("pic")){
                pic = jsonObject.optString("pic")
            }

            if(jsonObject.has("orderId")){
                orderId = jsonObject.optString("orderId")
            }

            if(jsonObject.has("orderTime")){
                orderTime = jsonObject.optLong("orderTime")
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
        val CREATOR : Parcelable.Creator<ClassMessage>  = object : Parcelable.Creator<ClassMessage> {
            override fun newArray(size: Int): Array<ClassMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): ClassMessage = ClassMessage(source)

        }
    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(content)){
                jsonObject.put("content",content)
            }

            if(!TextUtils.isEmpty(userCode)){
                jsonObject.put("userCode",userCode)
            }

            if(!TextUtils.isEmpty(userName)){
                jsonObject.put("userName",userName)
            }

            if(!TextUtils.isEmpty(userPic)){
                jsonObject.put("userPic",userPic)
            }

            if(!TextUtils.isEmpty(bzId)){
                jsonObject.put("bzId",bzId)
            }

            if(!TextUtils.isEmpty(title)){
                jsonObject.put("title",title)
            }

            if(!TextUtils.isEmpty(pic)){
                jsonObject.put("pic",pic)
            }

            if(!TextUtils.isEmpty(orderId)){
                jsonObject.put("orderId",orderId)
            }

            if(orderTime != 0L){
                jsonObject.put("orderTime",orderTime)
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