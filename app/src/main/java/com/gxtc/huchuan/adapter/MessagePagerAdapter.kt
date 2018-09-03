package com.gxtc.huchuan.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/2.
 */
class MessagePagerAdapter(fm: FragmentManager?, fragments: MutableList<Fragment>, flags: MutableList<Boolean>) : FragmentPagerAdapter(fm) {

    var fm: FragmentManager? = null
    var fragments: MutableList<Fragment>? = null
    var flags: MutableList<Boolean>? = null

    init {
        this.fm = fm
        this.fragments = fragments
        this.flags = flags
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return fragments?.get(position)!!
    }

    override fun getCount(): Int {
        return fragments?.size!!
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position)
    }
}