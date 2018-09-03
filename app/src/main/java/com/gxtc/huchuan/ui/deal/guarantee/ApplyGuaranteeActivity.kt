package com.gxtc.huchuan.ui.deal.guarantee

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.flyco.dialog.listener.OnBtnClickL
import com.flyco.dialog.widget.NormalDialog
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.GuaranteeImageAdapter
import com.gxtc.huchuan.bean.UploadResult
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.LoadHelper
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.im.ui.ConversationListActivity
import com.gxtc.huchuan.ui.circle.dynamic.CirclePhothViewActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.utils.*
import com.gxtc.huchuan.widget.DividerItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message
import io.rong.message.TextMessage
import kotlinx.android.synthetic.main.activity_apply_guarantee.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File

/**
 * 申请担保页面
 * 伍玉南
 */
class ApplyGuaranteeActivity : BaseTitleActivity(), View.OnClickListener, ApplyGuaranteeContract.View, PictureConfig.OnSelectResultCallback, TextWatcher {

    companion object {
        @JvmField val DBFEE_BUYER = 0
        @JvmField val DBFEE_SELLER = 1
        @JvmField val DBFEE_ALL = 2
        @JvmField val CREATESOURCE_FROM_DEAL = "1" //1资源交易主页创建的快速交易帖
        @JvmField val CREATESOURCE_FROM_PERSON = "2" //2个人主页创建的快速交易帖子

        @JvmField val TYPE_BUYER = 0        //0买家   1是卖家
        @JvmField val TYPE_SELL = 1

        //直接跟某个用户发起担保交易
        @JvmStatic
        fun startActivity(context: Context, userCode: String?, userName: String?,createSource:String?) =
                Intent(context, ApplyGuaranteeActivity::class.java)
                .apply { putExtra("userCode", userCode) }
                .apply { putExtra("userName", userName) }
                .apply { putExtra("createSource", createSource) }
                .apply { context.startActivity(this) }

    }
    private val request_choose_friend = 100

    private val MAX_IMG = 5
    private var imageCount = 5

    private var role = 0               //0买家   1是卖家
    private var buyWay = -1             //哪一方出担保费  2 平摊  1 卖家出  0 买家出
    private var dbFeePrice = 0f         //担保费价格
    private var targetUserCode: String ? = null    //目标userCode
    private var targetUserName: String ? = null
    private var createSource: String ? = null  //1资源交易主页创建的快速交易帖， 2个人主页创建的快速交易帖子

    private var isSource = false
    private var isCompressing = false  //判断是否正在压缩图片

    private var pathList: ArrayList<String> ? = null
    private var uploadImages: ArrayList<String> ? = null    //待上传图片列表

