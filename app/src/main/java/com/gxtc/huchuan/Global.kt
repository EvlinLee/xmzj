package com.gxtc.huchuan

import com.gxtc.commlibrary.utils.WindowUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/4/20.
 * 这里存放全局变量
 */
class Global {

    companion object {

        //手机屏幕的宽度
        val screenW: Int by lazy { WindowUtil.getScreenW(MyApplication.getInstance()) }

        //手机屏幕的高度
        val screenH: Int by lazy { WindowUtil.getScreenH(MyApplication.getInstance()) }
    }

}