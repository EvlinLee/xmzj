package com.gxtc.huchuan.ui.mine.circle.statistic.recent

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.RecentDynamicAdapter
import com.gxtc.huchuan.bean.RecentBean
import com.gxtc.huchuan.data.CircleRepository
import com.gxtc.huchuan.data.CircleSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/16.
 * 圈子数据统计
 * 新增动态
 */
class NewDynamicFragment : BaseTitleFragment() {

    private var start = 0
    private var dateType = "0"       //2 最近30天
    private var checkType = "1"      //0 新增用户 1 新增动态 2 活跃量
    private var groupId: Int ?= null

    private lateinit var mData: CircleSource
    private lateinit var mAdapter: RecentDynamicAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout


    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View {
        refreshLayout = View.inflate(context, R.layout.layout_swipe_recyclerview, null) as SwipeRefreshLayout
        recyclerView = refreshLayout.findViewById(com.gxtc.commlibrary.R.id.recyclerview)

        refreshLayout.setColorScheme(*Constant.REFRESH_COLOR)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mAdapter = RecentDynamicAdapter(context, mutableListOf(), R.layout.item_recent_dynamic)
        recyclerView.adapter = mAdapter
        return refreshLayout
    }

    override fun initListener() {
        /*recyclerView.setOnLoadMoreListener {
            loadMore()
        }*/

        refreshLayout.setOnRefreshListener {
            start = 0
            recyclerView.reLoadFinish()
            getData()
        }

        mAdapter.setOnReItemOnClickListener { _, position ->

        }
    }

    override fun initData() {
        mData = CircleRepository()
        getData()
    }

    private fun getData(){
        if(start == 0 && mAdapter.list.size == 0){
            baseLoadingView.showLoading()
        }

        val map = hashMapOf<String, String>()
        val token = UserManager.getInstance().token
        token?.let { map.put("token", it) }
        map.put("start", start.toString())
        map.put("dateType", dateType)
        map.put("checkType", checkType)
        map.put("groupId", groupId.toString())


        mData.getCircleRecentNewUser(map, object : ApiCallBack<List<RecentBean>>(){
            override fun onSuccess(data: List<RecentBean>?) {
                baseLoadingView?.let {
                    it.hideLoading()

                    if(data == null || data.size == 0){
                        if(mAdapter.list.size == 0){
                            baseEmptyView.showEmptyContent()
                        }
                        return
                    }
                    showData(data)
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                baseLoadingView?.let {
                    it.hideLoading()

                    if(errorCode == ApiObserver.NET_ERROR && mAdapter.list.size == 0){
                        baseEmptyView.showNetWorkView {
                            baseEmptyView.hideEmptyView()
                            getData()
                        }
                    }else{
                        ToastUtil.showShort(context, message)
                    }
                }
            }
        })
    }


    private fun loadMore(){
        val map = hashMapOf<String, String>()
        val token = UserManager.getInstance().token
        token?.let { map.put("token", it) }
        map.put("start", (mAdapter.list.size + 15).toString())
        map.put("dateType", dateType)
        map.put("checkType", checkType)
        map.put("groupId", groupId.toString())

        mData.getCircleRecentNewUser(map, object : ApiCallBack<List<RecentBean>>(){
            override fun onSuccess(data: List<RecentBean>?) {
                baseLoadingView?.let {
                    if(data == null || data.size == 0){
                        if(mAdapter.list.size == 0){
                            recyclerView.loadFinish()
                        }
                        return
                    }
                    recyclerView.changeData(data, mAdapter)
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                baseLoadingView?.let {
                    ToastUtil.showShort(context, message)
                }
            }
        })
    }

    private fun showData(datas: List<RecentBean>?){
        refreshLayout.isRefreshing = false
        recyclerView.notifyChangeData(datas, mAdapter)
    }


    override fun onGetBundle(bundle: Bundle?) {
        bundle?.let { groupId = it.getInt("id") }
    }

    override fun onDestroy() {
        super.onDestroy()
        mData.destroy()
    }
}