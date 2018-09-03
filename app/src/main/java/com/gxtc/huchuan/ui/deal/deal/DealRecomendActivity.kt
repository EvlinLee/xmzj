package com.gxtc.huchuan.ui.deal.deal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.Deal1LevelAdapter
import com.gxtc.huchuan.bean.DealData
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.ui.deal.DealRecommendPresenter
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity
import kotlinx.android.synthetic.main.activity_deal_recomend.*
import kotlin.collections.ArrayList

/**
 * Created by zzg on 17/10/27
 */
class DealRecomendActivity : BaseTitleActivity(),DealRecomendContrat.View{

    var adapter : Deal1LevelAdapter? = null
    var mPresenter:DealRecomendContrat.Presenter? = null
    var isLoadMore = false
    var start = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal_recomend)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("优质推荐").showBackButton{
            finish()
        }
        listView.setLoadMoreView(R.layout.model_footview_loadmore)
        adapter = Deal1LevelAdapter(this, ArrayList<DealListBean>(),
                R.layout.deal_list_home_page)
        var manager = LinearLayoutManager(this)
        listView.layoutManager = manager
        listView.adapter = adapter
    }

    override fun initData() {
        super.initData()
        DealRecommendPresenter(this)
        mPresenter!!.getDealRecomemndData(false)
    }

    override fun initListener() {
        super.initListener()
        swipeLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                isLoadMore = false
                swipeLayout.isRefreshing = false
            }
        })
        listView.setOnLoadMoreListener(object :LoadMoreWrapper.OnLoadMoreListener{
            override fun onLoadMoreRequested() {
                isLoadMore = true
                mPresenter!!.getDealRecomemndData(true)
            }

        })
    }

    override fun showDealRecomemndData(datas: DealData) {
        if(datas != null && datas.infos.size > 0 ){
            if(!isLoadMore){
                listView.notifyChangeData(datas.infos,adapter)
            }else{
                listView.changeData(datas.infos,adapter)
            }
        }else{
            listView.loadFinishNotView()
        }

        adapter!!.setOnReItemOnClickListener { v, position ->
            val intent = Intent(this, GoodsDetailedActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA,  adapter!!.list.get(position).id)
            startActivityForResult(intent, 101)
        }
    }

    override fun setPresenter(presenter: DealRecomendContrat.Presenter?) {
        this.mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {}

    override fun showReLoad() {}

    override fun showError(info: String?) {
      ToastUtil.showShort(this,info)
    }

    override fun showNetError() {}

    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.destroy()
    }
}
