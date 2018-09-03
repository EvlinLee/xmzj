package com.gxtc.huchuan.ui.mall.order

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallCustomersAdapter
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import io.rong.imkit.RongIM
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*

/** Created by zzg on 2017/11/8
 *  客服的公共界面
 */

class MallCustomersActivity : BaseTitleActivity(), MallCustermersContract.View {

    var mPrsenter: MallCustermersContract.Prensenter? = null
    var type: String? = null
    var rand: String? = null

    private lateinit var mAdapter: ServicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_services)
    }

    override fun initView() {
        baseHeadView.showTitle("客服").showBackButton { finish() }
        swipeLayout?.setColorSchemeResources(*Constant.REFRESH_COLOR)
        type = intent.getStringExtra("type")
        rand = intent.getStringExtra("rand")
        mAdapter = ServicesAdapter(this, ArrayList<CoustomMerBean>(), R.layout.item_invite_chat)
        recyclerview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview?.adapter = mAdapter
    }

    override fun initListener() {
        swipeLayout?.setOnRefreshListener {
            mPrsenter?.getCustermersList(type,rand)
        }

        mAdapter.setOnReItemOnClickListener { _, position ->
            val bean = mAdapter.list?.get(position)
            bean?.userCode?.let {
                RongIM.getInstance().startPrivateChat(this@MallCustomersActivity, it, bean.name)
            }
        }
    }


    override fun initData() {
        MallCustomersPresemter(this)
        mPrsenter.let {
            it?.getCustermersList(type,rand)
        }

    }


    override fun showCustermersList(datas: ArrayList<CoustomMerBean>) {
        if(datas == null || datas.isEmpty()){
            baseEmptyView.showEmptyView()
            return
        }
        baseLoadingView?.hideLoading()
        swipeLayout?.isRefreshing = false
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showAllStatusSums(data: ArrayList<AllStatusSumsBean>) {}

    override fun setPresenter(presenter: MallCustermersContract.Prensenter?) {
        mPrsenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {}

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showHeadIcon(datas: List<MallBean>) {}

    override fun showActivitysData(datas: List<MallBean>) {}

    override fun showNetError() {}



    override fun onDestroy() {
        super.onDestroy()
        mPrsenter!!.destroy()
    }

    companion object{
        //0：全局客服1：商城客服 2：交易客服 3：app客服  rand  0：列表 1：随机
        val CUSTOMERS_TYPE_OF_ALL = "0"
        val CUSTOMERS_TYPE_OF_MALL = "1"
        val CUSTOMERS_TYPE_OF_DEAL = "2"
        val CUSTOMERS_TYPE_OF_APP= "3"
        val CUSTOMERS_STATUS_SHOW_LIST = "0"
        val CUSTOMERS_STATUS_SHOW_RAND = "1"
        fun goToCustomerServicesActivity(context: Activity, type:String, rand:String){
            if (UserManager.getInstance().isLogin) {
                var intent = Intent(context, MallCustomersActivity::class.java)
                intent.putExtra("type",type)
                intent.putExtra("rand",rand)
                context.startActivity(intent)
            } else {
                GotoUtil.goToActivityForResult(context, LoginAndRegisteActivity::class.java,
                        Constant.requestCode.NEWS_AUTHOR)
            }
        }

        class ServicesAdapter(context: Context?, list: ArrayList<CoustomMerBean>?, itemLayoutId: Int)
            : BaseRecyclerAdapter<CoustomMerBean>(context, list, itemLayoutId) {

            override fun bindData(holder: ViewHolder?, position: Int, bean: CoustomMerBean?) {
                val img = holder?.getView(R.id.riv_item_focus) as ImageView
                val tvName = holder?.getView(R.id.tv_item_focus_name) as TextView

                ImageHelper.loadRound(context, img, bean?.headPic, 4)
                tvName?.text = bean?.name
            }
        }
    }

}
