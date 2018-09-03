package com.gxtc.huchuan.ui.deal.guarantee

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.GoodsDetailedBean
import com.gxtc.huchuan.bean.event.EventDealBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.common.CommonPhotoViewActivity
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.activity_guarantee_detailed.*
import org.greenrobot.eventbus.Subscribe

/**
 * 担保交易 接收页面
 * 伍玉南
 */
class GuaranteeDetailedActivity : BaseTitleActivity(), GuaranteeDetailedContract.View, View.OnClickListener {

    private var mPresenter: GuaranteeDetailedContract.Presenter ? = null

    private var infoId : String ? = null
    private var goodsBean : GoodsDetailedBean ? = null
    private var isBuyer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guarantee_detailed)
    }


    override fun initView() {
        baseHeadView.showTitle(getString(R.string.title_guarantee_detailed)).showBackButton { finish() }
    }


    override fun initListener() {
        btn_cancel.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        btn_go_order.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_cancel-> disagreen()

            R.id.btn_ok -> agreen()

            R.id.img_head, R.id.tv_name -> goodsBean?.let { PersonalInfoActivity.startActivity(this, goodsBean?.targetUserCode) }

            R.id.btn_go_order -> {
                if(goodsBean?.fastTradeState == 1){
                    gotoOrderDetailed()
                }
            }
        }
    }


    override fun initData() {
        GuaranteeDetailedPresenter(this)
        infoId = intent?.getStringExtra(Constant.INTENT_DATA)
        infoId?.let {
            hideContentView()
            mPresenter?.getData(infoId!!)
        }
    }


    override fun showData(goodsBean: GoodsDetailedBean) {
        this.goodsBean = goodsBean
        showContentView()

        tv_title.text = goodsBean.title
        tv_content.text = goodsBean.content
        tv_name.text = goodsBean.userName
        tv_price.text = "价格: ¥${goodsBean.price}"
        tv_name.setOnClickListener(this)
        img_head.setOnClickListener(this)
        ImageHelper.loadCircle(this, img_head, goodsBean.userPic)

        val price = StringUtil.toDouble(goodsBean.price)
        var dbfee = price * Constant.GUARANTEE_SCALE

        val role = goodsBean.buyer    //0：买家，1：卖家
        val sendUserCode = goodsBean.userCode
        val myUserCode = UserManager.getInstance().userCode

        val isMaster = sendUserCode == myUserCode
        isBuyer = false
        if(goodsBean.buyer == 0){
            isBuyer = isMaster
        }else if(goodsBean.buyer == 1){
            isBuyer = !isMaster
        }

        if(goodsBean.buyWay == 0){
            tv_dbfee?.text = "担保费: ¥${ if(isBuyer) StringUtil.formatMoney(2, dbfee) else 0 }"

        }else if(goodsBean.buyWay == 1){
            tv_dbfee?.text = "担保费: ¥${ if(isBuyer) 0 else StringUtil.formatMoney(2, dbfee) }"

        }else {
            dbfee /= 2
            tv_dbfee?.text = "担保费: ¥${ StringUtil.formatMoney(2, dbfee) }"
        }


        val images = goodsBean.picUrlList
        if(images != null && images.isNotEmpty()){
            layout_images.removeAllViews()
            val width = (layout_images.width - resources.getDimension(R.dimen.margin_tiny) * 4) / 5
            for(i in images.indices){
                val image = images[i]
                val imageView = ImageView(this)
                layout_images.addView(imageView)
                val param = imageView.layoutParams as? LinearLayout.LayoutParams
                param?.width = width.toInt()
                param?.height = width.toInt()
                param?.marginEnd = resources.getDimension(R.dimen.margin_tiny).toInt()
                ImageHelper.loadImage(this, imageView, image.trim())

                imageView.setTag(R.id.tag_first, i)
                imageView.setOnClickListener { v ->
                    val index = v.getTag(R.id.tag_first) as Int
                    val uris = arrayListOf<Uri>()
                    this@GuaranteeDetailedActivity.goodsBean?.picUrlList?.mapTo(uris){
                        Uri.parse(it)
                    }
                    CommonPhotoViewActivity.startActivity(this@GuaranteeDetailedActivity, uris, index)
                }
            }
        }

        //已同意交易
        if(goodsBean.fastTradeState == 1){
            layout_choose.visibility = View.GONE
            btn_go_order.visibility = View.VISIBLE
            btn_go_order.isEnabled = true
            btn_go_order.setTextColor(resources.getColor(R.color.white))
            btn_go_order.text = getString(R.string.guarantee_look_order)

        //未同意交易
        }else if(goodsBean.fastTradeState == 0){
            if(isMaster){
                layout_choose.visibility = View.GONE
                btn_go_order.visibility = View.VISIBLE
                btn_go_order.isEnabled = false
                btn_go_order.setTextColor(resources.getColor(R.color.tint))
                btn_go_order.text = getString(R.string.guarantee_wait_agree)

            }else{
                layout_choose.visibility = View.VISIBLE
                btn_go_order.visibility = View.GONE
            }

        //不同意交易
        }else{
            layout_choose.visibility = View.GONE
            btn_go_order.visibility = View.GONE
        }
    }


    private fun agreen() {
        if(goodsBean == null)   return
        val map = hashMapOf<String, String>()
        map.put("tradeInfoId", goodsBean!!.id.toString())
        map.put("buyWay", goodsBean!!.buyWay.toString())
        map.put("isPost", "0")
        map.put("buyNum", "1")
        map.put("tradeSource", goodsBean!!.createSource)
        UserManager.getInstance().token?.let { map.put("token", it) }
        goodsBean?.tradeType?.let { map.put("tradeType", it.toString()) }
        mPresenter?.agreeDeal(map)
    }

    override fun showAgreen(data: GoodsDetailedBean) {
        infoId?.let { mPresenter?.getData(it) }
    }

    private fun disagreen() {
        infoId?.let { mPresenter?.disagreenDeal(it) }
    }

    override fun showDisargreen() {
        layout_choose.visibility = View.GONE
        btn_go_order.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        EventBusUtil.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusUtil.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    @Subscribe
    fun onEvent(bean: EventDealBean){
        mPresenter?.getData(infoId!!)
    }

    private fun gotoOrderDetailed(){
        goodsBean?.let {
            if(isBuyer){
                OrderDetailedBuyerActivity.startActivity(this, it.tradeOrderId)
            }else{
                OrderDetailedActivity.startActivity(this, it.tradeOrderId)
            }
        }
    }


    override fun tokenOverdue() = GotoUtil.goToActivity(this, LoginAndRegisteActivity::class.java)

    override fun setPresenter(presenter: GuaranteeDetailedContract.Presenter?) { mPresenter = presenter }

    override fun showLoad() = baseLoadingView.showLoading()

    override fun showLoadFinish() = baseLoadingView.hideLoading()

    override fun showEmpty() = baseEmptyView.showEmptyContent("无法获取交易信息")

    override fun showReLoad() = Unit

    override fun showError(info: String?) = ToastUtil.showShort(this, info)

    override fun showNetError() = baseEmptyView.showNetWorkView { baseEmptyView.hideEmptyView() }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, tradeId: String){
            val intent = Intent(context, GuaranteeDetailedActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA, tradeId)
            context.startActivity(intent)
        }
    }

}
