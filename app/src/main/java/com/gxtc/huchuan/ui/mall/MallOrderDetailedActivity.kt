package com.gxtc.huchuan.ui.mall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.AddressBean
import com.gxtc.huchuan.bean.event.EventUpdataAddrBean
import com.gxtc.huchuan.bean.pay.OrdersRequestBean
import com.gxtc.huchuan.bean.pay.PayBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.deal.deal.fastDeal.FastDealContract
import com.gxtc.huchuan.ui.mall.order.MallOrderDetailActivity
import com.gxtc.huchuan.ui.mine.address.AddAddressActivity
import com.gxtc.huchuan.ui.mine.address.SelectAddressActivity
import com.gxtc.huchuan.ui.pay.PayActivity
import com.gxtc.huchuan.utils.ClickUtil
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.activity_mall_order_detailed.*
import kotlinx.android.synthetic.main.layout_address.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject

/**
 * 商城订单详情页面
 */
class MallOrderDetailedActivity : BaseTitleActivity(),MallOrderDetailedContract.View, View.OnClickListener {

    private var bean: MallDetailBean? = null
    private var beans: ArrayList<MallDetailBean>? = null
    private var currAddress: AddressBean? = null
    private var addBeans: ArrayList<AddressBean>? = null

    private var totalMoney = 0.0
    private var isDefult = false
    private var isAddress = false

