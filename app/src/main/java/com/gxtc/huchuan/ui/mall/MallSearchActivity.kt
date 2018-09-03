package com.gxtc.huchuan.ui.mall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallTabAdater
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.data.MallSearchContract
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.MallParamDialog
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.utils.StringUtil
import kotlinx.android.synthetic.main.activity_mall_search.*
import kotlinx.android.synthetic.main.mall_bottom_layout.*

/**
 * Created by zzg on 2017/10/24.
 */
class MallSearchActivity : BaseTitleActivity() ,TextWatcher,View.OnClickListener, MallSearchContract.view{

    var mMaillSearchAdapter : MallTabAdater? = null;
    var tempData : ArrayList<String>? = null;
    var mEditText : EditText? = null;
    var mSearchImage : ImageView? = null;
    var searchKey : String? = null;
    var mPresenter : MallSearchContract.Presenter? = null
    var map :HashMap<String,String>? = null
    var isLoadMore:Boolean = false
    var mallDialog: MallParamDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mall_search)
    }

    override fun initView() {
        super.initView()
        searchKey = intent.getStringExtra("searchValue")
        baseHeadView.showTitle("商品搜索").showBackButton(object : View.OnClickListener{
            override fun onClick(v: View?) {
                finish()
            }

        })
        onClick(mall_fenlei)
        refreshlayout.setColorSchemeResources(*Constant.REFRESH_COLOR)
        map = HashMap<String,String>()
        var view = layoutInflater.inflate(R.layout.search_bar_layout,null)
        tempData = ArrayList<String>()
        var titleLayout = view.findViewById(R.id.seach_layout) as LinearLayout
        mEditText = view.findViewById(R.id.seach_title) as EditText
        mSearchImage = view.findViewById(R.id.seach_iamge) as ImageView
        view.findViewById<View>(R.id.item_line)?.visibility = View.VISIBLE
        view.findViewById<View>(R.id.model_line)?.visibility = View.VISIBLE
        mEditText!!.setText(searchKey)
        titleLayout.setBackgroundColor(resources.getColor(R.color.white))
        recyclerview.addHeadView(view)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        mMaillSearchAdapter = MallTabAdater(this@MallSearchActivity, ArrayList<CategoryBean>(), R.layout.mall_tab_item_layout)
        recyclerview.adapter = mMaillSearchAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun initListener() {
        super.initListener()
        mall_shouyel.setOnClickListener(this)
        mall_fenlei.setOnClickListener(this)
        mall_huodong.setOnClickListener(this)
        mall_wode.setOnClickListener(this)
        mEditText!!.addTextChangedListener(this)
        mSearchImage!!.setOnClickListener(this)
        mEditText!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getData()
                recyclerview.reLoadFinish()
                WindowUtil.closeInputMethod(this@MallSearchActivity)
            }
            false
        })
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                isLoadMore = false
                refreshlayout.setRefreshing(false)
                recyclerview.reLoadFinish()
                mPresenter!!.getSearchData(false,map!!)
            }

        });
        recyclerview.setOnLoadMoreListener {
            isLoadMore = true
            mPresenter!!.getSearchData(true,map!!)
        }
        mMaillSearchAdapter!!.setOnReItemOnClickListener { v, position ->
            MallDetailedActivity.startActivity(this@MallSearchActivity, mMaillSearchAdapter!!.list.get(position).merchandiseId)
        }
        mMaillSearchAdapter!!.setAddGoodsListner(View.OnClickListener { v ->
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
                GotoUtil.goToActivity(this@MallSearchActivity, LoginAndRegisteActivity::class.java)
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

    override fun initData() {
        super.initData()
        MallSearchPresenter(this)
        getData()
    }

    fun getData(){
        baseLoadingView.showLoading()
        map!!.clear()
        map!!.put("searchValue",searchKey!!)
        val token = UserManager.getInstance().token
        token?.let { map?.put("token",token) }
        mPresenter!!.getSearchData(false,map!!)
    }

    override fun afterTextChanged(s: Editable?) {
        searchKey = s.toString()
        searchKey = searchKey?.replace("'", "")
        getData()
    }

    override fun showActivitysData(datas: List<MallBean>) {
        if(datas != null && datas.size > 0)
            MallHandleUtil.handlerByType(this,datas,0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.seach_iamge -> {
                getData()
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
                mPresenter?.getTags("")
            }
            //活动
            R.id.mall_huodong -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_huodong_selected)
                baseLoadingView.showLoading()
                mPresenter?.getActivitysData("",0)
            }
            //我的
            R.id.mall_wode -> {
                MallHandleUtil.setNolmal(this, mall_shouyel, mall_fenlei, mall_huodong, mall_wode)
                MallHandleUtil.setSelected(this,v,R.drawable.icon_wode_selected)
                GotoUtil.goToActivity(this@MallSearchActivity,MyMallCenterActivity::class.java)
            }
        }
    }

    override fun showSearchData(datas: ArrayList<CategoryBean>) {
        baseLoadingView.hideLoading()
        if(datas.size > 0 ){
            if(!isLoadMore){
                recyclerview.notifyChangeData(datas,mMaillSearchAdapter)
            }else{
                recyclerview.changeData(datas,mMaillSearchAdapter)
            }
        }else{
                recyclerview.loadFinishNotView()
        }
    }

    override fun showHeadIcon(datas: List<MallBean>) {
        baseLoadingView.hideLoading()
        if(datas == null) return
        MallHandleUtil.showPop(this,mall_fenlei,datas,R.drawable.shop_pop_bg, Gravity.TOP)
    }

    override fun showAddShopCarResule(datas: Any) {
        ToastUtil.showShort(this,"加入购物车成功")
    }

    override fun setPresenter(presenter: MallSearchContract.Presenter?) {
        mPresenter = presenter;
    }

    override fun showLoad() = Unit

    override fun showLoadFinish() = Unit

    override fun showEmpty() {
        baseLoadingView.hideLoading()
//        ToastUtil.showShort(MyApplication.getInstance(),"暂无搜到数据")
    }

    override fun showReLoad() = Unit

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() = Unit

    override fun onDestroy() {
        super.onDestroy()
        mEditText!!.removeTextChangedListener(this)
        mPresenter!!.destroy()
        mEditText?.setOnEditorActionListener(null)
    }

    companion object{
       fun jumpToMallSearchActivity(mContext:Context, searchValue:String){
           val intent = Intent(mContext,MallSearchActivity::class.java)
           intent.putExtra("searchValue",searchValue)
           mContext.startActivity(intent)
       }
    }
}
