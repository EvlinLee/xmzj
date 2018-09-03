package com.gxtc.huchuan.ui.circle.homePage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.ChatGroupAdapter
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.widget.DividerItemDecoration
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_chat_group.*

/**
 * 选择群聊
 */
class ChatGroupActivity : BaseTitleActivity(),ChatGroupContract.View {

    var memberType : Int ? = null
    var mAdapter : ChatGroupAdapter ? = null
    var mPresenter : ChatGroupPresenter ? = null
    var groupId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)
    }

    override fun initView() {
        baseHeadView?.showTitle("选择群聊")
        baseHeadView?.showBackButton { finish() }

        mAdapter = ChatGroupAdapter(this,mutableListOf<CircleBean>(),R.layout.item_circle_prise)
        recyclerview?.layoutManager = LinearLayoutManager(this,1,false)
        recyclerview?.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST,1,Color.parseColor("#ECEDEE")))
        recyclerview?.adapter = mAdapter

        swipeLayout?.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
    }

    override fun initListener() {
        swipeLayout?.setOnRefreshListener {
            mPresenter?.refreshData()
        }

        mAdapter?.setOnReItemOnClickListener { _, position ->
            val bean = mAdapter?.list?.get(position)
            bean?.let {
                val uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversation")
                        .appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                        .appendQueryParameter("targetId", bean.groupChatId)
                        .appendQueryParameter("title", bean.groupChatName).build()
                startActivity(Intent("android.intent.action.VIEW",uri))
            }
        }
    }

    override fun initData() {
        ChatGroupPresenter(this)
        try {
            groupId = intent.getStringExtra(Constant.INTENT_DATA).toInt()
        }catch (e :Exception){
            groupId = 0
        }

        mPresenter?.getData(groupId)
    }

    override fun showRefreshData(datas: MutableList<CircleBean>?) {
        swipeLayout?.isRefreshing = false
        if(datas != null){
            recyclerview?.notifyChangeData(datas,mAdapter)
        }
    }

    override fun showLoadMoreData(datas: MutableList<CircleBean>?) {}

    override fun showNoLoadMore() {}

    override fun showData(datas: MutableList<CircleBean>) {
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun setPresenter(presenter: ChatGroupContract.Presenter?) {
        mPresenter = presenter as ChatGroupPresenter?
    }

    override fun showLoad() {
        baseLoadingView.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView.hideLoading()
    }

    override fun showEmpty() {
        baseEmptyView.showEmptyContent()
    }

    override fun showReLoad() {
    }

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter?.getData(groupId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
    }

    companion object {
        @JvmStatic
        fun startActivity(context: Context, groupId: String){
            GotoUtil.goToActivity(context as Activity,ChatGroupActivity::class.java ,0, groupId)
        }
    }

}
