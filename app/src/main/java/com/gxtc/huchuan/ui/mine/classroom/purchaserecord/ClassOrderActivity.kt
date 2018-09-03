package com.gxtc.huchuan.ui.mine.classroom.purchaserecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.ClassOrderAdapter
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/30.
 * 课堂订单
 */
class ClassOrderActivity: BaseTitleActivity() {


    private lateinit var tvMonet: TextView
    private lateinit var tvCount: TextView
    private lateinit var tvNote: TextView

    private var type: Int = -1;
    private var start = 0

    private lateinit var mAdapter: ClassOrderAdapter

    private var id: String ? = null

    companion object {
        @JvmStatic
        fun startActivity(context: Context, id: String? ,type: Int){
            val intent = Intent(context, ClassOrderActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA, id)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_swipe_recyclerview)
    }

    override fun initView() {
        baseHeadView.showTitle(getString(R.string.title_class_order)).showBackButton { finish() }
        swipeLayout?.setColorScheme(*Constant.REFRESH_COLOR)
        swipeLayout.isEnabled = false

        val headView = layoutInflater.inflate( R.layout.head_class_order, null)
        tvMonet = headView.findViewById<TextView>(R.id.tv_money)
        tvCount = headView.findViewById<TextView>(R.id.income_count)
        tvNote = headView.findViewById<TextView>(R.id.income_note)
        recyclerview?.layoutManager = LinearLayoutManager(this)
        recyclerview?.setLoadMoreView(R.layout.model_footview_loadmore)
        recyclerview?.addHeadView(headView)


        mAdapter = ClassOrderAdapter(this, ArrayList<ClassOrderBean>(), R.layout.item_class_order)

        recyclerview?.adapter = mAdapter


    }

    override fun initListener() {
        swipeLayout.setOnRefreshListener {
            recyclerview?.reLoadFinish()
        }

        recyclerview?.setOnLoadMoreListener {
            start += 15
            getlistdata(true)
        }

        mAdapter?.setOnItemClickLisntener { parentView, v, position ->

        }
    }

    override fun initData() {
        id = intent.getStringExtra(Constant.INTENT_DATA)
        type = intent.getIntExtra("type",1);
        getHeaddata()
        getlistdata(false);
    }

    fun setHeadViewData(bean : ClassOrderHeadBean){
        tvMonet ?.text = StringUtil.formatMoney(2,bean.realIncome)+"元"
        tvCount ?.text = "共有"+bean.count+"笔"
        tvNote ?.text = "总收入"
    }

    fun  getlistdata( isLoadMore : Boolean) {

        if (!isLoadMore) {
            baseLoadingView.showLoading()
        }
        val map = HashMap<String, String>()
        map.put("token",UserManager.getInstance().token)
        map.put("chatId",id+"")
        map.put("start",start.toString())

        map.put("type",type.toString())

        val sub = LiveApi.getInstance().getlistChatSeriesOrderData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<ClassOrderBean>>>(object : ApiCallBack<Object>() {
                    override fun onSuccess(obj: Object) {
                        var list = obj as MutableList<ClassOrderBean>;
                        if(isLoadMore) {
                            if (list != null && list.size > 0) {
                                recyclerview.changeData(list, mAdapter)
                            } else {
                                recyclerview.loadFinish()
                            }

                        }else{
                            baseLoadingView.hideLoading()
                            if (list != null && list.size > 0) {

                                recyclerview.notifyChangeData(list, mAdapter)
                            } else {
                                baseEmptyView.showEmptyContent()
                            }
                        }
                    }

                    override fun onError(errorCode: String, message: String) {
                        if (recyclerview != null) {
                            if (!isLoadMore) {
                                baseLoadingView.hideLoading()
                            }else{
                                recyclerview.loadFinish()
                            }

                        }
                        ToastUtil.showShort(MyApplication.getInstance(), message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)

    }

    fun  getHeaddata() {
        val map = HashMap<String, String>()
        map.put("token",UserManager.getInstance().token)
        map.put("chatId",id+"")
        map.put("type",type.toString())

        val sub = LiveApi.getInstance().getHeadChatSeriesOrderData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<List<ClassOrderHeadBean>>>(object : ApiCallBack<List<ClassOrderHeadBean>>() {
                    override fun onSuccess(obj: List<ClassOrderHeadBean>) {
                        if(obj != null && obj.size>0)
                           setHeadViewData(obj.get(0))

                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(MyApplication.getInstance(), message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)

    }


    override fun onDestroy() {
        super.onDestroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }

}