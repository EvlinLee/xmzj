package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleMemberBean

/**
 * Created by zzg on 2017/8/28.
 */
class NewTaskAdater(context:Context,datas:ArrayList<String>,resId:Int) : BaseRecyclerAdapter<String>(context,datas,resId) {

    override fun bindData(holder: ViewHolder?, position: Int, t: String?) {
        var tvFollow = holder?.getView(R.id.follow_friends) as TextView;
        var content = holder?.getView(R.id.content) as TextView;
        var progressButton = holder?.getView(R.id.progress_button) as TextView;
        var tvCash = holder?.getView(R.id.tv_cash) as TextView;
        var bluebar = holder?.getView(R.id.blue_bar)
        if(position == 9)    bluebar.setVisibility(View.VISIBLE)
        else bluebar.setVisibility(View.GONE)
        content.text = t;
    }
}