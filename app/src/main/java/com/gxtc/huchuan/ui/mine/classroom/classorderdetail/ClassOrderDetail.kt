package com.gxtc.huchuan.ui.mine.classroom.classorderdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.AllPurchaseListBean
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.RefundDialog
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.DealApi
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.StringUtil
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_class_order_detail.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ClassOrderDetail : BaseTitleActivity(),View.OnClickListener{

    var tvTypeNumber :TextView? = null;
    var tvMoney :TextView? = null;
    var tvType :TextView? = null;
    var tvTime :TextView? = null;
    var tvOdd :TextView? = null;
    var tvMark :TextView? = null;
    var tvUserName :TextView? = null;
    var tvAplyTime: TextView? = null
    var tvRedundsMoney: TextView? = null
    var btnChat: TextView? = null
    var tvSpeaker: TextView? = null
    var tvStatus :TextView? = null;
    var layoutChat :View? = null;
    var layoutRefundCash :View? = null;
    var layoutApplyStatus :View? = null;
    var bean : AllPurchaseListBean? = null;
    var mPurchaseCircleRecordBean : PurchaseCircleRecordBean? = null;
    var mType : Conversation.ConversationType? = null;
    var mRefundDialog:RefundDialog? = null
    var msg :String? = null
    var mouney :String? = null
    var orderId :String? = null
    var isSett :String? = null
    var textBtn :String? = null
    var fee :String? = null
    var sellerCode :String? = null
    var title :String? = null
    var isPay :String? = null
    var isRefund :String? = null //0、未申请退款，1：审核中，2：完成，3：被拒
    var type :Int = -1  //0:交易退款，1，	课堂退款，2，圈子退款，3，商城退款
    var map = hashMapOf<String,String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_order_detail)
    }



    override fun initView() {
        super.initView()
        baseHeadView.showBackButton( View.OnClickListener{
            finish()
        })
        baseHeadView.showTitle("订单详情")

        tvTypeNumber = findViewById(R.id.tv_account_water_details_typenumber) as TextView
        tvMoney = findViewById(R.id.tv_account_water_details_money) as TextView
        tvType = findViewById(R.id.tv_account_water_details_type) as TextView
        tvTime = findViewById(R.id.tv_account_water_details_time) as TextView
        tvOdd = findViewById(R.id.tv_account_water_details_odd) as TextView
        btnChat = findViewById(R.id.tv_chat) as TextView
        tvMark = findViewById(R.id.tv_account_water_details_mark) as TextView
        tvUserName = findViewById(R.id.tv_account_user_name) as TextView
        tvSpeaker = findViewById(R.id.tv_speaker) as TextView
        tvStatus= findViewById(R.id.tv_status) as TextView
        tvAplyTime= findViewById(R.id.tv_time) as TextView
        tvRedundsMoney= findViewById(R.id.tv_refunds_money) as TextView
        layoutChat = findViewById(R.id.layout_chat)
        layoutRefundCash = findViewById(R.id.layout_refund_cash)
        layoutApplyStatus = findViewById(R.id.lay_aplly_status)
    }

    override fun initListener() {
        super.initListener()
        layoutChat?.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        bean = intent?.getSerializableExtra("data") as AllPurchaseListBean
        if(bean?.type == 1 || bean?.type == 2){
            setClassData()
        }
        if(bean?.type == 3){
            setCircleData()
        }
    }

    //课堂订单详情
    private fun setClassData(){
        tvTypeNumber?.text = "实付金额"
        tvSpeaker?.text = "收款用户"
        textBtn = "联系讲师"
        btnChat?.text = textBtn
        tvMoney?.text = bean?.fee.toString() + "元"
        orderId = bean?.orderNo
        type = 1
        title = bean?.assistantTitle
        mType = Conversation.ConversationType.PRIVATE
        isSett = bean?.isSett .toString()
        isRefund = bean?.isRefund .toString()
        isPay = bean?.isPay.toString()
        sellerCode = bean?.userCode
        fee = bean?.fee .toString()
        when(bean?.type){
           1 ->   tvType?.text = "课堂"
           2 -> tvType?.text = "系列课"
        }
        checkIsRefunds()//是否可以申请退款
        tvTime?.text = DateUtil.stampToDate(bean?.createTime .toString())
        tvOdd?.text = bean?.orderNo
        tvMark?.text = bean?.title
        tvUserName?.text = bean?.assistantTitle
    }

    //圈子订单详情
    private fun setCircleData() {
        tvTypeNumber?.text = "实付金额"
        tvSpeaker?.text = "圈        主"
        textBtn = "联系圈主"
        btnChat?.text = textBtn
        tvMoney?.text = StringUtil.formatMoney(2, bean?.fee .toString())  + "元"
        orderId = bean?.orderNo
        type = 2
        isSett = bean?.isSett .toString()
        isPay = bean?.isPay.toString()
        fee = bean?.fee .toString()
        tvType?.text = "圈子"
        isRefund = bean?.isRefund .toString()
        sellerCode = bean?.userCode
        title = bean?.title
        mType = Conversation.ConversationType.PRIVATE
        checkIsRefunds()
        tvTime?.text = DateUtil.stampToDate(bean?.createTime .toString())
        tvOdd?.text = bean?.orderNo
        tvMark?.text = bean?.title
        tvUserName?.text = bean?.assistantTitle

    }

    var mAlertDialog : AlertDialog? = null
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.layout_chat -> {
                if(!TextUtils.isEmpty(sellerCode))
                  startPrivateChat(sellerCode!!,title!!,mType!!)}
        }
    }

    fun initFirst(){
        tvStatus?.visibility = View.GONE
        layoutApplyStatus?.visibility = View.GONE
        layoutRefundCash?.visibility = View.GONE
        refund_line?.visibility = View.GONE
        tvAplyTime?.visibility = View.GONE
    }

    fun checkIsRefunds(){
        initFirst()
        when(isSett){
            //未结算均可申请提款
            "0" -> checkRefundsStatus()
        }
    }

    //0、未申请退款，1：审核中，2：完成，3：被拒
    fun checkRefundsStatus(){
        if(isPay != "0"){
            when (isRefund) {
                "1" -> {
                    tvStatus?.visibility = View.VISIBLE
                    tvStatus?.setText("申请退款审核中...")
                    layoutApplyStatus?.visibility = View.VISIBLE
                }
                "2" -> {
                    tvStatus?.visibility = View.VISIBLE
                    tvStatus?.setText("申请通过，成功")
                    layoutApplyStatus?.visibility = View.VISIBLE
                }
                "3" -> {
                    tvStatus?.visibility = View.VISIBLE
                    tvStatus?.setText("申请不通过，被拒")
                    layoutApplyStatus?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startPrivateChat(userCode: String? , title: String?, type:Conversation.ConversationType?) {
        val uri = Uri.parse("rong://" + applicationInfo.packageName).buildUpon().appendPath(
                "conversation").appendPath(
                type?.getName()?.toLowerCase()).appendQueryParameter(
                "targetId", userCode).appendQueryParameter("title", title).build()
        startActivity(Intent("android.intent.action.VIEW", uri))
    }

    fun showRefundsDialog(){
        if(mRefundDialog == null)
        mRefundDialog = RefundDialog(this)
        mRefundDialog?.setNotice(fee)
        mRefundDialog?.show()
        mRefundDialog?.setOnCompleteListener { v ->
            when(v.id){

                R.id.tv_cancel -> mRefundDialog?.dismiss()

                R.id.tv_sure -> {
                    if(TextUtils.isEmpty(mRefundDialog?.case)){
                        ToastUtil.showShort(this@ClassOrderDetail,"请输入退款金额")
                    }else if (java.lang.Double.parseDouble(mRefundDialog?.case) > java.lang.Double.parseDouble(fee) ){
                        ToastUtil.showShort(this@ClassOrderDetail,"输入退款金额不能大于最大退款金额")
                    }else if (TextUtils.isEmpty(mRefundDialog?.resone)){
                        ToastUtil.showShort(this@ClassOrderDetail,"请输入退款原因")
                    }else{
                        mouney = mRefundDialog?.case
                        msg = mRefundDialog?.resone
                        aplayRefund()
                    }

                }
            }
        }

    }

    fun aplayRefund(){
        map.clear()
        map.put("token",UserManager.getInstance().token)
        map.put("orderId",orderId!!)
        map.put("type", type.toString())
        map.put("reason",msg!!)
        map.put("money",mouney!!)
        map.put("role","1")
        val sub = DealApi.getInstance()
                .refund(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>(){
                    override fun onSuccess(data: Any?) {
                        isRefund = "1"
                        ToastUtil.showShort(this@ClassOrderDetail, "申请退款成功")
                        setdata()
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@ClassOrderDetail, message!!)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }

    fun  cancelRefund(){
        map.clear()
        map.put("token",UserManager.getInstance().token)
        map.put("orderId",orderId!!)
        map.put("type", type.toString())
        map.put("role","1")
        map.put("cancel","1")
        val sub = DealApi.getInstance()
                .refund(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>(){
                    override fun onSuccess(data: Any?) {
                        ToastUtil.showShort(this@ClassOrderDetail, "取消退款成功")
                        isRefund = "0"
                        setNormaldata()
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@ClassOrderDetail, message!!)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }

    fun setNormaldata(){
        tvRedundsMoney?.visibility = View.GONE
        tvStatus?.visibility = View.GONE
        layoutApplyStatus?.visibility = View.GONE
        layoutRefundCash?.visibility = View.GONE
        refund_line?.visibility = View.GONE
        tvAplyTime?.visibility = View.GONE
    }

    fun setdata(){
        tvRedundsMoney?.visibility = View.VISIBLE
        tvStatus?.visibility = View.VISIBLE
        layoutApplyStatus?.visibility = View.VISIBLE
        layoutRefundCash?.visibility = View.VISIBLE
        refund_line?.visibility = View.VISIBLE
        tvAplyTime?.visibility = View.VISIBLE
        tvRedundsMoney?.setText(mRefundDialog?.case+"元")
        tvStatus?.setText("申请退款成功.请耐心等待")
        tvAplyTime?.setText(DateUtil.getCurTime("yyyy-MM-dd HH:mm:ss"))
        mRefundDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
