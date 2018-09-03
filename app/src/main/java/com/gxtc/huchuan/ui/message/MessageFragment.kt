package com.gxtc.huchuan.ui.message

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ImageView
import com.flyco.tablayout.SegmentTabLayout
import com.flyco.tablayout.listener.OnTabSelectListener
import com.flyco.tablayout.widget.MsgView
import com.gxtc.commlibrary.base.BaseFragment
import com.gxtc.commlibrary.utils.*
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MessagePagerAdapter
import com.gxtc.huchuan.bean.event.EventJPushBean
import com.gxtc.huchuan.bean.event.EventLoginBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.dialog.MessageListDialog
import com.gxtc.huchuan.pop.PopEnterAnim
import com.gxtc.huchuan.pop.PopExitAnim
import com.gxtc.huchuan.qrcode.QrCodeActivity
import com.gxtc.huchuan.ui.mine.focus.FocusActivity
import com.gxtc.huchuan.ui.mine.focus.FocusFragment
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.ReLoginUtil
import com.gxtc.huchuan.utils.UnreadMsgUtils
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import org.greenrobot.eventbus.Subscribe

/**
 * 消息fragment
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
class MessageFragment : BaseFragment(),MessageFragmntContract.View{

    var layoutLogin: View ?= null
    var msgView: MsgView ? = null
    var tabLayout: SegmentTabLayout? = null
    var HeadRightImageButton: ImageView? = null
    var viewPager: ViewPager? = null
    var vs_login: ViewStub? = null

    var pagerAdapter: MessagePagerAdapter ? = null
    var fragments : ArrayList<Fragment> ? = null
    var mPresent : MessageFragmntContract.Present? = null
    var flags : MutableList<Boolean> = mutableListOf(false,false)
    var mAlertDialog: AlertDialog? = null
    var messageFragment: MyMessageFragment? = null

    companion object {
        @JvmField
        val KEY_HEAD_PIC = "head_pic"

        @JvmStatic
        fun KEY_NUM(): String = "msg_num" + UserManager.getInstance().userCode

    }

    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        val view = inflater?.inflate(R.layout.fragment_message,container,false)
        val layoutTitle = view?.findViewById<ConstraintLayout>(R.id.rl_tab)
        msgView = view?.findViewById<MsgView>(R.id.msgView)
        tabLayout = view?.findViewById(R.id.tabLayout)
        HeadRightImageButton = view?.findViewById(R.id.HeadRightImageButton)
        viewPager = view?.findViewById(R.id.viewPager)
        vs_login = view?.findViewById(R.id.vs_login)
        when (RomUtils.getLightStatausBarAvailableRomType()) {
            RomUtils.AvailableRomType.MIUI ->         layoutTitle?.let { setActionBarTopPadding(it, true) }

            RomUtils.AvailableRomType.FLYME ->         layoutTitle?.let { setActionBarTopPadding(it, true) }

            RomUtils.AvailableRomType.ANDROID_NATIVE//6.0以上
            ->         layoutTitle?.let { setActionBarTopPadding(it, true) }

            RomUtils.AvailableRomType.NA//6.0以下
            -> {
            }
        }
//        layoutTitle?.let { setActionBarTopPadding(it, true) }
        return view!!
    }


    override fun initListener() {
        tabLayout?.setOnTabSelectListener(object :OnTabSelectListener{
            override fun onTabSelect(position: Int) {
                viewPager?.currentItem = position
            }

            override fun onTabReselect(position: Int) = Unit
        })

        HeadRightImageButton?.setOnClickListener{
            showDialog()
        }

        viewPager?.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{

            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                tabLayout?.currentTab = position
            }
        })
    }

    override fun initData() {
        EventBusUtil.register(this)

        tabLayout?.setTabData(arrayOf("我的消息","我的好友"))
        MessageFragmentPresent(this)

        if(UserManager.getInstance().isLogin){
            mPresent?.getNewFriendsCount(UserManager.getInstance().token)
        }else{
            showLoginLayout()
        }

        messageFragment = MyMessageFragment()

        val focusFragment = FocusFragment()

        fragments = arrayListOf<Fragment>()
        fragments?.add(messageFragment!!)
        fragments?.add(focusFragment)

        focusFragment.arguments = Bundle().let { it.putInt("type_id",3);it }

        pagerAdapter = MessagePagerAdapter(childFragmentManager,fragments!!,flags)
        viewPager?.adapter = pagerAdapter

        //未申请的好友数
        val num = SpUtil.getInt(context,MessageFragment.KEY_NUM(),0)
        val userPic = SpUtil.getString(context, MessageFragment.KEY_HEAD_PIC, "")
        onEven(EventJPushBean(EventJPushBean.APPLY_FRIENDS, "", userPic, num))
    }

    private fun showLoginLayout() {
        if(layoutLogin == null){
            layoutLogin = vs_login?.inflate()
            layoutLogin?.findViewById<View>(R.id.tv_login)?.setOnClickListener{
                GotoUtil.goToActivity(activity, LoginAndRegisteActivity::class.java)
            }
        }
        layoutLogin?.visibility = View.VISIBLE
    }

    private fun showDialog(){
        val dialog = MessageListDialog(context)
          dialog.anchorView(HeadRightImageButton)
                  .location(10,-50)
                  .showAnim(PopEnterAnim().duration(200))
                  .dismissAnim(PopExitAnim().duration(200))
                  .gravity(Gravity.BOTTOM)
                  .cornerRadius(4F)
                  .bubbleColor(Color.parseColor("#ffffff"))
                  .listener =
                    AdapterView.OnItemClickListener { _, _, position, _->
                        if(!UserManager.getInstance().isLogin(activity)) return@OnItemClickListener
                        when(position){
                            //发起群聊
                            0->{ GotoUtil.goToActivity(activity,InviteChatActivity::class.java) }

                            //添加朋友
                            1->{ FocusActivity.startActivity(activity, "5") }

                            //扫一扫
                            2->{ GotoUtil.goToActivity(activity,QrCodeActivity::class.java) }

                            //消息设置
                            3->{
                                val intent = Intent(activity, MessageSettingActivity::class.java)
                                intent.putExtra("flag",1)
                                startActivity(intent)
                            }
                        }
                    }
        dialog.show()
    }

    @Subscribe
    fun onEven(bean: EventJPushBean){
        if(bean.type == EventJPushBean.APPLY_FRIENDS){
            if(bean.num == 0){
                msgView?.visibility = View.INVISIBLE

            }else{
                msgView?.visibility = View.VISIBLE
                UnreadMsgUtils.show(msgView, bean.num)
                SpUtil.putInt(context,MessageFragment.KEY_NUM(), bean.num)
                SpUtil.putString(context,MessageFragment.KEY_HEAD_PIC, bean.userPic)
            }
        }
    }


    @Subscribe
    fun onEvent(bean : EventLoginBean) =
            //退出登录
            if(bean.status == EventLoginBean.EXIT){
                showLoginLayout()
                msgView?.visibility = View.INVISIBLE

            }else{
                mPresent?.getNewFriendsCount(UserManager.getInstance().token)
                layoutLogin?.visibility = View.GONE
            }


    override fun showNewFriendsCount(data: Any?) {
        if(data != null){
            var count = GsonUtil.getJsonValue(GsonUtil.objectToJson(data),"count") as? Double
            val userPic = GsonUtil.getJsonValue(GsonUtil.objectToJson(data),"userPic") as? String
            if(count == null) count = 0.toDouble()
            EventBusUtil.post(EventJPushBean(EventJPushBean.APPLY_FRIENDS, "", userPic, count.toInt()))
            SpUtil.getString(context, MessageFragment.KEY_HEAD_PIC, "")
        }
    }


    override fun setPresenter(presenter: MessageFragmntContract.Present?) {
       mPresent = presenter
    }

    override fun showLoad() = Unit

    override fun showLoadFinish() = Unit

    override fun showEmpty() = Unit

    override fun showReLoad() = Unit

    override fun showError(info: String?) = ToastUtil.showShort(MyApplication.getInstance(),info)

    override fun showNetError() = Unit

    override fun tokenOverdue() {
          mAlertDialog = DialogUtil.showDeportDialog(activity, false, null, activity!!.resources.getString(R.string.token_overdue)) { v ->
            if (v.id == R.id.tv_dialog_confirm) {
                ReLoginUtil.ReloginTodo(activity)
          }
          mAlertDialog?.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.unregister(this)
        mPresent?.destroy()
        RongIM.setConversationListBehaviorListener(null)
    }


    private fun getUri(): Uri = Uri.parse("rong://" + context?.packageName).buildUpon()
            .appendPath("conversationlist")
            .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
            .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
            .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
            .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
            .build()
}