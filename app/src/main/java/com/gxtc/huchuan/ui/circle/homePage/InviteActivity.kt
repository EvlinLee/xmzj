package com.gxtc.huchuan.ui.circle.homePage

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean

/**
 * Created by zzg on 2017/8/7.
 */
class InviteActivity : BaseTitleActivity(), View.OnClickListener {

    var tvInviteAdmin: TextView? = null
    var tvInviteMember: TextView? = null
    var bean: CircleBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.invite_layout);
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("邀请").showBackButton {
            finish()
        }
        tvInviteAdmin = findViewById(R.id.tv_invite_admintor) as TextView
        tvInviteMember = findViewById(R.id.tv_invite_member) as TextView
    }

    override fun initListener() {
        super.initListener()
        tvInviteAdmin?.setOnClickListener(this)
        tvInviteMember?.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        bean = intent.getSerializableExtra("CircleBean") as CircleBean
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_invite_admintor -> {
                jumpToCircleInviteActivity(1, 1)
            }

            R.id.tv_invite_member -> {
                jumpToCircleInviteActivity(0, 0)
            }
        }

    }

    fun jumpToCircleInviteActivity(type: Int, inviteType: Int) {
        var url = bean?.getShareUrl() + inviteType
        var intent = Intent(this, CircleInviteActivity::class.java)
        intent.putExtra("id", bean?.id?.toString())
        intent.putExtra("share_img_url", bean?.cover)
        if (!TextUtils.isEmpty(bean?.groupName))
            intent.putExtra("share_title", bean?.groupName)
        else
            intent.putExtra("share_title", bean?.name)
        intent.putExtra("share_url", url)
        intent.putExtra("memberType", type.toString())
        intent.putExtra("money", bean?.brokerage)
        startActivity(intent)
    }
}