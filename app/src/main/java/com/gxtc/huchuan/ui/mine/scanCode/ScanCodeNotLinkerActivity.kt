package com.gxtc.huchuan.ui.mine.scanCode

import android.os.Bundle
import com.gxtc.commlibrary.base.BaseTitleActivity
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.huchuan.R
import kotlinx.android.synthetic.main.not_linker_layout.*

/**
 * Created by zzg on 2017/9/28.
 */
class ScanCodeNotLinkerActivity :BaseTitleActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.not_linker_layout)
    }

    override fun initView() {
        super.initView()
        baseHeadView.showTitle("扫描结果").showBackButton{ finish() }
    }
    override fun initData() {
        super.initData()
        var url = intent.getStringExtra("url")
        tv_title?.text = url
    }
}