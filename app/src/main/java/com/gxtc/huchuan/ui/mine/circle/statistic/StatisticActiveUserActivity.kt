package com.gxtc.huchuan.ui.mine.circle.statistic

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.utils.GsonUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.CircleActiveAdater
import com.gxtc.huchuan.adapter.CircleSignAdater
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.ui.mine.circle.CircleStatisticContract
import com.gxtc.huchuan.ui.mine.circle.CircleStatisticPresenter
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.ReLoginUtil
import kotlinx.android.synthetic.main.activity_circle_statistic.*
import java.util.ArrayList
/**
 * Created by zzg on 2018/3/16.
 */
class StatisticActiveUserActivity : BaseTitleActivity(), StatisticActiviContract.View  {

    var mPresenter:StatisticActiviContract.Presenter? = null
    var mAlertDialog: AlertDialog? = null
    var mAdapter:CircleActiveAdater? = null
    var id = -1
    var start = 0

    companion object {
        fun jumpToStatisticActiveUserActivity(activity: Activity,id:Int){
            val intent = Intent(activity,StatisticActiveUserActivity::class.java)
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
        }.showTitle("活跃用户")
        id = intent.getIntExtra("data",0)
        refreshlayout.setColorSchemeResources(*Constant.REFRESH_COLOR)
        recyclerView.setLoadMoreView(R.layout.model_footview_loadmore)
        mAdapter = CircleActiveAdater(this, ArrayList(), R.layout.statistic_avtive_item_layout)
        recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        recyclerView.setAdapter(mAdapter)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener {
            start = 0
            recyclerView.reLoadFinish()
            mPresenter?.getActiveDataList(start ,id)
        }
        recyclerView.setOnLoadMoreListener {
            start = start + 15
            mPresenter?.getActiveDataList(start ,id)
        }
    }

    override fun initData() {
        super.initData()
        StatisticActiviPresenter(this)
        mPresenter?.getActiveDataList(start ,id)
    }


    override fun showData(data: MutableList<CircleBean>?) {
        if(start == 0){
            refreshlayout.isRefreshing = false
            recyclerView.notifyChangeData(data, mAdapter)
        }else{
            recyclerView.changeData(data, mAdapter)
        }
    }



    override fun setPresenter(presenter: StatisticActiviContract.Presenter?) {
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
            mPresenter?.getActiveDataList(start ,id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }
}
