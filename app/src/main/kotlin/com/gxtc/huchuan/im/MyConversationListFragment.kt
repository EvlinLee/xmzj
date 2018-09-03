package com.gxtc.huchuan.im

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventClickBean
import com.gxtc.huchuan.bean.event.EventFocusBean
import com.gxtc.huchuan.bean.event.EventMessageBean
import com.gxtc.huchuan.bean.event.EventUnReadBean
import com.gxtc.huchuan.im.adapter.MyConversationListAdapter
import com.gxtc.huchuan.im.utilities.OptionsPopupDialog
import com.gxtc.huchuan.ui.message.MessageFragment
import com.gxtc.huchuan.utils.RIMErrorCodeUtil
import com.gxtc.huchuan.widget.MyActionSheetDialog
import io.rong.imkit.RongContext
import io.rong.imkit.RongIM
import io.rong.imkit.fragment.ConversationListFragment
import io.rong.imkit.mention.RongMentionManager
import io.rong.imkit.model.Event
import io.rong.imkit.model.UIConversation
import io.rong.imkit.widget.adapter.ConversationListAdapter
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.message.TextMessage
import org.greenrobot.eventbus.Subscribe

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/24.
 * 要给会话列表加个搜索头部
 */
class MyConversationListFragment : ConversationListFragment() {

    var myAdapter: MyConversationListAdapter? = null
    var myList: ListView? = null

    var count : Int = 0
    var pic : String ? = null

    companion object {
        val TYPE_HEADER_SEARCH = "search"
        val HEAD_COUNT = 1
    }

    override fun onResolveAdapter(context: Context?): ConversationListAdapter {
        var shareFlag = -1
        arguments?.let {
            shareFlag = it.getInt("select", -1)
        }
        return MyConversationListAdapter(activity, shareFlag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myList = findViewById<ListView>(view, R.id.rc_list)
        myAdapter = myList?.adapter as? MyConversationListAdapter
        val num = SpUtil.getInt(context, MessageFragment.KEY_NUM(),0)
        val userPic = SpUtil.getString(context, MessageFragment.KEY_HEAD_PIC, "")

        if(num > 0 && arguments?.getInt("Invitation", 0) == 1){
            this.count = num
            this.pic = userPic
            showMessage()
        }

        val conversationEmpty = Conversation.obtain(Conversation.ConversationType.PRIVATE, TYPE_HEADER_SEARCH, "")
        myAdapter?.add(UIConversation.obtain(conversationEmpty, false).let {
            it.conversationTargetId = TYPE_HEADER_SEARCH
            it.conversationType = Conversation.ConversationType.NONE
            it.isTop = true
            it
        }, 0)

    }


    override fun onEventMainThread(event: Event.OnReceiveMessageEvent?) {
        if(event?.message?.content is TextMessage){
            val msg = (event?.message?.content as TextMessage).content.toString()
            val uid =event?.message?.senderUserId as String
            if (msg?.equals("我们已经成为好友，快来一起聊天吧！") && event?.message?.conversationType == Conversation.ConversationType.PRIVATE ) {
                EventBusUtil.post(EventFocusBean(true,uid))
            }

        }
        super.onEventMainThread(event)
    }

    override fun initFragment(uri: Uri?) {
        super.initFragment(uri)
        if(myAdapter?.count == 0){
            val conversationEmpty = Conversation.obtain(Conversation.ConversationType.PRIVATE, TYPE_HEADER_SEARCH, "")
            val conver = UIConversation.obtain(conversationEmpty, false)
            conver.conversationType = Conversation.ConversationType.NONE
            conver.conversationTargetId = TYPE_HEADER_SEARCH
            conver.isTop = true
            myAdapter?.add(conver,0)
        }
    }

    fun buildMultiDialog(uiConversation: UIConversation) {
        val items = arrayOfNulls<String>(2)
        if (uiConversation.isTop)
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_cancel_top)
        else
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top)

