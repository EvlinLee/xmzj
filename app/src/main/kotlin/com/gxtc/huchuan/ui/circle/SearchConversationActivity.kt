package com.gxtc.huchuan.ui.circle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.SearchConversationAdapter
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.SearchChatBean
import com.gxtc.huchuan.bean.dao.User
import com.gxtc.huchuan.bean.event.EventSelectFriendBean
import com.gxtc.huchuan.bean.event.EventSelectFriendForPostCardBean
import com.gxtc.huchuan.bean.event.EventShareMessage
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.ConfirmRelayDialog
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.AllApi
import com.gxtc.huchuan.http.service.CircleApi
import com.gxtc.huchuan.http.service.MineApi
import com.gxtc.huchuan.im.MyConversationListFragment
import com.gxtc.huchuan.im.listener.MyConversationListListener
import io.rong.imkit.RongIM
import io.rong.imkit.model.UIConversation
import io.rong.imkit.tools.CharacterParser
import io.rong.imkit.userInfoCache.RongUserInfoManager
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_search_conversation.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers

/**
 * 搜索会话列表页面
 */
class SearchConversationActivity : BaseTitleActivity(), View.OnClickListener,TextWatcher {

    var editSearch : EditText ? = null

    var mAdapter : SearchConversationAdapter ? = null
    var resultList : MutableList<SearchChatBean>
    var conversationList : MutableList<UIConversation> ? = null

    var start = 0
    var cardPosion = 0
    var isSelect : Int = -1
    var searchModel = 0     //如果是0 则搜索好友跟会话列表的数据 如果是1 则只搜索我的好友列表
    var targetId: String? = null
    var conversationListListener : MyConversationListListener ? = null
    var mConversationType:Conversation.ConversationType? = null
    var mRelayDialog:ConfirmRelayDialog? = null

    init {
        resultList = mutableListOf()
        conversationListListener = MyConversationListListener()
    }

