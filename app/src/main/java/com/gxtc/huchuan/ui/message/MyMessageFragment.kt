package com.gxtc.huchuan.ui.message

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/1.
 */
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventConverListBean
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.bean.event.EventLoginBean
import com.gxtc.huchuan.bean.event.EventRefreshConversationBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.im.MyConversationListFragment
import com.gxtc.huchuan.im.listener.MyConversationListListener
import com.gxtc.huchuan.ui.circle.findCircle.CircleListActivity
import com.gxtc.huchuan.ui.mine.focus.FocusActivity
import io.rong.imkit.RongIM
import io.rong.imkit.model.UIConversation
import io.rong.imlib.model.Conversation
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference

/**
 * 我的消息
 * 来自 伍玉南 的装逼小尾巴 on 17/9/1.
 */
class MyMessageFragment : BaseTitleFragment() {

    private val TAG = MyMessageFragment::class.java.simpleName

    private val  handler =  MyHandler(this)

    private var viewStub: ViewStub ?= null
    private var emptyView: View ?= null
    lateinit var fragment : MyConversationListFragment

    companion object {
        internal class MyHandler(myMessageFragment:MyMessageFragment) : Handler(){
            var weakReference:WeakReference<MyMessageFragment>? = null

            init {
                this.weakReference = WeakReference(myMessageFragment)
            }

            override fun handleMessage(msg: android.os.Message?) {
                super.handleMessage(msg)
                weakReference?.get().let {
                    when(msg?.what){
                        0 -> it?.initFragment()
                        1 -> it?.fragment?.uri = it?.getUri()
                        else -> return
                    }
                }
            }
        }

       //会话列表界面操作的监听器。
      internal  class  ConversationListListenerImp(var fragment: MyConversationListFragment ) : MyConversationListListener(){
            var weakReference:WeakReference<MyConversationListFragment> ? = null

            init {
                weakReference = WeakReference (fragment)
                RongIM.setConversationListBehaviorListener(this)          //设置会话列表界面操作的监听器。
            }

            override fun onConversationLongClick(context: Context?, view: View?, uiConversation: UIConversation?): Boolean {
                weakReference?.get().let {
                    if (!it?.getGatherState(uiConversation?.conversationType)!!) {
                        it?.buildMultiDialog(uiConversation!!)
                    } else {
                        it?.buildSingleDialog(uiConversation!!)
                    }
                    return true
                }

            }
        }
    }


    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View {
        val view = inflater?.inflate(R.layout.fragment_my_message, container, false)
        viewStub = view?.findViewById(R.id.vs_my_message)
        return view!!
    }

    override fun initData() {
        EventBusUtil.register(this)
        if(UserManager.getInstance().isLogin){
            initFragment()
        }
    }

    fun initFragment(){
        fragment = MyConversationListFragment()

        fragment?.let {
            it.uri = getUri()
            val bundle = Bundle()
            bundle.putInt("Invitation", 1)
            it.arguments = bundle
            ConversationListListenerImp(it)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.add(R.id.rong_content, it,TAG)
            transaction?.commitAllowingStateLoss()
        }
    }

    @Subscribe
    fun onEvent(bean : EventLoginBean){
        //退出登录
        if(bean.status == EventLoginBean.EXIT){
            fragment?.myAdapter?.clear()
            fragment?.myAdapter?.notifyDataSetChanged()
        }
    }


    @Subscribe(sticky = true)
    fun onEvent(bean: EventConverListBean?){
        bean?.let {
            showEmptyView(it.count == 0)
        }
    }


    @Subscribe
    fun onEvent(bean : EventRefreshConversationBean){
        if(fragment == null){
            handler.sendEmptyMessageDelayed(0,2000)
        }else{
            handler.sendEmptyMessageDelayed(1,2000)
        }
    }

    @Subscribe
    fun onEven(bean: EventJPushBean){
        if(bean.type == EventJPushBean.APPLY_FRIENDS){
            if(bean.num == 0){
                fragment?.showHintMessage(0, "")

            }else{
                fragment?.showHintMessage(bean.num, bean.userPic)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
        handler.removeMessages(0)
        handler.removeMessages(1)
        RongIM.setConversationListBehaviorListener(null)
    }

    private fun getUri():Uri{
        return Uri.parse("rong://" + context?.packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build()
    }

    private fun showEmptyView(isShow: Boolean) {
        if(emptyView == null){
            emptyView = viewStub?.inflate()
        }

        if(isShow){
            emptyView?.visibility = View.VISIBLE
            emptyView?.findViewById<View>(R.id.tv_join)?.setOnClickListener { GotoUtil.goToActivity(activity, CircleListActivity::class.java) }    //加入圈子
            emptyView?.findViewById<View>(R.id.tv_add)?.setOnClickListener { FocusActivity.startActivity(activity, "5") }    //添加好友

        }else{
            emptyView?.visibility = View.GONE
        }
    }
}