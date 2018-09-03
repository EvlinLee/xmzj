package com.gxtc.huchuan.ui.resource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.gxtc.commlibrary.base.BaseTitleFragment
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventMainClickBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.deal.DealActivity
import com.gxtc.huchuan.ui.live.LiveActivity
import com.gxtc.huchuan.ui.mall.MallActivity
import com.gxtc.huchuan.ui.news.ShareMakeMoneyActivity
import com.gxtc.huchuan.ui.news.VideoNewsActivity
import kotlinx.android.synthetic.main.fragment_resource.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/5.
 * 资源
 */
class ResourceFragment: BaseTitleFragment(), View.OnClickListener {

    override fun initView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        baseHeadView.showTitle("资源")
        return inflater?.inflate(R.layout.fragment_resource, container, false)
    }


    override fun initData() {
        tv_class.setOnClickListener(this)
        tv_mall.setOnClickListener(this)
        tv_deal.setOnClickListener(this)
        tv_video.setOnClickListener(this)
        tv_make_money.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            //课程
            R.id.tv_class -> GotoUtil.goToActivity(this, LiveActivity::class.java)
                //EventMainClickBean(0,0).let { EventBusUtil.post(it) }

            //商城
            R.id.tv_mall -> GotoUtil.goToActivity(this, MallActivity::class.java)
                //EventMainClickBean(0,1).let { EventBusUtil.post(it) }

            //交易
            R.id.tv_deal -> GotoUtil.goToActivity(this, DealActivity::class.java)
                //EventMainClickBean(0,2).let { EventBusUtil.post(it) }

            //视频
            R.id.tv_video -> GotoUtil.goToActivity(this, VideoNewsActivity::class.java)
                //EventMainClickBean(0,3).let { EventBusUtil.post(it) }

            //分享赚钱
            R.id.tv_make_money -> {
                GotoUtil.goToActivity(this, ShareMakeMoneyActivity::class.java)
            }
        }
    }
}