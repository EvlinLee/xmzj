package com.gxtc.huchuan.ui.mine.circle.statistic

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.utils.GsonUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.SattisticPentAdater
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.data.UserManager
import kotlinx.android.synthetic.main.activity_circle_statistic.*
/**
 * Created by zzg on 2018/3/16.
 */
class StatisticPentActivity : BaseTitleActivity(), StatisticPentActiviContract.View  {

    var mPresenter:StatisticPentActiviContract.Presenter? = null
    var mAlertDialog: AlertDialog? = null
    var mAdapter:SattisticPentAdater? = null
    var id = -1
    var start = 0
    var titles:TextView? = null
    var income:TextView? = null
    var incomeCount:TextView? = null


    companion object {
        fun jumpToStatisticPentActivity(activity: Activity,id:Int){
            val intent = Intent(activity, StatisticPentActivity::class.java)
            intent.putExtra("data",id)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_statistic)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showBackButton{
            finish()
        }.showTitle("每日佣金")
        id = intent.getIntExtra("data",0)
        refreshlayout.setColorSchemeResources(*Constant.REFRESH_COLOR)
        recyclerView.setLoadMoreView(R.layout.model_footview_loadmore)
        val headView = layoutInflater.inflate(R.layout.days_income_header,null)
        titles = headView.findViewById<TextView>(R.id.income_note)
        income = headView.findViewById<TextView>(R.id.income)
        incomeCount = headView.findViewById<TextView>(R.id.income_count)
        recyclerView.addHeadView(headView)
        mAdapter = SattisticPentAdater(this, mutableListOf(), R.layout.statistic_pent_item_layout)
        recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        recyclerView.setAdapter(mAdapter)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener {
            start = 0
            recyclerView.reLoadFinish()
            mPresenter?.getPentDataList(UserManager.getInstance().token,start,id,0,0)
        }
        recyclerView.setOnLoadMoreListener {
            start = start + 15
            mPresenter?.getPentDataList(UserManager.getInstance().token,start,id,0,0)
        }

        mAdapter?.setOnReItemOnClickListener { v, position ->
            val bean = mAdapter?.list?.get(position)
            if(bean?.saleFee!! > 0.0){
                StatisticPentDetailActivity.jumpToStatisticPentDetailActivity(this, id, bean.timeSection, bean.count, bean.saleFee, bean.dateName)
            }else{
               ToastUtil.showShort(MyApplication.getInstance(),"圈子本日佣金为零")
            }
        }
    }

    override fun initData() {
        super.initData()
        StatisticPentPresenter(this)
        mPresenter?.getPentDataList(UserManager.getInstance().token,start,id,0,0)
    }


    override fun showData(data: MutableList<StatisticBean>?) {
        if(start == 0){
            refreshlayout.isRefreshing = false
            recyclerView.notifyChangeData(data, mAdapter)
            titles?.text = mAdapter?.list?.get(0)?.dateName
            income?.text = mAdapter?.list?.get(0)?.fee.toString()+"元"
            incomeCount?.text = "共计"+mAdapter?.list?.get(0)?.count+"笔"
        }else{
            recyclerView.changeData(data, mAdapter)
        }
    }



    override fun setPresenter(presenter: StatisticPentActiviContract.Presenter?) {
        mPresenter = presenter
    }


    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
        if(start == 0){
            refreshlayout.isRefreshing = false
            baseEmptyView.showEmptyContent()
        } else{
            recyclerView.loadFinish()
        }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
       ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter?.getPentDataList(UserManager.getInstance().token,start,id,0,0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }
}