        items[1] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove)
        OptionsPopupDialog.newInstance(activity, items)
                .setOptionsPopupDialogListener { witch ->
                    if (witch == 0) {
                        RongIM.getInstance().setConversationToTop(uiConversation.conversationType, uiConversation.conversationTargetId, !uiConversation.isTop,
                                object : RongIMClient.ResultCallback<Boolean>() {
                                    override fun onSuccess(b: Boolean?) =
                                            if (uiConversation.isTop) {
                                                ToastUtil.showShort(RongContext.getInstance(), this@MyConversationListFragment.getString(io.rong.imkit.R.string.rc_conversation_list_popup_cancel_top))
                                            } else {
                                                ToastUtil.showShort(RongContext.getInstance(), this@MyConversationListFragment.getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top))
                                            }

                                    override fun onError(b: RongIMClient.ErrorCode?) =
                                            ToastUtil.showShort(RongContext.getInstance(),RIMErrorCodeUtil.handleErrorCode(b))
                                })
                    }

                    if (witch == 1) {
                        /*EventBusUtil.post(EventUnReadBean())        //这里要更新下列表的未读消息数量
                        RongIM.getInstance().removeConversation(uiConversation.conversationType, uiConversation.conversationTargetId, null)*/
                        showRemoveDialog(uiConversation.conversationTargetId, uiConversation.conversationType)
                    }
                }
                .show()
    }

    fun buildSingleDialog(uiConversation: UIConversation) {
        val items = arrayOf(RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove))
        OptionsPopupDialog.newInstance(activity, items)
                .setOptionsPopupDialogListener {
                    RongIM.getInstance().getConversationList(object : RongIMClient.ResultCallback<MutableList<Conversation>>() {
                        override fun onSuccess(conversations: MutableList<Conversation>?) {
                            if (conversations != null && conversations.size > 0) {
                                val var2 = conversations.iterator()

                                while (var2.hasNext()){
                                    val conversation = var2.next()
                                    RongIMClient.getInstance().removeConversation(conversation.conversationType,conversation.targetId,null)
                                }
                            }
                        }

                        override fun onError(p0: RongIMClient.ErrorCode?) = Unit
                    }, uiConversation.conversationType)
                    val position = myAdapter?.findGatheredItem(uiConversation.conversationType)
                    myAdapter?.remove(position!!)
                    myAdapter?.notifyDataSetChanged()
                    EventBusUtil.post(EventUnReadBean())        //这里要更新下列表的未读消息数量
                }
                .show()
    }


    //快速定位
    fun onEventMainThread(bean: EventClickBean) {
        RongIM.getInstance().getConversationList(object : RongIMClient.ResultCallback<MutableList<Conversation>>() {
            override fun onSuccess(data: MutableList<Conversation>?) {
                if (data != null)
                    locationMessage(data)
            }

            override fun onError(error: RongIMClient.ErrorCode?) = Unit
        })
    }


    private fun locationMessage(data: MutableList<Conversation>) {
        for (i in data.indices) {
            val bean = data.get(i)
            if (bean.unreadMessageCount != 0 && bean.notificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
                myList?.smoothScrollToPositionFromTop(i + MyConversationListFragment.HEAD_COUNT, 0)
                return
            }
        }

        for (i in data.indices) {
            val bean = data.get(i)
            if (bean.unreadMessageCount != 0) {
                myList?.smoothScrollToPositionFromTop(i + MyConversationListFragment.HEAD_COUNT, 0)
                return
            }
        }
    }


    private fun showRemoveDialog(targetId: String, type: Conversation.ConversationType){
        val items = arrayOf("确认删除聊天")
        val dialog = MyActionSheetDialog(context, items, null)
        dialog.isTitleShow(false)
                .titleTextSize_SP(14.5f)
                .widthScale(1f)
                .cancelMarginBottom(0)
                .cornerRadius(0f)
                .dividerHeight(1f)
                .itemTextColor(context?.resources?.getColor(R.color.black)!!)
                .cancelText("取消")
                .show()

        dialog.setOnOperItemClickL { parent, view, position, id ->
            when (position){
                0 -> {
                    EventBusUtil.post(EventUnReadBean())        //这里要更新下列表的未读消息数量
                    RongIM.getInstance().removeConversation(type, targetId, null)
                }
            }
            dialog.dismiss()
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type = myAdapter?.getItem(position)?.conversationTargetId
        when (type) {
            //搜索
            TYPE_HEADER_SEARCH -> {
            }

            //融云默认
            else -> {
                super.onItemClick(parent, view, position, id)
            }
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val type = myAdapter?.getItem(position)?.conversationTargetId
        when (type) {
            //搜索
            TYPE_HEADER_SEARCH -> {
                return true
            }

            //融云默认
            else -> {
                return super.onItemLongClick(parent, view, position, id)
            }
        }
    }

    override fun onPortraitItemClick(v: View?, data: UIConversation?) {
        val type = data?.conversationTargetId
        when (type) {
            //搜索
            TYPE_HEADER_SEARCH -> {

            }

            //融云默认
            else -> {
                super.onPortraitItemClick(v, data)
            }
        }
    }

    override fun onPortraitItemLongClick(v: View?, data: UIConversation?): Boolean {
        val type = data?.conversationTargetId
        when (type) {
            //搜索
            TYPE_HEADER_SEARCH -> {
                return true
            }

            //融云默认
            else -> {
                return super.onPortraitItemLongClick(v, data)
            }
        }
    }

    fun showMessage(){
        if (myAdapter == null && myList == null)  return
        myAdapter?.msgCount = this.count
        myAdapter?.pic = pic
    }

    fun showHintMessage(count: Int = 0, userPic: String ?){
        if (myAdapter == null && myList == null) return
        myAdapter?.msgCount = count
        myAdapter?.pic = userPic
        var headView = myList!!.getChildAt(0 - myList!!.firstVisiblePosition)
        val layoutHint = headView?.findViewById<View>(R.id.message_note_layout)
        val tvHint = headView?.findViewById<TextView>(R.id.message_note_unReadcount)
        val imgHead = headView?.findViewById<ImageView>(R.id.message_note_pic)
        if(count == 0){
            layoutHint?.visibility = View.GONE
        }else{
            layoutHint?.visibility = View.VISIBLE
            tvHint?.text = "${count}个好友申请"
            imgHead?.let { ImageHelper.loadRound(context, it, userPic, 4) }
        }
    }

}