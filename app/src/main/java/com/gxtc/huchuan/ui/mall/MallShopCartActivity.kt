package com.gxtc.huchuan.ui.mall

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallShopCartAdapter
import com.gxtc.huchuan.bean.pay.PayBean
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.activity_mall_shop_cart.*
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*
import org.greenrobot.eventbus.Subscribe
import java.math.BigDecimal

/**
 * 购物车页面
 */
class MallShopCartActivity : BaseTitleActivity(),MallShopCartContract.View, View.OnClickListener {

    private var mPresenter: MallShopCartContract.Presenter ? = null
    private var mAdapter: MallShopCartAdapter? = null

    private var total = 0.0               //总价格
    private var selectCount = 0          //选中的商品数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mall_shop_cart)
        EventBusUtil.register(this)
    }

    override fun initView() {
        baseHeadView.showTitle("购物车")
        baseHeadView.showBackButton { finish() }

        swipeLayout?.setColorSchemeColors(*Constant.REFRESH_COLOR)

        mAdapter = MallShopCartAdapter(this, mutableListOf(),R.layout.item_mall_shop_cart)
        recyclerview?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerview?.adapter = mAdapter

        tv_total?.text = "¥${StringUtil.formatMoney(2,total.toString())}"
    }

    override fun initListener() {
        checkBoxAll?.setOnClickListener(this)
        btn_pay?.setOnClickListener(this)

        swipeLayout?.setOnRefreshListener {
            recyclerview?.reLoadFinish()
            mPresenter?.refreshData()
        }

        mAdapter?.clickListener = this
        mAdapter?.setOnReItemOnClickListener { _, position ->
            MallDetailedActivity.startActivity(this,mAdapter?.list?.get(position)?.storeId!!)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            //全选 或 取消 全选
            R.id.checkBoxAll-> clickAll(v)

            //选中或取消了某个商品
            R.id.radio_btn-> clickGoods(v)

            //结算
            R.id.btn_pay-> pay()

            //删除商品
            R.id.tv_delete->{
                baseLoadingView.showLoading(true)
                mPresenter?.removeGoods((v.tag as MallShopCartBean).id)
            }

            //商品
            R.id.content -> MallDetailedActivity.startActivity(this,(v.tag as MallShopCartBean).storeId)
        }
    }

    override fun initData() {
        hideContentView()
        MallShopCartPresenter(this)
        mPresenter?.getData()
    }

    override fun showData(datas: MutableList<MallShopCartBean>) {
        showContentView()
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showRefreshData(datas: MutableList<MallShopCartBean>?) {
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showRemoveSuccess(id: String) {
        for(i in 0 until mAdapter?.list?.size!!){
            if(id == mAdapter?.list?.get(i)?.id){

                val bean = mAdapter?.list?.get(i)
                bean?.let {
                    if(it.isSelect){
                        val priceB = BigDecimal(StringUtil.toDouble(it.storePricePrice)).multiply(BigDecimal(it.amount)).setScale(2,BigDecimal.ROUND_HALF_UP)
                        val totalB = BigDecimal(total).subtract(priceB).setScale(2,BigDecimal.ROUND_HALF_UP)
                        total = totalB.toDouble()
                        tv_total.text = "¥${ StringUtil.formatMoney(2,total) }"
                        btn_pay.text = "结算(${ --selectCount })"
                    }
                }
                recyclerview?.removeData(mAdapter,i)
                break
            }
        }
        checkAll()
    }

    override fun showNoLoadMore() {
        recyclerview?.loadFinish()
    }

    override fun setPresenter(presenter: MallShopCartContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView?.showLoading()
    }

    override fun showLoadFinish() {
        swipeLayout?.isRefreshing = false
        baseLoadingView?.hideLoading()
    }

    override fun showEmpty() {
        showContentView()
        baseEmptyView?.showEmptyContent()
    }

    override fun showReLoad() {}

    override fun showLoadMoreData(datas: MutableList<MallShopCartBean>?) {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
        }
    }

    @Subscribe
    fun onEvent(bean: PayBean){
        if(bean.isPaySucc){
            val temp = mutableListOf<MallShopCartBean>()
            val tempB = BigDecimal(total)
            for(shopBean in mAdapter?.list!!){
                if(shopBean.isSelect){
                    temp.add(shopBean)
                    tempB.add(BigDecimal(StringUtil.toDouble(shopBean.storePricePrice))).setScale(2,BigDecimal.ROUND_HALF_UP)
                    --selectCount
                }
            }
            total = BigDecimal(total).subtract(tempB).setScale(2,BigDecimal.ROUND_HALF_UP).toDouble()
            tv_total.text = "¥${ StringUtil.formatMoney(2,total) }"
            btn_pay.text = "结算($selectCount)"
            mAdapter?.list?.removeAll(temp.toTypedArray())
            recyclerview?.notifyChangeData()
            checkAll()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        EventBusUtil.unregister(this)
    }


    //结算
    private fun pay() {
        if(selectCount != 0){
            val list = arrayListOf<MallShopCartBean>()
            for(bean in mAdapter?.list!!){
                if(bean.isSelect){
                    list.add(bean)
                }
            }
            MallOrderDetailedActivity.startActivity(this,beans = list)

        }else{
            ToastUtil.showShort(this,"您还没有选择商品!")
        }
    }

    //选中或取消了某个商品
    private fun clickGoods(v: View){
        val tag = v.tag as MallShopCartBean
        if(tag.isSelect){
            total += StringUtil.toDouble(tag.storePricePrice) * tag.amount
            tv_total.text = "¥${ StringUtil.formatMoney(2,total) }"
            btn_pay.text = "结算(${ ++selectCount })"
        }else{
            total -= StringUtil.toDouble(tag.storePricePrice) * tag.amount
            tv_total.text = "¥${ StringUtil.formatMoney(2,total) }"
            btn_pay.text = "结算(${ --selectCount })"
        }
        checkAll()
    }

    //全选或者取消全选
    private fun clickAll(v: View) {
        if(mAdapter?.list?.size == 0){
            checkBoxAll.isChecked = false
            return
        }

        if(checkBoxAll.isChecked){
            for(bean in mAdapter?.list!!){
                if(!bean.isSelect){
                    bean.isSelect = true
                    selectCount++
                    total += StringUtil.toDouble(bean.storePricePrice) * bean.amount
                }
            }
            recyclerview?.notifyChangeData()
            tv_total.text = "¥${ StringUtil.formatMoney(2,total) }"
            btn_pay.text = "结算(${ selectCount })"

        }else{
            for(bean in mAdapter?.list!!){
                bean.isSelect = false
            }

            recyclerview?.notifyChangeData()
            total = 0.0
            selectCount = 0
            tv_total.text = "¥0"
            btn_pay.text = "结算(0)"
        }
    }

    private fun checkAll(){
        var count = 0
        for(bean in mAdapter?.list!!){
            if(bean.isSelect){
                count ++
            }
        }
        checkBoxAll.isChecked = count != 0 && count == mAdapter?.list!!.size
    }

}
