package com.gxtc.huchuan.ui.special

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.gxtc.commlibrary.R.id.recyclerview
import com.gxtc.commlibrary.base.BaseTitleActivity

import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.R.id.swipeLayout
import com.gxtc.huchuan.adapter.MySpecialListAdapter
import com.gxtc.huchuan.data.SpecialBean
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 * 我订阅的专题
 * SpecialSelectActivity 这个界面复用这边的大部分代码
 */
class MySpecialListActivity : BaseTitleActivity(), MySpecialListContract.View {

    private lateinit var mAdapter: MySpecialListAdapter
    private lateinit var mPresenter: MySpecialListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_swipe_recyclerview)
    }

    override fun initView() {
        baseHeadView.showTitle(getString(R.string.title_my_speciai_list))
                .showBackButton { finish() }
        swipeLayout.setColorScheme(*Constant.REFRESH_COLOR)

        mAdapter = MySpecialListAdapter(this, mutableListOf())
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MySpecialListActivity, LinearLayoutManager.VERTICAL, false)
            addFootView(R.layout.model_footview_loadmore)
            adapter = mAdapter
        }
    }

    override fun initListener() {
        swipeLayout.setOnRefreshListener {
            recyclerview.reLoadFinish()
            mPresenter.refreshData()
        }
        mAdapter.setOnReItemOnClickListener { _, position ->
            val bean = this.mAdapter.list[position]
            SpecialDetailActivity.gotoSpecialDetailActivity(this@MySpecialListActivity, bean.id.toString())
        }

        recyclerview.setOnLoadMoreListener {
            mPresenter.loadMoreData()
        }
    }

    override fun initData() {
        MySpecialListPresenter(this, mAdapter)
        mPresenter.getData()
    }

    override fun showData(datas: MutableList<SpecialBean>) {
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showRefreshData(datas: MutableList<SpecialBean>?) {
        swipeLayout.isRefreshing = false
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showLoadMoreData(datas: MutableList<SpecialBean>?) {
        recyclerview.changeData(datas, mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview.loadFinish()
    }

    override fun showLoad() {
        baseLoadingView.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView.hideLoading()
    }

    override fun showEmpty() {
        baseEmptyView.showEmptyContent()
    }

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter.getData()
        }
    }

    override fun setPresenter(presenter: MySpecialListContract.Presenter?) {
        mPresenter = presenter!!
    }

    override fun showReLoad() = Unit

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.destroy()
    }
}