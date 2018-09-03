package com.gxtc.huchuan.ui.mine.incomedetail.distribute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.InComeInfoAdater
import com.gxtc.huchuan.adapter.ProfitAdater
import com.gxtc.huchuan.bean.DistributionBean
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo.InComeContract
import com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo.InComePresenter
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.activity_income_info.*
/**
 * Created by zzg on 2017/12/29.
 */
class ClassProfitDetailActivity : BaseTitleActivity(), InComeContract.View {
    var id:String? = null
    var type:String? = null
    var title:String? = null
    var bean: DistributionBean? = null
    var mPresenter: InComeContract.Presenter? = null
    var adater: ProfitAdater? = null
    var map :HashMap<String,String>? = null
    var start = 0
    var dateType = 4 //0：本日，1：本周，2：本月，3:本年，4：全部
    var isLoadMore = false
    var ivHead: ImageView? = null
    var txTitle: TextView? = null
    var txPrice: TextView? = null
    var txType: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_info)
    }

    override fun initView() {
        super.initView()
        map = HashMap<String,String>()
        bean = intent.getSerializableExtra("bean") as DistributionBean
        dateType = intent.getIntExtra("dateType",4)
        id = bean?.id
        type = bean?.type
        title = bean?.title
        baseHeadView.showTitle("课程收益详细信息").showBackButton{
            finish()
        }
        val view = View.inflate(this, R.layout.distribute_info_header,null)
        recyclerview.addHeadView(view)
        ivHead = view.findViewById(R.id.iv_head) as ImageView
        txTitle = view.findViewById(R.id.tv_title) as TextView
        txType = view.findViewById(R.id.tv_type) as TextView
        txPrice = view.findViewById(R.id.tv_price) as TextView
        setHeadData()
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        adater = ProfitAdater(this,ArrayList<NewDistributeBean>(), R.layout.layout_income_info_item)
        recyclerview.adapter = adater
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    fun setHeadData(){
        when(type){
            "1" -> txType?.text = "课程"
            "2" -> txType?.text = "系列课"
            "3" -> txType?.text = "圈子"
        }
        txTitle?.text = bean?.title
        txPrice?.text = "价格:￥"+bean?.fee + "返"+bean?.pent+"%"
        ImageHelper.loadImage(this,ivHead,bean?.facePic)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                start = 0
                isLoadMore = false
                recyclerview.reLoadFinish()
                map?.set("start",start.toString())
                mPresenter?.getProfitDetail(true,map!!)
            }

        })
        recyclerview.setOnLoadMoreListener(object : LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMoreRequested() {
                isLoadMore = true
                start = start + 15
                map?.set("start",start.toString())
                mPresenter?.getProfitDetail(true,map!!)
            }

        })
    }

    override fun initData() {
        super.initData()
        map?.put("token", UserManager.getInstance().token)
        map?.put("id",id!!)
        map?.put("dateType",dateType!!.toString())
        map?.put("type",type!!)//业务类型，0话题 1系列课 2圈子
        map?.put("start",start.toString())
        InComePresenter(this)
        mPresenter?.getProfitDetail(false,map!!)
    }

    override fun showData(data: ArrayList<NewDistributeBean>) {}

    override fun setPresenter(presenter: InComeContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView.hideLoading()
    }

    override fun showProfitDetail(data: ArrayList<NewDistributeBean>) {
        if(!isLoadMore){
            refreshlayout.isRefreshing = false
            recyclerview.notifyChangeData(data,adater)
        }else{
            recyclerview.changeData(data,adater)
        }
    }


    override fun showEmpty() {
        if(start == 0){
            baseEmptyView.showEmptyContent()
        }else{
            recyclerview.loadFinish()
        }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun showNetError() {}

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    companion object{
        @JvmStatic
        fun jumpToIncomeInfoActivity(activity: Activity, bean: DistributionBean,dateType:Int){
            val intent = Intent(activity, ClassProfitDetailActivity::class.java)
            intent.putExtra("bean",bean)
            intent.putExtra("dateType",dateType)
            activity.startActivity(intent)
        }
    }
}
