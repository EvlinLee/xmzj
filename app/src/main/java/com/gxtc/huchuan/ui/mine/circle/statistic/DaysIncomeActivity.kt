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
import com.gxtc.huchuan.adapter.DaysInComeInfoAdater
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.data.UserManager
import kotlinx.android.synthetic.main.activity_days_income.*

/**
 * Created by zzg on 2018/3/16
 */
class DaysIncomeActivity : BaseTitleActivity(), StatisticContract.View {

    var type = 0
    var titles = ""
    var start = 0
    var groupId = 0
    var dateType = 0
    var action = 0
    var realIncomeCount = 0
    var realIncome = 0.0
    var circleName: String ?= null
    var title:TextView? = null
    var income:TextView? = null
    var incomeCount:TextView? = null
    var adater :DaysInComeInfoAdater? = null
    var mPresenter: StatisticContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days_income)
    }

    override fun initView() {
        super.initView()
        refreshlayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
        type = intent.getIntExtra("type",0)
        groupId = intent.getIntExtra("groupId",0)
        action = intent.getIntExtra("action",0)
        circleName = intent.getStringExtra("circleName")
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val headView = layoutInflater.inflate(R.layout.days_income_header,null)
        title = headView.findViewById<TextView>(R.id.income_note)
        income = headView.findViewById<TextView>(R.id.income)
        incomeCount = headView.findViewById<TextView>(R.id.income_count)
        realIncomeCount = intent.getIntExtra("realIncomeCount",0)
        realIncome = intent.getDoubleExtra("realIncome",0.0)
        recyclerview.addHeadView(headView)
        when(type){
            INCOME_WITH_DAYS ->{
                 dateType = INCOME_WITH_DAYS
                 titles = circleName+"日收入"
            }
            INCOME_WITH_WEEK ->{
                 dateType = INCOME_WITH_WEEK
                 titles = circleName+"周收入"
                 title?.text = "最近7天收入"
                 income?.text = realIncome.toString()+"元"
                 incomeCount?.text = "共计"+realIncomeCount+"笔"

            }
            INCOME_WITH_MOUTH ->{
                 dateType = INCOME_WITH_MOUTH
                 titles = circleName+"月收入"
                 title?.text = "最近30天收入"
                 income?.text = realIncome.toString()+"元"
                 incomeCount?.text = "共计"+realIncomeCount+"笔"
            }
            INCOME_WITH_TOATAL ->{
                 dateType = INCOME_WITH_TOATAL
                 titles = circleName+"总收入"
            }
        }

        baseHeadView.showTitle(titles).showBackButton { finish() }
        adater = DaysInComeInfoAdater(this, mutableListOf(), R.layout.layout_days_income_info_item)
        recyclerview.adapter = adater
    }

    override fun initData() {
        super.initData()
        StatisticPresenter(this)
        mPresenter?.getData(UserManager.getInstance().token,start,groupId,dateType,action)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener {
            start = 0
            mPresenter?.getData(UserManager.getInstance().token,start,groupId,dateType,action)
        }

        recyclerview.setOnLoadMoreListener{
            start = start + 15
            mPresenter?.getData(UserManager.getInstance().token,start,groupId,dateType,action)
        }

        adater?.setOnItemClickLisntener { parentView, v, position ->
                val bean  = adater?.list?.get(position) as StatisticBean
                if(bean.count!! > 0){
                    StatisticIncomeDetailActivity.jumpToStatisticIncomeDetailActivity(this,groupId,bean.timeSection,bean.count,bean.realIncome,bean.dateName, circleName)
                }else{
                    when(type){
                        INCOME_WITH_DAYS -> ToastUtil.showShort(MyApplication.getInstance(),"圈子本日交易笔数为零")
                        INCOME_WITH_WEEK -> ToastUtil.showShort(MyApplication.getInstance(),"圈子本周交易笔数为零")
                        INCOME_WITH_MOUTH -> ToastUtil.showShort(MyApplication.getInstance(),"圈子本月交易笔数为零")
                        INCOME_WITH_TOATAL -> ToastUtil.showShort(MyApplication.getInstance(),"圈子本年交易笔数为零")
                    }
            }
        }
    }

    override fun showData(data:  MutableList<StatisticBean>?) {
        if(start == 0){
            refreshlayout.isRefreshing = false
            recyclerview.notifyChangeData(data,adater)
            setHeaderData()
        }else{
            recyclerview.changeData(data,adater)
        }
    }

    fun setHeaderData(){
        when(type){
            INCOME_WITH_DAYS ->{
                title?.text = adater?.list?.get(0)?.dateName+"收益"
                income?.text = adater?.list?.get(0)?.realIncome.toString()+"元"
                incomeCount?.text = "共计"+adater?.list?.get(0)?.count+"笔"
            }
            INCOME_WITH_TOATAL ->{
                title?.text = adater?.list?.get(0)?.dateName+"收益"
                income?.text = adater?.list?.get(0)?.realIncome.toString()+"元"
                incomeCount?.text = "共计"+adater?.list?.get(0)?.count+"笔"
            }
        }

    }

    override fun setPresenter(presenter: StatisticContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() = Unit

    override fun showLoadFinish() {
       recyclerview.loadFinish()
    }

    override fun showEmpty() = if(start == 0){
        baseEmptyView.showEmptyContent()
    }else{
        recyclerview.loadFinish()
    }

    override fun showReLoad() = Unit

    override fun showError(info: String?) = ToastUtil.showShort(MyApplication.getInstance(),info)

    override fun showNetError() = Unit

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    companion object{
        @JvmStatic
        val INCOME_WITH_DAYS = 0;
        @JvmStatic
        val INCOME_WITH_WEEK = 1
        @JvmStatic
        val INCOME_WITH_MOUTH = 2
        @JvmStatic
        val INCOME_WITH_TOATAL = 3
        @JvmStatic
        fun jumpToDaysIncomeActivity(activity: Activity, type: Int, groupId: Int, action: Int, realIncomeCount: Int?, realIncome: Double?, name: String){
            val intent = Intent(activity, DaysIncomeActivity::class.java)
            intent.putExtra("type",type)
            intent.putExtra("groupId",groupId)
            intent.putExtra("action",action)
            intent.putExtra("realIncomeCount",realIncomeCount)
            intent.putExtra("realIncome",realIncome)
            intent.putExtra("circleName",name)
            activity.startActivity(intent)
        }
    }

}
