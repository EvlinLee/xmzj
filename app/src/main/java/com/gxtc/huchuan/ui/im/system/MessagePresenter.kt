package com.gxtc.huchuan.ui.im.system

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.gxtc.commlibrary.utils.*
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.bean.event.EventLoadBean
import com.gxtc.huchuan.bean.pay.OrdersRequestBean
import com.gxtc.huchuan.data.CircleRepository
import com.gxtc.huchuan.data.CircleSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.data.deal.DealRepository
import com.gxtc.huchuan.data.deal.DealSource
import com.gxtc.huchuan.handler.CircleShareHandler
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.AllApi
import com.gxtc.huchuan.http.service.CircleApi
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.ui.circle.classroom.newcalssroomaudit.NewClassRoomAuditActivity
import com.gxtc.huchuan.ui.circle.file.filelist.FileAuditActivity
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity
import com.gxtc.huchuan.ui.circle.home.CreateCirclePropertyActivity
import com.gxtc.huchuan.ui.circle.home.MineCircleActivity
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity
import com.gxtc.huchuan.ui.deal.guarantee.GuaranteeDetailedActivity
import com.gxtc.huchuan.ui.live.hostpage.LiveHostPageActivity
import com.gxtc.huchuan.ui.live.hostpage.RefundsAndCheckMemberActivity
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity
import com.gxtc.huchuan.ui.live.series.SeriesActivity
import com.gxtc.huchuan.ui.mall.order.MallOrderDetailActivity
import com.gxtc.huchuan.ui.message.MessageFragment
import com.gxtc.huchuan.ui.message.NewFriendsActivity
import com.gxtc.huchuan.ui.mine.account.AccountWaterActivity
import com.gxtc.huchuan.ui.mine.circle.article.ArticleManagerActivity
import com.gxtc.huchuan.ui.mine.deal.refund.RefundListActivity
import com.gxtc.huchuan.ui.mine.incomedetail.InComeDetailNewActivity
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.DistributeActivity
import com.gxtc.huchuan.ui.mine.news.MineArticleActivity
import com.gxtc.huchuan.ui.news.NewsWebActivity
import com.gxtc.huchuan.ui.pay.PayActivity
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.math.BigDecimal

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/17.
 */
object MessagePresenter {

    private var dealData: DealSource? = null
    private var circleData: CircleSource ? = null

    init {
        dealData = DealRepository()
        circleData = CircleRepository()
    }

    fun messageClick(context: Context, view: View, message: Message?): Boolean {
        //点击交易
        if (message?.content is DealMessage) {
            clickDeal(context, view, message)
        }

        //课堂点击
        if (message?.content is ClassMessage) {
            clickClass(context, view, message)
        }

        //圈子点击
        if (message?.content is CircleMessage) {
            clickCircle(context, view, message)
        }

        //文章点击
        if (message?.content is ArticleMessage) {
            clickArticle(context, view, message)
        }

        //商城点击
        if (message?.content is MallMessage){
            clickMall(context, view, message)
        }

        //系统消息点击
        if (message?.content is TextMessage){
            clickSystemMessage(context, view, message)
        }

        //担保消息点击
        if (message?.content is TradeInfoMessage){
            clickTradeInfoMessage(context, view, message)
        }

        return false
    }

    private fun clickTradeInfoMessage(context: Context, view: View, message: Message) {
        val tradeMsg = message.content as? TradeInfoMessage
        val msgType = tradeMsg?.msgType
        val id = tradeMsg?.tradeInfoId

        when(msgType){
            "0" -> id?.let{ GuaranteeDetailedActivity.startActivity(context, it) }
        }
    }


