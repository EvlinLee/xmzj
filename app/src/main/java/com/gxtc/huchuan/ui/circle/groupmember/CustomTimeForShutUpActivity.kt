package com.gxtc.huchuan.ui.circle.groupmember

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bigkoo.pickerview.TimePickerView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.CircleApi
import com.gxtc.huchuan.http.service.MallApi
import com.gxtc.huchuan.utils.DateUtil
import kotlinx.android.synthetic.main.activity_custom_time_for_shut_up.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.E

/**
 * Created by zzg on 2018/1/30  定时禁言
 */

class CustomTimeForShutUpActivity : BaseTitleActivity(),View.OnClickListener,TimePickerView.OnTimeSelectListener{
    var timePickerView : TimePickerView? = null
    var chooseType:Int? = -1
    var startTime: Long = -1
    var endTime: Long = -1
    var groupChatId:String? = null
    var groupId:Int? = null

    companion object {
      @JvmStatic
      fun jumpToCustomTimeForShutUpActivity(context: Activity,groupChatId:String,groupId:Int) {
          val intent = Intent(context,CustomTimeForShutUpActivity::class.java)
          intent.putExtra("groupChatId",groupChatId)
          intent.putExtra("groupId",groupId)
          context.startActivityForResult(intent,10005)
      }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_time_for_shut_up)
    }

    override fun initView() {
        super.initView()
        groupChatId = intent.getStringExtra("groupChatId")
        groupId = intent.getIntExtra("groupId",0)
        baseHeadView.showTitle("定时禁言").showBackButton {
            finish()
        }.showHeadRightButton("确定",object : View.OnClickListener{
            override fun onClick(p0: View?) {
                setGroupTaskInTiming()
            }

        })
    }

    override fun initListener() {
        super.initListener()
        tv_start_time.setOnClickListener (this)
        tv_end_time.setOnClickListener (this)
    }

    fun setGroupTaskInTiming(){
        if(TextUtils.isEmpty(tv_start_time.text.toString().trim())){
            ToastUtil.showShort(this, "开始时间不能为空")
            return
        }

        if(TextUtils.isEmpty(tv_end_time.text.toString().trim())){
            ToastUtil.showShort(this, "结束时间不能为空")
            return
        }

        if(endTime!!.compareTo(startTime) < 0){
            ToastUtil.showShort(this, "结束时间不能小于结束时间")
            return
        }
        baseLoadingView.showLoading()
        val map = hashMapOf<String,String>()
        map.put("type","1") // 1 聊天室  2 圈子动态
        map.put("clickType","1") //0解除  1设置
        map.put("startTimeL",startTime.toString())
        map.put("endTimeL", endTime.toString())
        map.put("groupChatId",groupChatId!!)
        map.put("groupId",groupId.toString())
        map.put("token",UserManager.getInstance().token)
        val sub = CircleApi.getInstance().setGroupTaskInTiming(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object :ApiCallBack<Any>(){
                    override fun onSuccess(data: Any?) {
                        baseLoadingView.hideLoading()
                        ToastUtil.showShort(MyApplication.getInstance(),"定时禁言成功")
                        val intent = Intent()
                        intent.putExtra("startTimeL",startTime.toString())
                        intent.putExtra("endTimeL",endTime.toString())
                        setResult(Activity.RESULT_OK,intent)
                        this@CustomTimeForShutUpActivity.finish()
                    }

                    override fun onError(errorCode: String?, message: String?) {
                       ToastUtil.showShort(MyApplication.getInstance(),message)
                    }

                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }



    override fun onClick(p0: View?) {
        chooseType = p0?.id
        showTimePop()
    }

    override fun onTimeSelect(date: Date?, v: View?) {
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA) //yyyy.MM.dd.HH.mm.ss
        val time = sdf.format(date)

        if(date?.time!!.compareTo(System.currentTimeMillis()) < 0){
            ToastUtil.showShort(this, "设置的时间必须是未来时间")
            return
        }

        when(chooseType){
            R.id.tv_start_time -> {
                startTime = date.time
                tv_start_time.setText(time)
            }
            R.id.tv_end_time -> {
                endTime = date.time
                tv_end_time.setText(time)
            }
        }
    }

    //选择日期
    private fun showTimePop() {
        val builder = TimePickerView.Builder(this, this).setType(
                    TimePickerView.Type.ALL).setDate(Date()).setOutSideCancelable(true)
        timePickerView = TimePickerView(builder)
        timePickerView?.findViewById(R.id.second)?.visibility = View.GONE //隐藏掉秒的选择
        timePickerView?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
