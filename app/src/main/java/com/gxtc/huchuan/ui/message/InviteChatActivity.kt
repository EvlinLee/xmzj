package com.gxtc.huchuan.ui.message

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GsonUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.InviteChatAdapter
import com.gxtc.huchuan.adapter.InviteChatSelectAdapter
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.dialog.CreateGroupDialog
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MessageApi
import com.gxtc.huchuan.utils.ClickUtil
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.JumpPermissionManagement
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig
import kotlinx.android.synthetic.main.activity_invite_chat.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * 邀请群聊
 */
class InviteChatActivity : BaseTitleActivity(), InviteChatContract.View, View.OnClickListener, PictureConfig.OnSelectResultCallback {

    lateinit var mAdapter : InviteChatAdapter
    lateinit var mSelectAdpater : InviteChatSelectAdapter
    lateinit var mSouce : MutableList<FocusBean>
    lateinit var mSearchData : MutableList<FocusBean>

    var mPresenter : InviteChatContract.Presenter ? = null
    var addPrineds : Int = 0
    var groupChatId = ""
    var id :String? = null
    private var  mAlertDialog: AlertDialog? = null
    private var dialog : CreateGroupDialog ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_chat)
    }

    override fun initView() {
        addPrineds = intent.getIntExtra("addPrineds",0)
        id = intent.getStringExtra("groupChatId")

        when(addPrineds){
            0 ->  {
                baseHeadView?.showTitle("发起群聊")
                groupChatId = ""
            }
            1 -> {
                baseHeadView?.showTitle("选择好友")
                groupChatId = id!!;
            }
            2 -> {
                baseHeadView?.showTitle("删除成员")
                groupChatId = id!!
            }
        }

        baseHeadView?.showBackButton(this)
        baseHeadView?.showHeadRightButton("确定",this)
        swipeLayout?.setColorSchemeResources(*Constant.REFRESH_COLOR)

        mAdapter = InviteChatAdapter(this, mutableListOf(), R.layout.item_invite_chat, addPrineds)
        mSelectAdpater = InviteChatSelectAdapter(this, mutableListOf(),R.layout.item_invite_chat_select)

        rv_select?.layoutManager = LinearLayoutManager(this,0,false)
        rv_select?.adapter = mSelectAdpater
        rv_member?.layoutManager = LinearLayoutManager(this,1,false)
        rv_member?.addFootView(R.layout.model_footview_loadmore)
        rv_member?.adapter = mAdapter

    }

    override fun initListener() {
        swipeLayout?.setOnRefreshListener {
            rv_member?.reLoadFinish()
            mPresenter?.refreshData()
        }
        rv_member?.setOnLoadMoreListener {
            mPresenter?.loadMoreData()
        }
        mAdapter.setOnReItemOnClickListener { itemView, position ->
            val check = itemView.findViewById(R.id.checkbox) as? CheckBox
            val bean = check?.tag as? FocusBean
            if(addPrineds != 0 && "1" == bean?.isGroupMember){
                return@setOnReItemOnClickListener
            }

            if(check?.isChecked!!){
                removeData(bean!!)
            }else{
                check.isChecked = true
                insertData(bean!!)
            }
            rv_member?.notifyItemChanged(position)
            SyncSource()
        }

        mAdapter.checkListener = View.OnClickListener { view->
            val check = view as? CheckBox
            val bean = check?.tag as? FocusBean
            bean?.isSelect = check?.isChecked!!
            if(check.isChecked){
                insertData(bean!!)
            }else{
                removeData(bean!!)
            }
            SyncSource()
        }

        mSelectAdpater.setOnReItemOnClickListener { _, position ->
            for(bean in mAdapter.list){
                if(bean == mSelectAdpater.list[position]){
                    bean.isSelect = false
                    mSelectAdpater.removeData(position)
                    rv_member?.notifyItemChanged(mAdapter.list.indexOf(bean))
                    break
                }
            }
        }

        searchView?.onCloseListener = SearchView.OnCloseListener {
            rv_member?.notifyChangeData(mSouce,mAdapter)
            false
        }

        swipeLayout?.setOnRefreshListener {
            rv_member?.reLoadFinish()
            mPresenter?.refreshData()
        }
        rv_member?.setOnLoadMoreListener {
            mPresenter?.loadMoreData()
        }
        mAdapter.setOnReItemOnClickListener { itemView, position ->
            val check = itemView.findViewById(R.id.checkbox) as? CheckBox
            val bean = check?.tag as? FocusBean
            if(addPrineds != 0 && "1" == bean?.isGroupMember){
                return@setOnReItemOnClickListener
            }

            if(check?.isChecked!!){
                removeData(bean!!)
            }else{
                check.isChecked = true
                insertData(bean!!)
            }
            rv_member?.notifyItemChanged(position)
            SyncSource()
        }

        mAdapter.checkListener = View.OnClickListener { view->
            val check = view as? CheckBox
            val bean = check?.tag as? FocusBean
            bean?.isSelect = check?.isChecked!!
            if(check.isChecked){
                insertData(bean!!)
            }else{
                removeData(bean!!)
            }
            SyncSource()
        }

        mSelectAdpater.setOnReItemOnClickListener { _, position ->
            for(bean in mAdapter.list){
                if(bean == mSelectAdpater.list[position]){
                    bean.isSelect = false
                    mSelectAdpater.removeData(position)
                    rv_member?.notifyItemChanged(mAdapter.list.indexOf(bean))
                    break
                }
            }
        }

        searchView?.onCloseListener = SearchView.OnCloseListener {
            rv_member?.notifyChangeData(mSouce,mAdapter)
            false
        }

        searchView?.onQueryTextListener = object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!TextUtils.isEmpty(query)){
                    mPresenter?.queryUser(query!!)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.headBackButton-> finish()

            R.id.headRightButton-> {
                when(addPrineds){
                    0 -> showDialog()
                    1 -> {
                        val intent = Intent()
                        val list = ArrayList<FocusBean>()
                        for(i in mSelectAdpater.list?.indices!!){
                            list.add(mSelectAdpater.list?.get(i)!!)
                        }
                        intent.putExtra("seclectData", list );
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                    2 -> {
                        val intent = Intent()
                        val list = ArrayList<FocusBean>()
                        for(i in mSelectAdpater.list?.indices!!){
                            list.add(mSelectAdpater.list?.get(i)!!)
                        }
                        intent.putExtra("seclectData", list );
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                }
            }

            //选择群头像
            R.id.head_pic-> selectPicture()

            //创建群聊
            R.id.btn_next-> {
                if(!ClickUtil.isFastClick()) {
                    createGroup()
                }
            }
        }
    }


    override fun initData() {
        InviteChatPresenter(this,addPrineds)
        if(2 == addPrineds){
            //踢人
            mPresenter?.getGroupMeamberData(0,groupChatId)
        }else{
            //拉人
            mPresenter?.getData(groupChatId)
        }
        mSouce = mutableListOf()
        mSearchData = mutableListOf()
    }

    override fun showRefreshData(datas: MutableList<FocusBean>?) {
        swipeLayout?.isRefreshing = false
        if(datas != null){
            //这里也要同步下刷新的数据
            for(bean in datas){
                for(search in mSouce){
                    if(bean == search){
                        bean.isSelect = search.isSelect
                    }
                }
            }
            mSouce.clear()
            mSouce.addAll(datas)
            rv_member?.notifyChangeData(datas,mAdapter)
        }
    }

    override fun showLoadMoreData(datas: MutableList<FocusBean>?) {
        //有可能搜索到的结果是原来列表里面没有分页过的，这里也要同步下加载更多的数据
        for(bean in datas!!){
            for(search in mSearchData){
                if(bean == search){
                    bean.isSelect = search.isSelect
                }
            }
        }
        mSouce.addAll(datas)
        rv_member?.changeData(datas,mAdapter)
    }

    override fun showNoLoadMore() {
        rv_member?.loadFinish()
    }

    override fun showData(datas: MutableList<FocusBean>) {
        mSouce.clear()
        mSouce.addAll(datas)
        rv_member?.notifyChangeData(datas,mAdapter)
    }

    //显示查询结果
    override fun showQuery(datas: MutableList<FocusBean>) {
        //如果搜索到已经选中的人，那么要默认选中
        for(bean in datas){
            if(mSelectAdpater.list.contains(bean)){
                bean.isSelect = true
            }
        }
        mSearchData.addAll(datas)
        rv_member?.notifyChangeData(datas,mAdapter)
    }

    //上传图片回调
    override fun showPic(url: String) {
        dialog?.picUrl = url
    }

    override fun showCreateResult(success: Boolean, bean: MessageBean?, error: String?) =
        if(success){
            ToastUtil.showShort(this,"创建群聊成功")
            finish()
        }else{
            ToastUtil.showShort(this,error)
        }


    override fun setPresenter(presenter: InviteChatContract.Presenter?) {
        mPresenter = presenter
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
        baseEmptyView.showNetWorkView{
            baseEmptyView.hideEmptyView()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        mAlertDialog = null;
    }

    private fun removeData(bean : FocusBean){
        if(mSelectAdpater.list?.contains(bean)!!){
            for(i in mSelectAdpater.list?.indices!!){
                if(bean.equals(mSelectAdpater.list?.get(i))){
                    mSelectAdpater.removeData(i)
                    bean.isSelect = false
                    break
                }
            }
        }
    }

    private fun insertData(bean : FocusBean){
        if(!mSelectAdpater.list?.contains(bean)!!){
            bean.isSelect = true
            mSelectAdpater.addData(bean)
        }
    }

    //如果选中查询的结果需要跟原来的数据源同步
    private fun SyncSource(){
        if(mSearchData.size > 0){
            for(bean in mSouce){
                for(search in mSearchData){
                    if(bean == search){
                        bean.isSelect = search.isSelect
                    }
                }
            }
        }
    }

    //显示填写群信息弹窗
    private fun showDialog() {
        if(mSelectAdpater.list.size == 0){
            ToastUtil.showShort(this,"请选择用户")
            return
        }

        if(dialog == null){
            dialog = CreateGroupDialog()
            dialog!!.setOnClickListener(this)
        }
        dialog?.show(supportFragmentManager,CreateGroupDialog::class.java.simpleName)
    }


    //选择群头像
    private fun selectPicture() {
        val array = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        performRequestPermissions("此应用需要读取相机和文件夹权限",array,Constant.requestCode.REQUEST_CODE_AVATAR,object :PermissionsResultListener{
            override fun onPermissionGranted() {
                val options = FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_SINGLE)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(false) //是否启质量压缩
                        .setEnablePixelCompress(false) //是否启用像素压缩
                        .setEnablePreview(true) // 是否打开预览选项
                        .setShowCamera(true)
                        .setPreviewVideo(true)
                        .setIsCrop(true)
                        .setAspectRatio(1, 1)
                        .create()
                PictureConfig.getInstance().init(options).openPhoto(this@InviteChatActivity, this@InviteChatActivity)
            }

            override fun onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(this@InviteChatActivity, false, null, getString(R.string.pre_scan_notice_msg)
                ) { v ->
                    if (v.id == R.id.tv_dialog_confirm) {
                        JumpPermissionManagement.GoToSetting(this@InviteChatActivity)
                    }
                    mAlertDialog!!.dismiss()
                }
            }
        })
    }

    //创建群组
    private fun createGroup() {
        val name = dialog?.name
        val picUrl = dialog?.picUrl
        if(TextUtils.isEmpty(name)) {
            ToastUtil.showShort(this,"群聊名字不能为空")
            return
        }
        if(TextUtils.isEmpty(picUrl)) {
            ToastUtil.showShort(this,"请选择头像")
            return
        }
        mPresenter?.createGroup(name,picUrl,mSelectAdpater.list)
        dialog?.dismiss()
    }


    override fun onSelectSuccess(resultList: MutableList<LocalMedia>?) {

    }

    override fun onSelectSuccess(media: LocalMedia?) {
        mPresenter?.uploadPic(media?.path)
    }



}

