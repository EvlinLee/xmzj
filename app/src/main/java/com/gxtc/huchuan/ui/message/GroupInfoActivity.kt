package com.gxtc.huchuan.ui.message

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.*
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.GroupInfoAdapter
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.bean.SentImageBean
import com.gxtc.huchuan.bean.event.EventGroupBean
import com.gxtc.huchuan.bean.event.EventSelectFriendBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MessageApi
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.im.ui.ConversationListActivity
import com.gxtc.huchuan.ui.circle.dynamic.IssueDynamicActivity
import com.gxtc.huchuan.ui.circle.erweicode.ErWeiCodeActivity
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoActivity.REQUEST_CODE_AVATAR
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.message.ImageMessage
import kotlinx.android.synthetic.main.activity_group_info.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * 群聊信息
 */
class GroupInfoActivity : BaseTitleActivity(), GroupInfoContract.View, View.OnClickListener, PictureConfig.OnSelectResultCallback {

    private var mAlertDialog: AlertDialog? = null
    private var mSwitch: Switch? = null
    private var groupNmae: TextView? = null
    private var groupPic: ImageView? = null
    private var userCodes: ArrayList<FocusBean>? = null
    private var mapData = hashMapOf<String, String>()
    private var nameMap = hashMapOf<String, String>()
    private var id: String? = null
    private var groupQr: String? = null
    private var count: Int = 0
    private var roleType: Int = 0     //0:普通；2：群主
    private var adapter: GroupInfoAdapter? = null
    private var mPresenter: GroupInfoContract.Presenter? = null
    var mBean: MessageBean? = null
    var SHARE_REAL_PATH: String? = null
    var countHasChange = false
    val handler: Handler = MyHandler(this)

