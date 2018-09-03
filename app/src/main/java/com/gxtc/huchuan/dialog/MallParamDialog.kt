package com.gxtc.huchuan.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.utils.StringUtil
import com.gxtc.huchuan.widget.FlowLayout
import com.gxtc.huchuan.widget.NumberAddandSubView

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 * 商城商品参数选择dialog
 */
class MallParamDialog: BaseDialogFragment(), View.OnClickListener {

    private var imgClose: ImageView? = null
    private var imgHead: ImageView? = null
    private var btn: Button ? = null
    private var tvTitle: TextView ? = null
    private var tvMoney: TextView ? = null
    private var tvCount: TextView ? = null
    private var flowlayout: FlowLayout ? = null
    private var numberView: NumberAddandSubView ? = null

    private var isFromHome = false
    private var bean: MallDetailBean? = null

    var dialogListener: MallParamListener ? = null
    var paramChangeListener: MallParamChangeListener ? = null
    var param: MallDetailBean.DetailParamBean ? = null  //选中的款式参数
    var selectCount = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        gravity = Gravity.BOTTOM
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_mall_param,null)
        imgClose = view.findViewById(R.id.img_close) as ImageView
        imgHead = view.findViewById(R.id.img_head) as ImageView
        btn = view.findViewById(R.id.btn) as Button
        tvTitle = view.findViewById(R.id.tv_title) as TextView
        tvMoney = view.findViewById(R.id.tv_money) as TextView
        tvCount = view.findViewById(R.id.tv_count) as TextView
        flowlayout = view.findViewById(R.id.flowlayout) as FlowLayout
        numberView = view.findViewById(R.id.view_num) as NumberAddandSubView

        if(!isFromHome)
            btn?.text = "立即购买"
        else
            btn?.text = "加入购物车"

        tvTitle?.text = bean?.storeName
        tvMoney?.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(bean?.price))}"
        ImageHelper.loadImage(context,imgHead,bean?.facePic)

        for(param in bean?.priceList!!){
            if(!param.name.isNullOrEmpty()){
                val tv = TextView(context)
                val topPadding = WindowUtil.dip2px(context, 4f)
                val leftPadding = WindowUtil.dip2px(context,8f)
                tv.setPadding(leftPadding,topPadding,leftPadding,topPadding)
                tv.setBackgroundResource(R.drawable.selector_mall_param)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,context!!.resources.getDimension(R.dimen.textSize_s))
                tv.setTextColor(context!!.resources.getColor(R.color.text_color_666))
                tv.text = param.name
                tv.tag = param
                tv.setOnClickListener { v ->
                    changeStatus(v)
                    paramChangeListener?.onParamChange(v.tag as MallDetailBean.DetailParamBean)
                }

                val layoutParam = ViewGroup.MarginLayoutParams(-2,-2)
                layoutParam.rightMargin = WindowUtil.dip2px(context!!,4f)
                layoutParam.topMargin = WindowUtil.dip2px(context!!,4f)
                flowlayout?.addView(tv,layoutParam)

                if(this.param != null){
                    if(this.param?.id == param.id){
                        changeStatus(tv)
                    }
                }
            }
        }

        if(flowlayout!!.childCount > 0) //默认第一项被选
        changeStatus(flowlayout!!.getChildAt(0))
        return view
    }

    override fun initListener() {
        imgClose?.setOnClickListener(this)
        btn?.setOnClickListener(this)
        numberView?.setOnNumChangeListener { _, num ->
            selectCount = num
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.img_close -> dismiss()

            R.id.btn -> {
                if(param != null){
                    dialogListener?.onPayClick(this,bean!!,v)
                    dismiss()
                }else{
                    ToastUtil.showShort(context,"请选择商品款式")
                }
            }
        }
    }


    fun show(manager: FragmentManager?, tag: String?, bean: MallDetailBean, isFromHome: Boolean = false) {
        super.show(manager, tag)
        this.isFromHome = isFromHome
        this.bean = bean
    }


    //改变选中的状态
    private fun changeStatus(targetView: View){
        val target = targetView.tag as MallDetailBean.DetailParamBean
        this.param = target
        for(i in 0 until flowlayout?.childCount!!){
            val view = flowlayout?.getChildAt(i) as TextView
            view.isSelected = false
            view.setTextColor(context!!.resources.getColor(R.color.text_color_666))

            val tag = view.tag as MallDetailBean.DetailParamBean
            if(tag.id == target.id){
                view.isSelected = true
                view.setTextColor(context!!.resources.getColor(R.color.white))

                tvCount?.visibility = View.VISIBLE
                tvCount?.text = "剩余数量：${target.sum}"
                tvMoney?.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(param?.price))}"

                numberView?.num = 1
                numberView?.max = target.sum
            }
        }
    }

    interface MallParamListener{
        fun onPayClick(dialog: MallParamDialog, bean: MallDetailBean, view: View)
    }

    interface MallParamChangeListener{
        fun onParamChange(param: MallDetailBean.DetailParamBean)
    }

    class Builder {

        private var storeId = 0
        private var price = ""
        private var goodsName = ""
        private var cover = ""
        private var priceList = arrayListOf<MallDetailBean.DetailParamBean>()

        fun setStoreId(storeId: Int): Builder {
            this.storeId = storeId
            return this
        }

        fun setGoodsName(goodsName: String): Builder {
            this.goodsName = goodsName
            return this
        }

        fun setPrice(price: String): Builder {
            this.price = price
            return this
        }

        fun setCover(cover: String): Builder {
            this.cover = cover
            return this
        }

        fun setPriceList(priceList: ArrayList<MallBean.PriceList>): Builder {
            for(bean in priceList){
                val param = MallDetailBean.DetailParamBean(bean.id,bean.name,bean.price,bean.storeId,bean.sum)
                this.priceList.add(param)
            }
            return this
        }

        fun builde(): MallDetailBean {
            val bean = MallDetailBean(this.storeId)
            bean.price = this.price
            bean.storeName = this.goodsName
            bean.facePic = this.cover
            bean.priceList = this.priceList
            return bean
        }

    }

}