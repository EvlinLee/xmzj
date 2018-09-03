package com.gxtc.huchuan.ui.mall.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.adapter.MallOrderStatusAdapter
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.bean.OrderBean
import com.gxtc.huchuan.bean.pay.OrdersRequestBean
import com.gxtc.huchuan.bean.pay.PayBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.live.LiveChanelFragment
import com.gxtc.huchuan.ui.mall.MallOrderDetailedActivity
import com.gxtc.huchuan.ui.mall.MallOrderListFragment
import com.gxtc.huchuan.ui.pay.PayActivity
import com.gxtc.huchuan.utils.DialogUtil
import kotlinx.android.synthetic.main.order_status_layout.*
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by zzg on 2017/10/24.
 */
class MallOrderStatusListActivity : BaseTitleActivity(){

    var title :String  = ""
    var status:String = ""
    var lFragment:MallOrderListFragment? = null

    companion object{
        @JvmStatic
        fun junmpToOrderStatusActivity(context: Activity,title:String,status:String,requestCode:Int){
             val intent = Intent(context, MallOrderStatusListActivity::class.java)
                 intent.putExtra("title",title)
                 intent.putExtra("status",status)
                 context.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)
    }

    override fun initView() {
        super.initView()
        title = intent.getStringExtra("title")
        status = intent.getStringExtra("status")
        baseHeadView.showTitle(title).showBackButton {
            lFragment?.goBack()
        }
         lFragment = MallOrderListFragment()
        val bundle = Bundle()
        bundle.putString("status",status)
        lFragment?.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fl_fragment, lFragment)
        ft.commitAllowingStateLoss()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            lFragment?.goBack()
        }
        return super.onKeyDown(keyCode, event);
    }


}