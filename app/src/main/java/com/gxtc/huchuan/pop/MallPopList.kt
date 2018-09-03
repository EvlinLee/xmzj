package com.gxtc.huchuan.pop

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.flyco.dialog.widget.popup.base.BaseBubblePopup
import com.gxtc.commlibrary.base.AbsBaseAdapter
import com.gxtc.commlibrary.base.BasePopupWindow
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.PopCircleListTypeAdapter
import com.gxtc.huchuan.adapter.PopListAdapter
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_chat_group.*

/**
 *  Created by zzg on 2017/12/8.
 */
class MallPopList(activity: Context) : BaseBubblePopup<MallPopList>(activity) {

    var mRecyclerView:RecyclerView? = null
    var mPopListAdapter:PopListAdapter? = null
    var mOnItemClickLisntener: BaseRecyclerAdapter.OnItemClickLisntener? = null

    override fun onCreateBubbleView(): View {
        val view = View.inflate(context, R.layout.mall_pop_layout,null)
        initView(view)
        return view
    }

     fun initView(inflate: View?) {
        mRecyclerView = inflate?.findViewById(R.id.layout) as RecyclerView
        mRecyclerView?.layoutManager = LinearLayoutManager(inflate.context)
        mPopListAdapter = PopListAdapter(inflate.context, ArrayList<MallBean>(), R.layout.pop_list_item_layout)
        mRecyclerView?.adapter = mPopListAdapter
        mPopListAdapter?.setOnItemClickLisntener { parentView, v, position ->
            mOnItemClickLisntener?.let {
                it?.onItemClick(parentView,v,position)
                dismiss()
            }
         }
     }

    fun setData(list: List<MallBean>){
        mPopListAdapter?.notifyChangeData(list)
    }

    override fun show() {
        super.show()
        val param = window.attributes
        param.dimAmount = 0.1f
        dimEnabled(true)
    }

    fun setOnItemClickLisntener(listener:BaseRecyclerAdapter.OnItemClickLisntener){
        mOnItemClickLisntener = listener
    }
}