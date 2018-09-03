package com.gxtc.huchuan.ui.mine.circle

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.CircleApi
import kotlinx.android.synthetic.main.activity_circle_audit.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CircleAuditActivity : BaseTitleActivity() {

    var bean:CircleBean? = null
    var mType:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_audit)
    }

    override fun initView() {
        super.initView()
        bean = intent.getSerializableExtra("data")  as CircleBean
        baseHeadView.showBackButton {
            finish()
        }.showHeadRightButton("保存",object :View.OnClickListener{
            override fun onClick(p0: View?) {
                isAudit(mType)
            }

        })
        baseHeadView.showTitle("成员设置")
    }

    override fun initData() {
        super.initData()
        if ("0" == bean?.getIsAudit()) { //0、不需要
            mType = 0
            et_input.visibility = View.GONE
            cbIsAudit.isChecked = false
        } else if ("1" == bean?.getIsAudit()) {
            mType = 1
            et_input.visibility = View.VISIBLE
            cbIsAudit.isChecked = true
        }
    }

    override fun initListener() {
        super.initListener()
        cbIsAudit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(buttonView: View?) {
                val checkBox = buttonView as CheckBox
                if (checkBox.isChecked) {
                    mType = 1
                    et_input.visibility = View.VISIBLE
                } else {
                    mType = 0
                    et_input.visibility = View.GONE
                }
            }
        })
    }

    /**
     * 加入是否需要审核
     *
     * @param type 0、不审核；1、审核
     */
    private fun isAudit(type: Int) {
        var prompt = et_input.text.toString()
        if(TextUtils.isEmpty(prompt)){
           prompt = "输入验证信息，管理员审核通过后方可加入"
        }
        if (UserManager.getInstance().isLogin) {
            val subAudit = CircleApi.getInstance().isAuditMember(UserManager.getInstance().token, bean?.getId(), type,prompt)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ApiObserver(object : ApiCallBack<Any>() {
                        override fun onSuccess(data: Any) {
                            if (1 == type) {
                                bean?.setIsAudit("1")
                            } else if (0 == type) {
                                bean?.setIsAudit("0")
                            }
                            ToastUtil.showShort(MyApplication.getInstance(),"设置成功")
                            val intent = Intent()
                            intent.putExtra("isAudit",bean?.isAudit)
                            setResult(Activity.RESULT_OK,intent)
                            finish()
                        }

                        override fun onError(errorCode: String, message: String) {
                            ToastUtil.showShort(MyApplication.getInstance(),
                                    message)
                        }
                    }))
            RxTaskHelper.getInstance().addTask(this,subAudit)
        } else {
            val intent = Intent(this, CircleManagerActivity::class.java)
            intent.putExtra("circleId", bean?.getId())
            intent.putExtra("isMy", bean?.getIsMy())
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }

}
