package com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.RelativeLayout
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.WindowUtil

import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.PurchaseRecordAdapter
import com.gxtc.huchuan.ui.live.LiveChanelFragment
import kotlinx.android.synthetic.main.activity_statistic_visitor.*
import rx.Observable
import rx.functions.Action1
/**
 * Created by zzg on 2018/3/16
 */
class StatisticVisitorActivity : BaseTitleActivity() {
    var dateType = 0
    var checkType = 0
    var groupId = 0
    var title = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic_visitor)
    }

    override fun initView() {
        super.initView()
        dateType = intent.getIntExtra("dateType",0)
        checkType = intent.getIntExtra("checkType",0)
        groupId = intent.getIntExtra("groupId",0)
        if(dateType == TIME_OF_DAY && checkType == CIRCLE_OF_HOME){
            title = "日访主页"
        }
        if(dateType == TIME_OF_MOUTH && checkType == CIRCLE_OF_HOME){
            title = "月访主页"
        }
        if(dateType == TIME_OF_DAY && checkType == CIRCLE_OF_INTRUDUCE){
            title = "日访介绍页"
        }
        if(dateType == TIME_OF_MOUTH && checkType == CIRCLE_OF_INTRUDUCE){
            title = "月访介绍页"
        }
        baseHeadView.showTitle(title).showBackButton(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
    }

    override fun initData() {
        super.initData()
        val mStatisticVisitorFragment = StatisticVisitorFragment();
        val bundle = Bundle()
        bundle.putInt("dateType",dateType)
        bundle.putInt("checkType",checkType)
        bundle.putInt("groupId",groupId)
        mStatisticVisitorFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fl_fragment, mStatisticVisitorFragment)
        ft.commitAllowingStateLoss()
    }

//    dateType 时间类型 0每天 2月
//    groupId 圈子Id
//    checkType  0圈子主页  18介绍页

    companion object {
        @JvmStatic
        val TIME_OF_DAY = 0
        @JvmStatic
        val TIME_OF_MOUTH= 2
        @JvmStatic
        val CIRCLE_OF_HOME = 0
        @JvmStatic
        val CIRCLE_OF_INTRUDUCE = 18

        @JvmStatic
        fun jumpToStatisticVisitorActivity(activity: Activity, dateType: Int?, checkType: Int?, groupId: Int?){
            val intent = Intent(activity,StatisticVisitorActivity::class.java)
            intent.putExtra("dateType",dateType)
            intent.putExtra("checkType",checkType)
            intent.putExtra("groupId",groupId)
            activity.startActivity(intent)
        }
    }
}
