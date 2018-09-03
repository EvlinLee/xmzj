package com.gxtc.huchuan.ui.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MessageApi
import io.rong.imkit.RongIM
import kotlinx.android.synthetic.main.activity_confirm_join_group.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * 确认加入群聊
 */
class ConfirmJoinGroupActivity : BaseTitleActivity(){

    var bean : MessageBean ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_join_group)
    }

    override fun initView() {
        baseHeadView.showTitle("群聊邀请")
        baseHeadView.showBackButton {
            finish()
        }

        bean  = intent?.getSerializableExtra(Constant.INTENT_DATA) as? MessageBean
        ImageHelper.loadRound(this,img_head,bean?.groupPic,4)
        tv_name?.text = bean?.groupName
        val string = bean?.count.toString() + "人"
        tv_count?.text = string

        btn_submit?.setOnClickListener{
            joinGroup()
        }
    }

    //加入讨论组
    private fun joinGroup() {
        baseLoadingView.showLoading()

        val mapData = HashMap<String, String>()
        mapData.put("groupChatId", bean?.chatId!!)
        mapData.put("userCode", UserManager.getInstance().userCode)
        val sub = MessageApi.getInstance()
                .joinGroup(mapData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any) {
                        baseLoadingView?.hideLoading()
                        RongIM.getInstance().startGroupChat(this@ConfirmJoinGroupActivity,  bean?.chatId!!,  bean?.groupName!!)
                        finish()
                    }

                    override fun onError(errorCode: String, message: String) {
                        baseLoadingView?.hideLoading()
                        ToastUtil.showShort(this@ConfirmJoinGroupActivity, message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this);
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, bean: MessageBean){
            val intent = Intent(context,ConfirmJoinGroupActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA,bean)
            context.startActivity(intent)
        }
    }

}