    companion object {
        val SAVE_REAL_PATH = FileStorage.getImgCacheFile()!!.path

        @JvmStatic
        fun startActivity(context: Activity, id: String, count: Int) {
            val intent = Intent(context, GroupInfoActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("count", count)
            context.startActivityForResult(intent, 1001)
        }

        class MyHandler(mGroupInfoActivity: GroupInfoActivity) : Handler() {
            var weakReference: WeakReference<GroupInfoActivity>? = null
            var mGroupInfoActivity: GroupInfoActivity? = null

            init {
                weakReference = WeakReference<GroupInfoActivity>(mGroupInfoActivity)
            }

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                if (weakReference?.get() != null) {
                    mGroupInfoActivity = weakReference?.get()
                    if (msg?.obj != null) {
                        val mSentImageBean = msg.obj as SentImageBean
                        val bitmap = mSentImageBean.getBitmap()
                        val intent = mSentImageBean.getIntent()
                        var uri: Uri? = null
                        try {
                            if (bitmap != null) {
                                uri = Uri.parse(mGroupInfoActivity?.mBean?.groupQr)
                                ImageUtils.saveImageToSD(mGroupInfoActivity, SAVE_REAL_PATH + uri!!.path, bitmap, 100)
                                mGroupInfoActivity?.SHARE_REAL_PATH = SAVE_REAL_PATH + uri.path
                                if (intent != null) {
                                    val bean = intent.getParcelableExtra<EventSelectFriendBean>(Constant.INTENT_DATA)
                                    mGroupInfoActivity?.sendRongIm(bean)
                                } else {
                                    // 发二维码到圈子动态
                                    val intent1 = Intent(mGroupInfoActivity, IssueDynamicActivity::class.java)
                                    intent1.putExtra("select_type", "5")
                                    intent1.putExtra("type", "1")
                                    intent1.putExtra("picUrl", mGroupInfoActivity?.SHARE_REAL_PATH)
                                    mGroupInfoActivity?.startActivity(intent1)
                                    mGroupInfoActivity?.finish()
                                }
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)
    }

    override fun initView() {
        id = intent.getStringExtra("id")
        count = intent.getIntExtra("count", 0)

        baseHeadView.showTitle("聊天信息($count) ")
        baseHeadView.showBackButton(this)

        findViewById(R.id.tv_all_member)?.setOnClickListener(this)
        findViewById(R.id.tv_group_pic)?.setOnClickListener(this)
        findViewById(R.id.tv_group_name)?.setOnClickListener(this)
        findViewById(R.id.tv_group_qr)?.setOnClickListener(this)
        findViewById(R.id.tv_invate_friends)?.setOnClickListener(this)
        findViewById(R.id.tv_clear_conversation)?.setOnClickListener(this)
        findViewById(R.id.tv_logout)?.setOnClickListener(this)

        mSwitch = findViewById(R.id.sw_message_free) as Switch
        groupNmae = findViewById(R.id.group_name) as TextView
        groupPic = findViewById(R.id.iv_group_avatar) as ImageView
    }

    override fun initListener() {
        super.initListener()
        mSwitch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            setMessageNotif(isChecked)
        })
    }

    override fun initData() {
        GroupInfoPresenter(this)
        mPresenter?.getRoleType(id!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.headBackButton -> {
                if (countHasChange) {
                    val intent = Intent()
                    intent.putExtra("count", count)
                    setResult(Activity.RESULT_OK, intent)
                }
                finish()
            }

            //查看更多成员
            R.id.tv_all_member -> {
                val intent = Intent(this, AllGroupInfoMemberActivity::class.java)
                intent.putExtra("targetId", id)
                intent.putExtra("count", count)
                startActivity(intent)
            }

            //群聊头像
            R.id.tv_group_pic -> chooseImg()

            //群聊名称
            R.id.tv_group_name -> {
                val view = this.getLayoutInflater().inflate(R.layout.group_name, null)
                //内容
                val etContent = view.findViewById(R.id.et_input) as EditText
                mAlertDialog = DialogUtil.showGriupDialog(this, view, View.OnClickListener {
                    if (TextUtils.isEmpty(etContent.text.toString())) {
                        ToastUtil.showShort(this@GroupInfoActivity, "群聊名称不能为空")
                    } else {
                        editGroupNameOrPic(etContent.text.toString(), "1")
                    }
                })
            }

            //群二维码
            R.id.tv_group_qr -> {
                mBean?.run {
                    ErWeiCodeActivity.startActivity(this@GroupInfoActivity, ErWeiCodeActivity.TYPE_CUSTOM_GROUP, groupId.toInt(), "")
                }
            }

            //邀请好友
            R.id.tv_invate_friends -> {
                showShareDialog()
            }

            //清除聊天记录
            R.id.tv_clear_conversation -> {
                if (UserManager.getInstance().isLogin) {
                    mAlertDialog = DialogUtil.showDeportDialog(this, false, null, "确定要清除聊天记录") { view ->
                        if (view.id == R.id.tv_dialog_confirm) {
                            removeConversationRecord()
                        }
                        mAlertDialog?.dismiss()
                    }
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity::class.java,
                            Constant.requestCode.NEWS_AUTHOR)
                }
            }

            //退出群聊
            R.id.tv_logout -> {
                var notice = ""
                if(roleType == 2){
                    notice  = "圈主退出就会解散群聊,"
                }
                if (UserManager.getInstance().isLogin) {
                    mAlertDialog = DialogUtil.showDeportDialog(this, false, null, notice+"确定退出群聊?".trim()) { v ->
                        if (v.id == R.id.tv_dialog_confirm) {
                            logOut()
                        }
                        mAlertDialog?.dismiss()
                    }
                } else {
                    GotoUtil.goToActivityForResult(this, LoginAndRegisteActivity::class.java,
                            Constant.requestCode.NEWS_AUTHOR)
                }
            }
        }
    }

    private fun showShareDialog() {
        val pers = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        performRequestPermissions(getString(R.string.txt_card_permission), pers, 2200, object : PermissionsResultListener {

            override fun onPermissionGranted() {
                mBean?.let {
                    val utils = UMShareUtils(this@GroupInfoActivity)
                    utils.shareGroupCodeImg(mBean?.groupQr)
                    utils.setOnItemClickListener(object : UMShareUtils.OnItemClickListener {
                        override fun onItemClick(flag: Int) {
                            when (flag) {
                                0 -> {
                                    ErWeiCodeActivity.startActivity(this@GroupInfoActivity, ErWeiCodeActivity.TYPE_CUSTOM_GROUP,it.groupId.toInt(), "")
                                }
                                1 -> {
                                    createByComprs(mBean?.groupQr, null, 1)
                                }
                                2 -> {
                                    ConversationListActivity.startActivity(this@GroupInfoActivity, ConversationActivity.STATUE_INVATE_CARD, Constant.SELECT_TYPE_SHARE)
                                }
                            }
                        }
                    })
                }
            }

            override fun onPermissionDenied() {
                mAlertDialog = DialogUtil.showDeportDialog(this@GroupInfoActivity, false, null, getString(R.string.pre_scan_notice_msg)) { v ->
                    if (v.id == R.id.tv_dialog_confirm) {
                        JumpPermissionManagement.GoToSetting(this@GroupInfoActivity)
                    }
                    mAlertDialog?.dismiss()
                }
            }
        })
    }

    private fun editGroupNameOrPic(groupTypeName: String, type: String) {
        nameMap.put("token", UserManager.getInstance().token)
        nameMap.put("groupChatId", id!!)
        when (type) {
            "1" -> nameMap.put("groupName", groupTypeName)
            "2" -> nameMap.put("groupPic", groupTypeName)
        }
        val sub = MessageApi.getInstance()
                .editGroupName(nameMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any?) {
                        when (type) {
                            "1" -> {
                                ToastUtil.showShort(this@GroupInfoActivity, "修改群聊名称成功")
                                groupNmae?.text = groupTypeName
                                mAlertDialog?.dismiss()
                            }
                            "2" -> {
                                ToastUtil.showShort(this@GroupInfoActivity, "修改群聊头像成功")
                                ImageHelper.loadImage(this@GroupInfoActivity, groupPic, groupTypeName)
                            }
                        }
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        if ("1" == type) mAlertDialog?.dismiss()
                        ToastUtil.showShort(this@GroupInfoActivity, message)
                    }

                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    //选择照片
    private fun chooseImg() {
        val pers = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        performRequestPermissions("此应用需要读取相机和文件夹权限", pers, REQUEST_CODE_AVATAR,
                object : BaseTitleActivity.PermissionsResultListener {
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
                        PictureConfig.getInstance().init(options).openPhoto(this@GroupInfoActivity, this@GroupInfoActivity)
                    }

                    override fun onPermissionDenied() {
                        mAlertDialog = DialogUtil.showDeportDialog(this@GroupInfoActivity, false, null, getString(R.string.pre_scan_notice_msg),
                                View.OnClickListener() { v ->
                                    if (v.getId() == R.id.tv_dialog_confirm) {
                                        JumpPermissionManagement.GoToSetting(this@GroupInfoActivity);
                                        mAlertDialog?.dismiss();
                                    }
                                });


                    }
                })
    }

    //设置消息免打扰
    private fun setMessageNotif(isChecked: Boolean) {
        val cns: Conversation.ConversationNotificationStatus
        if (isChecked) {
            cns = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB
        } else {
            cns = Conversation.ConversationNotificationStatus.NOTIFY
        }
        RongIM.getInstance().setConversationNotificationStatus(Conversation.ConversationType.GROUP,
                id, cns,
                object : RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                    override fun onSuccess(conversationNotificationStatus: Conversation.ConversationNotificationStatus) =
                            Unit

                    override fun onError(errorCode: RongIMClient.ErrorCode) = Unit
                })
    }

    private fun removeConversationRecord() {
        RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, id, object : RongIMClient.ResultCallback<Boolean>() {
            override fun onSuccess(aBoolean: Boolean?) {
                if (aBoolean!!) {
                    ToastUtil.showShort(this@GroupInfoActivity, "清除聊天记录成功")
                }
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) =
                    ToastUtil.showShort(this@GroupInfoActivity, RIMErrorCodeUtil.handleErrorCode(errorCode))
        })
    }

