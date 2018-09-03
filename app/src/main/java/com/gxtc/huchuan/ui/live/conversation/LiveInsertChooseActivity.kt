package com.gxtc.huchuan.ui.live.conversation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.LiveInsertChooseAdapter
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.LiveInsertBean
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.KeyboardUtils
import com.gxtc.huchuan.widget.SearchView
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*

/**
 * 课堂选择插入内容页面
 * 伍玉南
 */
class LiveInsertChooseActivity : BaseTitleActivity(), LiveInsertChooseContract.View {

    private var searchView: SearchView? = null

    private var type: Int = 1
    private var chatRoomId: String = ""
    private var IMType = Conversation.ConversationType.CHATROOM
    private var infosBean: ChatInfosBean ? = null


    private lateinit var sourceData: MutableList<LiveInsertBean>
    private lateinit var mAdapter: LiveInsertChooseAdapter

    private var mPresenter: LiveInsertChooseContract.Presenter ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_insert_choose)
    }

    override fun initView() {
        swipeLayout.setColorSchemeResources(*Constant.REFRESH_COLOR)

        baseHeadView?.showTitle("选择内容")?.showBackButton { finish() }
        searchView = SearchView(this)
        val itemsId = intArrayOf(R.layout.item_live_insert_circle,
                R.layout.item_live_insert_article,
                R.layout.item_live_insert_class,
                R.layout.item_live_insert_serise,
                R.layout.item_live_insert_deal,
                R.layout.item_live_insert_mall
        )

        type = intent.getIntExtra("type", 1)
        mAdapter = LiveInsertChooseAdapter(type, this, mutableListOf(), *itemsId)
        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val headView = View.inflate(this, R.layout.head_live_insert, null)
        searchView = headView.findViewById<SearchView>(R.id.search_live_insert)
        recyclerview.addHeadView(headView)
        if(type != 1) recyclerview?.addFootView(R.layout.model_footview_loadmore)

        recyclerview.adapter = mAdapter

        val tvHint = headView.findViewById<TextView>(R.id.tv_hint)
        when(type){
            TYPE_CIRCLE-> {
                tvHint.text = "可搜索其他圈子插入课堂"
                searchView?.tvHint?.text = "搜索圈子"
            }

            TYPE_ARTICLE->{
                tvHint.text = "可搜索其他作者文章插入课堂"
                searchView?.tvHint?.text = "搜索文章"
            }

            TYPE_CLASS->{
                tvHint.text = "可搜索其他讲师课程插入课堂"
                searchView?.tvHint?.text = "搜索课程"
            }

            TYPE_SERISE->{
                tvHint.text = "可搜索其他讲师系列课插入课堂"
                searchView?.tvHint?.text = "搜索课程"
            }

            TYPE_DEAL->{
                tvHint.text = "可搜索平台交易插入课堂"
                searchView?.tvHint?.text = "搜索交易"
            }

            TYPE_MALL->{
                tvHint.text = "可搜索平台商品插入课堂"
                searchView?.tvHint?.text = "搜索商品"
            }
        }
    }

    override fun initData() {
        infosBean = intent.getSerializableExtra(Constant.INTENT_DATA) as ChatInfosBean
        IMType = intent.getSerializableExtra("IMType") as Conversation.ConversationType
        chatRoomId = infosBean?.id!!

        LiveInsertChoosePresenter(type, this)
        mPresenter?.getData()
        sourceData = mutableListOf()
    }

    override fun initListener() {
        swipeLayout.setOnRefreshListener {
            recyclerview.reLoadFinish()
            mPresenter?.refreshData()
        }

        recyclerview.setOnLoadMoreListener {
            mPresenter?.loadMoreData()
        }

        mAdapter.setOnReItemOnClickListener { _, position ->
            showConfirmDialog(position)
        }

        searchView?.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!TextUtils.isEmpty(query)) {
                    KeyboardUtils.hideSoftInput(this@LiveInsertChooseActivity)
                    sourceData.clear()
                    sourceData.addAll(mAdapter.datas)
                    mPresenter?.searchData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        }

        searchView?.onCloseListener = android.support.v7.widget.SearchView.OnCloseListener {
            if(sourceData.size > 0){
                recyclerview?.notifyChangeData(sourceData, mAdapter)
            }
            true
        }
    }


    override fun showSearchData(datas: MutableList<LiveInsertBean>) =
            if(datas.isNotEmpty()){
                recyclerview.notifyChangeData(datas, mAdapter)
            }else{
                ToastUtil.showShort(this, "暂无搜索结果")
            }

    override fun showData(datas: MutableList<LiveInsertBean>) {
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showRefreshData(datas: MutableList<LiveInsertBean>?) {
        recyclerview.notifyChangeData(datas, mAdapter)
        swipeLayout.isRefreshing = false
    }

    override fun showLoadMoreData(datas: MutableList<LiveInsertBean>?) {
        recyclerview?.changeData(datas, mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview.loadFinish()
    }

    override fun setPresenter(presenter: LiveInsertChooseContract.Presenter?) {
        this.mPresenter = presenter
    }

    override fun showLoad() {
        if(!recyclerview.isLoadingMore && !swipeLayout.isRefreshing){
            baseLoadingView?.showLoading()
        }
    }

    override fun showLoadFinish() {
        baseLoadingView.hideLoading()
    }

    override fun showEmpty() = Unit

    override fun showReLoad() = Unit

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {
        baseEmptyView?.showNetWorkView {
            baseEmptyView?.hideEmptyView()
        }
    }

    override fun showShareSuccess(message: Message) {
        val intent = Intent()
        intent.putExtra(Constant.INTENT_DATA, message)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private var dialog: AlertDialog? = null
    private fun showConfirmDialog(position: Int){
        val bean = mAdapter.datas[position]
        dialog = DialogUtil.showInputDialog(this, false, "", "确认发送: ${bean.title} ?") {
            mPresenter?.sendShareMessage(infosBean!!, bean)
            dialog?.dismiss()
        }
    }


    companion object {
        @JvmField val TYPE_CIRCLE = 0

        @JvmField val TYPE_ARTICLE = 1

        @JvmField val TYPE_CLASS = 2

        @JvmField val TYPE_SERISE = 3

        @JvmField val TYPE_DEAL = 4

        @JvmField val TYPE_MALL = 5

        @JvmField val TYPE_VISITING_CARD = 6

        @JvmField val TYPE_SPECIAL = 7

        @JvmStatic
        fun startActivity(infosBean: ChatInfosBean?, IMType: Conversation.ConversationType?, type: Int = 1, activity: Activity){
            val intent = Intent(activity, LiveInsertChooseActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra(Constant.INTENT_DATA, infosBean)
            intent.putExtra("IMType", IMType)
            activity.startActivityForResult(intent, LiveConversationActivity.REQUSET_INSERT)
        }
    }

}
