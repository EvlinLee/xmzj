package com.gxtc.huchuan.ui.im.merge

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.gxtc.huchuan.ui.im.system.ArticleMessage
import io.rong.common.ParcelUtils
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import org.json.JSONException
import org.json.JSONObject

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/28.
 */
@MessageTag(value = "recordMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class MergeHistoryMessage(): MessageContent() {

    var id: String? = null
    var title: String? = null
    var content: String? = null

    constructor(parcel: Parcel) : this() {
        try {
            id = ParcelUtils.readFromParcel(parcel)
            title = ParcelUtils.readFromParcel(parcel)
            content = ParcelUtils.readFromParcel(parcel)
            userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        ParcelUtils.writeToParcel(parcel, id)
        ParcelUtils.writeToParcel(parcel, title)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, userInfo)
    }

    constructor(bytes : ByteArray):this(){
        var jsonStr : String? = null
        try {
            jsonStr = String(bytes, charset("UTF-8"))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(jsonStr)
            if(jsonObject.has("id")){
                id = jsonObject.optString("id")
            }

            if(jsonObject.has("title")){
                title = jsonObject.optString("title")
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

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(id)){
                jsonObject.put("id",id)
            }

            if(!TextUtils.isEmpty(title)){
                jsonObject.put("title",title)
            }

            if(!TextUtils.isEmpty(content)){
                jsonObject.put("content",content)
            }

            if(jsonUserInfo != null){
                jsonObject.put("user",jsonUserInfo)
            }

        } catch (e : Exception){
            e.printStackTrace()
        }

        try {
            return jsonObject.toString().toByteArray(charset("UTF-8"))
        }catch (e : Exception){
            e.printStackTrace()
        }
        return byteArrayOf()
    }

    override fun describeContents(): Int = 0

    //声明静态方法
    companion object {
        @JvmField
        val CREATOR : Parcelable.Creator<MergeHistoryMessage> = object : Parcelable.Creator<MergeHistoryMessage> {
            override fun newArray(size: Int): Array<MergeHistoryMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): MergeHistoryMessage = MergeHistoryMessage(source)

        }
    }
}