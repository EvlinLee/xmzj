package com.gxtc.huchuan.dialog

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.flyco.dialog.widget.popup.base.BaseBubblePopup
import com.gxtc.commlibrary.base.AbsBaseAdapter
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/6.
 */
class MessageListDialog(context: Context?) : BaseBubblePopup<MessageListDialog>(context) {

    var listener : AdapterView.OnItemClickListener ? = null
    var listView : ListView ? = null

    override fun onCreateBubbleView(): View {
        val view = View.inflate(context, R.layout.dialog_normal_list,null)
        initView(view)
        return view
    }


    private fun initView(dialogView: View){
        val titles = mutableListOf("发起群聊","添加朋友","扫一扫","消息设置")

        listView = dialogView.findViewById(R.id.listview) as? ListView
        listView?.dividerHeight = 0
        listView?.adapter = MessageListAdapter(context,titles,R.layout.item_simple_normal_text)
        listView?.setOnItemClickListener { parent, view, position, id ->
            listener?.onItemClick(parent,view,position,id)
            dismiss()
        }
    }


    override fun show() {
        super.show()
        val param = window.attributes as WindowManager.LayoutParams
        param.dimAmount = 0.1f
        dimEnabled(true)
    }


    class MessageListAdapter(context: Context?, datas: MutableList<String>?, itemLayoutId: Int)
        : AbsBaseAdapter<String>(context, datas, itemLayoutId) {

        override fun bindData(holder: AbsBaseAdapter<*>.ViewHolder?, t: String?, position: Int) {
            val text = holder?.getView(R.id.tv_content) as? TextView
            text?.text = t
        }
    }
}