package com.gxtc.huchuan.ui.mine.circle.statistic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.StatisticIncomeDetailAdater
import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.activity_days_income.*

/**
 * Created by zzg on 2018/3/16
 */
class StatisticPentDetailActivity : BaseTitleActivity(), StatisticIncomeDetailContract.View {

    var adater : StatisticIncomeDetailAdater? = null
    var mPresenter:StatisticIncomeDetailContract.Presenter? = null
    var start = 0
    var groupId = 0
    var count = 0
    var incomes :Double? = null
    var timeSection :String? = null
    var dateName :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days_income)
    }

    override fun initView() {
        super.initView()
        groupId = intent.getIntExtra("groupId",0)
        count = intent.getIntExtra("count",0)
        incomes = intent.getDoubleExtra("income",0.0)
        timeSection = intent.getStringExtra("timeSection")
        dateName = intent.getStringExtra("dateName")
        baseHeadView.showTitle("圈子佣金明细").showBackButton(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
        refreshlayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        val headView = layoutInflater.inflate(R.layout.days_income_header,null)
        val title = headView.findViewById<TextView>(R.id.income_note)
        val income = headView.findViewById<TextView>(R.id.income)
        val incomeCount = headView.findViewById<TextView>(R.id.income_count)
        title.text = dateName+" 佣金"
        incomeCount.text = "共计"+count+"笔"
        income.text = incomes?.toString() + "元"
        recyclerview.layoutManager = LinearLayoutManager(this)!!
        recyclerview.addHeadView(headView)
        adater = StatisticIncomeDetailAdater(this, mutableListOf(), R.layout.statistic_income_detail_item)
        recyclerview.adapter = adater
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener {
            start = 0
            recyclerview.reLoadFinish()
            mPresenter?.getStatisticIncomeDetailData(start,groupId,timeSection)
        }
        recyclerview.setOnLoadMoreListener {
            start = start + 15
            mPresenter?.getStatisticIncomeDetailData(start,groupId,timeSection)
        }

        adater?.setOnReItemOnClickListener { v, position ->
            PersonalInfoActivity.startActivity(this,adater?.list?.get(position)?.userCode)
        }
    }

    override fun initData() {
        super.initData()
        StatisticIncomeDetailPresenter(this)
        mPresenter?.getStatisticIncomeDetailData(start,groupId,timeSection)
    }

    override fun showData(data: MutableList<StatisticDetailBean>) {
        if(start == 0){
            refreshlayout.isRefreshing = false
            recyclerview.notifyChangeData(data,adater)
        }else{
            recyclerview.changeData(data,adater)
        }
    }

    override fun setPresenter(presenter: StatisticIncomeDetailContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
        if(start == 0){
            refreshlayout.isRefreshing = false
            baseEmptyView.baseEmptyView
        }else{
            recyclerview.loadFinish()
        }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    override fun showNetError() {}

    companion object {
        fun jumpToStatisticPentDetailActivity(activity: Activity, groupId: Int, timeSection: String?, count: Int?, income: Double?, dateName: String){
            val intent = Intent(activity,StatisticPentDetailActivity::class.java)
            intent.putExtra("groupId",groupId)
            intent.putExtra("timeSection",timeSection)
            intent.putExtra("count",count)
            intent.putExtra("income",income)
            intent.putExtra("dateName",dateName)
            activity.startActivity(intent)
        }
    }
}
