package com.gxtc.huchuan.ui.im.video

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
@MessageTag(value = "VideoMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class VideoMessage() : MessageContent() {

    val TAG : String = "VideoMessage";

    var id: String? = null
    var cover: String? = null
    var url: String? = null
    var progress: Int? = 0
    var width: Int = 0
    var height: Int = 0
    var duration: Long = 0

    //一下属性 不作为传输用途
    var localCover = ""             //本地图片地址

    constructor(parcel: Parcel) : this() {
        id = ParcelUtils.readFromParcel(parcel)
        cover = ParcelUtils.readFromParcel(parcel)
        url = ParcelUtils.readFromParcel(parcel)
        progress = ParcelUtils.readIntFromParcel(parcel)
        width = ParcelUtils.readIntFromParcel(parcel)
        height = ParcelUtils.readIntFromParcel(parcel)
        duration = ParcelUtils.readLongFromParcel(parcel)
        localCover = ParcelUtils.readFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, id)
        ParcelUtils.writeToParcel(parcel, cover)
        ParcelUtils.writeToParcel(parcel, url)
        ParcelUtils.writeToParcel(parcel, progress)
        ParcelUtils.writeToParcel(parcel, width)
        ParcelUtils.writeToParcel(parcel, height)
        ParcelUtils.writeToParcel(parcel, duration)
        ParcelUtils.writeToParcel(parcel, localCover)
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

            if(jsonObject.has("cover")){
                cover = jsonObject.optString("cover")
            }

            if(jsonObject.has("url")){
                url = jsonObject.optString("url")
            }

            if(jsonObject.has("progress")){
                progress = jsonObject.optInt("progress")
            }

            if(jsonObject.has("width")){
                width = jsonObject.optInt("width")
            }

            if(jsonObject.has("height")){
                height = jsonObject.optInt("height")
            }

            if(jsonObject.has("duration")){
                duration = jsonObject.optLong("duration")
            }

            if(jsonObject.has("localCover")){
                localCover = jsonObject.optString("localCover")
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
        val CREATOR : Parcelable.Creator<VideoMessage> = object : Parcelable.Creator<VideoMessage> {
            override fun newArray(size: Int): Array<VideoMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): VideoMessage = VideoMessage(source)

        }
    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(id)){
                jsonObject.put("id",id)
            }

            if(!TextUtils.isEmpty(cover)){
                jsonObject.put("cover",cover)
            }

            if(!TextUtils.isEmpty(url)){
                jsonObject.put("url",url)
            }

            if(progress != -1){
                jsonObject.put("progress", progress)
            }

            if(width != -1){
                jsonObject.put("width", width)
            }

            if(height != -1){
                jsonObject.put("height", height)
            }

            if(duration != -1L){
                jsonObject.put("duration", duration)
            }

            if(!TextUtils.isEmpty(localCover)){
                jsonObject.put("localCover",localCover)
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