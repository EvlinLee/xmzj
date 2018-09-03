package com.gxtc.huchuan.pop

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import com.gxtc.commlibrary.base.BasePopupWindow
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.PopCircleListTypeAdapter
import com.gxtc.huchuan.bean.CircleBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/28.
 */
class PopList(activity: Activity, res: Int) : BasePopupWindow(activity,res) {

    var listView: ListView ?= null
    var mAdapter: PopCircleListTypeAdapter?= null
    var itemListener: AdapterView.OnItemClickListener ?= null

    var initFlag = false

    override fun init(layoutView: View?) {
        listView = layoutView?.findViewById(R.id.listview) as? ListView
        mAdapter = PopCircleListTypeAdapter(activity, mutableListOf(),R.layout.item_list_circle_type)
        listView?.adapter = mAdapter
        setTransparentBg()
    }

    override fun initListener() {
        listView?.setOnItemClickListener { parent, view, position, id ->
            closePop()
            itemListener?.onItemClick(parent,view,position,id)
        }
    }

    fun setData(list: MutableList<CircleBean>){
        mAdapter?.notifyChangeData(list)
    }

    override fun showPop(view: View?,x: Int, y: Int) {
        if(!initFlag){
            val param = layoutView?.layoutParams as? LinearLayout.LayoutParams
            param?.width = view?.width
            initFlag = true
            getpWindow()?.width = view?.width!!
            val location = IntArray(2)

            view.getLocationInWindow(location)
            getpWindow()?.height = WindowUtil.getScreenH(activity) - location[1] - view.height
        }
        super.showPop(view,x,y)
    }

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener){
        itemListener = listener
    }

    fun notifyDataSetChanged(){
        mAdapter?.notifyDataSetChanged()
    }

}