    companion object {
        //如果是0 则搜索好友跟会话列表的数据 如果是1 则只搜索我的好友列表
        @JvmStatic
        fun startActivity(activity: Activity, isSelectFriend: Int, searchModel: Int, requestCode: Int){
            val intent = Intent(activity,SearchConversationActivity::class.java)
            intent.putExtra("select",isSelectFriend)
            intent.putExtra("model",searchModel)
            activity.startActivityForResult(intent,requestCode)
        }

        @JvmStatic
        fun startActivity(activity: Activity,isSelectFriend : Int, searchModel: Int = 0, requestCode : Int = 0,targetId : String?,mConversationType:Conversation.ConversationType?){
            val intent = Intent(activity,SearchConversationActivity::class.java)
            intent.putExtra("select",isSelectFriend)
            intent.putExtra("model",searchModel)
            intent.putExtra("targetId",targetId)
            intent.putExtra("type",mConversationType)
            activity.startActivityForResult(intent,requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_conversation)
    }

    override fun initView() {
        val headView = LayoutInflater.from(this).inflate(R.layout.model_search_view,baseHeadView.parentView,false)
        val params = headView.layoutParams as? RelativeLayout.LayoutParams
        params?.addRule(RelativeLayout.RIGHT_OF,R.id.headBackButton)
        params?.addRule(RelativeLayout.LEFT_OF,R.id.headRightLinearLayout)

        baseHeadView.parentView.addView(headView)
        baseHeadView.showHeadRightImageButton(R.drawable.news_icon_search,this)
        baseHeadView.showBackButton(this)

        editSearch = headView.findViewById(R.id.et_input_search) as? EditText
        mAdapter = SearchConversationAdapter(this, mutableListOf(),R.layout.item_search_conver)
        listView?.adapter = mAdapter
    }

    override fun initListener() {
        listView?.setOnItemClickListener { _, view, position, _ ->
            val bean = mAdapter?.datas?.get(position)
            val targetId = bean?.code
            val title = if(TextUtils.isEmpty(bean?.remarkName)) bean?.name else bean?.remarkName
            val type = if(bean?.type == 0) Conversation.ConversationType.PRIVATE else Conversation.ConversationType.GROUP
            val conversationEmpty = Conversation.obtain(type, targetId, title)
            val uiConversation = UIConversation.obtain(conversationEmpty, false)

            when(isSelect){
                //选择好友分享之类的
                Constant.SELECT_TYPE_SHARE ->{
                    showConfirmDialog(bean,Constant.SELECT_TYPE_SHARE,uiConversation.conversationType)
                }

                //转发好友
                Constant.SELECT_TYPE_RELAY ->{
                    showConfirmDialog(bean,Constant.SELECT_TYPE_RELAY,uiConversation.conversationType)
                }

                //分享名片
                Constant.SELECT_TYPE_CARD ->{
                    cardPosion = position
                    if(TextUtils.isEmpty(this@SearchConversationActivity.targetId)){
                        //这种情况主要是从用户个人信息那边推荐名片做特殊处理
                        setChooseType(targetId!!,type,Constant.SELECT_TYPE_CARD)
                    }else{
                        setChooseType(this@SearchConversationActivity.targetId,this@SearchConversationActivity.mConversationType,Constant.SELECT_TYPE_CARD)
                    }
                }

                //申请担保交易
                Constant.SELECT_TYPE_GUARAN_DEAL ->{
                    cardPosion = position
                    setChooseType(targetId!!,type,Constant.SELECT_TYPE_GUARAN_DEAL)
                }

                //搜索好友聊天
                -1 ->{
                    if(!conversationListListener?.onConversationClick(this,view,uiConversation)!!)
                        RongIM.getInstance().startGroupChat(this,uiConversation.conversationTargetId, uiConversation.uiConversationTitle)
                }
            }
        }

        editSearch?.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(editSearch?.text.toString())){
                searchByRemote()
            }
            false
        }
        editSearch?.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        if(TextUtils.isEmpty(s.toString())){
            baseEmptyView.hideEmptyView()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    private fun setChooseType(targetId: String?, type: Conversation.ConversationType?, shareFlag: Int) =
            if (type == Conversation.ConversationType.PRIVATE) {
                getUserInfo(targetId,type,shareFlag)
            } else {
                getGroupInfo(targetId, type, shareFlag)
            }

    private fun getUserInfo(userCode: String?,type: Conversation.ConversationType, shareFlag: Int) {
        val sub = MineApi.getInstance().getUserMemberByUserCode(userCode, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<User>>(object : ApiCallBack<User>() {
                    override fun onSuccess(user: User) {
                        val bean = SearchChatBean()
                        bean.code = user.userCode
                        bean.name = user.name
                        bean.pic = user.headPic
                        showConfirmDialog(bean, shareFlag,type)
                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(MyApplication.getInstance(), message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    private fun getGroupInfo(targetId: String?, type: Conversation.ConversationType?, shareFlag: Int) {
        val sub = CircleApi.getInstance()
                .getGroupInfo(UserManager.getInstance().token, targetId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<CircleBean>>(object : ApiCallBack<Object>() {
                    override fun onSuccess(data: Object) {
                        val bean = data as CircleBean
                        if (bean != null) {
                            val bean1 = SearchChatBean()
                            bean1.code = targetId!!
                            bean1.name = bean.groupName
                            bean1.pic = bean.cover
                            showConfirmDialog(bean1,shareFlag,type)
                        }
                    }

                    override fun onError(errorCode: String, message: String) {
                        ToastUtil.showShort(this@SearchConversationActivity, message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this, sub)
    }

    private fun showConfirmDialog(bean: SearchChatBean?, isSelectFriends: Int?, mConversationType:Conversation.ConversationType?) {
        val userInfo = UserInfo(bean?.code, bean?.name, Uri.parse(bean?.pic))
        mRelayDialog = ConfirmRelayDialog()
        mRelayDialog?.userInfo = userInfo
        mRelayDialog?.width = (WindowUtil.getScreenW(this) * 0.8).toInt()
        if (Constant.SELECT_TYPE_GUARAN_DEAL == isSelectFriends) {
            mRelayDialog?.isSelectFriends = Constant.SELECT_TYPE_GUARAN_DEAL //担保交易的把留言输入框隐藏
        }
        mRelayDialog?.show(this.supportFragmentManager, "tag")
        mRelayDialog?.onClickListener = View.OnClickListener {
            val luiyan = mRelayDialog?.editContent?.text.toString()
            when (isSelectFriends) {
            //选择好友分享之类的
                Constant.SELECT_TYPE_SHARE ->{
                    val intent = Intent()
                    intent.putExtra(Constant.INTENT_DATA,EventSelectFriendBean(bean?.code,mConversationType,luiyan))
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }

            //转发好友
                Constant.SELECT_TYPE_RELAY ->{
                    val intent = Intent()
                    intent.putExtra(Constant.INTENT_DATA,EventShareMessage(Constant.SELECT_TYPE_RELAY,bean!!.code,mConversationType,luiyan))
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }

            //发送名片
                Constant.SELECT_TYPE_GUARAN_DEAL,Constant.SELECT_TYPE_CARD -> {
                    mRelayDialog?.dismiss()
                    val bean1 = mAdapter?.datas?.get(cardPosion)
                    val resultData: EventSelectFriendForPostCardBean
                    if(!TextUtils.isEmpty(targetId)){
                        resultData = EventSelectFriendForPostCardBean(targetId, bean1?.code, bean1?.name, bean1?.pic, mConversationType,luiyan)
                    }else{
                        resultData = EventSelectFriendForPostCardBean(bean1?.code, bean1?.code, bean1?.name, bean1?.pic, mConversationType,luiyan)
                    }
                    val intent1 = Intent()
                    intent1.putExtra(Constant.INTENT_DATA, resultData)
                    setResult(Activity.RESULT_OK, intent1)
                    finish()
                    return@OnClickListener
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.headBackButton->{
                finish()
            }

            R.id.HeadRightImageButton->{
                searchByRemote()
            }
        }
    }

    override fun initData() {
        isSelect = intent.getIntExtra("select",-1)
        searchModel = intent.getIntExtra("model",0)
        targetId = intent.getStringExtra("targetId") as? String
        mConversationType = intent.getSerializableExtra("type") as? Conversation.ConversationType
        //这里会搜索会话列表 以及 好友列表的所有数据
        if(searchModel == 0){
            getConversationList()
        }
    }

    private fun getConversationList(){
        baseLoadingView.showLoading()
        RongIM.getInstance().getConversationList(object: RongIMClient.ResultCallback<MutableList<Conversation>>() {
            override fun onSuccess(conversations: MutableList<Conversation>?) {
                //将Conversation 对象转换成 UIConversation
                val sub =
                        Observable.just(conversations)
                                .subscribeOn(Schedulers.io())
                                .map(object : Func1<MutableList<Conversation>?, MutableList<UIConversation>> {
                                    override fun call(datas: MutableList<Conversation>?): MutableList<UIConversation> {
                                        return mutableListOf<UIConversation>()
                                                .let{
                                                    if(datas == null) return it
                                                    var uiBean : UIConversation
                                                    for(item in datas){
                                                        if(!TextUtils.isEmpty(item.targetId)){
                                                            uiBean = UIConversation.obtain(item,false)
                                                            it.add(uiBean)
                                                        }
                                                    }
                                                    it
                                                }
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object :Action1<MutableList<UIConversation>>{
                                    override fun call(t: MutableList<UIConversation>?) {
                                        baseLoadingView?.hideLoading()
                                        conversationList = t
                                    }
                                })

                RxTaskHelper.getInstance().addTask(this@SearchConversationActivity,sub)
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {
                baseLoadingView?.hideLoading()
                ToastUtil.showShort(this@SearchConversationActivity,"获取会话列表失败")
            }

        })
    }

    //提交搜索请求
    private fun searchByRemote(){
        if(TextUtils.isEmpty(editSearch?.text.toString()))  return
        if(resultList.size == 0)
            baseLoadingView?.showLoading()
        else
            baseLoadingView?.showLoading(true)

        val token = UserManager.getInstance().token
        val key = editSearch?.text.toString()
        val sub = AllApi.getInstance().searchChat(token,start,key)
                .subscribeOn(Schedulers.io())
                .doOnNext { data ->

                    //先过滤一遍会话列表的数据
                    filterResult(key)

                    //将本地数据插入到请求回来的数据前面
                    if(data?.result?.size == 0){
                        data.result.addAll(0,resultList)
                        return@doOnNext
                    }

                    if(resultList.size > 0 && data != null){
                        val tempArr = mutableListOf<SearchChatBean>()
                        //过滤掉重复的数据
                        for(i in data.result.indices){
                            for((j,value) in resultList.withIndex()){
                                if(value.code == data.result[i].code){
                                    tempArr.add(0,value)
                                }
                            }
                        }
                        resultList.removeAll(tempArr)
                        data.result?.addAll(resultList)

                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<SearchChatBean>>>(object : ApiCallBack<MutableList<SearchChatBean>>(){
                    override fun onSuccess(data: MutableList<SearchChatBean>?) {
                        baseLoadingView?.hideLoading()
                        if(data?.size != 0){
                            baseEmptyView.hideEmptyView()
                            mAdapter?.notifyChangeData(data)
                        }else{
                            baseEmptyView.showEmptyContent()
//                            mAdapter?.notifyChangeData(mutableListOf())
                        }
                    }

                    override fun onError(errorCode: String?, message: String?) {
                        baseLoadingView?.hideLoading()
                        ToastUtil.showShort(this@SearchConversationActivity,message)
                    }
                }))
        RxTaskHelper.getInstance().addTask(this,sub)
    }

    //先从会话列表筛选出结果 然后把本地结果优先添加到服务器数据前面
    private fun filterResult(text:CharSequence?){
        resultList.clear()
        if(!TextUtils.isEmpty(text)){
            conversationList?.let {
                for(item : UIConversation in conversationList!!){
                    if(item.uiConversationTitle.contains(text.toString(),true) || CharacterParser.getInstance().getSelling(item.uiConversationTitle).startsWith(text.toString(),true)){
                        val searchBean = SearchChatBean()
                        searchBean.name = item.uiConversationTitle
                        searchBean.code = item.conversationTargetId
                        searchBean.pic = item.iconUrl.toString()
                        searchBean.type = if(item.conversationType == Conversation.ConversationType.PRIVATE) 0 else 1
                        resultList.add(searchBean)
                    }
                }
                it
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRelayDialog = null
        editSearch?.removeTextChangedListener(this)
        RxTaskHelper.getInstance().cancelTask(this)
    }

}