    private fun logOut() {
        //群主退群：publish/userGroup/dissolve.do
        //群主退出跟普通成员退出调的接口不一样
        var sub:Subscription? = null
        if (roleType == 2) {
             sub = MessageApi.getInstance()
                    .dissolve(UserManager.getInstance().token, id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                        override fun onSuccess(data: Any?) = exitFromRim()

                        override fun onError(errorCode: String?, message: String?) =
                                ToastUtil.showShort(this@GroupInfoActivity, message)

                    }))
        }else{
             sub = MessageApi.getInstance()
                    .quitGroup(UserManager.getInstance().userCode, id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                        override fun onSuccess(data: Any?) = exitFromRim()

                        override fun onError(errorCode: String?, message: String?) =
                                ToastUtil.showShort(this@GroupInfoActivity, message)

                    }))
        }
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    private fun exitFromRim() =
            RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, id, object : RongIMClient.ResultCallback<Boolean>() {
                override fun onSuccess(aBoolean: Boolean?) {
                    if (aBoolean!!) {
                        ToastUtil.showShort(this@GroupInfoActivity, "退出群聊成功")
                        EventBusUtil.post(EventGroupBean(id, true))
                        this@GroupInfoActivity.finish()
                    }
                }

                override fun onError(errorCode: RongIMClient.ErrorCode) =
                        ToastUtil.showShort(this@GroupInfoActivity, RIMErrorCodeUtil.handleErrorCode(errorCode))
            })

    private fun sendRongIm(bean1: EventSelectFriendBean) {
        val imageMessage = ImageMessage.obtain(Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)), Uri.fromFile(FileUtil.createFile(SHARE_REAL_PATH)), true)
        RongIM.getInstance().sendImageMessage(bean1.mType, bean1.targetId, imageMessage, "[图片]", "[图片]", object : RongIMClient.SendImageMessageCallback() {

            override fun onAttached(message: io.rong.imlib.model.Message) = Unit

            override fun onError(message: io.rong.imlib.model.Message, errorCode: RongIMClient.ErrorCode) =
                    ToastUtil.showShort(MyApplication.getInstance(), "分享失败: " + RIMErrorCodeUtil.handleErrorCode(errorCode))

            override fun onSuccess(message: io.rong.imlib.model.Message) {
                ToastUtil.showShort(MyApplication.getInstance(), "分享成功")
                if (!TextUtils.isEmpty(bean1.liuyan)) {
                    RongIMTextUtil.relayMessage(bean1.liuyan, bean1.targetId, bean1.mType);
                }
            }

            override fun onProgress(message: io.rong.imlib.model.Message, i: Int) = Unit
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //图片路径
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == 100) {
            userCodes = data?.getSerializableExtra("seclectData") as ArrayList<FocusBean>
            joinGroup()
        }
        if (requestCode == 200) {
            userCodes = data?.getSerializableExtra("seclectData") as ArrayList<FocusBean>
            cleanGroup()
        }
        if (requestCode == ConversationActivity.STATUE_INVATE_CARD && resultCode == Activity.RESULT_OK) {
            createByComprs(mBean?.groupQr, data, 0)
        }
    }

    fun createByComprs(url: String?, data: Intent?, isShare: Int) {
        Glide.with(this).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val msg = Message.obtain()
                val mSentImageBean = SentImageBean()
                mSentImageBean.setBitmap(resource)
                mSentImageBean.setIntent(data)
                mSentImageBean.setIsShare(isShare)
                msg.obj = mSentImageBean
                handler.sendMessage(msg)
            }
        })
    }

    private fun joinGroup() {
        val sb = StringBuilder()
        for (bean in userCodes!!) {
            sb.append(bean.userCode).append(",")
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
            mapData.put("userCode", sb.toString())
        }
        mapData.put("groupChatId", id!!)
        val sub = MessageApi.getInstance()
                .joinGroup(mapData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any?) {
                        countHasChange = true
                        count = userCodes?.size!! + count
                        baseHeadView.showTitle("聊天信息($count ) ")
                        mPresenter?.getMembers(id!!)
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@GroupInfoActivity, message)
                    }

                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }


    //踢人
    private fun cleanGroup() {
        mapData.clear()
        val sb = StringBuilder()
        for (bean in userCodes!!) {
            sb.append(bean.userCode).append(",")
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
            mapData.put("userCode", sb.toString())
        }
        mapData.put("groupChatId", id!!)
        mapData.put("token", UserManager.getInstance().token)
        val sub = MessageApi.getInstance()
                .cleanGroup(mapData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(object : ApiCallBack<Any>() {
                    override fun onSuccess(data: Any?) {
                        countHasChange = true
                        count = count - userCodes?.size!!
                        baseHeadView.showTitle("聊天信息($count ) ")
                        mPresenter?.getMembers(id!!)
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        ToastUtil.showShort(this@GroupInfoActivity, message)
                    }

                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    override fun showPic(url: String) = editGroupNameOrPic(url, "2")

    override fun showMembers(datas: MutableList<MessageBean>) {
        count = datas.size
        baseHeadView.showTitle("聊天信息($count) ")
        if (count >= 40) {
            tv_all_member?.visibility = View.VISIBLE
            tv_all_member?.setOnClickListener(this)
        }
        MessageBean().let {
            it.userCode = "+"
            tv_group_name.visibility = View.GONE
            tv_group_pic.visibility = View.GONE
            line1.visibility = View.GONE
            line2.visibility = View.GONE
            datas.add(it)
        }

        //群主有踢人的权利
        if (roleType == 2) {
            MessageBean().let {
                it.userCode = "-"
                tv_group_name.visibility = View.VISIBLE
                tv_group_pic.visibility = View.VISIBLE
                line1.visibility = View.VISIBLE
                line2.visibility = View.VISIBLE
                datas.add(it)
            }
        }

        adapter = GroupInfoAdapter(this, datas, R.layout.item_group_chat_member)
        chat_menber_list?.adapter = adapter

        //0:普通；2：群主
        chat_menber_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (roleType) {
            //普通成员只有拉人
                0 -> {
                    if (position != adapter!!.count - 1) PersonalInfoActivity.startActivity(this@GroupInfoActivity, adapter!!.datas.get(position).userCode)
                    else {
                        val intent = Intent(this@GroupInfoActivity, InviteChatActivity::class.java)
                        intent.putExtra("addPrineds", 1)
                        intent.putExtra("groupChatId", this@GroupInfoActivity.id)
                        this@GroupInfoActivity.startActivityForResult(intent, 100);
                    }
                }

            //群主拉人或是踢人
                2 -> {
                    when (position) {
                        adapter!!.count - 1 -> {
                            //踢人
                            val intent = Intent(this@GroupInfoActivity, InviteChatActivity::class.java)
                            intent.putExtra("addPrineds", 2)
                            intent.putExtra("groupChatId", this@GroupInfoActivity.id)
                            this@GroupInfoActivity.startActivityForResult(intent, 200);
                        }
                        adapter!!.count - 2 -> {
                            //拉人
                            val intent = Intent(this@GroupInfoActivity, InviteChatActivity::class.java)
                            intent.putExtra("addPrineds", 1)
                            intent.putExtra("groupChatId", this@GroupInfoActivity.id)
                            this@GroupInfoActivity.startActivityForResult(intent, 100);
                        }
                        else -> PersonalInfoActivity.startActivity(this@GroupInfoActivity, adapter!!.datas.get(position).userCode)
                    }
                }
            }
        }
    }


    override fun showRoleType(bean: MessageBean) {
        mBean = bean;
        roleType = bean.roleType
        groupQr = bean.groupQr
        mPresenter?.getMembers(id!!)
        setData(bean)
    }

    private fun setData(bean: MessageBean) {
        groupNmae?.text = bean.groupName
        ImageHelper.loadImage(this, groupPic, bean.groupPic)
    }

    override fun setPresenter(presenter: GroupInfoContract.Presenter?) {
        mPresenter = presenter
    }

    override fun showLoad() = baseLoadingView.showLoading(true)

    override fun showLoadFinish() = baseLoadingView.hideLoading()

    override fun showEmpty() = Unit

    override fun showReLoad() = Unit

    override fun showError(info: String?) {
        ToastUtil.showShort(this, info)
    }

    override fun showNetError() = Unit

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.destroy()
        mAlertDialog = null
        RxTaskHelper.getInstance().cancelTask(this)
    }

    override fun onSelectSuccess(resultList: MutableList<LocalMedia>?) = Unit

    override fun onSelectSuccess(media: LocalMedia?) {
        mPresenter?.uploadPic(media?.path)
    }

}
