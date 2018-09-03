package com.gxtc.huchuan.ui.mine.account

import android.os.Bundle
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CheckBean
import com.gxtc.huchuan.bean.WithdrawRecordBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.AllApi
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.activity_withdraw_detailed.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class WithdrawDetailedActivity : BaseTitleActivity() {

    var bean : WithdrawRecordBean ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_detailed)
    }

    override fun initView() {
        baseHeadView.showTitle("提现明细")
        baseHeadView.showBackButton { finish() }
    }

    override fun initData() {
        fillData()
    }

    fun getData(){
        baseLoadingView?.showLoading()
        val token = UserManager.getInstance().token

        val sub =
            AllApi.getInstance()
                    .getInfo(token,bean?.id,"3")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<CheckBean>>(object :ApiCallBack<Any>(){
                        override fun onSuccess(data: Any?) {
                            baseLoadingView?.hideLoading()
                            val checkeBean = data as? CheckBean
                            tv_des?.text = checkeBean?.content
                        }

                        override fun onError(errorCode: String?, message: String?) {
                            baseLoadingView?.hideLoading()
                            ToastUtil.showShort(this@WithdrawDetailedActivity,message)
                        }
                    }))

        RxTaskHelper.getInstance().addTask(this,sub)
    }

    fun fillData(){
        bean = intent?.getSerializableExtra(Constant.INTENT_DATA) as? WithdrawRecordBean
        tv_money?.text = StringUtil.formatMoney(2,bean?.money!!)
        tv_time?.text = DateUtil.stampToDate(bean?.createTime)

        when(bean?.withdrawStatus){
            0->{
                tv_status?.setTextColor(this.resources.getColor(R.color.tool_bar_bg))
                tv_status?.text = "审核中"
                tv_des?.text = "系统正在审核您的提现申请，请耐心等候..."
            }
            1->{
                tv_status?.setTextColor(this.resources.getColor(R.color.tool_bar_bg))
                tv_status?.text = "审核通过"
                tv_des?.text = "您的提现申请已通过审核，请等候资金到账"
            }
            2->{
                tv_status?.setTextColor(this.resources.getColor(R.color.pay_failure))
                tv_status?.text = "审核拒绝"
                getData()
            }
            3->{
                tv_status?.setTextColor(this.resources.getColor(R.color.pay_finish))
                tv_status?.text = "完成"
                tv_des?.text = "您的提现资金已经到账"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
