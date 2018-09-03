package im.collect

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.ui.mine.collect.CollectActivity
import io.rong.imkit.RongExtension
import io.rong.imkit.plugin.IPluginModule

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/15.
 */
class CollectPlugin : IPluginModule {

    var mContext : Context ? = null

    override fun onClick(p0: Fragment?, p1: RongExtension?) {
        mContext = p0?.context
        val intent = Intent(p0?.context,CollectActivity::class.java)
        intent.putExtra(Constant.SELECT,true)
        intent.putExtra("type",p1?.conversationType)
        p0?.activity?.startActivityForResult(intent,ConversationActivity.REQUEST_SHARE_COLLECT)
    }

    override fun obtainDrawable(p0: Context?): Drawable =
            p0?.resources?.getDrawable(R.drawable.plugin_collect_selector)!!

    override fun obtainTitle(p0: Context?): String = "收藏"

    override fun onActivityResult(p0: Int, p1: Int, data: Intent?) {}
}