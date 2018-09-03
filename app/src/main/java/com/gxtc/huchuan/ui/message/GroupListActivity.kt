package com.gxtc.huchuan.ui.message

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.GroupListAdapter
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.bean.event.EventGroupBean
import com.gxtc.huchuan.widget.SearchView
import io.rong.imlib.model.Conversation
import org.greenrobot.eventbus.Subscribe

/**
 * 群聊列表
 */
class GroupListActivity : BaseTitleActivity(),GroupListContract.View {

    private var swipeLayout : SwipeRefreshLayout? = null
    private var recyclerview : RecyclerView? = null

    private var searchView : SearchView? = null

    private var mAdapter : GroupListAdapter? = null
    private var mPresenter : GroupListContract.Presenter ? = null

    private var mSource : MutableList<MessageBean> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_recyclerview)
        EventBusUtil.register(this)
    }

    override fun initView() {
        baseHeadView?.showTitle("群聊")
        baseHeadView?.showBackButton { finish() }

        searchView = SearchView(this)

        recyclerview = findViewById(R.id.recyclerview) as? RecyclerView
        swipeLayout = findViewById(R.id.swipeLayout) as? SwipeRefreshLayout
        swipeLayout?.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)

        recyclerview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recyclerview?.addHeadView(searchView)
        recyclerview?.setLoadMoreView(R.layout.model_footview_loadmore)
        mAdapter = GroupListAdapter(this, mutableListOf(),R.layout.item_invite_chat)
        recyclerview?.adapter = mAdapter

    }

    override fun initListener() {
        swipeLayout?.setOnRefreshListener {
            mSource?.clear()
            mPresenter?.refreshData()
        }

        recyclerview?.setOnLoadMoreListener {
            recyclerview?.reLoadFinish()
            mPresenter?.loadMoreData()
        }

        searchView?.let {
            it.onCloseListener = android.support.v7.widget.SearchView.OnCloseListener {
                recyclerview?.notifyChangeData(mSource,mAdapter)
                false
            }

            it.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!TextUtils.isEmpty(query!!)){
                        baseLoadingView?.showLoading(true)
                        mPresenter?.queryGroup(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            }
        }

        mAdapter?.setOnReItemOnClickListener { _, position ->
            val bean = mAdapter?.list?.get(position)
            val uri = Uri.parse(
                    "rong://" + this@GroupListActivity.getApplicationInfo().packageName).buildUpon().appendPath(
                    "conversation").appendPath(
                    Conversation.ConversationType.GROUP.getName().toLowerCase()).appendQueryParameter(
                    "targetId",
                    bean?.groupChatId).appendQueryParameter("title",bean?.groupName ).appendQueryParameter(
                    "isCircle", "1").build()
            startActivity(Intent("android.intent.action.VIEW", uri))
        }
    }

    //退出群聊结束会话
    @Subscribe
    fun onEvent(bean: EventGroupBean?) {
        if(bean!!.isDelete){
            for(i in mAdapter!!.list?.indices!!){
                if(bean.targetId.equals(mAdapter!!.list?.get(i)!!.groupChatId)){
                    recyclerview?.removeData(mAdapter!!,i)
                }
            }
        }
    }

    override fun initData() {
        GroupListPresenter(this)
        mPresenter?.getGroups()
        mSource = mutableListOf()
    }

    override fun showRefreshData(datas: MutableList<MessageBean>?) {
        mSource?.addAll(datas!!)
        swipeLayout?.isRefreshing = false
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showLoadMoreData(datas: MutableList<MessageBean>?) {
        mSource?.addAll(datas!!)
        recyclerview?.changeData(datas,mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview?.loadFinish()
    }

    override fun showGroups(datas: MutableList<MessageBean>) {
        mSource?.addAll(datas)
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showQueryResult(datas: MutableList<MessageBean>) {
        baseLoadingView?.hideLoading()
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun setPresenter(presenter: GroupListContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView?.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView?.hideLoading()
    }

    override fun showEmpty() {
        baseEmptyView?.showEmptyContent()
    }

    override fun showReLoad() {
    }

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {
        baseEmptyView?.showNetWorkView {
            baseEmptyView?.hideEmptyView()
            mPresenter?.getGroups()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        EventBusUtil.unregister(this)
    }

}
