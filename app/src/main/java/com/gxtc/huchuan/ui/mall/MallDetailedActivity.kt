package com.gxtc.huchuan.ui.mall

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.flyco.dialog.widget.popup.BubblePopup
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.bean.event.EventSelectFriendBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.MallParamDialog
import com.gxtc.huchuan.dialog.ShareDialog
import com.gxtc.huchuan.handler.CircleShareHandler
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.im.ui.ConversationListActivity
import com.gxtc.huchuan.pop.PopEnterAnim
import com.gxtc.huchuan.pop.PopExitAnim
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity
import com.gxtc.huchuan.ui.mall.order.MallCustomersActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.utils.*
import com.umeng.socialize.bean.SHARE_MEDIA
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_mall_detailed.*
import kotlinx.android.synthetic.main.layout_mall_detailed_content.*
import java.util.*

/**
 * 商城的商品详情页面
 */
class MallDetailedActivity : BaseTitleActivity(), MallDetailedContract.View, View.OnClickListener {

    private var id = ""
    private var bean: MallDetailBean? = null
    private var defaultChoose: Boolean = true
    private var paramBean: MallDetailBean.DetailParamBean? = null

    private var mallDialog: MallParamDialog? = null
    private var mBubblePopup: BubblePopup? = null
    private var mAlertDialog: AlertDialog? = null
    private var mPresenter: MallDetailedContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mall_detailed)
    }


    override fun initView() {
        baseHeadView.showTitle("营销工具")
        baseHeadView.showBackButton { finish() }
        baseHeadView.showHeadRightImageButton(R.drawable.navigation_icon_share, this)

        tv_param?.setOnClickListener(this)
        btn_pay?.setOnClickListener(this)
        btn_join_cart?.setOnClickListener(this)
        btn_shop_cart?.setOnClickListener(this)

        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.cacheMode = WebSettings.LOAD_DEFAULT  //设置 缓存模式
        webView?.settings?.domStorageEnabled = false             //开启 DOM storage API 功能
        webView?.settings?.databaseEnabled = false               //开启 database storage API 功能
        webView?.settings?.setAppCacheEnabled(false)             //开启 Application Caches 功能
        webView?.settings?.blockNetworkImage = true
        webView?.addJavascriptInterface(this, "recommend")
        webView?.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView?.settings?.blockNetworkImage = false
            }
        })
    }


    override fun onClick(v: View?) {
        if (ClickUtil.isFastClick()) return
        when (v?.id) {
            //选择款式
            R.id.tv_param -> showParamDialog()

            //购物车
            R.id.btn_shop_cart -> {
                if(UserManager.getInstance().isLogin){
                    showPop(v)
                }else{
                    GotoUtil.goToActivity(this,LoginAndRegisteActivity::class.java)
                }
            }

            //客服
            R.id.btn_join_cart -> {
                MallCustomersActivity.goToCustomerServicesActivity(this, MallCustomersActivity.CUSTOMERS_TYPE_OF_MALL, MallCustomersActivity.CUSTOMERS_STATUS_SHOW_LIST)
            }

            //立即购买
            R.id.btn_pay -> {
                if(UserManager.getInstance().isLogin){
                    if(mallDialog == null){
                        showParamDialog()
                    }else{
                        gotoPay()
                    }
                }else{
                    GotoUtil.goToActivity(this,LoginAndRegisteActivity::class.java)
                }
            }

            //分享
            R.id.HeadRightImageButton -> {
                if(UserManager.getInstance().isLogin){
                    showShareDialog()
                }else{
                    GotoUtil.goToActivity(this,LoginAndRegisteActivity::class.java)
                }
            }
        }
    }


    override fun initData() {
        MallDetailedPresenter(this)
        id = intent.getStringExtra(Constant.INTENT_DATA)
        if (!TextUtils.isEmpty(id)) {
            mPresenter?.getGoodsDetailed(id)
        }
    }


    override fun showGoodsDetailed(bean: MallDetailBean) {
        this.bean = bean
        content_layout?.visibility = View.VISIBLE

        tv_title?.text = bean.storeName
        tv_money?.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(bean.price))}"
        webView?.loadUrl(bean.descUrl + "android")

        ImageHelper.loadImage(this, img_head, bean.facePic)

        if(bean.priceList != null && bean.priceList?.size!! > 0){
            paramBean = bean.priceList?.get(0)
            setParamBean(paramBean)
        }
    }


    override fun showAddShopCartResult(datas: Any) {
        ToastUtil.showShort(this, "加入购物车成功")
    }


    override fun showCollectResult() {
        bean?.let {
            if(it.isCollect == 0){
                it.isCollect = 1
                ToastUtil.showShort(this, "收藏成功")
            }else{
                it.isCollect = 0
                ToastUtil.showShort(this, "取消收藏")
            }
        }
    }


    override fun setPresenter(presenter: MallDetailedContract.Presenter?) {
        mPresenter = presenter
    }


    override fun showLoad() = baseLoadingView.showLoading()

    override fun showLoadFinish() = baseLoadingView.hideLoading()

    override fun showEmpty() = baseEmptyView.showEmptyContent()

    override fun showReLoad() {}

    override fun showError(info: String?) = ToastUtil.showShort(this, info)


    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter?.getGoodsDetailed(id)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ConversationActivity.REQUEST_SHARE_CONTENT && resultCode == Activity.RESULT_OK && data != null) {
            val bean = data.getParcelableExtra<EventSelectFriendBean>(Constant.INTENT_DATA)
            val targetId = bean.targetId
            val type = bean.mType
            shareMessage(targetId, type,bean.liuyan)
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        webView?.let {
            it.setWebChromeClient(null)
            it.setWebViewClient(null)
            it.removeJavascriptInterface("callBack")

            val parent = webView.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(webView)
            }
            it.removeAllViews()
            it.destroy()
        }

    }


    private fun showPop(v: View?) {
        if (mBubblePopup != null && mBubblePopup!!.isShowing()) {
            mBubblePopup!!.dismiss()
        }

        val inflate = View.inflate(this, R.layout.pop_mall_coustomer, null)
        inflate.findViewById<View>(R.id.tv_my_shopcart).setOnClickListener(View.OnClickListener {
            if (mBubblePopup != null && mBubblePopup?.isShowing!!) {
                if (UserManager.getInstance().isLogin(this))
                    GotoUtil.goToActivity(this, MallShopCartActivity::class.java)
                else
                    GotoUtil.goToActivity(this, LoginAndRegisteActivity::class.java)
                mBubblePopup?.dismiss()
            }

        })

        inflate.findViewById<View>(R.id.tv_add_shop_cart).setOnClickListener(View.OnClickListener {
            if (mBubblePopup != null && mBubblePopup?.isShowing!!) {
                if (UserManager.getInstance().isLogin(this))
                    showParamDialog(true)
                else
                    GotoUtil.goToActivity(this, LoginAndRegisteActivity::class.java)
                mBubblePopup!!.dismiss()
            }

        })


        mBubblePopup = BubblePopup(this, inflate)

        mBubblePopup?.let {
            it.anchorView(v)
                    .location(0,5)
                    .showAnim(PopEnterAnim()
                    .duration(200))
                    .dismissAnim(PopExitAnim()
                    .duration(200))
                    .gravity(Gravity.TOP)
                    .bubbleColor(Color.parseColor("#ffffff"))
                    .show()

            val param = it.window.attributes as WindowManager.LayoutParams
            param.dimAmount = 0.1f
            it.dimEnabled(true)
        }
    }


    //分享到好友
    private fun shareMessage(targetId: String?, type: Conversation.ConversationType?, liuyan: String?) {
        val title = bean?.storeName
        val img = bean?.facePic
        val id = bean?.storeId?.toString()
        val infoType = CircleShareHandler.SHARE_MALL
        ImMessageUtils.shareMessage(targetId, type, id, title, img, infoType, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) {}

            override fun onSuccess(message: Message) {
                ToastUtil.showShort(this@MallDetailedActivity, "分享成功")
                 if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.relayMessage(liuyan,targetId,type);
                 }
            }

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(this@MallDetailedActivity, "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }


    //立即购买
    private fun gotoPay() {
        if (paramBean == null) {
            showParamDialog()
            return
        }
        if(mallDialog != null)
        paramBean?.sum = mallDialog?.selectCount!!
        bean?.currParam = paramBean
        MallOrderDetailedActivity.startActivity(this, bean!!)
    }


    //选择参数弹窗
    private fun showParamDialog(joinShopCar: Boolean = false) {
        if (mallDialog == null) {
            mallDialog = MallParamDialog()
        }
        mallDialog?.param = paramBean
        mallDialog?.show(supportFragmentManager, MallParamDialog::class.java.simpleName, bean!!, joinShopCar)

        mallDialog?.dialogListener = object : MallParamDialog.MallParamListener {
            override fun onPayClick(dialog: MallParamDialog, bean: MallDetailBean, view: View) {
                val tv = view as? TextView
                //加入购物车
                if (tv?.text == "加入购物车") {
                    baseLoadingView?.showLoading(true)
                    val map = HashMap<String, String>()
                    map.put("token", UserManager.getInstance().token)
                    map.put("storePriceId", mallDialog?.param?.id?.toString()!!)
                    map.put("amount", mallDialog?.selectCount.toString() + "")
                    mPresenter?.addShopCar(map)

                //立即购买
                } else {
                    //默认选择第一个
                    if(!UserManager.getInstance().isLogin(this@MallDetailedActivity)){
                        return
                    }
                    if (defaultChoose) {
                        if (bean.priceList != null && bean.priceList?.size!! > 0) {
                            this@MallDetailedActivity.paramBean = bean.priceList?.get(0)
                            setParamBean(bean.priceList?.get(0))
                        }
                    }
                    gotoPay()
                }
            }
        }
        mallDialog?.paramChangeListener = object : MallParamDialog.MallParamChangeListener {
            override fun onParamChange(param: MallDetailBean.DetailParamBean) {
                this@MallDetailedActivity.paramBean = param
                defaultChoose = false
                setParamBean(param)
            }
        }
    }


    private fun setParamBean(param: MallDetailBean.DetailParamBean ?) {
        tv_param?.text = "选择: ${param?.name}"
        tv_money?.text = "¥${StringUtil.formatMoney(2, StringUtil.toDouble(param?.price))}"
        tv_count?.text = "剩余：${param?.sum}"
    }


    private fun showShareDialog() {
        val pers = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200, object : PermissionsResultListener {

            override fun onPermissionGranted() {
                bean?.let {
                    val actions = arrayOf(
                            ShareDialog.Action(ShareDialog.ACTION_QRCODE, R.drawable.share_icon_erweima, null),
                            ShareDialog.Action(ShareDialog.ACTION_COLLECT, R.drawable.share_icon_collect, null),
                            ShareDialog.Action(ShareDialog.ACTION_COPY, R.drawable.share_icon_copy, null),
                            ShareDialog.Action(ShareDialog.ACTION_CIRCLE, R.drawable.share_icon_my_dynamic, null),
                            ShareDialog.Action(ShareDialog.ACTION_FRIENDS, R.drawable.share_icon_friends, null))

                    val utils = UMShareUtils(this@MallDetailedActivity)
                    utils.shareCustom(it.facePic, it.storeName, "新媒之家软件商城", it.descUrl, actions, object : ShareDialog.OnShareLisntener{
                        override fun onShare(key: String?, media: SHARE_MEDIA?) {
                            when(key){
                                //分享动态
                                ShareDialog.ACTION_CIRCLE -> IssueDynamicActivity.share(this@MallDetailedActivity, it.storeId?.toString(), "7", it.storeName, it.facePic)

                                //分享好友
                                ShareDialog.ACTION_FRIENDS -> ConversationListActivity.startActivity(this@MallDetailedActivity, ConversationActivity.REQUEST_SHARE_CONTENT, Constant.SELECT_TYPE_SHARE)

                                //收藏
                                ShareDialog.ACTION_COLLECT -> mPresenter?.collectMall(it.storeId.toString())

                                //分享二维码
                                ShareDialog.ACTION_QRCODE -> ErWeiCodeActivity.startActivity(this@MallDetailedActivity, ErWeiCodeActivity.TYPE_MALL_GOODS, bean?.storeId!!, "")
                            }
                        }
                    })
                }
            }

            override fun onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(this@MallDetailedActivity, false, null, getString(R.string.pre_scan_notice_msg)) { v ->
                    if (v.id == R.id.tv_dialog_confirm) {
                        JumpPermissionManagement.GoToSetting(this@MallDetailedActivity)
                    }
                    mAlertDialog?.dismiss()
                }
            }
        })
    }


    /**
     * webview js交互的回调
     */

    @JavascriptInterface
    fun recommendNews(id: String) {
        MallDetailedActivity.startActivity(this, id)
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: String) {
                val intent = Intent(context, MallDetailedActivity::class.java)
                intent.putExtra(Constant.INTENT_DATA, id)
                context.startActivity(intent)
        }
    }

}