    private var mAdapter: GuaranteeImageAdapter ? = null
    private var mPresenter: ApplyGuaranteeContract.Presenter ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_guarantee)
        AndroidBug5497Workaround.assistActivity(this)
    }

    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        baseHeadView.showTitle(getString(R.string.title_apply_guarantee)).showBackButton(this)

        tv_buyer.isSelected = true

        btn_confirm.setOnClickListener(this)
        tv_choose.setOnClickListener(this)
        tv_buyer.setOnClickListener(this)
        tv_seller.setOnClickListener(this)
        tv_dbfee_seller.setOnClickListener(this)
        tv_dbfee_buyer.setOnClickListener(this)
        tv_dbfee_all.setOnClickListener(this)
        img_add_pic.setOnClickListener(this)

        targetUserCode = intent.getStringExtra("userCode")
        targetUserName = intent.getStringExtra("userName")
        createSource = intent.getStringExtra("createSource")
        targetUserCode?.apply { if(!this.isEmpty()) edit_userCode.setText(this) }

        mAdapter = GuaranteeImageAdapter(this, mutableListOf(), R.layout.item_guarantee_img)
        val divider = WindowUtil.dip2px(this, 5f)
        recyclerview?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, divider, resources.getColor(R.color.white)))
        recyclerview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview?.adapter = mAdapter
    }


    override fun initListener() {
        mAdapter?.setOnItemClickLisntener { _, _, position ->
            val intent = Intent(this@ApplyGuaranteeActivity, CirclePhothViewActivity::class.java)
            val uris = arrayListOf<Uri>()
            mAdapter!!.list.mapTo(uris) { Uri.fromFile(File(it)) }
            intent.putExtra("photo", uris)
            intent.putExtra("position", position)
            startActivityForResult(intent, 666)
        }

        edit_price.addTextChangedListener(this)
    }

    override fun initData() {
        ApplyGuaranteePresenter(this)
        pathList = arrayListOf()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.headBackButton -> {
                finish()
            }

            R.id.btn_confirm -> {
                showConfirmDialog()
            }

            R.id.tv_choose -> chooseFriends()

            R.id.tv_seller -> {
                changeBuyWay(TYPE_SELL)
            }

            R.id.tv_buyer -> {
                changeBuyWay(TYPE_BUYER)
            }

            R.id.tv_dbfee_seller -> {
                changeDbFee(DBFEE_SELLER)
            }

            R.id.tv_dbfee_buyer -> {
                changeDbFee(DBFEE_BUYER)
            }

            R.id.tv_dbfee_all -> {
                changeDbFee(DBFEE_ALL)
            }

            R.id.img_add_pic -> {
                chooseImage()
            }
        }
    }


    var confirmDialog: ConfirmGuaranteeDialog ? = null
    private fun showConfirmDialog() {
        if(edit_userCode.text.toString().isNullOrEmpty()){
            ToastUtil.showShort(this, getString(R.string.toast_user_code))
            return
        }

        if(edit_title.text.toString().isNullOrEmpty()){
            ToastUtil.showShort(this, getString(R.string.toast_no_title))
            return
        }

        if(edit_info.text.toString().isNullOrEmpty()){
            ToastUtil.showShort(this, getString(R.string.toast_no_infomation))
            return
        }


        if(edit_price.text.toString().isNullOrEmpty()){
            ToastUtil.showShort(this, getString(R.string.guarantee_no_price))
            return
        }

        if(dbFeePrice == 0f){
            ToastUtil.showShort(this, getString(R.string.guarantee_no_dbFee))
            return
        }

        if(edit_phone.text.toString().isNullOrEmpty()){
            ToastUtil.showShort(this, getString(R.string.label_input_phone))
            return
        }

        if(!RegexUtils.isMobileExact(edit_phone.text.toString())){
            ToastUtil.showShort(this, getString(R.string.incorrect_phone_format))
            return
        }

        val text = "你将给用户 ${targetUserName} 发出交易邀请，交易内容为：${edit_title.text}。 <br/>交易价格为：<font color='#FF0000'>${edit_price.text}</font>"
        //val text = "你将给用户${targetUserName}发出交易邀请，交易内容为：${edit_title.text} \n交易价格为：${edit_price.text}"
        confirmDialog = ConfirmGuaranteeDialog()
        confirmDialog?.content = text
        confirmDialog?.show(supportFragmentManager, ConfirmGuaranteeDialog::class.java.simpleName)
        confirmDialog?.clickListener = View.OnClickListener{
            uploadImage()
            confirmDialog?.dismiss()
        }
    }


    private fun uploadImage() = if(pathList?.size !=  0){
        baseLoadingView.showLoading()
        val files = arrayListOf<File>()
        pathList?.mapTo(files){
            File(it)
        }

        LoadHelper.uploadFiles(LoadHelper.UP_TYPE_IMAGE, object : LoadHelper.UploadCallback{
            override fun onUploadSuccess(result: UploadResult?) {
                baseLoadingView.hideLoading()
                result?.let {
                    uploadImages = arrayListOf()
                    uploadImages?.addAll(it.urls)
                }
                submit()
            }

            override fun onUploadFailed(errorCode: String?, msg: String?){
                ToastUtil.showShort(this@ApplyGuaranteeActivity, getString(R.string.comment_upload_image_failed))
                baseLoadingView.hideLoading()
            }

        }, null, *files.toArray(arrayOf()))

    }else{
        submit()
    }


    private fun submit(){
        val token = UserManager.getInstance().token
        val map = hashMapOf<String, String>()
        map.put("buyer", if(role == TYPE_SELL) "1" else "0")
        map.put("tradeType", "0")
        map.put("targetUserCode", edit_userCode.text.toString())
        map.put("title", edit_title.text.toString())
        map.put("content", edit_info.text.toString())
        map.put("price", edit_price.text.toString())
        map.put("contacts", edit_phone.text.toString())
        map.put("buyWay", buyWay.toString())
        map.put("createSource",  createSource!!)
        map.put("isAppointTr", "1")
        uploadImages?.let { map.put("introPics", it.toString().substring(1, it.toString().length - 1)) }
        token?.let { map.put("token", it) }

        mPresenter?.submint(map)
    }

    private var successDialog: NormalDialog ? = null
    override fun showSubmitSuccess(id: String) {
        val content = getString(R.string.guarantee_apply_success)
        val left = getString(R.string.guarantee_dialog_left)
        val right = getString(R.string.guarantee_dialog_right)
        successDialog = DialogUtil.showNormalDialogTwo(this, null, content, left, right,
                OnBtnClickL{
                    successDialog?.dismiss()
                    RongIM.getInstance().startPrivateChat(this@ApplyGuaranteeActivity, targetUserCode, targetUserName)
                    finish()
                },
                OnBtnClickL{
                    successDialog?.dismiss()
                    GuaranteeDetailedActivity.startActivity(this, id)
                    finish()
                })

    }


    private var mAlertDialog: AlertDialog ? = null
    private fun chooseImage(){
        val pers = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 10010, object:BaseTitleActivity.PermissionsResultListener {
            override fun onPermissionGranted() {
                val options = FunctionOptions.Builder()
                        .setType(FunctionConfig.TYPE_IMAGE)
                        .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                        .setImageSpanCount(3)
                        .setEnableQualityCompress(false) //是否启质量压缩
                        .setEnablePixelCompress(false)   //是否启用像素压缩
                        .setEnablePreview(true)          //是否打开预览选项
                        .setShowCamera(true)
                        .setPreviewVideo(true)
                        .setIsCrop(true)
                        .setAspectRatio(1, 1)
                        .setMaxSelectNum(imageCount)
                        .create()
                PictureConfig.getInstance().init(options).openPhoto(this@ApplyGuaranteeActivity, this@ApplyGuaranteeActivity)
            }

            override fun onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(this@ApplyGuaranteeActivity, false, null, getString(R.string.pre_storage_notice_msg)) { v ->
                    if (v.id == R.id.tv_dialog_confirm) {
                        JumpPermissionManagement.GoToSetting(this@ApplyGuaranteeActivity)
                    }
                    mAlertDialog?.dismiss()
                }
            } })
    }


    private fun changeDbFee(index: Int){
        buyWay = index
        tv_dbfee_seller.isSelected = index == DBFEE_SELLER
        tv_dbfee_buyer.isSelected = index == DBFEE_BUYER
        tv_dbfee_all.isSelected = index == DBFEE_ALL

        updataDbFee()
    }


    private fun changeBuyWay(index: Int){
        role = index
        tv_seller.isSelected = index == TYPE_SELL
        tv_buyer.isSelected = index == TYPE_BUYER

        updataDbFee()
    }


    private fun updataDbFee(){
        if(edit_price.text.toString().isNullOrEmpty()){
            return
        }

        dbFeePrice = edit_price.text.toString().toFloat() * Constant.GUARANTEE_SCALE

        //买家出
        if(buyWay == DBFEE_BUYER){
            if(role == TYPE_SELL){
                tv_dbfee_price?.text = "¥0"
            }else{
                tv_dbfee_price?.text = "¥${StringUtil.formatMoney(2, dbFeePrice.toDouble())}"
            }
        }

        //卖家出
        if(buyWay == DBFEE_SELLER){
            if(role == TYPE_SELL){
                tv_dbfee_price?.text = "¥${StringUtil.formatMoney(2, dbFeePrice.toDouble())}"
            }else{
                tv_dbfee_price?.text = "¥0"
            }
        }

        //均摊担保费
        if(buyWay == DBFEE_ALL){
            dbFeePrice /= 2
            tv_dbfee_price?.text = "¥${StringUtil.formatMoney(2, dbFeePrice.toDouble())}"
        }
    }


    //选择好友
    private fun chooseFriends() {
        ConversationListActivity.startActivity(this, request_choose_friend, Constant.SELECT_TYPE_GUARAN_DEAL, 12)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == request_choose_friend && resultCode == Activity.RESULT_OK && data != null){
            val bean = data.getParcelableExtra(Constant.INTENT_DATA) as? EventSelectFriendForPostCardBean
            bean?.let {
                edit_userCode.setText(it.targetId)
                targetUserName = it.name
                targetUserCode = it.targetId
                sendMessage(bean)
            }
        }

        if(requestCode == 666 && resultCode == Activity.RESULT_OK && data != null){
            val datas = data.getSerializableExtra(Constant.INTENT_DATA) as? ArrayList<*>
            for(del in datas!!){
                val uri = del as Uri
                for(old in mAdapter!!.list){
                    if(uri.path == old){
                        pathList?.remove(old)
                    }
                }
            }
            countImages()
            mAdapter!!.notifyChangeData(pathList)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        edit_price.removeTextChangedListener(this)
    }


    private fun sendMessage(bean: EventSelectFriendForPostCardBean) {
        if(!bean.liuyan.isNullOrEmpty()){
            val textMsg = TextMessage.obtain(bean.liuyan)
            val msg = ImMessageUtils.obtain(bean.targetId, bean.mType, textMsg)
            RongIM.getInstance().sendMessage(msg, bean.liuyan, bean.liuyan, object : IRongCallback.ISendMessageCallback{
                override fun onAttached(p0: Message?) = Unit

                override fun onSuccess(p0: Message?) = Unit

                override fun onError(p0: Message?, p1: RongIMClient.ErrorCode?) = Unit
            })
        }
    }


    override fun setPresenter(presenter: ApplyGuaranteeContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() = baseLoadingView.showLoading()

    override fun showLoadFinish() = baseLoadingView.hideLoading()

    override fun showEmpty() = Unit

    override fun showReLoad() = Unit

    override fun tokenOverdue() = GotoUtil.goToActivity(this, LoginAndRegisteActivity::class.java)

    override fun showError(info: String?) = ToastUtil.showShort(this, info)

    override fun showNetError() = Unit

    override fun onSelectSuccess(resultList: MutableList<LocalMedia>?) {
        resultList?.let { compressImage(it) }
    }

    override fun afterTextChanged(s: Editable?) = updataDbFee()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun compressImage(resultList: MutableList<LocalMedia>){
        //判断是否选择原图
        if(!isSource){
            if(pathList!!.size + resultList.size <= MAX_IMG){
                Observable.from(resultList)
                        .subscribeOn(Schedulers.io())
                        .concatMap(Func1<LocalMedia, Observable<File>>{ media ->
                            isCompressing = true
                            return@Func1 Luban.get(MyApplication.getInstance().applicationContext)
                                    .load(File(media.path))
                                    .putGear(com.luck.picture.lib.compress.Luban.FIRST_GEAR)
                                    .asObservable()
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ file ->
                            if(mAdapter != null){
                                isCompressing = false
                                pathList?.add(file.path)
                                mAdapter?.notifyChangeData(pathList)
                                countImages()
                            }
                        })
            }
        }else{
            if(pathList!!.size + resultList.size <= MAX_IMG){
                for(item in resultList){
                    isSource = item.isSource
                    pathList!!.add(item.path)
                }
            }

            mAdapter?.notifyChangeData(pathList)
            countImages()
        }
    }


    //计算剩余图片
    private fun countImages(){
        imageCount = MAX_IMG - pathList!!.size
        if(imageCount == 0) img_add_pic.visibility = View.INVISIBLE
    }

    override fun onSelectSuccess(media: LocalMedia?) = Unit


}
