package com.gxtc.huchuan.ui.live.hostpage

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.PurchaseListBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.SpeakerRefundDialog
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.DealApi
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.DialogUtil
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_re_funds_detail.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

class ReFundsDetailActivity : BaseTitleActivity(), View.OnClickListener {

    var bean : PurchaseListBean? = null
    internal var id = -1
    internal var memberType = -1
    var map = HashMap<String,String>()
    var audit : String? = null  //1：同意；2：拒绝
    var mSpeakerRefundDialog :SpeakerRefundDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_funds_detail)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("订单详情").showBackButton(View.OnClickListener {
            finish()
        })
        bean = intent .getSerializableExtra("data") as PurchaseListBean
        id = intent.getIntExtra("id",-1)
        memberType = intent.getIntExtra("memberType",-1)
    }

    override fun initData() {
        super.initData()
        tv_money.text = bean!!.fee+"元"
        tv_case.text = String.format(Locale.CHINA, "%.2f", bean?.refundMoney)+"元"
        //0:交易退款，1，课堂退款，2，圈子退款，3，商城退款
        when(bean!!.type){
            0 -> tv_type.text = "交易退款"
            1 -> tv_type.text = "课堂退款"
            2 -> tv_type.text = "圈子退款"
            3 -> tv_type.text = "商城退款"
        }
        when(bean!!.isFinish){  //退款是否完成。 0：未完成，1：完成 2,拒绝
            0 -> {
                when(bean!!.audit){   //0:未审核，1：审核通过，2：审核不通过
                   0 -> {
                       tv_kuaisu.text = "审核"
                       tv_status.visibility = View.GONE
                       layout_refund?.setBackgroundColor(resources.getColor(R.color.white))
                       layout_refund?.isClickable = true
                   }
                   1 -> {
                       tv_status.text = "审核通过"
                       layout_refund?.setBackgroundColor(resources.getColor(R.color.greyd1d1d1))
                       layout_refund?.isClickable = false
                   }
                   2 -> {
                       tv_status.text = "审核不通过"
                       layout_refund?.setBackgroundColor(resources.getColor(R.color.greyd1d1d1))
                       layout_refund?.isClickable = false
                   }
                }
            }
            1 -> {
                tv_status.text = "您已同意对方的退款请求"
                tv_status.visibility = View.VISIBLE
                tv_kuaisu.text = "退款完成"
                layout_refund?.setBackgroundColor(resources.getColor(R.color.greyd1d1d1))
                layout_refund?.isClickable = false
            }
            2 -> {
                tv_status.text = "您已拒绝退款请求"
                tv_kuaisu.text = "退款已拒绝"
                layout_refund?.setBackgroundColor(resources.getColor(R.color.greyd1d1d1))
                layout_refund?.isClickable = false
            }
        }
        val time = DateUtil.stampToDate(bean!!.createTime.toString())
        tv_time.text = time
        tv_order.text = bean!!.orderId
        if(!TextUtils.isEmpty(bean!!.remark)){
            tv_remark.text = bean!!.remark
        }else{
            tv_remark.text = "无"
        }
        tv_name.text = bean!!.userName
        tv_order.text = bean!!.orderId
        tv_title.text = bean!!.title
    }

    override fun initListener() {
        super.initListener()
        layout_chat.setOnClickListener(this)
        layout_refund.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.layout_chat -> chat()
            R.id.layout_refund -> showRefundsDialog()
        }
    }

    fun showRefundsDialog(){
        if(mSpeakerRefundDialog == null)
        mSpeakerRefundDialog = SpeakerRefundDialog(this);
        mSpeakerRefundDialog?.show()
        mSpeakerRefundDialog?.setOnCompleteListener(View.OnClickListener { v ->
            when(v.id){
                R.id.tv_cancel -> {
                    if(id == -1){   //圈子ID没传过来就是-1 ，说明是课堂退款
                        audit = "2"
                        if(TextUtils.isEmpty(mSpeakerRefundDialog!!.resone)) ToastUtil.showShort(this@ReFundsDetailActivity,"请填写不同意的原因")//不同意原因必填，反之则不用
                        else refunds(audit!!)
                    }else{
                       // 圈子退款只有圈主可以
                        if(memberType == 2) {   //memberType;        //成员类型  0 普通成员  1 管理员  2 圈主
                            audit = "2"
                            if(TextUtils.isEmpty(mSpeakerRefundDialog!!.resone)) ToastUtil.showShort(this@ReFundsDetailActivity,"请填写不同意的原因")//不同意原因必填，反之则不用
                            else refunds(audit!!)
                        } else{
                            ToastUtil.showShort(this@ReFundsDetailActivity,"只有圈主可以审核退款")
                        }
                    }

                }
                R.id.tv_sure -> {
                    if(id == -1) {   //圈子ID没传过来就是-1 ，说明是课堂退款
                        audit = "1"
                        refunds(audit!!)
                    }else{
                        //圈子退款只有圈主可以
                        if(memberType == 2) {   //memberType;        //成员类型  0 普通成员  1 管理员  2 圈主
                            audit = "1"
                            refunds(audit!!)
                        } else{
                            ToastUtil.showShort(this@ReFundsDetailActivity,"只有圈主可以审核退款")
                        }
                    }
                }
            }
        })
    }

    fun chat() = RongIM.getInstance().startPrivateChat(this,bean!!.userCode,bean!!.userName)

    fun refunds(audit : String){
        map.put("token", UserManager.getInstance().token)
        map.put("orderId", bean!!.orderId)
        map.put("type", bean!!.type.toString())
        map.put("audit", audit)     //1：同意；2：拒绝
        if (!TextUtils.isEmpty(mSpeakerRefundDialog?.resone))
        map.put("remark", mSpeakerRefundDialog!!.resone)
        var sub = DealApi.getInstance()
                .sellerAgr(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>(){
                    override fun onSuccess(data: Any?) {
                        when (audit) {
                            "1" ->{
                                tv_status.text = "您已同意对方的退款请求"
                                tv_status.visibility = View.VISIBLE
                                layout_refund?.setBackgroundColor(resources.getColor(R.color.greyd1d1d1))
                            }

                            "2" -> {
                                tv_status.text = "您已拒绝对方的退款请求"
                                tv_status.visibility = View.VISIBLE
                                layout_refund?.setBackgroundColor(resources.getColor(R.color.white))
                            }
                        }

                        layout_refund?.isClickable = false
                        mSpeakerRefundDialog?.dismiss()
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@ReFundsDetailActivity, message!!)
                        mSpeakerRefundDialog?.dismiss()
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
