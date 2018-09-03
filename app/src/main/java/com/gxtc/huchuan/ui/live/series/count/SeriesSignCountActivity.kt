package com.gxtc.huchuan.ui.live.series.count

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.SeriesSignCountAdater
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.widget.SearchView
import kotlinx.android.synthetic.main.activity_series_sign_count.*
import java.util.HashMap


/**
 * Created by zzg on 2017/12/12.
 */

class SeriesSignCountActivity : BaseTitleActivity(),SeriseSignCountContract.View {

     var mSeriesSignCountAdater:SeriesSignCountAdater? = null
     var mPresenter: SeriseSignCountContract.Present? = null
     var mSearchView: SearchView? = null
     var searchKeyWord: String? = ""
     var id: String ? = null
     var isloadMore:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_sign_count)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                isloadMore = false
                recyclerview.reLoadFinish()
                if(TextUtils.isEmpty(searchKeyWord)){
                    mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , "",false)
                }else{
                    mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , searchKeyWord !!,false)
                }
            }
        });
        recyclerview.setOnLoadMoreListener(object :LoadMoreWrapper.OnLoadMoreListener{
            override fun onLoadMoreRequested() {
                isloadMore = true
                if(TextUtils.isEmpty(searchKeyWord)){
                    mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , "",isloadMore)
                }else{
                    mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , searchKeyWord !!,isloadMore)
                }
            }

        })
        mSearchView?.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                isloadMore = false
                searchKeyWord = query!!
                mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , searchKeyWord !!,false)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                isloadMore = false
                searchKeyWord = newText!!
                searchKeyWord = searchKeyWord?.replace("'", "")
                mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , searchKeyWord !!,false)
                return false
            }

        }
        mSeriesSignCountAdater?.setOnItemClickLisntener { _, _, position ->
            PersonalInfoActivity.startActivity(this@SeriesSignCountActivity, mSeriesSignCountAdater!!.list.get(position).userCode)
        }
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle(getString(R.string.title_member_manage)).showBackButton{
            finish()
        }
        id = intent.getStringExtra("id")
        val joinType = intent.getStringExtra("joinType")
        mSearchView = SearchView(this)
        recyclerview.addHeadView(mSearchView)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        val map = HashMap<String, String>()
        map.put("type", "2")
        map.put("joinType", joinType)
        map.put("chatId", id!!)

        mSeriesSignCountAdater = SeriesSignCountAdater(this, ArrayList<ChatJoinBean.MemberBean>(),R.layout.item_serise_sign_layout, map, recyclerview)
        recyclerview.adapter = mSeriesSignCountAdater
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        super.initData()
        baseLoadingView.showLoading()
        SeriesSignCountPresent(this)
        mPresenter?.getlistJoinMember(UserManager.getInstance().token,"1" ,"2", id!! , searchKeyWord !!,false)
    }

    override fun showSeriseSignCount(datas: java.util.ArrayList<ChatJoinBean.MemberBean>) {
        baseLoadingView.hideLoading()
        if(datas != null && datas.size > 0){
            if(!isloadMore){
                refreshlayout.setRefreshing(false)
                recyclerview.notifyChangeData(datas,mSeriesSignCountAdater)
            }else{
                recyclerview.changeData(datas,mSeriesSignCountAdater)
            }
        }else{
            refreshlayout.setRefreshing(false)
//            baseEmptyView.showEmptyView()
//            recyclerview.loadFinish()
        }
    }

    override fun setPresenter(presenter: SeriseSignCountContract.Present?) {
        mPresenter = presenter!!
    }

    override fun showLoad() {}

    override fun showLoadFinish() {
        recyclerview.loadFinish()
    }

    override fun showEmpty() {
        if(TextUtils.isEmpty(searchKeyWord)){
            baseEmptyView.showEmptyView()
        }else{
            ToastUtil.showShort(MyApplication.getInstance(),"暂无搜到数据")
        }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(MyApplication.getInstance(),info)
        recyclerview.loadFinish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        RxTaskHelper.getInstance().cancelTask("SeriesSignCountAdater")
    }

    override fun showNetError() {}

    companion object{
        val INSTANT_TYPE_TOPIC = "0"
        val INSTANT_TYPE_SERISE = "1"
        @JvmStatic
        fun jumpToSeriesSignCountActivity(context: Context,id:String, joinType : String){
            val intent = Intent(context,SeriesSignCountActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("joinType",joinType)
            context.startActivity(intent)
        }
    }
}
