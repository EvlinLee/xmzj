package com.gxtc.huchuan.ui.mall.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.OrderDetailAdater
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.bean.pay.OrdersRequestBean
import com.gxtc.huchuan.bean.pay.PayBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RongImHelper
import com.gxtc.huchuan.ui.mall.MallDetailedActivity
import com.gxtc.huchuan.ui.pay.PayActivity
import com.gxtc.huchuan.utils.DialogUtil
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.activity_sub_order_detail.*
import kotlinx.android.synthetic.main.layout_address.*
import org.greenrobot.eventbus.Subscribe

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zzg on 2017/10/24.
 */
class MallOrderDetailActivity : BaseTitleActivity(), View.OnClickListener, MallOrderDetailContract.View {


    var mOrderDetailAdater: OrderDetailAdater? = null
    var orderNo: String? = null
    var mPresenter: MallOrderDetailContract.Presenter? = null
    var data: OrderDetailBean? = null

    companion object {
        fun junmpToOrderDetailActivity(context: Context, orderNo: String) {
            val intent = Intent(context, MallOrderDetailActivity::class.java)
            intent.putExtra("orderNo", orderNo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        EventBusUtil.register(this)
    }

    override fun initView() {
        baseHeadView.showTitle("订单详情").showBackButton {
            finish()
        }
        orderNo = intent.getStringExtra("orderNo")
        mOrderDetailAdater = OrderDetailAdater(this, ArrayList(), R.layout.order_detail_mall_item)
        val lineLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = lineLayoutManager!!
        recyclerview.adapter = mOrderDetailAdater
    }

    override fun initListener() {
        layout_coustom.setOnClickListener(this)
        pay.setOnClickListener(this)
        cancel.setOnClickListener(this)
        sure.setOnClickListener(this)
        mOrderDetailAdater?.setOnItemClickLisntener { parentView, v, position ->
            MallDetailedActivity.startActivity(this, mOrderDetailAdater?.list?.get(position)?.storeId!!)
        }
    }

    override fun initData() {
        MallOrderDetailPresenter(this)
        hideContentView()
        mPresenter!!.getOrderDetail(UserManager.getInstance().token, orderNo!!)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
        //在线客服
            R.id.layout_coustom -> {
                MallCustomersActivity.goToCustomerServicesActivity(this@MallOrderDetailActivity, MallCustomersActivity.CUSTOMERS_TYPE_OF_MALL, MallCustomersActivity.CUSTOMERS_STATUS_SHOW_LIST)
            }

        //付款
            R.id.pay -> {
                gotoPay(data);
            }

        //取消订单
            R.id.cancel -> {
                var mAlertDialog: AlertDialog? = null
                var text = ""
                if ("5".equals(data?.status)) {
                    text = "确定要删除订单"
                } else {
                    text = "确定要取消订单"
                }
                mAlertDialog = DialogUtil.showDeportDialog(this, false, null, text, { view ->
                    if (view.id == R.id.tv_dialog_confirm) {
                        mPresenter?.let {
                            it.deleteOrder(UserManager.getInstance().token, data?.orderNo!!)
                        }
                    }
                    mAlertDialog?.dismiss()
                })


            }

        //确认收货
            R.id.sure -> {
                mPresenter!!.confirmOrder(UserManager.getInstance().token, data?.orderNo!!)
            }

        }
    }

    override fun showOrderDetail(data: OrderDetailBean) {
        showContentView()

        this.data = data
        //0:已取消 1:未支付 2:待发货 3:待收货 4：待评价 5：已结束
        if (data.status == null) {
            baseEmptyView.showEmptyContent("暂无此订单状态")
            return
        }
        when (data.status) {
            "0" -> {
                setProgress()
                mallTextStatis.text = "已取消"
                mallText.text = "此订单已取消"
                mallPic.setImageResource(R.drawable.order_icon_jiaoyiquxiao)
            }
            "1" -> {
                setProgress()
                mallPic.setImageResource(R.drawable.order_icon_daifukuang)
                mallTextStatis.text = "未支付"
                mallText.text = "此订单未支付"
                rl_pay_paytype.visibility = View.GONE
                rl_paid.visibility = View.GONE
                ll_paytime.visibility = View.GONE
                ll_updatestatus.visibility = View.VISIBLE
                sure.visibility = View.GONE
            }
            "2" -> {
                setProgress()
                mallPic.setImageResource(R.drawable.order_icon_daifahuo)
                first_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress1.setBackgroundResource(R.color.color_23b15c);
                mallTextStatis.text = "待发货"
                mallText.text = "此订单待发货"
                rl_pay_paytype.visibility = View.VISIBLE
                rl_paid.visibility = View.VISIBLE
                ll_paytime.visibility = View.VISIBLE
                cancel.visibility = View.GONE
                pay.visibility = View.GONE
            }
            "3" -> {
                setProgress()
                mallPic.setImageResource(R.drawable.order_icon_yifahuo)
                first_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress1.setBackgroundResource(R.color.color_23b15c);
                second_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                mallTextStatis.text = "待收货"
                mallText.text = "此订单待收货"
                ll_updatestatus.visibility = View.VISIBLE
                cancel.visibility = View.GONE
                pay.visibility = View.GONE
            }
            "4" -> {
                first_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress1.setBackgroundResource(R.color.color_23b15c);
                second_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress2.setBackgroundResource(R.color.color_23b15c);
                last_doc.setBackgroundResource(R.drawable.order_icon_gou)
                mallTextStatis.text = "待评价"
                cancel.visibility = View.GONE
                pay.visibility = View.GONE
            }
            "5" -> {
                mallPic.setImageResource(R.drawable.order_icon_jiaoyiwangcheng)
                first_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress1.setBackgroundResource(R.color.color_23b15c);
                second_doc.setBackgroundResource(R.drawable.shape_mall_circle)
                progress2.setBackgroundResource(R.color.color_23b15c);
                last_doc.setBackgroundResource(R.drawable.order_icon_gou)
                mallTextStatis.text = "已结束"
                mallText.text = "此订单待已完成"
                cancel.visibility = View.GONE
                pay.visibility = View.GONE
            }
        }
        if ("1".equals(data.isLogistics)) {
            wuliu_layout.visibility = View.VISIBLE
            setAddress(data)
        } else {
            wuliu_layout.visibility = View.GONE
            rl_address_btn.visibility = View.GONE
        }
        mOrderDetailAdater?.notifyChangeData(data.orderList)
        tv_liuyan.text = data.message
        tv_total.text = data.orderMoney
        tv_mall_price.text = data.orderMoney
        tv_yunfei.text = "+￥0"
        when (data.payType) {
            "WX" -> tv_pay_type.text = "微信支付"
            "ALIPAY" -> tv_pay_type.text = "支付宝支付"
            "BALANCE" -> tv_pay_type.text = "余额支付"
        }
        tv_all_total.text = data.orderMoney
        order_no.text = data.orderNo
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val mDate = Date(data.createDate.toLong())
        val time = sdf.format(mDate)
        creat_time.text = time
        for (i in 0 until data.statusList!!.size) {
            if ("2".equals(data.statusList!!.get(i).status)) {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                val time = sdf.format(Date(data.statusList!!.get(i).createTime!!.toLong()))
                pay_time.setText(time)
            }
        }
    }

    override fun showComstumers(data: ArrayList<CoustomMerBean>) {
        if (data != null && data.size > 0)
            RongImHelper.startCustomerServices(this, data.get(0).name, data.get(0).userCode)
    }

    fun setProgress() {
        first_doc.setBackgroundResource(R.drawable.shape_mall_white_circle)
        progress1.setBackgroundResource(R.color.divide_line)
        second_doc.setBackgroundResource(R.drawable.shape_mall_white_circle)
        progress2.setBackgroundResource(R.color.divide_line)
        last_doc.setBackgroundResource(R.drawable.shape_mall_white_circle)
    }

    fun setAddress(data: OrderDetailBean) {
        tv_select_address.visibility = View.INVISIBLE
        rl_address_btn.visibility = View.VISIBLE
        tv_name.visibility = View.VISIBLE
        tv_phone.visibility = View.VISIBLE
        tv_is_default.visibility = View.VISIBLE
        tv_address.visibility = View.VISIBLE
        tv_name.text = data.name
        tv_phone.text = data.tel
        tv_address.text = data.province + data.city + data.area + data.address
    }

    override fun setPresenter(presenter: MallOrderDetailContract.Presenter?) {
        mPresenter = presenter
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

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {}

    override fun showfirmOrderResult(result: Any) {
        ToastUtil.showShort(this, "确认收货成功");
        mPresenter!!.getOrderDetail(UserManager.getInstance().token, orderNo!!)
        EventBusUtil.post(data)
    }

    override fun showDeleteOrderResult(result: Any) {
        ToastUtil.showShort(this, "取消订单成功");
        EventBusUtil.post(data)
        finish()
    }

    private fun gotoPay(bean: OrderDetailBean?) {
        val jobj = JSONObject()
        val jarr = JSONArray()
        for (goods in bean!!.orderList!!) {
            val temp = JSONObject()
            temp.put("id", goods.storeId)
            temp.put("amount", goods.amount)
            jarr.put(temp)
        }
        jobj.put("storePrice", jarr)

        val orderBean = OrdersRequestBean()
        orderBean.goodsName = bean.orderList?.get(0)?.storeName
        orderBean.orderNo = bean.orderNo
        orderBean.token = UserManager.getInstance().token
        orderBean.transType = "SC"
        orderBean.totalPrice = (bean.orderMoney!!.toFloat() * 100).toString()
        orderBean.extra = jobj.toString()
        PayActivity.startActivity(this, orderBean, 666, "1")
    }

    @Subscribe
    fun onEvent(bean: PayBean) {
        if (bean.isPaySucc) {
            mPresenter?.getOrderDetail(UserManager.getInstance().token, bean.OrderNo!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.destroy()
        EventBusUtil.unregister(this)
    }


}
