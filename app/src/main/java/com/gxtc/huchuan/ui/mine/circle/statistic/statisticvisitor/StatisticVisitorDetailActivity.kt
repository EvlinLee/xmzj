package com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.SattisticVisitorDetailAdater
import com.gxtc.huchuan.bean.VisitorBean
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.activity_circle_statistic.*
/**
 * Created by zzg on 2018/3/16.
 */
class StatisticVisitorDetailActivity : BaseTitleActivity(), StatisticVisitorContract.View  {

    var mPresenter:StatisticVisitorContract.Present? = null
    var mAlertDialog: AlertDialog? = null
    var mAdapter:SattisticVisitorDetailAdater? = null
    var start = 0
    var groupId = 0
    var checkType = 0
    var timeSection = ""
    var headTitle = ""
    var titles:TextView? = null
    var income:TextView? = null

    companion object {
        @JvmStatic
        fun jumpToStatisticVisitorDetailActivity(activity: Activity,groupId:Int,checkType:Int?,timeSection:String?){
            val intent = Intent(activity, StatisticVisitorDetailActivity::class.java)
            intent.putExtra("timeSection",timeSection)
            intent.putExtra("checkType",checkType)
            intent.putExtra("groupId",groupId)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_statistic)
    }

    override fun initView() {
        super.initView()
        groupId = intent.getIntExtra("groupId",0)
        checkType = intent.getIntExtra("checkType",0)
        timeSection = intent.getStringExtra("timeSection")
        if( checkType == StatisticVisitorActivity.CIRCLE_OF_HOME){
            headTitle = "主页访客"
        }
        if( checkType == StatisticVisitorActivity.CIRCLE_OF_INTRUDUCE){
            headTitle = "介绍页访客"
        }
        baseHeadView.showBackButton{
            finish()
        }.showTitle(headTitle)
        refreshlayout.setColorSchemeResources(*Constant.REFRESH_COLOR)
        recyclerView.setLoadMoreView(R.layout.model_footview_loadmore)
        mAdapter = SattisticVisitorDetailAdater(this, mutableListOf(), R.layout.statistic_viditor_detail_item_layout)
        recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        recyclerView.setAdapter(mAdapter)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener {
            start = 0
            recyclerView.reLoadFinish()
            mPresenter?.getDetailData(groupId,start,checkType,timeSection)
        }
        recyclerView.setOnLoadMoreListener {
            start = start + 15
            mPresenter?.getDetailData(groupId,start,checkType,timeSection)
        }

        mAdapter?.setOnReItemOnClickListener { v, position ->
            val bean = mAdapter?.list?.get(position)
            PersonalInfoActivity.startActivity(this@StatisticVisitorDetailActivity,bean?.userCode)
        }
    }

    override fun initData() {
        super.initData()
        StatisticVisitorPresenter(this)
        mPresenter?.getDetailData(groupId,start,checkType,timeSection)
    }

    override fun showData(datas: MutableList<VisitorBean>) {
        if(start == 0){
            refreshlayout.isRefreshing = false
            recyclerView.notifyChangeData(datas,mAdapter)
        }else{
            recyclerView.changeData(datas,mAdapter)
        }
    }

    override fun showRefreshFinish(datas: MutableList<VisitorBean>) {}

    override fun showLoadMore(datas: MutableList<VisitorBean>) {}

    override fun showNoMore() {}

    override fun setPresenter(presenter: StatisticVisitorContract.Present?) {
        mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
       if(start == 0){
           baseEmptyView.baseEmptyView
       }else{
           recyclerView.loadFinish()
       }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
       ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun showNetError() {}



    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }
}
