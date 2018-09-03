package com.gxtc.huchuan.ui.live.hostpage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.ui.mine.circle.applyfor.ApplyForMemberListFragment
import kotlinx.android.synthetic.main.activity_apply_for_meamber_and_refund_list.*

class RefundsAndCheckMemberActivity : BaseTitleActivity(), View.OnClickListener {

    companion object {
        fun startActivity(context: Context, bean: CircleBean){
            val intent = Intent(context,RefundsAndCheckMemberActivity::class.java)
            intent.putExtra("bean",bean)
            context.startActivity(intent)
        }
    }

    //成员审核和圈子退款列表
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_for_meamber_and_refund_list)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("审核").showBackButton { finish() }
    }

    override fun initListener() {
        super.initListener()
        meanber.setOnClickListener(this)
        refunds.setOnClickListener(this)
    }

    private var mRefundListFragment: RefundListFragment? = null
    private var mApplyForMemberListFragment: ApplyForMemberListFragment? = null

    override fun initData() {
        val bean = intent.getSerializableExtra("bean") as CircleBean
        val groupId = bean.id
        mRefundListFragment = RefundListFragment()  //退款
        val mBundle = Bundle()
        mBundle.putString("type", "2")
        mBundle.putInt("groupId", groupId)
        mBundle.putInt("memberType", bean.memberType)
        mRefundListFragment?.arguments = mBundle
        meanber.setTextColor(resources.getColor(R.color.blue))
        mApplyForMemberListFragment = ApplyForMemberListFragment.getInstance(groupId)  //成员

        if(bean.jumpPage != -1){
            switchFragment(mRefundListFragment, ApplyForMemberListFragment::class.java.simpleName, R.id.content)
        }else{
            switchFragment(mApplyForMemberListFragment, RefundListFragment::class.java.simpleName, R.id.content)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.meanber -> {
                setTextColor()
                meanber.setTextColor(resources.getColor(R.color.blue))
                switchFragment(mApplyForMemberListFragment, RefundListFragment::class.java.simpleName, R.id.content)
            }

            R.id.refunds -> {
                setTextColor()
                refunds.setTextColor(resources.getColor(R.color.blue))
                switchFragment(mRefundListFragment, ApplyForMemberListFragment::class.java.simpleName, R.id.content)
            }
        }
    }

    fun setTextColor() {
        meanber.setTextColor(resources.getColor(R.color.text_color_333))
        refunds.setTextColor(resources.getColor(R.color.text_color_333))
    }
}
