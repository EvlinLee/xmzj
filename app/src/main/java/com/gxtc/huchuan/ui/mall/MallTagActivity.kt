package com.gxtc.huchuan.ui.mall

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallTabAdater
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.MallParamDialog
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.utils.StringUtil
import com.gxtc.huchuan.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_mall_search.*
import kotlinx.android.synthetic.main.mall_bottom_layout.*
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.hashMapOf
/**
 * Created by zzg on 2017/10/24.
 */
class MallTagActivity : BaseTitleActivity(),MallTagDataContract.view,View.OnClickListener {

    var mMaillSearchAdapter: MallTabAdater? = null;
    var data: ArrayList<MallBean>? = null;
    var categoryId: String? = null;
    var mPresenter: MallTagDataContract.presenter? = null
    var title: String? = null;
    var headTitle: TextView? = null;
    var isLoadMore: Boolean = false
    var mallDialog: MallParamDialog? = null
    var map: HashMap<String, String>? = null
    var flag:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mall_search)
    }

    override fun initView() {
        super.initView()
        map = hashMapOf()
        categoryId = intent.getStringExtra("categoryId")
        baseHeadView.showTitle("新媒工具平台").showBackButton(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }

        })
        refreshlayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        var view = layoutInflater.inflate(R.layout.activity_tab_head_view_layout, null)
        headTitle = view.findViewById(R.id.head_title) as TextView
        headTitle?.text = intent.getStringExtra("title")
        recyclerview.addHeadView(view)
        onClick(mall_fenlei)
        mMaillSearchAdapter = MallTabAdater(this@MallTagActivity, ArrayList<CategoryBean>(), R.layout.mall_tab_item_layout)
        recyclerview.adapter = mMaillSearchAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                isLoadMore = false
                recyclerview.reLoadFinish()
                mPresenter!!.getTagDatas(false, categoryId!!)
            }

        });
        recyclerview.setOnLoadMoreListener {
            isLoadMore = true
            mPresenter!!.getTagDatas(true, categoryId!!)
        }

        mall_shouyel.setOnClickListener(this)
        mall_fenlei.setOnClickListener(this)
        mall_huodong.setOnClickListener(this)
        mall_wode.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        baseLoadingView.showLoading()
        MallTagPresenter(this)
        mPresenter!!.getTagDatas(false, categoryId!!)
    }

    override fun tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity::class.java)
    }

    override fun showTagDatas(datas: ArrayList<CategoryBean>) {
        baseLoadingView.hideLoading()
        if(flag){
            headTitle?.text = title
            recyclerview.reLoadFinish()//这个页面启用的是单例起动模式，商城底部的分类都是这个界面，所以都要重启加载更多
            flag = false
        }
        if (datas != null && datas.size > 0) {
            if (!isLoadMore) {
                refreshlayout.setRefreshing(false)
                recyclerview.notifyChangeData(datas, mMaillSearchAdapter)
            } else {
                recyclerview.changeData(datas, mMaillSearchAdapter)
            }
        } else {
            recyclerview.loadFinishNotView()
        }
        mMaillSearchAdapter!!.setOnReItemOnClickListener { v, position ->
            MallDetailedActivity.startActivity(this@MallTagActivity, mMaillSearchAdapter!!.list.get(position).merchandiseId)
        }
        mMaillSearchAdapter!!.setAddGoodsListner(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(UserManager.getInstance().isLogin){
                    val position = StringUtil.toInt(v!!.getTag())
                    val bean = mMaillSearchAdapter!!.list.get(position);
                    val mallBean = MallBean()
                    mallBean.data = bean.merchandiseId
                    mallBean.picUrl = bean.facePic
                    mallBean.name = bean.name
                    mallBean.price = bean.price
                    mallBean.priceList = bean.priceList
                    showParamDialog(mallBean)
                }else{
                    GotoUtil.goToActivity(this@MallTagActivity,LoginAndRegisteActivity::class.java)
                }

            }

        })
    }

    fun showParamDialog(mallBean: MallBean) {
        if (mallDialog == null)
            mallDialog = MallParamDialog()
            mallDialog?.dialogListener = object : MallParamDialog.MallParamListener {
            override fun onPayClick(dialog: MallParamDialog, bean: MallDetailBean, view: View) {
                val map = HashMap<String, String>()
                map.put("token", UserManager.getInstance().token)
                map.put("storePriceId", dialog.param!!.id.toString() + "")
                map.put("amount", mallDialog?.selectCount.toString() + "")
                mPresenter?.addShopCar(map)
            }
        }
        val builder = MallParamDialog.Builder()
        builder.setStoreId(StringUtil.toInt(mallBean?.data))
        builder.setCover(mallBean?.picUrl!!)
        builder.setGoodsName(mallBean.name)
        builder.setPrice(mallBean.price)
        builder.setPriceList(mallBean.priceList)
        mallDialog?.show(supportFragmentManager, MallParamDialog::class.java.simpleName, builder.builde(), true)
    }

    override fun onClick(v: View?) {
        when(v?.id){
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
                mPresenter?.getTags("")
            }
        //活动
            R.id.mall_huodong -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_huodong_selected)
                mPresenter?.getActivitysData("",0)
            }
        //我的
            R.id.mall_wode -> {
                if(!UserManager.getInstance().isLogin(this)){
                    return;
                }
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_wode_selected)
                GotoUtil.goToActivity(this@MallTagActivity,MyMallCenterActivity::class.java)
            }
        }
    }

    //单例回调
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        isLoadMore = false
        flag = true
        val mBundle=  intent?.extras
        categoryId = mBundle?.getString("categoryId")
        title = mBundle?.getString("title")
        baseLoadingView.showLoading()
        mPresenter?.getTagDatas(false, categoryId!!)
    }

    override fun showHeadIcon(datas: List<MallBean>) {
        if(datas == null) return
        MallHandleUtil.showPop(this,mall_fenlei,datas,R.drawable.shop_pop_bg, Gravity.TOP)
    }

    override fun showActivitysData(datas: List<MallBean>) {
        if(datas != null && datas.size > 0)
            MallHandleUtil.handlerByType(this,datas,0)
    }


    override fun showAddShopCarResule(datas: Any) {
        ToastUtil.showShort(this, "加入购物车成功")
    }


    override fun setPresenter(presenter: MallTagDataContract.presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
        baseEmptyView.showEmptyContent()
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {}

    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.destroy()
    }
}


