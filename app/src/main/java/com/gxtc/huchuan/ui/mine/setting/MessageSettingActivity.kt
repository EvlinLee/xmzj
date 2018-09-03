package com.gxtc.huchuan.ui.mine.setting

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.dao.User
import com.gxtc.huchuan.bean.event.EventUnReadBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MineApi
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.utils.*
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_message_setting.*
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.HashMap

class MessageSettingActivity : BaseTitleActivity() {

    var mSwitchCompatNewMessage : SwitchCompat? = null
    var mSwitchCompat : SwitchCompat? = null
    var mAlertDialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_setting)
    }

    companion object{
        //@JvmStatic
        //val  SP_NEW_MESSAGE :String = "new_message_sound" + UserManager.getInstance().userCode
        val  MAXNUM :Int = 1439
        var  newMessageSound  : Boolean = true

        @JvmStatic
        fun SP_NEW_MESSAGE(): String = "new_message_sound" + UserManager.getInstance().userCode
    }

    override fun initView() {
        mSwitchCompatNewMessage = findViewById(R.id.switch_sound_notice)  as SwitchCompat
        mSwitchCompat = findViewById(R.id.switch_sound)  as SwitchCompat
    }

    override fun initListener() {
        baseHeadView.showTitle(getString(R.string.title_message_setting))
        baseHeadView.showBackButton { finish() }

        mSwitchCompatNewMessage?.setOnClickListener { v ->
            var mCheckBox = v as  SwitchCompat
            newMessageSound = mCheckBox.isChecked
            SpUtil.putBoolean(applicationContext, SP_NEW_MESSAGE(), newMessageSound)
            JPushUtil.setSoundAndVibrate(this@MessageSettingActivity, newMessageSound, !newMessageSound)
        }

        mSwitchCompat?.setOnClickListener { v ->
            setIsDisturble(v!!)
        }

        //仅好友发信息
        switch_messsge_status.setOnClickListener { v ->
             var mCheckBox = v as SwitchCompat
            if (mCheckBox.isChecked) {
                setFriend("0", "1")
            } else {
                setFriend("0", "0")
            }
        }
        tv_clear_conversation_record.setOnClickListener{
            if (UserManager.getInstance().isLogin) {
                mAlertDialog = DialogUtil.showDeportDialog(this, false, null, "确定清除聊天记录") { v ->
                    if (v.id == R.id.tv_dialog_confirm) {
                        clearConversationRecored()
                    }
                    mAlertDialog?.dismiss()
                }
            } else {
                GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity::class.java,
                        Constant.requestCode.NEWS_AUTHOR)
            }
        }
    }

   /**清除会话列表里的所有聊天记录，会话列表还留着**/
    private fun clearConversationRecored() {
        val conversations = RongIM.getInstance().rongIMClient.conversationList
        Observable.from(conversations!!)
              .flatMap { conversations ->
                    Observable.just(conversations) }
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(object : Observer<Conversation> {
                    override fun onCompleted() {
                        ToastUtil.showShort(MyApplication.getInstance().applicationContext, "清除成功")
                        EventBusUtil.post(EventUnReadBean())
                    }

                    override fun onError(e: Throwable) {
                        ToastUtil.showShort(MyApplication.getInstance().applicationContext, "清除失败")
                    }

                    override fun onNext(conversation: Conversation) {
                        RongIM.getInstance().rongIMClient.clearMessages(
                                conversation.conversationType, conversation.targetId,
                                object : RongIMClient.ResultCallback<Boolean>() {
                                    override fun onSuccess(aBoolean: Boolean?) = Unit

                                    override fun onError(errorCode: RongIMClient.ErrorCode) =
                                            ToastUtil.showShort(MyApplication.getInstance().applicationContext, "清除失败")
                                })
                    }
                })
    }

    override fun initData() {
        var flag = intent.getIntExtra("flag",-1)
        when(flag){
            1 ->{
                line.visibility = View.VISIBLE
                line2.visibility = View.VISIBLE
                set_meaber_massage_layou.visibility = View.VISIBLE
                tv_clear_conversation_record.visibility = View.VISIBLE
            }
            else ->{
                line.visibility = View.GONE
                line2.visibility = View.GONE
                set_meaber_massage_layou.visibility = View.GONE
                tv_clear_conversation_record.visibility = View.GONE
            }
        }
        SettingActivity.sound = SpUtil.getBoolean(this, SettingActivity.SP_SOUND(), true)!!
        mSwitchCompat?.isChecked = SettingActivity.sound

        newMessageSound = SpUtil.getBoolean(this, SP_NEW_MESSAGE(), true)!!
        mSwitchCompatNewMessage?.isChecked = newMessageSound

        getData()
    }

    private fun getData(){
        val token = UserManager.getInstance().token
        val sub = MineApi.getInstance()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<User>>(object : ApiCallBack<User>(){

                    override fun onSuccess(data: User?) {
                        switch_messsge_status?.isChecked = "1" == data?.friendChat
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@MessageSettingActivity,message)
                    }
                }))

        RxTaskHelper.getInstance().addTask(this,sub)
    }

    private fun setFriend(type: String, value: String) {
        val map = HashMap<String, String>()
        map.put("token", UserManager.getInstance().token)
        map.put("type", type)           //0、仅好友可聊天；1、仅好友可评论
        map.put("value", value)         //0、全部可操作；1、仅好友可操作
        val sub = MineApi.getInstance().setFriend(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any) {}

                    override fun onError(errorCode: String, message: String) {
                        LoginErrorCodeUtil.showHaveTokenError(this@MessageSettingActivity, errorCode, message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }

    fun setIsDisturble(v:View){
        val mCheckBox = v as  SwitchCompat
        SettingActivity.sound = mCheckBox.isChecked       //会话消息声音
        RIMSoundHandler.setRongIMSounds(this,SettingActivity.sound)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
