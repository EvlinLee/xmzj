package com.gxtc.huchuan.ui.mine.circle.statistic.recent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.flyco.tablayout.SlidingTabLayout
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.huchuan.R

/**
 * 圈子统计
 * 最近30天数据
 */
class RecentStatisticsActivity : BaseTitleActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: SlidingTabLayout

    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_statistics)
    }

    override fun initView() {
        id = intent.getIntExtra("id", 0)

        viewPager = findViewById(R.id.viewPager) as ViewPager
        tabLayout = findViewById(R.id.tabLayout) as SlidingTabLayout
        baseHeadView.showTitle("最近统计").showBackButton {
            finish()
        }
        val titles = resources.getStringArray(R.array.array_recent_statistics)
        val addFragment = NewAddFragment()
        val dynamicFragment = NewDynamicFragment()
        val userFragment = ActiveUserFragment()

        viewPager.adapter = RecentPagerAdapter(id, titles, listOf(addFragment, dynamicFragment, userFragment), supportFragmentManager)
        tabLayout.setViewPager(viewPager)

        viewPager.currentItem = intent.getIntExtra("type", 0)
    }




    companion object {

        @JvmStatic
        fun startAcitivity(context: Context, id: Int, type: Int){
            val intent = Intent(context, RecentStatisticsActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }

        class RecentPagerAdapter(var id: Int, var titles: Array<String>, var fragments: List<Fragment>, fm: FragmentManager?) : FragmentPagerAdapter(fm) {

            override fun getItem(position: Int): Fragment = fragments[position].let {
                val bundle = Bundle()
                bundle.putInt("id", id)
                it.arguments = bundle
                it
            }

            override fun getCount(): Int = fragments.size

            override fun getPageTitle(position: Int): CharSequence? = titles[position]
        }
    }

}
