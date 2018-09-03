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
 * 交易推送消息
 */
@MessageTag(value = "TradeInfoMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class TradeInfoMessage() : MessageContent() {

    val TAG: String = "TradeInfoMsg"

    var userName: String? = null    /// 用户名称

    var userPic: String? = null     /// 用户头像

    var content: String? = null     /// 文字描述

    var tradeInfoId: String? = null          /// 商品ID

    var cover: String? = null       /// 商品封面

    var title: String? = null        /// 商品名称

    var time: String? = null        /// 创建时间

    var msgType: String? = null     /// 通知类型

    constructor(parcel: Parcel) : this() {
        userName = ParcelUtils.readFromParcel(parcel)
        userPic = ParcelUtils.readFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        tradeInfoId = ParcelUtils.readFromParcel(parcel)
        cover = ParcelUtils.readFromParcel(parcel)
        title = ParcelUtils.readFromParcel(parcel)
        time = ParcelUtils.readFromParcel(parcel)
        msgType = ParcelUtils.readFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, userName)
        ParcelUtils.writeToParcel(parcel, userPic)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, tradeInfoId)
        ParcelUtils.writeToParcel(parcel, cover)
        ParcelUtils.writeToParcel(parcel, title)
        ParcelUtils.writeToParcel(parcel, time)
        ParcelUtils.writeToParcel(parcel, msgType)
        ParcelUtils.writeToParcel(parcel, userInfo)
    }

    constructor(bytes: ByteArray) : this() {
        var jsonStr: String? = null;
        try {
            //jsonStr = bytes.toString()
            jsonStr = String(bytes, charset("UTF-8"))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val jsonObject = JSONObject(jsonStr)
            if (jsonObject.has("userName")) {
                userName = jsonObject.optString("userName")
            }

            if (jsonObject.has("userPic")) {
                userPic = jsonObject.optString("userPic")
            }

            if (jsonObject.has("content")) {
                content = jsonObject.optString("content")
            }


            if (jsonObject.has("tradeInfoId")) {
                tradeInfoId = jsonObject.optString("tradeInfoId")
            }

            if (jsonObject.has("cover")) {
                cover = jsonObject.optString("cover")
            }

            if (jsonObject.has("title")) {
                title = jsonObject.optString("title")
            }

            if (jsonObject.has("time")) {
                time = jsonObject.optString("time")
            }

            if (jsonObject.has("msgType")) {
                msgType = jsonObject.optString("msgType")
            }


            if (jsonObject.has("user")) {
                userInfo = parseJsonToUserInfo(jsonObject.getJSONObject("user"))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    //声明静态方法
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TradeInfoMessage> = object : Parcelable.Creator<TradeInfoMessage> {
            override fun newArray(size: Int): Array<TradeInfoMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): TradeInfoMessage = TradeInfoMessage(source)

        }

    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try {

            if (!TextUtils.isEmpty(userName)) {
                jsonObject.put("userName", userName)
            }

            if (!TextUtils.isEmpty(userPic)) {
                jsonObject.put("userPic", userPic)
            }

            if (!TextUtils.isEmpty(content)) {
                jsonObject.put("content", content)
            }

            if (!TextUtils.isEmpty(tradeInfoId)) {
                jsonObject.put("tradeInfoId", tradeInfoId)
            }

            if (!TextUtils.isEmpty(cover)) {
                jsonObject.put("cover", cover)
            }

            if (!TextUtils.isEmpty(title)) {
                jsonObject.put("title", title)
            }

            if (!TextUtils.isEmpty(time)) {
                jsonObject.put("time", time)
            }

            if (!TextUtils.isEmpty(msgType)) {
                jsonObject.put("msgType", msgType)
            }

            if (jsonUserInfo != null) {
                jsonObject.put("user", jsonUserInfo)
            }

        } catch (e: Exception) {
            Log.e(TAG, "JSONException " + e.message)
        }

        try {
            return jsonObject.toString().toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            Log.e(TAG, "JSONException " + e.message)
        }

        return byteArrayOf()
    }

    override fun describeContents(): Int = 0

}