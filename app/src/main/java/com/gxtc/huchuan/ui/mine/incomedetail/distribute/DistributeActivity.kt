package com.gxtc.huchuan.ui.mine.incomedetail.distribute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.huchuan.R

class DistributeActivity : BaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribute)
        val dateType = intent.getIntExtra("dateType",0)
        val lFragment = DistributionFragment()
        val bundle = Bundle()
        bundle.putInt("dateType",dateType)
        lFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fl_fragment, lFragment)
        ft.commitAllowingStateLoss()
    }

    companion object{
        @JvmStatic
        fun jumpToDistributeActivity(activity: Activity,dateType:Int){
            val intent = Intent(activity,DistributeActivity::class.java)
                 intent.putExtra("dateType",dateType)
                 activity.startActivity(intent)
        }
    }

}
