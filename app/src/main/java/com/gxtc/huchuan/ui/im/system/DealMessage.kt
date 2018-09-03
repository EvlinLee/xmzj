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
@MessageTag(value = "TradeMsg", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
class DealMessage() : MessageContent() {

    val TAG: String = "DealMessage"

    var userCode: String? = null
    var userName: String? = null
    var userPic: String? = null
    var content: String? = null
    var title: String? = ""
    var orderId: String? = ""
    var tradeInfoId: String? = ""
    var time: Long? = 0
    var status: Int? = -1
    var msgType: Int? = 0
    var orderNum: String? = ""

    constructor(parcel: Parcel) : this() {
        userCode = ParcelUtils.readFromParcel(parcel)
        userName = ParcelUtils.readFromParcel(parcel)
        userPic = ParcelUtils.readFromParcel(parcel)
        content = ParcelUtils.readFromParcel(parcel)
        title = ParcelUtils.readFromParcel(parcel)
        status = ParcelUtils.readIntFromParcel(parcel)
        orderId = ParcelUtils.readFromParcel(parcel)
        tradeInfoId = ParcelUtils.readFromParcel(parcel)
        time = ParcelUtils.readLongFromParcel(parcel)
        msgType = ParcelUtils.readIntFromParcel(parcel)
        orderNum = ParcelUtils.readFromParcel(parcel)
        userInfo = ParcelUtils.readFromParcel(parcel, UserInfo::class.java)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        ParcelUtils.writeToParcel(parcel, userCode)
        ParcelUtils.writeToParcel(parcel, userName)
        ParcelUtils.writeToParcel(parcel, userPic)
        ParcelUtils.writeToParcel(parcel, content)
        ParcelUtils.writeToParcel(parcel, title)
        ParcelUtils.writeToParcel(parcel, status)
        ParcelUtils.writeToParcel(parcel, orderId)
        ParcelUtils.writeToParcel(parcel, tradeInfoId)
        ParcelUtils.writeToParcel(parcel, time)
        ParcelUtils.writeToParcel(parcel, msgType)
        ParcelUtils.writeToParcel(parcel, orderNum)
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
            if (jsonObject.has("userCode")) {
                userCode = jsonObject.optString("userCode")
            }

            if (jsonObject.has("userName")) {
                userName = jsonObject.optString("userName")
            }

            if (jsonObject.has("userPic")) {
                userPic = jsonObject.optString("userPic")
            }

            if (jsonObject.has("content")) {
                content = jsonObject.optString("content")
            }

            if (jsonObject.has("title")) {
                title = jsonObject.optString("title")
            }

            if (jsonObject.has("status")) {
                status = jsonObject.optInt("status")
            }

            if (jsonObject.has("orderId")) {
                orderId = jsonObject.optString("orderId")
            }

            if (jsonObject.has("tradeInfoId")) {
                tradeInfoId = jsonObject.optString("tradeInfoId")
            }

            if (jsonObject.has("time")) {
                time = jsonObject.optLong("time")
            }

            if (jsonObject.has("msgType")) {
                msgType = jsonObject.optInt("msgType")
            }

            if (jsonObject.has("orderNum")) {
                orderNum = jsonObject.optString("orderNum")
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
        val CREATOR: Parcelable.Creator<DealMessage> = object : Parcelable.Creator<DealMessage> {
            override fun newArray(size: Int): Array<DealMessage?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): DealMessage = DealMessage(source)

        }

    }

    override fun encode(): ByteArray {
        val jsonObject = JSONObject()
        try {
            if (!TextUtils.isEmpty(userCode)) {
                jsonObject.put("userCode", userCode)
            }

            if (!TextUtils.isEmpty(userName)) {
                jsonObject.put("userName", userName)
            }

            if (!TextUtils.isEmpty(userPic)) {
                jsonObject.put("userPic", userPic)
            }

            if (!TextUtils.isEmpty(content)) {
                jsonObject.put("content", content)
            }

            if (!TextUtils.isEmpty(title)) {
                jsonObject.put("title", title)
            }

            if (status != -1) {
                jsonObject.put("status", status)
            }

            if (!TextUtils.isEmpty(orderId)) {
                jsonObject.put("orderId", orderId)
            }

            if (!TextUtils.isEmpty(tradeInfoId)) {
                jsonObject.put("tradeInfoId", tradeInfoId)
            }

            if (time != 0L) {
                jsonObject.put("time", time)
            }

            if (msgType != -1) {
                jsonObject.put("mgsType", msgType)
            }

            if (!TextUtils.isEmpty(orderNum)) {
                jsonObject.put("orderNum", orderNum)
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