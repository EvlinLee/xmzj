package com.gxtc.huchuan.im.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.OnClick
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.im.MyConversationListFragment
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.ui.circle.SearchConversationActivity
import com.gxtc.huchuan.ui.message.MessageFragment
import com.gxtc.huchuan.ui.message.NewFriendsActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import io.rong.imkit.model.UIConversation
import io.rong.imkit.widget.adapter.ConversationListAdapter
import io.rong.imlib.model.Conversation


/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/24.
 */
class MyConversationListAdapter(context: Context?, shareFlag: Int) : ConversationListAdapter(context) {

     var mContext: Context ? = null
    var topHintMsg: View ? = null
    var msgCount : Int ? = null
    var pic :String ? = null
    companion object {
        @JvmStatic
        var mShareFlag: Int = -1
    }

    init {
        mContext = context
        mShareFlag = shareFlag
    }

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        val type = getItem(position)?.conversationTargetId
        when (type) {
            //头部搜索
            MyConversationListFragment.TYPE_HEADER_SEARCH -> {
                return 1
            }

            else -> {
                return 0
            }
        }
    }

    override fun bindView(v: View?, position: Int, bean: UIConversation ?) {
        if (getItemViewType(position) == 0 && bean != null) {
            //融云的代码会直接调用 getView方法  这里总报错只能做个判断了
            if (bean.notificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                bean.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY)
            } else {
                bean.setUnreadType(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)
            }
            if(v?.tag !is SearchViewHolder){
                super.bindView(v, position, bean)
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val type = getItemViewType(position)

        return when (type) {
                    0 -> {
                        super.getView(position, convertView, parent)
                    }

                    //自定义item
                    else -> {
                        convertView(type, position, convertView, parent)
                    }
                }
    }


    private fun convertView(type: Int, position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = null
        if (convertView == null) {
            //搜索布局
            if (type == 1) {
                view = LayoutInflater.from(mContext).inflate(R.layout.conversavtion_list_head, parent, false)
                val searchHolder = SearchViewHolder(view)
                view.tag = searchHolder
            }
        } else {
            view = convertView
        }

        if (view == null) {
            //bugly 有个别异常还是报空
            //搜索布局
            if (type == 1) {
                view = LayoutInflater.from(mContext).inflate(R.layout.conversavtion_list_head, parent, false)
                val searchHolder = SearchViewHolder(view)
                view.tag = searchHolder
            }
        }
        if(view?.tag is SearchViewHolder)
           (view?.tag as SearchViewHolder).showMessage(this?.msgCount, this?.pic, mContext)
        return view!!
    }

    class SearchViewHolder(itemView: View?) {
        private var tvSearch: TextView? = null
        private var topHintView: View? = null
        var line: View? = null

        init {
            topHintView = itemView?.findViewById(R.id.message_note_layout)
            topHintView?.setOnClickListener{ v->
                v.context?.let {
                    SpUtil.putInt(it, MessageFragment.KEY_NUM(), 0)
                    EventBusUtil.post(EventJPushBean(EventJPushBean.APPLY_FRIENDS, "", 0))
                    NewFriendsActivity.startActivity(it)
                }
            }

            tvSearch = itemView?.findViewById(R.id.et_input_search) as? TextView
            tvSearch?.setOnClickListener {

                if(tvSearch?.context is Activity){
                    if(UserManager.getInstance().isLogin()){
                        SearchConversationActivity.startActivity(tvSearch?.context as Activity, mShareFlag, 0, ConversationActivity.REQUEST_SHARE_CONTENT)
                    }else{
                        val intent = Intent(tvSearch?.context,LoginAndRegisteActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        (tvSearch?.context as Activity).startActivity(intent)
                    }
                }
            }
            line = itemView?.findViewById(R.id.line)
            line?.visibility = View.GONE
        }

        fun showMessage(count: Int ?, userPic: String ?,context: Context?) {

            val tvHint = topHintView?.findViewById<TextView>(R.id.message_note_unReadcount)
            val imgHead = topHintView?.findViewById<ImageView>(R.id.message_note_pic)
            if (count == 0 || count == null) {
                topHintView?.visibility = View.GONE
            } else {
                topHintView?.visibility = View.VISIBLE
                tvHint?.text = "${count}个好友申请"
                imgHead?.let { ImageHelper.loadRound(context, it, userPic, 4) }
            }
        }
    }

    override fun add(t: UIConversation?, position: Int) {
        if (super.getCount() == 0) {
            super.add(t, position)
        } else {
            if (position == 0){
                super.add(t, position + MyConversationListFragment.HEAD_COUNT)
            }else{
                super.add(t, position)
            }
        }
        notifyDataSetChanged()
    }

    override fun add(t: UIConversation?) {
        super.add(t)
        notifyDataSetChanged()
    }

    interface listener{
        fun onclick();
    }

}