    fun clickSystemMessage(context: Context, view: View, message: Message) {
        /**
            1.同意添加好友 2.收益结算 3.账户流水
         */
       val mSystemMessage = message.content as? TextMessage
       if(TextUtils.isEmpty(mSystemMessage?.extra)) return
       val msgType = GsonUtil.getJsonValue(mSystemMessage?.extra,"msgType") as String
       val id = GsonUtil.getJsonValue(mSystemMessage?.extra,"id") as String
       when(msgType){
            "1" -> {
                SpUtil.putInt(context, MessageFragment.KEY_NUM(), 0)
                EventBusUtil.post(EventJPushBean(EventJPushBean.APPLY_FRIENDS, "", 0))
                val intent = Intent(context, NewFriendsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            "2" -> {
                val intent = Intent(context, InComeDetailNewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("indext",0)
                context.startActivity(intent)
            }

            "3" -> {
                val intent = Intent(context, AccountWaterActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

            "4" -> {
                val mShareHandler = CircleShareHandler(context)
                mShareHandler.getNewsData(id)
            }

           "5" -> {
               val intent = Intent(context, GoodsDetailedActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               intent.putExtra(Constant.INTENT_DATA,  id)
               context.startActivity(intent)
           }
        }
    }

    fun onMessageLongClick(context: Context, view: View, message: Message): Boolean {
        return false
    }


    private fun clickMall(context: Context, view: View, message: Message) {
        val mallMsg = message.content as? MallMessage

        /**
         * 1、订单待发货   2、订单已付款  3、订单已发货  4、订单已完成  5、订单已退款
         */
        when(mallMsg?.msgType){
            1, 2, 3, 4, 5-> MallOrderDetailActivity.junmpToOrderDetailActivity(context, mallMsg.orderId!!)
        }
    }

    //点击文章
    private fun clickArticle(context: Context, view: View, message: Message) {
        val articleMessage = message.content as? ArticleMessage

        /**
         * 0、审核通知（跳文章管理）1、打赏通知（不跳）2、通知好友写了文章（跳到文章）
         */
        when (articleMessage?.msgType) {
            0 -> {
                val intent = Intent(context, MineArticleActivity::class.java)
                context.startActivity(intent)
            }

            2 -> getNewsData(context, articleMessage.id!!)
        }
    }

    //点击圈子
    private fun clickCircle(context: Context, view: View, message: Message?) {
        val circleMessage = message?.content as? CircleMessage

        /**
         * 0、审核通知（跳我的圈子）；1、新人通知（跳审核成员）2、付费加入（不跳）
         * 3、结算通知（不跳）4、退款提醒（跳退款申请列表）5、同步（不跳） 6、创建圈子(圈子介绍页)   7、邀请创建圈子
         * 8、跳去文件审核页面 9.文章 10 课堂 11 用户申请加入 （把11改成系列课同步到圈子） 12 续费圈子通知*/
        when (circleMessage?.msgType) {
            0 -> {
                val intent = Intent(context, MineCircleActivity::class.java)
                intent.putExtra("flag", "1")
                context.startActivity(intent)
            }

            1, 4, 6 -> circleMessage.msgType?.let {
                if(circleMessage?.groupId == null){
                    ToastUtil.showShort(MyApplication.getInstance(),"圈子ID为空")
                    return
                }
                getCircleInfo(context, it, message)
            }

            7-> circleMessage.msgType?.let {
                verifyInviteCode(context, it, message)
            }

            8-> circleMessage.groupId?.let{
                val bean = CircleBean()
                bean.id = it.toInt()
                FileAuditActivity.startActivity(context, bean, 4)
            }

            9-> circleMessage.groupId?.let{
                val intent = Intent(context, ArticleManagerActivity::class.java)
                intent.putExtra("circle_id", it.toInt())
                context.startActivity(intent)
            }

            10-> circleMessage.groupId?.let{
                NewClassRoomAuditActivity.startActivity(context, it.toInt(), UserManager.getInstance().userCode)
            }

            11-> circleMessage.groupId?.let{

                NewClassRoomAuditActivity.startActivity(context, it.toInt(), UserManager.getInstance().userCode)
            }
            12-> circleMessage.groupId?.let{
                getInfo(context,it)
            }
        }
    }

    private fun getInfo(context:Context,id:String) {
        val token = UserManager.getInstance().token
        val sub = CircleApi.getInstance().getInfo(token, id.toInt()).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                ApiObserver<ApiResponseBean<CircleBean>>(object : ApiCallBack<CircleBean>() {
                    override fun onSuccess(data: CircleBean) {
                        val money = data.fee
                        renew(money,context,id);
                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(MyApplication.getInstance(), message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }



    //续费
    private fun renew(money:Double,context:Context,id:String) {
        val requestBean = OrdersRequestBean()

        val moneyB = BigDecimal(money)
        val total = moneyB.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble() * 100
        val token = UserManager.getInstance().token
        val transType = "GJ"
        val extra = "{\"groupId\":\"" + id + "\"}"

        requestBean.totalPrice = total .toString()
        requestBean.token = token
        requestBean.transType = transType
        requestBean.extra = extra
        requestBean.goodsName = "圈子续费"

        GotoUtil.goToActivity(context, PayActivity::class.java, requestBean)
    }


    //点击交易
    private fun clickDeal(context: Context, view: View, message: Message?) {
        val dealMessage = message?.content as? DealMessage

        //0、订单状态通知（跳订单）；1、退款通知（订单）2、好友交易通知（跳交易详情）

        when (dealMessage?.msgType) {
            0, 1 -> {
                getDealInfo(context, dealMessage.msgType!!, message)
            }

            2 -> {
                GoodsDetailedActivity.startActivity(context, dealMessage.tradeInfoId)
            }
        }
    }

    //点击课堂
    private fun clickClass(context: Context, view: View, message: Message?) {
        /**
         * 0、审核通知（跳课堂管理）1、好友开课通知（跳课堂）2、开课通知（跳课堂）
         * 3、课堂变动（不跳）4、结算通知  5、退款通知（退款申请列表）6、创建课堂(跳课堂) 7 课程平台推广通过审核  8 系列课平台推广通过审核
         */
        val classMessage = message?.content as? ClassMessage
        when (classMessage?.msgType) {
            0 -> {
                val chatRoomId = UserManager.getInstance().user?.chatRoomId
                LiveHostPageActivity.startActivity(context, "1", chatRoomId)
            }

            1, 2, 6 -> getClassInfo(context, classMessage.msgType!!, message)


            5 -> LiveHostPageActivity.startActivity(context, classMessage.bzId, "1", 2)

            7 -> LiveIntroActivity.startActivity(context,classMessage?.bzId)

            8 -> SeriesActivity.startActivity(context, classMessage?.bzId)
        }
    }


    //获取文章信息
    private fun getNewsData(context: Context, id: String) {
        EventBusUtil.post(EventLoadBean(true))
        val token = UserManager.getInstance().token
        val sub =
                AllApi.getInstance()
                        .getNewsInfo(token, id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<NewsBean>>(object : ApiCallBack<NewsBean>() {

                            override fun onSuccess(data: NewsBean?) {
                                EventBusUtil.post(EventLoadBean(false))
                                val intent = Intent(context, NewsWebActivity::class.java)
                                intent.putExtra("data", data)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            }

                            override fun onError(errorCode: String?, message: String?) {
                                EventBusUtil.post(EventLoadBean(false))
                                ToastUtil.showShort(context, message)
                            }
                        }))

        RxTaskHelper.getInstance().addTask(this, sub)
    }


    //获取圈子信息
    private fun getCircleInfo(context: Context, msgType: Int, message: Message?) {
        EventBusUtil.post(EventLoadBean(true))
        val token = UserManager.getInstance().token
        val circleMessage = message?.content as? CircleMessage
        val sub =
                CircleApi.getInstance()
                        .circleData(token, circleMessage?.groupId?.toInt()!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<CircleBean>>(object : ApiCallBack<CircleBean>() {

                            override fun onSuccess(data: CircleBean?) {
                                EventBusUtil.post(EventLoadBean(false))
                                if (msgType == 4) {
                                    data?.jumpPage = 1
                                    RefundsAndCheckMemberActivity.startActivity(context, data!!)
                                }

                                if (msgType == 1) {
                                    RefundsAndCheckMemberActivity.startActivity(context, data!!)
                                }

                                if (msgType == 6) {
                                    //未加入圈子
                                    if (data?.isJoin == 0) {
                                        val joinIntent = Intent(context, CircleJoinActivity::class.java)
                                        joinIntent.putExtra("byLiveId", data.id)
                                        joinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(joinIntent)

                                    } else {
                                        val intent = Intent(context, CircleMainActivity::class.java)
                                        intent.putExtra("groupId", data?.getId())
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    }
                                }
                            }

                            override fun onError(errorCode: String?, message: String?) {
                                EventBusUtil.post(EventLoadBean(false))
                                ToastUtil.showShort(context, message)
                            }
                        }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    //获取课堂信息
    private fun getClassInfo(context: Context, msgType: Int, message: Message?) {
        EventBusUtil.post(EventLoadBean(true))
        val token = UserManager.getInstance().token
        val classMessage = message?.content as? ClassMessage

        val sub =
                LiveApi.getInstance()
                        .getChatInfosBean(token, classMessage?.bzId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ApiObserver<ApiResponseBean<ChatInfosBean>>(object : ApiCallBack<ChatInfosBean>() {

                            override fun onSuccess(data: ChatInfosBean?) {
                                EventBusUtil.post(EventLoadBean(false))

                                //跳去系列课主页购买
                                if ("0" != data?.chatSeries && !data?.isSingUp!!) {
                                    SeriesActivity.startActivity(context, data.chatSeries, true)
                                } else {
                                    LiveIntroActivity.startActivity(context, classMessage?.bzId)
                                }
                            }

                            override fun onError(errorCode: String?, message: String?) {
                                EventBusUtil.post(EventLoadBean(false))
                                ToastUtil.showShort(context, message)
                            }
                        }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    //获取交易信息
    private fun getDealInfo(context: Context, msgType: Int, message: Message?) {
        EventBusUtil.post(EventLoadBean(true))

        val dealMessage = message?.content as? DealMessage
        val token = UserManager.getInstance().token
        dealData?.getOrderDetailed(dealMessage?.orderId, token, object : ApiCallBack<OrderDetailedBean>() {

            override fun onSuccess(data: OrderDetailedBean?) {
                EventBusUtil.post(EventLoadBean(false))
                //卖家点击
                if (data?.buyer == 1) {
                    OrderDetailedActivity.startActivity(context, dealMessage?.orderId)
                }

                //买家点击
                if (data?.buyer == 0) {
                    //订单状态通知
                    if (msgType == 0) {
                        OrderDetailedBuyerActivity.startActivity(context, dealMessage?.orderId)
                    }

                    //退款通知
                    if (msgType == 1) {
                        context.startActivity(Intent(context, RefundListActivity::class.java))
                    }
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                EventBusUtil.post(EventLoadBean(false))
                ToastUtil.showShort(context, message)
            }
        })
    }

    //校验创建圈子免费邀请码
    private fun verifyInviteCode(context: Context, msgType: Int, message: Message?){
        EventBusUtil.post(EventLoadBean(true))
        val circleMessage = message?.content as? CircleMessage
        val token = UserManager.getInstance().token
        circleMessage?.let {

            circleData?.verifyInviteCode(token,it.groupId,object : ApiCallBack<Any>(){
                override fun onSuccess(data: Any?) {
                    EventBusUtil.post(EventLoadBean(false))
                    context.startActivity(Intent(context,CreateCirclePropertyActivity::class.java))
                }

                override fun onError(errorCode: String?, message: String?) {
                    EventBusUtil.post(EventLoadBean(false))
                    ToastUtil.showShort(context,message)
                }

            })
        }
    }

    fun destroy() {
        dealData?.destroy()
        circleData?.destroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}