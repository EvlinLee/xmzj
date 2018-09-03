package im.collect

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.TextUtils
import android.util.Log
import io.rong.common.ParcelUtils
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/15.
 * 收藏消息
 */
@MessageTag(value = "RongCloudIMCollectMessage", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class CollectMessage() : MessageContent() {

    val TAG : String = "CollectMessage";

    var url: String? = null
    var cover: String? = null
    var id: String? = null
    var type: String? = null
    var content: String? = ""

    constructor(parcel: Parcel) : this() {
        id = ParcelUtils.readFromParcel(parcel)
        url = ParcelUtils.readFromParcel(parcel)
        type = ParcelUtils.readFromParcel(parcel)
        cover = ParcelUtils.readFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel,UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel,id)
        ParcelUtils.writeToParcel(parcel,url)
        ParcelUtils.writeToParcel(parcel,type)
        ParcelUtils.writeToParcel(parcel,cover)
        ParcelUtils.writeToParcel(parcel,content)
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
            val jsonObject : JSONObject = JSONObject(jsonStr)
            if(jsonObject.has("id")){
                id = jsonObject.optString("id")
            }

            if(jsonObject.has("url")){
                url = jsonObject.optString("url")
            }

            if(jsonObject.has("type")){
                type = jsonObject.optString("type")
            }

            if(jsonObject.has("cover")){
                cover = jsonObject.optString("cover")
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
        val CREATOR : Parcelable.Creator<CollectMessage>  = object : Creator<CollectMessage>{
            override fun newArray(size: Int): Array<CollectMessage?> = arrayOfNulls<CollectMessage>(size)

            override fun createFromParcel(source: Parcel): CollectMessage = CollectMessage(source)

        }

        @JvmStatic
        fun obtain(id: String, type: String, cover: String ?, content : String , url: String ?) : CollectMessage {
            val msg = CollectMessage()
            msg.id = id
            msg.url = url
            msg.type = type
            msg.cover = cover
            msg.content = content
            return msg
        }

    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try{
            if(!TextUtils.isEmpty(id)){
                jsonObject.put("id",id)
            }

            if(!TextUtils.isEmpty(url)){
                jsonObject.put("url",url)
            }

            if(!TextUtils.isEmpty(type)){
                jsonObject.put("type",type)
            }

            if(!TextUtils.isEmpty(cover)){
                jsonObject.put("cover",cover)
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

    override fun describeContents(): Int {
        return 0
    }

}