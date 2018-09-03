package com.gxtc.huchuan.ui.message

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.AddressListAdater
import com.gxtc.huchuan.bean.NewFriendBean
import com.gxtc.huchuan.bean.PersonInfoBean
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.widget.SearchView
import kotlinx.android.synthetic.main.activity_mall_search.*

/**
 * 通讯录好友
 */
class AddressListActivity : BaseTitleActivity(), AddressListContract.View {

    lateinit var sourceData: MutableList<PersonInfoBean>            //用于关闭搜索之后 将数据切换回来

    var mPresenter: AddressListContract.Presenter? = null
    var mMaillSearchAdapter: AddressListAdater? = null
    var personInfoBean: PersonInfoBean? = null

    var curPosition = -1
    var startCount = 0
    var searchKey = ""
    var isRefresh: Boolean = true
    var mDialog: AlertDialog? = null
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
    }

    override fun initView() {
        sourceData = mutableListOf()
        baseHeadView.showTitle("查看手机通讯录").showBackButton { finish() }

        recyclerview.setLoadMoreView(R.layout.model_footview_loadmore)

        mMaillSearchAdapter = AddressListAdater(this@AddressListActivity, ArrayList<PersonInfoBean>(), R.layout.activity_new_friends)

        searchView = SearchView(this)
        val searchArea = FrameLayout(this)
        searchArea.setBackgroundColor(Color.parseColor("#ffffff"))
        searchArea.addView(searchView)
        recyclerview.addHeadView(searchArea)
        recyclerview.adapter = mMaillSearchAdapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    override fun initListener() {
        recyclerview.setOnLoadMoreListener {
            mPresenter?.loadMoreData()
        }


        mMaillSearchAdapter?.setOnAddListenber(object : AddressListAdater.addFriendsListener {
            override fun addFriend(mPersonInfoBean: PersonInfoBean?, position: Int) {
                curPosition = position
                personInfoBean = mPersonInfoBean
                if (mPersonInfoBean?.userCode != null) {
                    //申请好友
                    if (mPersonInfoBean.chatStatus == 0) {
                        applyFriends(mPersonInfoBean.userCode)
                    }
                    //通过好友
                    if (mPersonInfoBean.chatStatus == 1) {
                        follow(mPersonInfoBean.userCode)
                    }

                } else {
                    //邀请请好友
                    inviteFriends(mPersonInfoBean!!.phones)
                }

            }
        })


        mMaillSearchAdapter?.setOnItemClickLisntener { parentView, v, position ->
            if (mMaillSearchAdapter?.list?.get(position)?.userCode != null) {
                PersonalInfoActivity.startActivity(this@AddressListActivity, mMaillSearchAdapter?.list?.get(position)?.userCode)
            }
        }


        searchView.onQueryTextListener = object : android.support.v7.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchKey = newText.toString()
                if (TextUtils.isEmpty(searchKey)) {
                    recyclerview?.reLoadFinish()
                    recyclerview?.notifyChangeData(sourceData, mMaillSearchAdapter)
                } else {
                    recyclerview?.loadFinish()
                    mPresenter?.searchData(newText!!)
                }
                return false
            }
        }

        searchView.onCloseListener = android.support.v7.widget.SearchView.OnCloseListener {
            recyclerview?.notifyChangeData(sourceData, mMaillSearchAdapter)
            true
        }
    }


    override fun initData() {
        AddressListPresenter(this)

        val pers = arrayOf(Manifest.permission.READ_CONTACTS)
        performRequestPermissions("此应用需要读取通讯录权限", pers, 1011, object : BaseTitleActivity.PermissionsResultListener {
            override fun onPermissionGranted() {

                //读取通讯录好友
                mPresenter?.readContacts(contentResolver)
            }

            override fun onPermissionDenied() {
                ToastUtil.showShort(this@AddressListActivity, "应用没有读取通讯录权限")
            }
        })
    }


    private fun inviteFriends(phoneNumber: String) {
        val message = "新媒之家app，一个汇集百万新媒体大咖的信息交流，资源交易平台，下载链接：http://a.app.qq.com/o/simple.jsp?pkgname=com.gxtc.huchuan"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber))
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }


    private fun applyFriends(userCode: String) {
        val dialogView = View.inflate(this, R.layout.dialog_author_layout, null)
        val editAuthor = dialogView.findViewById<View>(R.id.et_input) as EditText
        editAuthor.setText("我是${com.gxtc.huchuan.data.UserManager.getInstance().userName}")
        mDialog = DialogUtil.showAuthorDialog(this, dialogView) {
            mDialog?.dismiss()
            mPresenter?.applyFriends(userCode, editAuthor.text.toString())
        }
    }


    override fun showFriendsData(datas: MutableList<PersonInfoBean>) {
        sourceData.addAll(datas)
        recyclerview?.notifyChangeData(datas, mMaillSearchAdapter)
    }


    override fun showLoadMoreData(datas: MutableList<PersonInfoBean>?) {
        sourceData.addAll(datas!!)
        recyclerview.changeData(datas, mMaillSearchAdapter)
    }


    override fun showSearchResult(datas: MutableList<PersonInfoBean>?) {
        if(datas != null && datas.size != 0){
            recyclerview.notifyChangeData(datas, mMaillSearchAdapter)

        }else{
            ToastUtil.showShort(this, "暂无联系人")
        }
    }


    //加关注
    private fun follow(userCode: String) {
        mPresenter?.folowUser(userCode)
    }

    override fun showRefreshData(datas: MutableList<PersonInfoBean>?) {
        refreshlayout.isRefreshing = false
        recyclerview.notifyChangeData(datas, mMaillSearchAdapter)
    }


    override fun showNoLoadMore() {
        recyclerview.loadFinish()
    }

    override fun showFollowSuccess() {
        ToastUtil.showShort(MyApplication.getInstance(), "关注成功")
        EventBusUtil.post(NewFriendBean(EventJPushBean.RE_APPLY_FRIENDS) )
        personInfoBean?.isFollow = 1
        recyclerview.notifyItemChanged(curPosition)
    }

    override fun showApplySuccess() {
        ToastUtil.showShort(MyApplication.getInstance(), "申请好友成功")
        personInfoBean?.isFollow = 1
        recyclerview.notifyItemChanged(curPosition)
    }

    override fun setPresenter(presenter: AddressListContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() {
        baseLoadingView?.showLoading()
    }

    override fun showLoadFinish() {
        baseLoadingView?.hideLoading()
    }

    override fun showEmpty() {
        baseEmptyView?.showEmptyContent("暂无联系人")
    }

    override fun showReLoad() {}

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() {
        baseEmptyView?.showNetWorkView {
            baseEmptyView.hideEmptyView()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }
}
