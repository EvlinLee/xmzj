package com.gxtc.huchuan.ui.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.NewFriendAdapter
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.NewFriendBean
import com.gxtc.huchuan.bean.event.EventFocusBean
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.focus.FocusActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.widget.SearchView
import kotlinx.android.synthetic.main.new_friends_layout.*
import org.greenrobot.eventbus.Subscribe

/**
 * 新的朋友
 */
class NewFriendsActivity : BaseTitleActivity(), NewFriendsContract.View {


    private var searchView : com.gxtc.huchuan.widget.SearchView? = null
    private var mAdapter : NewFriendAdapter ? = null
    private var mPresenter : NewFriendsContract.Presenter ? = null

    private var mAlertDialog : AlertDialog? = null
    private var curPosition = -1
    private var newFriendsPosition = -1
    private var mNewFriendBean: NewFriendBean? = null
    private var groupChatId = ""
    private lateinit var mSource : MutableList<FocusBean>

    companion object {
        @JvmStatic
        fun startActivity(context: Context) {
            val intent = Intent(context, NewFriendsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_friends_layout)
        EventBusUtil.register(this)
    }

    override fun initView() {
        baseHeadView.showTitle("新的朋友")
        baseHeadView.showBackButton { finish() }
        baseHeadView.showHeadRightButton("添加好友",object :View.OnClickListener{
            override fun onClick(v: View?) {
                FocusActivity.startActivity(this@NewFriendsActivity, "5")
            }

        })
        searchView = SearchView(this)
        val view = layoutInflater.inflate(R.layout.contact_header_layout,null)
        swipeLayout?.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)

        recyclerview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recyclerview?.addHeadView(searchView)
        recyclerview?.addHeadView(view)
        recyclerview?.setLoadMoreView(R.layout.model_footview_loadmore)
        mAdapter = NewFriendAdapter(this, mutableListOf(),R.layout.item_invite_chat)
        recyclerview?.adapter = mAdapter

        view.setOnClickListener {
            GotoUtil.goToActivity(this@NewFriendsActivity, AddressListActivity::class.java)
        }

    }

    override fun initListener() {
        swipeLayout?.setOnRefreshListener {
            mPresenter?.refreshData()
        }

        recyclerview?.setOnLoadMoreListener {
            recyclerview?.reLoadFinish()
            mPresenter?.loadMoreData()
        }

        mAdapter?.setOnClickListener(View.OnClickListener{  v ->
            // 1已添加  0未添加
            if("0".equals((v.tag as? FocusBean)?.isMyFriend)){
                val code = (v.tag as? FocusBean)?.userCode
                newFriendsPosition = (v.tag as? FocusBean)?.position!!
                mPresenter?.followUser(code!!)
            }
        })

        mAdapter?.setOnReItemOnClickListener { _, position ->
            val code = mAdapter?.list?.get(position)?.userCode
            PersonalInfoActivity.startActivity(this,code)
        }

        mAdapter?.setOnReItemOnLongClickListener { _, position ->
            if (UserManager.getInstance().isLogin) {
                 mAlertDialog  = DialogUtil.showDeportDialog(this, false, null, "确定要删除好友申请") { view ->
                    if (view.id == R.id.tv_dialog_confirm) {
                        curPosition =  position
                        mPresenter?.overlook(mAdapter!!.list.get(position).userCode)
                    }
                    mAlertDialog?.dismiss()
                }
            } else {
                GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity::class.java,
                        Constant.requestCode.NEWS_AUTHOR)
            }
        }

        searchView?.let {
            it.onCloseListener = android.support.v7.widget.SearchView.OnCloseListener {
                if(mSource.size > 0){
                    recyclerview?.notifyChangeData(mSource,mAdapter)
                }
                false
            }

            it.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!TextUtils.isEmpty(query!!)){
                        baseLoadingView?.showLoading(true)
                        mPresenter?.queryUser(query)
                        mSource.clear()
                        mSource.addAll(mAdapter!!.list)
                    }
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean = false
            }
        }

    }

    override fun initData() {
        NewFriendsPresenter(this)
        mPresenter?.getNewFriends(groupChatId)
        mSource = mutableListOf()
    }

    override fun showOverlookResult(isSuccess: Boolean, error: String,userCode: String) {
        when(isSuccess){
            true ->{
                recyclerview?.removeData(mAdapter,curPosition)
                ToastUtil.showShort(this,"取消好友申请成功")
            }
            false -> ToastUtil.showShort(this,error)
        }
    }

    override fun showNewFriends(datas: MutableList<FocusBean>) {
        recyclerview?.notifyChangeData(datas,mAdapter)
       /* if(mNewFriendBean != null){
            EventBusUtil.post(EventJPushBean(EventJPushBean.RE_APPLY_FRIENDS,"",mAdapter!!.list.size))
        }*/
    }

    override fun showRefreshData(datas: MutableList<FocusBean>?) {
        swipeLayout?.isRefreshing = false
        if(datas != null) return
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun showLoadMoreData(datas: MutableList<FocusBean>?) {
        recyclerview?.changeData(datas,mAdapter)
    }

    override fun showNoLoadMore() {
        recyclerview?.loadFinish()
    }

    override fun setPresenter(presenter: NewFriendsContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView?.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView?.hideLoading()
    }

    override fun showEmpty() {}

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this,info)
    }

    override fun showNetError() {
        baseEmptyView?.showNetWorkView {
            baseEmptyView?.hideEmptyView()
            mPresenter?.getNewFriends(groupChatId)
        }
    }

    @Subscribe
    fun onEven(bean: NewFriendBean){
        mNewFriendBean = bean;
        if(bean.type == EventJPushBean.RE_APPLY_FRIENDS){
             mPresenter?.getNewFriends(groupChatId)
        }
    }


    override fun showFollowUser(userCode: String, isSuccess: Boolean, error: String) {
        if(isSuccess){
            if(newFriendsPosition != -1){
                mAdapter?.list!!.get(newFriendsPosition).isMyFriend = "1"
                recyclerview?.notifyItemChanged(newFriendsPosition)

                EventBusUtil.post(EventFocusBean(true))
            }
            EventBusUtil.post(EventJPushBean(EventJPushBean.RE_APPLY_FRIENDS,"",0))
        }else{
            ToastUtil.showShort(this,error)
        }
    }

    override fun showQueryResult(datas: MutableList<FocusBean>) {
        baseLoadingView?.hideLoading()
        recyclerview?.notifyChangeData(datas,mAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
        mPresenter?.destroy()
    }

}
