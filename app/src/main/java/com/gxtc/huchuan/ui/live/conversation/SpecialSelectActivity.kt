package com.gxtc.huchuan.ui.live.conversation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MySpecialListAdapter
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.LiveInsertBean
import com.gxtc.huchuan.data.SpecialBean
import com.gxtc.huchuan.ui.special.MySpecialListContract
import com.gxtc.huchuan.ui.special.MySpecialListPresenter
import com.gxtc.huchuan.utils.ImMessageUtils
import com.gxtc.huchuan.utils.RIMErrorCodeUtil
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.layout_swipe_recyclerview.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/31.
 * 选择专题插入课堂
 */
class SpecialSelectActivity: BaseTitleActivity(), MySpecialListContract.View {

    private lateinit var mAdapter: MySpecialListAdapter
    private lateinit var mPresenter: MySpecialListContract.Presenter

    private var infosBean: ChatInfosBean ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_swipe_recyclerview)
    }

    override fun initView() {
        baseHeadView.showTitle(getString(R.string.title_my_speciai_list))
                .showBackButton { finish() }
        swipeLayout.setColorScheme(*Constant.REFRESH_COLOR)

        mAdapter = MySpecialListAdapter(this, mutableListOf())
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@SpecialSelectActivity, LinearLayoutManager.VERTICAL, false)
            addFootView(R.layout.model_footview_loadmore)
            adapter = mAdapter
        }
    }

    override fun initListener() {
        swipeLayout.setOnRefreshListener {
            recyclerview.reLoadFinish()
            mPresenter.refreshData()
        }
        mAdapter.setOnReItemOnClickListener { _, position ->
            val bean = this.mAdapter.list[position]
            sendShareMessage(bean)
        }

        recyclerview.setOnLoadMoreListener {
            mPresenter.loadMoreData()
        }
    }

    override fun initData() {
        MySpecialListPresenter(this, mAdapter)
        mPresenter.getData()

        infosBean = intent.getSerializableExtra(Constant.INTENT_DATA) as ChatInfosBean
    }

    override fun showData(datas: MutableList<SpecialBean>) {
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showRefreshData(datas: MutableList<SpecialBean>?) {
        swipeLayout.isRefreshing = false
        recyclerview.notifyChangeData(datas, mAdapter)
    }

    override fun showLoadMoreData(datas: MutableList<SpecialBean>?) {
        recyclerview.changeData(datas, mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview.loadFinish()
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

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {
        baseEmptyView.showNetWorkView {
            baseEmptyView.hideEmptyView()
            mPresenter.getData()
        }
    }

    override fun setPresenter(presenter: MySpecialListContract.Presenter?) {
        mPresenter = presenter!!
    }

    override fun showReLoad() = Unit

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.destroy()
    }

    private fun sendShareMessage(bean: SpecialBean) {
        val inserBean = LiveInsertBean()
        inserBean.id = bean.id?:0
        inserBean.title = bean.name?: ""
        inserBean.cover = bean.pic?: ""
        inserBean.infoType = LiveInsertChooseActivity.TYPE_SPECIAL.toString()

        ImMessageUtils.sendShareMessage(if(infosBean!!.isSelff) "3" else "1", infosBean!!.id, inserBean, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) = Unit

            override fun onSuccess(message: Message) {
                val intent = Intent()
                intent.putExtra(Constant.INTENT_DATA, message)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(this@SpecialSelectActivity, RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }

    companion object {
        @JvmStatic
        fun startActivity(infosBean: ChatInfosBean, activity: Activity){
            val intent = Intent(activity, SpecialSelectActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA, infosBean)
            activity.startActivityForResult(intent, LiveConversationActivity.REQUSET_INSERT)
        }
    }
}