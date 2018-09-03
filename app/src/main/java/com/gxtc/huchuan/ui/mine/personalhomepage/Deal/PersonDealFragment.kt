package com.gxtc.huchuan.ui.mine.personalhomepage.Deal

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.Deal1LevelAdapter
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity
import kotlinx.android.synthetic.main.activity_deal_recomend.*

/**
 * Created by zzg on 2017/11/1.
 */
class PersonDealFragment : BaseTitleFragment() , PersonDealContract.View{

    var mPresenter: PersonDealContract.Presenter? = null
    var adapter: Deal1LevelAdapter? = null
    var isLoadMore = false
    var start = 0

    var userCode = ""
    var token = ""

    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View
            = inflater!!.inflate(R.layout.activity_deal_recomend,container,false)

    override fun initListener() {
        listView.setLoadMoreView(R.layout.model_footview_loadmore)
        listView.setOnLoadMoreListener {
            mPresenter?.loadMoreData()
        }
        swipeLayout.setOnRefreshListener {
            listView.reLoadFinish()
            mPresenter?.refreshData()
        }
        adapter = Deal1LevelAdapter(activity!!, ArrayList<DealListBean>(), R.layout.deal_list_home_page)
        adapter?.setOnReItemOnClickListener { _, position ->
            GoodsDetailedActivity.startActivity(context, adapter?.list?.get(position)?.id)
        }
    }

    override fun initData() {
        val manager = LinearLayoutManager(activity)
        listView.layoutManager = manager
        listView.adapter = adapter

        token = UserManager.getInstance().token

        PersonDealPresenter(this)
        mPresenter?.getUserDealList(token, userCode)
    }

    override fun showData(datas: MutableList<DealListBean>?) {
        listView.notifyChangeData(datas, adapter)
    }

    override fun showRefreshData(datas: MutableList<DealListBean>?) {
        swipeLayout.isRefreshing = false
        listView.notifyChangeData(datas, adapter)
    }

    override fun showLoadMoreData(datas: MutableList<DealListBean>?) {
        listView.changeData(datas, adapter)
    }

    override fun showNoLoadMore() {
        listView.loadFinish()
    }

    override fun setPresenter(presenter: PersonDealContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
    }

    override fun showLoadFinish() {
    }

    override fun showEmpty() {
        baseEmptyView.showEmptyContent()
    }

    override fun showReLoad() {
    }

    override fun showError(info: String?) {
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter?.getUserDealList(token, userCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    override fun onGetBundle(bundle: Bundle?) {
        bundle?.let {
            userCode = bundle.getString(Constant.INTENT_DATA)
        }
    }

}