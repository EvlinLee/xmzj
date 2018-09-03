package com.gxtc.huchuan.ui.mine.newtask

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.ChatGroupAdapter
import com.gxtc.huchuan.adapter.NewTaskAdater
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.CircleMemberBean
import com.gxtc.huchuan.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_chat_group.*

/**
 * Created by zzg on 2017/8/28.
 */
class NewTaskActivity : BaseTitleActivity(){
    var listView : RecyclerView? = null
    var swipeLayout : SwipeRefreshLayout? = null
    var mNewTaskAdater : NewTaskAdater? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_task_layout)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("任务列表").showBackButton(View.OnClickListener {
            finish()
        })
        swipeLayout = findViewById(R.id.swipelayout) as SwipeRefreshLayout
        listView = findViewById(R.id.recyclerView) as RecyclerView
    }
    override fun initListener() {
        super.initListener()
        swipeLayout?.setOnRefreshListener {
            swipeLayout?.isRefreshing = false
        }

        listView?.setOnLoadMoreListener {
            listView?.loadFinish()
        }
    }

    override fun initData() {
        super.initData()
        var datas = arrayListOf<String>()
        swipeLayout?.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)
          // 测试数据
        datas.add("愿你驰骋万里，归来依旧是少年2")
        datas.add("愿你驰骋万里，归来依旧是少年3")
        datas.add("愿你驰骋万里，归来依旧是少年4")
        datas.add("愿你驰骋万里，归来依旧是少年5")
        datas.add("愿你驰骋万里，归来依旧是少年6")
        datas.add("愿你驰骋万里，归来依旧是少年7")
        datas.add("愿你驰骋万里，归来依旧是少年8")
        datas.add("愿你驰骋万里，归来依旧是少年9")
        datas.add("愿你驰骋万里，归来依旧是少年10")
        datas.add("愿你驰骋万里，归来依旧是少年11")

        listView?.setLoadMoreView(R.layout.model_footview_loadmore)
        mNewTaskAdater = NewTaskAdater(this,arrayListOf(String()),R.layout.activity_new_task_item)
        listView?.layoutManager = LinearLayoutManager(this,VERTICAL,false)
        listView?.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST,10, Color.parseColor("#ECEDEE")))
        listView?.notifyChangeData(datas,mNewTaskAdater)
        listView?.adapter = mNewTaskAdater

        swipeLayout?.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4)

        mNewTaskAdater?.setOnReItemOnClickListener {
            v, position ->
            ToastUtil.showShort(this, datas[position])
        }
    }
}