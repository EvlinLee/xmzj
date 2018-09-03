package com.gxtc.huchuan.ui.mine.circle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.CircleSignAdater
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.ui.mine.circle.circlesign.CirclePresenter
import com.gxtc.huchuan.ui.mine.circle.circlesign.CircleSignContract
import com.gxtc.huchuan.widget.SearchView
import kotlinx.android.synthetic.main.activity_join_count.*


class JoinCountActivity : BaseTitleActivity(), CircleSignContract.View {
    var status:String = "" //0 免费 1 付费
    var groupId:String = ""
    var title:String? = ""
    var serachkeyWord = ""
    var start = 0
    var mCircleSignAdater:CircleSignAdater? = null
    var mPresenter: CircleSignContract.Presenter? = null
    var mSearchView :SearchView? = null
    var isRefresh : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_count)
    }

    override fun initView() {
        super.initView()
        status = intent.getStringExtra("status")
        groupId = intent.getStringExtra("groupId")
        when(status){
            "0" -> title = "免费人数"
            "1" -> title = "付费人数"
            "2" -> title = "全部成员"
        }
        baseHeadView.showTitle(title).showBackButton{
            finish()
        }

        refreshlayout?.setColorScheme(* Constant.REFRESH_COLOR)

        mSearchView = SearchView(this)
        recyclerview.addHeadView(mSearchView)
        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)
        mCircleSignAdater = CircleSignAdater(this, ArrayList<CircleSignBean>(),R.layout.item_circle_prople_statistics,false)
        recyclerview.adapter = mCircleSignAdater
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        super.initData()
        baseLoadingView.showLoading()
        CirclePresenter(this)
        mPresenter?.getData(groupId,start.toString(),status,serachkeyWord)
    }

    override fun initListener() {
        super.initListener()
        refreshlayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                start = 0
                isRefresh = true
                recyclerview.reLoadFinish()
                if(TextUtils.isEmpty(serachkeyWord)){
                    mPresenter?.getData(groupId,start.toString(),status,"")
                }else{
                    mPresenter?.getData(groupId,start.toString(),status,serachkeyWord)
                }
            }

        });

        recyclerview.setOnLoadMoreListener(object :LoadMoreWrapper.OnLoadMoreListener{
            override fun onLoadMoreRequested() {
                start = start + 15
                isRefresh = false
                if(TextUtils.isEmpty(serachkeyWord)){
                    mPresenter?.getData(groupId,start.toString(),status,"")
                }else{
                    mPresenter?.getData(groupId,start.toString(),status,serachkeyWord)
                }
            }

        })

        mSearchView?.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                start = 0
                isRefresh = true
                serachkeyWord = query!!
                recyclerview.reLoadFinish()
                mPresenter?.getData(groupId,start.toString(),status,serachkeyWord)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                start = 0
                isRefresh = true
                serachkeyWord = newText!!
                recyclerview.reLoadFinish()
                mPresenter?.getData(groupId,start.toString(),status,serachkeyWord)
                return false
            }

        }

    }

    override fun showData(datas: ArrayList<CircleSignBean>) {
        baseLoadingView.hideLoading()
        if(isRefresh){
            refreshlayout.isRefreshing = false
            recyclerview.notifyChangeData(datas,mCircleSignAdater)
        }else{
            recyclerview.changeData(datas,mCircleSignAdater)
        }
    }

    override fun setPresenter(presenter: CircleSignContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {}

    override fun showLoadFinish() {}

    override fun showEmpty() {
        baseLoadingView.hideLoading()
        if(isRefresh){
            refreshlayout.isRefreshing = false
            if(TextUtils.isEmpty(serachkeyWord)){
                if(start == 0){
                    baseEmptyView.showEmptyContent()
                }
            }else{
                ToastUtil.showShort(MyApplication.getInstance(),"暂无搜到数据")
            }
        }else{
            recyclerview.loadFinish()
        }
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
       ToastUtil.showShort(MyApplication.getInstance(),info)
    }

    override fun showNetError() {}

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    companion object{
        val INSTANT_TYPE_FREE = "0"
        val INSTANT_TYPE_PAY = "1"
        @JvmField val INSTANT_TYPE_ALL = "2"

        @JvmStatic
        fun jumpToJoinCountActivity(activity: Activity,status:String,groupId:String){
            val intent = Intent(activity,JoinCountActivity::class.java)
            intent.putExtra("status",status)
            intent.putExtra("groupId",groupId)
            activity.startActivity(intent)
        }
    }
}
