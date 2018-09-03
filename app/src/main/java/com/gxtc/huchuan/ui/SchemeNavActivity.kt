package com.gxtc.huchuan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gxtc.commlibrary.utils.ActivityUtils
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.handler.CircleShareHandler
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity
import com.gxtc.huchuan.ui.live.series.SeriesActivity
import com.gxtc.huchuan.ui.mall.MallDetailedActivity
import com.gxtc.huchuan.ui.mine.deal.orderList.PurchaseListActivity
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity
import com.gxtc.huchuan.ui.mine.personalhomepage.PersonalHomePageActivity
import io.rong.imlib.model.Conversation
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import java.util.*

/**
 * 网页路由跳转界面，这边做主要控制跳转
 */
open class SchemeNavActivity : SwipeBackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scheme = intent?.data
        scheme?.let {
            LogUtil.i("scheme.toString()  ====  " + scheme.toString())
            handleUri(it)
        }
    }

    /**
     * type，0：圈子，1：课程，2：系列课，3：交易订单  4: 群聊邀请  5: 文章  6: 商品  7: 交易详情  8: 个人主页
     */
    private fun handleUri(uri: Uri) {
        val type = uri.getQueryParameter("type")
        val typeId = uri.getQueryParameter("objectId")

        if(!type.isNullOrEmpty()){
            var intent: Intent ?= null
            when (type) {
                "0"->{
                    intent = Intent(this,CircleMainActivity::class.java)
                    intent.putExtra("groupId",typeId?.toInt())
                }

                "1"->{
                    intent = Intent(this,LiveIntroActivity::class.java)
                    intent.putExtra("id",typeId)
                }

                "2"->{
                    intent = Intent(this,SeriesActivity::class.java)
                    intent.putExtra("id",typeId)
                }

                "3"->{
                    if(!typeId.isNullOrEmpty()){
                        intent = Intent(this,OrderDetailedBuyerActivity::class.java)
                        intent.putExtra(Constant.INTENT_DATA,typeId)
                    }else{
                        intent = Intent(this, PurchaseListActivity::class.java)
                        intent.putExtra(Constant.INTENT_DATA,typeId)
                    }
                }

                "4"->{
                    intent = Intent(this,ConversationActivity::class.java)
                    val name = uri.getQueryParameter("groupName")
                    val uri = Uri.parse("rong://" + applicationInfo.packageName)
                            .buildUpon()
                            .appendPath("conversation")
                            .appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase())
                            .appendQueryParameter("targetId", typeId)
                            .appendQueryParameter("title", name).build()
                    intent.data = uri
                }

                "5"->{
                    val shareHandle = CircleShareHandler(this)
                    shareHandle.getNewsData(typeId)
                }

                "6"->{
                    MallDetailedActivity.startActivity(this, typeId)
                }

                "7"->{
                    GoodsDetailedActivity.startActivity(this, typeId)
                }

                "8"->{
                    PersonalHomePageActivity.startActivity(this, typeId)
                }
            }

            intent?.let {
                val intents = getMainIntent()
                intents.add(it)

                //这里还要判断是否登录
                if(!UserManager.getInstance().isLogin){
                    intents.add(Intent(this,LoginAndRegisteActivity::class.java))
                }

                startActivities(intents.toTypedArray())
                finish()
            }

        }else{
            startActivities(getMainIntent().toTypedArray())
        }
    }

    private fun getMainIntent(): MutableList<Intent> {
        val intents = ArrayList<Intent>()
        if (!ActivityUtils.isActivityExists(this.packageName, ".ui.MainActivity", this)) {
            val intentMain = Intent(this, MainActivity::class.java)
            intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intents.add(intentMain)
        }
        return intents
    }


}
