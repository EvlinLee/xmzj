package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Switch
import com.flyco.dialog.listener.OnOperItemClickL
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.ChatJoinBean.*
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.widget.MyActionSheetDialog
import com.ta.utdid2.android.utils.TimeUtils
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.HashMap

/**
 * Created by zzg on 2017/12/12.
 */
class SeriesSignCountAdater(context:Context, data:ArrayList<ChatJoinBean.MemberBean>, res: Int , myBean: HashMap<String, String>, recyclerView : RecyclerView):BaseRecyclerAdapter<ChatJoinBean.MemberBean>(context,data,res) {
    private val myBean: HashMap<String, String>? = myBean
    private var mRecyclerView : RecyclerView ?= recyclerView
    override fun bindData(holder: ViewHolder?, position: Int, bean: ChatJoinBean.MemberBean?) {
        holder?.setText(R.id.tv_name, bean?.name)
        ImageHelper.loadCircle(context, holder?.getImageView(R.id.iv_head),bean?.headPic);
        var role = "";
        when (bean?.joinType) {
            ROLE_ORDINARY -> {
                role = "普通成员"
            }
            ROLE_MANAGER -> {
                role = "管理员"
            }
            ROLE_TEACHER -> {
                role = "讲师"
            }
            ROLE_HOST -> {
                role = "主持人"
            }
        }
        //隐藏管理按钮
        if (myBean!!["joinType"] .equals( ChatJoinBean.ROLE_TEACHER) || myBean["joinType"]  .equals(ChatJoinBean.ROLE_ORDINARY) || UserManager.getInstance().userCode .equals( bean?.userCode) ||
                myBean["joinType"] .equals(ChatJoinBean.ROLE_MANAGER) && (bean?.joinType.equals(  ChatJoinBean.ROLE_HOST) || bean?.joinType .equals(ChatJoinBean.ROLE_MANAGER))) {
            holder?.getViewV2<View>(R.id.tv_manager)?.visibility = View.GONE
        } else {
            holder?.getViewV2<View>(R.id.tv_manager)?.setOnClickListener { ShowBottomMenu(bean!!, position) }
        }

        holder?.setText(R.id.tv_role, role)


    }

    fun ShowBottomMenu(bean: ChatJoinBean.MemberBean, mPosition: Int) {
        if (bean?.userCode == UserManager.getInstance().userCode) return
        val itemList = java.util.ArrayList<String>()

        if (bean?.joinType != ChatJoinBean.ROLE_MANAGER && myBean!!["joinType"] == ROLE_HOST) {
            itemList.add("升级为管理员")
        } else if(bean?.joinType == ChatJoinBean.ROLE_HOST && myBean!!["joinType"] == ROLE_HOST){
            itemList.add("取消管理员")
        }
        if (bean?.joinType != ChatJoinBean.ROLE_TEACHER) {
            itemList.add("升级为讲师")
        } else {
            itemList.add("取消讲师")
        }
        if (bean.isProhibitSpeaking) {
            itemList.add("解禁")
        } else {
            itemList.add("禁言")
        }
        if (bean.isBlacklist)
            itemList.add("取消黑名单")
        else
            itemList.add("加入黑名单")

        val s = arrayOfNulls<String>(itemList.size)

        if (itemList.size == 0) return
        val dialog = MyActionSheetDialog(context,
                itemList.toTypedArray(), null)
        dialog.isTitleShow(false).titleTextSize_SP(14.5f).widthScale(1f).cancelMarginBottom(
                0).cornerRadius(0f).dividerHeight(1f).itemTextColor(
                context.getResources().getColor(R.color.black)).cancelText("取消").show()

        dialog.setOnOperItemClickL(OnOperItemClickL { parent, view, position, id ->
            try {
                when (itemList[position]) {
                    "升级为管理员" -> {
                        updateJoin(bean?.userCode, "1", "升级管理员成功", mPosition)
                    }

                    "取消管理员" -> {
                        updateJoin(bean?.userCode, "0", "取消管理员成功", mPosition)
                    }

                    "升级为讲师" -> {
                        updateJoin(bean?.userCode, "2", "升级讲师成功", mPosition)
                    }

                    "取消讲师" -> {
                        updateJoin(bean?.userCode, "0", "取消讲师成功", mPosition)
                    }

                    "禁言" -> {
                        doJoinMemberBlacklistOrProhibitSpeaking(bean,"2", "1", mPosition,"已禁言");
                    }

                    "解禁" -> {
                        doJoinMemberBlacklistOrProhibitSpeaking(bean,"2","0", mPosition,"已解禁");
                    }

                    "加入黑名单" -> {
                        doJoinMemberBlacklistOrProhibitSpeaking(bean,"1","1", mPosition,"已加入黑名单");
                    }

                    "取消黑名单" -> {
                        doJoinMemberBlacklistOrProhibitSpeaking(bean,"1","0", mPosition,"已取消黑名单");
                    }
                }//                            collectMessage(message);
                //                            collectMessage(message);
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    //修改成员身份
    private fun updateJoin(userCode: String, userType: String, msg: String, postion: Int) {
        val map = HashMap<String, String>()
        map["chatId"] = myBean?.get("chatId").toString()
        map["type"] = myBean?.get("type").toString()
        map["userCode"] = userCode
        map["token"] = UserManager.getInstance().token
        map["userType"] = userType

        LiveApi.getInstance().updateJoinMemberJoinType(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any) {
                        list[postion].joinType = userType
                        mRecyclerView?.notifyItemChanged(postion)
                        ToastUtil.showShort(context, msg)
                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(context, message)
                    }
                }))
    }

    /**
     *
     * 拉黑/解除拉黑操作  禁言/解除禁言
     * chatId 课程id
     * chatType 1课程 2系列课
     * userCode 目标用户新媒号
     * token 当前操作人token
     * type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
     * state 0解除 1拉黑/禁言
     */
    fun doJoinMemberBlacklistOrProhibitSpeaking(bean: ChatJoinBean.MemberBean, type: String, state: String, position: Int, msg : String) {
        val map = HashMap<String, String>()
        map["chatId"] = myBean?.get("chatId").toString()
        map["chatType"] = myBean?.get("type").toString()
        map["userCode"] = bean?.userCode
        map["token"] = UserManager.getInstance().token
        map["type"] = type
        map["state"] = state
        val sub = LiveApi.getInstance().doJoinMemberBlacklistOrProhibitSpeaking(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any) {
                        if (type == "1") { //黑名单
                            bean.setBlacklist(if (bean.isBlacklist) "0" else "1")

                        } else { //禁言
                            bean.setProhibitSpeaking(if (bean.isProhibitSpeaking) "0" else "1")
                        }
                        ToastUtil.showShort(context, msg)

                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(context, message)
                    }
                }))
        RxTaskHelper.getInstance().addTask("SeriesSignCountAdater",sub)
    }

}