    private var mPresenter: MallOrderDetailedContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mall_order_detailed)
        EventBusUtil.register(this)
    }

    override fun initView() {
        baseHeadView.showTitle("订单详情")
        baseHeadView.showBackButton { finish() }
    }

    override fun initListener() {
        rl_address_btn?.setOnClickListener(this)
        btn_pay?.setOnClickListener(this)

        //设置是否需要物流地址
        switch_address.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                rl_address_btn.visibility = View.VISIBLE
                isAddress = true
            } else {
                rl_address_btn.visibility = View.GONE
                isAddress = false
            }
        })
    }

    override fun onClick(v: View?) {
        if(ClickUtil.isFastClick()) return
        when(v?.id){
            //选择地址
            R.id.rl_address_btn -> gotoAddress()

            //提交订单
            R.id.btn_pay-> gotoPay()
        }
    }

    override fun initData() {
        MallOrderDetailedPresenter(this)
        mPresenter?.getAddressList()

        fillData()
    }

    override fun showLoad() {
        if(bean == null){
            baseLoadingView.showLoading()
        }else{
            baseLoadingView.showLoading(true)
        }
    }

    override fun showAddress(beans: List<AddressBean>) {
        tv_select_address.visibility = View.INVISIBLE
        tv_name.visibility = View.VISIBLE
        tv_phone.visibility = View.VISIBLE
        tv_address.visibility = View.VISIBLE

        addBeans = ArrayList(beans)

        var flag = false
        for (i in beans.indices) {
            val bean = beans[i]

            val isDefult = bean.isdefault
            //是默认地址
            if ("1" == isDefult) {
                flag = true
                currAddress = bean
                beans[i].isSelect = true
                showBean(bean)
                break
            }
        }

        //假如没有默认地址   取第一个
        if (!flag) {
            beans[0].isSelect = true
            currAddress = beans[0]
            showBean(currAddress!!)
        }
    }

    override fun showLoadFinish() {
        baseLoadingView.hideLoading()
    }

    override fun showEmpty() {
        tv_select_address.visibility = View.VISIBLE
    }

    override fun showReLoad() {
    }

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {

    }

    override fun setPresenter(presenter: MallOrderDetailedContract.Presenter?) {
        mPresenter = presenter
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            //添加新的收货地址
            Constant.ResponseCode.ADD_ADDRESS -> getAddressList()

            //选择地址
            Constant.ResponseCode.SELECT_ADDRESS -> if (data != null) {
                addBeans = data.getSerializableExtra(Constant.INTENT_DATA) as? ArrayList<AddressBean>
                tv_select_address.visibility = View.INVISIBLE
                for (bean in addBeans!!) {
                    if (bean.isSelect) {
                        showBean(bean)
                        break
                    }
                }
            }
            Constant.ResponseCode.LOGINRESPONSE_CODE -> getAddressList()

        }
    }

    /**
     * 修改地址回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvenUpdataAddress(event: EventUpdataAddrBean) {
        val tag = event.bean
        if (tag.id == currAddress?.id || TextUtils.isEmpty(tag.id)) {
            currAddress = tag
            displayAddress(currAddress!!)
        }
    }

    @Subscribe
    fun onEvent(bean: PayBean){
        if(bean.isPaySucc){
            var orderId = bean.OrderNo
            MallOrderDetailActivity.junmpToOrderDetailActivity(this@MallOrderDetailedActivity,orderId!!)
            finish()
        }
    }

    private fun fillData() {
        bean = intent.getParcelableExtra<MallDetailBean>("data")
        if(bean != null){
            val view = View.inflate(this,R.layout.item_mall_order_detailed,null)
            val image = view.findViewById(R.id.img_head) as ImageView
            val tvGoodsName = view.findViewById(R.id.tv_title) as TextView
            val tvParam = view.findViewById(R.id.tv_param) as TextView
            val tvMoney = view.findViewById(R.id.tv_money) as TextView
            val tvCount = view.findViewById(R.id.tv_count) as TextView

            ImageHelper.loadImage(this,image,bean?.facePic)
            tvGoodsName.text = bean?.storeName
            tvParam.text = bean?.currParam?.name
            tvMoney.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(bean?.currParam?.price))}"
            tvCount.text = bean?.currParam?.sum.toString()
            layout_goods.addView(view)

            val money = StringUtil.toDouble(bean?.currParam?.price ) * bean?.currParam?.sum!!
            tv_total.text = "¥${StringUtil.formatMoney(2, money)}"
            tv_total_money.text = "¥${StringUtil.formatMoney(2, money)}"
            totalMoney = money

        }else{
            beans = intent.getParcelableArrayListExtra<MallDetailBean>("datas")
            if(beans != null){
                for(temp in beans !!){
                    val view = View.inflate(this,R.layout.item_mall_order_detailed,null)
                    val image = view.findViewById(R.id.img_head) as ImageView
                    val tvGoodsName = view.findViewById(R.id.tv_title) as TextView
                    val tvParam = view.findViewById(R.id.tv_param) as TextView
                    val tvMoney = view.findViewById(R.id.tv_money) as TextView
                    val tvCount = view.findViewById(R.id.tv_count) as TextView

                    ImageHelper.loadImage(this,image,temp.facePic)
                    tvGoodsName.text = temp.storeName
                    tvParam.text = temp.currParam?.name
                    tvCount.text = temp.currParam?.sum.toString()
                    tvMoney.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(temp.currParam?.price))}"
                    layout_goods.addView(view)

                    totalMoney += StringUtil.toDouble(temp.currParam?.price) * temp.currParam?.sum!!
                }
            }
            tv_total.text = "¥$totalMoney"
            tv_total_money.text = "¥$totalMoney"
        }
    }

    private fun showBean(bean: AddressBean) {
        val name = bean.name
        val phone = bean.phone
        var address = bean.province + bean.city
        val area = bean.area
        if (!TextUtils.isEmpty(area)) {
            address += area
        }
        address += bean.address

        //是默认地址
        val isDefult = bean.isdefault
        if ("1" == isDefult) {
            tv_is_default.visibility = View.VISIBLE
        } else {
            tv_is_default.visibility = View.INVISIBLE
        }
        tv_name.text = name
        tv_phone.text = phone
        tv_address.text = address
    }

    private fun displayAddress(bean: AddressBean) {
        val name = currAddress?.name
        val phone = currAddress?.phone
        var address = currAddress?.province + currAddress?.city
        val area = currAddress?.area
        if (!TextUtils.isEmpty(area)) {
            address += area
        }
        address += currAddress?.address

        tv_name.text = name
        tv_phone.text = phone
        tv_address.text = address

        if ("1" == currAddress?.isdefault) {
            tv_is_default.visibility = View.VISIBLE
        } else {
            tv_is_default.visibility = View.INVISIBLE
        }
    }

    private fun getAddressList() {
        if (UserManager.getInstance().isLogin(this)) {
            mPresenter?.getAddressList()
        }
    }

    private fun gotoAddress(){
        //没有收货地址
        if(addBeans == null ){
            val addIntent = Intent(this, AddAddressActivity::class.java)
            addIntent.putExtra(FastDealContract.INTENT_ISDEFULT,isDefult)
            startActivityForResult(addIntent,101)

        //选择收货地址
        }else{
            val selectIntent = Intent(this, SelectAddressActivity::class.java)
            val temp: ArrayList<AddressBean> = ArrayList<AddressBean>()
            temp.addAll(addBeans!!)
            selectIntent.putExtra(Constant.INTENT_DATA,temp)
            startActivityForResult(selectIntent,101)
        }
    }

    private fun gotoPay() {
        val jobj = JSONObject()
        if(isAddress){
            jobj.put("takeAddrId",currAddress?.id)
            jobj.put("isLogistics","1")//是否需要物流 1 要 0 不要
        }


        val msg = edit_message.text.toString()
        if(!TextUtils.isEmpty(msg)){
            jobj.put("message",msg)
        }

        val jarr = JSONArray()
        if(bean != null){
            val temp = JSONObject()
            temp.put("id",bean?.currParam?.id)
            temp.put("amount",bean?.currParam?.sum)
            jarr.put(temp)
        }else{
            for(goods in beans!!){
                val temp = JSONObject()
                temp.put("id",goods.currParam?.id)
                temp.put("amount",goods.currParam?.sum)
                jarr.put(temp)
            }
        }
        jobj.put("storePrice",jarr)

        val orderBean = OrdersRequestBean()
        orderBean.goodsName = "新媒工具"
        orderBean.token = UserManager.getInstance().token
        orderBean.transType = "SC"
        orderBean.totalPrice = (totalMoney * 100).toString()
        orderBean.extra = jobj.toString()

        PayActivity.startActivity(this,orderBean,Constant.ResponseCode.GOTO_MALL_ORDER_DETAIL,COME_FROM_MALL)
    }

    companion object {
        val COME_FROM_MALL = "1"
        @JvmStatic
        fun startActivity(context: Context, bean: MallDetailBean = MallDetailBean(-1), beans: MutableList<MallShopCartBean> = mutableListOf()){
            val intent = Intent(context, MallOrderDetailedActivity::class.java)

            if(beans.size != 0){
                val detailBeans = arrayListOf<MallDetailBean>()
                for(shopBean in beans){
                    val temp = MallDetailBean(shopBean.storeId.toInt())
                    temp.facePic = shopBean.picUrl
                    temp.storeName = shopBean.storeName

                    val param = MallDetailBean.DetailParamBean(
                            shopBean.storePriceId,
                            shopBean.storeName,
                            shopBean.storePricePrice,
                            shopBean.storeId.toInt(),
                            shopBean.amount)
                    temp.currParam = param
                    detailBeans.add(temp)
                }
                intent.putParcelableArrayListExtra("datas", detailBeans)

            }else{
                intent.putExtra("data", bean)
            }

            context.startActivity(intent)
        }

    }


}
