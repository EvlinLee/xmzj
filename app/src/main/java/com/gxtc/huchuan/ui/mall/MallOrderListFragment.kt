package com.gxtc.huchuan.ui.mall

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallOrderStatusAdapter
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.bean.pay.OrdersRequestBean
import com.gxtc.huchuan.bean.pay.PayBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mall.order.*
import com.gxtc.huchuan.ui.pay.PayActivity
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.ReLoginUtil
import kotlinx.android.synthetic.main.activity_my_mall.*
import kotlinx.android.synthetic.main.maill_order_tab.*
import kotlinx.android.synthetic.main.mall_bottom_layout.*
import kotlinx.android.synthetic.main.mall_layout_middle_tab.*
import kotlinx.android.synthetic.main.order_status_layout.*
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by zzg on 2018/2/5.
 * 因为商城订单要跟订单列表合并，所以要抽出来
 */
class MallOrderListFragment : BaseTitleFragment() , MallOrderContract.View{
    var mMallOrderStatusAdapter : MallOrderStatusAdapter? = null
    var mPresenter :MallOrderContract.Presenter? = null
    var title :String? = null
    var map :HashMap<String,String>? = null
    var isLoadMore :Boolean = false
    var status:String ? = null
    var OrderNo:String = ""
    var curentPosition:Int = -1
    var comfirPosition:Int = -1

    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View {
        return inflater!!.inflate(R.layout.order_status_layout, container, false)
    }

    override fun onGetBundle(bundle: Bundle?) {
        super.onGetBundle(bundle)
        status = bundle?.getString("status")
    }

    override fun initListener() {
        super.initListener()
        initView()
        refreshlayout.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                isLoadMore = false
                refreshlayout.isRefreshing = false
                mPresenter!!.getOrderStatusList(false,map!!)
            }

        })
        recyclerview.setOnLoadMoreListener {
            isLoadMore = true
            mPresenter!!.getOrderStatusList(true,map!!)
        }
        mMallOrderStatusAdapter!!.setOnReItemOnClickListener { v, position ->
            MallOrderDetailActivity.junmpToOrderDetailActivity(activity!!,mMallOrderStatusAdapter!!.list.get(position).orderId!!)
        }
        mMallOrderStatusAdapter!!.setOnClickBtnListener(object :MallOrderStatusAdapter.OnClickBtnLisner{
            //再来一单
            override fun anotherOrder(bean: OrderBean?) {
                val list = arrayListOf<MallShopCartBean>()
                bean!!.storeOrderList
                for(mOrderMallBean in bean!!.storeOrderList!!){
                    val bean1 = MallShopCartBean();
                    bean1.storeId = mOrderMallBean.storeId!!.toString()
                    bean1.amount = mOrderMallBean.sum!!
                    bean1.storePriceId = mOrderMallBean.storePriceId!!
                    bean1.picUrl = mOrderMallBean.picUrl!!
                    bean1.storeName = mOrderMallBean.name!!
                    bean1.storePriceName = mOrderMallBean.priceName!!
                    bean1.storePricePrice = mOrderMallBean.price!!.toString()
                    list.add(bean1)
                }
                MallOrderDetailedActivity.startActivity(activity!!,beans = list)
            }
            //确认收货
            override fun sure(bean: OrderBean?, position:Int) {
                comfirPosition = position
                mPresenter!!.confirmOrder(UserManager.getInstance().token,bean!!.orderId!!)
            }
            //取消订单
            override fun cancel(bean: OrderBean?, position:Int ) {
                var mAlertDialog: AlertDialog? = null
                var text = ""
                if("5".equals(status)){
                    text = "确定要删除订单"
                }else{
                    text = "确定要取消订单"
                }
                mAlertDialog = DialogUtil.showDeportDialog(activity, false, null, text,{ view ->
                    if (view.id == R.id.tv_dialog_confirm) {
                        curentPosition = position
                        mPresenter!!.deleteOrder(UserManager.getInstance().token,bean!!.orderId!!)
                    }
                    mAlertDialog?.dismiss()
                })
            }
            //付款
            override fun pay(bean: OrderBean?) {
                OrderNo = bean?.orderId!!
                gotoPay(bean)
            }

        })
    }

    override fun initData() {
        super.initData()
        EventBusUtil.register(this)
        map = HashMap()
        map!!.put("token",UserManager.getInstance().token)
        if(!TextUtils.isEmpty(status))
            map!!.put("status",status!!)
        MallOrderSatusPresenter(this)
        baseLoadingView.showLoading()
        mPresenter!!.getOrderStatusList(false,map!!)

    }

     fun initView() {
        refreshlayout.setColorSchemeResources(*Constant.REFRESH_COLOR)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        mMallOrderStatusAdapter = MallOrderStatusAdapter(activity, ArrayList<OrderBean>(), R.layout.mall_order_status_item_layout)
        recyclerview.adapter = mMallOrderStatusAdapter
        recyclerview.layoutManager = LinearLayoutManager(activity)!!
    }

     fun goBack(){
        val intent = Intent()
        intent.putExtra("sum",mMallOrderStatusAdapter!!.list.size)
        activity?.setResult(Activity.RESULT_OK,intent)
        activity?.finish()
    }

    private fun gotoPay(bean: OrderBean?) {
        val jobj = JSONObject()
        val jarr = JSONArray()
        for(goods in bean!!.storeOrderList!!){
            val temp = JSONObject()
            temp.put("id",goods.storePriceId)
            temp.put("amount",goods.sum)
            jarr.put(temp)
        }
        jobj.put("storePrice",jarr)

        val orderBean = OrdersRequestBean()
        orderBean.goodsName = bean.name
        orderBean.orderNo = bean.orderId
        orderBean.token = UserManager.getInstance().token
        orderBean.transType = "SC"
        orderBean.totalPrice = (bean.money!!.toFloat() * 100).toString()
        orderBean.extra = jobj.toString()

        PayActivity.startActivity(activity!!,orderBean)
    }

    @Subscribe
    fun onEvent(bean: PayBean){
        //因为是重新支付，所以订单号不需后台返回
        if(bean.isPaySucc){
            var orderId = bean.OrderNo
            mPresenter!!.getOrderStatusList(false,map!!)
        }
    }

    @Subscribe
    fun onEvent(bean: OrderDetailBean){
        mPresenter!!.getOrderStatusList(false,map!!)
    }


    override fun tokenOverdue() {
        ReLoginUtil.ReloginTodo(activity)
    }

    override fun showOrderStatusList(datas: ArrayList<OrderBean>) {
        baseLoadingView.hideLoading()
        if(datas != null && datas.size > 0 ){
            if(!isLoadMore){
                recyclerview.notifyChangeData(datas,mMallOrderStatusAdapter)
            }else{
                recyclerview.changeData(datas,mMallOrderStatusAdapter)
            }
        }else{
            recyclerview.loadFinishNotView()
        }
    }

    override fun showfirmOrderResult(result: Any) {
        if(comfirPosition != -1){
            ToastUtil.showShort(activity,"收货成功")
            recyclerview.removeData(mMallOrderStatusAdapter,comfirPosition)
        }
    }

    override fun showDeleteOrderResult(result: Any) {
        if(curentPosition != -1){
            ToastUtil.showShort(activity,"取消订单成功")
            recyclerview.removeData(mMallOrderStatusAdapter,curentPosition)
        }
    }

    override fun setPresenter(presenter: MallOrderContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
        baseEmptyView.showEmptyContent()
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun showNetError() {}

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
        mPresenter?.destroy()
    }

}
