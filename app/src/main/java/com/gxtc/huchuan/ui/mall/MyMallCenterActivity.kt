package com.gxtc.huchuan.ui.mall

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mall.order.MallCustermersContract
import com.gxtc.huchuan.ui.mall.order.MallCustomersPresemter
import com.gxtc.huchuan.ui.mall.order.MallOrderStatusListActivity
import kotlinx.android.synthetic.main.maill_order_tab.*
import kotlinx.android.synthetic.main.mall_bottom_layout.*
import kotlinx.android.synthetic.main.mall_layout_middle_tab.*

/**
 * Created by zzg on 2017/10/24.
 */
class MyMallCenterActivity : BaseTitleActivity(),View.OnClickListener, MallCustermersContract.View {
    var mPrsenter: MallCustermersContract.Prensenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_mall)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("商城订单").showBackButton{
            finish()
        }
        onClick(mall_wode)

    }

    override fun initData() {
        super.initData()
        MallCustomersPresemter(this)
        baseLoadingView.showLoading()
        onClick(mall_wode)
        mPrsenter!!.getAllStatusSums(UserManager.getInstance().token)
    }

    override fun initListener() {
        super.initListener()
        tv_to_pay.setOnClickListener(this)
        tv_to_sent.setOnClickListener(this)
        tv_sended.setOnClickListener(this)
        tv_finished.setOnClickListener(this)
        tv_my_shopcar.setOnClickListener(this)
        tv_all_order.setOnClickListener(this)
        mall_shouyel.setOnClickListener(this)
        mall_fenlei.setOnClickListener(this)
        mall_huodong.setOnClickListener(this)
        mall_wode.setOnClickListener(this)
    }

    //0:已取消 1:待付款 2:待发货 3:已发货 4：已退款 5：已完成
    override fun onClick(v: View?) {
        when(v?.id){
            //待付款
            R.id.tv_to_pay -> {
                MallOrderStatusListActivity.junmpToOrderStatusActivity(this,"待付款","1",100)
            }
            //待发货
            R.id.tv_to_sent -> {
                MallOrderStatusListActivity.junmpToOrderStatusActivity(this,"待发货","2",200)
            }
            //已发货
            R.id.tv_sended -> {
                MallOrderStatusListActivity. junmpToOrderStatusActivity(this,"已发货","3",300)
            }
            //已完成
            R.id.tv_finished -> {
                MallOrderStatusListActivity.junmpToOrderStatusActivity(this,"已完成","5",500)
            }
            //我的购物车
            R.id.tv_my_shopcar -> {
                GotoUtil.goToActivity(this,MallShopCartActivity::class.java)
            }
            //全部订单
            R.id.tv_all_order -> {
                MallOrderStatusListActivity.junmpToOrderStatusActivity(this,"全部订单","",1001)
            }
            //首页
            R.id.mall_shouyel -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_shouye_selected)
                GotoUtil.goToActivity(this, MallActivity::class.java)
            }
            //分类
            R.id.mall_fenlei -> {
               MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
               MallHandleUtil.setSelected(this,v,R.drawable.icon_fenlei_selected)
               mPrsenter?.getTags("")
            }
            //活动
            R.id.mall_huodong -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_huodong_selected)
                mPrsenter?.getActivitysData("",0)
            }
            //我的
            R.id.mall_wode -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_wode_selected)
            }
        }
    }

    override fun showCustermersList(datas: ArrayList<CoustomMerBean>) {}

    //0:已取消 1:未支付 2:待发货 3:待收货 4：待评价 5：已结束
    override fun showAllStatusSums(data: ArrayList<AllStatusSumsBean>) {
        baseLoadingView.hideLoading()
        if(data == null) return
        for(i in 0 until data.size ) {
            when (data.get(i).status) {
                "1" -> {
                    if (data.get(i).sum > 0) {
                        doc1.visibility = View.VISIBLE
                        doc1.setText(data.get(i).sum.toString())
                    } else {
                        doc1.visibility = View.INVISIBLE
                    }
                }
                "2" -> {
                    if (data.get(i).sum > 0) {
                        doc2.visibility = View.VISIBLE
                        doc2.setText(data.get(i).sum.toString())
                    } else {
                        doc2.visibility = View.INVISIBLE
                    }
                }
                "3" -> {
                    if (data.get(i).sum > 0) {
                        doc3.visibility = View.VISIBLE
                        doc3.setText(data.get(i).sum.toString())
                    } else {
                        doc3.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun setPresenter(presenter: MallCustermersContract.Prensenter?) {
        mPrsenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {}

    override fun showReLoad() {}

    override fun showError(info: String?) {
        baseLoadingView.hideLoading()
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK) return
        val sum = data?.getIntExtra("sum",0)
        when(requestCode){
            100 ->{
                if (sum!! > 0) {
                    doc1.visibility = View.VISIBLE
                    doc1.setText(sum.toString())
                } else {
                    doc1.visibility = View.INVISIBLE
                }
            }
            200 ->{
                if (sum!! > 0) {
                    doc2.visibility = View.VISIBLE
                    doc2.setText(sum.toString())
                } else {
                    doc2.visibility = View.INVISIBLE
                }
            }
            300 ->{
                if (sum!! > 0) {
                    doc3.visibility = View.VISIBLE
                    doc3.setText(sum.toString())
                } else {
                    doc3.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun showHeadIcon(datas: List<MallBean>) {
        if(datas != null && datas.size > 0)
        MallHandleUtil.showPop(this,mall_fenlei,datas,R.drawable.shop_pop_bg, Gravity.TOP)
    }

    override fun showActivitysData(datas: List<MallBean>) {
        if(datas != null && datas.size > 0)
        MallHandleUtil.handlerByType(this,datas,0)
    }
}
