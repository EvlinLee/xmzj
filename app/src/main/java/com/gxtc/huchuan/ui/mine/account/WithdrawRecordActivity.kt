package com.gxtc.huchuan.ui.mine.account

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.WithdrawRecordAdapter
import com.gxtc.huchuan.bean.WithdrawRecordBean
import kotlinx.android.synthetic.main.activity_withdraw_record.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
class WithdrawRecordActivity : BaseTitleActivity(),WithdrawRecordContract.View {

    var mPresenter : WithdrawRecordContract.Presenter ? = null
    var mAdapter : WithdrawRecordAdapter ? = null

    var bean : WithdrawRecordBean ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_record)
    }

    override fun initView() {
        baseHeadView?.showTitle("提现记录")
        baseHeadView?.showBackButton { finish() }
        swipelayout?.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)

        mAdapter = WithdrawRecordAdapter(this, mutableListOf(),R.layout.item_withdraw_record)
        recyclerview?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerview?.setLoadMoreView(R.layout.model_footview_loadmore)
        recyclerview?.adapter = mAdapter
    }

    override fun initListener() {
        recyclerview?.setOnLoadMoreListener{
            mPresenter?.loadMoreData()
        }
        swipelayout?.setOnRefreshListener {
            mPresenter?.refreshData()
            recyclerview?.reLoadFinish()
        }

        mAdapter?.setOnReItemOnClickListener { _, position ->
            val bean = mAdapter?.list?.get(position)
            GotoUtil.goToActivity(this@WithdrawRecordActivity,WithdrawDetailedActivity::class.java,0,bean)
        }
    }

    override fun initData() {
        WithdrawRecordPresenter(this)
        mPresenter?.getDataList()
    }

    override fun showData(datas: MutableList<WithdrawRecordBean>) {
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showRefreshData(datas: MutableList<WithdrawRecordBean>?) {
        swipelayout?.isRefreshing = false
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showLoadMoreData(datas: MutableList<WithdrawRecordBean>?) {
        recyclerview?.changeData(datas,mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview?.loadFinish()
    }

    override fun setPresenter(presenter: WithdrawRecordContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView?.hideLoading()
    }

    override fun showEmpty() {
        baseEmptyView?.showEmptyContent()
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {
        baseEmptyView?.showNetWorkView{
            baseEmptyView?.hideEmptyView()
            mPresenter?.getDataList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

}