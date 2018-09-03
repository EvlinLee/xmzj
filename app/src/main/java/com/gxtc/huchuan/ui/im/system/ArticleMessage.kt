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
@MessageTag(value = "ArticleMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class ArticleMessage() : MessageContent() {

    val TAG : String = "ArticleMessage";

    var id: String? = null
    var pic: String? = null
    var title: String? = null
    var content: String? = ""
    var time: Long? = 0
    var msgType: Int? = -1

    constructor(parcel: Parcel) : this() {
        id = ParcelUtils.readFromParcel(parcel)
        pic = ParcelUtils.readFromParcel(parcel)
        title = ParcelUtils.readFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        time = ParcelUtils.readLongFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, id)
        ParcelUtils.writeToParcel(parcel, pic)
        ParcelUtils.writeToParcel(parcel, title)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, time)
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
            if(jsonObject.has("id")){
                id = jsonObject.optString("id")
            }

            if(jsonObject.has("pic")){
                pic = jsonObject.optString("pic")
            }

            if(jsonObject.has("title")){
                title = jsonObject.optString("title")
            }

            if(jsonObject.has("content")){
                content = jsonObject.optString("content")
            }

            if(jsonObject.has("time")){
                time = jsonObject.optLong("time")
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
        val CREATOR : Parcelable.Creator<ArticleMessage> = object : Parcelable.Creator<ArticleMessage> {
            override fun newArray(size: Int): Array<ArticleMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): ArticleMessage = ArticleMessage(source)

        }
    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(id)){
                jsonObject.put("id",id)
            }

            if(!TextUtils.isEmpty(pic)){
                jsonObject.put("pic",pic)
            }

            if(!TextUtils.isEmpty(title)){
                jsonObject.put("title",title)
            }

            if(!TextUtils.isEmpty(content)){
                jsonObject.put("content",content)
            }

            if(time != 0L){
                jsonObject.put("time",time)
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