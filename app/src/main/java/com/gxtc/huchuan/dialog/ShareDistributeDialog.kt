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
 * Created by zzg on 2017/12/25.
 */
class ShareDistributeDialog (context: Context?) : BaseBubblePopup<ShareDistributeDialog>(context) {
    var listener : AdapterView.OnItemClickListener ? = null
    var listView : ListView? = null

    override fun onCreateBubbleView(): View {
        val view = View.inflate(context, R.layout.share_pop_type,null)
        initView(view)
        return view
    }


    private fun initView(dialogView: View){
        val titles = mutableListOf("全部","圈子","课程")

        listView = dialogView.findViewById(R.id.listview) as? ListView
        listView?.adapter = ShareDistributeAdapter(context,titles, R.layout.item_simple_normal_text)
        listView?.setOnItemClickListener { parent, view, position, id ->
            listener?.onItemClick(parent,view,position,id)
            dismiss()
        }
    }


    override fun show() {
        super.show()
        val param = window.attributes
        param.dimAmount = 0.1f
        dimEnabled(true)
    }


    class ShareDistributeAdapter(context: Context?, datas: MutableList<String>?, itemLayoutId: Int)
        : AbsBaseAdapter<String>(context, datas, itemLayoutId) {

        override fun bindData(holder: AbsBaseAdapter<*>.ViewHolder?, t: String?, position: Int) {
            val text = holder?.getView(R.id.tv_content) as? TextView
            text?.text = t
        }
    }
}