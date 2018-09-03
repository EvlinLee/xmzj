package com.gxtc.huchuan.ui.live.hostpage

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.AllMembersInfoListAdapter
import com.gxtc.huchuan.adapter.SpeackerRefundListAdapter
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.bean.PurchaseListBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.widget.RecyclerSpace
import kotlinx.android.synthetic.main.empty_error.view.*
import kotlinx.android.synthetic.main.refunds_list_layout.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by zzg on 2017/9/15.
 */
class RefundListFragment : BaseTitleFragment() {
    var adapter : SpeackerRefundListAdapter? = null;
    var type : String? = null;
    var loadMore = false
    internal var id = -1
    internal var memberType = -1
    var start = 0
    var map = HashMap<String,String>()
    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View {
        val view = inflater?.inflate(R.layout.refunds_list_layout, container, false)
        return view!!
    }

    private fun  initView() {
        swipe_members.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
        val mLinearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listview.setLayoutManager(mLinearLayoutManager)
        listview.setLoadMoreView(R.layout.model_footview_loadmore)
        adapter = SpeackerRefundListAdapter(activity, ArrayList<PurchaseListBean>(),
                R.layout.activity_refunds_list_layout_item)
        listview.setAdapter(adapter)
        adapter!!.setOnReItemOnClickListener { v, position ->
            val intent = Intent(activity,ReFundsDetailActivity::class.java)
            intent.putExtra("data",adapter!!.list.get(position))
            intent.putExtra("memberType",memberType)
            intent.putExtra("id",id)
            startActivity(intent)
        }
    }

    override fun initData() {
        super.initData()
        type = arguments!!.getString("type")
        id = arguments!!.getInt("groupId",-1)
        memberType = arguments!!.getInt("memberType",-1)
        initView()
        getData(false)
    }


    fun getData(isLoadMoer : Boolean){
        map.put("token",UserManager.getInstance().token)
        map.put("start",start.toString())
        map.put("type",type!!)//0:交易退款，1，课堂退款，2，圈子退款，3，商城退款
        map.put("pageSize","15")
        if(id != -1){
            map.put("groupId",id.toString())
        }
        var sub = LiveApi.getInstance().getSellerRefundList(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<List<PurchaseListBean>>>(object : ApiCallBack<List<PurchaseListBean>>(){
                    override fun onSuccess(data: List<PurchaseListBean>?) {
                        if(data != null && data.size > 0){
                            if(!isLoadMoer){
                                listview.notifyChangeData(data,adapter)
                            }else{
                                start = start + 15
                                listview.changeData(data,adapter)
                            }
                        }else{
                            if(start == 0){
                                baseEmptyView.showEmptyContent(getString(R.string.empty_no_data))
                            }else{
                                listview.loadFinish()
                            }
                        }
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(activity, message!!)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }
    override fun initListener() {
        super.initListener()
        swipe_members.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            swipe_members.isRefreshing = false
            listview.reLoadFinish()
            start = 0
            getData(false)
        })
        listview.setOnLoadMoreListener(LoadMoreWrapper.OnLoadMoreListener {
            getData(true)
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}